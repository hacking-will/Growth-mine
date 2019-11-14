package com.code.common.util;

import com.code.modules.dict.entity.Dict;
import com.code.modules.dict.entity.vo.SearchVo;
import org.apache.poi.ss.formula.functions.T;
import org.hibernate.query.criteria.internal.CriteriaBuilderImpl;
import org.hibernate.query.criteria.internal.CriteriaQueryImpl;
import org.hibernate.query.criteria.internal.QueryStructure;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * <p>
 *
 * @author jintingying
 * @version 1.0
 * @date 2019/11/14
 */
public class QueryPredicate<T> {

    private String[] ignoredFieldsDefault = {"createTime", "createUser", "modifyTime", "modifyTser", "delFlag"};

    private static final String DEL_FLAG = "delFlag";

    private String[] ignoredFields;

//    private Map<String, List<Object>> inValues;
//
//    private Map<String, Long[]> betweenValues;
//
//    private Map<String, Long> fieldRules;

    public void setIgnoredFields(String... fields) {
        ignoredFields = fields;
    }

    public void setIgnoredFieldsDefault() {
        ignoredFields = ignoredFieldsDefault;
    }

    public List<String> getIgnoredFields() {
        return Arrays.asList(ignoredFields);
    }

    public static <T> Specification<T> of(SearchVo searchVo, QueryPredicate queryPredicate) {
        List<String> ignoredFields = queryPredicate.getIgnoredFields();
        Specification<T> specification = new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> preList = new ArrayList<>();
                try {
                    Object prod = searchVo.getProd();
                    final BeanInfo beanInfo = Introspector.getBeanInfo(prod.getClass());
                    for (final PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
                        final Object value = pd.getReadMethod().invoke(prod, (Object[]) null);
                        if (!(value instanceof Class) && !ignoredFields.contains(pd.getName())) {
                            switch (searchVo.getType()) {
                                case 1:
                                    if (searchVo.getSearch() != null && !searchVo.getSearch().equals("")) {
                                        preList.add(cb.like(root.get(pd.getName()).as(String.class), searchVo.getSearch()));
                                    }
                                    break;
                                case 2:
                                    if (value != null && !value.equals("")) {
                                        preList.add(cb.like(root.get(pd.getName()).as(String.class), String.valueOf(value)));
                                    }
                                    break;
                            }
                        }
                    }
                    preList.add(cb.equal(root.get(DEL_FLAG).as(String.class), String.valueOf(0)));
                } catch (IntrospectionException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
                Predicate[] pres = new Predicate[preList.size()];
                if (searchVo.getType() == 1 && preList.size() > 0)
                    return query.where(cb.or(preList.toArray(pres))).getRestriction();
                return query.where(preList.toArray(pres)).getRestriction();
            }
        };
        return specification;
    }
}

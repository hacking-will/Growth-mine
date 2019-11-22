package com.design.common.util;

import com.design.common.vo.CommonQuery;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 *
 * @author jintingying
 * @version 1.0
 * @date 2019/11/14
 */
public class QueryPredicate {

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

    //存在的问题：对于存放字典代码的字段无法成功匹配
    // 再查询前将条件中的码表数据转换？？
    public static <T> Specification<T> ofAllLikeMatch(CommonQuery searchVo, QueryPredicate queryPredicate) {
        queryPredicate.setIgnoredFieldsDefault();
        List<String> ignoredFields = queryPredicate.getIgnoredFields();
        Specification<T> specification = new Specification<T>() {
            @Override
            public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> preList = new ArrayList<>();
                try {
                    Object prod = searchVo.getTerms();
                    final BeanInfo beanInfo = Introspector.getBeanInfo(prod.getClass());
                    for (final PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
                        final Object value = pd.getReadMethod().invoke(prod, (Object[]) null);
                        if (!(value instanceof Class) && !ignoredFields.contains(pd.getName())) {
                            if(searchVo.isObject()){
                                if (value != null && !value.equals("")) {
                                    preList.add(cb.like(root.get(pd.getName()).as(String.class), String.valueOf(value)));
                                }
                            }else{
                                if (searchVo.getSerchWord() != null && !searchVo.getSerchWord().equals("")) {
                                    preList.add(cb.like(root.get(pd.getName()).as(String.class), (String)searchVo.getSerchWord()));
                                }
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
                if (!searchVo.isObject() && preList.size() > 0)
                    return query.where(cb.or(preList.toArray(pres))).getRestriction();
                return query.where(preList.toArray(pres)).getRestriction();
            }
        };
        return specification;
    }
}

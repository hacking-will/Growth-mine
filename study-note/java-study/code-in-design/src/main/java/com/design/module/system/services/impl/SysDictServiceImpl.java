package com.design.module.system.services.impl;


import com.design.common.util.ObjectUtil;
import com.design.common.util.QueryPredicate;
import com.design.common.util.result.OptResult;
import com.design.common.vo.CommonQuery;
import com.design.module.system.entity.SysDict;
import com.design.module.system.repository.SysDictRepository;
import com.design.module.system.services.SysDictService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 字典表Service接口实现
 *
 * @author ${author}
 * @version $v: ${version}, $time:${datetime} Exp $
 */
@Service("DictService")
public class SysDictServiceImpl implements SysDictService, Serializable {

    @Autowired
    private SysDictRepository dictRepository;

    @Override
    public Map deleteByIds(List<String> ids) {
        int successCount = 0;
        if (ids == null)
            return OptResult.optError();

        for (String id : ids) {
            SysDict dict = dictRepository.delelteById(id);
            if (dict != null) {
                successCount++;
            }
        }
        return OptResult.deleteResult(ids.size(), successCount,
                ids.size() - successCount, null);
    }

    @Transactional
    @Modifying
    @Override
    public Map save(List<SysDict> dictList) {
        if (dictList == null)
            return OptResult.optError();
        int successCount = 0;
        List<SysDict> fieldList = new ArrayList<>();
        for (SysDict dict : dictList) {
            if (ObjectUtil.isNotEmpty(dict.getId())) {
                dict.setModifyUser(null);
                dict.setModifyTime(new Date());
            } else {
                dict.setId(ObjectUtil.randomID());
                dict.setDelFlag(0);
                dict.setCreateUser(null);
                dict.setCreateTime(new Date());
            }
            SysDict dictRet = dictRepository.saveAndFlush(dict);
            if (dictRet != null) successCount++;
            else fieldList.add(dict);
        }
        return OptResult.saveResult(dictList.size(), successCount, fieldList.size(), fieldList);
    }

    @Override
    public Map get(CommonQuery condition) {
        QueryPredicate queryPredicate = new QueryPredicate();
        queryPredicate.setIgnoredFieldsDefault();
        if(condition.isPageable()){
            Page<SysDict> page = dictRepository.findAll(QueryPredicate.ofAllLikeMatch(condition, queryPredicate), condition.ofPage());
            return OptResult.selectResult(page);
        }
        else{
            List<SysDict> list = dictRepository.findAll(QueryPredicate.ofAllLikeMatch(condition, queryPredicate));
            return OptResult.selectResult(list);
        }
    }

    @Override
    public String getTextByCode(String catalog, String code) {
        return dictRepository.getText(catalog, code);
    }

    @Override
    public List<SysDict> getDictList(String catalog) {
        return dictRepository.getDictList(catalog);
    }

    @Override
    public List<String> getTextList(String catalog) {
        return dictRepository.getTextList(catalog);
    }
}
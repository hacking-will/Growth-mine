package com.uniform.modules.system.services.impl;


import com.uniform.common.base.BaseServiceOperator;
import com.uniform.common.utils.QueryStrategy;
import com.uniform.common.vo.QueryVO;
import com.uniform.modules.system.entity.SysExcel;
import com.uniform.modules.system.repository.SysExcelRepository;
import com.uniform.modules.system.services.SysExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *
 * @author jintingying
 * @version 1.0
 * @date 2019/12/2
 */
@Service("SysExcelService")
public class SysExcelServiceImpl implements SysExcelService {
    @Autowired
    private SysExcelRepository excelRepository;

    @Override
    public Map select(QueryVO<SysExcel> queryVO) throws Exception {
        QueryStrategy queryStrategy = new QueryStrategy();
        List<Sort.Order> orders = new ArrayList<>();
//        orders.add(new Sort.Order(Sort.Direction.ASC, "sort"));
        return BaseServiceOperator.select(excelRepository, queryVO, queryStrategy, orders);
    }

    @Override
    public Map save(List<SysExcel> records) throws Exception {
        return new BaseServiceOperator().save(excelRepository, records);
    }

    @Override
    public Map deleteByIds(List<String> ids) throws Exception {
        return null;
    }


    @Override
    public SysExcel getByName(String entityName, String excelName) {
        return excelRepository.getByName(entityName,excelName);
    }

    @Override
    public SysExcel getByName(String entityName) {
        return excelRepository.getByName(entityName);
    }
}

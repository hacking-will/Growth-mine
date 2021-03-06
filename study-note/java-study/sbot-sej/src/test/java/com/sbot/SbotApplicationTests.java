package com.sbot;

import com.sbot.common.utils.AppContextUtil;
import com.sbot.common.utils.DateTimeUitl;
import com.sbot.common.utils.FileUtil;
import com.sbot.common.utils.easyexcel.ExcelUtil;
import com.sbot.common.utils.ToolUtil;
import com.sbot.modules.system.entity.SysUser;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;

@SpringBootTest
class SbotApplicationTests {

    @Test
    void contextLoads() {

        SysUser user = new SysUser();

        try {
            ToolUtil.setFieldValueByFieldName(user, "id", "qwe");
            System.out.println(user);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        try {
            ToolUtil.setFieldValueBySetMethod(user, "borndate", new Date());
            System.out.println(user);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

//	@Test
//	void test(){
//		System.out.println(ExcelUtil.getCellRange(1, 1));
//		System.out.println(ExcelUtil.getCellRange(1, 26));
//		System.out.println(ExcelUtil.getCellRange(1, 27));
//		System.out.println(ExcelUtil.getCellRange(1, 51));
//		System.out.println(ExcelUtil.getCellRange(1, 52));
//
//		System.out.println(ExcelUtil.getRowRange(1, 1, 10));
//		System.out.println(ExcelUtil.getRowRange(1, 26, 10));
//		System.out.println(ExcelUtil.getRowRange(1, 27, 10));
//		System.out.println(ExcelUtil.getRowRange(1, 51, 10));
//		System.out.println(ExcelUtil.getRowRange(1, 52, 10));
//
//
//		System.out.println("xxx+!"+ExcelUtil.getRowRange(1, 1, 1));
//		System.out.println("xxx+!"+ExcelUtil.getRowRange(1, 2, 26));
//		System.out.println("xxx+!"+ExcelUtil.getColRange(1, 2, 26));
//
//	}

    @Test
    void test() throws Exception {
        List arrList = new ArrayList();
        List linkList = new LinkedList();

        System.out.println(DateTimeUitl.getFieldFromDate(new Date(), Calendar.MONDAY));

        FileUtil.readFile("H:\\Growth-mine\\sql\\someCode.sql");

    }
}

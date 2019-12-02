package com.sbot.common.utils.easyexcel;

import com.alibaba.excel.EasyExcel;
import com.sbot.common.enums.ResultCode;
import com.sbot.common.exception.ProjectException;
import com.sbot.common.utils.AppContextUtil;
import com.sbot.common.utils.FileUtil;
import com.sbot.common.utils.ToolUtil;
import com.sbot.common.utils.easyexcel.handler.ExcelSheetHandler;
import com.sbot.common.utils.easyexcel.listener.ExcelListener;
import com.sbot.common.vo.QueryVO;
import com.sbot.common.vo.ResultVO;
import com.sbot.modules.system.entity.SysExcel;
import com.sbot.modules.system.repository.SysExcelRepository;
import org.apache.poi.ss.formula.functions.T;
import org.aspectj.weaver.ast.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *
 * @author jintingying
 * @version 1.0
 * @date 2019/11/26
 */
public class EasyExcelUtil {
    public static final String excelBasePaket = "com.sbot.modules.system.entity.excel.";

    @Autowired
    private static SysExcelRepository excelRepository;

    /**
     * 读取并解析Excel文档
     *
     * @param pathName 文件路径
     * @param sheet    指定哪一个表格
     * @param row      指定开始行
     * @param head     表头
     */
    public static List read(String pathName, int sheet, int row, Class head) {
        return row == 0 ?
                EasyExcel.read(pathName, head, new ExcelListener()).sheet(sheet).doReadSync() :
                EasyExcel.read(pathName, head, new ExcelListener()).sheet(sheet).headRowNumber(row).doReadSync();
    }

    /**
     * 写入Excel文档
     *
     * @param fileName  文件名
     * @param sheetName sheet名
     * @param head      表头
     * @param list      数据列表
     */
    public static void write(String fileName, String sheetName, Class head, List list) {
        EasyExcel.write(fileName, head).sheet(sheetName).doWrite(list);
    }

    /**
     * 导出xlsx数据模板
     */
    public static void exportTemplateByExcelFile(String filename, String sheetName, String entityName) throws IOException, ClassNotFoundException, IllegalAccessException, InstantiationException {
        Class clazz = Class.forName(excelBasePaket + entityName + "Excel");
        File file = FileUtil.createRomdonNameFile(FileUtil.templatePath, "xlsx");
        EasyExcel.write(file.getAbsolutePath(), clazz)
                .registerWriteHandler(new ExcelSheetHandler(clazz.newInstance()))
                .sheet(sheetName)
                .doWrite(null);
        FileUtil.downloadFile(file.getAbsolutePath(), filename);
    }

    /**
     * 导出数据为xlsx文档
     *
     * @param filename   文档名
     * @param sheetName  sheet名
     * @param entityName “实体名”
     * @param query      查询条件
     */
    public static void exportDataByExcelFile(String filename,
                                             String sheetName,
                                             String entityName,
                                             QueryVO query) throws Exception {
        Class excelClass = Class.forName(excelBasePaket + entityName + "Excel");
        Object excel = excelClass.newInstance();
        List datas = getDataFromQuery(entityName, query);
        if (datas == null || datas.size() == 0)
            return;

        List list = ToolUtil.list2ExcelList(excel, datas);
        File file = FileUtil.createRomdonNameFile(FileUtil.templatePath, "xlsx");
        write(file.getAbsolutePath(), sheetName, excelClass, list);
        FileUtil.downloadFile(file.getAbsolutePath(), filename);
    }

    /**
     * 查询需要导出的数据
     */
    private static List getDataFromQuery(String entityName, QueryVO query) throws Exception {

        Object service = AppContextUtil.getBeanByName(entityName + "Service");
        Class entityClass = Class.forName("com.sbot.modules.system.entity." + entityName);
        query.setTerms(ToolUtil.Json2Object(query.getTerms(), entityClass));
        Method method = service.getClass().getMethod("select", QueryVO.class);
        Object ret = method.invoke(service, query);
        return (List) ((Map) ret).get("list");
    }

    /**
     * 导入Excel文档数据
     *
     * @param file       Excel文件
     * @param entityName “实体”名
     * @param sheet      哪个sheet(从1开始)
     * @param row        哪行开始
     */
    public static ResultVO importDataFromExcelFile(MultipartFile file, String entityName,
                                                   int sheet, int row) throws Exception {
        List<Object> datas = analyzingDataFromExcelFile(file, entityName + "Excel", sheet - 1, row);
        Object service = AppContextUtil.getBeanByName(entityName + "Service");
        Method method = service.getClass().getMethod("uploadExcelData", List.class);
        return ResultVO.success(method.invoke(service, datas));
    }

    /**
     * 从xlsx文档解析数据
     */
    private static List analyzingDataFromExcelFile(MultipartFile file, String excelClassName,
                                                   int sheet, int row) throws IOException, ClassNotFoundException {
        Class excelClass = Class.forName(excelBasePaket + excelClassName);
        String filePath = FileUtil.uploadFile(file);
        List<Object> list = read(filePath, sheet, row, excelClass);
        FileUtil.deleteFile(filePath);
        return list;
    }



    /**
     * 导出数据为xlsx文档
     *
     * @param filename   导出文档名
     * @param sheetName  导出sheet名
     * @param entityName “实体名”
     * @param excelName  “Excel模板实体名”
     * @param query      查询条件
     */
    public static void exportData(String filename,
                                  String sheetName,
                                  String entityName,
                                  String excelName,
                                  QueryVO query) throws Exception {
        SysExcel sysExcel = excelRepository.getByName(entityName, excelName);
        if(sysExcel == null)
            throw new ProjectException(ResultCode.excelNotExitis);
        Class excelClazz = Class.forName(sysExcel.getExcelClazz());
        Object excelTemplate = excelClazz.newInstance();
        final String selectMethodName = "select";
        Object service = AppContextUtil.getBeanByName(sysExcel.getDealServiceName());
        Class entityClass = Class.forName(sysExcel.getEntityClazz());
        query.setTerms(ToolUtil.Json2Object(query.getTerms(), entityClass));
        Method method = service.getClass().getMethod(selectMethodName, QueryVO.class);
        Object ret = method.invoke(service, query);
        List datas = (List) ((Map) ret).get("list");
        if (datas == null || datas.size() == 0)
            throw new ProjectException(ResultCode.excelNoData);
        List list = ToolUtil.list2ExcelList(excelTemplate, datas);
        File file = FileUtil.createRomdonNameFile(FileUtil.templatePath, "xlsx");
        write(file.getAbsolutePath(), sheetName, excelClazz, list);
        FileUtil.downloadFile(file.getAbsolutePath(), filename);
    }

    /**
     * 导入Excel文档数据
     *
     * @param file       Excel文件
     * @param entityName “实体”名
     * @param excelName “Excel实体”名
     */
    public static ResultVO importData(MultipartFile file, String entityName,String excelName) throws Exception {
        SysExcel sysExcel = excelRepository.getByName(entityName, excelName);
        if(sysExcel == null)
            throw new ProjectException(ResultCode.excelNotExitis);
        Class excelClazz = Class.forName(sysExcel.getExcelName());
        String filePath = FileUtil.uploadFile(file);
        List<Object> datas = read(filePath, sysExcel.getBeginSheet()-1, sysExcel.getBeginRow()-1, excelClazz);
        FileUtil.deleteFile(filePath);
        Object service = AppContextUtil.getBeanByName(sysExcel.getDealServiceName());
        Method method = service.getClass().getMethod("uploadExcelData", List.class);
        return ResultVO.success(method.invoke(service, datas));
    }

    /**
     * 导出xlsx数据模板
     */
    public static void exportTemplate(String filename, String sheetName, String entityName,String excelName) throws Exception {
        SysExcel sysExcel = excelRepository.getByName(entityName, excelName);
        if(sysExcel == null)
            throw new ProjectException(ResultCode.excelNotExitis);

        Class clazz = Class.forName(sysExcel.getExcelClazz());
        File file = FileUtil.createRomdonNameFile(FileUtil.templatePath, "xlsx");
        EasyExcel.write(file.getAbsolutePath(), clazz)
                .registerWriteHandler(new ExcelSheetHandler(clazz.newInstance()))
                .sheet(sheetName)
                .doWrite(null);
        FileUtil.downloadFile(file.getAbsolutePath(), filename);
    }
}

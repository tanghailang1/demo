package com.example.demo.utils;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * created by tang
 */
public class ExcelUtils {

    /**
     * 读取excel文件
     * @param path
     */
    public static List<List<String>> read(String path, boolean ignoreHead){
        List<List<String>> list = new ArrayList<>();
        try{
            FileInputStream in = new FileInputStream(path);
            XSSFWorkbook workbook = new XSSFWorkbook(in);
            XSSFSheet sheet = workbook.getSheetAt(0);
            int rowNum = sheet.getLastRowNum();
            for(int i=0;i<=rowNum;i++){
                if(ignoreHead && i==0){
                    continue;
                }
                XSSFRow row = sheet.getRow(i);
                List<String> s = new ArrayList<>();
                for(int columnIndex=0;;columnIndex++){
                    String cellValue = CommonUtil.getTrim(getCellValue(row.getCell(columnIndex)));
                    if(StringUtils.isEmpty(cellValue)){
                        break;
                    }
                    s.add(cellValue);
                }
                list.add(s);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return list;
    }

    public static void main(String[] args) {
        List<List<String>> dataList = new ArrayList<>();
        dataList.add(Arrays.asList("订单号", "手机号", "身份证", "订单申请日期", "订单应还款日期",
                "最近一次还款时间（平台级）", "最近一次还款金额（平台级）", "应还款日后累计还款金额（平台级）"));
        dataList.add(Arrays.asList("订单号", "手机号", "身份证", "订单申请日期", "订单应还款日期"));
        dataList.add(Arrays.asList("订单号", "手机号", "身份证", "订单申请日期"));
        dataList.add(Arrays.asList("订单号", "手机号", "身份证", "订单申请日期", "订单应还款日期",
                "最近一次还款时间（平台级）", "最近一次还款金额（平台级）", "应还款日后累计还款金额（平台级）"));
        addAndAppend(dataList, "/Users/sean/Desktop/tt.xlsx");
    }


    public static void addAndAppend(List<List<String>> resultList, String targetPath){
        try {
            //判断文件是否存在，存在则读取，不存在忽略
            XSSFWorkbook workbook = null;
            XSSFSheet sheet = null;
            if(fileExist(targetPath)){//已经存在
                FileInputStream in = new FileInputStream(targetPath);
                //判断是否已经有表头，没有则添加
                workbook = new XSSFWorkbook(in);
                sheet = workbook.getSheetAt(0);
            }else{//不存在，需要新建
                workbook = new XSSFWorkbook();
                sheet = workbook.createSheet("riskData");
                XSSFRow firstRow = sheet.createRow(0);//第一行表头
                List<String> fixedHead = resultList.get(0);//标题行列
                XSSFCell cells[] = new XSSFCell[fixedHead.size()];
                //循环设置表头信息
                for (int i=0;i<fixedHead.size();i++){
                    cells[0]=firstRow.createCell(i);
                    cells[0].setCellValue(fixedHead.get(i));
                }
                resultList = resultList.stream().skip(1).collect(Collectors.toList());
            }

            //添加数据
            FileOutputStream out = new FileOutputStream(targetPath);

            //追加行数据
            for(int i=0;i<resultList.size();i++){
                XSSFRow row=sheet.createRow((short)(sheet.getLastRowNum()+1)); //在现有行号后追加数据
                List<String> stringList = resultList.get(i);
                for(int k=0;k<stringList.size();k++){
                    row.createCell(k).setCellValue(resultList.get(i).get(k));
                }
            }
            out.flush();
            workbook.write(out);
            out.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //判断文件是否存在
    public static boolean fileExist(String filePath){
        boolean flag = false;
        File file = new File(filePath);
        flag = file.exists();
        return flag;
    }



    /**
     * 获取cell中的值
     * @param cell
     * @return
     */
    public static String getCellValue(Cell cell) {
        String cellValue = "";
        if (cell == null) {
            return cellValue;
        }
        // 判断数据的类型
        switch (cell.getCellType()) {
            case Cell.CELL_TYPE_NUMERIC: // 数字
                //short s = cell.getCellStyle().getDataFormat();
                if (HSSFDateUtil.isCellDateFormatted(cell)) {// 处理日期格式、时间格式
                    SimpleDateFormat sdf = null;
                    // 验证short值
                    if (cell.getCellStyle().getDataFormat() == 14) {
                        sdf = new SimpleDateFormat("yyyy/MM/dd");
                    } else if (cell.getCellStyle().getDataFormat() == 21) {
                        sdf = new SimpleDateFormat("HH:mm:ss");
                    } else if (cell.getCellStyle().getDataFormat() == 22) {
                        sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                    } else {
                        throw new RuntimeException("日期格式错误!!!");
                    }
                    Date date = cell.getDateCellValue();
                    cellValue = sdf.format(date);
                } else if (cell.getCellStyle().getDataFormat() == 0) {//处理数值格式
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    cellValue = String.valueOf(cell.getRichStringCellValue().getString());
                }else{
                    cell.setCellType(Cell.CELL_TYPE_STRING);
                    cellValue = String.valueOf(cell.getRichStringCellValue().getString());
                }
                break;
            case Cell.CELL_TYPE_STRING: // 字符串
                cellValue = String.valueOf(cell.getStringCellValue());
                break;
            case Cell.CELL_TYPE_BOOLEAN: // Boolean
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_FORMULA: // 公式
                cellValue = String.valueOf(cell.getCellFormula());
                break;
            case Cell.CELL_TYPE_BLANK: // 空值
                cellValue = null;
                break;
            case Cell.CELL_TYPE_ERROR: // 故障
                cellValue = "非法字符";
                break;
            default:
                cellValue = "未知类型";
                break;
        }
        return cellValue;
    }
}

package com.example.demo.utils;

import com.csvreader.CsvReader;
import com.example.demo.dao.sass.ZPhonesDao;
import com.example.demo.entity.sass.ZPhonesPO;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.nio.charset.Charset;
import java.util.ArrayList;

public class CsvUtils {

    @Resource
    private static ZPhonesDao zPhonesDao;

    /**
     * CsvReader 读取
     * @param filePath
     * @return
     */
    public static  ArrayList<String> readCsvByCsvReader(String filePath) {
        ArrayList<String> strList = null;
        try {
            ArrayList<String[]> arrList = new ArrayList<String[]>();
            strList = new ArrayList<String>();
            CsvReader reader = new CsvReader(filePath, ',', Charset.forName("UTF-8"));
            while (reader.readRecord()) {
                arrList.add(reader.getValues()); // 按行读取，并把每一行的数据添加到list集合
            }
            reader.close();
            System.out.println("读取的行数：" + arrList.size());
            // 如果要返回 String[] 类型的 list 集合，则直接返回 arrList
            // 以下步骤是把 String[] 类型的 list 集合转化为 String 类型的 list 集合
            for (int row = 0; row < arrList.size(); row++) {
                // 组装String字符串
                // 如果不知道有多少列，则可再加一个循环
                String ele = arrList.get(row)[0] + "," + arrList.get(row)[1] + ","
                        + arrList.get(row)[2] + "," + arrList.get(row)[3] ;
             //   System.out.println(ele);
                strList.add(ele);
                ZPhonesPO zPhonesPO = new ZPhonesPO();
                zPhonesPO.setUser_phone(arrList.get(row)[1]);
                zPhonesDao.save(zPhonesPO);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strList;
    }

}

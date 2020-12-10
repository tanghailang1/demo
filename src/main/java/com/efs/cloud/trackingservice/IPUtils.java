package com.efs.cloud.trackingservice;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Subdivision;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.net.InetAddress;

/**
 * @Author: maxun
 * @Date: 2020/12/10
 */
@Slf4j
public class IPUtils {
    /**
     * 全局静态变量，DatabaseReader，保证类加载时加载一次
     */
    private static DatabaseReader reader;

    /**
     * 静态代码块，保证项目启动只获取一次文件
     */
    static {

        File database = null;

        try {
            //绝对路径读取文件方式
            //database = new File("F:\\IDEA project\\my_demo\\src\\main\\resources\\GeoLite2-City.mmdb");

            // 通过 InputStream 流式读取文件，解决无法通过File方式读取jar包内的文件的问题
            database = getFile("GeoLite2-City.mmdb","geolite2.mmdb");
            log.info("-------加载文件");
            reader = new DatabaseReader.Builder(database).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据ip获取城市
     * @param ip
     * @return
     */
    public static String getAddressCity(String ip){
        try {
            CityResponse response = null;
            InetAddress ipAddress = InetAddress.getByName(ip);
            response = reader.city(ipAddress);
            // 获取国家信息
            Country country = response.getCountry();
            // 获取省份
            Subdivision subdivision = response.getMostSpecificSubdivision();
            // 获取城市
            City city = response.getCity();

            if (city.getNames().get("zh-CN") != null){
                return city.getNames().get("zh-CN");
            }else if(subdivision.getNames().get("zh-CN") != null){
                return subdivision.getNames().get("zh-CN");
            }else {
                return country.getNames().get("zh-CN");
            }
        } catch (Exception e) {
            log.error("ip获取城市error:" + ip + "," + e.getMessage());
        }
        return "";
    }

    /**
     * 读取classpath下的文件
     * @param fileName 原文件全名
     * @param newFileName  缓存的新文件的名称
     * @return
     * @throws IOException
     */
    public static File getFile(String fileName, String newFileName) throws IOException {
        //读取 ClassPath 路径下指定资源的输入流
        ClassPathResource resource = new ClassPathResource(fileName);
        InputStream inputStream = resource.getInputStream();

        File file = new File(newFileName);

        inputstreamToFile(inputStream, file);

        return file;
    }

    /**
     * InputStream -> File
     * @param inputStream
     * @param file
     */
    private static void inputstreamToFile(InputStream inputStream,File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = inputStream.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

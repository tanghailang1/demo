package com.example.demo.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.csvreader.CsvReader;
import com.example.demo.annotation.LogDeals;
import com.example.demo.config.minio.MinioUtil;
import com.example.demo.config.rabbitmq.MqQueueConstant;
import com.example.demo.dao.risk.TaskDao;
import com.example.demo.dao.sass.SimHashInfoDao;
import com.example.demo.dao.sass.StatisticalDataDao;
import com.example.demo.dto.SmsInfo;
import com.example.demo.entity.risk.RiskTaskPo;
import com.example.demo.entity.sass.SimHashInfoPO;
import com.example.demo.entity.sass.StatisticalData;
import com.example.demo.entity.sass.ZPhonesPO;
import com.example.demo.service.impl.RabbitmqServiceImpl;
import com.example.demo.service.impl.TestService;
import com.example.demo.utils.*;
import com.example.demo.vo.Result;
import com.example.demo.vo.SmsRecordInfo;
import com.example.demo.vo.SmsRecordInfo3;
import com.example.demo.vo.SmsVariableVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.minio.StatObjectResponse;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class TestController {

    @Autowired
    private TaskDao taskDao;

    @Autowired
    MinioUtil minioUtil;

    @Autowired
    private TestService testService;

    @Resource
    private SimHashInfoDao simHashInfoDao;

    @Autowired
    private StatisticalDataDao statisticalDataDao;

    @Value("${email.send_email}")
    private String sendEmail;
    @Value("${email.auth_code}")
    private String authCode;
    @Value("${email.receive_email}")
    private String receiveEmail;
    @Value("${email.python_path}")
    private String pythonPath;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RabbitmqServiceImpl rabbitmqService;

    @Resource
    private RestTemplate restTemplate;



    @LogDeals(value = "查询风控订单接口")
    @PostMapping("/findById")
    public Result findById(@RequestBody JSONObject json) {
        RiskTaskPo riskTaskPo = taskDao.findId(json.getString("id"));
        return Result.builder().data(riskTaskPo).build();
    }


    @LogDeals(value = "数据库密码加密接口")
    @PostMapping("/enc")
    public Result enc(@RequestBody JSONObject json) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
//设置密钥
        encryptor.setPassword("qwer1234tyu");
//设置加密算法
        encryptor.setAlgorithm("PBEWithMD5AndDES");
//加密信息
        //ChinaNo.1LPL=Top
        String encryptedText = encryptor.encrypt(json.getString("input"));
        System.out.println("encryptedText:"+  encryptedText);
//解密
        String decryptedText = encryptor.decrypt(encryptedText);
        System.out.println("decryptedText:"+  decryptedText);

        return Result.builder().data(encryptedText).build();
    }



    @LogDeals(value = "数据库密码解密接口")
    @PostMapping("/dec")
    public Result dec(@RequestBody JSONObject json) {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
//设置密钥
        encryptor.setPassword("qwer1234tyu");
//设置加密算法
        encryptor.setAlgorithm("PBEWithMD5AndDES");


//解密
        String decryptedText = encryptor.decrypt(json.getString("password"));
        System.out.println("decryptedText:"+  decryptedText);

        return Result.builder().data(json.getString("password")).build();
    }



    @LogDeals(value = "获取⽂件信息")
    @PostMapping("/getObjectInfo")
    public Result getObjectInfo(@RequestBody JSONObject json) throws Exception {
        StatObjectResponse objectInfo = minioUtil.getObjectInfo(json.getString("bucketName"), json.getString("objectName"));
        return Result.builder().data(objectInfo.toString()).build();
    }

    @LogDeals(value = "获取⽂件外链")
    @PostMapping("/getObjectURL")
    public Result getObjectURL(@RequestBody JSONObject json) throws Exception {
        String fileUrl = minioUtil.getObjectURL(json.getString("bucketName"),json.getString("objectName"),86400);
        return Result.builder().data(fileUrl.toString()).build();
    }

    @LogDeals(value = "MinIO上传文件接口")
    @PostMapping("/uploadFile")
    public Result uploadFile(MultipartFile file) throws Exception {
        String fileUrl = minioUtil.uploadFile(file, "test");
        return Result.builder().data(fileUrl).build();
    }

    @LogDeals(value = "测试spring事务传播机制接口")
    @PostMapping("/testAdd")
    public void testAdd(@RequestBody JSONObject json) throws Exception {
        testService.addTask();
    }


    @LogDeals(value = "读取csv文件接口")
    @PostMapping("/readCSV")
    public void readCSV(@RequestBody JSONObject json) throws Exception {
        ZPhonesPO zPhonesPO = new ZPhonesPO();
        readCsvByCsvReader("/Users/sean/Desktop/1.csv");
    }


    public ArrayList<String> readCsvByCsvReader(String filePath) {
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
            for (int row = 1; row < arrList.size(); row++) {
                SimHashInfoPO simHashInfoPO = new SimHashInfoPO();
                simHashInfoPO.setMobile(arrList.get(row)[1]);
                simHashInfoDao.save(simHashInfoPO);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strList;
    }



    @LogDeals(value = "测试发邮件")
    @PostMapping("/sendEmail")
    public void sendEmail(@RequestBody JSONObject json)  {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String filePath =  "/Users/sean/Desktop/测试邮件_" + dtf.format(now) + ".xlsx";
        List<List<String>> dataList = new ArrayList<>();
        dataList.add(Arrays.asList("mobile_phone", "name", "last_apply_time",
                "last_actual_time"));
        List<StatisticalData> statisticalDataList = statisticalDataDao.findAll();
        for (StatisticalData statisticalData : statisticalDataList){
            List<String> list = new ArrayList<>();
            list.add(statisticalData.getMobile_phone());
            list.add(statisticalData.getName());
            list.add(statisticalData.getLast_apply_time().toString());
            list.add(statisticalData.getLast_actual_time().toString());
            dataList.add(list);
        }
        if (!CollectionUtils.isEmpty(dataList)) {
            try {
                ExcelUtils.addAndAppend(dataList, filePath);
                EmailUtils.send(sendEmail, authCode, receiveEmail, "测试邮件", filePath,pythonPath);
            } catch (Exception e) {
                log.error("发送邮件异常", e);
            }
        }
    }


    @LogDeals(value = "测试redis")
    @PostMapping("/testRedis")
    public void testRedis(@RequestBody JSONObject json)  {
        try {
            String key = json.getString("key");
            SmsVariableVO smsVariableVO = new SmsVariableVO();
            smsVariableVO.setSmsRecordInfo(SmsRecordInfo.builder().sms_cnt(100).sms_contacts_cnt(200).sms_relatives_cnt(300).build());
            smsVariableVO.setSmsRecordInfo3(SmsRecordInfo3.builder().sms_cnt3(100).sms_contacts_cnt3(200).sms_relatives_cnt3(300).build());
            redisUtil.set(key,smsVariableVO.toString(),86400);
            if(redisUtil.hasKey(key)){
                try {
                    SmsVariableVO smsVariableVO1 =JSONObject.parseObject(String.valueOf(redisUtil.get(key)),SmsVariableVO.class);
                    System.out.println(String.valueOf(redisUtil.get(key)));
                    System.out.println(smsVariableVO1.toString());
                }catch (Exception e){
                    redisUtil.set(key+"_date",JSONObject.toJSONString(smsVariableVO),86400);
                    SmsVariableVO smsVariableVO1 =JSONObject.parseObject(String.valueOf(redisUtil.get(key+"_date")),SmsVariableVO.class);
                    System.out.println(String.valueOf(redisUtil.get(key+"_date")));
                    System.out.println(smsVariableVO1.toString());
                }
            }
        }catch (Exception e){
            System.out.println("1");
        }
    }


    @LogDeals(value = "测试rabbit")
    @PostMapping("/testRabbit")
    public void testRabbit(@RequestBody JSONObject json)  {
        for (int i = 1; i < 100; i++) {
            SmsRecordInfo smsRecordInfo = SmsRecordInfo.builder().sms_relatives_cnt(i).sms_contacts_cnt(i * i).sms_cnt(i + i).build();
            rabbitmqService.sendOne(MqQueueConstant.VAR_PROCESS_HANDLE,smsRecordInfo);
        }
    }

    @LogDeals(value = "测试rabbit获取消息数")
    @PostMapping("/testRabbitNum")
    public int testRabbitNum(@RequestBody JSONObject json)  {
        return rabbitmqService.getMsgNumber(json.getString("queue"));
    }



    @LogDeals(value = "测试okhttpNew")
    @PostMapping("/testokhttpNew")
    public void testokhttpNew(@RequestBody SmsRecordInfo smsRecordInfo)  {
        rabbitmqService.sendOne(MqQueueConstant.VAR_PROCESS_HANDLE,smsRecordInfo);
    }


    @LogDeals(value = "测试okhttp")
    @PostMapping("/testOkHttp")
    public void testOkHttp(@RequestBody JSONObject json1)  {
        JSONObject json =new JSONObject();
        json.put("sms_cnt",1);
        json.put("sms_contacts_cnt",2);
        json.put("sms_relatives_cnt",3);
        json.put("sms_relatives_cnt_new",44);
        boolean b = HttpUtils.postSend("http://127.0.0.1:8080/testokhttpNew", JSON.toJSONString(json));

    }


    @LogDeals(value = "测试okhttp")
    @GetMapping("/testOkHttp1")
    public void testOkHttp1(@RequestBody JSONObject json1)  {
        try {
            for (int i = 0; i < 10; i++) {
                try {
                    Object forObject = restTemplate.getForObject("http://localhost:8004/hello2", Object.class);
                }catch (Exception e){
                    log.info("=========");
                }

            }
        }catch (Exception e){
            log.info(">>>>>>>>>>>>>>>>");
        }


    }



    @LogDeals(value = "测试查询水星分")
    @PostMapping("/testWaterStarScore")
    public String testWaterStarScore(@RequestBody JSONObject json1) throws IOException {
        Map<String,Object> map = new TreeMap<>();
        String timeMillis = System.currentTimeMillis()+"";
        String nonce = UUID.randomUUID().toString().replaceAll("-","");
        map.put("merchantId","1146");
        map.put("serviceType","110");
        map.put("timestamp",timeMillis);
        map.put("nonce",nonce);
        map.put("signKey","fYaBGSQbuYQgafXGqLDueNLakOpnSexW");
        String params = paramsSort(map);
        String sign =  SecurityUtil.MD5(params).toUpperCase();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("merchantId","1146");
        jsonObject.put("serviceType","110");
        jsonObject.put("timestamp",timeMillis);
        jsonObject.put("nonce",nonce);
        jsonObject.put("sign",sign);
        JSONObject dataJson = new JSONObject();
        dataJson.put("merchantOrderId",json1.getString("order_id"));
        jsonObject.put("data",dataJson);
        String zip = GZipUtils.compress(jsonObject.toJSONString());
        JSONObject paramJson = new JSONObject();
        paramJson.put("compressData",zip);
        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(JSON_TYPE, paramJson.toJSONString());
        Request request = new Request.Builder().url("http://api.mercurycreditnb.com/riskmirror/mercuryi/v1/queryOrder").post(requestBody).build();
        OkHttpClient okHttpClient = HttpPool.getInstance();
        Response response = okHttpClient.newCall(request).execute();
        String resp = response.body().string();
        JSONObject result = JSON.parseObject(resp);
        JSONObject data = result.getJSONObject("data");
        Integer resultCode = data.getInteger("resultCode");
        Integer riskStatus = data.getInteger("riskStatus");
        BigDecimal score = data.getBigDecimal("score");
        if(score!= null){
            log.info("=======resultCode={},riskStatus={},score={}",resultCode,riskStatus,score.toString());
        }

        return resp;
    }

    @LogDeals(value = "测试水星")
    @PostMapping("/testWaterStar")
    public String testWaterStar(@RequestBody JSONObject json1) throws IOException {
        Map<String, Object> variable = new HashMap<>();
        Map<String, Object> baseInfo = new HashMap<>();
        variable.put("user_id","630ef5373efec1127f086fdf");
        variable.put("order_id",json1.getString("order_id"));
        baseInfo.put("name","Chekuri Sai Krishna");
        baseInfo.put("aadhaar_number","809251891381");
        baseInfo.put("pan_number","BOFPC6651B");
        baseInfo.put("mobile","8985813275");
        baseInfo.put("sex","1");
        baseInfo.put("birthday","1994-09-27");
        baseInfo.put("register_ip","49.37.133.48");
        Map<String, Object> deviceInfo6 = new HashMap<>();
        deviceInfo6.put("wifi_count",0);
        deviceInfo6.put("current_wifi_ssid","bum chick 5G");
        deviceInfo6.put("current_wifi_mac","02:00:00:00:00:00");
        deviceInfo6.put("ram_total","12.07 GB");
        deviceInfo6.put("cash_total","243 GB");
        deviceInfo6.put("ram_can_use","4.87 GB");
        deviceInfo6.put("cash_can_use","110 GB");
        deviceInfo6.put("audio_external",899);
        deviceInfo6.put("audio_internal",231);
        deviceInfo6.put("images_internal",0);
        deviceInfo6.put("images_external",5749);
        deviceInfo6.put("video_internal",0);
        deviceInfo6.put("video_external",365);
        deviceInfo6.put("download_files",0);
        deviceInfo6.put("network_operator_name","40449");
        deviceInfo6.put("network_type","NETWORK_WIFI");
        deviceInfo6.put("time_zone_id","GMT+05:30");
        deviceInfo6.put("default_language","en");
        deviceInfo6.put("elapsed_realtime",1661667546287L);
        deviceInfo6.put("is_using_debug",1);
        deviceInfo6.put("is_using_proxy_port",0);
        deviceInfo6.put("is_using_vpn",0);
        deviceInfo6.put("is_rooted",0);
        deviceInfo6.put("last_boot_time",256718524);
        deviceInfo6.put("battery_pct","0.8999999761581421");
        deviceInfo6.put("is_usb_charge",0);
        deviceInfo6.put("is_ac_charge",0);
        deviceInfo6.put("is_charging",0);
        deviceInfo6.put("advance_ocr_name","Chekuri Sai Krishna");
        deviceInfo6.put("advance_custom_name","Chekuri Sai Krishna");

        Map<String, Object> gpsInfo = new HashMap<>();
        gpsInfo.put("latitude","17.4943295");
        gpsInfo.put("longitude","78.3964399");
        gpsInfo.put("gps_province","Telangana");
        gpsInfo.put("gps_city","Ranga Reddy");
        gpsInfo.put("gps_street","medak road");

        Map<String, Object> deviceInfo = new HashMap<>();
        deviceInfo.put("device_id","ca0dc7eb3ff8710c");
        deviceInfo.put("gaid","1c28c94b-e763-4a96-9c55-1358331b4b7f");
        deviceInfo.put("imei1","-999999");
        deviceInfo.put("phone_mac","-999999");
        deviceInfo.put("is_emulator",1);
        deviceInfo.put("phone_brand","oneplus");
        deviceInfo.put("phone_model","DN2101");
        deviceInfo.put("product","DN2101IND");
        deviceInfo.put("system_version","-999999");
        deviceInfo.put("sdk_version","31");
        deviceInfo.put("manufacturer","OnePlus");
        deviceInfo.put("hardware","mt6893");
        deviceInfo.put("device","OP515BL1");
        deviceInfo.put("user","root");
        deviceInfo.put("tags","release-keys");
        deviceInfo.put("type","user");

        Map<String, Object> thirdBlueRayRequest = new HashMap<>();
        List<JSONObject> apps = new ArrayList<>();
        List<JSONObject> configuredWifis = new ArrayList<>();
        List<JSONObject> txls = new ArrayList<>();
        List<JSONObject> msgs = new ArrayList<>();
        List<JSONObject> albs = new ArrayList<>();
        JSONObject appsJson = new JSONObject();
        appsJson.put("app_name","Tethering");
        appsJson.put("first_install_time","2022-08-31 05:51:48.219");
        appsJson.put("package_name","com.google.android.networkstack.tethering");
        appsJson.put("last_update_time","2022-08-31 05:51:48.219");
        appsJson.put("version",null);
        appsJson.put("iexpress",1);
        appsJson.put("flags",null);
        appsJson.put("version_code",null);
        apps.add(appsJson);
        thirdBlueRayRequest.put("apps",apps);

//        JSONObject configuredWifisJson = new JSONObject();
//        configuredWifisJson.put("bssid","");
//        configuredWifisJson.put("name","");
//        configuredWifisJson.put("ssid","");
//        configuredWifis.add(configuredWifisJson);
//        thirdBlueRayRequest.put("configuredWifis",configuredWifis);
        thirdBlueRayRequest.put("configuredWifis",null);

        JSONObject txlsJson = new JSONObject();
        txlsJson.put("number","*121#");
        txlsJson.put("display_name","instant help 24x7");
        txls.add(txlsJson);
        thirdBlueRayRequest.put("txls",txls);
        JSONObject msgsJson = new JSONObject();
        msgsJson.put("address","TX-CRIFHM");
        msgsJson.put("body","Your CRIF Credit Report CCR220830CR895110499 is generated through MOBIKWIK ZIP with your consent.This is a soft inquiry & doesn't impact your credit score");
        msgsJson.put("createTime",1661826464113L);
        msgsJson.put("type",1);
        msgs.add(msgsJson);
        thirdBlueRayRequest.put("msgs",msgs);
        JSONObject albsJson = new JSONObject();
        albsJson.put("name","IMG-20220831-WA0000.jpeg");
        albsJson.put("author","OnePlus");
        albsJson.put("height","3264");
        albsJson.put("width","1632");
        albsJson.put("longitude","0.0");
        albsJson.put("latitude","0.0");
        albsJson.put("date","2022:08:31 10:26:00");
        albsJson.put("model","DN2101");
        albs.add(albsJson);
        thirdBlueRayRequest.put("albs",albs);

        variable.put("baseInfo",baseInfo);
        variable.put("deviceInfo6",deviceInfo6);
        variable.put("gpsInfo",gpsInfo);
        variable.put("deviceInfo",deviceInfo);
        variable.put("thirdBlueRayRequest",thirdBlueRayRequest);
        OkHttpClient okHttpClient = HttpPool.getInstance();
        Request request = assembleRequest(variable);
        Response response = okHttpClient.newCall(request).execute();
        String resp = response.body().string();
        System.out.println("======="+resp);

        return resp;

    }

    public static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");

    protected Request assembleRequest( Map<String, Object> variable) throws IOException {
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String,Object> map = new TreeMap<>();
        Map deviceInfo6Map = (Map)variable.get("deviceInfo6");
        String timeMillis = System.currentTimeMillis()+"";
        String nonce = UUID.randomUUID().toString().replaceAll("-","");
        map.put("merchantId","1146");
        map.put("serviceType","110");
        map.put("timestamp",timeMillis);
        map.put("nonce",nonce);
        map.put("signKey","fYaBGSQbuYQgafXGqLDueNLakOpnSexW");
        String params = paramsSort(map);
        String sign =  SecurityUtil.MD5(params).toUpperCase();
        log.info("params={},sign={}",params,sign);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("merchantId","1146");
        jsonObject.put("serviceType","110");
        jsonObject.put("timestamp",timeMillis);
        jsonObject.put("nonce",nonce);
        jsonObject.put("sign",sign);
        JSONObject dataJson = new JSONObject();
        dataJson.put("merchantOrderId",variable.get("order_id"));
        dataJson.put("productId","20");
        dataJson.put("userId",variable.get("user_id"));
        dataJson.put("userName","-999999".equals(((Map) variable.get("baseInfo")).get("name")) ? null:((Map) variable.get("baseInfo")).get("name"));
        dataJson.put("idCardNumber","-999999".equals(((Map) variable.get("baseInfo")).get("aadhaar_number")) ? null:((Map) variable.get("baseInfo")).get("aadhaar_number"));
        dataJson.put("userPan","-999999".equals(((Map) variable.get("baseInfo")).get("pan_number")) ? null:((Map) variable.get("baseInfo")).get("pan_number"));
        dataJson.put("userMobile","-999999".equals(((Map) variable.get("baseInfo")).get("mobile")) ? null:((Map) variable.get("baseInfo")).get("mobile"));
        dataJson.put("userSex",sexConvert((String) ((Map) variable.get("baseInfo")).get("sex")));
        dataJson.put("birthDate","-999999".equals(((Map) variable.get("baseInfo")).get("birthday")) ? null:((Map) variable.get("baseInfo")).get("birthday"));
        JSONObject deviceInfoJson = new JSONObject();
        JSONObject networkJson = new JSONObject();
        networkJson.put("ip","-999999".equals(((Map) variable.get("baseInfo")).get("register_ip")) ? null:((Map) variable.get("baseInfo")).get("register_ip"));
        networkJson.put("wifi_count",(int)deviceInfo6Map.get("wifi_count")==-999999? null:deviceInfo6Map.get("wifi_count"));
        JSONObject currentWifiJson = new JSONObject();
        currentWifiJson.put("ssid", "-999999".equals(deviceInfo6Map.get("current_wifi_ssid"))? null:deviceInfo6Map.get("current_wifi_ssid"));
        currentWifiJson.put("mac","-999999".equals(deviceInfo6Map.get("current_wifi_mac"))? null:deviceInfo6Map.get("current_wifi_mac"));
        networkJson.put("current_wifi",currentWifiJson);
        List<JSONObject> configuredWifiList = (List<JSONObject>) ((Map) variable.get("thirdBlueRayRequest")).get("configuredWifis");
        if(configuredWifiList!=null && configuredWifiList.size() > 0){
            networkJson.put("configured_wifi",configuredWifiList);
        }
        deviceInfoJson.put("network",networkJson);

        Map gpsInfoMap = (Map)variable.get("gpsInfo");
        JSONObject locationJson = new JSONObject();
        JSONObject deviceGpsJson = new JSONObject();
        deviceGpsJson.put("latitude","-999999".equals(gpsInfoMap.get("latitude")) ? null:gpsInfoMap.get("latitude"));
        deviceGpsJson.put("longitude","-999999".equals(gpsInfoMap.get("longitude")) ? null:gpsInfoMap.get("longitude"));
        locationJson.put("gps",deviceGpsJson);
        locationJson.put("gpsAddressProvince","-999999".equals(gpsInfoMap.get("gps_province")) ? null:gpsInfoMap.get("gps_province"));
        locationJson.put("gpsAddressCity","-999999".equals(gpsInfoMap.get("gps_city")) ? null:gpsInfoMap.get("gps_city"));
        locationJson.put("gpsAddressStreet","-999999".equals(gpsInfoMap.get("gps_street")) ? null:gpsInfoMap.get("gps_street"));
        deviceInfoJson.put("location",locationJson);

        Map deviceInfoMap = (Map)variable.get("deviceInfo");
        JSONObject hardwareJson = new JSONObject();
        hardwareJson.put("brand","-999999".equals(deviceInfoMap.get("phone_brand")) ? null: deviceInfoMap.get("phone_brand"));
        hardwareJson.put("serialNumber","-999999".equals(deviceInfoMap.get("device_id")) ? null: deviceInfoMap.get("device_id"));
        hardwareJson.put("model","-999999".equals(deviceInfoMap.get("phone_model")) ? null: deviceInfoMap.get("phone_model"));
        //hardwareJson.put("deviceName","-999999".equals(deviceInfoMap.get("device_name")) ? null: deviceInfoMap.get("device_name"));
        hardwareJson.put("product","-999999".equals(deviceInfoMap.get("product")) ? null: deviceInfoMap.get("product"));
        hardwareJson.put("systemVersion","-999999".equals(deviceInfoMap.get("system_version")) ? null: deviceInfoMap.get("system_version"));
        hardwareJson.put("release","-999999".equals(deviceInfoMap.get("system_version")) ? null: deviceInfoMap.get("system_version"));
        hardwareJson.put("sdkVersion","-999999".equals(deviceInfoMap.get("sdk_version")) ? null: deviceInfoMap.get("sdk_version"));
        hardwareJson.put("manufacturername","-999999".equals(deviceInfoMap.get("manufacturer")) ? null: deviceInfoMap.get("manufacturer"));
        hardwareJson.put("hardware","-999999".equals(deviceInfoMap.get("hardware")) ? null: deviceInfoMap.get("hardware"));
        hardwareJson.put("device","-999999".equals(deviceInfoMap.get("device")) ? null: deviceInfoMap.get("device"));
        hardwareJson.put("user","-999999".equals(deviceInfoMap.get("user")) ? null: deviceInfoMap.get("user"));
        hardwareJson.put("tags","-999999".equals(deviceInfoMap.get("tags")) ? null: deviceInfoMap.get("tags"));
        hardwareJson.put("type","-999999".equals(deviceInfoMap.get("type")) ? null: deviceInfoMap.get("type"));
        deviceInfoJson.put("hardware",hardwareJson);



        JSONObject storageJson = new JSONObject();
        String ram_total = (String) deviceInfo6Map.get("ram_total");
        String cash_total = (String) deviceInfo6Map.get("cash_total");
        String ram_can_use = (String) deviceInfo6Map.get("ram_can_use");
        String cash_can_use = (String) deviceInfo6Map.get("cash_can_use");
        storageJson.put("ramTotalSize",storageConvert(ram_total));
        storageJson.put("ramUsableSize",storageConvert(ram_can_use));
        storageJson.put("memoryCardSize",storageConvert(cash_total));
        storageJson.put("memoryCardSizeUse",storageConvert(cash_can_use));
        deviceInfoJson.put("storage",storageJson);


        JSONObject deviceFileCntJson = new JSONObject();
        deviceFileCntJson.put("audioExternal",(int)deviceInfo6Map.get("audio_external")==-999999? null:deviceInfo6Map.get("audio_external"));
        deviceFileCntJson.put("audioInternal",(int)deviceInfo6Map.get("audio_internal")==-999999? null:deviceInfo6Map.get("audio_internal"));
        deviceFileCntJson.put("imagesInternal",(int)deviceInfo6Map.get("images_internal")==-999999? null:deviceInfo6Map.get("images_internal"));
        deviceFileCntJson.put("imagesExternal",(int)deviceInfo6Map.get("images_external")==-999999? null:deviceInfo6Map.get("images_external"));
        deviceFileCntJson.put("videoInternal",(int)deviceInfo6Map.get("video_internal")==-999999? null:deviceInfo6Map.get("video_internal"));
        deviceFileCntJson.put("videoExternal",(int)deviceInfo6Map.get("video_external")==-999999? null:deviceInfo6Map.get("video_external"));
        deviceFileCntJson.put("downloadFiles",(int)deviceInfo6Map.get("download_files")==-999999? null:deviceInfo6Map.get("download_files"));
        deviceInfoJson.put("deviceFileCnt",deviceFileCntJson);


        List<JSONObject> appsJsonArray = new ArrayList<>();
        List<JSONObject> appsList = (List<JSONObject>) ((Map) variable.get("thirdBlueRayRequest")).get("apps");
        if(appsList!=null && appsList.size() > 0){
            appsList.forEach(x->{
                JSONObject appsSubJson = new JSONObject();
                appsSubJson.put("appName",x.getString("app_name"));
                appsSubJson.put("packAge",x.getString("package_name"));
                Date first_install_time = x.getDate("first_install_time");
                if(first_install_time!=null){
                    appsSubJson.put("inTime",first_install_time.getTime());
                }
                Date last_update_time = x.getDate("last_update_time");
                if(last_update_time!=null){
                    appsSubJson.put("upTime",last_update_time.getTime());
                }
                appsSubJson.put("versionName",x.getString("version"));
                if(x.getString("version_code") != null){
                    appsSubJson.put("versionCode",x.getInteger("version_code"));
                }
                if(x.getString("flags") != null){
                    appsSubJson.put("flags",x.getInteger("flags"));
                }
                if(x.getString("iexpress") != null){
                    appsSubJson.put("appType",x.getInteger("iexpress"));
                }
                appsJsonArray.add(appsSubJson);
            });
        }
        deviceInfoJson.put("application",appsJsonArray);


        List<JSONObject> txlsJsonArray = new ArrayList<>();
        List<JSONObject> txlsList = (List<JSONObject>) ((Map) variable.get("thirdBlueRayRequest")).get("txls");
        if(txlsList!=null && txlsList.size() > 0){
            txlsList.forEach(x->{
                JSONObject txlsSubJson = new JSONObject();
                txlsSubJson.put("phone",x.getString("number"));
                txlsSubJson.put("contactName",x.getString("display_name"));
                txlsJsonArray.add(txlsSubJson);
            });
        }
        deviceInfoJson.put("contact",txlsJsonArray);

        List<JSONObject> msgsJsonArray = new ArrayList<>();
        List<JSONObject> msgList = (List<JSONObject>) ((Map) variable.get("thirdBlueRayRequest")).get("msgs");
        if(msgList!=null && msgList.size() > 0){
            msgList.forEach(x->{
                JSONObject msgsSubJson = new JSONObject();
                msgsSubJson.put("body",x.getString("body"));
                msgsSubJson.put("sendOrderReceiveTime",sd.format(new Date(x.getLong("createTime"))));
                msgsSubJson.put("address",x.getString("address"));
                msgsSubJson.put("phone",x.getString("address"));
                Integer type = x.getInteger("type");
                int typeStr;
                if(type == 1){
                    typeStr = 1;
                }else {
                    typeStr = 0;
                }
                msgsSubJson.put("type",typeStr);
                msgsJsonArray.add(msgsSubJson);
            });
        }
        deviceInfoJson.put("sms",msgsJsonArray);

        List<JSONObject> albsJsonArray = new ArrayList<>();
        List<JSONObject> albsList = (List<JSONObject>) ((Map) variable.get("thirdBlueRayRequest")).get("albs");
        if(albsList!=null && albsList.size() > 0){
            albsList.forEach(x->{
                JSONObject albsSubJson = new JSONObject();
                albsSubJson.put("name",x.getString("name"));
                albsSubJson.put("make",x.getString("author"));
                albsSubJson.put("height",x.getString("height"));
                albsSubJson.put("width",x.getString("width"));
                albsSubJson.put("longitudeG",x.getString("longitude"));
                albsSubJson.put("latitudeG",x.getString("latitude"));
                albsSubJson.put("date",x.getString("date"));
                albsSubJson.put("model",x.getString("model"));
                albsJsonArray.add(albsSubJson);
            });
        }
        deviceInfoJson.put("deviceAlbums",albsJsonArray);

        JSONObject generalDataJson = new JSONObject();
        generalDataJson.put("deviceId","-999999".equals(deviceInfoMap.get("device_id")) ? null: deviceInfoMap.get("device_id"));
        generalDataJson.put("andId","-999999".equals(deviceInfoMap.get("device_id")) ? null: deviceInfoMap.get("device_id"));
        generalDataJson.put("gaId","-999999".equals(deviceInfoMap.get("gaid")) ? null: deviceInfoMap.get("gaid"));
        generalDataJson.put("imei","-999999".equals(deviceInfoMap.get("imei1")) ? null: deviceInfoMap.get("imei1"));
        generalDataJson.put("mac","-999999".equals(deviceInfoMap.get("phone_mac")) ? null: deviceInfoMap.get("phone_mac"));
        int lastBootTime = (int)deviceInfo6Map.get("last_boot_time");
        long bootTime = lastBootTime* 1000L;
        generalDataJson.put("lastRootTime",bootTime == -999999000 ? null: bootTime);
        //generalDataJson.put("dbm","");
        generalDataJson.put("networkOperatorName","-999999".equals(deviceInfo6Map.get("network_operator_name")) ? null: deviceInfo6Map.get("network_operator_name"));
        generalDataJson.put("networkType","-999999".equals(deviceInfo6Map.get("network_type")) ? null: deviceInfo6Map.get("network_type"));
        generalDataJson.put("timeZoneId","-999999".equals(deviceInfo6Map.get("time_zone_id")) ? null: deviceInfo6Map.get("time_zone_id"));
        generalDataJson.put("language","-999999".equals(deviceInfo6Map.get("default_language")) ? null: deviceInfo6Map.get("default_language"));
        generalDataJson.put("elapsedRealtime",(long)deviceInfo6Map.get("elapsed_realtime")==-999999L? null:deviceInfo6Map.get("elapsed_realtime"));
        generalDataJson.put("isUsbDebug",(int)deviceInfo6Map.get("is_using_debug")==-999999? null:tagsConvert((int)deviceInfo6Map.get("is_using_debug")));
        generalDataJson.put("simulator",(int)deviceInfoMap.get("is_emulator")==-999999? null:tagsConvert((int)deviceInfoMap.get("is_emulator")));
        generalDataJson.put("isUsingProxyPort",(int)deviceInfo6Map.get("is_using_proxy_port")==-999999? null:tagsConvert((int)deviceInfo6Map.get("is_using_proxy_port")));
        generalDataJson.put("isUsingVpn",(int)deviceInfo6Map.get("is_using_vpn")==-999999? null:tagsConvert((int)deviceInfo6Map.get("is_using_vpn")));
        generalDataJson.put("isRoot",(int)deviceInfo6Map.get("is_rooted")==-999999? null:tagsConvert((int)deviceInfo6Map.get("is_rooted")));
        deviceInfoJson.put("generalData",generalDataJson);

        JSONObject batteryStatusJson = new JSONObject();
        batteryStatusJson.put("batteryPct","-999999".equals(deviceInfo6Map.get("battery_pct"))? null:deviceInfo6Map.get("battery_pct"));
        batteryStatusJson.put("isUsbCharge",(int)deviceInfo6Map.get("is_usb_charge")==-999999? null:deviceInfo6Map.get("is_usb_charge"));
        batteryStatusJson.put("isAcCharge",(int)deviceInfo6Map.get("is_ac_charge")==-999999? null:deviceInfo6Map.get("is_ac_charge"));
        batteryStatusJson.put("isCharging",(int)deviceInfo6Map.get("is_charging")==-999999? null:deviceInfo6Map.get("is_charging"));
        deviceInfoJson.put("batteryStatus",batteryStatusJson);


        JSONObject userNamesJson = new JSONObject();
        userNamesJson.put("idCardOcrName","-999999".equals(deviceInfo6Map.get("advance_ocr_name"))? null:deviceInfo6Map.get("advance_ocr_name"));
        userNamesJson.put("panOcrName","-999999".equals(deviceInfo6Map.get("advance_ocr_name"))? null:deviceInfo6Map.get("advance_ocr_name"));
        userNamesJson.put("panValidateName","-999999".equals(deviceInfo6Map.get("advance_ocr_name"))? null:deviceInfo6Map.get("advance_ocr_name"));
        userNamesJson.put("idCardModifyName","-999999".equals(deviceInfo6Map.get("advance_custom_name"))? null:deviceInfo6Map.get("advance_custom_name"));
        userNamesJson.put("panModifyName","-999999".equals(deviceInfo6Map.get("advance_custom_name"))? null:deviceInfo6Map.get("advance_custom_name"));
        userNamesJson.put("bankCardValidateName","-999999".equals(deviceInfo6Map.get("advance_ocr_name"))? null:deviceInfo6Map.get("advance_ocr_name"));
        deviceInfoJson.put("userNames",userNamesJson);

        dataJson.put("deviceInfo",deviceInfoJson);
        jsonObject.put("data",dataJson.toJSONString());
        String zip = GZipUtils.compress(jsonObject.toJSONString());
        JSONObject paramJson = new JSONObject();
        paramJson.put("compressData",zip);
        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(JSON_TYPE, paramJson.toJSONString());
        log.info("data={}",jsonObject.toJSONString());
        return new Request.Builder()
                .url("http://api.mercurycreditnb.com/riskmirror/mercuryi/v1/createOrder")
                .post(requestBody)
                .build();
    }



    @LogDeals(value = "测试尼日永青")
    @PostMapping("/testNgCreditScore")
    public String testNgCreditScore(@RequestBody JSONObject json1) throws IOException {
        OkHttpClient okHttpClient = HttpPool.getInstance();
        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(JSON_TYPE, json1.toJSONString());
        Request request = new Request.Builder()
                .addHeader("key", "b891d8951f824060939f2fbb971b32a7")
                .addHeader("secret","O9swkg5nfSHREcfVx2VeyMOqESTiDJb0")
                .addHeader("companyName","cash888")
                .addHeader("Content-Type","application/json")
                .url("https://api.evergreen-credit.com/ng/blackList")
                .post(requestBody)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        String resp = response.body().string();
        System.out.println("======="+resp);
        return resp;
    }




    @LogDeals(value = "测试印度v11")
    @PostMapping("/testindiaV11Score")
    public String testindiaV11Score(@RequestBody JSONObject json1) throws IOException {
        OkHttpClient okHttpClient = HttpPool.getInstance();

        JSONObject jsonObject = new JSONObject();
        JSONObject smsFeat = new JSONObject();
        smsFeat.put("sms_disburl_7d_sms_morning_sum",1);
        smsFeat.put("sms_repay_sms_overdue_7_15d_diff",1);
        smsFeat.put("sms_repay_7d_sms_morning_sum",1);
        smsFeat.put("sms_reminder_percent_new",1);
        smsFeat.put("sms_keyword_15d_increase_zz",1);
        smsFeat.put("sms_keyword_30d_increase_zz",1);
        smsFeat.put("sms_keyword_7d_increase_zz",1);
        smsFeat.put("sms_keyword_3d_increase_zz",1);
        smsFeat.put("sms_keyword_24h_increase_zz",1);
        smsFeat.put("sms_keyword_24h_reminder",1);
        smsFeat.put("sms_keyword_24h_reminder_zz",1);
        smsFeat.put("sms_disburl_30d_sms_afternoon_sum",1);
        smsFeat.put("sms_cnt_average_per_day_7d",1);
        smsFeat.put("sms_cnt_average_per_day_15d",1);
        smsFeat.put("sms_cnt_average_per_day_30d",1);
        smsFeat.put("sms_5times_addresslist_cnt",1);
        smsFeat.put("sms_5times_contacts_cnt",1);
        smsFeat.put("sms_contacts_cnt",1);
        smsFeat.put("sms_contacts_cnt_30d",1);
        smsFeat.put("sms_cnt_30d",1);
        smsFeat.put("sms_cnt",1);
        smsFeat.put("sms_keyword_60d_reminder_zz",1);
        smsFeat.put("sms_keyword_30d_reminder_zz",1);
        smsFeat.put("sms_keyword_15d_reminder_zz",1);
        smsFeat.put("sms_keyword_7d_reminder_zz",1);
        smsFeat.put("sms_keyword_3d_reminder_zz",1);
        smsFeat.put("sms_keyword_60d_reminder",1);
        smsFeat.put("sms_keyword_15d_reminder",1);
        smsFeat.put("sms_keyword_30d_reminder",1);
        smsFeat.put("sms_keyword_7d_reminder",1);
        smsFeat.put("sms_keyword_60d_disburs_zz",1);
        smsFeat.put("sms_keyword_30d_disburs_zz",1);
        smsFeat.put("sms_keyword_15d_disburs_zz",1);
        smsFeat.put("sms_keyword_7d_disburs_zz",1);
        smsFeat.put("sms_keyword_3d_disburs_zz",1);
        smsFeat.put("sms_keyword_60d_disburs",1);
        smsFeat.put("sms_keyword_30d_disburs",1);
        smsFeat.put("sms_keyword_15d_disburs",1);
        smsFeat.put("sms_keyword_7d_disburs",1);
        smsFeat.put("sms_keyword_3d_disburs",1);
        smsFeat.put("sms_keyword_60d_reject",1);
        smsFeat.put("sms_keyword_30d_reject",1);
        smsFeat.put("sms_keyword_15d_reject",1);
        smsFeat.put("sms_keyword_7d_reject",1);
        smsFeat.put("sms_keyword_30d_overdue_zz",1);
        smsFeat.put("sms_keyword_60d_overdue",1);
        smsFeat.put("sms_keyword_30d_overdue",1);
        smsFeat.put("sms_keyword_15d_overdue",1);
        smsFeat.put("sms_keyword_7d_overdue",1);
        smsFeat.put("sms_keyword_60d_repay_zz",1);
        smsFeat.put("sms_keyword_30d_repay_zz",1);
        smsFeat.put("sms_keyword_15d_repay_zz",1);
        smsFeat.put("sms_keyword_7d_repay_zz",1);
        smsFeat.put("sms_keyword_60d_repay",1);
        smsFeat.put("sms_keyword_30d_repay",1);
        smsFeat.put("sms_keyword_15d_repay",1);
        smsFeat.put("sms_keyword_7d_repay",1);

        JSONObject addressBookFeat = new JSONObject();
        addressBookFeat.put("addresslist_cnt",1);
        addressBookFeat.put("addresslist_clean_cnt",1);

        JSONObject galleryFeat = new JSONObject();
        galleryFeat.put("photo_cnt",1);
        galleryFeat.put("photo_storage_cnt",1);
        galleryFeat.put("photo_shoot_cnt",1);
        galleryFeat.put("photo_auther_cnt",1);
        galleryFeat.put("photo_model_cnt",1);
        galleryFeat.put("max_auther_photo_cnt",1);
        galleryFeat.put("max_model_photo_cnt",1);
        galleryFeat.put("max_auther_photo_cnt_30d",1);
        galleryFeat.put("max_auther_photo_cnt_7d",1);
        galleryFeat.put("max_model_photo_cnt_30d",1);
        galleryFeat.put("max_model_photo_cnt_7d",1);
        galleryFeat.put("photo_shoot_cnt_30d",1);
        galleryFeat.put("photo_shoot_cnt_7d",1);
        galleryFeat.put("photo_shoot_cnt_24h",1);
        galleryFeat.put("photo_storage_cnt_30d",1);
        galleryFeat.put("photo_storage_cnt_7d",1);
        galleryFeat.put("photo_auther_cnt_30d",1);
        galleryFeat.put("photo_auther_cnt_7d",1);
        galleryFeat.put("photo_auther_cnt_24h",1);
        galleryFeat.put("photo_model_cnt_30d",1);
        galleryFeat.put("photo_model_cnt_7d",1);
        galleryFeat.put("photo_model_cnt_24h",1);

        JSONObject bank_account_json = new JSONObject();
        bank_account_json.put("card_no","aaa");
        bank_account_json.put("ifsc_code","bbb");
        bank_account_json.put("bank_holder_name","ccc");
        List<JSONObject> emergency_contacts_list = new ArrayList<>();
        JSONObject emergency_contacts_json = new JSONObject();
        List<String> list1 = Arrays.asList("123", "456");
        emergency_contacts_json.put("name","aaa");
        emergency_contacts_json.put("relationship","bbb");
        emergency_contacts_json.put("mobiles",list1);
        emergency_contacts_list.add(emergency_contacts_json);
        JSONObject baseInfoJson = new JSONObject();
        baseInfoJson.put("job","Farmer");
        baseInfoJson.put("mobile","9954856988");
        baseInfoJson.put("bank_account",bank_account_json);
        baseInfoJson.put("emergency_contacts",emergency_contacts_list);
        List<JSONObject> txlsList = new ArrayList<>();
        JSONObject txls_json = new JSONObject();
        List<String> list2 = Arrays.asList("123", "456");
        jsonObject.put("phone_numbers",list2);
        jsonObject.put("display_name","Jio");
        txlsList.add(txls_json);
        List<JSONObject> albsList = new ArrayList<>();
        JSONObject albs_json = new JSONObject();
        albs_json.put("name","Screenshot_2022-06-30-19-44-44-649_com.android.contacts.jpg");
        albs_json.put("author","huawei");
        albs_json.put("height","2");
        albs_json.put("width","10");
        albs_json.put("longitude","0.0");
        albs_json.put("latitude","0.0");
        albs_json.put("model","P20");
        albs_json.put("time","2022:06:30 19:44:44");
        albsList.add(albs_json);
        List<JSONObject> smsList = new ArrayList<>();
        JSONObject sms_json = new JSONObject();
        sms_json.put("body","aaa");
        sms_json.put("address","bbb");
        sms_json.put("createTime",1664354503311L);
        sms_json.put("type",1);
        sms_json.put("date",1664354503311L);
        smsList.add(sms_json);

        List<JSONObject> smsNewList = new ArrayList<>();
        if(smsList!=null && smsList.size() > 0){
            smsList.forEach(x->{
                JSONObject smsSubJson = new JSONObject();
                smsSubJson.put("body",x.get("body"));
                smsSubJson.put("address",x.get("address"));
                smsSubJson.put("createTime",x.get("date"));
                smsSubJson.put("type",x.get("type"));
                smsSubJson.put("date",x.get("createTime"));
                smsNewList.add(smsSubJson);
            });
        }

        jsonObject.put("sms_feat",smsFeat);
        jsonObject.put("addressbook_feat",addressBookFeat);
        jsonObject.put("gallery_feat",galleryFeat);
        jsonObject.put("base_info",baseInfoJson);
        jsonObject.put("sms_src",smsNewList);
        jsonObject.put("addressbook_src",txlsList);
        jsonObject.put("gallery_src",albsList);
        System.out.println(jsonObject.toJSONString());
        okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(JSON_TYPE, jsonObject.toJSONString());
        Request request = new Request.Builder()
                .url("http://8.219.119.212:5000/India/V11/CreditScore")
                .post(requestBody)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        String resp = response.body().string();
        System.out.println("======="+resp);
        return resp;
    }

    private static Long ramConvert(String storage) {
        try {
            if(storage.equals("-999999")){
                return null;
            }else if(storage.contains("MB")){
                String[] s = storage.split(" ");
                if(s.length > 0){
                    double num = Double.parseDouble(s[0]);
                    return (long) (num * 1024*1024L);
                }
            }else if(storage.contains("GB")){
                String[] s = storage.split(" ");
                if(s.length > 0){
                    double num = Double.parseDouble(s[0]);
                    return (long) (num * 1024*1024*1024L);
                }
            }else {
                return 0L;
            }
            return 0L;
        }catch (Exception e){
            return null;
        }
    }



    //性别sex值转换
    protected String sexConvert(String sex) {
        if(sex.equals("1")){
            return "10";
        }else if(sex.equals("2")){
            return "20";
        }else {
            return "10";
        }
    }


    //int bollean值转换
    private boolean tagsConvert(int tag) {
        if(tag == 1){
            return true;
        }else {
            return false;
        }
    }



    private static Long storageConvert(String storage) {
        try {
            if(storage.equals("-999999")){
                return null;
            }else if(storage.contains("एमबी") || storage.contains("MB")){
                String[] s = storage.split(" ");
                if(s.length > 0){
                    double num = Double.parseDouble(s[0]);
                    return (long) (num * 1000000L);
                }
            }else if(storage.contains("जीबी") || storage.contains("GB") || storage.contains("ஜி.பை.")){
                String[] s = storage.split(" ");
                if(s.length > 0){
                    double num = Double.parseDouble(s[0]);
                    return (long) (num * 1000000000L);
                }
            }else {
                return 0L;
            }
            return 0L;
        }catch (Exception e){
            return null;
        }
    }


    /**
     * 请求参数排序拼接
     * @param map
     * @return
     */
    public String paramsSort(Map<String,Object> map) {
        val tobeSignStr = map.entrySet().stream().sorted(HashMap.Entry.comparingByKey()).map(entry -> entry.getKey() + "=" + entry.getValue()).reduce((acc, s) -> acc + "&" + s).orElse("");
        return tobeSignStr;
    }

    public static void main(String[] args) {


        Long aLong = ramConvert("56.50 GB");
        System.out.println(aLong);
        System.out.println(ramConvert("56.50 GB").intValue());


    }




    public static boolean isEnglish(String p) {
        byte[] bytes = p.getBytes();
        int i = bytes.length;//i为字节长度
        int j = p.length();//j为字符长度
        return i == j;
    }


    public static String getDateToEsDate(String dateStr) {

        String dateRes = null;
        if (null == dateStr) {
            return dateRes;
        }

        try {
            // 字符串转日期
            DateTimeFormatter strToDateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            TemporalAccessor dateTemporal = strToDateFormatter.parse(dateStr);
            LocalDateTime date = LocalDateTime.from(dateTemporal);

            //System.out.println("字符串转为日期结果:" + date);

            // 格式化日期时间
            DateTimeFormatter dateToStrFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            dateRes = dateToStrFormatter.format(date);

            dateRes = dateRes.replace(" ","T") + "Z";
            //System.out.println("格式化日期时间:" + dateRes);
        } catch (Exception ex) {
            // System.out.println(ex);
        } finally {
            return dateRes;
        }
    }


}

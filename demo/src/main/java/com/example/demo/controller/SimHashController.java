package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.annotation.LogDeals;
import com.example.demo.dao.sass.*;
import com.example.demo.entity.sass.SimHashInfoCopy1PO;
import com.example.demo.entity.sass.SimHashInfoCopyPO;
import com.example.demo.entity.sass.SimHashInfoPO;
import com.example.demo.utils.SimHashUtil;
import com.example.demo.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/simhash")
public class SimHashController {

    @Resource
    private ZPhonesCopyDao zPhonesCopyDao;

    @Resource
    private ZPhonesDao zPhonesDao;

    @Resource
    private SimHashInfoCopyDao simHashInfoCopyDao;

    @Resource
    private SimHashInfoCopy1Dao simHashInfoCopy1Dao;



    @LogDeals(value = "计算Hashcode值接口")
    @PostMapping("/getHashcodeCopy")
    public Result getHashcodeCopy(@RequestBody JSONObject json) {
        List<String> userPhones = zPhonesCopyDao.groupByUserPhone();
        for (String userPhone : userPhones) {
            List<String> zPhonesDaoPhones = zPhonesCopyDao.findPhones(userPhone);
            zPhonesDaoPhones = zPhonesDaoPhones.stream().filter(x-> x != null && !x.equals("") ).distinct().collect(Collectors.toList());
            StringBuilder s = new StringBuilder("'");
            for (String phone : zPhonesDaoPhones) {
                s.append(phone).append("'");
            }
            SimHashUtil mySimHash_1 = new SimHashUtil(s.toString(), 64);
            SimHashInfoCopyPO simHashInfoPO = new SimHashInfoCopyPO();
            simHashInfoPO.setMobile(userPhone);
            simHashInfoPO.setHash_code( mySimHash_1.simHash().toString() );
            simHashInfoCopyDao.save(simHashInfoPO);
        }
        return Result.builder().data("").build();
    }


    @LogDeals(value = "计算汉明距离接口")
    @PostMapping("/getHangmingCopy")
    public Result getHangmingCopy(@RequestBody JSONObject json) {
        long start = System.currentTimeMillis();
        List<SimHashInfoCopyPO> simHashInfoPOList = simHashInfoCopyDao.findAll();
        for (int i =0;i<simHashInfoPOList.size();i++){
            Map<String,Integer> hanMap = new HashMap<>();
            SimHashInfoCopyPO simHashInfoPO = simHashInfoPOList.get(i);
            String hash_code = simHashInfoPO.getHash_code();
            List<String> hashCodeList = simHashInfoCopyDao.findHashCode(hash_code);
            int count = 0;
            List<String> numberList = new ArrayList<>();
            for (String s : hashCodeList){
                BigInteger a=new BigInteger(hash_code);
                BigInteger b=new BigInteger(s);
                int hanming = SimHashUtil.getHammingDistanceNew(a,b);
                hanMap.put(hash_code+"_"+s,hanming);
                if(hanming <= 3){
                    count++;
                    List<SimHashInfoCopyPO> mobileList = simHashInfoCopyDao.findmobile(s);
                    if(!CollectionUtils.isEmpty(mobileList)){
                        numberList.add(mobileList.get(0).getMobile());
                    }
                }
            }
            List<Map.Entry<String, Integer>> list = new ArrayList(hanMap.entrySet());
            Collections.sort(list, (o1, o2) -> (o1.getValue().intValue() - o2.getValue().intValue()));
            String min = list.get(0).getKey();
            System.out.println("key1 = " + min + ", value1 = " + list.get(0).getValue()+",time="+(System.currentTimeMillis()-start)+"ms");
            if(numberList.size() != 0){
                if(numberList.size() > 10){
                    numberList = numberList.subList(0,10);
                }
                simHashInfoCopyDao.updateDistance(list.get(0).getValue(),min,count,numberList.toString(),simHashInfoPO.getMobile());
            }else {
                simHashInfoCopyDao.updateDistance(list.get(0).getValue(),min,count,null,simHashInfoPO.getMobile());
            }
        }
        return Result.builder().data("").build();
    }


    @LogDeals(value = "获取相似度交集比例接口")
    @PostMapping("/getScaleCopy")
    public Result getScaleCopy(@RequestBody JSONObject json) {
        List<SimHashInfoCopyPO> simHashInfoPOList = simHashInfoCopyDao.findAll();
        for (int i =0;i<simHashInfoPOList.size();i++){
            SimHashInfoCopyPO simHashInfoPO = simHashInfoPOList.get(i);
            String scale = simHashInfoPO.getScale();
            String[] scaleArray = scale.split("_");
            String hashCode2 = scaleArray[1];
            List<SimHashInfoCopyPO> simHashInfoList2 = simHashInfoCopyDao.findSimHashInfoPOByHash_code(hashCode2);
            SimHashInfoCopyPO simHashInfo2 = simHashInfoList2.get(0);
            List<String> finalPhones1 = zPhonesCopyDao.findPhones(simHashInfoPO.getMobile());
            if(CollectionUtils.isEmpty(finalPhones1)){
                finalPhones1 = zPhonesDao.findPhones(simHashInfoPO.getMobile());
            }
            List<String> finalPhones2 = zPhonesCopyDao.findPhones(simHashInfo2.getMobile());
            if(finalPhones2 == null || finalPhones2.size() <= 0 ){
                finalPhones2 = zPhonesDao.findPhones(simHashInfo2.getMobile());
            }
           //log.info("=====>mobile1={},mobile2={},finalPhones1={},finalPhones2={}",simHashInfoPO.getMobile(),simHashInfo2.getMobile(),finalPhones1,finalPhones2);
            List<String> list = Arrays.asList("\\*","-","#"," ","\\+",",","@",".",":",";");
            List<String> phones1 = finalPhones1.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
            List<String> phones2 = finalPhones2.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
            list.forEach(s->{
                phones1.replaceAll(x->x.replaceAll(s,""));
                phones2.replaceAll(x->x.replaceAll(s,""));
            });
            List<String> accountIdList = phones1.stream().filter(phones2::contains).collect(Collectors.toList());
            List<String> collect = accountIdList.stream().distinct().collect(Collectors.toList());
            DecimalFormat df = new DecimalFormat("0.00000000");
            String scaleNew;
            if(phones1.size() <= phones2.size()){
                scaleNew = df.format((double) collect.size()/phones1.size());
            }else {
                scaleNew = df.format((double) collect.size()/phones2.size());
            }

            String length = phones1.size()+"_"+phones2.size()+"_"+collect.size();
            simHashInfoCopyDao.updateBiLi(scaleNew,length,simHashInfoPO.getMobile());

            System.out.println("mobile = " + simHashInfoPO.getMobile()+ ", hashCode1 = " + scaleArray[0]  + ", hashCode2 = " + hashCode2 + ", scaleNew = " + scaleNew);
        }
        return Result.builder().data("").build();
    }



    @LogDeals(value = "获取相似度交集比例接口")
    @PostMapping("/ces")
    public Result ces(@RequestBody JSONObject json) {

        List<String> finalPhones1 = zPhonesCopyDao.findPhones(json.getString("phone1"));
        List<String> finalPhones2 = zPhonesCopyDao.findPhones(json.getString("phone2"));
        if(CollectionUtils.isEmpty(finalPhones1)){
            finalPhones1 = zPhonesDao.findPhones(json.getString("phone1"));
        }
        if(finalPhones2 == null || finalPhones2.size() <= 0 ){
            finalPhones2 = zPhonesDao.findPhones(json.getString("phone2"));
        }


        List<String> phones = finalPhones1.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<String> phones2 = finalPhones2.stream().filter(Objects::nonNull).distinct().collect(Collectors.toList());
        List<String> list = Arrays.asList("\\*","-","#"," ","\\+",",","@",".",":");
        list.forEach(s->{
            phones.replaceAll(x->x.replaceAll(s,""));
            phones2.replaceAll(x->x.replaceAll(s,""));
        });

        String s1 = "'";
        for (String s : phones) {
            s1 += s + "'";
        }

        String s2 = "'";
        for (String s : phones2) {
            s2 += s + "'";
        }


        SimHashUtil mySimHash_1 = new SimHashUtil(s1, 64);
        SimHashUtil mySimHash_2 = new SimHashUtil(s2, 64);

        //Double similar = mySimHash_1.getSimilar(mySimHash_2);

        int similar = mySimHash_1.getHammingDistance(mySimHash_2);
        Double similar1 = mySimHash_1.getSimilar(mySimHash_2);

        List<String> accountIdList1 = phones.stream().filter(phones2::contains).collect(Collectors.toList());
        System.out.println(accountIdList1.toString());
        log.info("hash={},phones1={},phones2={},jiaojisize={},size={}",similar,phones.size(),phones2.size(),accountIdList1.size(),accountIdList1.stream().distinct().count());
        return Result.builder().data("").build();
    }



    @LogDeals(value = "计算汉明距离接口")
    @PostMapping("/getHangmingOne")
    public Result getHangmingOne(@RequestBody JSONObject json) {
        Optional<SimHashInfoCopyPO> simHashInfoCopyPOOptional = simHashInfoCopyDao.findById(49151);
        SimHashInfoCopyPO simHashInfoPO = simHashInfoCopyPOOptional.get();
        Map<String,Integer> hanMap = new HashMap<>();
        String hash_code = simHashInfoPO.getHash_code();
        List<String> hashCodeList = simHashInfoCopyDao.findHashCode(hash_code);
        int count = 0;
        for (String s : hashCodeList){
            BigInteger a=new BigInteger(hash_code);
            BigInteger b=new BigInteger(s);
            int hanming = SimHashUtil.getHammingDistanceNew(a,b);
            hanMap.put(hash_code+"_"+s,hanming);
            if(hanming <= 3){
                count++;
            }
        }
        List<Map.Entry<String, Integer>> list = new ArrayList(hanMap.entrySet());
        Collections.sort(list, (o1, o2) -> (o1.getValue().intValue() - o2.getValue().intValue()));
        String min = list.get(0).getKey();
        System.out.println("key1 = " + min + ", value1 = " + list.get(0).getValue()+ ", count = "+count);
        //simHashInfoCopyDao.updateDistance(list.get(0).getValue(),min,count,simHashInfoPO.getMobile());

        return Result.builder().data("").build();
    }



    @LogDeals(value = "计算Hashcode值接口New")
    @PostMapping("/getHashcodeCopyNew")
    public Result getHashcodeCopyNew(@RequestBody JSONObject json) {
        List<String> userPhones = zPhonesCopyDao.groupByUserPhone();
        Date date = new Date();
        for (String userPhone : userPhones) {
            List<String> zPhonesDaoPhones = zPhonesCopyDao.findPhones(userPhone);
            zPhonesDaoPhones = zPhonesDaoPhones.stream().filter(x-> x != null  && x.length() >= 9 ).distinct().collect(Collectors.toList());
            StringBuilder s = new StringBuilder("'");
            for (String phone : zPhonesDaoPhones) {
                s.append(phone).append("'");
            }
            SimHashUtil mySimHash_1 = new SimHashUtil(s.toString(), 64);
            SimHashInfoCopy1PO simHashInfoPO = new SimHashInfoCopy1PO();
            simHashInfoPO.setMobile(userPhone);
            simHashInfoPO.setHash_code( mySimHash_1.simHash().toString() );
            simHashInfoPO.setCounty("thai");
            simHashInfoPO.setBad_sign(0);
            simHashInfoPO.setCreate_time(date);
            simHashInfoCopy1Dao.save(simHashInfoPO);
            System.out.println("userPhone = " + userPhone + ", Hash_code = " + mySimHash_1.simHash().toString());
        }
        return Result.builder().data("").build();
    }

}

package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.SimHash;
import com.example.demo.annotation.LogDeals;
import com.example.demo.dao.sass.*;
import com.example.demo.entity.risk.RiskTaskPo;
import com.example.demo.entity.sass.LoanOrderPO;
import com.example.demo.entity.sass.SimHashInfoCopy1PO;
import com.example.demo.entity.sass.SimHashInfoCopyPO;
import com.example.demo.entity.sass.SimHashInfoPO;
import com.example.demo.utils.SimHashUtil;
import com.example.demo.vo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/sass")
public class SassController {

    @Resource
    private LoanOrderDao loanOrderDao;

    @Resource
    private ZPhonesDao zPhonesDao;

    @Resource
    private SimHashInfoDao simHashInfoDao;

    @Resource
    private SimHashInfoCopyDao simHashInfoCopyDao;

    @LogDeals(value = "查询sass订单接口")
    @PostMapping("/findOrderById")
    public Result findOrderById(@RequestBody JSONObject json) {
        LoanOrderPO loanOrderPO = loanOrderDao.findOrderId(json.getString("id"));
        return Result.builder().data(loanOrderPO).build();
    }


    @LogDeals(value = "查询z_phones接口")
    @PostMapping("/findPhones")
    public Result findPhones(@RequestBody JSONObject json) {
        List<String> zPhonesDaoPhones = zPhonesDao.findPhones("871985264");
        List<String> zPhonesDaoPhones2 = zPhonesDao.findPhones("632363929");
        List<String> zPhonesDaoPhones3 = zPhonesDao.findPhones("972739271");
        List<String> zPhonesDaoPhones4 = zPhonesDao.findPhones("620853900");
        List<String> zPhonesDaoPhones5 = zPhonesDao.findPhones("610535728");
        List<String> zPhonesDaoPhones6 = zPhonesDao.findPhones("610684771");
        List<String> zPhonesDaoPhones7 = zPhonesDao.findPhones("910672956");
        String s1 = "'";
        for (String s : zPhonesDaoPhones) {
            s1 += s + "'";
        }
        String s2 = "'";
        for (String s : zPhonesDaoPhones2) {
            s2 += s + "'";
        }

        String s3 = "'";
        for (String s : zPhonesDaoPhones3) {
            s3 += s + "'";
        }

        String s4 = "'";
        for (String s : zPhonesDaoPhones4) {
            s4 += s + "'";
        }


        String s5 = "'";
        for (String s : zPhonesDaoPhones5) {
            s5 += s + "'";
        }

        String s6 = "'";
        for (String s : zPhonesDaoPhones6) {
            s6 += s + "'";
        }

        String s7 = "'";
        for (String s : zPhonesDaoPhones7) {
            s7 += s + "'";
        }

        SimHashUtil mySimHash_1 = new SimHashUtil(s1, 64);
        SimHashUtil mySimHash_2 = new SimHashUtil(s2, 64);
        SimHashUtil mySimHash_3 = new SimHashUtil(s3, 64);
        SimHashUtil mySimHash_4 = new SimHashUtil(s4, 64);
        SimHashUtil mySimHash_5 = new SimHashUtil(s5, 64);
        SimHashUtil mySimHash_6 = new SimHashUtil(s6, 64);
        SimHashUtil mySimHash_7 = new SimHashUtil(s7, 64);
        int i12 = mySimHash_1.getHammingDistance(mySimHash_2);
        int i13 = mySimHash_1.getHammingDistance(mySimHash_3);
        int i14 = mySimHash_1.getHammingDistance(mySimHash_4);
        int i23 = mySimHash_2.getHammingDistance(mySimHash_3);
        int i24 = mySimHash_2.getHammingDistance(mySimHash_4);
        int i34 = mySimHash_3.getHammingDistance(mySimHash_4);

        int i56 = mySimHash_5.getHammingDistance(mySimHash_6);
        int i57 = mySimHash_5.getHammingDistance(mySimHash_7);
        int i67 = mySimHash_6.getHammingDistance(mySimHash_7);

        log.info("i12={},i13={},i14={},i23={},i24={},i34={},i56={},i57={},i67={}",i12,i13,i14,i23,i24,i34,i56,i57,i67);

        return Result.builder().data("").build();
    }

    @LogDeals(value = "计算Hashcode值接口")
    @PostMapping("/getHashcode")
    public Result getHashcode(@RequestBody JSONObject json) {
        List<String> userPhones = zPhonesDao.groupByUserPhone();
        for (String userPhone : userPhones) {
            List<String> zPhonesDaoPhones = zPhonesDao.findPhones(userPhone);
            zPhonesDaoPhones = zPhonesDaoPhones.stream().filter(x-> x != null && !x.equals("") ).distinct().collect(Collectors.toList());
            String s = "'";
            for (String phone : zPhonesDaoPhones) {
                s += phone + "'";
            }

            SimHashUtil mySimHash_1 = new SimHashUtil(s, 64);
            SimHashInfoCopyPO simHashInfoPO = new SimHashInfoCopyPO();
            simHashInfoPO.setMobile(userPhone);
            simHashInfoPO.setHash_code( mySimHash_1.simHash().toString() );
            simHashInfoCopyDao.save(simHashInfoPO);
            System.out.println("mobile = " + userPhone + ", hashCode = " + mySimHash_1.simHash().toString());
        }
        return Result.builder().data("").build();
    }


    @LogDeals(value = "计算汉明距离接口")
    @PostMapping("/getHangming")
    public Result getHangming(@RequestBody JSONObject json) {
        List<SimHashInfoPO> simHashInfoPOList = simHashInfoDao.findAll();
        for (int i =0;i<simHashInfoPOList.size();i++){
            Map<String,Integer> hanMap = new HashMap<>();
            SimHashInfoPO simHashInfoPO = simHashInfoPOList.get(i);
            String hash_code = simHashInfoPO.getHash_code();
            List<String> hashCodeList = simHashInfoDao.findHashCode(hash_code);
            int count = 0;
            for (String s : hashCodeList){
                BigInteger a=new BigInteger(hash_code);
                BigInteger b=new BigInteger(s);
               // int hanming = SimHash.hammingDistanceNew(a,b);
                int hanming = SimHashUtil.getHammingDistanceNew(a,b);
                hanMap.put(hash_code+"_"+s,hanming);
                if(hanming <= 3){
                    count++;
                }
            }
            List<Map.Entry<String, Integer>> list = new ArrayList(hanMap.entrySet());
            Collections.sort(list, (o1, o2) -> (o1.getValue().intValue() - o2.getValue().intValue()));
            String min = list.get(0).getKey();
            System.out.println("key1 = " + min + ", value1 = " + list.get(0).getValue());
            simHashInfoDao.updateDistance(list.get(0).getValue(),min,count,simHashInfoPO.getMobile());
        }
        return Result.builder().data("").build();
    }


    @LogDeals(value = "获取相似度交集比例接口")
    @PostMapping("/getScale")
    public Result getScale(@RequestBody JSONObject json) {
        List<SimHashInfoPO> simHashInfoPOList = simHashInfoDao.findAll();
        for (int i =0;i<simHashInfoPOList.size();i++){
            SimHashInfoPO simHashInfoPO = simHashInfoPOList.get(i);
            String scale = simHashInfoPO.getScale();
            String[] scaleArray = scale.split("_");
            String hashCode2 = scaleArray[1];
           // System.out.println("mobile = " + simHashInfoPO.getMobile() + ", hashCode2 = " + hashCode2 );
            List<SimHashInfoPO> simHashInfoList = simHashInfoDao.findSimHashInfoPOByHash_code(hashCode2);
//            if(simHashInfoList.size() > 1){
//                System.out.println("==============================hashcode="+simHashInfoList.get(0).getHash_code());
//                continue;
//            }
            SimHashInfoPO simHashInfo2 = simHashInfoList.get(0);
            List<String> finalPhones1 = zPhonesDao.findPhones(simHashInfoPO.getMobile());
            List<String> finalPhones2 = zPhonesDao.findPhones(simHashInfo2.getMobile());
            List<String> list = Arrays.asList("\\*","-","#"," ","\\+",",");
            List<String> phones1 = finalPhones1.stream().distinct().collect(Collectors.toList());
            List<String> phones2 = finalPhones2.stream().distinct().collect(Collectors.toList());
            list.forEach(s->{
                phones1.replaceAll(x->x.replaceAll(s,""));
                phones2.replaceAll(x->x.replaceAll(s,""));
            });
            List<String> accountIdList = phones1.stream().filter(phones2::contains).collect(Collectors.toList());
            List<String> collect = accountIdList.stream().distinct().collect(Collectors.toList());
            DecimalFormat df = new DecimalFormat("0.00000000");
            String scaleNew = "";
            if(phones1.size() <= phones2.size()){
                scaleNew = df.format((double) collect.size()/phones1.size());
            }else {
                scaleNew = df.format((double) collect.size()/phones2.size());
            }
            String length = phones1.size()+"_"+phones2.size()+"_"+collect.size();
            simHashInfoDao.updateBiLi(scaleNew,length,simHashInfoPO.getMobile());
            System.out.println("mobile = " + simHashInfoPO.getMobile()+ ", hashCode1 = " + scaleArray[0]  + ", hashCode2 = " + hashCode2 + ", scaleNew = " + scaleNew);
        }
        return Result.builder().data("").build();
    }


//    @LogDeals(value = "获取null相似度交集比例接口")
//    @PostMapping("/getNullScale")
//    public Result getNullScale(@RequestBody JSONObject json) {
//        List<SimHashInfoPO> simHashInfoPOList = simHashInfoDao.findBillNull();
//        for (int i =0;i<simHashInfoPOList.size();i++){
//            SimHashInfoPO simHashInfoPO = simHashInfoPOList.get(i);
//            String scale = simHashInfoPO.getScale();
//            String[] scaleArray = scale.split("_");
//            String hashCode2 = scaleArray[1];
//            // System.out.println("mobile = " + simHashInfoPO.getMobile() + ", hashCode2 = " + hashCode2 );
//            List<SimHashInfoPO> simHashInfoList = simHashInfoDao.findSimHashInfoPOByHash_code(hashCode2);
//            if(simHashInfoList.size() > 1){
//                System.out.println("==============================hashcode="+simHashInfoList.get(0).getHash_code());
//                continue;
//            }
//            SimHashInfoPO simHashInfo2 = simHashInfoList.get(0);
//            List<String> phones1 = zPhonesDao.findPhones(simHashInfoPO.getMobile());
//            List<String> phones2 = zPhonesDao.findPhones(simHashInfo2.getMobile());
//            List<String> accountIdList = phones1.stream().filter(phones2::contains).collect(Collectors.toList());
//            DecimalFormat df = new DecimalFormat("0.00000000");
//            String scaleNew = df.format((double) accountIdList.size()/phones1.size());
//
//            simHashInfoDao.updateBiLi(scaleNew,simHashInfoPO.getMobile());
//
//            System.out.println("mobile = " + simHashInfoPO.getMobile()+ ", hashCode1 = " + scaleArray[0]  + ", hashCode2 = " + hashCode2 + ", scaleNew = " + scaleNew);
//        }
//        return Result.builder().data("").build();
//    }



    @LogDeals(value = "获取相似度交集比例接口")
    @PostMapping("/ces")
    public Result getScale1(@RequestBody JSONObject json) {

        List<String> phones = zPhonesDao.findPhones(json.getString("phone1"));
        List<String> phones2 = zPhonesDao.findPhones(json.getString("phone2"));

        String s1 = "'";
        for (String s : phones) {
            s1 += s + "'";
        }

        String s2 = "'";
        for (String s : phones2) {
            s2 += s + "'";
        }


        SimHashUtil mySimHash_1 = new SimHashUtil(s1.replaceAll(" ",""), 64);
        SimHashUtil mySimHash_2 = new SimHashUtil(s2.replaceAll(" ",""), 64);

        //Double similar = mySimHash_1.getSimilar(mySimHash_2);

        int similar = mySimHash_1.getHammingDistance(mySimHash_2);
        Double similar1 = mySimHash_1.getSimilar(mySimHash_2);



        List<String> accountIdList1 = phones.stream().filter(phones2::contains).collect(Collectors.toList());
        System.out.println(accountIdList1.toString());
        log.info("hash={},similar={}",similar,similar1);
        return Result.builder().data("").build();
    }



    public  static  Object getMinKey(Map<String, Integer> map) {
        if  (map ==  null )  return  null ;
        Set<String> set = map.keySet();
        Object[] obj = set.toArray();
        Arrays.sort(obj);
        return  obj[ 0 ];
    }

    public  static  Object getMinValue(Map<String, Integer> map) {
        if  (map ==  null )  return  null ;
        Collection<Integer> c = map.values();
        Object[] obj = c.toArray();
        Arrays.sort(obj);
        return  obj[ 0 ];
    }


    @Resource
    private SimHashInfoCopy1Dao simHashInfoCopy1Dao;

    @LogDeals(value = "计算Hashcode值接口New")
    @PostMapping("/getHashcodeNew")
    public Result getHashcodeNew(@RequestBody JSONObject json) {
        List<String> userPhones = zPhonesDao.groupByUserPhone();
        for (String userPhone : userPhones) {
            List<String> zPhonesDaoPhones = zPhonesDao.findPhones(userPhone);
            zPhonesDaoPhones = zPhonesDaoPhones.stream().filter(x-> x != null && x.length() >= 9).distinct().collect(Collectors.toList());
            StringBuilder s = new StringBuilder("'");
            for (String phone : zPhonesDaoPhones) {
                s.append(phone).append("'");
            }
            SimHashUtil mySimHash_1 = new SimHashUtil(s.toString(), 64);
            SimHashInfoCopy1PO simHashInfoPO = new SimHashInfoCopy1PO();
            simHashInfoPO.setMobile(userPhone);
            simHashInfoPO.setHash_code( mySimHash_1.simHash().toString() );
            simHashInfoPO.setCounty("thai");
            simHashInfoPO.setBad_sign(1);
            simHashInfoPO.setCreate_time(new Date());
            simHashInfoCopy1Dao.save(simHashInfoPO);
            System.out.println("userPhone = " + userPhone + ", Hash_code = " + mySimHash_1.simHash().toString());
        }
        return Result.builder().data("").build();
    }

}

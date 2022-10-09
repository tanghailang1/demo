package com.example.demo;
import com.example.demo.dao.sass.SimHashInfoDao;
import com.example.demo.dao.sass.ZPhonesDao;
import com.example.demo.dto.SmsInfo;

import javax.annotation.Resource;
import javax.persistence.criteria.CriteriaBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.nio.CharBuffer;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class SimHash {


    private  String tokens;
    private BigInteger strSimHash;
    private  int hashbits =  64;
    public SimHash( String tokens)
    {
        this.tokens = tokens;
        this.strSimHash =  this.simHash();
    }
    public SimHash( String tokens,  int hashbits)
    {
        this.tokens = tokens;
        this.hashbits = hashbits;
        this.strSimHash =  this.simHash();
    }
    public BigInteger simHash()
    {
        int[] v =  new  int[ this.hashbits];
        StringTokenizer stringTokens =  new StringTokenizer( this.tokens);
        while (stringTokens.hasMoreTokens())
        {
            String temp = stringTokens.nextToken();
            BigInteger t =  this.hash(temp);
            for ( int i =  0; i <  this.hashbits; i++)
            {
                BigInteger bitmask =  new BigInteger( "1").shiftLeft(i);
                if (t.and(bitmask).signum() !=  0)
                {
                    v[i] +=  1;
                }
                else
                {
                    v[i] -=  1;
                }
            }
        }
        BigInteger fingerprint =  new BigInteger( "0");
        for ( int i =  0; i <  this.hashbits; i++)
        {
            if (v[i] >=  0)
            {
                fingerprint = fingerprint.add( new BigInteger( "1").shiftLeft(i));
            }
        }
        return fingerprint;
    }
    private BigInteger hash( String source)
    {
        if (source == null || source.length() ==  0)
        {
            return  new BigInteger( "0");
        }
        else
        {
            char[] sourceArray = source.toCharArray();
            BigInteger x = BigInteger.valueOf((( long) sourceArray[ 0]) <<  7);
            BigInteger m =  new BigInteger( "1000003");
            BigInteger mask =  new BigInteger( "2").pow( this.hashbits).subtract(
                    new BigInteger( "1"));
            for ( char item : sourceArray)
            {
                BigInteger temp = BigInteger.valueOf(( long) item);
                x = x.multiply(m).xor(temp).and(mask);
            }
            x = x.xor( new BigInteger( String.valueOf(source.length())));
            if (x.equals( new BigInteger( "-1")))
            {
                x =  new BigInteger( "-2");
            }
            return x;
        }
    }
    public  int hammingDistance(SimHash other)
    {
        BigInteger m =  new BigInteger( "1").shiftLeft( this.hashbits).subtract(
                new BigInteger( "1"));
        BigInteger x =  this.strSimHash.xor(other.strSimHash).and(m);
        int tot =  0;
        while (x.signum() !=  0)
        {
            tot +=  1;
            x = x.and(x.subtract( new BigInteger( "1")));
        }
        return tot;
    }

    public  static int hammingDistanceNew(BigInteger my,BigInteger other)
    {
        BigInteger m =  new BigInteger( "1").shiftLeft( 128).subtract(
                new BigInteger( "1"));
        BigInteger x =  my.xor(other).and(m);
        int tot =  0;
        while (x.signum() !=  0)
        {
            tot +=  1;
            x = x.and(x.subtract( new BigInteger( "1")));
        }
        return tot;
    }



    @Resource
    private static ZPhonesDao zPhonesDao;

    @Resource
    private SimHashInfoDao simHashInfoDao;


    public  static  void main( String[] args) throws ParseException {

        //System.out.println("{\"code\":\"200\",\"data\":null,\"message\":\"ok\",\"trace\":null}".length());

        String s =  "18258866591,15805687001,15805687992,18258866591,15805687001,15805687992,18258866591,15805687001,15805687992,18258866591,15805687001,15805687992,18258866591,15805687001,15805687992,18258866591,15805687001,15805687992,18258866591,15805687001,15805687992,18258866591,15805687001,15805687992,18258866591,15805687001,15805687992,18258866591,15805687001,15805687992,18258866591,15805687001,15805687992";
        SimHash hash1 =  new SimHash(s,  64);
       // System.out.println(hash1.strSimHash +  "  " + hash1.strSimHash.bitLength());
        s =  "18258866591,15805687001,15805687874,18258863871,15805687001,15805687992,18258866591,15805687001,15805687992,18258866591,15805687001,15805687992,18258866591,15805687001,15805687992,18258866591,15805687001,15805687992,18258866591,15805687001,15805687992,18258866591,15805687001,15805687992,18258866591,15805687001,15805687992,18258866591,15805687001,15805687992,18258866591,15805687001,15805687992";
        SimHash hash2 =  new SimHash(s,  64);
//        System.out.println(hash2.strSimHash +  "  " + hash2.strSimHash.bitCount());
//        System.out.println( "============================");
//        System.out.println(hash1.hammingDistance(hash2));

        List<Integer> list = Arrays.asList(1, 2, 3, 5);
        List<Integer> list2 = Arrays.asList(1, 4,5);
//
//        List<Integer> accountIdList = list.stream().filter(list2::contains).collect(Collectors.toList());
//        System.out.println(accountIdList.toString());
//
//        int  a=10;
//        int  b=3;
//        DecimalFormat df = new DecimalFormat("0.00000");
//        String maerialRatio = df.format((float)a/b);
//        System.out.println(maerialRatio.toString());


//        SmsInfo smsInfo = new SmsInfo("TX-RUFILO",101);
//        SmsInfo smsInfo2 = new SmsInfo("918128152415",102);
//        SmsInfo smsInfo3 = new SmsInfo(null,103);
//        SmsInfo smsInfo4 = new SmsInfo("BX-CBSSBI",null);
//        SmsInfo smsInfo5 = new SmsInfo("918128152415",105);
//        List<SmsInfo> smsInfos =  new ArrayList<>();
//        smsInfos.add(smsInfo);
//        smsInfos.add(smsInfo2);
//        smsInfos.add(smsInfo3);
//        smsInfos.add(smsInfo4);
//        smsInfos.add(smsInfo5);
//        smsInfos.add(null);
//        smsInfos = smsInfos.stream().filter(Objects::nonNull).filter(i->i.getAddress()!=null).filter(i->i.getTime()!=null).collect(Collectors.toList());
//
//        List<Integer> applyAfterMedList = Arrays.asList(121351, 2365090, 4510022, 4510022, 35673861, 35673861, 420991375, 420991375, 420991375, 420991375, 420991375, 420991375, 420991375, 420991375);
//        double applyAfterMedian = 0;
//        applyAfterMedList = applyAfterMedList.stream().sorted().collect(Collectors.toList());
//        if (applyAfterMedList.size() % 2 == 0) {
//            applyAfterMedian = (double) (applyAfterMedList.get(applyAfterMedList.size() / 2 - 1) + applyAfterMedList.get(applyAfterMedList.size() / 2)) / 2;
//        } else {
//            applyAfterMedian = applyAfterMedList.get(applyAfterMedList.size() / 2);
//        }
//
//        BigDecimal bigDecimal = new BigDecimal(111);
//        System.out.println(BigDecimal.valueOf(applyAfterMedian));
//        Comparator<SmsInfo> byName = Comparator.comparing(SmsInfo::getAddress);
//        Comparator<SmsInfo> bySizeDesc = Comparator.comparing(SmsInfo::getTime);
//
//        smsInfos.sort(byName.thenComparing(bySizeDesc)); // 先以adress升序排列，再按照create_time升叙排列
//        System.out.println(smsInfos.toString());
//
//        String arr [] = new String [5];
//        arr[0] = "TX-RUFILO";
//        arr[1] = "918128152415";
//        arr[2] = "VM-HDFCBK";
//        arr[3] = "BX-CBSSBI";
//        arr[4] = "918128152415";

//        Arrays.sort(arr);
//        System.out.println(Arrays.toString(arr));
//
////        int a1 = 10/8;
////        System.out.println(a1);
//
//        Arrays.asList(1L,2L,3L,4L,5L,9L,7L);
//        List<Long> farray = farray(Arrays.asList(1L, 2L, 3L, 4L, 5L, 9L, 7L));
//        System.out.println(farray);
//        List<Long> createMorMinList =  new ArrayList<>();
//        long l = createMorMinList.stream().mapToLong(Long::longValue).sum();
//        System.out.println(l);

//        String  mobile = "132312";
//        String substring = mobile.substring(mobile.length() - 1);
//        System.out.println(Integer.valueOf(substring));


//        int i = countStr("2022:05:03 20:52:07", ":");
//        System.out.println(i);
//        String s2 = "2022:05:03 20:52:07";
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String[] dateArray = s2.split(" ");
//        dateArray[0] = dateArray[0].replaceAll(":","-");
//        Date date = sdf.parse(dateArray[0]+" "+ dateArray[1]);
//        System.out.println(date.getTime());


        SmsInfo smsInfo = new SmsInfo("121",1643006650);
        SmsInfo smsInfo2 = new SmsInfo("121",1645588512);
        SmsInfo smsInfo3 = new SmsInfo("121",1645764513);

        SmsInfo smsInfo4 = new SmsInfo("121",1647874471);
        SmsInfo smsInfo5 = new SmsInfo("121",1651498617);
        List<SmsInfo> smsInfos =  new ArrayList<>();
        smsInfos.add(smsInfo4);
        smsInfos.add(smsInfo5);
        smsInfos.add(smsInfo);
        smsInfos.add(smsInfo2);
        smsInfos.add(smsInfo3);


        Comparator<SmsInfo> byName = Comparator.comparing(SmsInfo::getAddress);
        Comparator<SmsInfo> bySizeDesc = Comparator.comparing(SmsInfo::getTime);
        smsInfos.sort(byName.thenComparing(bySizeDesc)); // 先以adress升序排列，再按照create_time升叙排列
        System.out.println(smsInfos.toString());

        List<Integer> list1 = Arrays.asList(1651725207, 1651712986, 1651725031, 1651713686, 1651377412, 1651627313, 1651796678, 1651719356, 1651287573, 1651796849);
        List<Integer> list3 = farray1(list1);
        System.out.println(list3.size());
        System.out.println(list3);


        //        Date date = new Date();
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date parse = simpleDateFormat.parse("2022-05-02 10:00:00");
//        Integer weekOfDate = getWeekOfDate(parse);
//        System.out.println(weekOfDate);
    }

    public static Integer getWeekOfDate(Date date) {
        Integer[] weekDays = { 7, 1, 2, 3, 4, 5, 6 };
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekDays[w];
    }

    public static int countStr(String longStr, String mixStr) {
        //如果确定传入的字符串不为空，可以把下面这个判断去掉，提高执行效率
//        if(longStr == null || mixStr == null || "".equals(longStr.trim()) || "".equals(mixStr.trim()) ){
//             return 0;
//        }
        int count = 0;
        int index = 0;
        while((index = longStr.indexOf(mixStr,index))!= -1){
            index = index + mixStr.length();
            count++;
        }
        return count;
    }

    public static List<Long> farray(List<Long> a){
        List<Long> list = new ArrayList<>();
        for(int i = 0;i < a.size()-1;i++){
            list.add(a.get(i+1)-a.get(i));
            System.out.println("第"+i+"个元素与第"+(i+1)+"个元素的差为"+(a.get(i)-a.get(i+1)));
        }

        return list;

    }


    public static List<Integer> farray1(List<Integer> a){
        List<Integer> list = new ArrayList<>();
        for(int i = 0;i < a.size()-1;i++){
            list.add(a.get(i+1)-a.get(i));
        }
        return list;

    }
}
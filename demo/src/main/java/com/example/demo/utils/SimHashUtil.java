package com.example.demo.utils;

import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import org.apache.commons.lang3.StringUtils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.*;

/**
 * System:     BBS论坛系统
 * Department:  研发一组
 * Title:       [aiyou-bbs — SimHashUtil 模块]
 * Description: [SimHash 标题内容相似度算法工具类]
 * Created on:  2020-04-01
 * Contacts:    [who.seek.me@java98k.vip]
 *
 * @author huazai
 * @version V1.1.0
 */
public class SimHashUtil {

    /**
     * 标题名称
     */
    private String topicName;
    /**
     * 分词向量
     */
    private BigInteger bigSimHash;
    /**
     * 初始桶大小
     */
    private Integer hashCount = 64;

    private List<String> list = Arrays.asList("\\*","-","#"," ","\\+",",","@","\\.",":","_",";");
    /**
     * 分词最小长度限制
     */
    private static final Integer WORD_MIN_LENGTH = 3;

    private static final BigInteger ILLEGAL_X = new BigInteger("-1");

    public SimHashUtil(String topicName, Integer hashCount) {

        this.topicName = topicName;
        this.bigSimHash = this.simHash();
        this.hashCount = hashCount;
    }

    /**
     * Description:[分词计算向量]
     *
     * @return BigInteger
     * @date 2020-04-01
     * @author huazai
     */
    public BigInteger simHash() {

        // 清除特殊字符

        for (String s : list){
            this.topicName = this.topicName.replaceAll(s,"");
        }
        int[] hashArray = new int[this.hashCount];

        // 对内容进行分词处理
        List<Term> terms = StandardTokenizer.segment(this.topicName);

        // 配置词性权重
        Map<String, Integer> weightMap = new HashMap<>(16, 0.75F);
        weightMap.put("nx", 0);
        weightMap.put("m", 100);

        // 设置停用词
        Map<String, String> stopMap = new HashMap<>(16, 0.75F);
        stopMap.put("w", "");
        // 设置超频词上线
        Integer overCount = 1000;

        // 设置分词统计量
        Map<String, Integer> wordMap = new HashMap<>(16, 0.75F);

        for (Term term : terms) {
            // 获取分词字符串
            String word = term.word;
            // 获取分词词性
            String nature = term.nature.toString();

         //  System.out.println("word="+word+" nature="+nature);


            // 过滤超频词
            if (wordMap.containsKey(word)) {

                Integer count = wordMap.get(word);
                if (count > overCount) {
                    continue;
                } else {
                    wordMap.put(word, count + 1);
                }
            } else {
                wordMap.put(word, 1);
            }

            // 过滤停用词
            if (stopMap.containsKey(nature)) {
                continue;
            }

            // 计算单个分词的Hash值
            BigInteger wordHash = this.getWordHash(word);

            for (int i = 0; i < this.hashCount; i++) {

                // 向量位移
                BigInteger bitMask = new BigInteger("1").shiftLeft(i);

                // 对每个分词hash后的列进行判断，例如：1000...1，则数组的第一位和末尾一位加1,中间的62位减一，也就是，逢1加1，逢0减1，一直到把所有的分词hash列全部判断完

                // 设置初始权重
                Integer weight = 1;
                if (weightMap.containsKey(nature)) {

                    weight = weightMap.get(nature);
                }
                // 计算所有分词的向量
                if (wordHash.and(bitMask).signum() != 0) {
                    hashArray[i] += weight;
                } else {
                    hashArray[i] -= weight;
                }

            }
        }

        // 生成指纹
        BigInteger fingerPrint = new BigInteger("0");
        for (int i = 0; i < this.hashCount; i++) {

            if (hashArray[i] >= 0) {
                fingerPrint = fingerPrint.add(new BigInteger("1").shiftLeft(i));
            }
        }

        return fingerPrint;
    }

    /**
     * Description:[计算单个分词的hash值]
     *
     * @return BigInteger
     * @date 2020-04-01
     * @author huazai
     */
    public BigInteger getWordHash(String word) {

        if (StringUtils.isEmpty(word)) {

            // 如果分词为null，则默认hash为0
            return new BigInteger("0");
        } else {

            // 分词补位，如果过短会导致Hash算法失败
            while (word.length() < SimHashUtil.WORD_MIN_LENGTH) {
                word = word + word.charAt(0);
            }
            // 分词位运算
            char[] wordArray = word.toCharArray();
            BigInteger x = BigInteger.valueOf(wordArray[0] << 7);
            BigInteger m = new BigInteger("1000003");

            // 初始桶pow运算
            BigInteger mask = new BigInteger("2").pow(this.hashCount).subtract(new BigInteger("1"));

            for (char item : wordArray) {
                BigInteger temp = BigInteger.valueOf(item);
                x = x.multiply(m).xor(temp).and(mask);
            }

            x = x.xor(new BigInteger(String.valueOf(word.length())));

            if (x.equals(ILLEGAL_X)) {

                x = new BigInteger("-2");
            }

            return x;
        }
    }



    /**
     * Description:[过滤特殊字符]
     *
     * @return BigInteger
     * @date 2020-04-01
     * @author huazai
     */
    private String clearSpecialCharacters(String topicName) {

        // 将内容转换为小写
        topicName = StringUtils.lowerCase(topicName);

        // 过来HTML标签
       // topicName = Jsoup.clean(topicName, Whitelist.none());

        // 过滤特殊字符
        String[] strings = {" ", "\n", "\r", "\t", "\\r", "\\n", "\\t", "&nbsp;", "&amp;", "&lt;", "&gt;", "&quot;", "&qpos;"};
        for (String string : strings) {
            topicName = topicName.replaceAll(string, "");
        }

        return topicName;
    }

    /**
     * Description:[获取标题内容的相似度]
     *
     * @return Double
     * @date 2020-04-01
     * @author huazai
     */
    public Double getSimilar(SimHashUtil simHashUtil) {

        // 获取海明距离
        Double hammingDistance = (double) this.getHammingDistance(simHashUtil);

        // 求得海明距离百分比
        Double scale = (1 - hammingDistance / this.hashCount) * 100;

        Double formatScale = Double.parseDouble(String.format("%.2f", scale));

        return formatScale;
    }

    /**
     * Description:[获取标题内容的海明距离]
     *
     * @return Double
     * @date 2020-04-01
     * @author huazai
     */
    public int getHammingDistance(SimHashUtil simHashUtil) {

        // 求差集
        BigInteger subtract = new BigInteger("1").shiftLeft(this.hashCount).subtract(new BigInteger("1"));

        // 求异或
        BigInteger xor = this.bigSimHash.xor(simHashUtil.bigSimHash).and(subtract);

        int total = 0;
        while (xor.signum() != 0) {
            total += 1;
            xor = xor.and(xor.subtract(new BigInteger("1")));
        }

        return total;
    }


    public static int getHammingDistanceNew(BigInteger my,BigInteger other) {

        // 求差集
        BigInteger subtract = new BigInteger("1").shiftLeft(64).subtract(new BigInteger("1"));

        // 求异或
        BigInteger xor = my.xor(other).and(subtract);

        int total = 0;
        while (xor.signum() != 0) {
            total += 1;
            xor = xor.and(xor.subtract(new BigInteger("1")));
        }

        return total;
    }




    public static void main(String[] args) throws ParseException, ScriptException {
//        // 准备测试标题内容数据
//        List<String> titleList = new ArrayList<>();
//        titleList.add("有哪些养猫必须知道的冷知识");
//        titleList.add("有哪些养猫必须知道的冷");
//        titleList.add("有哪些养猫必须知道");
//        titleList.add("有哪些养猫");
//        titleList.add("有哪些");
//
//        // 原始标题内容数据
//        String originalTitle = "有哪些养猫必须知道的冷知识？";
//
//        Map<String, Integer> simHashMap = new HashMap<>(16, 0.75F);
//
//        System.out.println("======================================");
//        long startTime = System.currentTimeMillis();
//        System.out.println("原始标题：" + originalTitle);
//
//        // 计算相似度
//        titleList.forEach(title -> {
//            SimHashUtil mySimHash_1 = new SimHashUtil(title, 64);
//            SimHashUtil mySimHash_2 = new SimHashUtil(originalTitle, 64);
//
//            //Double similar = mySimHash_1.getSimilar(mySimHash_2);
//
//            int similar = mySimHash_1.getHammingDistance(mySimHash_2);
//
//            simHashMap.put(title, similar);
//        });
//
//        // 按相标题内容排序输出控制台
//        Set<String> titleSet = simHashMap.keySet();
//        Object[] titleArrays = titleSet.toArray();
//        Arrays.sort(titleArrays, Collections.reverseOrder());
//
//        System.out.println("-------------------------------------");
//        for (Object title : titleArrays) {
//            System.out.println("标题：" + title + "-----------相似度：" + simHashMap.get(title));
//        }
//
//        // 求得运算时长（单位：毫秒）
//        long endTime = System.currentTimeMillis();
//        long totalTime = endTime - startTime;
//        System.out.println("\n本次运算总耗时" + totalTime + "毫秒");
//
//        System.out.println("======================================");

//        List<String> list = Arrays.asList("\\*","-","#"," ","\\+",",","@","\\.",":","_",";");
//        String aa = "*aaa- # + , @.:;_aaaaa";
//        System.out.println(aa);
//        for (String s : list){
//            aa = aa.replaceAll(s,"");
//            System.out.println("------>"+aa);
//        }
//        System.out.println(aa);
//        SimHashUtil mySimHash_1 = new SimHashUtil("09090，111", 64);
//        SimHashUtil mySimHash_2 = new SimHashUtil("09090，123", 64);
//
//        //Double similar = mySimHash_1.getSimilar(mySimHash_2);
//
//        int similar = mySimHash_1.getHammingDistance(mySimHash_2);
//
//        String s = "+1111,";
//        String s1 = s.replaceAll(",", "");
//        System.out.println(s1);
//
//        System.out.println(similar);

//        long s1 = 1652691825000L;
//        long s2 = 1652594625000L;
//
//        long between_days=(s1-s2)/(1000*3600*24);
//
//        int ceil = (int)Math.ceil((double) (s1 - s2) / (1000 * 3600 * 24));
//        System.out.println(between_days);
//        System.out.println(ceil);



//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        SimpleDateFormat formatTimes = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 7);
//        String timeDay = format.format(calendar.getTime());
//        long startTime = formatTimes.parse(timeDay + " 00:00:00").getTime();
//        System.out.println(startTime);
//        System.out.println(new Date(startTime));


//        ScriptEngineManager manager = new ScriptEngineManager();
//        ScriptEngine engine = manager.getEngineByName("js");
//        engine.put("apply_type",1);
//        engine.put("companyId","60990c41fd6378020537e4");
//        Boolean flag = (Boolean) engine.eval("apply_type!=2 && companyId !='60990c41fd6378020537ef94' && companyId !='60dd99d3629621b2795e2fa1' && companyId !='619db3196d055dcde85ffaff' && companyId !='619dd9cfca9f51cea505ecb7'");
//        System.out.println(flag);

//        BigInteger a = new BigInteger( "123");
//        int i = a.intValue();
//        System.out.println(i);

//        LocalDate localDate = LocalDate.now().minusDays(1);
//        Date startDate = Date.from(LocalDateTime.of(localDate, LocalTime.MIN).atZone(ZoneId.systemDefault()).toInstant());
//        Date endDate = Date.from(LocalDateTime.of(localDate, LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
//
//        System.out.println(startDate);
//        System.out.println(endDate);
//
//        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
//        SimpleDateFormat formatTimes = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 6);
//        // timeDay的格式形如：2020-12-21
//        String timeDay = format.format(calendar.getTime());
//        long startTime = formatTimes.parse(timeDay + " 00:00:00").getTime();
//
//        System.out.println(new Date(startTime));




        Date date = Date.from(LocalDateTime.of(LocalDate.now().minusDays(0), LocalTime.MIN).atZone(ZoneId.systemDefault()).toInstant());

        System.out.println(date);

    }


}

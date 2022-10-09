//package com.example.demo.config.es;
//
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.pool2.impl.GenericObjectPool;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * TODO 还需要完善，加个定时任务检查里面的线程是否健康，或者每次取出检查一下。
// * TODO 如果归还异常，需要有办法再次归还。
// */
//@Component
//@Slf4j
//public class ESClientPool {
//
//    @Autowired
//    private GenericObjectPool<RestHighLevelClient> genericObjectPool;
//
//    /**
//     * 获得对象
//     *
//     * @return
//     * @throws Exception
//     */
//    public RestHighLevelClient getClient() throws Exception {
//        // 从池中取一个对象
//        return genericObjectPool.borrowObject();
//    }
//
//    /**
//     * 归还对象
//     *
//     * @param client
//     */
////    public void returnClient(RestHighLevelClient client) {
////        // 使用完毕之后，归还对象
////        genericObjectPool.returnObject(client);
////    }
//
////    public Map info(){
////        Map map = new HashMap<>();
////        long borrowedCount = genericObjectPool.getBorrowedCount();
//////        log.info("borrowedCount={}", borrowedCount);
////        long createdCount = genericObjectPool.getCreatedCount();
//////        log.info("createdCount={}", createdCount);
////        int numActive = genericObjectPool.getNumActive();
//////        log.info("numActive={}", numActive);
////        int numIdle = genericObjectPool.getNumIdle();
//////        log.info("numIdle="+numIdle);
////
////        map.put("borrowedCount", borrowedCount);
////        map.put("createdCount", createdCount);
////        map.put("numActive", numActive);
////        map.put("numIdle", numIdle);
////        return map;
////    }
//}

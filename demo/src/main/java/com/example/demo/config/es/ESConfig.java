//package com.example.demo.config.es;
//
//import com.example.demo.utils.HttpUtils;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.pool2.BasePooledObjectFactory;
//import org.apache.commons.pool2.PooledObject;
//import org.apache.commons.pool2.PooledObjectFactory;
//import org.apache.commons.pool2.impl.DefaultPooledObject;
//import org.apache.commons.pool2.impl.GenericObjectPool;
//import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
//import org.apache.http.HttpHost;
//import org.apache.http.auth.AuthScope;
//import org.apache.http.auth.UsernamePasswordCredentials;
//import org.apache.http.client.CredentialsProvider;
//import org.apache.http.impl.client.BasicCredentialsProvider;
//import org.elasticsearch.client.RestClient;
//import org.elasticsearch.client.RestHighLevelClient;
//import org.springframework.beans.factory.InitializingBean;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.util.StringUtils;
//
///**
// * restHighLevelClient线程池配置
// */
//@Configuration
//@Slf4j
//public class ESConfig implements InitializingBean {
//    @Value("${es.config.cluster-nodes:}")
//    private String esClusterNodes = "localhost:9200";
//    @Value("${es.config.username:}")
//    private String esUserName;
//    @Value("${es.config.password:}")
//    private String esPassword;
//    @Value("${es.config.connect-timeout:5000}")
//    private int esConnectTimeout;
//    @Value("${es.config.connection-request-timeout:30000}")
//    private int esConnectionRequestTimeout;
//    @Value("${es.config.socket-timeout:30000}")
//    private int esSocketTimeout;
//    @Value("${es.pool.max-total:8}")
//    private int maxTotal;
//    @Value("${es.pool.max-idle:8}")
//    private int maxIdle;
//    @Value("${es.pool.min-idle:1}")
//    private int minIdle ;
//
//    public static String ES_CLUSTER_NODES = "";
//    public static String ES_USERNAME = "";
//    public static String ES_PASSWORD = "";
//    public static int ES_CONNECTION_REQUEST_TIMEOUT = -1;
//    public static int ES_CONNECT_TIMEOUT = -1;
//    public static int ES_SOCKET_TIMEOUT = -1;
//    public static int MAX_TOTAL = 8;
//    public static int MAX_IDLE = 8;
//    public static int MIN_IDLE = 1;
//
//    @Override
//    public void afterPropertiesSet() {
//        ES_CLUSTER_NODES = esClusterNodes;
//        ES_USERNAME = esUserName;
//        ES_PASSWORD = esPassword;
//        ES_CONNECTION_REQUEST_TIMEOUT = esConnectionRequestTimeout;
//        ES_CONNECT_TIMEOUT = esConnectTimeout;
//        ES_SOCKET_TIMEOUT = esSocketTimeout;
//
//        MAX_TOTAL = maxTotal;
//        MAX_IDLE = maxIdle;
//        MIN_IDLE = minIdle;
//    }
//
//    @Bean
//    public GenericObjectPoolConfig config() {
//        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
//        poolConfig.setMinIdle(ESConfig.MIN_IDLE);
//        poolConfig.setMaxTotal(ESConfig.MAX_TOTAL);
//        poolConfig.setMaxIdle(ESConfig.MAX_IDLE);
//        poolConfig.setJmxEnabled(false);
//        return poolConfig;
//    }
//
//    @Bean
//    public GenericObjectPool<RestHighLevelClient> pool(
//            PooledObjectFactory<RestHighLevelClient> factory,
//            GenericObjectPoolConfig config) {
//        return new GenericObjectPool<>(factory, config);
//    }
//
//    @Bean
//    public PooledObjectFactory<RestHighLevelClient> factory() {
//        return new BasePooledObjectFactory<RestHighLevelClient>() {
//
//            @Override
//            public void destroyObject(PooledObject<RestHighLevelClient> pooledObject) throws Exception {
//                RestHighLevelClient highLevelClient = pooledObject.getObject();
//                highLevelClient.close();
//            }
//
//            @Override
//            public RestHighLevelClient create() throws Exception {
//                log.info("线程池新增RestHighLevelClient实例");
//                HttpHost[] nodes = HttpUtils.makeHttpHost(ESConfig.ES_CLUSTER_NODES);
//                final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
//                if(!StringUtils.isEmpty(ES_USERNAME) && !StringUtils.isEmpty(ES_PASSWORD)){
//                    credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(ESConfig.ES_USERNAME, ESConfig.ES_PASSWORD));
//                }
//
//                return new RestHighLevelClient(RestClient.builder(nodes)
//                        .setRequestConfigCallback(
//                                requestConfigBuilder -> {
//                                    requestConfigBuilder.setConnectTimeout(ESConfig.ES_CONNECT_TIMEOUT);
//                                    requestConfigBuilder.setSocketTimeout(ESConfig.ES_SOCKET_TIMEOUT);
//                                    requestConfigBuilder.setConnectionRequestTimeout(ESConfig.ES_CONNECTION_REQUEST_TIMEOUT);
//                                    return requestConfigBuilder;
//                                }
//                        ).setHttpClientConfigCallback(
//                                httpAsyncClientBuilder -> httpAsyncClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
//                        )
//                );
//            }
//
//            @Override
//            public PooledObject<RestHighLevelClient> wrap(RestHighLevelClient restHighLevelClient) {
//                return new DefaultPooledObject<>(restHighLevelClient);
//            }
//        };
//    }
//}

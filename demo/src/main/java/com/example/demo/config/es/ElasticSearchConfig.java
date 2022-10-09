package com.example.demo.config.es;

import com.example.demo.utils.HttpUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * created by DengJin on 2020/10/10 16:17
 * restHighLevelClient单例配置
 * TODO 待注释
 */
@Configuration
//@ConfigurationProperties(prefix = "spring.data.elasticsearch")
@Slf4j
@Deprecated
public class ElasticSearchConfig {
    //权限验证
    final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

    //@Value("${es.config.cluster-nodes:}")
    private String address = "147.139.32.141:9200";
    @Value("${es.config.username:}")
    private String user;
    @Value("${es.config.password:}")
    private String password;


    @Bean
    public RestClientBuilder restClientBuilder() {
        HttpHost[] hosts = HttpUtils.makeHttpHost(address);
        //配置权限验证
        if(!StringUtils.isEmpty(user) && !StringUtils.isEmpty(password)){
            credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(user, password));
        }
        RestClientBuilder restClientBuilder = RestClient.builder(hosts).setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
            @Override
            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
            }
        });
        return restClientBuilder;
    }


    @Bean(name = "highLevelClient")
    public RestHighLevelClient highLevelClient(@Autowired RestClientBuilder restClientBuilder) {
//        restClientBuilder.setMaxRetryTimeoutMillis(60000);
        return new RestHighLevelClient(restClientBuilder);
    }
}

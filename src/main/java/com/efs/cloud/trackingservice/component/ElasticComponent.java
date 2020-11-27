package com.efs.cloud.trackingservice.component;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * ElasticComponent
 *
 * @author maxun
 */
@Component
@Slf4j
public class ElasticComponent {

    @Value("${elasticsearch.serverUrl}")
    private String elasticServerUrl;

    @Value("${elasticsearch.username}")
    private String elasticUsername;

    @Value("${elasticsearch.passwd}")
    private String elasticPasswd;

    /**
     * 定义请求头
     */
    private static HttpHeaders REQUEST_HEADERS = new HttpHeaders();

    @PostConstruct
    public void init() {
        String authString = this.getAuthString(this.elasticUsername, this.elasticPasswd);
        REQUEST_HEADERS.add("Authorization", authString);
        REQUEST_HEADERS.add(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
    }

    /**
     * 获取授权字符串
     *
     * @param username
     * @param passwd
     * @return
     */
    public String getAuthString(String username, String passwd) {
        String authString = "Basic" + " "
                + Base64.encodeBase64String((username + ":" + passwd).getBytes());
        return authString;
    }

    /**
     * 检索文档
     *
     * @param index 索引名称
     * @param body  请求报文
     * @return
     */
    public SearchDocumentResponse searchDocument(String index, String body) {
        String requestUrl = elasticServerUrl + index + "/_search";
        HttpEntity<String> httpEntity = new HttpEntity<>(body, REQUEST_HEADERS);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<SearchDocumentResponse> responseResponseEntity
                = restTemplate.exchange(requestUrl, HttpMethod.POST, httpEntity, SearchDocumentResponse.class);
        return responseResponseEntity.getBody();
    }

    /**
     * 推送
     *
     * @param index
     * @param type
     * @param id
     * @param body
     * @return
     */
    public PushDocumentResponse pushDocument(String index, String type, String id, String body) {
        String requestUrl = elasticServerUrl + index + "/" + type + "/" + id;
        HttpEntity<String> httpEntity = new HttpEntity<>(body, REQUEST_HEADERS);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<PushDocumentResponse> responseResponseEntity
                = restTemplate.exchange(requestUrl, HttpMethod.POST, httpEntity, PushDocumentResponse.class);
        return responseResponseEntity.getBody();
    }

    /**
     * 检索文档的返回类
     */
    @Getter
    @Setter
    public static class SearchDocumentResponse {
        private int took;
        private boolean timed_out;
        private Shards _shards;
        private HitsX hits;

        @NoArgsConstructor
        @Data
        public static class Shards {

            private int total;
            private int successful;
            private int skipped;
            private int failed;
        }

        @NoArgsConstructor
        @Data
        public static class HitsX {

            private int total;
            private int max_score;
            private List<Hits> hits;

            @NoArgsConstructor
            @Data
            public static class Hits {

                private String _index;
                private String _type;
                private String _id;
                private int _score;
                private Object _source;
            }
        }
    }

    @NoArgsConstructor
    @Data
    public static class PushDocumentResponse {

        private String _index;
        private String _type;
        private String _id;
        private int _version;
        private String result;
        private ShardsBean _shards;
        private int _seq_no;
        private int _primary_term;

        @NoArgsConstructor
        @Data
        public static class ShardsBean {

            private int total;
            private int successful;
            private int failed;
        }
    }

}

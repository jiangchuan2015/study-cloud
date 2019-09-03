package chuan.study.cloud.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Jiang Chuan
 * @version 1.0.0
 * @since 2019-08-28
 */
@Slf4j
@Configuration
public class ElasticConfig {
    @Value("${spring.data.elasticsearch.cluster-name}")
    private String clusterName;

    @Value("#{'${spring.data.elasticsearch.cluster-nodes}'.split(',')}")
    private List<String> clusterNodes = new ArrayList<>();


    @Bean(name = "elasticSearchClient")
    public RestHighLevelClient elasticSearchClient() {
        return new RestHighLevelClient(RestClient.builder(new HttpHost("localhost", 9200, "http")));
    }
}

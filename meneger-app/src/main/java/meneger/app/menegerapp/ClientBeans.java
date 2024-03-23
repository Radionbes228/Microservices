package meneger.app.menegerapp;

import meneger.app.menegerapp.client.RestClientProductServiceClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ClientBeans {
    @Bean
    public RestClientProductServiceClient productServiceClient(@Value("${selmag.services.catalog.uri:http://localhost:8081}") String catalogBaseUri){
        return new RestClientProductServiceClient(RestClient.builder()
                .baseUrl(catalogBaseUri)
                .build());
    }
}

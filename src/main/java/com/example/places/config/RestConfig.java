package com.example.places.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.eclipse.jetty.client.HttpClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.client.JettyClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

@Configuration
@RequiredArgsConstructor
public class RestConfig {

    private final ObjectMapper objectMapper;


    private RestClient buildRestCLient(int connectionTimeout, int socketTimeout, String baseUrl) {

        HttpClient httpClient = new HttpClient();
        httpClient.setConnectTimeout(connectionTimeout);
        httpClient.setIdleTimeout(socketTimeout);

        JettyClientHttpRequestFactory factory = new JettyClientHttpRequestFactory(httpClient);
        FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();

        var converter = new MappingJackson2HttpMessageConverter(objectMapper);

        return RestClient.builder()
                .requestFactory(factory)
                .baseUrl(baseUrl)
                .messageConverters(converters -> {
                    converters.clear(); // remové todos los anteriores
                    converters.add(converter);
                    converters.add(formHttpMessageConverter);// agregá solo el que usa snake_case
                })
                .build();
    }

    @Bean
    public RestClient usersRestClient(Environment env) {

        return buildRestCLient(
                Integer.valueOf(env.getProperty("clients.users.connection-timeout")),
                Integer.valueOf(env.getProperty("clients.users.socket-timeout")),
                env.getProperty("clients.users.base-url")
        );
    }

}

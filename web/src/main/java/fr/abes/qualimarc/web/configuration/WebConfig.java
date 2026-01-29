package fr.abes.qualimarc.web.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Configuration
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {


    @Override
    public void  configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(yamlHttpConverter());
        converters.add(new StringHttpMessageConverter());
        converters.add(jsonHttpConverter());
    }

    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer
                .favorParameter(false)
                .ignoreAcceptHeader(false)
                .defaultContentType(MediaType.APPLICATION_JSON)
                .mediaType("json", MediaType.APPLICATION_JSON)
                // YAML: extensions yml/yaml
                .mediaType("yaml", MediaType.valueOf("application/x-yaml"))
                .mediaType("yaml", MediaType.valueOf("application/yaml"))
                .mediaType("yaml", MediaType.valueOf("text/yaml"))
                .mediaType("yml", MediaType.valueOf("application/x-yaml"))
                .mediaType("yml", MediaType.valueOf("text/yml"));
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        Jackson2ObjectMapperBuilder builder = new Jackson2ObjectMapperBuilder();
        ObjectMapper objectMapper = builder.build();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

    @Bean
    public MappingJackson2HttpMessageConverter jsonHttpConverter() {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setObjectMapper(objectMapper());
        return jsonConverter;
    }

    @Bean
    public MappingJackson2HttpMessageConverter yamlHttpConverter() {
        YAMLMapper mapper = new YAMLMapper();
        mapper.registerModule(new JavaTimeModule());
        MappingJackson2HttpMessageConverter yamlConverter =
                new MappingJackson2HttpMessageConverter(mapper);
        yamlConverter.setSupportedMediaTypes(List.of(
                MediaType.valueOf("application/x-yaml"),
                MediaType.valueOf("application/yaml"),
                MediaType.valueOf("text/yaml"),
                MediaType.valueOf("text/yml")
        ));
        yamlConverter.setDefaultCharset(StandardCharsets.UTF_8);
        return yamlConverter;
    }
}

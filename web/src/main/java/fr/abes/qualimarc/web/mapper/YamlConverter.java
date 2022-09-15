package fr.abes.qualimarc.web.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class YamlConverter extends AbstractJackson2HttpMessageConverter {

    protected YamlConverter() {
        super(new ObjectMapper(new YAMLFactory()),
                new MediaType("application", "yaml", StandardCharsets.UTF_8),
                new MediaType("text", "yaml", StandardCharsets.UTF_8),
                new MediaType("application", "*+yaml", StandardCharsets.UTF_8),
                new MediaType("text", "*+yaml", StandardCharsets.UTF_8),
                new MediaType("application", "yml", StandardCharsets.UTF_8),
                new MediaType("text", "yml", StandardCharsets.UTF_8),
                new MediaType("application", "*+yaml", StandardCharsets.UTF_8),
                new MediaType("text", "*+yaml", StandardCharsets.UTF_8));

    }

    @Override
    public void setObjectMapper(final ObjectMapper objectMapper) {
        if (!(objectMapper.getFactory() instanceof YAMLFactory)) {
            // Sanity check to make sure we do have an ObjectMapper configured
            // with YAML support, just in case someone attempts to call
            // this method elsewhere.
            throw new IllegalArgumentException(
                    "ObjectMapper must be configured with an instance of YAMLFactory");
        }
        super.setObjectMapper(objectMapper);
    }
}

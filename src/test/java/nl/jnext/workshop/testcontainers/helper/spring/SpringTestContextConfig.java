package nl.jnext.workshop.testcontainers.helper.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Option;
import com.jayway.jsonpath.spi.json.JacksonJsonProvider;
import com.jayway.jsonpath.spi.json.JsonProvider;
import com.jayway.jsonpath.spi.mapper.JacksonMappingProvider;
import com.jayway.jsonpath.spi.mapper.MappingProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.EnumSet;
import java.util.Set;

@TestConfiguration
public class SpringTestContextConfig {
    @Autowired
    private ObjectMapper mapper;

    /**
     * We want JsonPath to use the same Jackson instance as our production code,
     * i.e. the one that is injected by Spring. We wire that into the configuration of
     * the 'json-path' library.
     * see: https://github.com/json-path/JsonPath
     */
    @PostConstruct
    public void configureJsonPath() {
        com.jayway.jsonpath.Configuration.setDefaults(new SpringJacksonMappingConfigForJsonPath(mapper));
    }

    static final class SpringJacksonMappingConfigForJsonPath implements com.jayway.jsonpath.Configuration.Defaults {
        private final JsonProvider jsonProvider;
        private final MappingProvider mappingProvider;

        SpringJacksonMappingConfigForJsonPath(ObjectMapper mapper) {
            this.jsonProvider = new JacksonJsonProvider(mapper);
            this.mappingProvider = new JacksonMappingProvider(mapper);
        }

        @Override
        public JsonProvider jsonProvider() {
            return jsonProvider;
        }

        @Override
        public MappingProvider mappingProvider() {
            return mappingProvider;
        }

        @Override
        public Set<Option> options() {
            return EnumSet.noneOf(Option.class);
        }
    }
}

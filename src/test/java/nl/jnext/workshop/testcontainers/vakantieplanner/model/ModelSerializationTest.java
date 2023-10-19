package nl.jnext.workshop.testcontainers.vakantieplanner.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.TypeRef;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.context.annotation.ComponentScan;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

@JsonTest
@ComponentScan("nl.jnext.workshop.testcontainers.helper.spring")
public class ModelSerializationTest {

    Logger logger = LoggerFactory.getLogger(ModelSerializationTest.class);

    @Autowired
    private ObjectMapper mapper;

//    private static final TypeRef<List<Holiday>> LIST_OF_HOLIDAYS_TYPE = new TypeRef<>() {};

    @Test
    void serializeMember() throws Exception {
        Member member = new Member(1, "bob");
        String s = mapper.writeValueAsString(member);
        logger.info("Result of serialization is {}", s);

        String nameAttr = JsonPath.parse(s).read("$.name");
        assertThat(nameAttr).isEqualTo("bob");
//        List<Holiday> result = JsonPath.parse(s).read("$.holidays", LIST_OF_HOLIDAYS_TYPE);
    }
}

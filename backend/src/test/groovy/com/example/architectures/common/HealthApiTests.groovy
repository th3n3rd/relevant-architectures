package com.example.architectures.common

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
class HealthApiTests extends Specification {

    @Autowired
    private TestRestTemplate client;

    def "health endpoint is not protected"() {
        when:
        var response = client.getForEntity("/actuator/health", Void);

        then:
        assert response.statusCode.is2xxSuccessful()
    }

}

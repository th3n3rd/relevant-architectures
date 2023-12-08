package com.example.architectures.postings

import com.example.architectures.common.InMemoryEventPublisher
import com.example.architectures.common.WebSecurityConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static com.example.architectures.common.AuthServer.validTokenForSpring
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest([
    SetupAccountApi,
    SetupAccount,
    InMemoryEventPublisher,
    InMemoryAuthorisations,
    WebSecurityConfig
])
class SetupAccountApiTests extends Specification {

    @Autowired
    private MockMvc client

    @Autowired
    private InMemoryEventPublisher eventPublisher

    @Autowired
    private InMemoryAuthorisations authorisations

    void setup() {
        authorisations.deleteAll()
    }

    def "setup a given account"() {
        given:
        def anyClientId = 123
        def anyAccountId = 789
        def anyConsultantId = 456
        authorisations.authorise(anyConsultantId, anyClientId)

        when:
        def result = client.perform(
            post("/clients/{clientId}/accounts", anyClientId)
                .with(validTokenForSpring(anyConsultantId))
                .contentType("application/json")
                .content("""
                {
                    "accountId": "${anyAccountId}"
                }
                """)
        )

        then:
        result.andExpect(status().isNoContent())
        eventPublisher.publishedEvents() == [new NewAccountSetup(anyClientId, anyAccountId)]
    }

    def "fails when not authenticated"() {
        given:
        def anyClientId = 123
        def anyAccountId = 789

        when:
        def result = client.perform(
            post("/clients/{clientId}/accounts", anyClientId)
                .contentType("application/json")
                .content("""
                {
                    "accountId": "${anyAccountId}"
                }
                """)
        )

        then:
        result.andExpect(status().isUnauthorized())
    }

    def "fails when not authorised to manage the given client"() {
        given:
        def anyClientId = 123
        def anyAccountId = 789
        def anyConsultantId = 456

        when:
        def result = client.perform(
            post("/clients/{clientId}/accounts", anyClientId)
                .with(validTokenForSpring(anyConsultantId))
                .contentType("application/json")
                .content("""
                {
                    "accountId": "${anyAccountId}"
                }
                """)
        )

        then:
        result.andExpect(status().isForbidden())
    }
}

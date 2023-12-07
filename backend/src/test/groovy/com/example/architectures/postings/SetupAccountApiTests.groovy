package com.example.architectures.postings

import com.example.architectures.common.InMemoryEventPublisher
import com.example.architectures.common.WebSecurityConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(SetupAccountApi)
@Import([SetupAccount, InMemoryEventPublisher, WebSecurityConfig])
class SetupAccountApiTests extends Specification {

    @Autowired
    private MockMvc client

    @Autowired
    private InMemoryEventPublisher eventPublisher

    def "setup a given account"() {
        given:
        def anyClientId = 123
        def anyAccountId = 789

        when:
        def result = client.perform(
            post("/clients/{clientId}/accounts", anyClientId)
                .with(jwt())
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
}

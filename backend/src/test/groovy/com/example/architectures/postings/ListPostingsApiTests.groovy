package com.example.architectures.postings

import com.example.architectures.common.WebSecurityConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static com.example.architectures.common.AuthServer.validTokenForSpring
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest([
    ListPostingsApi,
    InMemoryPostings,
    InMemoryAuthorisations,
    WebSecurityConfig
])
class ListPostingsApiTests extends Specification {

    @Autowired
    private MockMvc client

    @Autowired
    private InMemoryPostings postings

    @Autowired
    private InMemoryAuthorisations authorisations

    void setup() {
        authorisations.deleteAll()
    }

    def "lists postings for a given client and account"() {
        given:
        def anyConsultantId = 456
        def anyClientId = 123
        def anotherClientId = 135
        def klarna = 789
        def amazon = 792
        authorisations.authorise(anyConsultantId, anyClientId)
        postings.saveAll([
            new Posting(anyClientId, klarna, new BigDecimal("120.0"), "GBP"),
            new Posting(anotherClientId, amazon, new BigDecimal("80.0"), "EUR"),
            new Posting(anyClientId, amazon, new BigDecimal("30.0"), "EUR"),
            new Posting(anotherClientId, klarna, new BigDecimal("50.0"), "GBP"),
        ])

        when:
        def result = client.perform(
            get("/clients/{clientId}/accounts/{accountId}/postings", anyClientId, klarna)
                .with(validTokenForSpring(anyConsultantId))
        )

        then:
        result.andExpect(status().isOk())
        result.andExpect(content().json("""
        {
            "postings": [
                { "clientId": $anyClientId, "accountId": $klarna, "amount": "120.0", "currency": "GBP" }
            ]
        }
        """))
    }

    def "fails when not authenticated"() {
        given:
        def anyClientId = 123
        def anyAccountId = 789

        when:
        def result = client.perform(
            get("/clients/{clientId}/accounts/{accountId}/postings", anyClientId, anyAccountId)
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
            get("/clients/{clientId}/accounts/{accountId}/postings", anyClientId, anyAccountId)
                .with(validTokenForSpring(anyConsultantId))
        )

        then:
        result.andExpect(status().isForbidden())
    }
}
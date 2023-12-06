package com.example.architectures.postings

import com.example.architectures.common.WebSecurityConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(ListPostingsApi)
@Import([InMemoryPostings, WebSecurityConfig])
class ListPostingsApiTests extends Specification {

    @Autowired
    private MockMvc client

    @Autowired
    private InMemoryPostings postings;

    def "lists postings for a given client and account"() {
        given:
        def anyClientId = 123
        def anotherClientId = 135
        def klarna = 789
        def amazon = 792
        postings.saveAll([
            new Posting(anyClientId, klarna, new BigDecimal("120.0"), "GBP"),
            new Posting(anotherClientId, amazon, new BigDecimal("80.0"), "EUR"),
            new Posting(anyClientId, amazon, new BigDecimal("30.0"), "EUR"),
            new Posting(anotherClientId, klarna, new BigDecimal("50.0"), "GBP"),
        ])

        when:
        def result = client.perform(
            get("/clients/{clientId}/accounts/{accountId}/postings", anyClientId, klarna)
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
}

package com.example.architectures.postings

import com.example.architectures.common.WebSecurityConfig
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson
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
@AutoConfigureJson
class ListPostingsApiTests extends Specification {

    private static final anyConsultantId = 456
    private static final anyClientId = new ClientId(123)
    private static final anyAccountId = new AccountId(789)

    @Autowired
    private MockMvc client

    @Autowired
    private InMemoryPostings postings

    @Autowired
    private InMemoryAuthorisations authorisations

    void setup() {
        authorisations.deleteAll()
        postings.deleteAll()
    }

    def "lists postings for a given client and account"() {
        given:
        def anotherClientId = new ClientId(135)
        def klarna = new AccountId(789)
        def amazon = new AccountId(792)
        authorisations.authorise(anyConsultantId, anyClientId)
        postings.saveAll([
            new Posting(anyClientId, klarna, new BigDecimal("120.0"), "GBP"),
            new Posting(anotherClientId, amazon, new BigDecimal("80.0"), "EUR"),
            new Posting(anyClientId, amazon, new BigDecimal("30.0"), "EUR"),
            new Posting(anotherClientId, klarna, new BigDecimal("50.0"), "GBP"),
        ])

        when:
        def result = client.perform(
            get("/clients/{clientId}/accounts/{accountId}/postings", anyClientId.value(), klarna.value())
                .with(validTokenForSpring(anyConsultantId))
        )

        then:
        result.andExpect(status().isOk())
        result.andExpect(content().json("""
        {
            "postings": [
                {
                    "clientId": $anyClientId.value,
                    "accountId": $klarna.value,
                    "amount": "120.0",
                    "currency": "GBP"
                }
            ]
        }
        """))
    }

    def "paginate postings for a given client and account"() {
        given:
        authorisations.authorise(anyConsultantId, anyClientId)
        postings.saveAll([
            new Posting(anyClientId, anyAccountId, new BigDecimal("45.0"), "GBP"),
            new Posting(anyClientId, anyAccountId, new BigDecimal("70.0"), "EUR"),
            new Posting(anyClientId, anyAccountId, new BigDecimal("15.0"), "GPB"),
        ])

        when:
        def result = client.perform(
            get("/clients/{clientId}/accounts/{accountId}/postings", anyClientId.value(), anyAccountId.value())
                .queryParam("page", "1")
                .queryParam("size", "1")
                .with(validTokenForSpring(anyConsultantId))
        )

        then:
        result.andExpect(status().isOk())
        result.andExpect(content().json("""
        {
            "postings": [
                {
                    "clientId": $anyClientId.value,
                    "accountId": $anyAccountId.value,
                    "amount": "70.0",
                    "currency": "EUR"
                }
            ],
            "metadata": {
                "pageNumber": 1,
                "pageSize": 1,
                "totalPages": 3,
                "totalElements": 3
            }
        }
        """))
    }

    def "fails when not authenticated"() {
        when:
        def result = client.perform(
            get("/clients/{clientId}/accounts/{accountId}/postings", anyClientId.value(), anyAccountId.value())
        )

        then:
        result.andExpect(status().isUnauthorized())
    }

    def "fails when not authorised to manage the given client"() {
        when:
        def result = client.perform(
            get("/clients/{clientId}/accounts/{accountId}/postings", anyClientId.value(), anyAccountId.value())
                .with(validTokenForSpring(anyConsultantId))
        )

        then:
        result.andExpect(status().isForbidden())
    }
}

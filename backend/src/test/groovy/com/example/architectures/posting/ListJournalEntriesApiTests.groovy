package com.example.architectures.posting

import com.example.architectures.auth.InMemoryAuthorisations
import com.example.architectures.auth.WebSecurityConfig
import com.example.architectures.common.ClientId
import com.example.architectures.common.ConsultantId
import com.example.architectures.ecommerce.AccountId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static com.example.architectures.auth.Auth.authenticatedConsultant
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest([
    ListJournalEntriesApi,
    InMemoryJournal,
    InMemoryAuthorisations,
    WebSecurityConfig
])
@AutoConfigureJson
class ListJournalEntriesApiTests extends Specification {

    private static final anyConsultantId = new ConsultantId(456)
    private static final anyClientId = new ClientId(123)
    private static final anyAccountId = new AccountId("789")

    @Autowired
    private MockMvc client

    @Autowired
    private InMemoryJournal journal

    @Autowired
    private InMemoryAuthorisations authorisations

    void setup() {
        authorisations.deleteAll()
        journal.deleteAll()
    }

    def "lists journal entries for a given client"() {
        given:
        def anotherClientId = new ClientId(135)
        def klarna = new AccountId("789")
        def amazon = new AccountId("792")
        def entries = [
            new JournalEntry(anyClientId, klarna, new BigDecimal("120.0"), "GBP"),
            new JournalEntry(anotherClientId, amazon, new BigDecimal("80.0"), "EUR"),
            new JournalEntry(anyClientId, amazon, new BigDecimal("30.0"), "EUR"),
            new JournalEntry(anotherClientId, klarna, new BigDecimal("50.0"), "GBP"),
        ]
        def firstEntryId = entries.get(0).id().value()
        def thirdEntryId = entries.get(2).id().value()
        authorisations.authorise(anyConsultantId, anyClientId)
        journal.saveAll(entries)

        when:
        def result = client.perform(
            get("/clients/{clientId}/journal", anyClientId.value())
                .with(authenticatedConsultant(anyConsultantId))
        )

        then:
        result.andExpect(status().isOk())
        result.andExpect(content().json("""
        {
            "entries": [
                {
                    "id": "$firstEntryId",
                    "clientId": $anyClientId.value,
                    "accountId": "$klarna.value",
                    "amount": "120.0",
                    "currency": "GBP"
                },
                {
                    "id": "$thirdEntryId",
                    "clientId": $anyClientId.value,
                    "accountId": "$amazon.value",
                    "amount": "30.0",
                    "currency": "EUR"
                }
            ]
        }
        """))
    }

    def "paginate journal entries for a given client"() {
        given:
        authorisations.authorise(anyConsultantId, anyClientId)
        journal.saveAll([
            new JournalEntry(anyClientId, anyAccountId, new BigDecimal("45.0"), "GBP"),
            new JournalEntry(anyClientId, anyAccountId, new BigDecimal("70.0"), "EUR"),
            new JournalEntry(anyClientId, anyAccountId, new BigDecimal("15.0"), "GPB"),
        ])

        when:
        def result = client.perform(
            get("/clients/{clientId}/journal", anyClientId.value())
                .queryParam("page", "1")
                .queryParam("size", "1")
                .with(authenticatedConsultant(anyConsultantId))
        )

        then:
        result.andExpect(status().isOk())
        result.andExpect(content().json("""
        {
            "entries": [
                {
                    "clientId": $anyClientId.value,
                    "accountId": "$anyAccountId.value",
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
            get("/clients/{clientId}/journal", anyClientId.value())
        )

        then:
        result.andExpect(status().isUnauthorized())
    }

    def "fails when not authorised to manage the given client"() {
        when:
        def result = client.perform(
            get("/clients/{clientId}/journal", anyClientId.value())
                .with(authenticatedConsultant(anyConsultantId))
        )

        then:
        result.andExpect(status().isForbidden())
    }
}

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
import static com.example.architectures.posting.ChartOfAccounts.Cash
import static com.example.architectures.posting.ChartOfAccounts.SalesRevenue
import static com.example.architectures.posting.JournalEntry.Line.credit
import static com.example.architectures.posting.JournalEntry.Line.debit
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest([
    GetJournalDetailsApi,
    InMemoryJournal,
    InMemoryAuthorisations,
    WebSecurityConfig
])
@AutoConfigureJson
class GetJournalDetailsApiTests extends Specification {

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
            JournalEntry.fromEcommerce(klarna)
                .clientId(anyClientId)
                .amount(new BigDecimal("120.0"))
                .currency("GBP")
                .build(),
            JournalEntry.fromEcommerce(amazon)
                .clientId(anotherClientId)
                .amount(new BigDecimal("80.0"))
                .currency("EUR")
                .build(),
            JournalEntry.fromEcommerce(amazon)
                .clientId(anyClientId)
                .amount(new BigDecimal("30.0"))
                .currency("EUR")
                .build()
                .withLines(List.of(
                    debit(Cash, new BigDecimal("30.0"), "EUR"),
                    credit(SalesRevenue, new BigDecimal("30.0"), "EUR"),
                )),
            JournalEntry.fromEcommerce(klarna)
                .clientId(anotherClientId)
                .amount(new BigDecimal("50.0"))
                .currency("GBP")
                .build(),
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
                    "amount": "120.0",
                    "currency": "GBP",
                    "status": "Incomplete",
                    "lines": [],
                    "metadata": {
                        "origin": "e-commerce",
                        "accountId": "$klarna.value"
                    }
                },
                {
                    "id": "$thirdEntryId",
                    "clientId": $anyClientId.value,
                    "amount": "30.0",
                    "currency": "EUR",
                    "status": "Complete",
                    "lines": [
                        { type: "Debit", accountName: "cash", amount: "30.0", currency: "EUR" },
                        { type: "Credit", accountName: "sales-revenue", amount: "30.0", currency: "EUR" }
                    ],
                    "metadata": {
                        "origin": "e-commerce",
                        "accountId": "$amazon.value"
                    }
                }
            ]
        }
        """))
    }

    def "paginate journal entries for a given client"() {
        given:
        authorisations.authorise(anyConsultantId, anyClientId)
        journal.saveAll([
            JournalEntry.fromEcommerce(anyAccountId)
                .clientId(anyClientId)
                .amount(new BigDecimal("45.0"))
                .currency("GBP")
                .build(),
            JournalEntry.fromEcommerce(anyAccountId)
                .clientId(anyClientId)
                .amount(new BigDecimal("70.0"))
                .currency("EUR")
                .build(),
            JournalEntry.fromEcommerce(anyAccountId)
                .clientId(anyClientId)
                .amount(new BigDecimal("15.0"))
                .currency("GBP")
                .build(),
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
                    "amount": "70.0",
                    "currency": "EUR",
                    "lines": [],
                    "metadata": {
                        "origin": "e-commerce",
                        "accountId": "$anyAccountId.value"
                    }
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

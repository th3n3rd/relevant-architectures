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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest([
    EditJournalApi,
    EditJournal,
    ChartOfAccounts,
    InMemoryJournal,
    InMemoryAuthorisations,
    WebSecurityConfig
])
@AutoConfigureJson
class EditJournalApiTests extends Specification {

    private static final anyConsultantId = new ConsultantId(456)
    private static final anyClientId = new ClientId(123)
    private static final anyAccountId = new AccountId("729")


    @Autowired
    private MockMvc client

    @Autowired
    private InMemoryAuthorisations authorisations

    @Autowired
    private InMemoryJournal journal

    void setup() {
        journal.deleteAll()
        authorisations.deleteAll()
    }

    def "updates an entry with the given entry lines"() {
        given:
        def entry = JournalEntry.fromEcommerce(anyAccountId)
            .clientId(anyClientId)
            .amount(new BigDecimal("100.0"))
            .currency("EUR")
            .build()
        journal.save(entry)
        authorisations.authorise(anyConsultantId, anyClientId)

        when:
        def result = client.perform(
            patch("/clients/{clientId}/journal/{entryId}", anyClientId.value(), entry.id().value())
                .with(authenticatedConsultant(anyConsultantId))
                .contentType("application/json")
                .content("""
                {
                    "lines": [
                        { "type": "Debit", "accountName": "cash", "amount": 100.0, "currency": "EUR" },
                        { "type": "Credit", "accountName": "sales-revenue", "amount": 100.0, "currency": "EUR" }
                    ]
                }
                """)
        )

        then:
        result.andExpect(status().isNoContent())
        var updatedEntry = journal.findById(entry.id()).orElseThrow()
        updatedEntry.lines() != entry.lines()
    }

    def "fails to update an entry when the lines are unbalanced"() {
        given:
        def entry = JournalEntry.fromEcommerce(anyAccountId)
            .clientId(anyClientId)
            .amount(new BigDecimal("80.0"))
            .currency("EUR")
            .build()
        journal.save(entry)
        authorisations.authorise(anyConsultantId, anyClientId)

        when:
        def result = client.perform(
            patch("/clients/{clientId}/journal/{entryId}", anyClientId.value(), entry.id().value())
                .with(authenticatedConsultant(anyConsultantId))
                .contentType("application/json")
                .content("""
                {
                    "lines": [
                        { "type": "Debit", "accountName": "cash", "amount": 60.0, "currency": "EUR" },
                        { "type": "Credit", "accountName": "sales-revenue", "amount": 80.0, "currency": "EUR" }
                    ]
                }
                """)
        )

        then:
        result.andExpect(status().isBadRequest())
    }

    def "fails when not authenticated"() {
        when:
        def result = client.perform(
            patch("/clients/{clientId}/journal/{entryId}", anyClientId.value(), new JournalEntryId().value())
        )

        then:
        result.andExpect(status().isUnauthorized())
    }

    def "fails when not authorised to manage the given client"() {
        when:
        def result = client.perform(
            patch("/clients/{clientId}/journal/{entryId}", anyClientId.value(), new JournalEntryId().value())
                .with(authenticatedConsultant(anyConsultantId))
                .contentType("application/json")
                .content("""
                {
                    "lines": [
                        { "type": "Debit", "accountName": "cash", "amount": 50.0, "currency": "GBP" },
                        { "type": "Credit", "accountName": "sales-revenue", "amount": 50.0, "currency": "GBP" }
                    ]
                }
                """)
        )

        then:
        result.andExpect(status().isForbidden())
    }
}

package com.example.architectures.posting.infra

import com.example.architectures.auth.InMemoryAuthorisations
import com.example.architectures.auth.WebSecurityConfig
import com.example.architectures.common.ClientId
import com.example.architectures.common.ConsultantId
import com.example.architectures.posting.ChartOfAccounts
import com.example.architectures.posting.CreateLedger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static com.example.architectures.auth.Auth.authenticatedConsultant
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest([
    CreateLedgerApi,
    CreateLedger,
    ChartOfAccounts,
    InMemoryLedgers,
    InMemoryAuthorisations,
    WebSecurityConfig
])
@AutoConfigureJson
class CreateLedgerApiTests extends Specification {

    private static final anyConsultantId = new ConsultantId(456)
    private static final anyClientId = new ClientId(123)

    @Autowired
    private MockMvc client

    @Autowired
    private InMemoryAuthorisations authorisations

    @Autowired
    private InMemoryLedgers ledgers

    void setup() {
        authorisations.deleteAll()
    }

    def "create a new ledger for a given client"() {
        given:
        authorisations.authorise(anyConsultantId, anyClientId)

        when:
        def result = client.perform(
            post("/clients/{clientId}/ledger", anyClientId.value())
                .with(authenticatedConsultant(anyConsultantId))
        )

        then:
        result.andExpect(status().isCreated())
        ledgers.findByClientId(anyClientId).isPresent()
    }

    def "fails when not authenticated"() {
        when:
        def result = client.perform(
            post("/clients/{clientId}/ledger", anyClientId.value())
        )

        then:
        result.andExpect(status().isUnauthorized())
    }

    def "fails when not authorised to manage the given client"() {
        when:
        def result = client.perform(
            post("/clients/{clientId}/ledger", anyClientId.value())
                .with(authenticatedConsultant(anyConsultantId))
        )

        then:
        result.andExpect(status().isForbidden())
    }
}

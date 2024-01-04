package com.example.architectures.posting.infra

import com.example.architectures.auth.InMemoryAuthorisations
import com.example.architectures.auth.WebSecurityConfig
import com.example.architectures.common.ClientId
import com.example.architectures.common.ConsultantId
import com.example.architectures.posting.Ledger
import com.example.architectures.posting.LedgerAccount
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static com.example.architectures.auth.Auth.authenticatedConsultant
import static com.example.architectures.posting.ChartOfAccounts.AccountsReceivable
import static com.example.architectures.posting.ChartOfAccounts.Cash
import static com.example.architectures.posting.ChartOfAccounts.SalesRevenue
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest([
    GetLedgerDetailsApi,
    InMemoryLedgers,
    InMemoryAuthorisations,
    WebSecurityConfig
])
@AutoConfigureJson
class GetLedgerDetailsApiTests extends Specification {

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

    def "provide all the ledger accounts and their balances for the given client"() {
        given:
        authorisations.authorise(anyConsultantId, anyClientId)
        ledgers.save(new Ledger(anyClientId, List.of(
            new LedgerAccount(Cash, new BigDecimal("100.0")),
            new LedgerAccount(AccountsReceivable, new BigDecimal("50.0")),
            new LedgerAccount(SalesRevenue, new BigDecimal("150.0"))
        )))

        when:
        def result = client.perform(
            get("/clients/{clientId}/ledger", anyClientId.value())
                .with(authenticatedConsultant(anyConsultantId))
        )

        then:
        result.andExpect(status().isOk())
        result.andExpect(content().json("""
        {
            "accounts": [
                { name: "cash", balance: 100.0 },
                { name: "accounts-receivable", balance: 50.0 },
                { name: "sales-revenue", balance: 150.0 }
            ] 
        }
        """))
    }

    def "fails when not authenticated"() {
        when:
        def result = client.perform(
            get("/clients/{clientId}/ledger", anyClientId.value())
        )

        then:
        result.andExpect(status().isUnauthorized())
    }

    def "fails when not authorised to manage the given client"() {
        when:
        def result = client.perform(
            get("/clients/{clientId}/ledger", anyClientId.value())
                .with(authenticatedConsultant(anyConsultantId))
        )

        then:
        result.andExpect(status().isForbidden())
    }
}

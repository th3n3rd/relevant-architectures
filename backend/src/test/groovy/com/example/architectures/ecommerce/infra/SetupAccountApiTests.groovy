package com.example.architectures.ecommerce.infra

import com.example.architectures.auth.infra.InMemoryAuthorisations
import com.example.architectures.auth.infra.WebSecurityConfig
import com.example.architectures.common.ClientId
import com.example.architectures.common.ConsultantId
import com.example.architectures.common.infra.InMemoryEventPublisher
import com.example.architectures.ecommerce.AccountId
import com.example.architectures.ecommerce.NewAccountSetup
import com.example.architectures.ecommerce.SetupAccount
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJson
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static com.example.architectures.auth.infra.Auth.authenticatedConsultant
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest([
    SetupAccountApi,
    SetupAccount,
    InMemoryEventPublisher,
    InMemoryAuthorisations,
    WebSecurityConfig
])
@AutoConfigureJson
class SetupAccountApiTests extends Specification {

    private static final anyConsultantId = new ConsultantId(456)
    private static final anyClientId = new ClientId(123)
    private static final anyAccountId = new AccountId("789")

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
        authorisations.authorise(anyConsultantId, anyClientId)

        when:
        def result = client.perform(
            post("/clients/{clientId}/accounts", anyClientId.value())
                .with(authenticatedConsultant(anyConsultantId))
                .contentType("application/json")
                .content("""
                {
                    "accountId": $anyAccountId.value
                }
                """)
        )

        then:
        result.andExpect(status().isNoContent())
        eventPublisher.publishedEvents() == [
            new NewAccountSetup(anyClientId, anyAccountId)
        ]
    }

    def "fails when not authenticated"() {
        when:
        def result = client.perform(
            post("/clients/{clientId}/accounts", anyClientId.value())
                .contentType("application/json")
                .content("""
                {
                    "accountId": $anyAccountId.value
                }
                """)
        )

        then:
        result.andExpect(status().isUnauthorized())
    }

    def "fails when not authorised to manage the given client"() {
        when:
        def result = client.perform(
            post("/clients/{clientId}/accounts", anyClientId.value())
                .with(authenticatedConsultant(anyConsultantId))
                .contentType("application/json")
                .content("""
                {
                    "accountId": $anyAccountId.value
                }
                """)
        )

        then:
        result.andExpect(status().isForbidden())
    }
}

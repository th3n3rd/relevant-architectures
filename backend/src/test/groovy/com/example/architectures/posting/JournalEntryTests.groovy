package com.example.architectures.posting

import com.example.architectures.common.ClientId
import com.example.architectures.ecommerce.AccountId
import spock.lang.Specification

class JournalEntryTests extends Specification {

    def "two entries with the same identifier, but different values, are considered equal"() {
        given:
        def first = JournalEntry
            .builder()
            .id(JournalEntryId.of("00000000-0000-0000-0000-000000000000"))
            .clientId(new ClientId(456))
            .accountId(new AccountId("729"))
            .amount(new BigDecimal("10.0"))
            .currency("EUR")
            .build()

        when:
        def second = first.withAmount(new BigDecimal("15.0"))
        def third = first.withId(new JournalEntryId())

        then:
        first == second
        first != third
    }
}

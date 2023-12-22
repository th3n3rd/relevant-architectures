package com.example.architectures

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.modulith.core.ApplicationModules
import org.springframework.modulith.docs.Documenter
import spock.lang.Specification

@SpringBootTest
class DemoApplicationTests extends Specification {

	def modules = ApplicationModules.of(DemoApplication.class);

	def "context loads successfully"() {
		expect:
		true
	}

	def "architecture is documented"() {
		when:
		new Documenter(modules).writeDocumentation()

		then:
		true
	}

	def "architecture is well-structured"() {
		expect:
		modules.verify()
	}
}

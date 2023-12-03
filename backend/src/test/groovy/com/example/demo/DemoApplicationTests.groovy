package com.example.demo

import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class DemoApplicationTests extends Specification {

	def "context loads successfully"() {
		expect:
		true
	}

}

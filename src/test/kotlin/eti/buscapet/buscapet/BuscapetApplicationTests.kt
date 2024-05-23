package eti.buscapet.buscapet

import org.junit.jupiter.api.Test
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
@ConditionalOnProperty(value = arrayOf("skipTests"), havingValue = "false", matchIfMissing = true)
class BuscapetApplicationTests {

	@Test
	fun contextLoads() {
	}

}

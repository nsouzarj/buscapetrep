package eti.buscapet.buscapet
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication


@EnableAutoConfiguration

@SpringBootApplication
class BuscapetApplication

fun main(args: Array<String>) {
	runApplication<BuscapetApplication>(*args)
}

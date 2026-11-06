package net.konohana.sakuya.sandbox

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class JspecifySpringSandboxApplication

fun main(args: Array<String>) {
	runApplication<JspecifySpringSandboxApplication>(*args)
}

package kr.co.lunatalk

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@org.springframework.context.annotation.Import(TestSecurityConfig::class)
open class TestApplication

fun main(args: Array<String>) {
    runApplication<TestApplication>(*args)
}

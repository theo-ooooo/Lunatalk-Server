package kr.co.lunatalk

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
open class LunatalkServerApplication

fun main(args: Array<String>) {
    runApplication<LunatalkServerApplication>(*args)
}

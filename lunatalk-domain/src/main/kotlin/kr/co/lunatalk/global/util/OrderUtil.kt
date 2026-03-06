package kr.co.lunatalk.global.util

import org.springframework.stereotype.Component
import java.security.SecureRandom
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class OrderUtil {

    companion object {
        private val random = SecureRandom()
        private const val ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        private const val ORDER_NUMBER_PREFIX = "L"
    }

    fun generateOrderNumber(): String {
        val timeStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyMMddHHmmss"))
        val timeLong = timeStr.toLong()
        val base36Time = timeLong.toString(36).uppercase()
        val randomPart = generateRandomAlpha(2)

        return ORDER_NUMBER_PREFIX + base36Time + randomPart
    }

    private fun generateRandomAlpha(length: Int): String {
        val sb = StringBuilder()
        repeat(length) {
            sb.append(ALPHABET[random.nextInt(ALPHABET.length)])
        }
        return sb.toString()
    }
}

package kr.co.lunatalk.domain.payment.dto.toss

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@JsonIgnoreProperties(ignoreUnknown = true)
data class TossPaymentConfirmResponse(
    @JsonProperty("paymentKey")
    val paymentKey: String?,

    @JsonProperty("orderId")
    val orderId: String?,

    @JsonProperty("status")
    val status: String?,

    @JsonProperty("totalAmount")
    val totalAmount: Long?,

    @JsonProperty("method")
    val method: String?,

    @JsonProperty("approvedAt")
    val approvedAt: String?
) {
    fun getApprovedAtAsLocalDateTime(): LocalDateTime {
        if (approvedAt.isNullOrEmpty()) {
            return LocalDateTime.now()
        }
        return try {
            var dateStr = approvedAt
            if (dateStr.contains("+")) {
                dateStr = dateStr.substring(0, dateStr.indexOf("+"))
            } else if (dateStr.endsWith("Z")) {
                dateStr = dateStr.substring(0, dateStr.length - 1)
            }
            LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        } catch (e: Exception) {
            LocalDateTime.now()
        }
    }
}

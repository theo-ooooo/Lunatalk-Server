package kr.co.lunatalk.domain.payment.dto.toss;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TossPaymentConfirmResponse(
	@JsonProperty("paymentKey")
	String paymentKey,
	@JsonProperty("orderId")
	String orderId,
	@JsonProperty("status")
	String status,
	@JsonProperty("totalAmount")
	Long totalAmount,
	@JsonProperty("method")
	String method,
	@JsonProperty("approvedAt")
	String approvedAt
) {
	public LocalDateTime getApprovedAtAsLocalDateTime() {
		if (approvedAt == null || approvedAt.isEmpty()) {
			return LocalDateTime.now();
		}
		try {
			// ISO 8601 형식 파싱 시도
			String dateStr = approvedAt;
			// 타임존 제거 (예: "+09:00", "Z")
			if (dateStr.contains("+")) {
				dateStr = dateStr.substring(0, dateStr.indexOf("+"));
			} else if (dateStr.endsWith("Z")) {
				dateStr = dateStr.substring(0, dateStr.length() - 1);
			}
			// ISO_LOCAL_DATE_TIME 형식으로 파싱
			return LocalDateTime.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		} catch (Exception e) {
			// 파싱 실패 시 현재 시간 반환
			return LocalDateTime.now();
		}
	}
}


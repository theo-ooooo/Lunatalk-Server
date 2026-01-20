package kr.co.lunatalk.infra.config.mail;

import jakarta.mail.internet.MimeMessage;
import kr.co.lunatalk.domain.payment.event.PaymentCompletedEvent;
import kr.co.lunatalk.global.common.constants.UrlConstants;
import kr.co.lunatalk.global.util.SpringEnvironmentUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.nio.charset.StandardCharsets;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminPaymentCompletedMailListener {
	private final ObjectProvider<JavaMailSender> mailSenderProvider;
	private final LunatalkMailProperties mailProperties;
	private final SpringEnvironmentUtil springEnvironmentUtil;

	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void onPaymentCompleted(PaymentCompletedEvent event) {
		try {
			JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
			if (mailSender == null) {
				log.warn("JavaMailSender 빈이 없어 관리자 결제완료 메일 발송을 건너뜁니다. orderNumber={}", event.orderNumber());
				return;
			}

			MimeMessage mimeMessage = mailSender.createMimeMessage();
			// plain/html 대체 본문(alt text)을 넣으려면 multipart 모드가 필요
			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

			if (mailProperties.from() != null && !mailProperties.from().isBlank()) {
				helper.setFrom(mailProperties.from());
			}

			if (mailProperties.adminTo() == null || mailProperties.adminTo().isBlank()) {
				log.warn("lunatalk.mail.admin-to 가 비어있어 메일 발송을 건너뜁니다. orderNumber={}", event.orderNumber());
				return;
			}
			helper.setTo(mailProperties.adminTo());
			helper.setSubject("[Lunatalk] 결제 완료 - 주문번호 " + event.orderNumber());

			String plain = buildPlainText(event);
			String html = buildHtml(event);
			helper.setText(plain, html);

			mailSender.send(mimeMessage);
		} catch (Exception e) {
			// 메일 발송 실패가 결제 플로우를 깨지 않도록 로깅만
			log.error("관리자 결제완료 메일 발송 실패. orderNumber={}", event.orderNumber(), e);
		}
	}

	private String buildPlainText(PaymentCompletedEvent event) {
		String adminOrderUrl = buildAdminOrderUrl(event.orderNumber());
		StringBuilder sb = new StringBuilder();
		sb.append("결제가 완료되었습니다.\n\n");
		sb.append("주문번호: ").append(event.orderNumber()).append("\n");
		sb.append("주문ID: ").append(event.orderId()).append("\n");
		sb.append("구매자 이메일: ").append(event.memberEmail()).append("\n");
		sb.append("결제금액: ").append(event.totalAmount()).append("\n");
		sb.append("관리자 주문 상세: ").append(adminOrderUrl).append("\n\n");
		sb.append("구매 상품:\n");
		for (PaymentCompletedEvent.PaymentOrderItem item : event.items()) {
			sb.append("- ")
				.append(item.productName())
				.append(" (productId=").append(item.productId()).append(")")
				.append(" / 수량=").append(item.quantity())
				.append(" / 단가=").append(item.price())
				.append("\n");
		}
		return sb.toString();
	}

	private String buildHtml(PaymentCompletedEvent event) {
		String title = "결제 완료";
		String adminOrderUrl = buildAdminOrderUrl(event.orderNumber());

		StringBuilder rows = new StringBuilder();
		for (PaymentCompletedEvent.PaymentOrderItem item : event.items()) {
			rows.append("<tr>")
				.append("<td style=\"padding:10px;border-bottom:1px solid #eee;\">")
				.append(escapeHtml(item.productName()))
				.append("<div style=\"color:#6b7280;font-size:12px;\">productId: ")
				.append(item.productId())
				.append("</div>")
				.append("</td>")
				.append("<td style=\"padding:10px;border-bottom:1px solid #eee;text-align:right;\">")
				.append(item.quantity())
				.append("</td>")
				.append("<td style=\"padding:10px;border-bottom:1px solid #eee;text-align:right;\">")
				.append(formatAmount(item.price()))
				.append("</td>")
				.append("</tr>");
		}

		return "<!doctype html>"
			+ "<html lang=\"ko\"><head><meta charset=\"utf-8\"/>"
			+ "<meta name=\"viewport\" content=\"width=device-width,initial-scale=1\"/>"
			+ "<title>" + title + "</title>"
			+ "</head><body style=\"margin:0;padding:0;background:#f5f6f8;font-family:Arial,Helvetica,sans-serif;color:#111827;\">"
			+ "<div style=\"max-width:720px;margin:0 auto;padding:24px;\">"
			+ "<div style=\"background:#ffffff;border:1px solid #e5e7eb;border-radius:14px;overflow:hidden;\">"
			+ "<div style=\"padding:20px 22px;background:linear-gradient(135deg,#111827,#374151);color:#fff;\">"
			+ "<div style=\"font-size:14px;opacity:.9;\">Lunatalk 알림</div>"
			+ "<div style=\"font-size:22px;font-weight:700;margin-top:6px;\">결제 완료</div>"
			+ "<div style=\"font-size:13px;opacity:.9;margin-top:8px;\">주문번호: <b>" + escapeHtml(event.orderNumber()) + "</b></div>"
			+ "</div>"
			+ "<div style=\"padding:22px;\">"
			+ "<div style=\"display:flex;gap:12px;flex-wrap:wrap;margin-bottom:18px;\">"
			+ badge("주문ID", String.valueOf(event.orderId()))
			+ badge("구매자", escapeHtml(event.memberEmail()))
			+ badge("결제금액", formatAmount(event.totalAmount()))
			+ "</div>"
			+ "<div style=\"margin:6px 0 18px;\">"
			+ "<a href=\"" + escapeHtml(adminOrderUrl) + "\" "
			+ "style=\"display:inline-block;padding:10px 14px;border-radius:12px;background:#111827;color:#fff;text-decoration:none;font-weight:700;font-size:13px;\">"
			+ "관리자에서 주문 보기"
			+ "</a>"
			+ "<div style=\"margin-top:8px;color:#6b7280;font-size:12px;\">"
			+ escapeHtml(adminOrderUrl)
			+ "</div>"
			+ "</div>"
			+ "<div style=\"font-size:16px;font-weight:700;margin:18px 0 10px;\">구매 상품</div>"
			+ "<table style=\"width:100%;border-collapse:collapse;border:1px solid #e5e7eb;border-radius:12px;overflow:hidden;\">"
			+ "<thead><tr style=\"background:#f9fafb;\">"
			+ "<th style=\"padding:10px;text-align:left;font-size:12px;color:#374151;border-bottom:1px solid #e5e7eb;\">상품</th>"
			+ "<th style=\"padding:10px;text-align:right;font-size:12px;color:#374151;border-bottom:1px solid #e5e7eb;\">수량</th>"
			+ "<th style=\"padding:10px;text-align:right;font-size:12px;color:#374151;border-bottom:1px solid #e5e7eb;\">단가</th>"
			+ "</tr></thead>"
			+ "<tbody>" + rows + "</tbody>"
			+ "</table>"
			+ "<div style=\"margin-top:18px;padding:12px 14px;background:#f9fafb;border:1px solid #e5e7eb;border-radius:12px;color:#6b7280;font-size:12px;\">"
			+ "이 메일은 결제 완료 시점에 자동 발송됩니다."
			+ "</div>"
			+ "</div>"
			+ "</div>"
			+ "<div style=\"text-align:center;color:#9ca3af;font-size:12px;margin-top:14px;\">© Lunatalk</div>"
			+ "</div></body></html>";
	}

	private String buildAdminOrderUrl(String orderNumber) {
		String base;
		if (springEnvironmentUtil.isProdProfile()) {
			base = UrlConstants.PROD_DOMAIN_ADMIN_URL.getValue();
		} else if (springEnvironmentUtil.isDevProfile()) {
			base = UrlConstants.DEV_DOMAIN_ADMIN_URL.getValue();
		} else {
			base = UrlConstants.LOCAL_ADMIN_DOMAIN_URL.getValue();
		}
		return base + "/orders/" + orderNumber;
	}

	private static String badge(String label, String value) {
		return "<div style=\"display:inline-flex;flex-direction:column;gap:4px;padding:10px 12px;border:1px solid #e5e7eb;border-radius:12px;min-width:200px;\">"
			+ "<div style=\"font-size:12px;color:#6b7280;\">" + escapeHtml(label) + "</div>"
			+ "<div style=\"font-size:14px;font-weight:700;color:#111827;\">" + escapeHtml(value) + "</div>"
			+ "</div>";
	}

	private static String formatAmount(Long amount) {
		if (amount == null) {
			return "-";
		}
		// 간단히 원 단위 표기 (천단위 콤마)
		return String.format("%,d원", amount);
	}

	private static String escapeHtml(String s) {
		if (s == null) {
			return "";
		}
		return s
			.replace("&", "&amp;")
			.replace("<", "&lt;")
			.replace(">", "&gt;")
			.replace("\"", "&quot;")
			.replace("'", "&#39;");
	}
}



package kr.co.lunatalk.domain.inquiry.domain;

import jakarta.persistence.*;
import kr.co.lunatalk.domain.common.domain.BaseTimeEntity;
import kr.co.lunatalk.domain.member.domain.Member;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inquiry extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private InquiryType type;

	@Column(nullable = false, length = 200)
	private String title;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private InquiryStatus status = InquiryStatus.PENDING;

	// 참조 대상 ID (상품 ID, 주문 ID 등, 일반 문의인 경우 null)
	@Column(name = "reference_id")
	private Long referenceId;

	@OneToOne(mappedBy = "inquiry", cascade = CascadeType.ALL, orphanRemoval = true)
	private InquiryReply reply;

	@Builder(access = AccessLevel.PRIVATE)
	public Inquiry(Member member, InquiryType type, String title, String content, Long referenceId) {
		this.member = member;
		this.type = type;
		this.title = title;
		this.content = content;
		this.referenceId = referenceId;
		this.status = InquiryStatus.PENDING;
	}

	public static Inquiry createProductInquiry(Member member, String title, String content, Long productId) {
		return Inquiry.builder()
			.member(member)
			.type(InquiryType.PRODUCT)
			.title(title)
			.content(content)
			.referenceId(productId)
			.build();
	}

	public static Inquiry createOrderInquiry(Member member, String title, String content, Long orderId) {
		return Inquiry.builder()
			.member(member)
			.type(InquiryType.ORDER)
			.title(title)
			.content(content)
			.referenceId(orderId)
			.build();
	}

	public static Inquiry createGeneralInquiry(Member member, String title, String content) {
		return Inquiry.builder()
			.member(member)
			.type(InquiryType.GENERAL)
			.title(title)
			.content(content)
			.referenceId(null)
			.build();
	}

	public void addReply(InquiryReply reply) {
		this.reply = reply;
		this.status = InquiryStatus.ANSWERED;
	}

	public void updateStatus(InquiryStatus status) {
		this.status = status;
	}

	public void update(String title, String content) {
		this.title = title;
		this.content = content;
	}
}


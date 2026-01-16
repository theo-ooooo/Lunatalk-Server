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
public class InquiryReply extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "inquiry_id", nullable = false, unique = true)
	private Inquiry inquiry;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "admin_id", nullable = false)
	private Member admin;

	@Column(nullable = false, columnDefinition = "TEXT")
	private String content;

	@Builder(access = AccessLevel.PRIVATE)
	public InquiryReply(Inquiry inquiry, Member admin, String content) {
		this.inquiry = inquiry;
		this.admin = admin;
		this.content = content;
	}

	public static InquiryReply createReply(Inquiry inquiry, Member admin, String content) {
		return InquiryReply.builder()
			.inquiry(inquiry)
			.admin(admin)
			.content(content)
			.build();
	}

	public void update(String content) {
		this.content = content;
	}
}


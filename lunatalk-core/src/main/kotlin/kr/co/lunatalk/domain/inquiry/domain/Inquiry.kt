package kr.co.lunatalk.domain.inquiry.domain

import jakarta.persistence.*
import kr.co.lunatalk.domain.common.domain.BaseTimeEntity
import kr.co.lunatalk.domain.member.domain.Member

@Entity
open class Inquiry protected constructor() : BaseTimeEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    open var member: Member? = null
        protected set

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    open var type: InquiryType? = null
        protected set

    @Column(nullable = false, length = 200)
    open var title: String = ""
        protected set

    @Column(nullable = false, columnDefinition = "TEXT")
    open var content: String = ""
        protected set

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    open var status: InquiryStatus = InquiryStatus.PENDING
        protected set

    @Column(name = "reference_id")
    open var referenceId: Long? = null
        protected set

    @OneToOne(mappedBy = "inquiry", cascade = [CascadeType.ALL], orphanRemoval = true)
    open var reply: InquiryReply? = null
        protected set

    private constructor(
        member: Member,
        type: InquiryType,
        title: String,
        content: String,
        referenceId: Long?
    ) : this() {
        this.member = member
        this.type = type
        this.title = title
        this.content = content
        this.referenceId = referenceId
        this.status = InquiryStatus.PENDING
    }

    fun addReply(reply: InquiryReply) {
        this.reply = reply
        this.status = InquiryStatus.ANSWERED
    }

    fun updateStatus(status: InquiryStatus) {
        this.status = status
    }

    fun update(title: String, content: String) {
        this.title = title
        this.content = content
    }

    companion object {
        fun createProductInquiry(member: Member, title: String, content: String, productId: Long): Inquiry {
            return Inquiry(member, InquiryType.PRODUCT, title, content, productId)
        }

        fun createOrderInquiry(member: Member, title: String, content: String, orderId: Long): Inquiry {
            return Inquiry(member, InquiryType.ORDER, title, content, orderId)
        }

        fun createGeneralInquiry(member: Member, title: String, content: String): Inquiry {
            return Inquiry(member, InquiryType.GENERAL, title, content, null)
        }
    }
}

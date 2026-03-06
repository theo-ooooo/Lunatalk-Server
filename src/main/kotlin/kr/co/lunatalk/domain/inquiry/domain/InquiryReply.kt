package kr.co.lunatalk.domain.inquiry.domain

import jakarta.persistence.*
import kr.co.lunatalk.domain.common.domain.BaseTimeEntity
import kr.co.lunatalk.domain.member.domain.Member

@Entity
open class InquiryReply protected constructor() : BaseTimeEntity() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null
        protected set

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id", nullable = false, unique = true)
    open var inquiry: Inquiry? = null
        protected set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", nullable = false)
    open var admin: Member? = null
        protected set

    @Column(nullable = false, columnDefinition = "TEXT")
    open var content: String = ""
        protected set

    private constructor(inquiry: Inquiry, admin: Member, content: String) : this() {
        this.inquiry = inquiry
        this.admin = admin
        this.content = content
    }

    fun update(content: String) {
        this.content = content
    }

    companion object {
        fun createReply(inquiry: Inquiry, admin: Member, content: String): InquiryReply {
            return InquiryReply(inquiry, admin, content)
        }
    }
}

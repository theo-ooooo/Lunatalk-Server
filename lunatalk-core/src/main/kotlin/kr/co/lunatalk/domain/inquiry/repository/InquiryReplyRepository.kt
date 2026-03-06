package kr.co.lunatalk.domain.inquiry.repository

import kr.co.lunatalk.domain.inquiry.domain.InquiryReply
import org.springframework.data.jpa.repository.JpaRepository

interface InquiryReplyRepository : JpaRepository<InquiryReply, Long>

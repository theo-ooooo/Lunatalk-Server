package kr.co.lunatalk.domain.inquiry.repository

import kr.co.lunatalk.domain.inquiry.domain.Inquiry
import org.springframework.data.jpa.repository.JpaRepository

interface InquiryRepository : JpaRepository<Inquiry, Long>, InquiryRepositoryCustom

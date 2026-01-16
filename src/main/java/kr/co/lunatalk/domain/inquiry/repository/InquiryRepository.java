package kr.co.lunatalk.domain.inquiry.repository;

import kr.co.lunatalk.domain.inquiry.domain.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long>, InquiryRepositoryCustom {
}


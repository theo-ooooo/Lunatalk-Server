package kr.co.lunatalk.domain.exhibition.repository;

import kr.co.lunatalk.domain.exhibition.domain.Exhibition;
import kr.co.lunatalk.domain.exhibition.domain.ExhibitionVisibility;

import java.time.LocalDateTime;
import java.util.List;

public interface ExhibitionRepositoryCustom {
	List<Exhibition> findActiveExhibitions(ExhibitionVisibility visibility, LocalDateTime now);
}

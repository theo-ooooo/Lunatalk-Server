package kr.co.lunatalk.domain.exhibition.repository;

import kr.co.lunatalk.domain.exhibition.domain.Exhibition;
import kr.co.lunatalk.domain.exhibition.domain.ExhibitionVisibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExhibitionRepository extends JpaRepository<Exhibition, Long>, ExhibitionRepositoryCustom {

	@Modifying(clearAutomatically = true)
	@Query("DELETE FROM ExhibitionProduct ep WHERE ep.exhibition.id = :exhibitionId")
	void deleteProductByExhibitionId(@Param("exhibitionId") Long exhibitionId);

}

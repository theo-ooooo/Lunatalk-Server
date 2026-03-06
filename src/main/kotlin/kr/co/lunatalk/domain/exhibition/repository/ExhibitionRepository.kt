package kr.co.lunatalk.domain.exhibition.repository

import kr.co.lunatalk.domain.exhibition.domain.Exhibition
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ExhibitionRepository : JpaRepository<Exhibition, Long>, ExhibitionRepositoryCustom {

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM ExhibitionProduct ep WHERE ep.exhibition.id = :exhibitionId")
    fun deleteProductByExhibitionId(@Param("exhibitionId") exhibitionId: Long)
}

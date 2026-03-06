package kr.co.lunatalk.domain.exhibition.repository

import kr.co.lunatalk.domain.exhibition.domain.Exhibition
import kr.co.lunatalk.domain.exhibition.domain.ExhibitionVisibility
import java.time.LocalDateTime

interface ExhibitionRepositoryCustom {
    fun findActiveExhibitions(visibility: ExhibitionVisibility, now: LocalDateTime): List<Exhibition>
}

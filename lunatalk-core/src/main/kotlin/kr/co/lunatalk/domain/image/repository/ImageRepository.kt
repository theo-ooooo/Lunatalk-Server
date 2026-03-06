package kr.co.lunatalk.domain.image.repository

import kr.co.lunatalk.domain.image.domain.Image
import org.springframework.data.jpa.repository.JpaRepository
import java.util.Optional

interface ImageRepository : JpaRepository<Image, Long>, ImageRepositoryCustom {
    fun findByImageKey(imageKey: String): Optional<Image>
}

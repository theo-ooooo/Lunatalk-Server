package kr.co.lunatalk.domain.image.dto

import kr.co.lunatalk.domain.image.domain.Image

data class FindImageDto(
    val imageType: String,
    val imageUrl: String?,
    val imageKey: String?
) {
    companion object {
        fun from(image: Image): FindImageDto {
            return FindImageDto(
                imageType = image.imageType!!.name,
                imageUrl = image.imagePath,
                imageKey = image.imageKey
            )
        }
    }
}

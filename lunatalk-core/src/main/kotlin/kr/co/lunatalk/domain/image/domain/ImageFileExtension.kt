package kr.co.lunatalk.domain.image.domain

import kr.co.lunatalk.global.exception.CustomException
import kr.co.lunatalk.global.exception.ErrorCode

enum class ImageFileExtension(val uploadExtension: String) {
    PNG("png"),
    JPG("jpg"),
    JPEG("jpeg"),
    WEBP("webp");

    companion object {
        fun of(extension: String): ImageFileExtension {
            return entries.firstOrNull { it.uploadExtension == extension }
                ?: throw CustomException(ErrorCode.IMAGE_FILE_EXTENSION_NOT_FOUND)
        }
    }
}

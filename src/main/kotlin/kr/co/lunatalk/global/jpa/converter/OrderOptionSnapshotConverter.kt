package kr.co.lunatalk.global.jpa.converter

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kr.co.lunatalk.domain.order.domain.OptionSnapshot
import kr.co.lunatalk.global.exception.CustomException
import kr.co.lunatalk.global.exception.ErrorCode

@Converter(autoApply = true)
class OrderOptionSnapshotConverter : AttributeConverter<OptionSnapshot, String> {

    companion object {
        private val objectMapper = ObjectMapper()
    }

    override fun convertToDatabaseColumn(attribute: OptionSnapshot?): String? {
        return try {
            objectMapper.writeValueAsString(attribute)
        } catch (e: Exception) {
            throw CustomException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
    }

    override fun convertToEntityAttribute(dbData: String?): OptionSnapshot? {
        return try {
            dbData?.let { objectMapper.readValue(it, OptionSnapshot::class.java) }
        } catch (e: Exception) {
            throw CustomException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
    }
}

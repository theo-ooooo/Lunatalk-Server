package kr.co.lunatalk.global.jpa.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import kr.co.lunatalk.domain.order.domain.OptionSnapshot;
import kr.co.lunatalk.global.exception.CustomException;
import kr.co.lunatalk.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

@Converter(autoApply = true)
@RequiredArgsConstructor
public class OrderOptionSnapshotConverter implements AttributeConverter<OptionSnapshot, String> {
	private static final ObjectMapper objectMapper = new ObjectMapper();
	@Override
	public String convertToDatabaseColumn(OptionSnapshot attribute) {
		try {
			return objectMapper.writeValueAsString(attribute);
		}catch(JsonProcessingException e) {
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}

	@Override
	public OptionSnapshot convertToEntityAttribute(String dbData) {
		try {
			return objectMapper.readValue(dbData, OptionSnapshot.class);
		}catch (IOException e) {
			throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
		}
	}
}

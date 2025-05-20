package kr.co.lunatalk.domain.image.domain;

import jakarta.persistence.*;
import kr.co.lunatalk.domain.common.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Image extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "image_id")
	private Long id;

	@Enumerated(EnumType.STRING)
	private ImageType imageType;

	// 연관 id
	private Long referenceId;

	@Column(length = 36)
	private String imageKey;

	private String imagePath;

	private Integer imageOrder;

	@Enumerated(EnumType.STRING)
	private ImageFileExtension imageFileExtension;

	@Enumerated(EnumType.STRING)
	private ImageStatus imageStatus;


	@Builder(access = AccessLevel.PRIVATE)
	public Image(ImageType imageType, Long referenceId, String imageKey, String imagePath, ImageFileExtension imageFileExtension, Integer order) {
		this.imageType = imageType;
		this.referenceId = referenceId;
		this.imageKey = imageKey;
		this.imagePath = imagePath;
		this.imageFileExtension = imageFileExtension;
		this.imageStatus = ImageStatus.PENDING;
		this.imageOrder = order;
	}

	public static Image createImage(ImageType imageType, Long referenceId, String imageKey, String imagePath, ImageFileExtension imageFileExtension, Integer order) {
		return Image.builder()
			.imageType(imageType)
			.referenceId(referenceId)
			.imageKey(imageKey)
			.imagePath(imagePath)
			.imageFileExtension(imageFileExtension)
			.order(order)
			.build();
	}

	public void uploadedImage() {
		this.imageStatus = ImageStatus.COMPLETED;
	}

	public void deletedImage() {
		this.imageStatus = ImageStatus.DELETED;
	}
}

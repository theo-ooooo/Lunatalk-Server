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

	@Enumerated(EnumType.STRING)
	private ImageFileExtension imageFileExtension;

	@Enumerated(EnumType.STRING)
	private ImageStatus imageStatus;


	@Builder(access = AccessLevel.PRIVATE)
	public Image(ImageType imageType, Long referenceId, String imageKey, String imagePath, ImageFileExtension imageFileExtension) {
		this.imageType = imageType;
		this.referenceId = referenceId;
		this.imageKey = imageKey;
		this.imagePath = imagePath;
		this.imageFileExtension = imageFileExtension;
		this.imageStatus = ImageStatus.PENDING;
	}

	public static Image createImage(ImageType imageType, Long referenceId, String imageKey, String imagePath, ImageFileExtension imageFileExtension) {
		return Image.builder()
			.imageType(imageType)
			.referenceId(referenceId)
			.imageKey(imageKey)
			.imagePath(imagePath)
			.imageFileExtension(imageFileExtension)
			.build();
	}

	public void uploadedImage() {
		this.imageStatus = ImageStatus.COMPLETED;
	}

	public void deletedImage() {
		this.imageStatus = ImageStatus.DELETED;
	}
}

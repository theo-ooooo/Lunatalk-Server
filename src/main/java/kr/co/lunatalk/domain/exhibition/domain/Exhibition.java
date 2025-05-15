package kr.co.lunatalk.domain.exhibition.domain;

import jakarta.persistence.*;
import kr.co.lunatalk.domain.common.domain.BaseTimeEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Exhibition extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String title;
	private String description;

	@Enumerated(EnumType.STRING)
	@ColumnDefault("'VISIBLE'")
	private ExhibitionVisibility visibility;

	@OneToMany(mappedBy = "exhibition", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<ExhibitionProduct> exhibitionProducts = new ArrayList<>();

	@Column(nullable = false)
	private LocalDateTime startAt;
	private LocalDateTime endAt;


	@Builder
	public Exhibition(String title, String description, ExhibitionVisibility visibility, LocalDateTime startAt, LocalDateTime endAt) {
		this.title = title;
		this.description = description;
		this.visibility = visibility;
		this.startAt = startAt;
		this.endAt = endAt;
	}

	public static Exhibition createExhibition(String title, String description, ExhibitionVisibility visibility, LocalDateTime startAt, LocalDateTime endAt) {
		return Exhibition.builder()
			.title(title)
			.description(description)
			.visibility(visibility)
			.startAt(startAt)
			.endAt(endAt)
			.build();
	}

	public void updateExhibition(String title, String description, ExhibitionVisibility visibility, LocalDateTime startAt, LocalDateTime endAt) {
		this.title = title;
		this.description = description;
		this.visibility = visibility == null ? ExhibitionVisibility.HIDDEN : visibility;
		this.startAt = startAt;
		this.endAt = endAt;
	}

	public void addProducts(List<ExhibitionProduct> products) {
		this.exhibitionProducts.addAll(products);
	}

	public void addProduct(ExhibitionProduct product) {
		this.exhibitionProducts.add(product);
	}
}

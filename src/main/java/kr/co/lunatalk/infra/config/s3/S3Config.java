package kr.co.lunatalk.infra.config.s3;


import kr.co.lunatalk.global.util.SpringEnvironmentUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class S3Config {

	private final S3Properties properties;
	private final SpringEnvironmentUtil springEnvironmentUtil;


	@Bean
	public S3Client s3Client() {
		log.info("S3Properties : {}", properties);
		S3ClientBuilder builder = S3Client.builder().region(Region.of(properties.region()));

		if(springEnvironmentUtil.getLocalProfile()) {
			builder.credentialsProvider(ProfileCredentialsProvider.create(properties.profile()));
		} else {
			builder.credentialsProvider(DefaultCredentialsProvider.create());
		}

		return builder.build();
	}

	@Bean
	public S3Presigner s3Presigner() {
		S3Presigner.Builder builder = S3Presigner.builder().region(Region.of(properties.region()));

		if(springEnvironmentUtil.getLocalProfile()) {
			builder.credentialsProvider(ProfileCredentialsProvider.create(properties.profile()));
		} else {
			builder.credentialsProvider(DefaultCredentialsProvider.create());
		}

		return builder.build();
	}

}

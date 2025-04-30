package kr.co.lunatalk.infra.config.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "aws")
public record S3Properties(String profile, String bucket, String region, String endpoint) {

}

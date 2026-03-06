plugins {
    id("org.jetbrains.kotlin.kapt")
}

dependencies {
    api(project(":lunatalk-core"))

    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework:spring-web")

    // Swagger (DTOs use @Schema)
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.2")

    // JWT
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
    implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")

    // AWS S3
    implementation(platform("software.amazon.awssdk:bom:2.20.56"))
    implementation("software.amazon.awssdk:s3")

    // Configuration Processor
    kapt("org.springframework.boot:spring-boot-configuration-processor")

    // Test
    testImplementation(project(":lunatalk-infra"))
    testImplementation("org.springframework.boot:spring-boot-starter-mail")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("com.navercorp.fixturemonkey:fixture-monkey-starter:1.1.11")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
}

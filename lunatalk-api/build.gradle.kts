plugins {
    id("org.springframework.boot")
}

dependencies {
    implementation(project(":lunatalk-common"))
    implementation(project(":lunatalk-core"))
    implementation(project(":lunatalk-domain"))
    implementation(project(":lunatalk-infra"))

    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Spring (for Pageable, Page, exception handling)
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework:spring-tx")

    // Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:3.0.2")

    // Monitoring
    runtimeOnly("io.micrometer:micrometer-registry-prometheus")

    // Test
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("com.navercorp.fixturemonkey:fixture-monkey-starter:1.1.11")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.4.0")
}

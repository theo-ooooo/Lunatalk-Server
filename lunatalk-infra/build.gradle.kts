dependencies {
    implementation(project(":lunatalk-common"))
    implementation(project(":lunatalk-core"))
    implementation(project(":lunatalk-domain"))

    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-mail")
    implementation("org.springframework:spring-web")

    // QueryDSL
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")

    // AWS S3
    implementation(platform("software.amazon.awssdk:bom:2.20.56"))
    implementation("software.amazon.awssdk:s3")
}

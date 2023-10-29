plugins {
    id("java")
    id("org.springframework.boot") version "3.1.4"
    id("org.liquibase.gradle") version "2.2.0"
}

group = "com.mashanlote"

repositories {
    mavenCentral()
}

tasks.jar {
    manifest.attributes["Main-Class"] = "com.mashanlote.WeatherApplication"
    val dependencies = configurations
            .runtimeClasspath
            .get()
            .map(::zipTree)
    from(dependencies)
    archiveBaseName.set("WeatherApp")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

dependencies {
    implementation("org.projectlombok:lombok:1.18.28")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.1.4")
    implementation("org.springframework.boot:spring-boot-starter-jdbc:3.1.4")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("org.springframework.boot:spring-boot-testcontainers:3.1.4")
    testImplementation("org.testcontainers:junit-jupiter:1.19.1")
    testImplementation("org.springframework.boot:spring-boot-starter-test:3.1.4")
    testImplementation("org.assertj:assertj-core:3.6.1")
    compileOnly("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.28")
    implementation("org.springframework.boot:spring-boot-starter-web:3.1.4")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")
    implementation("io.github.resilience4j:resilience4j-spring-boot3:2.0.2")
    implementation("io.github.resilience4j:resilience4j-ratelimiter:2.0.2")
    implementation("org.springframework.boot:spring-boot-starter-aop:3.1.2")
    runtimeOnly("com.h2database:h2:2.2.224")
    liquibaseRuntime("org.liquibase:liquibase-core:4.24.0")
    runtimeOnly("org.liquibase:liquibase-core:4.24.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

plugins {
    id("java")
    id("org.springframework.boot") version "3.1.4"
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
    compileOnly("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.28")
    implementation("org.springframework.boot:spring-boot-starter-web:3.1.4")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.15.2")
}

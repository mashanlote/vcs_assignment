plugins {
    id("java")
}

group = "com.mashanlote"

repositories {
    mavenCentral()
}

tasks.jar {
    manifest.attributes["Main-Class"] = "Weather"
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
}

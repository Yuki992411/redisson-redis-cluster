plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // Redisson
    implementation("org.redisson:redisson-spring-boot-starter:3.24.3")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

// infrastructureモジュールはライブラリとして使用されるため、実行可能JARを生成しない
tasks.getByName<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.getByName<Jar>("jar") {
    enabled = true
}

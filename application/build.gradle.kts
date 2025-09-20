plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    implementation(project(":infrastructure"))

    // Spring Boot Web
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Redisson
    implementation("org.redisson:redisson-spring-boot-starter:3.24.3")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

springBoot {
    mainClass.set("com.example.yukikom.redisson_redis_cluster.application.RedissonRedisClusterApplicationKt")
}

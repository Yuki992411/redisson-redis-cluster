import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension
import io.gitlab.arturbosch.detekt.extensions.DetektExtension

plugins {
    kotlin("jvm") version "2.0.21" apply false
    kotlin("plugin.spring") version "2.0.21" apply false
    id("org.springframework.boot") version "3.5.5" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("org.jlleitschuh.gradle.ktlint") version "12.1.2" apply false
    id("io.gitlab.arturbosch.detekt") version "1.23.8" apply false
}

// Version catalog for use outside plugins block
object Versions {
    const val KTLINT = "1.7.1"
    const val DETEKT = "1.23.8"
}

// Apply ktlint to root project for Git hooks
apply(plugin = "org.jlleitschuh.gradle.ktlint")

configure<KtlintExtension> {
    version.set(Versions.KTLINT)
    verbose.set(true)
    android.set(false)
    outputToConsole.set(true)
    outputColorName.set("RED")
    ignoreFailures.set(false)
}

allprojects {
    group = "com.example.yukikom"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "io.gitlab.arturbosch.detekt")

    configure<KtlintExtension> {
        version.set(Versions.KTLINT)
        verbose.set(true)
        android.set(false)
        outputToConsole.set(true)
        outputColorName.set("RED")
        ignoreFailures.set(false)
        enableExperimentalRules.set(false)
        additionalEditorconfig.set(
            mapOf(
                "insert_final_newline" to "true"
            )
        )
        filter {
            exclude("**/generated/**")
            include("**/kotlin/**")
        }
    }

    configure<DetektExtension> {
        toolVersion = Versions.DETEKT
        buildUponDefaultConfig = true
        allRules = false
        ignoreFailures = true // Allow build to pass while showing violations
        parallel = true
        // Use default configuration (no custom config file)
    }

    configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    tasks.withType<KotlinCompile> {
        compilerOptions {
            freeCompilerArgs.addAll("-Xjsr305=strict")
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }

    dependencies {
        "implementation"("org.jetbrains.kotlin:kotlin-reflect")
        "implementation"("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
        "testImplementation"("org.jetbrains.kotlin:kotlin-test-junit5")
        "testRuntimeOnly"("org.junit.platform:junit-platform-launcher")

        "detektPlugins"("io.gitlab.arturbosch.detekt:detekt-formatting:${Versions.DETEKT}")
    }
}

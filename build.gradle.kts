plugins {
    kotlin("jvm") version "2.3.21"
    kotlin("plugin.spring") version "2.3.21"
    id("org.springframework.boot") version "4.1.2"
    id("io.spring.dependency-management") version "1.1.7"

    // ドキュメント生成 (Dokka)
    id("org.jetbrains.dokka") version "2.0.0"

    // 静的解析 & コードフォーマット
    id("com.diffplug.spotless") version "7.0.2"
}

group = "net.konohana.sakuya.sandbox"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("tools.jackson.module:jackson-module-kotlin")

    // JSpecify 1.0 (標準 Null 仕様)
    implementation("org.jspecify:jspecify:1.0.0")

    // JetBrains Annotations (@Contract, pure=true 用)
    compileOnly("org.jetbrains:annotations:26.0.2")

    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll(
            listOf(
                "-Xjsr305=strict",
                "-Xannotation-default-target=param-property"
            )
        )
    }
}

dokka {
    moduleName.set("jspecify-spring-sandbox")
    dokkaPublications.html {
        outputDirectory.set(layout.buildDirectory.dir("dokka/html"))
    }
}

// Spotless 設定 (Java と Kotlin のスタイルを統一)
spotless {
    kotlin {
        target("src/**/*.kt")
        ktlint()
        trimTrailingWhitespace()
        endWithNewline()
    }
    java {
        target("src/**/*.java")
        googleJavaFormat()
        removeUnusedImports()
        trimTrailingWhitespace()
        endWithNewline()
    }
}

tasks.named<org.gradle.api.tasks.testing.Test>("test") {
    useJUnitPlatform()
}

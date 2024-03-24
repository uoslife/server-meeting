import org.asciidoctor.gradle.jvm.AsciidoctorTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.5"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.asciidoctor.jvm.convert") version "4.0.2"
    id("com.diffplug.spotless") version "6.11.0"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
    kotlin("plugin.jpa") version "1.7.22"
    kotlin("kapt") version "1.7.10" // querydsl
    id("jacoco") // jacoco 플러그인 추가
    id("org.jetbrains.dokka") version "1.9.10" // dokka 플러그인 추가
    id("com.epages.restdocs-api-spec") version "0.18.2"

}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.Embeddable")
    annotation("jakarta.persistence.MappedSuperclass")
}

noArg {
    annotation("javax.persistence.Entity")
}

group = "uoslife"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

val snippetsDir by extra { file("build/generated-snippets") }


dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("com.h2database:h2")

    // validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // flyway
    implementation("org.flywaydb:flyway-core")

    // swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.4")

    // querydsl
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
    kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")

    implementation(platform("org.testcontainers:testcontainers-bom:1.19.5")) //import bom

    // spring security
    implementation("org.springframework.boot:spring-boot-starter-security")
    testImplementation("org.springframework.security:spring-security-test")

    // h2
    testImplementation("com.h2database:h2")

    // test
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // kotest
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    testImplementation("io.kotest.extensions:kotest-extensions-spring:1.1.3")
    testImplementation("io.mockk:mockk:1.13.5")
    testImplementation("io.kotest.extensions:kotest-extensions-testcontainers:2.0.2")
    testImplementation("org.testcontainers:postgresql")

    //spring-restdocs
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-asciidoctor")

    // hibernate annotation
    implementation("com.vladmihalcea:hibernate-types-60:2.21.1")

    // dokka
    dokkaPlugin("org.jetbrains.dokka:mathjax-plugin:1.9.10")

    // mail
    implementation("org.springframework.boot:spring-boot-starter-mail")

    // reids
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    // amazon aws
    implementation ("com.amazonaws:aws-java-sdk-ses:1.12.3")

    // jwt
    compileOnly("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
    testImplementation("io.jsonwebtoken:jjwt-jackson:0.11.5")
    testImplementation("io.jsonwebtoken:jjwt-impl:0.11.5")
    testImplementation("io.jsonwebtoken:jjwt-api:0.11.5")
}


tasks {
    withType<AsciidoctorTask> {
        inputs.dir(snippetsDir)
        dependsOn("test")
    }
}
tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

openapi3 {
    setServer("http://localhost:8080")
    title = "UOSLIFE Meeting API"
    description = "UOSLIFE Meeting API Documentation"
    version = "v0.0.1"
    format = "yaml"
    outputDirectory = "src/main/resources/static/api-spec"
}

tasks.named<Jar>("jar") {
    enabled = false
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy("jacocoTestReport")
}

spotless {
    kotlin {
        ktfmt().kotlinlangStyle()
    }
}

tasks.jacocoTestReport {
    dependsOn(tasks.named("spotlessKotlin"))
    executionData(fileTree(project.rootDir.absolutePath).include("**/build/jacoco/*.exec"))

    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(false)
    }
}

tasks.test {
    outputs.dir(snippetsDir)
}

asciidoctorj {

}

tasks.withType<org.jetbrains.dokka.gradle.DokkaTask> {
    dokkaSourceSets {
        outputDirectory.set(file("build/dokka"))

        named("main") {
            displayName.set("Main")
            includes.from("src/main/kotlin")
        }
    }
}


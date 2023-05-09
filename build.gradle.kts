import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "3.0.5"
    id("io.spring.dependency-management") version "1.1.0"
    id("org.asciidoctor.convert") version "2.4.0"
    kotlin("jvm") version "1.7.22"
    kotlin("plugin.spring") version "1.7.22"
    kotlin("plugin.jpa") version "1.7.22"
    kotlin("kapt") version "1.7.10" //querydsl
    id("org.jlleitschuh.gradle.ktlint") version "10.3.0" //ktlint
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
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.postgresql:postgresql")

    // validation
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // flyway
    implementation("org.flywaydb:flyway-core")

    // swagger
    implementation("org.springdoc:springdoc-openapi-ui:1.6.15")
    implementation("org.springdoc:springdoc-openapi-kotlin:1.6.15")

    // auth0
//    implementation("com.auth0:auth0-spring-security-api:1.5.2")
//    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")

    // jwt
//    compileOnly("io.jsonwebtoken:jjwt-api:0.11.5")
//    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
//    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")

    //querydsl
    implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
    kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")

    // spring security
//    implementation("org.springframework.boot:spring-boot-starter-security")
//    implementation("org.springframework.security:spring-security-oauth2-resource-server")
//    implementation("org.springframework.security:spring-security-oauth2-jose")
//    implementation("org.springframework.security:spring-security-config")
//    testImplementation("org.springframework.security:spring-security-test")

    // testcontainers
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql:1.18.0")

    // test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.kotest.extensions:kotest-extensions-testcontainers:1.3.4")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")

    // kotest
    testImplementation("io.kotest:kotest-runner-junit5:5.5.5")
    testImplementation("io.kotest:kotest-assertions-core:5.5.5")
    testImplementation("io.kotest:kotest-extensions-spring:4.4.3")
}

dependencyManagement {
    imports {
        mavenBom("org.testcontainers:testcontainers-bom:1.18.0")
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "17"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.test {
    outputs.dir(snippetsDir)
}

tasks.asciidoctor {
    inputs.dir(snippetsDir)
    dependsOn(tasks.test)
}
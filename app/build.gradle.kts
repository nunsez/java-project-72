import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    java
    application
    checkstyle
    jacoco
    alias(libs.plugins.jte)
}

group = "hexlet.code"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

application {
    mainClass = "hexlet.code.App"
}

java {
    toolchain {
        version = JavaLanguageVersion.of(21)
    }
}

jte {
    binaryStaticContent = true
    generate()
}

dependencies {
    implementation(libs.bundles.javalin)
    implementation(libs.jte)
    implementation(libs.slf4j.simple)
    implementation(libs.hikariCP)
    implementation(libs.h2database)
    implementation(libs.postgresql)

    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
    testImplementation(libs.assertj.core)
}

// Set UTF-8 encoding
tasks {
    withType<JavaCompile>().configureEach { options.encoding = "UTF-8" }
    withType<JavaExec>().configureEach { defaultCharacterEncoding = "UTF-8" }
    withType<Javadoc>().configureEach { options.encoding = "UTF-8" }
    withType<Test>().configureEach { defaultCharacterEncoding = "UTF-8" }
}

tasks.jar {
    dependsOn(tasks.precompileJte)
    from(fileTree("build/generated-sources/jte")) {
        include("**/*.class")
        include("**/*.bin")
    }
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
        events(TestLogEvent.FAILED, TestLogEvent.SKIPPED)
        showStandardStreams = true
    }
}

tasks.jacocoTestReport {
    dependsOn(tasks.test) // tests are required to run before generating the report

    reports {
        xml.required = true
        html.required = true
        csv.required = false
    }

    val coverageDirs = classDirectories.files.flatMap { dir ->
        val tree = fileTree(dir)
        tree.exclude("**/gg/jte/generated/*")
        tree
    }

    classDirectories.setFrom(coverageDirs)

}

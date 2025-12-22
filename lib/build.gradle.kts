import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    `java-library`
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.detekt)
    alias(libs.plugins.dokka)
}

group = "io.github.evgenius1424"
version = providers.environmentVariable("VERSION")
    .map { it.removePrefix("v") }
    .getOrElse("0.0.1-SNAPSHOT")

repositories {
    mavenCentral()
}

dependencies {
    api(libs.kotlinx.serialization.json)

    testImplementation(kotlin("test"))
    testImplementation(libs.junit.jupiter.engine)
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:${libs.versions.detekt.get()}")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withJavadocJar()
    withSourcesJar()
}

kotlin {
    explicitApi()
    compilerOptions {
        jvmTarget = JvmTarget.JVM_1_8
        apiVersion = KotlinVersion.KOTLIN_1_8
        languageVersion = KotlinVersion.KOTLIN_1_8
        freeCompilerArgs.addAll(
            "-Xjsr305=strict",
            "-opt-in=kotlinx.serialization.ExperimentalSerializationApi"
        )
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

detekt {
    config.setFrom(files("${rootProject.projectDir}/detekt.yml"))
    buildUponDefaultConfig = true
    autoCorrect = true
    parallel = true
}

mavenPublishing {
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()

    coordinates(
        groupId = project.group.toString(),
        artifactId = "kotlin-json-merge-patch",
        version = project.version.toString()
    )

    pom {
        name = "Kotlin JSON Merge Patch"
        description = "A Kotlin library for JSON Merge Patch operations (RFC 7396)"
        url = "https://github.com/evgenius1424/kotlin-json-merge-patch"
        inceptionYear = "2025"

        licenses {
            license {
                name = "The Apache License, Version 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
                distribution = "repo"
            }
        }

        developers {
            developer {
                id = "evgenius1424"
                name = "Evgenii"
                url = "https://github.com/evgenius1424"
            }
        }

        scm {
            url = "https://github.com/evgenius1424/kotlin-json-merge-patch"
            connection = "scm:git:git://github.com/evgenius1424/kotlin-json-merge-patch.git"
            developerConnection = "scm:git:ssh://git@github.com/evgenius1424/kotlin-json-merge-patch.git"
        }

        issueManagement {
            system = "GitHub Issues"
            url = "https://github.com/evgenius1424/kotlin-json-merge-patch/issues"
        }
    }
}

tasks.dokkaGeneratePublicationHtml {
    generator.moduleName.set("Kotlin JSON Merge Patch")
    generator.moduleVersion.set(project.version.toString())
}
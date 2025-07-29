plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	kotlin("plugin.jpa") version "1.9.25"
	id("org.springframework.boot") version "3.5.4"
	id("io.spring.dependency-management") version "1.1.7"
	id("info.solidsoft.pitest") version "1.15.0"
}

group = "de.jkrech.projectradar"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

extra["springAiVersion"] = "1.0.0"

dependencies {
	// Spring
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	//implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	//implementation("org.springframework.boot:spring-boot-starter-data-rest")
	//implementation("org.springframework.boot:spring-boot-starter-security")
	//implementation("org.springframework.boot:spring-boot-starter-actuator")
	annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

	// Kotlin
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

	// AI
	implementation("org.springframework.ai:spring-ai-markdown-document-reader")
	implementation("org.springframework.ai:spring-ai-pdf-document-reader")
	implementation("org.springframework.ai:spring-ai-starter-model-chat-memory")
	implementation("org.springframework.ai:spring-ai-starter-model-openai")

	// Scraping
	implementation("com.microsoft.playwright:playwright:1.54.0")

	// Testing
	testImplementation("org.springframework.boot:spring-boot-starter-test") {
		exclude(group = "org.mockito")
	}
	testImplementation("org.springframework.security:spring-security-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("com.ninja-squad:springmockk:4.0.2")
	testImplementation("org.assertj:assertj-core:3.27.3")
	testImplementation("io.mockk:mockk:1.14.5")
	testImplementation("io.kotest:kotest-assertions-core-jvm:5.9.1")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.ai:spring-ai-bom:${property("springAiVersion")}")
	}
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

// https://github.com/szpak/gradle-pitest-plugin
pitest {
	junit5PluginVersion.set("1.2.1")
	targetClasses.set(setOf("de.jkrech.projectradar.*"))
	excludedClasses.set(setOf("de.jkrech.projectradar.**.*Properties", "de.jkrech.projectradar.**.*Configuration"))
	//excludedTestClasses.set(setOf("de.jkrech.projectradar.**.*IntegrationTest*"))
	//excludedMethods.set(setOf("equals", "hashCode", "toString"))
	mutationThreshold.set(20)
	threads.set(4)
	outputFormats.set(setOf("HTML"))
	timestampedReports.set(false)
	failWhenNoMutations.set(false)
	avoidCallsTo.set(
		setOf(
			"kotlin.jvm.internal",
			"org.slf4j.Logger"
		)
	)
	mutators.set(setOf("DEFAULTS"))
	//jvmArgs.set(
	//    setOf(
	//        "-Xms2048m",
	//        "-Xmx2048m",
	//        "-XX:+HeapDumpOnOutOfMemoryError",
	//        "-Dfile.encoding=UTF-8"
	//    )
	//)
}

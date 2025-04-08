plugins {
	java
	id("org.springframework.boot") version "3.4.4"
	id("io.spring.dependency-management") version "1.1.7"
	id("jacoco")
}

group = "com.eduacsp.walletassignment"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.mapstruct:mapstruct:1.5.5.Final")
	implementation("org.springframework.retry:spring-retry")
	implementation("org.springframework.boot:spring-boot-starter-aop")
	implementation("org.slf4j:slf4j-api:2.0.12")

	annotationProcessor("org.mapstruct:mapstruct-processor:1.5.5.Final")

	runtimeOnly("com.h2database:h2")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
	useJUnitPlatform()
	finalizedBy(tasks.jacocoTestReport)
}

tasks.withType<JavaCompile> {
	options.compilerArgs.add("-Xlint:deprecation")
}

jacoco {
	toolVersion = "0.8.11"
}

tasks.jacocoTestReport {
	dependsOn(tasks.test)

	val excludedPackages = listOf(
		"**/com/eduacsp/walletassignment/model/**",
		"**/com/eduacsp/walletassignment/mapper/**",
		"**/com/eduacsp/walletassignment/domain/**",
		"**/com/eduacsp/walletassignment/enums/**"
	)

	reports {
		xml.required.set(true)
		html.required.set(true)
		csv.required.set(false)
	}

	classDirectories.setFrom(
		files(classDirectories.files.map {
			fileTree(it) {
				exclude(excludedPackages)
			}
		})
	)
}

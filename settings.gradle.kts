pluginManagement {
	repositories {
		mavenCentral()
		gradlePluginPortal()
		maven("https://maven.fabricmc.net/")
		maven("https://maven.kikugie.dev/releases")
		maven("https://maven.kikugie.dev/snapshots")
	}
}

plugins {
	id("dev.kikugie.stonecutter") version "0.9.5"
}

stonecutter {
	kotlinController = true

	create(rootProject) {
		version("26.1").buildscript("build-unobf.gradle.kts")
		version("26.2").buildscript("build-unobf.gradle.kts")

		vcsVersion = "26.1"
	}
}

rootProject.name = "phantom-schedule"

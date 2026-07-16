pluginManagement {
	repositories {
		mavenLocal()
		mavenCentral()
		gradlePluginPortal()
		maven("https://maven.fabricmc.net/") { name = "Fabric" }
		maven("https://maven.neoforged.net/releases/") { name = "NeoForged" }
		maven("https://maven.kikugie.dev/snapshots") { name = "KikuGie Snapshots" }
		maven("https://maven.kikugie.dev/releases") { name = "KikuGie Releases" }
		maven("https://maven.parchmentmc.org") { name = "ParchmentMC" }
		maven("https://maven.terraformersmc.com/") { name = "TerraformersMC" }
		exclusiveContent {
			forRepository { maven("https://api.modrinth.com/maven") { name = "Modrinth" } }
			filter { includeGroup("maven.modrinth") }
		}
	}
	includeBuild("build-logic")
}

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
	id("dev.kikugie.stonecutter") version "0.9.4"
}

stonecutter {
	create(rootProject) {
		fun match(version: String, vararg loaders: String) =
			loaders.forEach {
				val isGitHub = System.getenv("GITHUB_ACTIONS") == "true"
				if (isGitHub && version == "1.20.1" && it == "forge") { //skip forge 1.20.1 on github bc it keeps failing
					return@forEach
				}
				version("$version-$it", version).buildscript = if (version >= "26.1" && it == "fabric") {
					"build.$it" + "-unobf.gradle.kts"
				} else {
					"build.$it.gradle.kts"
				}
			}
		match("1.20.1","fabric","forge")
		match("1.21.1", "fabric","neoforge")
		match("1.21.2", "fabric")
		match("1.21.4", "fabric")
		match("1.21.5", "fabric")
		match("1.21.6", "fabric")
		match("1.21.9", "fabric")
		match("1.21.10", "fabric")
		match("1.21.11", "fabric","neoforge")
		match("26.1", "fabric","neoforge")
		match("26.2", "fabric","neoforge")
		vcsVersion = "1.21.1-fabric"
	}
}

plugins {
	id("mod-platform")
//	id("fabric-loom")
	id("net.fabricmc.fabric-loom")
}

platform {
	loader = "fabric"
	dependencies {
		required("minecraft") {
			versionRange = ">="+prop("deps.minecraft")
		}
		required("fabric-api") {
			slug("fabric-api")
//			versionRange = ">=${prop("deps.fabric-api")}"
			versionRange = "*"
		}
		required("fabricloader") {
//			versionRange = ">=${libs.fabric.loader.get().version}"
			versionRange = "*"
		}
		optional("modmenu") {}
	}
}

loom {
	runs.named("client") {
		client()
		ideConfigGenerated(true)
		runDir = "run/"
		environment = "client"
		programArgs("--username=Dev")
		configName = "Fabric Client"
	}
	runs.named("server") {
		server()
		ideConfigGenerated(true)
		runDir = "run/"
		environment = "server"
		configName = "Fabric Server"
	}
}

fabricApi {
	configureDataGeneration {
		outputDirectory = file("${rootDir}/versions/datagen/${stonecutter.current.version.split("-")[0]}/src/main/generated")
		client = true
	}
}

repositories {
	mavenCentral()
	strictMaven("https://maven.terraformersmc.com/", "com.terraformersmc") { name = "TerraformersMC" }
	strictMaven("https://api.modrinth.com/maven", "maven.modrinth") { name = "Modrinth" }
}

dependencies {
	minecraft("com.mojang:minecraft:${prop("deps.minecraft")}")
//	dependencies.add("mappings", loom.layered {
//		officialMojangMappings()
//		if (hasProperty("deps.parchment")) parchment("org.parchmentmc.data:parchment-${prop("deps.parchment")}@zip")
//	})
	println("Current version: ${stonecutter.current.version}")
	implementation(libs.fabric.loader)
	implementation(libs.moulberry.mixinconstraints)
	include(libs.moulberry.mixinconstraints)
	implementation("net.fabricmc.fabric-api:fabric-api:${prop("deps.fabric-api")}")
	implementation("com.terraformersmc:modmenu:${prop("deps.modmenu")}")
}


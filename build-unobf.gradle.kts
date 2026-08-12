plugins {
	id("net.fabricmc.fabric-loom")
	`maven-publish`
}

class ModData {
	val id = property("mod.id").toString()
	val name = property("mod.name").toString()
	val version = property("mod.version").toString()
	val group = property("mod.group").toString()
}

class ModDependencies {
	operator fun get(name: String) = property("deps.$name").toString()
}

val mod = ModData()
val deps = ModDependencies()
val projectVersion = stonecutter.current.project
val mcVersion = sc.current.version
val mcDep = property("mod.mc_dep").toString()
val loaderDep = property("mod.loader_dep").toString()
val targetJava = JavaVersion.VERSION_25

version = "${mod.version}+$projectVersion"
group = mod.group

base {
	archivesName.set(mod.id)
}

repositories {
	mavenCentral()
	maven("https://maven.fabricmc.net/")
}

dependencies {
	minecraft("com.mojang:minecraft:$mcVersion")
	implementation("net.fabricmc:fabric-loader:${deps["fabric_loader"]}")
	implementation("net.fabricmc.fabric-api:fabric-api:${deps["fabric_api"]}+$projectVersion")
}

loom {
	runConfigs.all {
		ideConfigGenerated(stonecutter.current.isActive)
		runDir = "../../run"
		programArgs("--offlineDeveloperMode")
	}
}

java {
	withSourcesJar()
	sourceCompatibility = targetJava
	targetCompatibility = targetJava
}

tasks.withType<JavaCompile>().configureEach {
	options.release.set(25)
}

tasks.processResources {
	inputs.property("id", mod.id)
	inputs.property("name", mod.name)
	inputs.property("version", project.version)
	inputs.property("mcdep", mcDep)
	inputs.property("loaderdep", loaderDep)
	inputs.property("java", "$targetJava")

	filesMatching("fabric.mod.json") {
		expand(
			mapOf(
				"id" to mod.id,
				"name" to mod.name,
				"version" to project.version,
				"mcdep" to mcDep,
				"loaderdep" to loaderDep,
				"java" to "$targetJava"
			)
		)
	}
}

tasks.register<Copy>("buildAndCollect") {
	group = "build"
	from(tasks.jar.get().archiveFile)
	into(rootProject.layout.buildDirectory.file("libs/${mod.version}"))
	dependsOn("build")
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			from(components["java"])
		}
	}
}

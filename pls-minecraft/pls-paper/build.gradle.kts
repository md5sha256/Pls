plugins {
    java
    idea
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("xyz.jpenilla.run-paper") version "2.2.2"
}

repositories {
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        url = uri("https://libraries.minecraft.net")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.20.2-R0.1-SNAPSHOT")
    implementation(projects.plsCore)
    compileOnly(projects.plsMinecraft.plsPaperAdapter)
    compileOnly("com.mojang:brigadier:1.1.8")
    runtimeOnly(projects.plsMinecraft.plsPaperAdapter) {
        targetConfiguration = "reobf"
    }
}

val targetJavaVersion = 17
java.toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))


tasks {
    withType(JavaCompile::class) {
        options.release.set(targetJavaVersion)
        options.encoding = Charsets.UTF_8.name()
        options.isFork = true
        options.isDeprecation = true
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
        filesMatching("plugin.yml") {
            expand("version" to project.version)
        }
    }

    assemble {
        dependsOn(shadowJar)
    }

    shadowJar {
        val base = "io.github.md5sha256.pls.libraries"
        relocate("org.spongepowered.configurate", "${base}.configurate")
    }

    runServer {
        // Configure the Minecraft version for our task.
        // This is the only required configuration besides applying the plugin.
        // Your plugin's jar (or shadowJar if present) will be used automatically.
        minecraftVersion("1.20.2")
    }

}

plugins {
    java
    idea
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "io.github.md5sha256"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")
    implementation("org.spongepowered:configurate-jackson:4.1.2")
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

}

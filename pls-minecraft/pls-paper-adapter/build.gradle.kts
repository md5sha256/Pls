plugins {
    java
    idea
    id("io.github.goooler.shadow") version "8.1.8"
    id("io.papermc.paperweight.userdev") version "1.7.1"
}

repositories {
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    paperweight.paperDevBundle("1.21-R0.1-SNAPSHOT") {
        exclude("com.mojang.brigadier")
    }
    implementation(projects.plsCore)
    testImplementation(platform("org.junit:junit-bom:5.10.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

val targetJavaVersion = 21
java.toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))

tasks {
    withType(JavaCompile::class) {
        options.release.set(targetJavaVersion)
        options.encoding = Charsets.UTF_8.name()
        options.isFork = true
        options.isDeprecation = true
    }


    test {
        useJUnitPlatform()
    }
}

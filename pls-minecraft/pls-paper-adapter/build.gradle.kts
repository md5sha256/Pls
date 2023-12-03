
plugins {
    java
    idea
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.papermc.paperweight.userdev") version "1.5.10"
}

repositories {
    maven {
        name = "papermc-repo"
        url = uri("https://repo.papermc.io/repository/maven-public/")
    }
}

dependencies {
    paperweight.paperDevBundle("1.20.2-R0.1-SNAPSHOT") {
        exclude("com.mojang.brigadier")
    }
    implementation(projects.plsCore)
    testImplementation(platform("org.junit:junit-bom:5.10.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
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
    assemble {
        dependsOn(reobfJar)
    }

    named("reobfJar", io.papermc.paperweight.tasks.RemapJar::class) {
        outputJar.set(file("build/libs/${project.name}-${project.version}.jar"))
    }

    test {
        useJUnitPlatform()
    }

}

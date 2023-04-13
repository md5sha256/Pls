plugins {
    java
    `java-library`
    idea
    eclipse
}

group = "io.github.md5sha256"
version = "1.0-SNAPSHOT"

subprojects {

    group = rootProject.group
    version = rootProject.version

    apply {
        plugin<JavaPlugin>()
        plugin<JavaLibraryPlugin>()
        plugin<IdeaPlugin>()
        plugin<EclipsePlugin>()
    }

    repositories {
        mavenCentral()
        maven("https://oss.sonatype.org/content/groups/public/")
    }

    java.toolchain.languageVersion.set(JavaLanguageVersion.of(17))

    tasks {
        withType(JavaCompile::class) {
            options.release.set(17)
            options.encoding = Charsets.UTF_8.name()
            options.isFork = true
            options.isDeprecation = true
        }

        withType(Javadoc::class) {
            options.encoding = Charsets.UTF_8.name()
        }

        withType(ProcessResources::class) {
            filteringCharset = Charsets.UTF_8.name()
        }
    }

}

plugins {
    kotlin("jvm") version "1.4.10"
}

group = "dev.siro256"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(kotlin("stdlib"))
}

tasks {
    withType<Jar> {
        from(configurations.compileOnly.get().files.map { if (it.isDirectory) it else zipTree(it) })
        manifest.attributes("Main-Class" to "dev.siro256.evaluate.Evaluate")
    }

    withType<ProcessResources> {
        filteringCharset = "UTF-8"
        from(projectDir) { include("LICENSE", "README.md") }
    }
}

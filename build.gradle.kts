import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    application
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.lumenmc"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    implementation("net.minestom:minestom-snapshots:39d445482f")

    implementation("org.yaml:snakeyaml:2.0")

    implementation("org.slf4j:slf4j-api:2.0.9")
    implementation("org.apache.logging.log4j:log4j-core:2.21.1")
    implementation("org.apache.logging.log4j:log4j-api:2.21.1")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.21.1")
}

tasks.test {
    useJUnitPlatform()
}

application {
    mainClass.set("com.lumenmc.server.LumenServer")
}

tasks.withType<ShadowJar> {
    archiveBaseName.set("LumenServer")
    archiveClassifier.set("")
    archiveVersion.set("")
    mergeServiceFiles()
}
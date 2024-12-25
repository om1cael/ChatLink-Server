plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.om1cael"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(platform("org.apache.logging.log4j:log4j-bom:2.24.3"))
    implementation("org.apache.logging.log4j:log4j-api")
    implementation("org.apache.logging.log4j:log4j-core")
}

tasks.shadowJar {
    manifest {
        attributes(
            "Implementation-Title" to "ChatLinkServer",
            "Implementation-Version" to version,
            "Main-Class" to "com.om1cael.server.ChatLinkServer"
        )
    }
}
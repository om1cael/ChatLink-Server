plugins {
    id("java")
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
plugins {
    id("java")
    id("checkstyle")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")

    implementation(project(":common"))
    implementation(project(":paper-platform"))
    implementation(project(":folia-platform"))
}

allprojects {
    apply(plugin = "java")
    apply(plugin = "checkstyle")

    repositories {
        mavenCentral()
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    group = "pw.kaboom"
    version = "master"
    description = "Extras"
    java.sourceCompatibility = JavaVersion.VERSION_17

    tasks {
        assemble {
            dependsOn(checkstyleMain)
        }

        compileJava {
            options.encoding = "UTF-8"
        }
    }
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }
}
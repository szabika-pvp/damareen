plugins {
    java
    application
    id("org.javamodularity.moduleplugin") version "1.8.15"
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("org.beryx.jlink") version "2.25.0"
}

group = "hu.szatomi"
version = "1.0"

repositories {
    mavenCentral()
}

val junitVersion = "5.12.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainModule.set("hu.szatomi.damareen")
    mainClass.set("hu.szatomi.damareen.Main")
}

javafx {
    version = "21.0.6"
    modules = listOf("javafx.controls", "javafx.fxml")
}

dependencies {
    implementation("org.controlsfx:controlsfx:11.2.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")
    implementation("org.slf4j:slf4j-api:2.0.13")
    implementation("org.slf4j:slf4j-simple:2.0.13")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jlink {
    imageZip.set(layout.buildDirectory.file("/distributions/app-${javafx.platform.classifier}.zip"))
    options.set(listOf("--strip-debug", "--compress", "2", "--no-header-files", "--no-man-pages"))
    launcher {
        name = "Damareen"
    }
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "hu.szatomi.damareen.Main"
    }
}

val runtimeLibsDir = layout.buildDirectory.dir("runtimeLibs")

tasks.register("copyRuntimeLibs") {
    // Győződj meg róla, hogy a JavaFX plugined be van állítva
    doLast {
        configurations.runtimeClasspath.get().files.forEach { file ->
            copy {
                from(file)
                into(runtimeLibsDir)
            }
        }
    }
}
// Hozzáadjuk a copyRuntimeLibs taskot a build függőségeihez
tasks.named("build").configure {
    dependsOn("copyRuntimeLibs")
}
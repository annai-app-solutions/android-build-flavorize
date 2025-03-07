import java.util.Properties
import java.io.FileInputStream

val rootDir = "../../"

// Load external gradle.properties (for Sonatype credentials & signing keys)
val externalProperties = Properties()
val externalPropertiesFile = file("$rootDir/annai_app_data/mavencentral/gradle.properties")

if (externalPropertiesFile.exists()) {
    externalProperties.load(FileInputStream(externalPropertiesFile))
    externalProperties.forEach { key, value ->
        project.extensions.extraProperties[key.toString()] = value
    }
}

plugins {
    kotlin("jvm") version "2.1.10"
    `java-gradle-plugin`  // Required for creating Gradle plugins
    `maven-publish`       // For publishing to Maven repositories
    signing               // Required for Maven Central publishing
}

group = "com.annaibrands.studio"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(gradleApi())
    implementation(localGroovy())
    testImplementation(kotlin("test"))
}

gradlePlugin {
    plugins {
        create("flavorizePlugin") {
            id = "com.annaibrands.studio.flavorize"
            implementationClass = "com.annai.flavorize.FlavorizePlugin"
        }
    }
}

java {
    withSourcesJar()
    withJavadocJar()
}

kotlin {
    jvmToolchain(JavaVersion.VERSION_17.ordinal)
}

publishing {
    publications {
        withType<MavenPublication> {
            //from(components["java"])
            groupId = "com.annaibrands.studio"
            artifactId = "flavorize"
            version = "1.0.0"

            pom {
                name.set("Android Build Flavorize Plugin")
                description.set("A Gradle plugin for managing Android build flavors")
                url.set("https://github.com/annai-app-solutions/android-build-flavorize")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("annai")
                        name.set("Annai Development Team")
                        email.set("app@annaibrands.com")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/annai-app-solutions/android-build-flavorize.git")
                    developerConnection.set("scm:git:ssh://github.com/annai-app-solutions/android-build-flavorize.git")
                    url.set("https://github.com/annai-app-solutions/android-build-flavorize")
                }
            }
        }
    }

    repositories {

        //mavenLocal()

        // ✅ Local Maven Repository for Testing
        //maven {
            //name = "localMaven"
            //url = uri("$rootDir/local-maven-repo")
        //}

        // ✅ Maven Central Repository (Sonatype)
        maven {
            name = "MavenCentral"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = findProperty("mavenCentralUsername") as String? ?: ""
                password = findProperty("mavenCentralPassword") as String? ?: ""
            }
        }
    }
}

// ✅ Signing Configuration (Only Sign if Keys Exist)
signing {
    val signingKey = findProperty("signingKey") as String?
    val signingPassword = findProperty("signingPassword") as String?

    if (!signingKey.isNullOrEmpty() && !signingPassword.isNullOrEmpty()) {
        useInMemoryPgpKeys(signingKey, signingPassword)
        sign(publishing.publications["maven"])
    } else {
        println("⚠️ Signing skipped: GPG keys not found in gradle.properties")
    }
}

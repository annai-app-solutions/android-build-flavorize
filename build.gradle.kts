import java.util.Properties
import java.io.FileInputStream
import java.nio.file.Files
import java.nio.file.Paths
import java.security.MessageDigest

val rootDir : String = project.projectDir.resolve("../../../").canonicalPath
val annaiDataDir: String = File(rootDir, "annai_app_data/keys/mavencentral").canonicalPath

// Load external gradle.properties (for Sonatype credentials & signing keys)
val externalProperties = Properties()
val externalPropertiesFile = file("$annaiDataDir/gradle.properties")

if (externalPropertiesFile.exists()) {
    externalProperties.load(FileInputStream(externalPropertiesFile))
    externalProperties.forEach { key, value ->
        project.extensions.extraProperties[key.toString()] = value
    }
} else {
    println("⚠️ Jetbrains gradle.properties not found in ${externalPropertiesFile.canonicalPath}")
}

plugins {
    kotlin("jvm") version "2.1.10"
    id("java")
    `java-gradle-plugin`  // Required for creating Gradle plugins
    `maven-publish`       // For publishing to Maven repositories
    signing               // Required for Maven Central publishing
    id("java-gradle-plugin")
}

group = "com.annaibrands.android"
version = "1.1.1"

repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

dependencies {

    implementation(gradleApi())
    implementation(localGroovy())
    implementation(kotlin("stdlib"))

    implementation("com.android.tools.build:gradle:8.2.0")

    implementation("org.yaml:snakeyaml:2.4")

    testImplementation(kotlin("test"))
}

gradlePlugin {
    plugins {
        create("flavorizePlugin") {
            id = "com.annaibrands.android.flavorize"
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
            groupId = "com.annaibrands.android"
            artifactId = "flavorize"
            version = project.version.toString()

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
    val signingKeyFile = findProperty("signingKeyFile") as String?
    val signingPassword = findProperty("signingPassword") as String?

    if (!signingKeyFile.isNullOrEmpty() && !signingPassword.isNullOrEmpty()) {
        var keyFile = file(signingKeyFile)

        if (!keyFile.exists()) {
            keyFile = File(annaiDataDir, signingKeyFile)
        }

        if (!keyFile.exists()) {
            throw GradleException("❌ Signing key file not found: ${keyFile.absolutePath}")
        }

        val signingKey = keyFile.readText().trim()

        if (signingKey.isBlank()) {
            throw GradleException("❌ Signing key file is empty: ${keyFile.absolutePath}")
        }

        useInMemoryPgpKeys(signingKey, signingPassword)

        publishing.publications.withType<MavenPublication>().configureEach {
            sign(this)
        }
    } else {
        println("⚠️ Signing skipped: Missing signing key file or password in gradle.properties")
    }
}

tasks.register("cleanMavenLocal") {
    group = "publishing"
    description = "Removes existing artifacts from ~/.m2/repository before publishing"

    doLast {
        val groupPath = project.group.toString().replace(".", "/")
        val localMavenPath = Paths.get(System.getProperty("user.home"),
            ".m2", "repository", groupPath, "flavorize", project.version.toString())

        if (Files.exists(localMavenPath)) {
            println("🧹 Removing old artifacts from $localMavenPath")
            localMavenPath.toFile().deleteRecursively()
        } else {
            println("✅ No existing artifacts found in $localMavenPath")
        }
    }
}

// Ensure `cleanMavenLocal` runs **before** publishing
tasks.named("publishToMavenLocal").configure {
    dependsOn("cleanMavenLocal")
}

tasks.withType<Jar> {
    archiveBaseName.set("flavorize")
}

tasks.register("uploadToMavenCentral") {
    group = "publishing"
    description = "Uploads artifacts to Maven Central via Sonatype API"

    dependsOn("jar", "generatePomFileForPluginMavenPublication", "signPluginMavenPublication")

    doLast {
        val groupId = "com.annaibrands.android"
        val artifactId = "flavorize"
        val version = project.version.toString()

        // Ensure correct JAR file name
        val jarFile = layout.buildDirectory.file("libs/$artifactId-$version.jar").get().asFile
        if (!jarFile.exists()) {
            throw GradleException("❌ JAR file not found: ${jarFile.absolutePath}")
        }

        // Ensure correct POM file name
        val pomFile = layout.buildDirectory.file("publications/pluginMaven/pom-default.xml").get().asFile
        if (!pomFile.exists()) {
            throw GradleException("❌ POM file not found: ${pomFile.absolutePath}")
        }

        // Retrieve Bearer Token from gradle.properties
        val mavenBearerToken = findProperty("mavenBearerToken") as String? ?: ""
        if (mavenBearerToken.isEmpty()) {
            throw GradleException("❌ Bearer Token not found in gradle.properties (key: mavenBearerToken)")
        }

        val uploadUrl = "https://central.sonatype.com/api/v1/publisher/upload"

        println("🚀 Uploading JAR and POM to Sonatype...")

        try {
            val process = ProcessBuilder(
                "curl", "-X", "POST", uploadUrl,
                "-H", "Authorization: Bearer $mavenBearerToken",
                "-H", "Content-Type: multipart/form-data",
                "-F", "file=@${jarFile.absolutePath}",
                "-F", "file=@${pomFile.absolutePath}",
                "-F", "groupId=$groupId",
                "-F", "artifactId=$artifactId",
                "-F", "version=$version",
                "-F", "extension=jar"
            )
                .redirectErrorStream(true)
                .start()

            val output = process.inputStream.bufferedReader().readText()
            process.waitFor()

            println("📦 Sonatype Response:\n$output")

            if (process.exitValue() != 0 || !output.contains("\"httpStatus\":201")) {
                throw GradleException("❌ Upload failed! See the error above.")
            } else {
                println("✅ Upload successful!")
            }

        } catch (e: Exception) {
            throw GradleException("❌ Error executing cURL command: ${e.message}", e)
        }
    }
}

tasks.register("bundleForSonatypeUpload") {
    group = "publishing"
    description = "Packages JAR, POM, signatures, sources, and Javadocs into a single bundle for Sonatype upload"

    val artifactId = "flavorize"
    val version = project.version.toString()
    val groupPath = "com/annaibrands/android/$artifactId/$version"

    val buildDirPath = layout.buildDirectory.dir("libs").get().asFile
    val publicationsDir = layout.buildDirectory.dir("publications/pluginMaven").get().asFile

    val pluginJar = buildDirPath.resolve("${artifactId}-$version.jar")
    val pomFile = publicationsDir.resolve("pom-default.xml")
    val sourcesJar = buildDirPath.resolve("${artifactId}-$version-sources.jar")
    val javadocJar = buildDirPath.resolve("${artifactId}-$version-javadoc.jar")

    val jarSignature = buildDirPath.resolve("${artifactId}-$version.jar.asc")
    val pomSignature = publicationsDir.resolve("pom-default.xml.asc")
    val sourcesSignature = buildDirPath.resolve("${artifactId}-$version-sources.jar.asc")
    val javadocSignature = buildDirPath.resolve("${artifactId}-$version-javadoc.jar.asc")

    val jarMd5 = buildDirPath.resolve("${artifactId}-$version.jar.md5")
    val pomMd5 = buildDirPath.resolve("${artifactId}-$version.pom.md5")
    val jarSha1 = buildDirPath.resolve("${artifactId}-$version.jar.sha1")
    val pomSha1 = buildDirPath.resolve("${artifactId}-$version.pom.sha1")

    val bundleZip = buildDirPath.resolve("${artifactId}-$version-bundle.zip")

    dependsOn("build", "jar", "sourcesJar", "javadocJar", "signPluginMavenPublication")

    doFirst {
        if (!pluginJar.exists() || !pomFile.exists() || !sourcesJar.exists() || !javadocJar.exists()) {
            throw GradleException("❌ Missing required files! Ensure JAR, POM, sources, and Javadoc exist.")
        }

        println("🔹 Moving files to correct Maven structure...")
        val destinationDir = buildDirPath.resolve(groupPath).apply { mkdirs() }

        pluginJar.copyTo(destinationDir.resolve("${artifactId}-$version.jar"), overwrite = true)
        pomFile.copyTo(destinationDir.resolve("${artifactId}-$version.pom"), overwrite = true)
        sourcesJar.copyTo(destinationDir.resolve("${artifactId}-$version-sources.jar"), overwrite = true)
        javadocJar.copyTo(destinationDir.resolve("${artifactId}-$version-javadoc.jar"), overwrite = true)

        jarSignature.copyTo(destinationDir.resolve("${artifactId}-$version.jar.asc"), overwrite = true)
        pomSignature.copyTo(destinationDir.resolve("${artifactId}-$version.pom.asc"), overwrite = true)
        sourcesSignature.copyTo(destinationDir.resolve("${artifactId}-$version-sources.jar.asc"), overwrite = true)
        javadocSignature.copyTo(destinationDir.resolve("${artifactId}-$version-javadoc.jar.asc"), overwrite = true)

        println("✅ Files moved successfully.")
    }

    doLast {
        println("🔹 Generating MD5 and SHA1 checksums...")

        fun generateChecksum(file: File, algorithm: String): String {
            val messageDigest = MessageDigest.getInstance(algorithm)
            file.inputStream().use { stream ->
                val buffer = ByteArray(1024)
                var bytesRead: Int
                while (stream.read(buffer).also { bytesRead = it } != -1) {
                    messageDigest.update(buffer, 0, bytesRead)
                }
            }
            return messageDigest.digest().joinToString("") { "%02x".format(it) }
        }

        fun writeChecksum(file: File, checksumFile: File, algorithm: String) {
            checksumFile.writeText(generateChecksum(file, algorithm))
        }

        writeChecksum(pluginJar, jarMd5, "MD5")
        writeChecksum(pluginJar, jarSha1, "SHA-1")
        writeChecksum(pomFile, pomMd5, "MD5")
        writeChecksum(pomFile, pomSha1, "SHA-1")

        println("✅ Checksums generated.")

        println("🔹 Creating ZIP bundle for upload...")
        ant.invokeMethod("zip", mapOf(
            "destfile" to bundleZip.absolutePath,
            "basedir" to buildDirPath.resolve(groupPath).absolutePath
        ))

        if (!bundleZip.exists()) {
            throw GradleException("❌ Failed to create bundle ZIP!")
        }

        println("✅ Bundle ZIP created: ${bundleZip.absolutePath}")
    }
}

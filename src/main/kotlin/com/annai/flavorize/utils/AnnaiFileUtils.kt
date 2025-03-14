package com.annai.flavorize.utils

import com.annai.flavorize.data.spec.app.AnnaiAppData
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.LoaderOptions
import org.yaml.snakeyaml.TypeDescription
import org.yaml.snakeyaml.Yaml
import org.yaml.snakeyaml.constructor.Constructor
import java.io.*
import java.nio.charset.StandardCharsets

class AnnaiFileUtils {

    companion object {

        // ✅ Generic function to load YAML into a specified data class
        fun <T> loadYaml(path: String, type: Class<T>): T {
            FileInputStream(File(path)).use { inputStream ->
                return loadYaml(inputStream, type)
            }
        }

        // ✅ Overloaded function to load YAML from an InputStream
        fun <T> loadYaml(inputStream: InputStream, type: Class<T>): T {
            val loaderOptions = LoaderOptions() // ✅ Required for SnakeYAML 2.x
            val yaml = Yaml(Constructor(type, loaderOptions)) // ✅ Fix: Use LoaderOptions
            return yaml.load(inputStream)
        }

        // ✅ Write YAML File using SnakeYAML
        //fun <T> writeYaml(path: String, data: T) {
        //    FileOutputStream(File(path)).use { outputStream ->
        //        outputStream.write(yaml.dump(data).toByteArray(StandardCharsets.UTF_8))
        //    }
        //}

        // ✅ Create Directory If It Doesn't Exist
        fun assureDirectory(file: File) {
            file.parentFile?.takeIf { !it.exists() }?.mkdirs()
        }

        // ✅ Safe Delete File
        fun delete(file: File) {
            file.takeIf { it.exists() }?.delete()
        }

        // ✅ Safe Recursive Delete (For Directories)
        fun deleteRecursively(dir: File) {
            if (dir.exists()) {
                dir.deleteRecursively()
            }
        }

        fun runShellScript(scriptFile: File, homeDirectory: File): String {
            if (!scriptFile.exists()) {
                throw IOException("❌ Script file not found: ${scriptFile.absolutePath}")
            }

            val processBuilder = ProcessBuilder().apply {
                directory(homeDirectory)
                command(
                    if (isWindows()) listOf("cmd", "/c", scriptFile.absolutePath)
                    else listOf("bash", scriptFile.absolutePath)
                )
                redirectErrorStream(true) // ✅ Merge stdout & stderr
            }

            return try {
                val process = processBuilder.start()
                val output = process.inputStream.bufferedReader().use { it.readText() }
                process.waitFor()
                output
            } catch (e: Exception) {
                throw IOException("❌ Error running script: ${e.message}", e)
            }
        }

        private fun isWindows(): Boolean {
            return System.getProperty("os.name").contains("Windows", ignoreCase = true)
        }
    }
}

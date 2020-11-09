package dev.siro256.evaluate

import java.io.File
import java.lang.StringBuilder
import java.nio.charset.Charset
import kotlin.math.abs

object Evaluate {
    lateinit var maybeFile: File
    val output = StringBuilder()

    @JvmStatic
    fun main(args: Array<String>) {
        maybeFile = File(args[0])

        evaluate(maybeFile.listFiles())

        val outputDirectory: File = if (maybeFile.isDirectory) maybeFile else maybeFile.parentFile

        File(outputDirectory, "evaluate.txt").writeText(output.toString(), Charsets.UTF_8)
    }

    private fun evaluate(files: Array<File>) {
        files.forEach {
            if (!it.isDirectory && !it.name.endsWith("csv")) return@forEach

            var returnFlag = false
            if (it.isDirectory) {
                evaluate(it.listFiles())
                returnFlag = true
                return@forEach
            }

            if (returnFlag) return

            val reader = it.bufferedReader()
            val data = mutableListOf<Pair<Pair<Double, Double>, Pair<Double, Double>>>()

            var maxLatitudeDiff = 0.0
            var maxLongitudeDiff = 0.0
            var averageLatitudeDiff = 0.0
            var averageLongitudeDiff = 0.0

            reader.lines().forEach reader@{ line ->
                if (line.contains("Latitude", true)) return@reader
                val split = line.split(",")
                data.add((split[0].toDouble() to split[1].toDouble()) to (split[2].toDouble() to split[3].toDouble()))
            }

            data.forEach {
                val diffLatitude = abs(it.first.first - it.second.first)
                val diffLongitude = abs(it.first.second - it.second.second)
                if (maxLatitudeDiff < diffLatitude) maxLatitudeDiff = diffLatitude
                if (maxLongitudeDiff < diffLongitude) maxLongitudeDiff = diffLongitude

                averageLatitudeDiff += diffLatitude
                averageLongitudeDiff += diffLongitude
            }

            averageLatitudeDiff /= data.size
            averageLongitudeDiff /= data.size
            output.append("""
                
                ### ${it.relativeTo(maybeFile)} ###
                Max latitude difference: $maxLatitudeDiff
                Max longitude difference: $maxLongitudeDiff
                
                Average latitude difference: $averageLatitudeDiff
                Average longitude difference: $averageLongitudeDiff
                ##################
                
            """.trimIndent())
        }
    }
}
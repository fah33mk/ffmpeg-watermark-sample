package com.example.ffmpeg_hello_world

import android.content.Context
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegSessionCompleteCallback
import java.io.File
import java.io.FileOutputStream

object FFMpegWatermarkUtil {
    private fun copyResourceToFile(context: Context, resourceId: Int, fileName: String): File {
        val outputFile = File(context.cacheDir, fileName)
        try {
            context.resources.openRawResource(resourceId).use { inputStream ->
                FileOutputStream(outputFile).use { outputStream ->
                    val buffer = ByteArray(1024)
                    var length: Int
                    while ((inputStream.read(buffer).also { length = it }) > 0) {
                        outputStream.write(buffer, 0, length)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return outputFile
    }

    fun addWatermark(
        context: Context,
        filePath: String,
        newFilePath: String,
        callback: FFmpegSessionCompleteCallback
    ) {
        val watermarkFile: File = copyResourceToFile(context, R.drawable.watermark, "watermark.png")
        FFmpegKit.executeAsync(
            "-i $filePath -i "+watermarkFile.absolutePath+" -filter_complex overlay=\"(W-w)/2:(H-h)/2\" -c:a copy $newFilePath",
            callback
        )
    }
}

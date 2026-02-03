package com.notloco.android.utils

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File
import java.io.IOException

class AudioRecorder(private val context: Context) {

    private var mediaRecorder: MediaRecorder? = null
    private var outputFile: File? = null
    private var isRecording = false

    fun startRecording(): String? {
        return try {
            // Create output file
            val fileName = "audio_${System.currentTimeMillis()}.m4a"
            outputFile = File(context.cacheDir, fileName)

            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            mediaRecorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioEncodingBitRate(128000)
                setAudioSamplingRate(44100)
                setOutputFile(outputFile?.absolutePath)

                prepare()
                start()
                isRecording = true
            }

            outputFile?.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    fun stopRecording(): File? {
        return try {
            if (isRecording) {
                mediaRecorder?.apply {
                    stop()
                    reset()
                    release()
                }
                mediaRecorder = null
                isRecording = false
                outputFile
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun cancelRecording() {
        if (isRecording) {
            mediaRecorder?.apply {
                stop()
                reset()
                release()
            }
            mediaRecorder = null
            isRecording = false
            outputFile?.delete()
            outputFile = null
        }
    }

    fun isRecording(): Boolean = isRecording

    fun getRecordingDuration(): Long {
        return if (isRecording && outputFile?.exists() == true) {
            System.currentTimeMillis() - (outputFile?.lastModified() ?: 0)
        } else {
            0
        }
    }
}

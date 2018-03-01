package cunningham.taylor.soundboard

import android.os.Environment
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by taylorcunningham on 1/15/18.
 */
class DownloadHelper(private val okHttpClient: OkHttpClient, private val baseUrl: String) {

    private var authToken: String? = null

    fun setAuthToken(token: String) {
        authToken = token
    }

    fun downloadSync(routeUrl: String, saveDir: String, filename: String, overwrite: Boolean = true) {
        // Build request
        val request = Request.Builder()
                .url("$baseUrl$routeUrl")
                .header(DownloadHelper.AUTHORIZATION_HEADER, authToken ?: "")
                .build()

        // Get response
        val response = okHttpClient.newCall(request).execute()

        if (!response.isSuccessful) {
            throw IOException("Invalid response")
        }

        // Save to file
        saveBinaryToFile(response?.body()?.bytes(), saveDir, filename, overwrite)
    }

    fun downloadAsync(routeUrl: String, saveDir: String, filename: String, overwrite: Boolean = true) {
        val request = Request.Builder()
                .url("$baseUrl$routeUrl")
                .header(DownloadHelper.AUTHORIZATION_HEADER, authToken ?: "")
                .build()

        okHttpClient.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                throw e!!
            }

            override fun onResponse(call: Call?, response: Response?) {
                if (!response?.isSuccessful!!) {
                    throw IOException("Unexpected code $response")
                }

                saveBinaryToFile(response.body()?.bytes(), saveDir, filename, overwrite)
            }
        })
    }

    private fun saveBinaryToFile(bytes: ByteArray?, saveDir: String, filename: String, overwrite: Boolean) {
        if (!isExternalStorageWritable()) {
            throw IOException("External storage is not writable")
        }

        val dir = File(saveDir)
        if (!dir.exists()) {
            dir.mkdirs()
        }

        val file = File(dir, filename)
        if (file.exists() && overwrite) {
            file.delete()
        }

        try {
            val fos = FileOutputStream(file)
            fos.write(bytes)
        } catch(e: IOException) {
            throw e
        }
    }

    private fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    companion object {
        val AUTHORIZATION_HEADER = "Authorization"
    }
}
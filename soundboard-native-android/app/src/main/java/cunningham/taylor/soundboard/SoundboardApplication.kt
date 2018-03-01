package cunningham.taylor.soundboard

import android.app.Application
import android.arch.persistence.room.Room
import android.os.Environment
import cunningham.taylor.soundboard.database.AppDatabase
import okhttp3.OkHttpClient

/**
 * Created by taylorcunningham on 1/14/18.
 */
class SoundboardApplication : Application() {

    private val okHttpClient = OkHttpClient()
    private val apiUrl = "http://165.227.121.208:8080/api/"
//    private val apiUrl = "http://10.0.2.2:8080/api/"
    private val soundDirectory = Environment.getExternalStorageDirectory().toString() + "/Soundboard"
    private val downloadHelper = DownloadHelper(okHttpClient, apiUrl)
    private var authToken: String? = null
    private val mAppDatabase: AppDatabase = Room.databaseBuilder(this, AppDatabase::class.java, "app_db")
            .allowMainThreadQueries()
            .build()

    fun getOkHttpClient(): OkHttpClient {
        return okHttpClient
    }

    fun getApiUrl(): String {
        return apiUrl
    }

    fun getDownloadHelper(): DownloadHelper {
        return downloadHelper
    }

    fun getSoundDirectory(): String {
        return soundDirectory
    }

    fun setAuthToken(token: String) {
        authToken = token
        downloadHelper.setAuthToken(token)
    }

    fun getAuthToken(): String? {
        return authToken
    }

    fun getAppDatabase(): AppDatabase {
        return mAppDatabase
    }

    override fun onCreate() {
        super.onCreate()
        val dao = mAppDatabase.soundDao()
//        dao.deleteAll()
    }
}
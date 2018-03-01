package cunningham.taylor.soundboard

import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.GridView
import cunningham.taylor.soundboard.database.Sound
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException

class SoundboardActivity : AppCompatActivity() {

    private var mGridView: GridView? = null
    private val mPlayer: AudioPlayer = AudioPlayer()
    private var mSoundList: List<Sound>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_soundboard)

        // Set the title bar color
        supportActionBar?.setBackgroundDrawable(ColorDrawable(ContextCompat.getColor(this, R.color.red)))

        mGridView = findViewById<GridView>(R.id.gridView) as GridView

        checkForDownloads()
    }

    /** Disables back button functionality (since we don't want to navigate back to the login screen). */
    override fun onBackPressed() {
        // do nothing
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_settings, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.logout -> {
                logOut()
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    private fun logOut() {
        // Remove token from SharedPreferences
        val sp = getSharedPreferences("login", Context.MODE_PRIVATE)
        sp.edit().remove("token").apply()

        // Just to be safe, reset the application's auth token member
        (application as SoundboardApplication).setAuthToken("")

        // Launch login activity and clear the navigation stack
        val intent = Intent(this, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    /** Invoked once downloading is finished. */
    private fun onDownloadsFinished() {
        // Configure the grid view
        runOnUiThread {
            configureGrid()
        }
    }

    /**
     * Queries the server for outstanding downloads for this user,
     * then stores the downloads in external storage.
     */
    private fun checkForDownloads() {
        val okHttpClient = (application as SoundboardApplication).getOkHttpClient()
        val baseUrl = (application as SoundboardApplication).getApiUrl()
        val routeUrl = "download/user"

        // Build request
        val request = Request.Builder()
                .url("$baseUrl$routeUrl")
                .header(DownloadHelper.AUTHORIZATION_HEADER, (application as SoundboardApplication).getAuthToken() ?: "")
                .build()

        okHttpClient.newCall(request).enqueue(object: Callback {
            override fun onFailure(call: Call?, e: IOException?) {
                throw e!!
            }

            override fun onResponse(call: Call?, response: Response?) {
                if (!response?.isSuccessful!!) {
                    throw IOException("Unexpected code $response")
                }

                val downloader = (application as SoundboardApplication).getDownloadHelper()

                try {
                    val jsonData = JSONObject(response?.body()?.string())
                    val jsonArr = jsonData.getJSONArray("files")

                    for (i in 0 until jsonArr.length()) {
                        val jsonObj = jsonArr.getJSONObject(i)
                        val originalName = jsonObj.get("originalname") as String
                        val filename = jsonObj.get("filename") as String
                        val saveDir = (application as SoundboardApplication).getSoundDirectory()

                        downloader.downloadSync(routeUrl = "download/$filename",
                                saveDir = saveDir,
                                filename = originalName)

                        // Add the sound to the database
                        addSoundToDb(originalName, "$saveDir/$originalName")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                onDownloadsFinished()
            }
        })
    }

    /** Constructs a Sound entity and saves it to the app database. */
    private fun addSoundToDb(filename: String, path: String) {
        val sound = Sound(filename, path, ContextCompat.getColor(this, R.color.red))
        val dao = (application as SoundboardApplication).getAppDatabase().soundDao()
        dao.insertAll(sound)
    }

    /** Creates a list of Sound objects to pass to the grid view adapter. */
    private fun configureGrid() {
        val dao = (application as SoundboardApplication).getAppDatabase().soundDao()
        mSoundList = dao.all
        mGridView?.adapter = SoundboardAdapter(this, mSoundList!!, mPlayer)
    }
}

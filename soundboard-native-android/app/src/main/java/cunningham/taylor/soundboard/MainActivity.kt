package cunningham.taylor.soundboard

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log


class MainActivity : AppCompatActivity() {

    private val REQUEST_WRITE_STORAGE = 112
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check if app has External Storage write permission
        if (!isPermissionGranted(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            askExternalStoragePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    REQUEST_WRITE_STORAGE,
                    "Please??",
                    "Please allow Soundboard to write to external storage")
        } else {
            initApp()
        }
    }

    override fun onResume() {
        super.onResume()

        initApp()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_WRITE_STORAGE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.i(TAG, "Permission has been denied by user")
                } else {
                    initApp()
                }
                return
            }
        }
    }

    /**
     * Checks if the app has the given permission
     * @param permission the permission name to check for
     */
    private fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Presents a dialogue to get user's permission for the app.
     * @param permission the permission name to ask for
     * @param code the permission code to ask for
     * @param title the title of the permission rationale dialogue
     * @param message the message of the permission rationale dialogue
     */
    private fun askExternalStoragePermission(permission: String, code: Int, title: String, message: String) {
        Log.i(TAG, "Permission to write denied")

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(message)
                    .setTitle(title)

            builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, id ->
                makeRequest(permission, code)
            })

            val dialog = builder.create()
            dialog.show()

        } else {
            makeRequest(permission, code)
        }
    }

    /** Performs the request for the given permission */
    private fun makeRequest(permission: String, code: Int) {
        ActivityCompat.requestPermissions(this, arrayOf(permission), code)
    }

    /** Starts LoginActivity or SoundboardActivity depending on whether the user is logged in. */
    private fun initApp() {
        val sp = getSharedPreferences("login", Context.MODE_PRIVATE)
        val token = sp.getString("token", "")

        if (token == "") {
            launchLoginActivity()
        } else {
            (application as SoundboardApplication).setAuthToken(token)
            launchSoundboardActivity()
        }
    }

    private fun launchLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    private fun launchSoundboardActivity() {
        val intent = Intent(this, SoundboardActivity::class.java)
        startActivity(intent)
    }
}

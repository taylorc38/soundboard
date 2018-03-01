package cunningham.taylor.soundboard

import android.media.MediaPlayer

/**
 * Provides a simple wrapper to play audio files.
 */
class AudioPlayer() {

    var player: MediaPlayer? = null

    fun playSound(path: String) {
        stopPlayer()

        try {
            player = MediaPlayer()
            player?.setDataSource(path)
            player?.prepare()
            player?.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopPlayer() {
        if (player != null && player!!.isPlaying) {
            player?.stop()
            player?.release()
            player = null
        } else {
            player = null
        }
    }


}
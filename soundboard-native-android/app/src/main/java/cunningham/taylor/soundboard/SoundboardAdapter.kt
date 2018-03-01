package cunningham.taylor.soundboard

import android.app.Activity
import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import cunningham.taylor.soundboard.database.Sound

/**
 * Provides an adapter for Sounds.
 */
class SoundboardAdapter(private val mContext: Context, private val mItems: List<Sound>, private val mPlayer: AudioPlayer) : BaseAdapter() {

    override fun getCount(): Int {
        return mItems.size
    }

    override fun getItem(position: Int): Sound {
        return mItems[position]
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = (mContext as Activity).layoutInflater
        val delegateView = convertView ?: inflater.inflate(R.layout.delegate_soundboard, parent, false)

        // Set button text
        val btnSound = delegateView.findViewById<Button>(R.id.btnSound)
        btnSound.text = mItems[position].filename

        // Set button color
        val gradientDrawable = btnSound.background as GradientDrawable
        gradientDrawable.setColor(mItems[position].color)

        // Set button onClickListener
        btnSound.setOnClickListener { view ->
            val path = mItems[position].path
            mPlayer.playSound(path)
        }

        return delegateView
    }

}
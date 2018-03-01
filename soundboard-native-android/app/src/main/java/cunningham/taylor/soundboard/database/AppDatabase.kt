package cunningham.taylor.soundboard.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase

/**
 * Created by taylorcunningham on 1/20/18.
 */
@Database(entities = arrayOf(Sound::class), version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun soundDao(): SoundDao
}
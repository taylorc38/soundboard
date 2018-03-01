package cunningham.taylor.soundboard.database

import android.arch.persistence.room.*

/**
 * Created by taylorcunningham on 1/20/18.
 */
@Dao
interface SoundDao {
    @get:Query("SELECT * FROM sound")
    val all: List<Sound>

    @Insert
    fun insertAll(vararg sounds: Sound)

    @Update
    fun updateSounds(vararg sounds: Sound)

    @Delete
    fun delete(sound: Sound)

    @Query("DELETE FROM sound")
    fun deleteAll()
}
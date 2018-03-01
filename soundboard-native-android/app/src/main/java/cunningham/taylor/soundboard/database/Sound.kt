package cunningham.taylor.soundboard.database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by taylorcunningham on 1/20/18.
 */
@Entity
class Sound(@ColumnInfo(name = "filename") var filename: String,
            @ColumnInfo(name = "path") var path: String,
            @ColumnInfo(name = "color") var color: Int) {

    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0
}
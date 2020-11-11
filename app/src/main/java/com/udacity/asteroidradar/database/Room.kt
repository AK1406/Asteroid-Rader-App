package com.udacity.asteroidradar.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*


@Dao
interface AsteroidDao {
    @Query("SELECT * FROM databaseAsteroid ORDER BY date(closeApproachDate) ")
    fun getAsteroid(): LiveData<List<DatabaseAsteroid>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg asteroid: DatabaseAsteroid)

    @Query("SELECT * FROM databaseAsteroid where strftime('%Y-%m-%d',closeApproachDate) >= strftime('%Y-%m-%d','now') ORDER BY date(closeApproachDate) ")
    fun getWeekAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Query("SELECT * FROM databaseAsteroid where strftime('%Y-%m-%d',closeApproachDate) = strftime('%Y-%m-%d','now')")
    fun getTodayAsteroids(): LiveData<List<DatabaseAsteroid>>

    @Query("DELETE FROM databaseAsteroid WHERE closeApproachDate < strftime('%s', datetime('now'))")
    fun delOldAsteroids()
}

@Dao
interface PictureDao {
    @Query("SELECT * FROM databasePicture")
    fun getPicture(): LiveData<DatabasePicture>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg picture: DatabasePicture)

    @Query("DELETE FROM databasePicture")
    fun clear()

}


@Database(entities = [DatabaseAsteroid::class, DatabasePicture::class], version = 1)
abstract class AsteroidDatabase : RoomDatabase() {
    abstract val asteroidDao: AsteroidDao
    abstract val pictureDao: PictureDao
}


// instance of database
private lateinit var INSTANCE: AsteroidDatabase

fun getDatabase(context: Context): AsteroidDatabase {
    synchronized(AsteroidDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    AsteroidDatabase::class.java,
                    "asteroids"
            ).build()
        }
    }
    return INSTANCE
}
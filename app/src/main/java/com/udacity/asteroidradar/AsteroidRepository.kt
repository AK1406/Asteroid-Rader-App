package com.udacity.asteroidradar

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.Transformations.*
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDatabaseModel
import com.udacity.asteroidradar.database.asDomainModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject


class AsteroidRepository(private val database: AsteroidDatabase) {

    val asteroid: LiveData<List<Asteroid>> =
        map(database.asteroidDao.getAsteroid()) {
            it.asDomainModel()
        }

    // Responsible for updating the offline cache
    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val result = parseAsteroidsJsonResult(
                    JSONObject(
                        AsteroidApi.retrofitService.getAsteroids().await()
                    )
                )
                database.asteroidDao.insertAll(*result.asDatabaseModel())
            }
            catch (e : Exception){
                withContext(Dispatchers.Main) {

                }
                e.printStackTrace()
            }

            }
        }

}

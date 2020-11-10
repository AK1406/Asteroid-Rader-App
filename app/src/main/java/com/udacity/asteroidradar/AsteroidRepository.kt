package com.udacity.asteroidradar

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
import java.text.SimpleDateFormat
import java.util.*


class AsteroidRepository(private val database: AsteroidDatabase) {

    val asteroid: LiveData<List<Asteroid>> =
        map(database.asteroidDao.getAsteroid()) {
            it.asDomainModel()
        }

    private val _pictureOfDay = MutableLiveData<String>()
    val pictureOfDay: LiveData<String>
        get() = _pictureOfDay


    // Responsible for updating the offline cache
    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val result = AsteroidApi.retrofitService.getAsteroids().await().toString()
                val resultParsed = parseAsteroidsJsonResult(JSONObject(result))
                database.asteroidDao.clear()
                database.asteroidDao.insertAll(*resultParsed.asDatabaseModel())
            }
            catch (e : Exception){
                withContext(Dispatchers.Main) {

                }
                e.printStackTrace()
            }

            }
        }

    suspend fun refreshPicture() {
        withContext(Dispatchers.IO) {
            try {
                val imageOfDay = AsteroidApi.retrofitService.getPictureOfTheDay().await()
                if (imageOfDay.mediaType == "image") {
                    database.pictureDao.clear()
                    database.pictureDao.insertAll(imageOfDay.asDatabaseModel())
                    _pictureOfDay.value = database.pictureDao.getPicture().asDomainModel().url
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("Error refresh picture ", e.message!!)
                }
                e.printStackTrace()
            }
        }
    }

    fun getStartDateFormatted(): String {
        val calendar = Calendar.getInstance()
        val currentTime = calendar.time
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        return dateFormat.format(currentTime)
    }

    fun getEndDateFormatted(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 7)
        val currentTime = calendar.time
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        return dateFormat.format(currentTime)
    }

}

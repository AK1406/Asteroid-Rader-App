package com.udacity.asteroidradar

//code from dev-bytes

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
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
import java.lang.Exception
import java.lang.IllegalArgumentException


class AsteroidRepository(private val database: AsteroidDatabase) {


    enum class Type() { SAVED, TODAY, WEEK }

    private val _asteroidType: MutableLiveData<Type> = MutableLiveData(Type.WEEK)
    private val asteroidType: LiveData<Type>
        get() = _asteroidType

    private val _progressBar: MutableLiveData<Boolean> = MutableLiveData(false)
    val progressBar: LiveData<Boolean>
        get() = _progressBar


    val asteroid: LiveData<List<Asteroid>> =
            Transformations.switchMap(asteroidType) { type ->
                when (type) {
                    Type.SAVED -> Transformations.map(database.asteroidDao.getAsteroid()) {
                        it.asDomainModel()
                    }
                    Type.TODAY -> Transformations.map(database.asteroidDao.getTodayAsteroids()) {
                        it.asDomainModel()
                    }
                    Type.WEEK -> Transformations.map(database.asteroidDao.getWeekAsteroids()) {
                        it.asDomainModel()
                    }
                    else -> throw IllegalArgumentException(" Invalid type !")
                }

            }

    val pictureOfDay: LiveData<PictureOfDay> = Transformations.map(database.pictureDao.getPicture()) {
        it?.asDomainModel()
    }

    // updating the offline cache
    suspend fun refreshAsteroids() {
        _progressBar.value = true
        withContext(Dispatchers.IO) {
            try {
                //get network url
                val result = AsteroidApi.retrofitService.getAsteroids(
                        startDate = getStartDateFormatted(),
                        endDate = getEndDateFormatted()
                ).await()
                //fetch data by parsing network url
                val resultParsed = parseAsteroidsJsonResult(JSONObject(result))
                val imageOfTheDay = AsteroidApi.retrofitService.getPictureOfTheDay().await()
                if (imageOfTheDay.mediaType == "image") {
                    database.pictureDao.clear()
                    database.pictureDao.insertAll(imageOfTheDay.asDatabaseModel())
                }
                _progressBar.postValue(false)
                database.asteroidDao.insertAll(*resultParsed.asDatabaseModel())

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("Error refresh asteroid ", e.message!!)
                    _progressBar.value = false
                    _progressBar.postValue(false)
                }
                e.printStackTrace()
            }
        }
    }


    suspend fun delOldAsteroids() {
        withContext(Dispatchers.IO) {
            database.asteroidDao.delOldAsteroids()
        }
    }

    @SuppressLint("WeekBasedYear")
    private fun getStartDateFormatted(): String {
        val calendar = Calendar.getInstance()
        val currentTime = calendar.time
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        return dateFormat.format(currentTime)
    }

    @SuppressLint("WeekBasedYear")
    private fun getEndDateFormatted(): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 7)
        val currentTime = calendar.time
        val dateFormat = SimpleDateFormat(Constants.API_QUERY_DATE_FORMAT, Locale.getDefault())
        return dateFormat.format(currentTime)
    }

    fun applyFilter(filter: Type) {
        _asteroidType.value = filter
    }
}

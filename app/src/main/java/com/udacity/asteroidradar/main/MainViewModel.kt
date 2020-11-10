package com.udacity.asteroidradar.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.squareup.picasso.Downloader
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await


class MainViewModel : ViewModel() {

    private val _status = MutableLiveData<String>()
    val status: LiveData<String>
        get() = _status

    private val _imageOfTheDay = MutableLiveData<String>()
    val imageOfTheDay: LiveData<String>
        get() = _imageOfTheDay

    private val _asteroids = MutableLiveData<List<Asteroid>>()
    val asteroid: LiveData<List<Asteroid>>
        get() = _asteroids

    init {
        getAsteroidsList()
    }


    private fun getAsteroidsList() {
        viewModelScope.launch {
            try {
                val result = AsteroidApi.retrofitService.getAsteroids().await()
                _asteroids.value = parseAsteroidsJsonResult(JSONObject(result))
            } catch (e: Exception) {
                _status.value = "Failure: ${e.message}"
            }
        }
    }

    private fun getImageOfTheDay() {

        viewModelScope.launch {

            AsteroidApi.retrofitService.getPictureOfTheDay().enqueue(object : Callback<String> {

                override fun onResponse(call: Call<String>, response: Downloader.Response<String>) {
                    Log.i("Mylog ", "Success Image")
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    Log.i("Mylog Failed Image:", t.message.toString())
                }

            })

        }
    }
}



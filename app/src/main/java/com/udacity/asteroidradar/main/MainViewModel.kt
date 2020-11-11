package com.udacity.asteroidradar.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.squareup.picasso.Downloader
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.AsteroidRepository
import com.udacity.asteroidradar.api.AsteroidApi
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.getDatabase
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await

class MainViewModel(application: Application) : ViewModel() {

    private val _navigateToDetailFragment = MutableLiveData<Asteroid>()
    val navigateToDetailFragment: LiveData<Asteroid>
        get() = _navigateToDetailFragment

    private val database = getDatabase(application)
    private val asteroidsRepository = AsteroidRepository(database)

    init {
        viewModelScope.launch {
            asteroidsRepository.refreshAsteroids()
        }
    }

    val asteroid = asteroidsRepository.asteroid
    val imageOfTheDay = asteroidsRepository.pictureOfDay
    val loadingStatus = asteroidsRepository.progressBar


    fun shownAsteroidDetail() {
        _navigateToDetailFragment.value = null
    }

    fun displayAsteroidDetails(asteroid: Asteroid) {
        _navigateToDetailFragment.value = asteroid
    }

    fun onFilterSelect(filter: AsteroidRepository.Type) {
        asteroidsRepository.applyFilter(filter)
    }


}



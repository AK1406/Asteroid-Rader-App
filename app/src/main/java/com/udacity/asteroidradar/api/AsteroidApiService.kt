package com.udacity.asteroidradar.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Asteroid
import com.udacity.asteroidradar.Constants
import com.udacity.asteroidradar.Constants.API_KEY
import com.udacity.asteroidradar.PictureOfDay
import kotlinx.coroutines.Deferred
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query





private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()


private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(Constants.BASE_URL)
    .build()

interface AsteroidApiService {

    @GET("neo/rest/v1/feed?start_date=2020-11-010&end_date=2020-11-17&API_KEY")
    fun getAsteroids(/*@Query("start_date") startDate: String,
                     @Query("end_date") endDate: String,
                     @Query("api_key") apiKey: String = Constants.API_KEY*/):
            Deferred<Asteroid>

    @GET("@GET(planetary/apod?$API_KEY)")
    fun getPictureOfTheDay(/*@Query("api_key") apiKey: String = Constants.API_KEY*/):
            Callback<String>

}

object AsteroidApi {

    val retrofitService : AsteroidApiService by lazy { retrofit.create(AsteroidApiService::class.java)
    }
}
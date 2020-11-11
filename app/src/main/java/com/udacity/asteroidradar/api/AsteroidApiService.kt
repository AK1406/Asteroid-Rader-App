package com.udacity.asteroidradar.api

//Code from mars-real-estate

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.udacity.asteroidradar.Constants.BASE_URL
import com.udacity.asteroidradar.Constants.NASA_API_KEY
import com.udacity.asteroidradar.PictureOfDay
import kotlinx.coroutines.Deferred
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
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .baseUrl(BASE_URL)
        .build()

interface AsteroidApiService {

    @GET("neo/rest/v1/feed")
    fun getAsteroids(@Query("start_date") startDate: String,
                     @Query("end_date") endDate: String,
                     @Query("api_key") apiKey: String = NASA_API_KEY):
            Deferred<String>

    @GET("planetary/apod")
    fun getPictureOfTheDay(@Query("api_key") apiKey: String = NASA_API_KEY):
            Deferred<PictureOfDay>

}

object AsteroidApi {

    val retrofitService: AsteroidApiService by lazy {
        retrofit.create(AsteroidApiService::class.java)
    }
}
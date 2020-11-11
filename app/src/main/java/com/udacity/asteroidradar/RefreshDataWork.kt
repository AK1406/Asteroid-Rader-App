package com.udacity.asteroidradar

//code from dev-bytes

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.udacity.asteroidradar.database.getDatabase
import retrofit2.HttpException

class RefreshDataWorker(appContext: Context, params: WorkerParameters): CoroutineWorker(appContext, params)
{
    override suspend fun doWork(): Result {
        val database = getDatabase(applicationContext)
        val repository = AsteroidRepository(database)
        return try {
            repository.refreshAsteroids()
            repository.delOldAsteroids()
            Result.success()
        } catch (e: HttpException) {
            Result.retry()
        }

    }

    companion object {
        const val WORK_NAME = "RefreshDataWorker"
    }
}
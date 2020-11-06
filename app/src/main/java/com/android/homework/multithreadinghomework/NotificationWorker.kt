package com.android.homework.multithreadinghomework

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters


class NotificationWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : Worker(appContext, workerParams) {

    companion object {
        const val KEY_AMOUNT_OF_TIME = "amount_of_time"
        private const val TITLE_NOTIFICATION = "Таймер завершился"
        private const val TEXT_NOTIFICATION = "Время работы таймера составило "
        private const val DEFAULT_ID_NOTIFICATION = 1
        private const val DEFAULT_ID_CHANNEL = "default"
        private const val DEFAULT_NAME_CHANNEL = "Default"
    }

    override fun doWork(): Result {
        val amountOfTime = inputData.getString(KEY_AMOUNT_OF_TIME)
        amountOfTime?.let { sendNotification(it) }
        return Result.success()
    }

    private fun sendNotification(amountOfTime: String) {
        val notificationManager: NotificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(
                    DEFAULT_ID_CHANNEL,
                    DEFAULT_NAME_CHANNEL,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val notification = NotificationCompat.Builder(applicationContext, DEFAULT_ID_CHANNEL)
            .setContentTitle(TITLE_NOTIFICATION)
            .setContentText(TEXT_NOTIFICATION + amountOfTime)
            .setSmallIcon(R.mipmap.ic_launcher)

        notificationManager.notify(DEFAULT_ID_NOTIFICATION, notification.build())
    }
}
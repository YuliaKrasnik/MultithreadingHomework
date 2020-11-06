package com.android.homework.multithreadinghomework

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit


class TimerFragment : Fragment() {
    companion object {
        private const val DURATION_WORK_MANAGER: Long = 10
        fun newInstance() = TimerFragment()
        private const val TIMER_INTERVAL: Long = 1000
    }

    private lateinit var btnStartTimer: Button
    private lateinit var tvCountTimer: TextView

    private var timer: Timer? = null

    private var minutes = 0
    private var seconds = 0

    private var isConfigurationChange = false

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        val fragmentLayout = inflater.inflate(R.layout.fragment_timer, container, false)

        btnStartTimer = fragmentLayout.findViewById(R.id.btn_start_timer)
        btnStartTimer.setOnClickListener {
            startTimer()
            btnStartTimer.isEnabled = false
        }
        tvCountTimer = fragmentLayout.findViewById(R.id.tv_count_timer)

        if (savedInstanceState != null) {
            minutes = savedInstanceState.getInt("minutes")
            seconds = savedInstanceState.getInt("seconds")
            setTimeInTextView()
            startTimer()
            btnStartTimer.isEnabled = false
        } else {
            setTimeInTextView()
        }

        return fragmentLayout
    }

    private fun setTimeInTextView() {
        tvCountTimer.text = convertIntoFormatTime(minutes, seconds)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        isConfigurationChange = true
        outState.putInt("minutes", minutes)
        outState.putInt("seconds", seconds)
    }

    private fun startTimer() {
        timer = Timer({
            increaseTheTime()
            setTimeInTextView()
        }, TIMER_INTERVAL, true)

        timer?.start()
    }

    private fun increaseTheTime() {
        seconds++
        if (seconds >= 60) {
            seconds = 0
            minutes++
        }
    }

    private fun convertIntoFormatTime(minutes: Int, seconds: Int): String {
        val formatSeconds: String = if (seconds.toString().length == 1) "0$seconds" else "$seconds"
        val formatMinutes = if (minutes.toString().length == 1) "0$minutes" else "$minutes"
        return "$formatMinutes:$formatSeconds"
    }

    override fun onDestroy() {
        timer?.stopTimer()
        if (!isConfigurationChange) {
            createNotification()
        }
        super.onDestroy()
    }

    private fun createNotification() {
        val data: Data = Data
                .Builder()
                .putString(
                        NotificationWorker.KEY_AMOUNT_OF_TIME,
                        convertIntoFormatTime(minutes, seconds)
                )
                .build()

        val notificationWorkRequest = OneTimeWorkRequest
                .Builder(NotificationWorker::class.java)
                .setInputData(data)
                .setInitialDelay(DURATION_WORK_MANAGER, TimeUnit.SECONDS)
                .build()

        WorkManager.getInstance(requireContext()).enqueue(notificationWorkRequest)
    }

}
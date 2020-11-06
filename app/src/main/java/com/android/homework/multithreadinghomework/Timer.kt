package com.android.homework.multithreadinghomework

import android.os.Handler
import android.os.Looper


class Timer(val runnable: Runnable, val interval: Long, private val started: Boolean) : Thread() {
    private var paused = false
    private var looper: Looper? = null
    private var handler: Handler? = null

    private var minutes = 0
    private var seconds = 0

    override fun run() {
        Looper.prepare()
        looper = Looper.myLooper()
        handler = looper?.let { Handler(it) }

        if (started) startTimer()

        Looper.loop()
    }

    private val task: Runnable = object : Runnable {
        override fun run() {
            if (!paused) {
                Handler(Looper.getMainLooper()).post(runnable)
                increaseTheTime()
                handler?.postDelayed(this, interval)
            } else return
        }
    }

    private fun increaseTheTime() {
        seconds++
        if (seconds >= 60) {
            seconds = 0
            minutes++
        }
    }

    private fun startTimer() {
        paused = false
        handler?.postDelayed(task, interval)
    }


    fun stopTimer() {
        looper?.quit()
        paused = true
    }

}
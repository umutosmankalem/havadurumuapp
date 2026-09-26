package com.weatherapp.core

interface AppClock {
    fun nowMillis(): Long
}

object SystemAppClock : AppClock {
    override fun nowMillis(): Long = System.currentTimeMillis()
}

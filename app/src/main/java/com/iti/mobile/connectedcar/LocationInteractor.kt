package com.iti.mobile.connectedcar

interface LocationInteractor{
    fun sendCarCurrentSpeed(speed: Float)
    fun sendIncreaseTimeFrom10To30(increaseSpeedTime: Long)
    fun sendDecreaseTimeFrom30To10(decreaseSpeedTime: Long)
}
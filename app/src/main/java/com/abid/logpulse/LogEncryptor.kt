package com.abid.logpulse

interface LogEncryptor {
    fun encrypt(logData: String): String
}
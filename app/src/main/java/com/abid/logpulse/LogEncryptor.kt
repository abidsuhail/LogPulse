package com.abid.logpulse

interface LogEncryptor {
    fun encrypt(plainText: String): String
}
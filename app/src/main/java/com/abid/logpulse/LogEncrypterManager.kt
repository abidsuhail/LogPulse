package com.abid.logpulse

import java.security.MessageDigest

internal object LogEncrypterManager {

    // Helper to create a SHA-256 hash
    fun hashAppPackageString(input: String): String {
        return MessageDigest.getInstance("SHA-256")
            .digest(input.toByteArray())
            .joinToString("") { "%02x".format(it) }
    }
}
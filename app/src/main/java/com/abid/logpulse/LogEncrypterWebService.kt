package com.abid.logpulse

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

sealed class LogTagType(val type:String){
    data object DEBUG:LogTagType("DEBUG")
    data object ERROR:LogTagType("ERROR")
    data object VERBOSE:LogTagType("VERBOSE")
}
class LogEncrypterWebService(
    private val webhookUrl: String?,
    private val projectId: String?,
    private val encryptor: LogEncryptor?,
    private val sessionId: String
) {
    private val client = OkHttpClient()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    fun sendLogToServer(message: String, tag: LogTagType) {
        val url = webhookUrl ?: return
        val currentProjectId = projectId ?: return

        scope.launch {
            try {
                // 1. Prepare Data
                val rawJson = JSONObject().apply {
                    put("sessionId",sessionId)
                    put("message", message)
                    put("tag", tag.type)
                    put("timestamp", System.currentTimeMillis())
                }.toString()

                // 2. Encrypt (Using the Secret Key inside this logic)
                val encryptedData = encryptor?.encrypt(rawJson) ?: rawJson

                // 3. Wrap in JSON with Project ID
                // The server uses projectId to know WHICH locker to put the data in
                val postBody = JSONObject().apply {
                    put("projectId", currentProjectId)
                    put("data", encryptedData)
                }.toString().toRequestBody("application/json".toMediaType())

                // 4. Send Request
                val request = Request.Builder()
                    .url(url)
                    .post(postBody)
                    .build()

                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                    }else{
                        Log.e(this::class.java.simpleName,"LogPulse Fail: ${response.code}")
                    }
                }
            } catch (e: Exception) {
                Log.e(this::class.java.simpleName,e.toString())
            }
        }
    }
}
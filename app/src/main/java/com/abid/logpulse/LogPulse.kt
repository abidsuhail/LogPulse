package com.abid.logpulse

import android.content.Context
import android.util.Log
import com.abid.logpulse.LogEncrypterManager.hashAppPackageString
import java.util.UUID

object LogPulse {

    /** Should logs be sent in production? */
    private var enableSendProdLogsToServer = false

    private var logService:LogEncrypterWebService?=null

    /**
     * @param context Used to automatically get the Package Name
     * @param url Your Node.js backend URL,should of POST type
     * @param encryptor Your AES encryption logic
     */
    fun init(context: Context, url: String, encryptor: LogEncryptor,enableSendProdLogsToServer:Boolean=false) {
        val sessionId = UUID.randomUUID().toString().replace("-", "")
        this.enableSendProdLogsToServer = enableSendProdLogsToServer
        this.logService = LogEncrypterWebService(
            webhookUrl = url,
            projectId = hashAppPackageString(context.packageName),
            encryptor = encryptor,
            sessionId = sessionId
        )
    }
    /**
     * @param msg log message
     * */
    fun Any.pulse(msg: String) {
        Log.d("[${this::class.java.simpleName}]", msg)
        if (logService==null) {
            return
        }
        logService?.sendLogToServer(msg,LogTagType.DEBUG)

    }

    /**
     * @param msg error message
     * */
    fun Any.pulseError(msg: String) {
        Log.e("[${this::class.java.simpleName}]", msg)
        if (logService==null) {
            return
        }
        logService?.sendLogToServer(msg,LogTagType.ERROR)
    }
}
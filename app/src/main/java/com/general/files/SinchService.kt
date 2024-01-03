package com.general.files

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Binder
import android.os.IBinder
import android.util.Log
import ciride.driver.IncomingCallScreenActivity
import com.sinch.android.rtc.ClientRegistration
import com.sinch.android.rtc.SinchClient
import com.sinch.android.rtc.SinchClientListener
import com.sinch.android.rtc.SinchError
import com.sinch.android.rtc.calling.Call
import com.sinch.android.rtc.calling.CallController
import com.sinch.android.rtc.calling.CallControllerListener
import com.sinch.android.rtc.calling.CallNotificationResult
import com.sinch.android.rtc.calling.MediaConstraints
import com.general.files.JWT.create
import com.sinch.android.rtc.video.VideoController
import com.utils.Logger
import com.utils.Utils
import java.io.IOException

class SinchService : Service() {
    private val mSinchServiceInterface = SinchServiceInterface()
    var sinchClient: SinchClient? = null
        private set
    var userName: String? = null
        private set
    private var mListener: StartFailedListener? = null
    private var mSettings: PersistedSettings? = null
    override fun onCreate() {
        super.onCreate()
        mSettings = PersistedSettings(applicationContext)
    }

    override fun onDestroy() {
        if (sinchClient != null && sinchClient!!.isStarted) {
            sinchClient!!.terminateGracefully()
        }
        super.onDestroy()
    }

    private fun start(userName: String) {
        val generalFunctions = MyApp.getInstance().getGeneralFun(applicationContext)
        if (sinchClient == null) {
            this.userName = userName
            mSettings?.username = userName
            if (generalFunctions.retrieveValue(Utils.SINCH_APP_KEY) == null || generalFunctions.retrieveValue(
                    Utils.SINCH_APP_KEY
                ).equals("", ignoreCase = true)
            ) {
                return
            }
            createClient(userName)
        }
        sinchClient!!.start()
    }

    private fun createClient(userName: String?) {
        val generalFunctions = MyApp.getInstance().getGeneralFun(applicationContext)
        try {
            sinchClient = SinchClient.builder().context(applicationContext)
                .userId(userName!!)
                .applicationKey(generalFunctions.retrieveValue(Utils.SINCH_APP_KEY))
                .environmentHost(generalFunctions.retrieveValue(Utils.SINCH_APP_ENVIRONMENT_HOST))
                .pushNotificationDisplayName(userName)
                .build()
        } catch (e: IOException) {
            throw RuntimeException(e)
        }
        sinchClient!!.addSinchClientListener(MySinchClientListener())
        sinchClient!!.callController.addCallControllerListener(SinchCallClientListener())
    }

    private fun stop() {
        if (sinchClient != null) {
            sinchClient!!.terminateGracefully()
            sinchClient = null
        }
    }

    private val isStarted: Boolean
        private get() = sinchClient != null && sinchClient!!.isStarted

    override fun onBind(intent: Intent): IBinder? {
        return mSinchServiceInterface
    }

    inner class SinchServiceInterface : Binder() {
        fun getSinchClient() : SinchClient{
            start(userName?:"")
            return sinchClient!!
        }

        fun callPhoneNumber(phoneNumber: String?): Call {
            return sinchClient!!.callController.callPhoneNumber(phoneNumber!!, phoneNumber)
        }

        fun callUser(userId: String?, headers: Map<String?, String?>?): Call {
            val sinchClient = this.getSinchClient()
            return sinchClient.callController.callUser(userId!!, MediaConstraints(false))
        }

        val videoController: VideoController
            get() = sinchClient!!.videoController
        val isStarted: Boolean
            get() = this@SinchService.isStarted

        fun startClient(userName: String) {
            start(userName)
        }

        fun stopClient() {
            stop()
        }

        fun setStartListener(listener: StartFailedListener?) {
            mListener = listener
        }

        fun getCall(callId: String?): Call? {
            return sinchClient!!.callController.getCall(callId!!)
        }

        fun relayRemotePushNotificationPayload(callNotificationResult: CallNotificationResult?) {
            if (sinchClient != null) {
                sinchClient!!.relayRemotePushNotification(callNotificationResult!!)
            } else {
                createClient(userName)
                sinchClient!!.relayRemotePushNotification(callNotificationResult!!)
            }
        }
    }

    interface StartFailedListener {
        fun onStartFailed(error: SinchError?)
        fun onStarted()
    }

    private inner class MySinchClientListener : SinchClientListener {
        override fun onClientFailed(client: SinchClient, error: SinchError) {
            Log.d(TAG, "SinchClient Failed : ${error.code} ${error.extras}")
            if (mListener != null) {
                mListener!!.onStartFailed(error)
            }
            sinchClient!!.terminateGracefully()
            sinchClient = null
        }

        override fun onClientStarted(client: SinchClient) {
            Log.d(TAG, "SinchClient started")
            if (mListener != null) {
                mListener!!.onStarted()
            }
        }

        override fun onPushTokenRegistered() {}
        override fun onPushTokenRegistrationFailed(sinchError: SinchError) {}
        override fun onPushTokenUnregistered() {}
        override fun onPushTokenUnregistrationFailed(sinchError: SinchError) {}
        override fun onCredentialsRequired(clientRegistration: ClientRegistration) {
            val generalFunctions = MyApp.getInstance().getGeneralFun(applicationContext)
            clientRegistration.register(
                create(
                    generalFunctions.retrieveValue(Utils.SINCH_APP_KEY),
                    generalFunctions.retrieveValue(
                        Utils.SINCH_APP_SECRET_KEY
                    ),
                    mSettings?.username?:""
                )
            )
        }

        override fun onUserRegistered() {}
        override fun onUserRegistrationFailed(sinchError: SinchError) {}
    }

    private inner class SinchCallClientListener : CallControllerListener {
        override fun onIncomingCall(callController: CallController, call: Call) {
            val intent = Intent(this@SinchService, IncomingCallScreenActivity::class.java)
            intent.putExtra(CALL_ID, call.callId)
            Logger.d("SinchCallClientListenerName", "::" + call.headers.toString())
            intent.putExtra("Name", call.headers["Name"])
            intent.putExtra("PImage", call.headers["PImage"])
            intent.putExtra("Id", call.headers["Id"])
            intent.putExtra("type", call.headers["type"])
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
            this@SinchService.startActivity(intent)
        }
    }

    private inner class PersistedSettings(context: Context) {
        private val mStore: SharedPreferences

        init {
            mStore = context.getSharedPreferences(Companion.PREF_KEY, MODE_PRIVATE)
        }

        var username: String?
            get() = mStore.getString("Username", "")
            set(username) {
                val editor = mStore.edit()
                editor.putString("Username", username)
                editor.commit()
            }
    }

    companion object {
        private const val PREF_KEY = "Sinch"
        const val CALL_ID = "CALL_ID"
        val TAG = SinchService::class.java.simpleName
    }
}
package com.ruimgreis.test_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import com.usercentrics.sdk.*
import com.usercentrics.sdk.models.common.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val placeholder by lazy { findViewById<FrameLayout>(R.id.placeholder) }
    private val LOG_TAG = "ViewActivity"
    // var to contain injected settingsId
    private lateinit var settingsId: String
    private var predefinedUI: UCPredefinedUI? = null
    lateinit var usercentrics: Usercentrics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // hides soft input keyboard
        input_settings_id.showSoftInputOnFocus = false
        // version input
        txt_version.text =  getVersionName()
        // go button input to get settingsId
        btn_go.setOnClickListener(){ _ ->
            if(input_settings_id.text.toString().isNullOrEmpty()){
                Toast.makeText(this,
                    "Please insert a SettingsId",
                    Toast.LENGTH_LONG).show()
            } else {
                settingsId = input_settings_id.text.toString().trim()
                // Launch CMP
                showCMP(settingsId)
            }
        }

        // btn close app
        val btn_close_app= findViewById<View>(R.id.btn_close_app)
        btn_close_app.setOnClickListener{
            this.finishAffinity()
        }
    }

    override fun onBackPressed() {
        val view = predefinedUI
        if (view != null) {
            view.onBackButtonPress()
        } else {
            super.onBackPressed()
        }
    }

    /**
     * gets version of SDK being used
     */
    private fun getVersionName(): String {
        val pInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0)
        return pInfo.versionName ?: "test"
    }

    // shows CMP, using UsercentricsActivityLauncher
    // https://docs.usercentrics.com/cmp_in_app_sdk/latest/predefined_ui/present/#presenting-the-cmp
    private fun showCMP(settingsId: String) {
        val userOptions = UserOptions(
                controllerId = null,
                defaultLanguage = null,
                version = null,
                debugMode = true,
                predefinedUI = true,
                noCache = null
        )
        usercentrics = Usercentrics(
                settingsId = settingsId,
                options = userOptions,
                appContext = getApplicationContext()
        ).apply {
            setLoggerDelegate(UsercentricsLoggerDelegate(
                logDebug = { message, cause ->
                    Log.d(LOG_TAG, message, cause)
                },
                logWarning = { message, cause ->
                    Log.w(LOG_TAG, message, cause)
                },
                logError = { message, cause ->
                    Log.e(LOG_TAG, message, cause)
                }
            ))
        }

        usercentrics.initialize(
                callback = {
                    predefinedUI = usercentrics.getPredefinedUI(viewContext = this) {
                            placeholder.removeView(predefinedUI)
                            predefinedUI = null
                            this.finishAffinity()

                    }
                    predefinedUI?.let {
                        placeholder.addView(it)
                    }

                },
                onFailure = { error ->
                    println("Error on initialization: $error.message")
                }
        )

    }
}
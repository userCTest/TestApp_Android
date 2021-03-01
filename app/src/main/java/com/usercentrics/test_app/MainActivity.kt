package com.usercentrics.test_app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.usercentrics.sdk.*
import com.usercentrics.sdk.models.common.UserOptions
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var predefinedUI: UCPredefinedUI? = null
    lateinit var usercentrics: Usercentrics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*val btn_close_app= findViewById<View>(R.id.btn_close_app)
        btn_close_app.setOnClickListener{
            this.finishAffinity()
        }*/

        showsUsercentrics()
    }

    override fun onBackPressed() {
        if (predefinedUI != null) {
            predefinedUI!!.onBackButtonPress()
        } else {
            super.onBackPressed()
        }
    }

    private fun showsUsercentrics() {
        // setingsIds: cmp v1 - egLMgjg9j
        //             cmp v2 - ZDQes7xES
        val settingsID = "ZDQes7xES"
        val defLang = "de"
        val userOptions = UserOptions(
                controllerId = null,
                defaultLanguage = defLang,
                version = null,
                debugMode = null,
                predefinedUI = true,
                noCache = null
        )
        usercentrics = Usercentrics(
                settingsId = settingsID,
                options = userOptions,
                appContext = getApplicationContext()
        )

        usercentrics.initialize(
                callback = {
                    predefinedUI = usercentrics.getPredefinedUI(viewContext = this, customAssets = null) {
                        mainLayout.removeView(this.predefinedUI)
                        predefinedUI = null
                    }
                    mainLayout.addView(predefinedUI)
                },
                onFailure = { error ->
                    println("Error on initialization")
                }
        )
    }
}
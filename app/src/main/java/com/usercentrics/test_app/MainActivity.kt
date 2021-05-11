package com.usercentrics.test_app

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.usercentrics.sdk.*
import com.usercentrics.sdk.models.common.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val placeholder by lazy { findViewById<FrameLayout>(R.id.placeholder) }
    var predefinedUI: UCPredefinedUI? = null
    lateinit var usercentrics: Usercentrics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
        val defLang = null //"de"
        val userOptions = UserOptions(
                controllerId = null,
                defaultLanguage = defLang,
                version = null,
                debugMode = true,
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
                    println("Error on initialization")
                }
        )

        /*btn_close_app.setOnClickListener{
            this.finishAffinity()
        }*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UsercentricsActivity.REQUEST_CODE &&
            resultCode == UsercentricsActivity.RESULT_OK_CODE
        ) {
            val services = UsercentricsActivity.getResult(data)
            for (service in services) {
                Log.i("Consents: ", service.toString());
            }
        }

    }
}
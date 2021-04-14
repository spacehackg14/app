package com.extech.spacehack

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.extech.spacehack.databinding.ActivityMainBinding
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {
    val TAG = "MAIN"
    private lateinit var binding: ActivityMainBinding
    lateinit var callbackManager: CallbackManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        callbackManager = CallbackManager.Factory.create()
        binding.loginButton.setReadPermissions(listOf("email", "public_profile"))
        binding.loginButton.setPermissions(listOf("email", "public_profile"))

        val loginManager = LoginManager.getInstance()
        loginManager.registerCallback(callbackManager, object : FacebookCallback<LoginResult?> {
            override fun onSuccess(loginResult: LoginResult?) {
                // not called
                if (loginResult != null) {
                    showSnackbar(loginResult.accessToken.token)
                }

                GraphRequest(
                        AccessToken.getCurrentAccessToken(),
                        "/${loginResult!!.accessToken.userId}/",
                        null,
                        HttpMethod.GET
                ) {
                    Log.d(TAG, "onCreate: ${it.rawResponse} ")
                }.executeAsync()
            }

            override fun onCancel() {
                // not called
                showSnackbar("Cancel")
            }

            override fun onError(e: FacebookException) {
                // not called
                showSnackbar("Error")
            }
        })

        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        accessToken?.let {
            Log.d(TAG, "onCreate: ${accessToken.userId}")
            Log.d(TAG, "onCreate: ${accessToken.applicationId}")
            Log.d(TAG, "onCreate: ${accessToken.token}")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }

    fun showSnackbar(message: String){
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }
}
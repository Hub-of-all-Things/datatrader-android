package com.hubofallthings.datatrader.service

import android.app.Activity
import android.content.Intent
import android.os.Build
import com.hubofallthings.datatrader.activity.DrawerActivity
import com.hubofallthings.datatrader.encryption.KeyStoreWrapper
import com.hubofallthings.datatrader.helper.UserHelper
import com.hubofallthings.login.LoginActivityHAT
import com.hubofallthings.signup.activity.CreateAccountActivity
import com.nimbusds.jwt.JWTParser
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.SecretKey

class MainActivityServices(private var activity: Activity) {
    private val mUserHelper = UserHelper(activity)
    private val mPreference = DataTraderPreference(activity)

    companion object {
        val LOGIN = 130
    }

    fun login() {
        tokenExpiration()
    }

    // check if token is expired
    private fun tokenExpiration() {
        val token = getDecryptToken()
        if (!token.isNullOrEmpty()) {
            val parsedToken = JWTParser.parse(token)
            val tokenExpDate = parsedToken.jwtClaimsSet.expirationTime

            parsedToken.jwtClaimsSet.getClaim("iss")

            val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ", Locale.getDefault())
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            val result = formatter.format(Date())
            val currentDate = formatter.parse(result)

            // check if token expired
            if (currentDate < tokenExpDate) {
                startDrawerActivity()
            }
        }
    }

    // encrypt the newToken and store it to Preference
    private fun encryptToken(token: String?) {
        val mEncryptionServices = EncryptionServices(activity)
        mEncryptionServices.createMasterKey(null)
        val encryptedToken = mEncryptionServices.encrypt(token, null)
        mPreference.setToken(encryptedToken)
    }

    // success result from login to HAT , go to DrawerActivity
    fun success(userDomain: String?, newToken: String?) {
        encryptToken(newToken)
        val intent = Intent(activity, DrawerActivity::class.java)
        mPreference.setUserDomain(userDomain)
        mPreference.setLoginStatus(true)
        activity.startActivity(intent)
        activity.finish()
    }

    fun getMasterKey(): SecretKey? {
        val DEFAULT_KEY_STORE_NAME = "datatrader_keystore"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return KeyStoreWrapper(
                activity,
                DEFAULT_KEY_STORE_NAME
            ).getAndroidKeyStoreSymmetricKey(EncryptionServices.MASTER_KEY)
        } else {
            return KeyStoreWrapper(
                activity,
                DEFAULT_KEY_STORE_NAME
            ).getDefaultKeyStoreSymmetricKey(EncryptionServices.MASTER_KEY, "")
        }
    }

    // start Drawer Activity
    private fun startDrawerActivity() {
        val intent = Intent(activity, DrawerActivity::class.java)
        intent.putExtra("key", "MainActivity")
        activity.startActivity(intent)
        activity.finish()
    }

    // start Login Activity
    fun startLoginActivity() {
        val intent = Intent(activity, LoginActivityHAT::class.java)
        activity.startActivityForResult(intent, LOGIN)
    }

    // start Login Activity
    fun startCreateAccount() {
        val intent = Intent(activity, CreateAccountActivity::class.java)
        intent.putExtra("key", "MainActivity")
        activity.startActivity(intent)
    }

    // get decrypted token
    private fun getDecryptToken(): String? {
        return mUserHelper.getToken()
    }
}
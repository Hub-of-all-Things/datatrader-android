package com.hubofallthings.dataplugs.helpers

import android.content.Context
import android.content.Intent
import android.os.Build
import com.hubofallthings.dataplugs.encryption.KeyStoreWrapper
import com.hubofallthings.dataplugs.services.EncryptionServices
import com.hubofallthings.dataplugs.services.Preference
import javax.crypto.SecretKey



class UserHelper(private val context: Context){
    private val mPreference = Preference(context)
    fun getToken() : String?{
        if(getLoginStatus()){
            val encryptedToken = mPreference.getToken()
            val masterKey = getMasterKey()
            if (encryptedToken.length > 5 && masterKey != null) {
                return EncryptionServices(context).decrypt(encryptedToken, null)
            } else {
                goForLogin()
            }
        }
        return null
    }
    fun getUserDomain() : String{
        return  mPreference.getUserDomain()
    }
    private fun getLoginStatus() : Boolean{
        return mPreference.getLoginStatus()
    }
    private fun goForLogin(){
        mPreference.setLoginStatus(false)
//        val intent = Intent(context, MainActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//        context.startActivity(intent)
    }
    //encrypt the newToken and store it to Preference
    fun encryptToken ( token : String?) {
        val mEncryptionServices = EncryptionServices(context)
        mEncryptionServices.createMasterKey(null)
        val encryptedToken = mEncryptionServices.encrypt(token, null)
        mPreference.setToken(encryptedToken)
    }
    private fun getMasterKey(): SecretKey?{
        val DEFAULT_KEY_STORE_NAME = "default_keystore"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return KeyStoreWrapper(context, DEFAULT_KEY_STORE_NAME).getAndroidKeyStoreSymmetricKey(EncryptionServices.MASTER_KEY)
        } else {
            return KeyStoreWrapper(context, DEFAULT_KEY_STORE_NAME).getDefaultKeyStoreSymmetricKey(EncryptionServices.MASTER_KEY, "")
        }
    }
}
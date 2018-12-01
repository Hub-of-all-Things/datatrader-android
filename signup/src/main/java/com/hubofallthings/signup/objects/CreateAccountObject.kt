package com.hubofallthings.signup.objects

import java.io.Serializable

data class CreateAccountObject(
    var firstName : String? = null,
    var lastName : String? = null,
    var email : String? = null,
    var userName : String? = null,
    var password : String? = null,
    var optins : ArrayList<String>? = null

) : Serializable
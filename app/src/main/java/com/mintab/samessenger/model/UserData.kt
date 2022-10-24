package com.mintab.samessenger.model

import android.view.View

data class UserData(var id:String?, var imageURL:String?, var username:String?, var password:String?, var firstname:String?, var lastname:String?, var email:String?, var isonline:Boolean=false, var lastseen:String? = "recently") {
    constructor():this(null,"",null,null,null,null, null,false,null)
    constructor(id:String?, imageURL:String?,  username:String?, password:String? , firstname:String?,lastname:String?, email:String?):this(id,imageURL,username, password, firstname, lastname, email,false,"recently")

    fun getFullName(): String {
        return "$firstname $lastname"
    }
    fun getStatus():String{
        var status = ""
        status = if (isonline){
            "Online"
        }else{
            "last seen $lastseen"
        }
        return status
    }
    fun getOnlineStateVisibility():Int{
        return if(isonline){
            View.VISIBLE
        }else{
            View.INVISIBLE
        }
    }
}
package com.mintab.samessenger.model

data class MessageData(val sender:String?, val receiver:String?, var message:String?,val time:Long?) {
    constructor():this(null,null,null,null)
}
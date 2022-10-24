package com.mintab.samessenger.worker

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class SetUserStatus {
    companion object{
        public fun setStatus(isonline:Boolean) {
            val user = FirebaseAuth.getInstance().currentUser
            val databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(user!!.uid)
            val hashmap= HashMap<String,Any>()
            hashmap["isonline"] = isonline
            if (!isonline) {
                hashmap["lastseen"] = System.currentTimeMillis().toString()
            }
            databaseReference.updateChildren(hashmap)
        }
    }
}
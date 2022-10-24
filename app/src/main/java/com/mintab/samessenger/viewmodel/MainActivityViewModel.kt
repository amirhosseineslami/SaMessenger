package com.mintab.samessenger.viewmodel

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.mintab.samessenger.model.MessageData
import com.mintab.samessenger.model.UserData
import com.mintab.samessenger.view.firstpage.chatsDatabaseReference
import com.mintab.samessenger.view.messagepage.DATABASE_REF_PATH_RECEIVED
import com.mintab.samessenger.view.messagepage.DATABASE_REF_PATH_SENT
import java.io.File

private const val TAG = "MainActivityViewModel"

class MainActivityViewModel : ViewModel() {
    companion object {
        private var profilePicturesList = ArrayList<File>()
        private var profilePicturesLiveData = MutableLiveData<List<File>>()
        private var currentUserContactsListLiveData = MutableLiveData<ArrayList<UserData>>()
        var currentUserContactsList: ArrayList<UserData> = ArrayList()
        var messagesReceivedOrSentList: ArrayList<MessageData> = ArrayList()
        val messagesReceivedOrSentListLiveData = MutableLiveData<ArrayList<MessageData>>()
        private val firebaseUser = FirebaseAuth.getInstance().currentUser
        private val sentMessagesDatabaseReference = chatsDatabaseReference
            .child(firebaseUser!!.uid)
            .child(DATABASE_REF_PATH_SENT)
        private val receivedMessagesDatabaseReference = chatsDatabaseReference
            .child(firebaseUser!!.uid)
            .child(DATABASE_REF_PATH_RECEIVED)

        init {
            messagesReceivedOrSentListLiveData.value = ArrayList()
        }

        public fun getProfilePicturesQuantity(): Int {
            return if (profilePicturesLiveData.value?.isNotEmpty() == true) {
                profilePicturesLiveData.value!!.size
            } else {
                0
            }
        }

        public fun getProfilePicturesLiveData(): MutableLiveData<List<File>> {
            return profilePicturesLiveData
        }

        public fun addToProfilePicturesList(file: File) {
            if (profilePicturesLiveData.value != null) {
                profilePicturesList = profilePicturesLiveData.value as ArrayList<File>
            }
            var isExistInTheList = false
            for (item in profilePicturesList) {
                if (file == item) isExistInTheList = true
            }
            if (!isExistInTheList) {
                profilePicturesList.add(file)
                profilePicturesLiveData.value = profilePicturesList
                Log.i(TAG, "addToProfilePicturesList: list:* ${profilePicturesLiveData.value}")
            }
        }

        public fun getContactsLiveData(): MutableLiveData<ArrayList<UserData>> {
            return currentUserContactsListLiveData
        }

        public fun addToCurrentUserContactsList(userData: UserData) {
            if (!currentUserContactsList.contains(userData)) {
                currentUserContactsList.add(userData)
                currentUserContactsListLiveData.value?.add(userData)
            }
        }

        public fun getCurrentUserContactList(): ArrayList<UserData> {
            return currentUserContactsList
        }

        public fun getContactByID(idList: List<String>): UserData? {
            var foundContact: UserData? = null
            for (itemInIdList in idList) {
                for (itemInContactsList in currentUserContactsList) {
                    if (itemInIdList == itemInContactsList.id) {
                        foundContact = itemInContactsList
                        break
                    }
                }
            }
            return foundContact
        }

        public fun updateLastReceivedOrSentMessagesList() {
            sentMessagesDatabaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.i(TAG, "last onDataChange1: $snapshot")
                    for (bigData in snapshot.children) {
                        Log.i(TAG, "last onDataChange2: $snapshot")
                        val messageData = bigData.children.last().getValue(MessageData::class.java)
                        var newMessageReceivedOrSentList:ArrayList<MessageData> = ArrayList()
                        newMessageReceivedOrSentList = messagesReceivedOrSentList
                        Log.i(TAG,"onDataChange: messageReceivedOrSentList Size:${messagesReceivedOrSentList.size}")
                        messagesReceivedOrSentList.iterator()
                        for (item in messagesReceivedOrSentList) {
                            if (item.receiver == messageData?.receiver) {
                                if (item.time!! <= messageData?.time!!) {
                                    newMessageReceivedOrSentList.remove(item)
                                } else {
                                    Log.i(TAG, "onDataChange: returned")
                                    return
                                }
                            }
                        }
                        Log.i(TAG, "onDataChange: continued")
                        messagesReceivedOrSentList = newMessageReceivedOrSentList
                        if (!messageData?.message.isNullOrEmpty()
                        ) {
                            messagesReceivedOrSentList.add(messageData!!)
                            messagesReceivedOrSentListLiveData.value = messagesReceivedOrSentList
                            Log.i(
                                TAG,
                                "last onDataChange: successfully added ${messageData.message} time:${messageData.time}"
                            )
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "onCancelled: ${error.message}")
                }
            })


            receivedMessagesDatabaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (bigData in snapshot.children) {
                        val messageData = bigData.children.last().getValue(MessageData::class.java)

                        // check if the last sent message isn't newer than the received
                        for (item in messagesReceivedOrSentListLiveData.value!!) {
                            if (item.receiver == messageData?.sender && item.time!! < messageData?.time!!) {
                                // received is newest so remove last sent from the list
                                messagesReceivedOrSentList.remove(messageData)
                                // received Message is newer so add it
                                messagesReceivedOrSentList.add(messageData)
                                messagesReceivedOrSentListLiveData.value =
                                    messagesReceivedOrSentList
                                messagesReceivedOrSentList.iterator()
                                Log.i(
                                    TAG,
                                    "onDataChange: successfully added ${messageData.message} time:${messageData.time}"
                                )
                                break
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(
                        TAG,
                        "onCancelled: ${error.message}"
                    )
                }
            })
        }

        public fun updateSentMessagesList() {

            sentMessagesDatabaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.i(TAG, "onDataChange1: $snapshot")
                    for (bigData in snapshot.children) {
                        Log.i(TAG, "onDataChange2: $snapshot")
                        for (data in bigData.children) {
                            Log.i(
                                TAG,
                                "onDataChange3: ${snapshot.children}"
                            )
                            val messageData = data.getValue(MessageData::class.java)
                            if (!messageData?.message.isNullOrEmpty()
                            ) {
                                messagesReceivedOrSentList.add(messageData!!)
                                Log.i(
                                    TAG,
                                    "onDataChange: successfully added ${messageData.message} time:${messageData.time}"
                                )
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(
                        TAG,
                        "onCancelled: ${error.message}"
                    )
                }
            })
            messagesReceivedOrSentListLiveData.value = messagesReceivedOrSentList
        }

        public fun updateAllReceivedMessages() {

            // push received messages

            receivedMessagesDatabaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (bigData in snapshot.children) {
                        for (data in bigData.children) {
                            val messageData = data.getValue(MessageData::class.java)
                            messagesReceivedOrSentList.add(messageData!!)
                            Log.i(
                                TAG,
                                "onDataChange: successfully added ${messageData.message} time:${messageData.time}"
                            )
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e(
                        TAG,
                        "onCancelled: ${error.message}"
                    )
                }
            })
            messagesReceivedOrSentListLiveData.value = messagesReceivedOrSentList
        }

        private fun isThisTheLastMessage(messageData: MessageData): Boolean {
            if (messagesReceivedOrSentList.isEmpty()) {
                return true
            }
            var isLastMessage = true
            for (item in messagesReceivedOrSentList) {
                if (!messageData.message.isNullOrEmpty() && item.sender == messageData.sender && item.receiver == messageData.receiver && item.time!! < messageData.time!!) {
                    Log.i(
                        TAG,
                        "isThisTheLastMessage: not last message itemTime:${item.time} < messageTime:${messageData.time}"
                    )
                    isLastMessage = false
                }
            }
            return isLastMessage
        }


    }
}



package com.mintab.samessenger.view.messagepage

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mintab.samessenger.R
import com.mintab.samessenger.databinding.FragmentMessageBinding
import com.mintab.samessenger.model.MessageData
import com.mintab.samessenger.model.UserData
import java.text.SimpleDateFormat
import java.util.*

public const val DATABASE_REF_PATH_CHATS = "chats"
public const val DATABASE_REF_PATH_RECEIVED = "received"
public const val DATABASE_REF_PATH_SENT = "sent"

class MessageFragment() : Fragment() {
    private lateinit var fragmentMessageBinding: FragmentMessageBinding
    private lateinit var messagesRV: RecyclerView
    private lateinit var profilePictureIV: ImageView
    private lateinit var sendButton: ImageButton
    private lateinit var messageEditText: EditText
    private lateinit var currentUserData: UserData
    private lateinit var contactUserData: UserData
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var contactDatabaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentMessageBinding = DataBindingUtil.inflate(
            LayoutInflater.from(container!!.context),
            R.layout.fragment_message, container, false
        )
        return fragmentMessageBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializing()
    }

    private fun initializing() {
        initInputArguments()
        initViews()
        initEventListener()
        initFirebase()
    }

    private fun initFirebase() {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        contactDatabaseReference =
            FirebaseDatabase.getInstance().getReference("Users").child(contactUserData.id!!)

    }

    private fun initInputArguments() {
        val currentUserDataList = requireArguments().getStringArray("currentUserData")
        val contactUserDataList = requireArguments().getStringArray("contactUserData")
        contactUserData = UserData(
            contactUserDataList?.get(0)!!,
            contactUserDataList[1]!!,
            contactUserDataList[2]!!,
            contactUserDataList[3]!!,
            contactUserDataList[4]!!,
            contactUserDataList[5]!!,
            contactUserDataList[6]!!,
            contactUserDataList[7]!!.toBoolean()
        )
        currentUserData = UserData(
            currentUserDataList?.get(0)!!,
            currentUserDataList[1]!!,
            currentUserDataList[2]!!,
            currentUserDataList[3]!!,
            currentUserDataList[4]!!,
            currentUserDataList[5]!!,
            currentUserDataList[6]!!
        )
    }

    private fun initEventListener() {
        fragmentMessageBinding.contactUserData = contactUserData
        fragmentMessageBinding.listener =
            MessageFragmentEventListener(
                requireActivity(),
                sendButton,
                messageEditText,
                currentUserData,
                contactUserData
            )
    }

    private fun initViews() {
        messagesRV = fragmentMessageBinding.fragmentMessageRecyclerview
        messagesRV.layoutManager = LinearLayoutManager(requireContext())
        profilePictureIV = fragmentMessageBinding.fragmentMessageImageviewProfileIcon
        sendButton = fragmentMessageBinding.fragmentMessageButtonSend
        messageEditText = fragmentMessageBinding.fragmentMessageEdittextSend
    }

    class MessageFragmentEventListener(
        val activity: Activity,
        private val sendButton: ImageButton,
        private val messageEditText: EditText,
        private val currentUserData: UserData,
        private val contactUserData: UserData
    ) {
        public fun onSendBtnClick(view: View) {
            val message = messageEditText.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessage(messageEditText.text.toString())
                messageEditText.setText("")
            }
        }

        private fun sendMessage(message: String) {

            val mainDatabaseReference = FirebaseDatabase.getInstance().reference
/*
            val messageHashMap = HashMap<String, Any>()
            messageHashMap["senderID"] = currentUserData.id!!
            messageHashMap["receiver"] = contactUserData.id!!
            messageHashMap["message"] = message
            messageHashMap["time"] = System.currentTimeMillis().toString()
*/
/*
            val milliSeconds = System.currentTimeMillis()
            val sdf = SimpleDateFormat("MMM dd,yyyy HH:mm", Locale.US)
            val resultDate = Date(milliSeconds)
            println(sdf.format(resultDate))*/

            val data = MessageData(currentUserData.id,contactUserData.id,message,System.currentTimeMillis())
            // save to sent message of current user
            val sentMessageDatabaseReference = mainDatabaseReference.child(DATABASE_REF_PATH_CHATS)
                .child(currentUserData.id!!)
                .child(DATABASE_REF_PATH_SENT)
                .child(contactUserData.id!!)
            sentMessageDatabaseReference.push().setValue(data)

            // save to received message of contact user
            val receivedMessageDatabaseReference = mainDatabaseReference.child(DATABASE_REF_PATH_CHATS)
                    .child(contactUserData.id!!)
                    .child(DATABASE_REF_PATH_RECEIVED)
                .child(currentUserData.id!!)
            receivedMessageDatabaseReference.push().setValue(data)

        }

        public fun afterTextChanged(editable: Editable) {
            sendButton.isClickable = editable.isNotEmpty()
        }
    }
}
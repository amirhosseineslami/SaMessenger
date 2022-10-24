package com.mintab.samessenger.view.firstpage

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.mintab.samessenger.R
import com.mintab.samessenger.databinding.FragmentChatsBinding
import com.mintab.samessenger.model.MessageData
import com.mintab.samessenger.model.UserData
import com.mintab.samessenger.view.messagepage.DATABASE_REF_PATH_CHATS
import com.mintab.samessenger.view.messagepage.DATABASE_REF_PATH_RECEIVED
import com.mintab.samessenger.view.messagepage.DATABASE_REF_PATH_SENT
import com.mintab.samessenger.viewmodel.MainActivityViewModel

private const val TAG = "ChatsFragment"
public var chatsDatabaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference(DATABASE_REF_PATH_CHATS)

class ChatsFragment : Fragment() {
    private lateinit var chatsRecyclerView: RecyclerView
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var fragmentChatsBinding: FragmentChatsBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        fragmentChatsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_chats, container, false)
        return fragmentChatsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializing()
    }

    private fun initializing() {
        initFirebase()
        initViews()
    }

    private fun initViews() {
        chatsRecyclerView = fragmentChatsBinding.fragmentChatsRv
        chatsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        pushChatsToRecyclerView()

    }

    private fun pushChatsToRecyclerView() {
MainActivityViewModel.updateLastReceivedOrSentMessagesList()
        // get all last messages
        MainActivityViewModel.messagesReceivedOrSentListLiveData.observe(viewLifecycleOwner) {
            Log.i(TAG, "pushChatsToRecyclerView: ${it.size}")
        }
    }

    private fun initFirebase() {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
    }

    class ChatsFragmentEventListener(activity: Activity) {

    }
}
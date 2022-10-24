package com.mintab.samessenger.view.firstpage

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.mintab.samessenger.R
import com.mintab.samessenger.adapter.ContactRVAdapter
import com.mintab.samessenger.databinding.FragmentContactsBinding
import com.mintab.samessenger.model.UserData
import com.mintab.samessenger.viewmodel.MainActivityViewModel

private const val TAG = "ContactsFragment"

class ContactsFragment : Fragment() {
    private lateinit var searchView: SearchView
    private lateinit var recyclerView: RecyclerView
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var users: ArrayList<UserData>
    private var contactRVAdapter: ContactRVAdapter? = null
    private lateinit var fragmentContactsBinding: FragmentContactsBinding
    private var currentUserData = UserData()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentContactsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_contacts, container, false)
        return fragmentContactsBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initial()
    }

    private fun initial() {
        initViews()
        initCurrentUserData()
    }

    private fun initCurrentUserData() {
        if (currentUserData.id == null){
            val sharedPreferences = requireActivity().getSharedPreferences(SHARED_PREFERENCES_NAME,Activity.MODE_PRIVATE)
            currentUserData = UserData(sharedPreferences.getString("id",null),sharedPreferences.getString("imageURL",null),sharedPreferences.getString("username",null),sharedPreferences.getString("password",null),sharedPreferences.getString("firstname",null),sharedPreferences.getString("lastname",null),sharedPreferences.getString("email",null))
        }
    }

    private fun initViews() {
        searchView = fragmentContactsBinding.fragmentContactsSearchView
        searchView.setOnQueryTextListener(onQueryTextListener())
        recyclerView = fragmentContactsBinding.fragmentContactsRecyclerView
        users = ArrayList()
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        getContactsFromFirebase()
    }

    private fun onQueryTextListener(): SearchView.OnQueryTextListener {
        return object :SearchView.OnQueryTextListener{
            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()){
                    getContactsFromFirebase()
                }else{
                    searchInReadyContacts(newText)
                }
                return true
            }
            override fun onQueryTextSubmit(query: String?): Boolean {

                return false
            }
        }
    }
    private fun searchInReadyContacts(newText: String){
        val searchResult = ArrayList<UserData>()
        for (user in users){
            if (user.firstname!!.contains(newText) or user.lastname!!.contains(newText) or user.username!!.contains(newText)){
                searchResult.add(user)
            }
        }
        contactRVAdapter?.setUsers(searchResult)
    }

    private fun searchForContacts(newText: String) {
        val query:Query = FirebaseDatabase.getInstance().getReference("Users")
            .orderByChild("name")
            .startAt(newText)
            .endAt(newText+"\uf8ff")

        query.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                users.clear()
                for (data in snapshot.children){
                    val user = data.getValue(UserData::class.java)
                    if (user != null){
                        if (user.id != firebaseUser.uid){
                            users.add(user)
                            MainActivityViewModel.addToCurrentUserContactsList(user)
                        }else if (user.id == firebaseUser.uid){
                            currentUserData = user
                        }
                    }
                }
                contactRVAdapter = ContactRVAdapter(requireActivity(),currentUserData)
                contactRVAdapter?.setUsers(users)
                recyclerView.adapter = contactRVAdapter
            }
            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "onCancelled: ${error.message}", )
            }
        })
    }

    private fun getContactsFromFirebase() {
        FirebaseDatabase.getInstance().getReference("Users")
            .addValueEventListener(object :ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    users.clear()
                    for (dataSnapshot in snapshot.children){
                        val user = dataSnapshot.getValue(UserData::class.java)
                        if (user!!.id != firebaseUser.uid){
                            users.add(user)
                            Log.i(TAG, "onDataChange: $user")
                        }
                        Log.i(TAG, "onDataChange: $users")
                    }
                    contactRVAdapter = ContactRVAdapter(requireActivity(),currentUserData)
                    recyclerView.adapter = contactRVAdapter
                    recyclerView.layoutManager = LinearLayoutManager(requireContext())
                    contactRVAdapter?.setUsers(users)
                }
                override fun onCancelled(error: DatabaseError) {
                    Log.e(TAG, "onCancelled: ${error.message}", )
                }
            })
    }
}
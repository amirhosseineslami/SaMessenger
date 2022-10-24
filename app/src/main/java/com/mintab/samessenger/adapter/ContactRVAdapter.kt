package com.mintab.samessenger.adapter

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.mintab.samessenger.R
import com.mintab.samessenger.databinding.ContactRvItemBinding
import com.mintab.samessenger.model.UserData
import com.mintab.samessenger.view.firstpage.AppHomeFragmentDirections

private const val TAG = "ContactRVAdapter"
class ContactRVAdapter(val activity: Activity,val currentUserData: UserData) : RecyclerView.Adapter<ContactRVAdapter.Holder>() {
    private var currentUserDataList:List<String> = ArrayList()
    private var users = ArrayList<UserData>()
    init {
        users.add(UserData())
        users.add(UserData())
        users.add(UserData())
        users.add(UserData())
    }

    class Holder(val contactRvItemBinding: ContactRvItemBinding) :
        RecyclerView.ViewHolder(contactRvItemBinding.root) {
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding: ContactRvItemBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.contact_rv_item,parent,false)
        return Holder(binding)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.contactRvItemBinding.user = users[position]

        if (currentUserData.id != null && users[position].id != null) {
            holder.itemView.setOnClickListener(onItemClickListener(users[position]))
        }else{
            Log.i(TAG, "onBindViewHolder: something is null: ${currentUserData.id}+${users[position].id}")
        }
    }

    private fun onItemClickListener(userData: UserData): View.OnClickListener {
        return View.OnClickListener {
                currentUserDataList = listOf(
                    currentUserData.id!!,
                    currentUserData.imageURL!!,
                    currentUserData.username!!,
                    currentUserData.password!!,
                    currentUserData.firstname!!,
                    currentUserData.lastname!!,
                    currentUserData.email!!
                )
                val contactUserDataList: List<String> = listOf(
                    userData.id!!,
                    userData.imageURL!!,
                    userData.username!!,
                    userData.password!!,
                    userData.firstname!!,
                    userData.lastname!!,
                    userData.email!!,
                    userData.isonline.toString()
                )
                activity.findNavController(R.id.fragmentContainerView_main_activity)
                    .navigate(
                        AppHomeFragmentDirections.actionAppHomeFragmentToMessageFragment(
                            currentUserDataList.toTypedArray(),
                            contactUserDataList.toTypedArray()
                        )
                    )
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

     fun setUsers(users: ArrayList<UserData>){
        this.users = users
         notifyDataSetChanged()
    }
}
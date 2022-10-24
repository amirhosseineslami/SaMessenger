package com.mintab.samessenger.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mintab.samessenger.view.firstpage.ChatsFragment
import com.mintab.samessenger.view.firstpage.ContactsFragment
import com.mintab.samessenger.view.firstpage.GroupsFragment

class ViewPagerAdapter(fragmentManager: FragmentManager,lifecycle:Lifecycle) : FragmentStateAdapter(fragmentManager,lifecycle) {
    override fun createFragment(position: Int): Fragment {
        var fragment = Fragment()
        when(position){
            0 ->{
                fragment = ContactsFragment()
            }
            1 ->{
                fragment = ChatsFragment()
            }
            2 ->{
                fragment = GroupsFragment()
            }
        }
        return fragment
    }
    override fun getItemCount(): Int {
        return 3
    }
}
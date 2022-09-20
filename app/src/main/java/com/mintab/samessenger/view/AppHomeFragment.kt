package com.mintab.samessenger.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.mintab.samessenger.R
import com.mintab.samessenger.databinding.FragmentAppHomeBinding

class AppHomeFragment : Fragment() {
    private lateinit var fragmentAppHomeBinding: FragmentAppHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       fragmentAppHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_app_home,container,false)
        return fragmentAppHomeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()

    }

    private fun initializeViews() {

    }
}
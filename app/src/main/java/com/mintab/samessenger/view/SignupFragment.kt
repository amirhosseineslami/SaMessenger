package com.mintab.samessenger.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.mintab.samessenger.R
import com.mintab.samessenger.databinding.FragmentSignupBinding

class SignupFragment : Fragment() {
    private lateinit var fragmentSignupBinding: FragmentSignupBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentSignupBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup,container,false)
        return fragmentSignupBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}
package com.mintab.samessenger.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.mintab.samessenger.R
import com.mintab.samessenger.databinding.FragmentForgetpasswordBinding

class ForgetPasswordFragment : Fragment() {
    private lateinit var fragmentForgetpasswordBinding: FragmentForgetpasswordBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentForgetpasswordBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_forgetpassword,container,false)
        return fragmentForgetpasswordBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}
package com.mintab.samessenger.view

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputLayout
import com.mintab.samessenger.R
import com.mintab.samessenger.databinding.FragmentForgetpasswordBinding
import com.mintab.samessenger.worker.CheckTextInputs

class ForgetPasswordFragment : Fragment() {
    private lateinit var fragmentForgetPasswordBinding: FragmentForgetpasswordBinding
    private lateinit var emailTextInputLayout: TextInputLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentForgetPasswordBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_forgetpassword,container,false)
        return fragmentForgetPasswordBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()
        // set listener
        fragmentForgetPasswordBinding.listener = ForgetPasswordFragmentEventListener(requireActivity(), emailTextInputLayout)

    }

    private fun initializeViews() {
        emailTextInputLayout = fragmentForgetPasswordBinding.forgetpasswordTextInputLayoutEmail
    }

    class ForgetPasswordFragmentEventListener(private val activity: Activity, val emailTextInputLayout: TextInputLayout){
        private val checkTextInputs = CheckTextInputs(activity,emailTextInputLayout,null,null,null,null,null,null,null,null,null)

        public fun recoverPasswordBtnOnClick(view: View){
            if (checkTextInputs.checkIsEmailValid()){

            }
        }
        public fun afterTextChanged(editable: Editable){
            if (checkTextInputs.checkIsEmailValid()){
                emailTextInputLayout.error = null
            }
        }

    }
}
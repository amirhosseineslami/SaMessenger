package com.mintab.samessenger.view.entrance

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
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

        // set pre value of email from login field
        emailTextInputLayout.editText!!.setText(requireArguments().getString("email"))

        // set listener
        fragmentForgetPasswordBinding.listener = ForgetPasswordFragmentEventListener(requireActivity(), emailTextInputLayout)
    }

    private fun initializeViews() {
        emailTextInputLayout = fragmentForgetPasswordBinding.forgetpasswordTextInputLayoutEmail
    }

    class ForgetPasswordFragmentEventListener(private val activity: Activity, val emailTextInputLayout: TextInputLayout){
        private val checkTextInputs = CheckTextInputs(activity,emailTextInputLayout,null,null,null,null,null,null,null,null,null,null)

        public fun recoverPasswordBtnOnClick(view: View){
            if (checkTextInputs.checkIsEmailValid()){
                FirebaseAuth.getInstance().sendPasswordResetEmail(emailTextInputLayout.editText!!.text.toString().trim())
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            activity.findNavController(R.id.fragmentContainerView_main_activity)
                                .navigate(ForgetPasswordFragmentDirections.actionForgetPasswordFragmentToLoginFragment(true))
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(activity,it.message,Toast.LENGTH_SHORT).show()
                    }
            }
        }
        public fun afterTextChanged(editable: Editable){
            if (checkTextInputs.checkIsEmailValid()){
                emailTextInputLayout.error = null
            }
        }

    }
}
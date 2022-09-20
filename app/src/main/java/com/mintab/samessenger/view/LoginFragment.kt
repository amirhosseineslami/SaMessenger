package com.mintab.samessenger.view

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
import com.mintab.samessenger.databinding.FragmentLoginBinding
import com.mintab.samessenger.worker.CheckTextInputs
import io.reactivex.rxjava3.exceptions.Exceptions

class LoginFragment : Fragment() {
    private lateinit var fragmentLoginBinding: FragmentLoginBinding
    private lateinit var emailTextInputLayout: TextInputLayout
    private lateinit var passwordTextInputLayout: TextInputLayout
    private lateinit var firebaseAuth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentLoginBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        return fragmentLoginBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()

        // set event listener
        fragmentLoginBinding.listener = LoginFragmentEventListener(requireActivity(),emailTextInputLayout,passwordTextInputLayout,firebaseAuth)

        initializeFirebase()
    }

    private fun initializeFirebase() {
        firebaseAuth = FirebaseAuth.getInstance()
    }

    private fun initializeViews() {
        emailTextInputLayout = fragmentLoginBinding.textInputLayoutLoginEmail
        passwordTextInputLayout = fragmentLoginBinding.textInputLayoutLoginPassword
    }

    class LoginFragmentEventListener(
        private val activity: Activity,
        val emailTextInputLayout: TextInputLayout,
        val passwordTextInputLayout: TextInputLayout,
        val firebaseAuth: FirebaseAuth
    ) {
        private val checkTextInputs = CheckTextInputs(
            activity,
            emailTextInputLayout,
            passwordTextInputLayout,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        )

        public fun loginBtnOnClick(view: View) {
            if (checkTextInputs.checkIsEmailValid() and checkTextInputs.checkIsPasswordValid()) {
                val email = emailTextInputLayout.editText!!.text.toString().trim()
                val password = passwordTextInputLayout.editText!!.text.toString().trim()
                firebaseAuth.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener {
                        if (it.isSuccessful){
                            activity.findNavController(R.id.fragmentContainerView_main_activity)
                                .navigate(LoginFragmentDirections.actionLoginFragmentToAppHomeFragment())
                        }else{
                            Toast.makeText(activity,"Login Failed",Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener {
                        Toast.makeText(activity,it.message,Toast.LENGTH_SHORT).show()
                    }
            }
        }

        public fun signupNowTextViewOnClick(view: View) {
            activity.findNavController(R.id.fragmentContainerView_main_activity)
                .navigate(LoginFragmentDirections.actionLoginFragmentToSignupFragment())
        }

        public fun forgetPasswordTextViewOnClick(view: View) {
            activity.findNavController(R.id.fragmentContainerView_main_activity)
                .navigate(LoginFragmentDirections.actionLoginFragmentToForgetPasswordFragment())
        }

        public fun afterTextChanged(editable: Editable, whichEditText: Int) {
            when (whichEditText) {
                1 -> {
                    // email editText
                    if (checkTextInputs.checkIsEmailValid()) {
                        emailTextInputLayout.error = null
                    }
                }
                2 -> {
                    // password editText
                    if (checkTextInputs.checkIsPasswordValid()) {
                        emailTextInputLayout.error = null
                    }
                }
            }

        }
    }
}
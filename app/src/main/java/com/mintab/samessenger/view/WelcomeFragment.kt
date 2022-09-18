package com.mintab.samessenger.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.mintab.samessenger.R
import com.mintab.samessenger.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {
    private lateinit var fragmentWelcomeBinding: FragmentWelcomeBinding
    private lateinit var loginTextView: TextView
    private lateinit var signupTextView:TextView
    private lateinit var forgetPasswordTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentWelcomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_welcome,container,false)
        return fragmentWelcomeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initialize()

    }

    private fun initialize() {
        //views
        loginTextView = fragmentWelcomeBinding.textViewLogin
        signupTextView = fragmentWelcomeBinding.textViewSignup
        forgetPasswordTextView = fragmentWelcomeBinding.textViewForgetpassword

    }


    public class WelcomeFragmentEventListener(){
        public fun loginTextViewOnClickListener(view: View){

        }
        public fun signupTextViewOnClickListener(view: View){

        }
        public fun forgetPasswordTextViewOnClickListener(view: View){

        }
    }
}
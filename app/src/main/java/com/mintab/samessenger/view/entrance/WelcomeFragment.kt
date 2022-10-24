package com.mintab.samessenger.view.entrance

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.mintab.samessenger.R
import com.mintab.samessenger.databinding.FragmentWelcomeBinding


class WelcomeFragment : Fragment() {
    private lateinit var fragmentWelcomeBinding: FragmentWelcomeBinding
    private lateinit var loginTextView: TextView
    private lateinit var signupTextView:TextView
    private lateinit var forgetPasswordTextView: TextView
    private lateinit var navController:NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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
        checkIsLoggedIn()
        initialize()
    }

    private fun checkIsLoggedIn() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser!=null){
            navController = requireActivity().findNavController(R.id.fragmentContainerView_main_activity)
            navController.navigate(WelcomeFragmentDirections.actionWelcomeFragmentToAppHomeFragment())
        }
    }

    private fun initialize() {

        // init views
        loginTextView = fragmentWelcomeBinding.fragmentWelcomTextViewLogin
        signupTextView = fragmentWelcomeBinding.fragmentWelcomeTextViewSignup
        forgetPasswordTextView = fragmentWelcomeBinding.fragmentWelcomeTextViewForgetpassword

        // set event listener
        fragmentWelcomeBinding.listener = WelcomeFragmentEventListener(requireActivity())

    }


    public class WelcomeFragmentEventListener(private val activity: Activity){
        public fun loginTextViewOnClickListener(view: View){
            activity.findNavController(R.id.fragmentContainerView_main_activity)
                .navigate(WelcomeFragmentDirections.actionWelcomeFragmentToLoginFragment())
        }
        public fun signupTextViewOnClickListener(view: View){
            activity.findNavController(R.id.fragmentContainerView_main_activity)
                .navigate(WelcomeFragmentDirections.actionWelcomeFragmentToSignupFragment())
        }
        public fun forgetPasswordTextViewOnClickListener(view: View){
            activity.findNavController(R.id.fragmentContainerView_main_activity)
                .navigate(WelcomeFragmentDirections.actionWelcomeFragmentToForgetPasswordFragment(null))
        }
    }
}
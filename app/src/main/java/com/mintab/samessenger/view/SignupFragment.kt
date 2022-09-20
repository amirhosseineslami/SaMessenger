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
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mintab.samessenger.R
import com.mintab.samessenger.databinding.FragmentSignupBinding
import com.mintab.samessenger.worker.CheckTextInputs

class SignupFragment : Fragment() {
    private lateinit var fragmentSignupBinding: FragmentSignupBinding
    private lateinit var usernameTextInputLayout: TextInputLayout
    private lateinit var emailTextInputLayout: TextInputLayout
    private lateinit var passwordTextInputLayout: TextInputLayout
    private lateinit var againPasswordTextInputLayout: TextInputLayout
    private lateinit var firebaseAuth:FirebaseAuth
    private lateinit var databaseReference:DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentSignupBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_signup, container, false)
        return fragmentSignupBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeViews()

        // set listener
        fragmentSignupBinding.listener = SignupFragmentEventListener(
            requireActivity(),
            emailTextInputLayout,
            usernameTextInputLayout,
            passwordTextInputLayout,
            againPasswordTextInputLayout,
            firebaseAuth,
            databaseReference
        )

        initializeFirebase()


    }

    private fun initializeFirebase() {
        firebaseAuth = FirebaseAuth.getInstance()

    }

    private fun initializeViews() {
        usernameTextInputLayout = fragmentSignupBinding.textInputLayoutSignupName
        emailTextInputLayout = fragmentSignupBinding.textInputLayoutSignupEmail
        passwordTextInputLayout = fragmentSignupBinding.textInputLayoutSignupPassword
        againPasswordTextInputLayout = fragmentSignupBinding.textInputLayoutSignupPasswordAgain
    }

    public class SignupFragmentEventListener(
        val activity: Activity,
        val emailTextInputLayout: TextInputLayout,
        val usernameTextInputLayout: TextInputLayout,
        val passwordTextInputLayout: TextInputLayout,
        val againPasswordTextInputLayout: TextInputLayout,
        val firebaseAuth: FirebaseAuth,
        var databaseReference: DatabaseReference
    ) {
        private var checkTextInputs:CheckTextInputs = CheckTextInputs(
            activity,
            emailTextInputLayout,
            passwordTextInputLayout,
            againPasswordTextInputLayout,
            usernameTextInputLayout,
            null,
            null,
            null,
            null,
            null,
            null
        )

        public fun signupBtnOnClickListener(view: View) {
            val isUsernameValid = checkTextInputs.checkIsUserNameValid()
            val isPasswordValid = checkTextInputs.checkIsPasswordValid()
            val isAgainPasswordValid = checkTextInputs.checkIsPasswordsEqual()
            val isEmailValid = checkTextInputs.checkIsEmailValid()

            if (isUsernameValid and isPasswordValid and isAgainPasswordValid and isEmailValid){
                registerTheUser()
            }
        }

        private fun registerTheUser() {
            firebaseAuth.createUserWithEmailAndPassword(emailTextInputLayout.editText!!.text.toString().trim(),passwordTextInputLayout.editText!!.text.toString().trim())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        val firebaseUser = firebaseAuth.currentUser
                        if (firebaseUser!=null){
                            val uid = firebaseUser.uid
                            databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid)
                            val hashMap = HashMap<String,String>()
                            hashMap["id"] = uid
                            hashMap["username"] = usernameTextInputLayout.editText!!.text.toString().trim()
                            hashMap["imageURL"] = "default"
                            hashMap["email"] = emailTextInputLayout.editText!!.text.toString().trim()
                            databaseReference.setValue(hashMap).addOnCompleteListener {
                                if (task.isSuccessful){
                                    activity.findNavController(R.id.fragmentContainerView_main_activity)
                                        .navigate(SignupFragmentDirections.actionSignupFragmentToAppHomeFragment())
                                    /*
                                    val manager: FragmentManager =
                                        (activity as MainActivity).supportFragmentManager
                                    val trans: FragmentTransaction = manager.beginTransaction()
                                    trans.remove(Fragment(R.layout.fragment_signup))
                                    trans.commit()
                                    manager.popBackStack()
                                    */
                                }else{
                                    Toast.makeText(activity,activity.getString(R.string.signup_toast_error_cannot_register),Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }else{
                        Toast.makeText(activity,activity.getString(R.string.signup_toast_error_cannot_register),Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(activity,it.message,Toast.LENGTH_SHORT).show()
                }
        }

        public fun loginTextViewOnClick(view: View){
            activity.findNavController(R.id.fragmentContainerView_main_activity)
                .navigate(SignupFragmentDirections.actionSignupFragmentToLoginFragment())
        }
        public fun onEditTextChangedListener(editable:Editable,editTextFlag:Int){
            when(editTextFlag){
                1 ->{
                    if(checkTextInputs.checkIsUserNameValid()){
                        usernameTextInputLayout.error = null
                    }
                }
                2 ->{
                    if (checkTextInputs.checkIsEmailValid()){
                        emailTextInputLayout.error = null
                    }
                }
                3 ->{
                    if (checkTextInputs.checkIsPasswordValid()){
                        passwordTextInputLayout.error = null
                    }
                }
                4 ->{
                    if (checkTextInputs.checkIsPasswordsEqual()){
                        againPasswordTextInputLayout.error = null
                    }
                }
            }
        }
    }
}
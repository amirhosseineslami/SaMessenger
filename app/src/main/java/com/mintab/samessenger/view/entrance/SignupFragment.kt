package com.mintab.samessenger.view.entrance

import android.app.Activity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.mintab.samessenger.R
import com.mintab.samessenger.databinding.FragmentSignupBinding
import com.mintab.samessenger.model.UserData
import com.mintab.samessenger.worker.CheckTextInputs
import pl.droidsonroids.gif.GifImageView

private const val TAG = "SignupFragment"


class SignupFragment : Fragment() {
    private lateinit var fragmentSignupBinding: FragmentSignupBinding
    private lateinit var usernameTextInputLayout: TextInputLayout
    private lateinit var emailTextInputLayout: TextInputLayout
    private lateinit var passwordTextInputLayout: TextInputLayout
    private lateinit var againPasswordTextInputLayout: TextInputLayout
    private lateinit var firstNameTextInputLayout: TextInputLayout
    private lateinit var lastNameTextInputLayout:TextInputLayout
    private lateinit var firebaseAuth:FirebaseAuth
    private lateinit var loadingImageView: GifImageView

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
        initializeFirebase()


        // set listener
        fragmentSignupBinding.listener = SignupFragmentEventListener(
            requireActivity(),
            emailTextInputLayout,
            usernameTextInputLayout,
            passwordTextInputLayout,
            againPasswordTextInputLayout,
            firstNameTextInputLayout,
            lastNameTextInputLayout,
            firebaseAuth,
            loadingImageView
        )


    }

    private fun initializeFirebase() {
        firebaseAuth = FirebaseAuth.getInstance()
    }

    private fun initializeViews() {
        usernameTextInputLayout = fragmentSignupBinding.textInputLayoutSignupName
        emailTextInputLayout = fragmentSignupBinding.textInputLayoutSignupEmail
        passwordTextInputLayout = fragmentSignupBinding.textInputLayoutSignupPassword
        againPasswordTextInputLayout = fragmentSignupBinding.textInputLayoutSignupPasswordAgain
        firstNameTextInputLayout = fragmentSignupBinding.textInputLayoutSignupFirstname
        lastNameTextInputLayout = fragmentSignupBinding.textInputLayoutSignupLastname
        loadingImageView = fragmentSignupBinding.imageViewSignupLoading
    }

    class SignupFragmentEventListener(
        val activity: Activity,
        val emailTextInputLayout: TextInputLayout,
        val usernameTextInputLayout: TextInputLayout,
        val passwordTextInputLayout: TextInputLayout,
        val againPasswordTextInputLayout: TextInputLayout,
        val firstNameTextInputLayout: TextInputLayout,
        val lastnameTextInputLayout: TextInputLayout,
        val firebaseAuth: FirebaseAuth,
        val loadingImageView: GifImageView
    ) {
       // private lateinit var progressDialog:ProgressDialog
        private var checkTextInputs:CheckTextInputs = CheckTextInputs(
            activity,
            emailTextInputLayout,
            passwordTextInputLayout,
            againPasswordTextInputLayout,
            usernameTextInputLayout,
            null,
            firstNameTextInputLayout,
            lastnameTextInputLayout,
            null,
            null,
            null,
            null
        )

        fun signupBtnOnClickListener(view: View) {
            val isUsernameValid = checkTextInputs.checkIsUserNameValid()
            val isPasswordValid = checkTextInputs.checkIsPasswordValid()
            val isAgainPasswordValid = checkTextInputs.checkIsPasswordsEqual()
            val isEmailValid = checkTextInputs.checkIsEmailValid()
            val isFirstNameValid = checkTextInputs.checkIsNameValid()
            val isLastNameValid = checkTextInputs.checkIsLastNameValid()

            if (isUsernameValid and isPasswordValid and isAgainPasswordValid and isEmailValid and isFirstNameValid and isLastNameValid){
                registerTheUser(view as Button)
            }
        }

        private fun registerTheUser(signupBtn:Button) {
            loadingImageView.visibility = View.VISIBLE
            signupBtn.text = ""
            signupBtn.isEnabled = false

            firebaseAuth.createUserWithEmailAndPassword(emailTextInputLayout.editText!!.text.toString().trim(),passwordTextInputLayout.editText!!.text.toString().trim())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful){
                        showProgressDialog()
                        val firebaseUser = firebaseAuth.currentUser
                        if (firebaseUser!=null){
                            val username = usernameTextInputLayout.editText!!.text.toString().trim()
                            val email = emailTextInputLayout.editText!!.text.toString().trim()
                            val password = passwordTextInputLayout.editText!!.text.toString().trim()
                            val uid = firebaseUser.uid
                            val databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(uid)
                            HashMap<String,String>()
                            databaseReference.setValue(UserData(uid,null,username,password,firstNameTextInputLayout.editText!!.text.toString().trim(),lastnameTextInputLayout.editText!!.text.toString().trim(),email))
                            .addOnCompleteListener {
                                loadingImageView.visibility = View.INVISIBLE
                                signupBtn.text = activity.getText(R.string.signup_button_text)
                                signupBtn.isEnabled = true
                                if (task.isSuccessful){
                                    Log.i(TAG, "registerTheUser: succeed")
                                    activity.findNavController(R.id.fragmentContainerView_main_activity)
                                        .navigate(SignupFragmentDirections.actionSignupFragmentToAppHomeFragment())
                                    //progressDialog.dismiss()
                                }else{
                                    Toast.makeText(activity,activity.getString(R.string.signup_toast_error_cannot_register),Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }else{
                        Toast.makeText(activity,activity.getString(R.string.signup_toast_error_cannot_register_not_completed),Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener {
                    Log.e(TAG, "registerTheUser: ${it.message}", )
                    Toast.makeText(activity,it.message,Toast.LENGTH_SHORT).show()
                }
        }

        private fun showProgressDialog() {
            /*
             progressDialog = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) //Check if Android API Level is greater than or equal to 30
            {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.getDefaultNightMode()) //This is optional. This will enable Android's Autotheming for the entire App
                ProgressDialog(activity,ProgressDialog.THEME_FOLLOW_SYSTEM) // Enables AutoTheming for the ProgressDialog instance.
            } else //Autotheming not compatible
            {
                ProgressDialog(activity, ProgressDialog.THEME_DARK) // or any other constructors mentioned above
            }
            progressDialog.setOnCancelListener {
                firebaseAuth.signOut()
            }
            progressDialog.isCancelable = false
            progressDialog.show()
*/
        }

        public fun loginTextViewOnClick(view: View){
            activity.findNavController(R.id.fragmentContainerView_main_activity)
                .navigate(SignupFragmentDirections.actionSignupFragmentToLoginFragment())
        }
        public fun onEditTextChangedListener(editable:Editable,editTextFlag:Int){
            when(editTextFlag){
                -1 ->{
                    if (checkTextInputs.checkIsNameValid()){
                        firstNameTextInputLayout.error = null
                    }
                }
                0 ->{
                    if (checkTextInputs.checkIsLastNameValid()){
                        lastnameTextInputLayout.error = null
                    }
                }
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
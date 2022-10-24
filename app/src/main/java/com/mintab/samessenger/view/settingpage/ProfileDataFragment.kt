package com.mintab.samessenger.view.settingpage

import android.app.Activity
import android.content.ContentValues.TAG
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputLayout
import com.mintab.samessenger.R
import com.mintab.samessenger.databinding.FragmentProfileDataBinding
import com.mintab.samessenger.model.UserData
import com.mintab.samessenger.viewmodel.MainActivityViewModel
import com.mintab.samessenger.worker.SaveThePictureIntoStorage
import pl.droidsonroids.gif.GifImageView


public const val PROFILE_PICTURE_CLOUDE_EXT_NAME = "_ProfilePicture"
private const val TAG = "ProfileDataFragment"
const val EXTERNAL_STORAGE_PERMISSION_REQ_CODE = 5432
val externalStoragePermission = arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

class ProfileDataFragment : Fragment() {
    private lateinit var fragmentProfileDataBinding: FragmentProfileDataBinding
    private lateinit var profilePictureIV: ImageView
    private lateinit var firstnameTextInputLayout: TextInputLayout
    private lateinit var lastnameTextInputLayout: TextInputLayout
    private lateinit var usernameTextInputLayout: TextInputLayout
    private lateinit var emailTextInputLayout: TextInputLayout
    private lateinit var profileGifLoaderIV: GifImageView
    private var currentUserData: UserData? = UserData()
    private lateinit var listener: ProfileDataFragmentEventListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentProfileDataBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_profile_data, container, false)
        return fragmentProfileDataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        receiveIncomeArgs()
        initializing()
        setProfilePicture()
    }

    private fun receiveIncomeArgs() {
        val data = requireArguments().getStringArray("currentUserData")
        if (data != null) currentUserData =
            UserData(data[0], data[1], data[2], data[3], data[4], data[5], data[6])
    }

    private fun initializing() {
        initViews()
        // set listener
        listener =
            ProfileDataFragmentEventListener(requireActivity(), galleryActivityResultLauncher())
        fragmentProfileDataBinding.listener = listener
        fragmentProfileDataBinding.userdata = currentUserData
        //setProfilePicture()
    }

    private fun setProfilePicture(){

        val initProfList = SaveThePictureIntoStorage.getProfilePictures(requireActivity(),currentUserData!!)

        if(initProfList.isNotEmpty()) {
            Glide.with(requireContext())
                .load(initProfList.last())
                .into(profilePictureIV)
        }
        Log.i(TAG, "setProfilePicture: 1")
        MainActivityViewModel.getProfilePicturesLiveData()
            .observe(viewLifecycleOwner) {
                Log.i(TAG, "setProfilePicture: 2")
                if (it.isNotEmpty()) {
                    Log.i(TAG, "livedata${it.size}: $it")
                    Glide.with(requireContext())
                        .load(it.last())
                        .into(profilePictureIV)
                }
            }
/*
        Glide.with(requireContext())
            .load(imageRef.getFile(Uri.parse(currentUserData?.imageURL)))
            .error(R.drawable.forgot_password)
            .into(profilePictureIV)
        */
    }

    override fun onResume() {
        super.onResume()
        setProfilePicture()
    }

    private fun galleryActivityResultLauncher(): ActivityResultLauncher<Intent> {
        return registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            if (it.resultCode == Activity.RESULT_OK) {
                profilePictureIV.setImageURI(it.data!!.data)
                uploadProfilePic(it.data!!.data)
            }
        }
    }

    private fun uploadProfilePic(uri: Uri?) {
        profileGifLoaderIV.visibility = View.VISIBLE
        if (uri == null) {
            // no pic selected
            Toast.makeText(
                requireContext(),
                requireActivity().getString(R.string.fragment_profile_set_profpic_toast_not_selected),
                Toast.LENGTH_SHORT
            ).show()
        } else {

            SaveThePictureIntoStorage.addProfilePicture(
                requireActivity(),
                currentUserData!!,
                profilePictureIV,
                uri
            )
/*
            task.continueWithTask { task1 ->
                task1.addOnCompleteListener {
                    if (!task1.isSuccessful) {
                        throw it.exception!!
                    }
                }
                task1.addOnFailureListener {
                    throw it
                }
            }*/

        }
    }

    private fun initViews() {
        profilePictureIV = fragmentProfileDataBinding.fragmentProfileDataAccountImageView
        firstnameTextInputLayout = fragmentProfileDataBinding.textInputLayoutProfileFirstname
        lastnameTextInputLayout = fragmentProfileDataBinding.textInputLayoutProfileLastname
        usernameTextInputLayout = fragmentProfileDataBinding.textInputLayoutProfileUsername
        emailTextInputLayout = fragmentProfileDataBinding.textInputLayoutProfileEmail
        profileGifLoaderIV = fragmentProfileDataBinding.fragmentProfileDataProfilePicGifLoader
    }

    class ProfileDataFragmentEventListener(
        val activity: Activity,
        val launcher: ActivityResultLauncher<Intent>
    ) {
        fun onProfilePictureClick(view: View?) {
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    externalStoragePermission[0]
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                openGalleryToChooseProfilePicture()
            } else {
                ActivityCompat.requestPermissions(
                    activity, externalStoragePermission,
                    EXTERNAL_STORAGE_PERMISSION_REQ_CODE
                )
            }
        }

        private fun openGalleryToChooseProfilePicture() {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            launcher.launch(intent)
        }

        fun onChangePasswordBtnOnClick(view: View) {

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == EXTERNAL_STORAGE_PERMISSION_REQ_CODE) {
            listener.onProfilePictureClick(null)
        }
    }

}
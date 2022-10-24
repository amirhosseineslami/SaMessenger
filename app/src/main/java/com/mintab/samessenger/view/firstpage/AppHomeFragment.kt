package com.mintab.samessenger.view.firstpage

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.mintab.samessenger.R
import com.mintab.samessenger.adapter.ViewPagerAdapter
import com.mintab.samessenger.databinding.FragmentAppHomeBinding
import com.mintab.samessenger.model.UserData
import com.mintab.samessenger.viewmodel.MainActivityViewModel
import com.mintab.samessenger.worker.SaveThePictureIntoStorage
import com.mintab.samessenger.worker.SetUserStatus
import dev.shreyaspatil.MaterialDialog.AbstractDialog
import dev.shreyaspatil.MaterialDialog.MaterialDialog

private const val TAG = "AppHomeFragment"
private const val PERMISSION_READ_EXTERNAL_REQ_CODE = 2431
public const val SHARED_PREFERENCES_NAME = "SharedPreferences"
public const val SHARED_PREFERENCES_USER_DATA_KEY = "userData"

class AppHomeFragment : Fragment() {
    private lateinit var fragmentAppHomeBinding: FragmentAppHomeBinding
    private lateinit var profileImageView: ImageView
    private lateinit var profileUsername: TextView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var headerView: View
    private var currentUserData: UserData? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentAppHomeBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_app_home, container, false)
        return fragmentAppHomeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializing()
    }

    override fun onResume() {
        super.onResume()
        initProfilePicture()
    }

    private fun initializing() {
        initDatabaseReference()
        initializeViews()
        initUserDataFromLocalStorage()
        initViewPager()
        initListener()
        initViewModelLiveDataListener()
    }
private fun initProfilePicture(){

    val existProfilesInLocalStorage = SaveThePictureIntoStorage.getProfilePictures(requireActivity(),currentUserData!!)
if (existProfilesInLocalStorage.size == 0){
    return
}
    Glide.with(requireContext())
        .load(existProfilesInLocalStorage.last())
        .into(profileImageView)

    Glide.with(requireContext())
        .load(existProfilesInLocalStorage.last())
        .into(headerView.findViewById(R.id.fragment_app_home_navdrawer_header_profile))

}
    private fun initViewModelLiveDataListener() {
        initProfilePicture()
        MainActivityViewModel.getProfilePicturesLiveData()
            .observe(viewLifecycleOwner) {

                if (it.isNotEmpty()) {
                    Log.i(TAG, "livedata${it.size}: $it")
                    Glide.with(requireContext())
                        .load(it.last())
                        .into(profileImageView)

                    Glide.with(requireContext())
                        .load(it.last())
                        .into(headerView.findViewById(R.id.fragment_app_home_navdrawer_header_profile))
                }
            }
    }

    private fun initUserDataFromLocalStorage() {
        val sharedPreferences =
            requireActivity().getSharedPreferences(SHARED_PREFERENCES_NAME, Activity.MODE_PRIVATE)
        if (sharedPreferences?.getString("id",null) != null) {
            val savedUser = UserData(sharedPreferences.getString("id",null),sharedPreferences.getString("imageURL",null),sharedPreferences.getString("username",null),sharedPreferences.getString("password",null),sharedPreferences.getString("firstname",null),sharedPreferences.getString("lastname",null),sharedPreferences.getString("email",null))
            if (savedUser.id != null) {
                currentUserData = savedUser
                setUserInformationIntoViews()
            }
        }
    }

    private fun setUserInformationIntoViews() {
        profileUsername.text = currentUserData!!.username
        updateNavDrawerHeaderInformation()
    }

    private fun initDatabaseReference() {
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        databaseReference =
            FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.value != null) {
                    currentUserData = snapshot.getValue(UserData::class.java)
                }
                val editor = requireActivity().getSharedPreferences(
                    SHARED_PREFERENCES_NAME,
                    Activity.MODE_PRIVATE
                ).edit()
                editor.putString("id", currentUserData!!.id)
                editor.putString("imageURL", currentUserData!!.imageURL)
                editor.putString("email", currentUserData!!.email)
                editor.putString("firstname", currentUserData!!.firstname)
                editor.putString("lastname", currentUserData!!.lastname)
                editor.putString("password", currentUserData!!.password)
                editor.putString("username", currentUserData!!.username)
                editor.apply()
                setUserInformationIntoViews()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "onCancelled: ${error.message}")
            }

        })
    }

    private fun updateNavDrawerHeaderInformation() {
        headerView.findViewById<TextView>(R.id.fragment_app_home_navdrawer_header_name).text =
            "${currentUserData!!.firstname} ${currentUserData!!.lastname}"
        headerView.findViewById<TextView>(R.id.fragment_app_home_navdrawer_header_username).text =
            currentUserData!!.username
        SaveThePictureIntoStorage.getProfilePictures(requireActivity(),currentUserData!!)
    }

    private fun initListener() {
        fragmentAppHomeBinding.listener =
            AppHomeFragmentEventListener(requireActivity(), drawerLayout, navigationView)
    }


    private fun initViewPager() {
        val viewPagerAdapter =
            ViewPagerAdapter(
                requireActivity().supportFragmentManager,
                requireActivity().lifecycle
            )

        viewPager2.adapter = viewPagerAdapter

        TabLayoutMediator(
            tabLayout, viewPager2
        ) { tab, position ->
            when (position) {
                0 -> {
                    tab.text =
                        requireActivity().getString(R.string.fragment_contacts_main_textView_text)
                }
                1 -> {
                    tab.text =
                        requireActivity().getString(R.string.fragment_chats_main_textView_text)
                }
                2 -> {
                    tab.text =
                        requireActivity().getString(R.string.fragment_groups_main_textView_text)
                }
            }
        }.attach()

    }

    private fun initializeViews() {
        profileImageView = fragmentAppHomeBinding.fragmentAppHomeToolbarImageProfilePicture
        profileUsername = fragmentAppHomeBinding.fragmentAppHomeToolbarTextviewProfileUsername
        tabLayout = fragmentAppHomeBinding.fragmentAppHomeTablayout
        viewPager2 = fragmentAppHomeBinding.fragmentAppHomeViewpager


        // navigation drawer
        navigationView = fragmentAppHomeBinding.fragmentAppHomeNavigationView
        drawerLayout = fragmentAppHomeBinding.fragmentAppHomeDrawerLayout


        // header view
        headerView = navigationView.getHeaderView(0)
        headerView.findViewById<ImageView>(R.id.fragment_app_home_navdrawer_header_profile)
            .setOnClickListener(headerViewProfileOnClickListener())
        headerView.findViewById<ImageButton>(R.id.fragment_app_home_navdrawer_header_button_logout)
            .setOnClickListener(onHeaderViewLogoutBtnClick())
    }

    private fun onHeaderViewLogoutBtnClick(): View.OnClickListener {
        return View.OnClickListener {
            MaterialDialog.Builder(requireActivity())
                .setTitle(requireActivity().getString(R.string.fragment_app_home_material_dialog_logout_title))
                .setMessage(requireActivity().getString(R.string.fragment_app_home_material_dialog_logout_message))
                .setCancelable(true)
                .setPositiveButton(
                    requireActivity().getString(R.string.fragment_app_home_material_dialog_logout_positive_button)
                ,AbstractDialog.OnClickListener { dialogInterface, which ->
                        SetUserStatus.setStatus(false)
                        FirebaseAuth.getInstance().signOut()
                        findNavController().navigate(AppHomeFragmentDirections.actionAppHomeFragmentToLoginFragment())
                        dialogInterface.cancel()
                    })
                .setNegativeButton(
                    requireActivity().getString(R.string.fragment_app_home_material_dialog_logout_negative_button)
                ) { dialogInterface, which ->
                    dialogInterface.cancel()
                }
                .build()
                .show()
        }
    }

    private fun headerViewProfileOnClickListener(): View.OnClickListener {
        return View.OnClickListener {
            if (currentUserData != null) {
                if (currentUserData!!.imageURL == null) currentUserData!!.imageURL = ""
                findNavController().navigate(
                    AppHomeFragmentDirections.actionAppHomeFragmentToProfileDataFragment(
                        arrayOf(
                            currentUserData!!.id!!,
                            currentUserData!!.imageURL!!,
                            currentUserData!!.username!!,
                            currentUserData!!.password!!,
                            currentUserData!!.firstname!!,
                            currentUserData!!.lastname!!,
                            currentUserData!!.email!!
                        )
                    )
                )
            }
        }
    }

    class AppHomeFragmentEventListener(
        val activity: Activity,
        val drawerLayout: DrawerLayout,
        val navigationView: NavigationView
    ) {
        fun profilePicOnClick(view: View) {
            //drawerLayout.openDrawer(GravityCompat.START)
            drawerLayout.openDrawer(navigationView)
        }
    }
}
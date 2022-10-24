package com.mintab.samessenger.worker

import android.app.Activity
import android.content.Context
import android.net.Uri
import android.service.autofill.RegexValidator
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.core.net.toFile
import com.anggrayudi.storage.file.CreateMode
import com.anggrayudi.storage.file.child
import com.anggrayudi.storage.file.makeFolder
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.mintab.samessenger.R
import com.mintab.samessenger.model.UserData
import com.mintab.samessenger.view.settingpage.PROFILE_PICTURE_CLOUDE_EXT_NAME
import com.mintab.samessenger.viewmodel.MainActivityViewModel
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import javax.annotation.RegEx

private const val TAG = "SaveThePictureIntoStora"

class SaveThePictureIntoStorage() {
    companion object {
        var saveThePictureIntoStorage: SaveThePictureIntoStorage? = null
        public fun getInstance(): SaveThePictureIntoStorage? {
            if (saveThePictureIntoStorage == null) {
                saveThePictureIntoStorage = SaveThePictureIntoStorage()
            }
            return saveThePictureIntoStorage
        }

        /*
        public fun updateProfilePicture(activity: Activity,currentUserData: UserData,imageView: ImageView,uri:Uri, profilePictureNumber:Int){
            val file = File(activity.filesDir.absolutePath + "/${currentUserData.imageURL}")
            if (file.exists()) {
                file.delete()
                Log.i(TAG, "savePicture: previous profile is deleting")
            }
            val fileType = GetFileTypeExtension.getFileType(activity, uri)
            val storageReference = FirebaseStorage.getInstance().getReference("Uploads/${currentUserData.id}")
            val firebaseRef = FirebaseDatabase.getInstance().getReference("Users")
            val imageRef =
                storageReference.child("${currentUserData.id + PROFILE_PICTURE_CLOUDE_EXT_NAME+profilePictureNumber}.$fileType")

            imageRef.putFile(uri)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        currentUserData.imageURL = "${currentUserData.id + PROFILE_PICTURE_CLOUDE_EXT_NAME+profilePictureNumber}.$fileType"
                        Toast.makeText(activity, "uploaded", Toast.LENGTH_SHORT).show()
                        val map = HashMap<String,Any>()
                        map[currentUserData.id!!] = currentUserData
                        firebaseRef
                            .updateChildren(map)
                            .addOnSuccessListener {
                                Log.i(ContentValues.TAG, "uploadProfilePic: succeed to update")
                            }
                    }
                }.addOnFailureListener {
                    Toast.makeText(activity, "failed ${it.message}", Toast.LENGTH_SHORT)
                        .show()
                }
            //savePicture(activity, imageView, currentUserData.id!!, "-",)
        }
*/

        public fun getProfilePictures(
            activity: Activity,
            currentUserData: UserData
        ): ArrayList<File> {
            val file = File(activity.filesDir.absolutePath + "/${currentUserData.id}")
            val firstInitForProfile: ArrayList<File> = ArrayList()
            if (firstInitForProfile.size > 0) {
                firstInitForProfile.addAll(file.listFiles()!!)
                firstInitForProfile.sortBy { file -> file.name }
            }
            /*if (file.exists()) {
                file.delete()
                Log.i(TAG, "getProfilesPictures: previous profile is deleting")
            }*/
            val storageReference =
                FirebaseStorage.getInstance().getReference("Uploads/${currentUserData.id}")

            var firebaseStorageProfilesList: MutableList<StorageReference>?

            storageReference.listAll()
                .addOnSuccessListener {
                    firebaseStorageProfilesList = it.items
                    Log.i(
                        TAG,
                        "getProfilePicturesItems: ${it.items.size} & ${(firebaseStorageProfilesList)?.size}"
                    )

                    checkIfFirebaseProfilesPicturesAreInTheLocalStorage(
                        activity,
                        currentUserData,
                        firebaseStorageProfilesList
                    )

                }
                .addOnFailureListener {
                    Log.i(TAG, "getProfilePictures: ${it.message}")
                    return@addOnFailureListener
                }
                .addOnCanceledListener {
                    Log.i(TAG, "getProfilePictures: cancelled")
                    return@addOnCanceledListener
                }
            return firstInitForProfile
        }

        private fun checkIfFirebaseProfilesPicturesAreInTheLocalStorage(
            activity: Activity,
            currentUserData: UserData,
            firebaseStorageProfilesList: MutableList<StorageReference>?
        ) {

            val folderOfProfilesPicture =
                File(activity.filesDir.absolutePath + "/${currentUserData.id}")

            var profilesPictureLocalFilesList: List<File> = ArrayList()

            if (folderOfProfilesPicture.listFiles() != null) {
                profilesPictureLocalFilesList =
                    folderOfProfilesPicture.listFiles()!!.toList()
            }
            if (firebaseStorageProfilesList?.size!! > 0) {
                for (i in firebaseStorageProfilesList.indices) {
                    Log.i(TAG, "getProfilePictures started searching1:")
                    for (file in profilesPictureLocalFilesList) {
                        Log.i(TAG, "getProfilePictures started searching2: ${file.name}")
                        if (firebaseStorageProfilesList[i].name == file.name) {
                            // the file exists on local storage
                            MainActivityViewModel.addToProfilePicturesList(file)
                        } else {
                            val fileInTheFolderOfProfilesPicture =
                                File(activity.filesDir.absolutePath + "/${currentUserData.id}/${firebaseStorageProfilesList[i].name}")
                            firebaseStorageProfilesList[i].getFile(fileInTheFolderOfProfilesPicture)
                                .addOnSuccessListener {
                                    Log.i(TAG, "getProfilePictures: succeed")
                                    MainActivityViewModel.addToProfilePicturesList(
                                        fileInTheFolderOfProfilesPicture
                                    )
                                }.addOnFailureListener {
                                    Log.e(TAG, "getProfilePictures: ${it.message}")
                                }
                        }
                    }
                }
            }
        }

        public fun addProfilePicture(
            activity: Activity,
            currentUserData: UserData,
            imageView: ImageView,
            uri: Uri
        ) {
            val folder =
                File(activity.filesDir.absolutePath + "/${currentUserData.id}")
            if (!folder.exists()) {
                try {
                    folder.mkdir()
                    Log.i(
                        TAG, "addProfilePicture make folder : " + folder.makeFolder(
                            activity,folder.absolutePath ,
                            CreateMode.CREATE_NEW
                        )
                    )
                } catch (e: java.lang.Exception) {
                    Log.e(TAG, "addProfilePicture: ${e.message}")
                }
            }
            val currentProfilesNumber = MainActivityViewModel.getProfilePicturesQuantity()

            Log.i(TAG, "addProfilePicture: currentProfileQua:$currentProfilesNumber")
            if (currentProfilesNumber > 98) {
                Toast.makeText(activity, "Maximum Profiles Pictures!", Toast.LENGTH_SHORT)
                    .show()
                return
            }

            savePicture(
                activity,
                imageView,
                currentUserData.id!!,
                currentProfilesNumber.toString(),
                uri
            )
        }

        public fun deleteProfilePicture(
            activity: Activity,
            currentUserData: UserData,
            whichNumber: Int
        ) {
            val folder =
                File(activity.filesDir.absolutePath + "/${currentUserData.id}+$PROFILE_PICTURE_CLOUDE_EXT_NAME")
            val pathReference = FirebaseStorage.getInstance().getReference("Uploads/${currentUserData.id}")
            pathReference.child(currentUserData.id + whichNumber + PROFILE_PICTURE_CLOUDE_EXT_NAME)
                .delete()
                .addOnSuccessListener {
                    folder.listFiles()!![whichNumber].delete()
                }.addOnFailureListener {
                    Log.e(TAG, "deleteProPic: ${it.message}")
                }
        }

        public fun savePicture(
            activity: Activity,
            imageView: ImageView,
            folderName: String,
            filename: String,
            uri: Uri
        ) {
            /*
            val file = File(activity.filesDir.absolutePath + "/${currentUserData.imageURL}")
            if (file.exists()) {
                Log.i(TAG, "savePicture: profile exists")
                Glide.with(activity)
                    .load(file)
                    .error(R.drawable.forgot_password)
                    .into(imageView)
            }*/

            val savingFileName = filename + "." + GetFileTypeExtension.getFileType(activity, uri)
            val pathReference =
                FirebaseStorage.getInstance().getReference("Uploads/${folderName}")
            pathReference.child(savingFileName).putFile(uri)
                .addOnSuccessListener {
                    startSaving(
                        activity,
                        savingFileName,
                        folderName,
                        uri,
                        imageView
                    )
                }.addOnFailureListener {
                    Log.e(
                        TAG,
                        "updateNavDrawerHeaderInformation: ${it.message}"
                    )
                }
        }

        private fun startSaving(
            activity: Activity,
            filename: String,
            folderName: String,
            uri: Uri,
            imageView: ImageView,
        ) {
            //**************************************************

            //Checking the availability state of the External Storage.
            val internalDirectory = activity.filesDir


            //if (!Environment.MEDIA_MOUNTED.equals(internalDirectory)) { //If it isn't mounted - we can't write into it. Toasty.error(activity,"media unmounted").show()return }

            //Create a new file that points to the root directory, with the given name:
            val fileDir = File(internalDirectory, folderName)

            //This point and below is responsible for the write operation
            try {
                /*
                for (item in fileDir.listFiles()!!) {
                    if (item.isFile) {
                        if (item.name == filename) {
                            Log.i(TAG, "savePicture: Item is already exist")
                            return
                        }
                    }
                }
                */
                val file = File(fileDir, activity.filesDir.absolutePath + "/$folderName/$filename")
                var outputStream: OutputStream
                outputStream = activity.openFileOutput(filename, Context.MODE_PRIVATE)
                outputStream.write(uri.toFile().readBytes())
                file.mkdir()
                //folder.child(filename).createNewFile()
                //file.createNewFile()
                //second argument of FileOutputStream constructor indicates whether
                //to append or create new file if one exists
                outputStream = FileOutputStream(file, true)
                outputStream.flush()
                outputStream.close()
            } catch (e: Exception) {
                Log.e(TAG, "savePicture: ${e.message}")
            }
            // ********************************

            val folder = File(activity.filesDir.absolutePath + "/$folderName")
            //if (!folder.exists()) { folder.makeFolder(activity, folderName, CreateMode.CREATE_NEW) }
            val file = File(filename)
            var outputStream: FileOutputStream

            try {
                outputStream = folder.outputStream()
                //outputStream = activity.openFileOutput(filename, Activity.MODE_PRIVATE)
                outputStream.write(uri.toFile().readBytes())
                outputStream.close()
            } catch (e: java.lang.Exception) {
                Log.e(TAG, "startSaving: ${e.message}")
            }
            Glide.with(activity)
                .load(file)
                .error(R.drawable.forgot_password)
                .into(imageView)

            MainActivityViewModel.addToProfilePicturesList(file)
        }
    }
/*
            //Checking the availability state of the External Storage.
            val internalDirectory = activity.getExternalFilesDir(null)


            //if (!Environment.MEDIA_MOUNTED.equals(internalDirectory)) { //If it isn't mounted - we can't write into it. Toasty.error(activity,"media unmounted").show()return }

            //Create a new file that points to the root directory, with the given name:
            val fileDir = File(internalDirectory, folderName)

            //This point and below is responsible for the write operation
            try {
                for (item in fileDir.listFiles()!!){
                    if (item.isFile){
                        if (item.name == filename){
                            Log.i(TAG, "savePicture: Item is already exist")
                            return
                        }
                    }
                }
                val file: File? = null

                var outputStream: OutputStream = FileOutputStream(file)
                outputStream = activity.openFileOutput(filename, Context.MODE_PRIVATE);
                outputStream.write(byteArray)
                file?.createNewFile()
                //second argument of FileOutputStream constructor indicates whether
                //to append or create new file if one exists
                outputStream = FileOutputStream(file, true)

                outputStream.flush()
                outputStream.close()
            } catch (e: Exception) {
                Log.e(TAG, "savePicture: ${e.message}")
            }
        */
}
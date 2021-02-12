package com.dbtechprojects.uploadtos3

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.amplifyframework.core.Amplify
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*


class MainActivity : AppCompatActivity() {

    private var mSelectedImageFileUri: Uri? = null





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        AmplifyInit().intializeAmplify(this@MainActivity)

        button.setOnClickListener {
            // CHECK PERMISSIONS

            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                == PackageManager.PERMISSION_GRANTED
            ) {
                showImageChooser(this@MainActivity)
            } else {
                /*Requests permissions to be granted to this application. These permissions
                 must be requested in your manifest, they should not be granted to your app,
                 and they should have protection level*/
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    3
                )
            }

        }

        GalleryButton.setOnClickListener {
            val intent = Intent(this@MainActivity, GalleryActivity::class.java)
            startActivity(intent)
        }


    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 2) {
                if (data != null) {
                    try {

                        // The uri of selected image from phone storage.
                        mSelectedImageFileUri = data.data!!

//                        Log.d("MainActivity", mSelectedImageFileUri.toString())
//
//                        // Convert Uri to File Path
//
//                        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
//                        val cursor = contentResolver.query(
//                            mSelectedImageFileUri!!,
//                            filePathColumn, null, null, null
//                        )
//                        cursor!!.moveToFirst()
//                        val columnIndex = cursor!!.getColumnIndex(filePathColumn[0])
//                        val picturePath = cursor!!.getString(columnIndex)
//                        cursor!!.close()
//                        // String picturePath contains the path of selected Image
//                        // String picturePath contains the path of selected Image
//                        var photoPath = picturePath

                        uploadFile(mSelectedImageFileUri!!)


                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@MainActivity,
                            "Image Selection Failed",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                }
            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            // A log is printed when user close or cancel the image selection.
            Log.e("Request Cancelled", "Image selection cancelled")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 3) {
            //If permission is granted
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showImageChooser(this@MainActivity)
            } else {
                //Displaying another toast if permission is not granted
                Toast.makeText(
                    this,
                    "permission denied",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun uploadFile(Uri: Uri) {

        println("Upload: $Uri")
        val exampleInputStream = getContentResolver().openInputStream(Uri)
        println("Upload: $exampleInputStream")

        val randomNumber = (1000..9999).random()

        exampleInputStream?.let {
            Amplify.Storage.uploadInputStream(
                    "UploadedFile" + randomNumber.toString(),
                    it,
                    { result -> Toast.makeText(this, "File has Successfully Uploaded:" + result.key, Toast.LENGTH_SHORT).show() },
                    { error -> Log.e("MyAmplifyApp", "Upload failed", error) }
        )
        }
    }

    fun showImageChooser(activity: Activity) {
        // An intent for launching the image selection of phone storage.
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        // Launches the image selection of phone storage using the constant code.
        activity.startActivityForResult(galleryIntent, 2)
    }
}
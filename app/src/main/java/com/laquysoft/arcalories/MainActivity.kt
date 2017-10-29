package com.laquysoft.arcalories

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.laquysoft.arcalories.databinding.ActivityMainBinding
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity() {

    internal var storage: FirebaseStorage?=null
    internal var storageReference: StorageReference?=null
    internal var firestoreDb : FirebaseFirestore?=null


    private val PICK_IMAGE_REQUEST = 1234
    private var filePath: Uri? = null

    private var imageRef: StorageReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var binding : ActivityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.presenter = this

        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.getReference()
        firestoreDb = FirebaseFirestore.getInstance()

    }

    private fun retrieveVisionAPIData() {
        firestoreDb!!.collection("images").document(imageRef!!.name)
                .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    if (firebaseFirestoreException != null) {
                        Log.e("MainActivity", firebaseFirestoreException.localizedMessage)
                    } else {
                        if (documentSnapshot!!.exists()) {
                            Log.d("MainActivity", documentSnapshot.data.toString())
                        } else {
                            Log.d("MainActivity", "Waiting for Vision API data...")
                        }
                    }
                }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK &&
                data != null && data.data !=null) {
            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                uploadImage()
            } catch (e:IOException) {
                e.printStackTrace()
            }
        }
    }

    fun onUploadClick() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_IMAGE_REQUEST)
    }


    private fun uploadImage() {
        if (filePath != null) {
            val progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleSmall)
            progressBar.visibility = View.VISIBLE

            imageRef = storageReference!!.child("images/" + UUID.randomUUID().toString())
            imageRef!!.putFile(filePath!!)
                    .addOnSuccessListener { result ->
                        progressBar.visibility = View.GONE
                        Toast.makeText(applicationContext, "Image uploaded", Toast.LENGTH_SHORT ).show()
                        retrieveVisionAPIData()
                    }
                    .addOnFailureListener {
                        progressBar.visibility = View.GONE
                        Toast.makeText(applicationContext, "Upload failed", Toast.LENGTH_SHORT ).show()
                    }
                    .addOnProgressListener { snapShot ->
                        progressBar.progress = (100 * snapShot.bytesTransferred/snapShot.totalByteCount).toInt()
                    }
        }
    }
}

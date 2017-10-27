package com.laquysoft.arcalories

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class MainActivity : AppCompatActivity() {

    internal var storage: FirebaseStorage?=null
    internal var storageReference: StorageReference?=null
    internal var firestoreDb : FirebaseFirestore?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.getReference()
        firestoreDb = FirebaseFirestore.getInstance()
    }
}

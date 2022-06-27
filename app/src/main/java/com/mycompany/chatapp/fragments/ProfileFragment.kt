package com.mycompany.chatapp.fragments

import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.ProgressBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.mycompany.chatapp.R
import com.mycompany.chatapp.model.User
import de.hdodenhof.circleimageview.CircleImageView
import androidx.activity.result.ActivityResultCallback

import androidx.activity.result.contract.ActivityResultContracts

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult


class ProfileFragment : Fragment() {

    companion object {
        const val IMAGE_REQUEST = 1
    }

    var firebaseUser: FirebaseUser? = null
    var databaseReference: DatabaseReference? = null
    var storageReference: StorageReference? = null

    var username: TextView? = null
    var imageProfile: CircleImageView? = null

    var imageUri: Uri? = null
    var uploadTask: StorageTask? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_profile, container, false)

        imageProfile = view.findViewById(R.id.profile_image)
        username = view.findViewById(R.id.user_name)

        storageReference = FirebaseStorage.getInstance().getReference("uploads")

        firebaseUser = FirebaseAuth.getInstance().currentUser
        databaseReference =
            FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)
        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user: User? = dataSnapshot.getValue(User::class.java)
                username?.text = user?.username
                if (user?.imageUrl.equals("default")) {
                    imageProfile?.setImageResource(R.mipmap.ic_launcher)
                } else {
                    Glide.with(context!!).load(user?.imageUrl).into(imageProfile!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value

            }
        })
        imageProfile?.setOnClickListener {
            openImage()
        }
        return view
    }

    private fun openImage(){
var intent= Intent()
        intent.setType("image/*")
        intent.action = Intent.ACTION_GET_CONTENT
        resultLauncher.launch(intent)
    }

    var resultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            doSomeOperations()
        }
    }

    private fun getFileExtension(uri:Uri):String?{
        var contentResolver = context?.contentResolver
        var mimeTypeMap:MimeTypeMap= MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun uploadImage(){
        var progressBar:ProgressDialog =ProgressDialog(context)
        progressBar.setMessage("Uploading")
        progressBar.show()

        if (imageUri!=null){
            var fileReference:StorageReference? = storageReference?.child(System.currentTimeMillis()+
            "." + getFileExtension(imageUri!!))

            uploadTask = fileReference?.getFile(imageUri!!)
            uploadTask.continueWith {

            }
        }
    }
}
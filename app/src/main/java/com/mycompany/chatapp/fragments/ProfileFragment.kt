package com.mycompany.chatapp.fragments

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.MimeTypeMap
import android.widget.TextView
import android.widget.Toast
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

import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.Task

import com.google.firebase.storage.UploadTask
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.*


class ProfileFragment : Fragment() {

    companion object {
        const val IMAGE_REQUEST = 1
    }

    private var firebaseUser: FirebaseUser? = null
    private var databaseReference: DatabaseReference? = null
    private var storageReference: StorageReference? = null

    private var username: TextView? = null
    private var imageProfile: CircleImageView? = null

    private var imageUri: Uri? = null
    private var uploadTask: StorageTask<*>? = null


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

    private fun openImage() {
        var intent = Intent()
        intent.setType("image/*")
        intent.action = Intent.ACTION_GET_CONTENT
        resultLauncher.launch(intent)
    }

    private var resultLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri = result.data?.data
            if (uploadTask != null && uploadTask!!.isInProgress) {
                Toast.makeText(context, "Upload in progress", Toast.LENGTH_SHORT).show()
            } else {
                uploadImage()
            }
        }
    }

    private fun getFileExtension(uri: Uri): String? {
        var contentResolver = context?.contentResolver
        var mimeTypeMap: MimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver?.getType(uri))
    }

    private fun uploadImage() {
        var progressBar = ProgressDialog(context)
        progressBar.setMessage("Uploading")
        progressBar.show()

        if (imageUri != null) {
            val fileReference: StorageReference = storageReference!!.child(
                System.currentTimeMillis()
                    .toString() + "." + getFileExtension(imageUri!!)
            )

            uploadTask = fileReference.putFile(imageUri!!)
            (uploadTask as UploadTask).continueWithTask { p0 ->
                if (!p0.isSuccessful) {
                    throw  p0.exception!!
                }
                fileReference.downloadUrl
            }.addOnCompleteListener { p0 ->
                if (p0.isSuccessful) {
                    var uri: Uri = p0.result
                    var mUri: String = uri.toString()

                    databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                        .child(firebaseUser!!.uid)

                    val hashList = hashMapOf<String, Any>()
                    hashList["imageUrl"] = mUri
                    databaseReference!!.updateChildren(hashList)

                    progressBar.dismiss()

                } else {
                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
                    progressBar.dismiss()
                }
            }.addOnFailureListener { p0 ->
                Toast.makeText(context, p0.message, Toast.LENGTH_SHORT).show()
                progressBar.dismiss()
            }

        } else {
            Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }
}
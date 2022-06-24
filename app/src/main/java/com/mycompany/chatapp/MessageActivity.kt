package com.mycompany.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.mycompany.chatapp.model.User
import de.hdodenhof.circleimageview.CircleImageView

class MessageActivity : AppCompatActivity() {

    var profileImage:CircleImageView?=null
    var userName:TextView?=null

    var firebaseUser:FirebaseUser?=null
    var databaseReference:DatabaseReference?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
    }

    private fun init(){
        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        supportActionBar?.title = " "
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        profileImage = findViewById(R.id.profile_image)
        userName = findViewById(R.id.user_name)

        var userId: String? = intent.getStringExtra("userId")

        firebaseUser = FirebaseAuth.getInstance().currentUser
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId!!)


        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user: User? = dataSnapshot.getValue(User::class.java)
                userName?.text = user?.username
                if (user?.imageUrl.equals("default")) {
                    profileImage?.setImageResource(R.mipmap.ic_launcher)
                } else {
                    Glide.with(this@MessageActivity).load(user?.imageUrl).into(profileImage!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value

            }
        })
    }
}
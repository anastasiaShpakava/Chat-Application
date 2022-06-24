package com.mycompany.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.mycompany.chatapp.model.User
import de.hdodenhof.circleimageview.CircleImageView

class MessageActivity : AppCompatActivity() {

    var profileImage: CircleImageView? = null
    var userName: TextView? = null

    var firebaseUser: FirebaseUser? = null
    var databaseReference: DatabaseReference? = null

    var imageSend: ImageButton? = null
    var textSend: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        init()
    }

    private fun init() {
        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        supportActionBar?.title = " "
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        profileImage = findViewById(R.id.profile_image)
        userName = findViewById(R.id.user_name)
        imageSend = findViewById(R.id.btn_send)
        textSend = findViewById(R.id.text_send)

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

        imageSend?.setOnClickListener {
            var msg: String = textSend?.text.toString()
            if (!msg.equals("")) {
                sendMessage(firebaseUser!!.uid, userId, msg)
            } else {
                Toast.makeText(this@MessageActivity, "You can't send message", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun sendMessage(sender: String, receiver: String, message: String) {
        var reference: DatabaseReference = FirebaseDatabase.getInstance().reference
        val hashList = hashMapOf<String, String>()

        hashList["sender"] = sender
        hashList["receiver"] = receiver
        hashList["message"] = message

        reference.child("Chats").push().setValue(hashList)
    }
}
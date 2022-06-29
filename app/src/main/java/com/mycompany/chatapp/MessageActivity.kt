package com.mycompany.chatapp


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.mycompany.chatapp.adapter.MessageAdapter
import com.mycompany.chatapp.model.Chat
import com.mycompany.chatapp.model.User
import de.hdodenhof.circleimageview.CircleImageView

class MessageActivity : AppCompatActivity() {

    var profileImage: CircleImageView? = null
    var userName: TextView? = null

    var firebaseUser: FirebaseUser? = null
    var databaseReference: DatabaseReference? = null

    var imageSend: ImageButton? = null
    var textSend: EditText? = null

    var messageAdapter: MessageAdapter? = null
    var listChat: List<Chat>? = null
    var recyclerView: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        init()
    }

    private fun init() {

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView?.setHasFixedSize(true)
        var linearLayoutManager = LinearLayoutManager(applicationContext)
        linearLayoutManager.stackFromEnd = true
        recyclerView?.layoutManager = linearLayoutManager

        val toolbar: Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        supportActionBar?.title = " "
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
          // startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
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
                readMessages(firebaseUser?.uid!!, userId, user?.imageUrl!!)
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value

            }
        })

        imageSend?.setOnClickListener {
            val msg: String = textSend?.text.toString()
            if (msg != "") {
                sendMessage(firebaseUser!!.uid, userId, msg)
            } else {
                Toast.makeText(
                    this@MessageActivity,
                    "You can't send empty message",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
            textSend?.setText("")
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

    private fun readMessages(myid: String, userid: String, imageUrl: String) {
        listChat = ArrayList()
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats")
        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                (listChat as ArrayList<Chat>).clear()

                for (snapshot: DataSnapshot in dataSnapshot.children) {
                    val chat: Chat? = snapshot.getValue(Chat::class.java)
                    if (chat?.receiver.equals(myid) && chat?.sender.equals(userid)
                        || chat?.receiver.equals(userid) && chat?.sender.equals(myid)
                    ) {
                        (listChat as ArrayList<Chat>).add(chat!!)
                    }
                    messageAdapter = MessageAdapter(
                        this@MessageActivity,
                        listChat as ArrayList<Chat>, imageUrl
                    )
                    recyclerView?.adapter = messageAdapter
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value

            }
        })
    }

    private fun status(status:String){
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)

        val hashList = hashMapOf<String, Any>()
        hashList["status"] = status
        databaseReference?.updateChildren(hashList)
    }

    override fun onResume() {
        super.onResume()
        status("online")
    }

    override fun onPause() {
        super.onPause()
        status("offline")
    }
}
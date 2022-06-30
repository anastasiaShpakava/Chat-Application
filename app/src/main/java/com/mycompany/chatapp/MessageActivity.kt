package com.mycompany.chatapp

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
import com.mycompany.chatapp.notifications.*
import de.hdodenhof.circleimageview.CircleImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MessageActivity : AppCompatActivity() {

    private var profileImage: CircleImageView? = null
    private var userName: TextView? = null
    var userId: String? = null

    private var firebaseUser: FirebaseUser? = null
    private var databaseReference: DatabaseReference? = null
    private var apiService: APIService? = null

    private var imageSend: ImageButton? = null
    private var textSend: EditText? = null

    private var messageAdapter: MessageAdapter? = null
    private var listChat: List<Chat>? = null
    private var recyclerView: RecyclerView? = null

    private var seenListener: ValueEventListener? = null

    private var notify: Boolean = false

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

        apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService::class.java)

        profileImage = findViewById(R.id.profile_image)
        userName = findViewById(R.id.user_name)
        imageSend = findViewById(R.id.btn_send)
        textSend = findViewById(R.id.text_send)

        userId = intent.getStringExtra("userId")

        firebaseUser = FirebaseAuth.getInstance().currentUser
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId!!)


        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user: User? = dataSnapshot.getValue(User::class.java)
                userName?.text = user?.username
                if (user?.imageUrl.equals("default")) {
                    profileImage?.setImageResource(R.mipmap.ic_launcher)
                } else {
                    Glide.with(applicationContext).load(user?.imageUrl).into(profileImage!!)
                }
                readMessages(firebaseUser?.uid!!, userId!!, user?.imageUrl!!)
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value

            }
        })

        imageSend?.setOnClickListener {
            notify = true
            val msg: String = textSend?.text.toString()
            if (msg != "") {
                sendMessage(firebaseUser!!.uid, userId!!, msg)
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
        seenMessage(userId!!)
    }

    private fun seenMessage(userid: String) {
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats")
        seenListener = databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (datasnapshot: DataSnapshot in snapshot.children) {
                    var chat: Chat? = datasnapshot.getValue(Chat::class.java)
                    if (chat?.receiver.equals(firebaseUser?.uid) && chat?.sender.equals(userid)) {
                        val hashList = hashMapOf<String, Any>()
                        hashList["isseen"] = true
                        datasnapshot.ref.updateChildren(hashList)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun sendMessage(sender: String, receiver: String, message: String) {
        var reference: DatabaseReference = FirebaseDatabase.getInstance().reference
        val hashList = hashMapOf<String, Any>()
        hashList["sender"] = sender
        hashList["receiver"] = receiver
        hashList["message"] = message
        hashList["isseen"] = false

        reference.child("Chats").push().setValue(hashList)

        //add user to chat fragment
        val chatRef: DatabaseReference = FirebaseDatabase.getInstance().getReference("Chatlist")
            .child(firebaseUser!!.uid)
            .child(userId!!)

        chatRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) {
                    chatRef.child("id").setValue(userId)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        var msg: String = message

        databaseReference =
            FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)
        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var user: User? = snapshot.getValue(User::class.java)
                if (notify) {
                    sendNotification(receiver, user?.username!!, msg)
                }

                notify = false
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    private fun sendNotification(receiver: String, username: String, message: String) {
        var tokens: DatabaseReference = FirebaseDatabase.getInstance().getReference("Tokens")
        var query: Query = tokens.orderByKey().equalTo(receiver)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dataSnapshot: DataSnapshot in snapshot.children) {

                    var token: Token? = dataSnapshot.getValue(Token::class.java)
                    var data: Data = Data(
                        firebaseUser?.uid, R.mipmap.ic_launcher,
                        "$username: $message", "New message"
                    )

                    var sender = Sender(data, token?.token!!)

                    apiService?.sendNotifications(sender)
                        ?.enqueue(object : Callback<MyResponse> {
                            override fun onResponse(
                                call: Call<MyResponse>,
                                response: Response<MyResponse>
                            ) {
                                if (response.code() == 200) {
                                    if (response.body()?.success != 1) {
                                        Toast.makeText(
                                            this@MessageActivity,
                                            "Failed",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }

                            override fun onFailure(call: Call<MyResponse>, t: Throwable) {

                            }

                        })
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
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

    private fun status(status: String) {
        databaseReference =
            FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)

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
        databaseReference?.removeEventListener(seenListener!!)
        status("offline")
    }
}
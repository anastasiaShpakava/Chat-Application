package com.mycompany.chatapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.mycompany.chatapp.R
import com.mycompany.chatapp.adapter.UserAdapter
import com.mycompany.chatapp.model.Chat
import com.mycompany.chatapp.model.ChatList
import com.mycompany.chatapp.model.User

class ChatsFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var userAdapter: UserAdapter? = null

    private var firebaseUser: FirebaseUser? = null
    private var databaseReference: DatabaseReference? = null

    private var userList: List<ChatList>? = null
    private var mUser: List<User>? = null
    private var chatList: List<Chat>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View = inflater.inflate(R.layout.fragment_chats, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        firebaseUser = FirebaseAuth.getInstance().currentUser

        userList = ArrayList()

        databaseReference = FirebaseDatabase.getInstance().getReference("Chatlist").child(firebaseUser!!.uid)
        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                (userList as ArrayList<ChatList>).clear()

                for (snapShop: DataSnapshot in dataSnapshot.children) {
                    var chatList = snapShop.getValue(ChatList::class.java)
                    (userList as ArrayList<ChatList>).add(chatList!!)

                }
               chatList()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value

            }
        })
        return view
    }

    private fun  chatList(){
        mUser = ArrayList()

        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
        databaseReference?.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                (mUser as ArrayList<User>).clear()

                for (datasnapshot:DataSnapshot in snapshot.children){
                    var user:User?=datasnapshot.getValue(User::class.java)
                    for (chatlist:ChatList in userList!!){
                        if (user?.id.equals(chatlist.id)){
                            (mUser as ArrayList<User>).add(user!!)
                        }
                    }
                }
                userAdapter = UserAdapter(context!!, mUser!!,true)
                recyclerView?.adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}
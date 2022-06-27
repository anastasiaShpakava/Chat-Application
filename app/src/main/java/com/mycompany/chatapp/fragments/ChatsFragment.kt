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
import com.mycompany.chatapp.model.User

class ChatsFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var userAdapter: UserAdapter? = null

    private var firebaseUser: FirebaseUser? = null
    private var databaseReference: DatabaseReference? = null

    private var userList: List<String>? = null
    private var mUser: List<User>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view: View = inflater.inflate(R.layout.fragment_chats, container, false)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats")

        userList = ArrayList()

        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                (userList as ArrayList<String>).clear()

                for (snapShop: DataSnapshot in dataSnapshot.children) {
                    var chat = snapShop.getValue(Chat::class.java)
                    if (chat?.sender.equals(firebaseUser?.uid)) {
                        (userList as ArrayList<String>).add(chat?.receiver!!)
                    }
                    if (chat?.receiver.equals(firebaseUser?.uid)) {
                        (userList as ArrayList<String>).add(chat?.sender!!)
                    }
                }
                readChats()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value

            }
        })
        return view
    }

    private fun readChats() {
        mUser = ArrayList()

        databaseReference = FirebaseDatabase.getInstance().getReference("Users")

        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                (mUser as ArrayList<User>).clear()

                for (snapShop: DataSnapshot in dataSnapshot.children) {

                    var user: User? = snapShop.getValue(User::class.java)

                    //display 1 user from chat
                    for (id: String in userList!!) {
                        if (user?.id.equals(id)) {
                            if ((mUser as ArrayList<User>).isNotEmpty()) {
                                for (curUser: User in mUser as ArrayList<User>) {
                                    if (!user?.id.equals(curUser.id)) {
                                        (mUser as ArrayList<User>).add(user!!)
                                    }
                                }
                            } else {
                                (mUser as ArrayList<User>).add(user!!)
                            }
                        }
                    }
                }
                userAdapter = UserAdapter(context!!, mUser!!)
                recyclerView?.adapter = userAdapter
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value

            }
        })
    }
}
package com.mycompany.chatapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.mycompany.chatapp.R
import com.mycompany.chatapp.ui.adapter.UserAdapter
import com.mycompany.chatapp.model.ChatList
import com.mycompany.chatapp.model.User
import com.mycompany.chatapp.notifications.Token

class ChatsFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var userAdapter: UserAdapter? = null

    private var firebaseUser: FirebaseUser? = null
    private var databaseReference: DatabaseReference? = null

    private var userList: List<ChatList>? = null
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

        updateToken(FirebaseInstanceId.getInstance().getToken())

        return view
    }

    private fun updateToken(token:String?){
        var databaseReference:DatabaseReference = FirebaseDatabase.getInstance().getReference("Tokens")
        var newToken = Token(token)
        databaseReference.child(firebaseUser!!.uid).setValue(newToken)
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
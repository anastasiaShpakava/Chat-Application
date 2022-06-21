package com.mycompany.chatapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.mycompany.chatapp.R
import com.mycompany.chatapp.adapter.UserAdapter
import com.mycompany.chatapp.model.User

class UserFragment : Fragment() {

    private var recyclerView: RecyclerView?=null

    private var userAdapter:UserAdapter?=null
    private var usersList:MutableList<User>?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view:View = inflater.inflate(R.layout.fragment_user, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        readUsers()

        return view
    }

    private fun readUsers(){
        var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        var databaseReference:DatabaseReference = FirebaseDatabase.getInstance().getReference("Users")

        databaseReference.addValueEventListener()
    }


}
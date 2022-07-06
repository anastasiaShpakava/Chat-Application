package com.mycompany.chatapp.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.mycompany.chatapp.model.User

class UserRepository {
    private var usersList: ArrayList<User>? = ArrayList()
    fun readUsers(): ArrayList<User> {
        var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        var databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Users")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                usersList?.clear()
                for (data: DataSnapshot in dataSnapshot.children) {
                    val user: User? = data.getValue(User::class.java)
                    if (!user?.id.equals(firebaseUser?.uid)) {
                        usersList?.add(user!!)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }
        })

        return usersList!!
    }
}
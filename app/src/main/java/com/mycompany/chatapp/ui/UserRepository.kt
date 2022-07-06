package com.mycompany.chatapp.ui

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.mycompany.chatapp.model.User
import com.mycompany.chatapp.ui.adapter.UserAdapter

class UserRepository {

    private var usersList: MutableLiveData<User>? = MutableLiveData()
  //  private var

  fun readUsers() :MutableLiveData<User>?{
        var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        var databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Users")

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
               // usersList?.clear()
                for (data: DataSnapshot in dataSnapshot.children) {
                    val user: User? = data.getValue(User::class.java)
                    if (!user?.id.equals(firebaseUser?.uid)) {
                        usersList?.postValue(user!!)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value

            }
        })
        return usersList
    }

    fun getAllUsers(): MutableLiveData<User>?{
        return usersList
    }
}
package com.mycompany.chatapp.ui

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.mycompany.chatapp.model.User
import com.mycompany.chatapp.utils.ActivityConstants

class UserRepository {
    private var usersList: ArrayList<User>? = ArrayList()

    fun readUsers(myCallback: ReadUsersCallback) {
        val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference(ActivityConstants.USERS)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                usersList?.clear()
                for (data: DataSnapshot in dataSnapshot.children) {
                    val user: User? = data.getValue(User::class.java)
                    if (!user?.id.equals(firebaseUser?.uid)) {
                        usersList?.add(user!!)
                    }
                    myCallback.onCallbackReadUsers(usersList!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }
        })
    }

    fun searchUsers(s: String, searchUsersCallback: SearchUsersCallback) {
        val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        val query: Query =
            FirebaseDatabase.getInstance().getReference(ActivityConstants.USERS).orderByChild(ActivityConstants.SEARCH)
                .startAt(s)
                .endAt(s + "\uf8ff")

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                usersList?.clear()

                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    val user: User? = dataSnapshot.getValue(User::class.java)

                    if (!user?.id.equals(firebaseUser?.uid)) {
                        usersList?.add(user!!)
                    }
                    searchUsersCallback.onCallbackSearchUsers(usersList!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    interface ReadUsersCallback {
        fun onCallbackReadUsers(usersList: ArrayList<User>)
    }

    interface SearchUsersCallback {
        fun onCallbackSearchUsers(usersList: ArrayList<User>)
    }
}
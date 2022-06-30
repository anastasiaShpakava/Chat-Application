package com.mycompany.chatapp.notifications

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService

class FirebaseIdService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        super.onTokenRefresh()
        val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        val refreshToken: String? = FirebaseInstanceId.getInstance().getToken()
        if (firebaseUser != null) {
            updateToken(refreshToken!!)
        }
    }

    private fun updateToken(refreshToken: String) {
        val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser

        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Tokens")
        val token = Token(refreshToken)
        databaseReference.child(firebaseUser!!.uid).setValue(token)

    }
}
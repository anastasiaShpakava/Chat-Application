package com.mycompany.chatapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.mycompany.chatapp.R
import com.mycompany.chatapp.model.User
import de.hdodenhof.circleimageview.CircleImageView

class ProfileFragment : Fragment() {

    var firebaseUser: FirebaseUser? = null
    var databaseReference: DatabaseReference? = null

    var username: TextView? = null
    var imageProfile: CircleImageView? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_profile, container, false)

        imageProfile = view.findViewById(R.id.profile_image)
        username = view.findViewById(R.id.user_name)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        databaseReference =
            FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser!!.uid)
        databaseReference?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user: User? = dataSnapshot.getValue(User::class.java)
                username?.text = user?.username
                if (user?.imageUrl.equals("default")) {
                    imageProfile?.setImageResource(R.mipmap.ic_launcher)
                } else {
                    Glide.with(context!!).load(user?.imageUrl).into(imageProfile!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value

            }
        })
        return view
    }
}
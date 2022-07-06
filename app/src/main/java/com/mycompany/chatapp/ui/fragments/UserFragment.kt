package com.mycompany.chatapp.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.mycompany.chatapp.ui.adapter.UserAdapter
import com.mycompany.chatapp.model.User
import com.mycompany.chatapp.ui.UserViewModel
import kotlin.collections.ArrayList
import com.mycompany.chatapp.R


class UserFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var searchEdit: EditText? = null

    private var userAdapter: UserAdapter? = null
    private var usersList: ArrayList<User>? = ArrayList()

    private var userViewModel: UserViewModel? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_user, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)

      //  userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

     //   userViewModel?.getList()

//        userViewModel?.getAllUsers()?.observe(this) { responseObject ->
//            usersList = responseObject as ArrayList<User>?
//        }
//        userViewModel?.getAllUsers()?.observe(viewLifecycleOwner,
//            { t ->
//                userAdapter = UserAdapter(requireContext(), t!!, false)
//                recyclerView?.adapter = userAdapter
//            })


        searchEdit = view.findViewById(R.id.search_users)
        searchEdit?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                searchUsers(s.toString().lowercase())
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        userViewModel?.getAllUsers()?.observe(viewLifecycleOwner,
            { t ->
                userAdapter = UserAdapter(requireContext(), t!!, false)
                recyclerView?.adapter = userAdapter
            })
    }

    private fun searchUsers(s: String) {
        var firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        var query: Query =
            FirebaseDatabase.getInstance().getReference("Users").orderByChild("search")
                .startAt(s)
                .endAt(s + "\uf8ff")

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                usersList?.clear()

                for (dataSnapshot: DataSnapshot in snapshot.children) {
                    var user: User? = dataSnapshot.getValue(User::class.java)

                    if (!user?.id.equals(firebaseUser?.uid)) {
                        usersList?.add(user!!)
                    }
                }

                userAdapter = UserAdapter(context!!, usersList!!, false)
                recyclerView?.adapter = userAdapter

            }


            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

}
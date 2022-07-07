package com.mycompany.chatapp.ui.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mycompany.chatapp.ui.adapter.UserAdapter
import com.mycompany.chatapp.ui.UserViewModel
import com.mycompany.chatapp.R


class UserFragment : Fragment() {

    private var recyclerView: RecyclerView? = null
    private var searchEdit: EditText? = null

    private var userAdapter: UserAdapter? = null

    private var userViewModel: UserViewModel? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = inflater.inflate(R.layout.fragment_user, container, false)
        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.layoutManager = LinearLayoutManager(context)

        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]

        readUsers()

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

    private fun readUsers() {
        userViewModel?.getAllUsers()?.observe(viewLifecycleOwner,
            { t ->

                userAdapter = UserAdapter(requireContext(), t!!, false)
                recyclerView?.adapter = userAdapter
            })
    }

    private fun searchUsers(s: String) {
        userViewModel?.getSearchingAllUsers(s)?.observe(viewLifecycleOwner,
            { t ->

                userAdapter = UserAdapter(context!!, t!!, false)
                recyclerView?.adapter = userAdapter
            })
    }
}
package com.mycompany.chatapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.mycompany.chatapp.model.User

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private var userRepository:UserRepository?=null
    private var usersList: MutableLiveData<User>? = MutableLiveData()

    init {
        userRepository = UserRepository()
        usersList = userRepository?.getAllUsers()
    }

    fun getAllUsers(): MutableLiveData<User>?{
        userRepository = UserRepository()
        usersList = userRepository?.readUsers()
        return usersList
    }
}
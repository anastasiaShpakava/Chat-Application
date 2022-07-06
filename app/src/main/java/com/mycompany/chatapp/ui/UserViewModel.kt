package com.mycompany.chatapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.mycompany.chatapp.model.User

class UserViewModel(application: Application) : AndroidViewModel(application){

    private var userRepository: UserRepository? = UserRepository()
    private var mutableLiveData: MutableLiveData<List<User>>? = MutableLiveData()

    init {
        mutableLiveData?.value=userRepository?.readUsers()
    }

    fun getAllUsers(): MutableLiveData<List<User>>? {
        return mutableLiveData
    }
}
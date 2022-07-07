package com.mycompany.chatapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.mycompany.chatapp.model.User


class UserViewModel(application: Application) : AndroidViewModel(application) {

    private var userRepository: UserRepository? = UserRepository()
    private var mutableLiveData: MutableLiveData<List<User>>? = MutableLiveData()

    fun getAllUsers(): MutableLiveData<List<User>>? {
        userRepository?.readUsers(object : UserRepository.ReadUsersCallback {
            override fun onCallbackReadUsers(usersList: ArrayList<User>) {
                mutableLiveData?.value = usersList
            }
        })
        return mutableLiveData
    }

    fun getSearchingAllUsers(s: String): MutableLiveData<List<User>>? {
        userRepository?.searchUsers(s, object : UserRepository.SearchUsersCallback {
            override fun onCallbackSearchUsers(usersList: ArrayList<User>) {
                mutableLiveData?.value = usersList
            }

        })
        return mutableLiveData
    }
}
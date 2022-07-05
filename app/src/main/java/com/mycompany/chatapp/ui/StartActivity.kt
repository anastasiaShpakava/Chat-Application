package com.mycompany.chatapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.mycompany.chatapp.R

class StartActivity : AppCompatActivity() {

    private var startRegisterButton: Button? = null
    private var startLoginButton: Button? = null

    private var firebaseUser: FirebaseUser? = null

    override fun onStart() {
        super.onStart()

        firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        init()
    }

    private fun init() {
        startRegisterButton = findViewById(R.id.start_button_register)
        startLoginButton = findViewById(R.id.start_button_login)

        startLoginButton?.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        startRegisterButton?.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
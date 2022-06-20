package com.mycompany.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class StartActivity : AppCompatActivity() {

    var start_register_button: Button?=null
    var start_login_button: Button?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        start_register_button = findViewById(R.id.startButtonRegister)
        start_login_button = findViewById(R.id.startButtonLogin)

        start_login_button?.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

       start_register_button?.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
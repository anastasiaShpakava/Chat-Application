package com.mycompany.chatapp.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.mycompany.chatapp.R

class LoginActivity : AppCompatActivity() {

    private var email: EditText? = null
    private var password: EditText? = null
    private var buttonRegister: Button? = null
    var forgotPassword:TextView? = null

    var txtEmail: String? = null
    var txtPassword: String? = null

    private var auth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        init()
    }

    private fun init() {
        email = findViewById(R.id.edit_text_email)
        password = findViewById(R.id.edit_text_password)
        buttonRegister = findViewById(R.id.button_login)
        forgotPassword = findViewById(R.id.forgot_password)

        forgotPassword?.setOnClickListener {
           startActivity(Intent(this, ResetPasswordActivity::class.java))
        }

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Login"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()

        buttonRegister?.setOnClickListener {

            txtEmail = email?.text.toString()
            txtPassword = password?.text.toString()

            if (validate()) {
                logIn(txtEmail!!, txtPassword!!)
            }
        }
    }

    private fun logIn(password: String, email: String) {
        auth?.signInWithEmailAndPassword(password, email)?.addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {

                val intent = Intent(this, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()

            } else {
                Toast.makeText(
                    baseContext, "Login failed.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun validate(): Boolean {
        var valid = true

        if (txtPassword?.isEmpty() == true || txtPassword?.length!! < 4
            || txtPassword?.length!! > 10
        ) {
            password?.error = "between 4 and 10 alphanumeric characters"
            valid = false
        } else {
            password?.error = null
        }

        if (txtEmail?.isEmpty() == true) //TODO
        {
            email?.error = "enter a valid email address"
            valid = false
        } else {
            email?.error = null
        }
        return valid
    }
}
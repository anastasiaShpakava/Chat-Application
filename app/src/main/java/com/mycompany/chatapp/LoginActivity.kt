package com.mycompany.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {

    private var email: EditText? = null
    private var password: EditText? = null
    private var button_register: Button? = null

    var txt_email: String? = null
    var txt_password: String? = null

    private var auth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        email = findViewById(R.id.editTextEmail)
        password = findViewById(R.id.editTextPassword)
        button_register = findViewById(R.id.buttonRegister)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Login"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        auth = FirebaseAuth.getInstance()

        button_register?.setOnClickListener {

            txt_email = email?.text.toString()
            txt_password = password?.text.toString()

            if (validate()) {
                logIn(txt_email!!, txt_password!!)
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

        if (txt_password?.isEmpty() == true || txt_password?.length!! < 4
            || txt_password?.length!! > 10
        ) {
            password?.error = "between 4 and 10 alphanumeric characters"
            valid = false
        } else {
            password?.error = null
        }

        if (txt_email?.isEmpty() == true || android.util.Patterns.EMAIL_ADDRESS.matcher(txt_email)
                .matches()
        ) {
            email?.error = "enter a valid email address"
            valid = false
        } else {
            email?.error = null
        }

        return valid

    }
}
package com.mycompany.chatapp.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.mycompany.chatapp.R


class ResetPasswordActivity : AppCompatActivity() {

    private var auth: FirebaseAuth? = null

    private var sendEmail: EditText? = null
    private var btnReset: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset_password)

        val toolbar: Toolbar = findViewById(R.id.tool_bar_reset)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Forgot password"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sendEmail = findViewById(R.id.send_email)
        btnReset = findViewById(R.id.btn_reset)

        auth = FirebaseAuth.getInstance()

        btnReset?.setOnClickListener {
            var email: String = sendEmail?.text.toString()

            if (email.equals("")) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
            } else {
                auth?.sendPasswordResetEmail(email)?.addOnCompleteListener { p0 ->
                    if (p0.isSuccessful) {
                        Toast.makeText(this, "Please. check youe email", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, LoginActivity::class.java))
                    } else {
                        var error: String? = p0.exception?.message
                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                    }
                }
            }


        }

    }

}
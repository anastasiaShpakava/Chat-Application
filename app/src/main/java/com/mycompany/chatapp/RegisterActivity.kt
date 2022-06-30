package com.mycompany.chatapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private var username: EditText? = null
    private var email: EditText? = null
    private var password: EditText? = null
    private var buttonRegister: Button? = null

    var txtUsername: String? = null
    var txtEmail: String? = null
    var txtPassword: String? = null

    private var auth: FirebaseAuth? = null
    private var reference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        init()
    }

    private fun init() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Register"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        username = findViewById(R.id.editTextUsername)
        email = findViewById(R.id.edit_text_email)
        password = findViewById(R.id.edit_text_password)
        buttonRegister = findViewById(R.id.button_register)

        auth = FirebaseAuth.getInstance()

        buttonRegister?.setOnClickListener {
            txtUsername = username?.text.toString()
            txtEmail = email?.text.toString()
            txtPassword = password?.text.toString()

            register(txtUsername!!, txtPassword!!, txtEmail!!)

        }
    }

    private fun register(userName: String, password: String, email: String) {
        auth?.createUserWithEmailAndPassword(email, password)
            ?.addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth?.currentUser
                    if (user != null) {
                        val userId = user.uid
                        reference =
                            FirebaseDatabase.getInstance().getReference("Users").child(userId)

                        val hashList = hashMapOf<String, String>()

                        hashList["id"] = userId
                        hashList["username"] = userName
                        hashList["imageUrl"] = "default"
                        hashList["status"] = "offline"
                        hashList["search"] = userName.lowercase()

                        reference?.setValue(hashList)?.addOnCompleteListener(this) { taskNew ->
                            if (taskNew.isSuccessful) {
                                val intent = Intent(this, MainActivity::class.java)
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                startActivity(intent)
                            }
                        }
                    }
                } else {
                    Toast.makeText(
                        baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            }
    }
}
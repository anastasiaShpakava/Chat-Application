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
    private var button_register: Button? = null

    var txt_username: String? = null
    var txt_email: String? = null
    var txt_password: String? = null

    private var auth: FirebaseAuth? = null
    private var reference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle("Register")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        username = findViewById(R.id.editTextUsername)
        email = findViewById(R.id.editTextEmail)
        password = findViewById(R.id.editTextPassword)
        button_register = findViewById(R.id.buttonRegister)

        auth = FirebaseAuth.getInstance()

        button_register?.setOnClickListener {
            txt_username = username?.text.toString()
            txt_email = email?.text.toString()
            txt_password = password?.text.toString()

            if (validate()) {
                register(txt_username!!, txt_password!!, txt_email!!)
            }
        }

    }

    private fun validate(): Boolean {
        var valid = true

        if (txt_username?.isEmpty() == true || txt_username?.length!! < 3) {
            username?.error = "at least 3 characters"
            valid = false
        } else {
            username?.error = null
        }

        if (txt_password?.isEmpty() == true || txt_password?.length!! < 4
            || txt_password?.length!! > 10
        ) {
            password?.error = "between 4 and 10 alphanumeric characters"
            valid = false
        } else {
            password?.error = null
        }

        if (txt_email?.isEmpty() == true) {
            email?.error = "enter a valid email address"
            valid = false
        } else {
            email?.error = null
        }

        return valid

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
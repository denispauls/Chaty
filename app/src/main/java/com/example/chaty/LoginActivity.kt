package com.example.chaty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class LoginActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val toolbar: Toolbar = findViewById(R.id.toolbar_login)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Login"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{
            val intent = Intent(this@LoginActivity, WillkommenActivity::class.java)
            startActivity(intent)
            finish()
        }

        mAuth = FirebaseAuth.getInstance()

        findViewById<Button>(R.id.login_btn).setOnClickListener{
            loginUser()
        }
    }

    private fun loginUser() {
        val email: String = findViewById<EditText>(R.id.email_login).text.toString()
        val password: String = findViewById<EditText>(R.id.password_login).text.toString()

        if(email == ""){
            Toast.makeText(this@LoginActivity, "Bitte E-Mailadresse einfügen", Toast.LENGTH_LONG).show()
        }
        else if(password == ""){
            Toast.makeText(this@LoginActivity, "Bitte Passwort einfügen", Toast.LENGTH_LONG).show()
        }
        else {
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful){
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    }
                    else {
                        Toast.makeText(this@LoginActivity, "Fehler: " + task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}
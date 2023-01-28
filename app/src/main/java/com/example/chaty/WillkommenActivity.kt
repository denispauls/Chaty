package com.example.chaty

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser



class WillkommenActivity : AppCompatActivity() {

    var firebaseUser: FirebaseUser? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_willkommen)

        findViewById<Button>(R.id.register_welcome_btn).setOnClickListener{
            val intent = Intent(this@WillkommenActivity, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        findViewById<Button>(R.id.login_welcome_btn).setOnClickListener{
            val intent = Intent(this@WillkommenActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }


    }

    override fun onStart() {
        super.onStart()

        firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser != null){
            val intent = Intent(this@WillkommenActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
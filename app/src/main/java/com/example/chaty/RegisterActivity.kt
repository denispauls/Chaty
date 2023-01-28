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
import com.google.firebase.database.FirebaseDatabase
import org.w3c.dom.Text

class RegisterActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var refUser: DatabaseReference
    private var firebaseUserID: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val toolbar: Toolbar = findViewById(R.id.toolbar_register)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Registrieren"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener{
            val intent = Intent(this@RegisterActivity, WillkommenActivity::class.java)
            startActivity(intent)
            finish()
        }

        mAuth = FirebaseAuth.getInstance()

        findViewById<Button>(R.id.register_btn).setOnClickListener{
           registerUser()
        }
    }

    private fun registerUser() {
        val username: String = findViewById<EditText>(R.id.username_register).text.toString()
        val email: String = findViewById<EditText>(R.id.email_register).text.toString()
        val password: String = findViewById<EditText>(R.id.password_register).text.toString()

        if(username.equals("")){
            Toast.makeText(this@RegisterActivity, "Bitte Username einfügen", Toast.LENGTH_LONG).show()
        }
        else if(email == ""){
            Toast.makeText(this@RegisterActivity, "Bitte E-Mailadresse einfügen", Toast.LENGTH_LONG).show()
        }
        else if(password == ""){
            Toast.makeText(this@RegisterActivity, "Bitte Passwort einfügen", Toast.LENGTH_LONG).show()
        }
        else{
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener{task ->
                    if(task.isSuccessful){
                        firebaseUserID = mAuth.currentUser!!.uid
                        refUser = FirebaseDatabase.getInstance().reference.child("User").child(firebaseUserID)

                        val userHashMap = HashMap<String, Any>()
                        userHashMap["uid"] = firebaseUserID
                        userHashMap["username"] = username
                        userHashMap["profile"] = "https://firebasestorage.googleapis.com/v0/b/chaty-59b05.appspot.com/o/profile_i.webp?alt=media&token=192efc51-07b8-46df-9974-6497a00e6414"
                        userHashMap["cover"] = "https://firebasestorage.googleapis.com/v0/b/chaty-59b05.appspot.com/o/cover_i.webp?alt=media&token=437329fb-7ace-4328-a935-bd1342841e88"
                        userHashMap["status"] = "offline"
                        userHashMap["search"] = username.toLowerCase()
                        userHashMap["facebook"] = "https://m.facebook.com"
                        userHashMap["instagram"] = "https://m.instagram.com"
                        userHashMap["website"] = "https://www.google.com"

                        refUser.updateChildren(userHashMap)
                            .addOnCompleteListener{task ->
                                if(task.isSuccessful){
                                    val intent = Intent(this@RegisterActivity, MainActivity::class.java)
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    finish()
                                }
                            }
                    }
                    else{
                        Toast.makeText(this@RegisterActivity, "Fehler: " + task.exception!!.message.toString(), Toast.LENGTH_LONG).show()
                    }
                }
        }



    }
}
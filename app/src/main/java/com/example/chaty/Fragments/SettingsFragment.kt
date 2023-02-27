@file:Suppress("DEPRECATION")

package com.example.chaty.Fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import android.widget.Toast.*
import com.example.chaty.ModelClasses.Users
import com.example.chaty.R
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.StorageTask
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.squareup.picasso.Request
import com.squareup.picasso.RequestCreator
import kotlinx.android.synthetic.main.fragment_settings.view.*
import kotlin.coroutines.Continuation

class SettingsFragment : Fragment() {

    var usersReference: DatabaseReference?=null
    var firebaseUser: FirebaseUser? = null
    private val RequestCode = 438
    private var imageUri: Uri? = null
    private var storageRef: StorageReference? = null
    private var profileTest: String? = ""
    private var socialTest: String? = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view =  inflater.inflate(R.layout.fragment_settings, container, false)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        usersReference = FirebaseDatabase.getInstance().reference.child("User").child(firebaseUser!!.uid)
        storageRef = FirebaseStorage.getInstance().reference.child("User Fotos")

        usersReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val user: Users? = p0.getValue(Users::class.java)

                    if (context!=null){
                        view.username_settings.text = user!!.getUserName()
                        Picasso.get().load(user.getProfile()).into(view.profile_image)
                    }
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })

        view.profile_image.setOnClickListener{
            profileTest = "profile"
            pickImage()
        }

        view.set_facebook.setOnClickListener{
            socialTest = "facebook"
            setSocialLinks()
        }
        view.set_instagram.setOnClickListener{
            socialTest = "instagram"
            setSocialLinks()
        }
        view.set_website.setOnClickListener{
            socialTest = "website"
            setSocialLinks()
        }
        return view
    }

    private fun setSocialLinks() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())

        if (socialTest == "website"){
            builder.setTitle("URL:")
        } else {
            builder.setTitle("Schreibe Namen:")
        }

        val editText = EditText(context)
        if (socialTest == "website"){
            editText.hint = "z.B. www.google.com"
        } else {
            editText.hint = "Vorname & Name"
        }
        builder.setView(editText)


        builder.setPositiveButton("Create", DialogInterface.OnClickListener{
                dialog, which ->
            val str = editText.text.toString()

            if (str == "")
            {
                makeText(context, "Please write something...", LENGTH_LONG).show()
            }
            else
            {
                saveSocialLink(str)
            }
        })

        builder.setNegativeButton("Cancel", DialogInterface.OnClickListener {
                dialog, which ->
            dialog.cancel()
        })
        builder.show()
    }

    private fun saveSocialLink(str: String) {
        val mapSocial = HashMap<String, Any>()
       // mapSocial["profile"] = url
       // usersReference!!.updateChildren(mapSocial)

        when(socialTest){
            "facebook" -> {
                mapSocial["facebook"] = "https://m.facebook.com/$str"
            }
            "instagram" -> {
                mapSocial["instagram"] = "https://m.instagram.com/$str"
            }
            "website" -> {
                mapSocial["website"] = "https://$str"
            }
        }
        usersReference!!.updateChildren(mapSocial).addOnCompleteListener{
            task ->
            if(task.isSuccessful){
                makeText(context, "erfolgreich gespeichert", LENGTH_LONG).show()
            }
        }
    }

    private fun pickImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, RequestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RequestCode && resultCode == Activity.RESULT_OK && data!!.data != null){
            imageUri = data.data
            makeText(context, "hochladen..", LENGTH_SHORT).show()
            uploadImageToDatabase()
        }
    }

    private fun uploadImageToDatabase() {
        val progressBar = ProgressDialog(context)
        progressBar.setMessage("Foto lädt hoch")
        progressBar.show()

        if (imageUri!=null){
            val fileRef = storageRef!!.child(System.currentTimeMillis().toString() + ".jpg")
            var uploadTask: StorageTask<*>
            uploadTask = fileRef.putFile(imageUri!!)
            uploadTask.continueWithTask<Uri?>(com.google.android.gms.tasks.Continuation <UploadTask.TaskSnapshot, Task<Uri>>{ task ->
                if(!task.isSuccessful){
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation fileRef.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val downloadURL = task.result
                    val url = downloadURL.toString()

                    if (profileTest == "profile") {
                        val mapProfileImg = HashMap<String, Any>()
                        mapProfileImg["profile"] = url
                        usersReference!!.updateChildren(mapProfileImg)
                        profileTest = ""
                    }
                    progressBar.dismiss()
                }
            }
        }
    }
}
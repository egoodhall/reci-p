package com.reci_p.reci_p.activities

import android.Manifest
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import com.reci_p.reci_p.R
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import org.jetbrains.anko.padding
import org.jetbrains.anko.sdk25.coroutines.onLongClick
import android.widget.Toast
import com.facebook.drawee.view.SimpleDraweeView
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import gun0912.tedbottompicker.TedBottomPicker
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.google.android.gms.tasks.OnSuccessListener
import android.support.annotation.NonNull
import android.view.LayoutInflater
import com.google.android.gms.tasks.OnFailureListener
import android.view.animation.TranslateAnimation
import android.view.animation.Animation
import org.jetbrains.anko.custom.style


class EditorActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        val permissionlistener = object : PermissionListener {
            override fun onPermissionGranted() {
                Toast.makeText(this@EditorActivity, "Permission Granted", Toast.LENGTH_SHORT).show()
            }

            override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
                Toast.makeText(this@EditorActivity, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show()
            }


        }

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check();

    }

    fun addIngredient(v : View) {
        val ingredient = LayoutInflater.from(this).inflate(R.layout.ingredient, null)
        ingredient.findViewById<TextView>(R.id.ingredient_text).text = (findViewById<EditText>(R.id.ingredientText)).text
        ingredient.onLongClick { v -> removeIngredient(v!!) }

        if (ingredient.findViewById<TextView>(R.id.ingredient_text).text.toString() != "") {
            findViewById<LinearLayout>(R.id.ingredientHolder).addView(ingredient)
            (findViewById<EditText>(R.id.ingredientText)).text.clear()
        }
    }

    fun removeIngredient(v: View) {
        val linLayout = findViewById<LinearLayout>(R.id.ingredientHolder)
//        val animation = TranslateAnimation(0f, 1500f, 0f, 0f) //May need to check the direction you want.
        val animation = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 100f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f) //May need to check the direction you want.
        animation.duration = 1000
        animation.fillAfter = true
        v.startAnimation(animation)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(arg0: Animation) {}
            override fun onAnimationRepeat(arg0: Animation) {}
            override fun onAnimationEnd(arg0: Animation) {
                v.visibility = View.GONE
                linLayout.removeView(v)
            }
        })
    }


    fun addInstruction(v : View) {
        val ingredient = LayoutInflater.from(this).inflate(R.layout.ingredient, null)
        ingredient.findViewById<TextView>(R.id.ingredient_text).text = (findViewById<EditText>(R.id.instructionText)).text
        ingredient.onLongClick { v -> removeInstruction(v!!) }

        if (ingredient.findViewById<TextView>(R.id.ingredient_text).text.toString() != "") {
            findViewById<LinearLayout>(R.id.instructionHolder).addView(ingredient)
            (findViewById<EditText>(R.id.instructionText)).text.clear()
        }
    }

    fun removeInstruction(v: View) {
        val linLayout = findViewById<LinearLayout>(R.id.ingredientHolder)
//        val animation = TranslateAnimation(0f, 1500f, 0f, 0f) //May need to check the direction you want.
        val animation = TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 100f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f) //May need to check the direction you want.
        animation.duration = 1000
        animation.fillAfter = true
        v.startAnimation(animation)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(arg0: Animation) {}
            override fun onAnimationRepeat(arg0: Animation) {}
            override fun onAnimationEnd(arg0: Animation) {
                v.visibility = View.GONE
                linLayout.removeView(v)
            }
        })
    }

    fun updateImage(v: View) {

        val tedBottomPicker = TedBottomPicker.Builder(this@EditorActivity)
                .setOnImageSelectedListener { uri ->
                    // here is selected uri
                    val storage = FirebaseStorage.getInstance()
                    val storageRef = storage.reference.child("images/test.jpg")
                    val uploadTask = storageRef.putFile(uri)

                    // Register observers to listen for when the download is done or if it fails
                    uploadTask.addOnFailureListener({ e ->
                        // Handle unsuccessful uploads
                        Toast.makeText(this@EditorActivity, "Upload Failed: " + e.localizedMessage, Toast.LENGTH_SHORT).show()
                    }).addOnSuccessListener({ taskSnapshot ->
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        val downloadUrl = taskSnapshot.downloadUrl
                        (findViewById<SimpleDraweeView>(R.id.app_bar_image)).setImageURI(downloadUrl, this@EditorActivity)
                        Toast.makeText(this@EditorActivity, "Upload successful, URL: " + downloadUrl, Toast.LENGTH_SHORT).show()
                    })

                }
                .create()

        tedBottomPicker.show(supportFragmentManager)
    }

    fun saveRecipe(v: View) {
        Toast.makeText(this@EditorActivity, "Save button pressed", Toast.LENGTH_SHORT).show()
    }

}

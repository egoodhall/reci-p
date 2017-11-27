package com.reci_p.reci_p.activities

import android.Manifest
import android.graphics.Typeface
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import com.reci_p.reci_p.R
import android.view.View
import org.jetbrains.anko.padding
import org.jetbrains.anko.sdk25.coroutines.onLongClick
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
import android.widget.*
import com.reci_p.reci_p.data.Recipe
import org.jetbrains.anko.childrenSequence
import org.jetbrains.anko.custom.style
import org.jetbrains.anko.imageURI
import java.util.*


class EditorActivity : AppCompatActivity() {


    var recipeModel = Recipe(Collections.emptyList<String>(),
                        "",
                        "",
                        "",
                        "",
                        Collections.emptyList<String>(),
                        "",
                        "",
                        "",
                        "",
                        0L,
                        0L,
                        0f)

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

        setRecipeView()

    }

    fun addIngredient(v : View) {
        recipeModel.ingredients += (findViewById<EditText>(R.id.ingredientText)).text.toString()
        updateRecipeView()
    }

    fun removeIngredient(v: View) {
        val linLayout = findViewById<LinearLayout>(R.id.ingredientHolder)
        val ingr = v.findViewById<TextView>(R.id.ingredient_text).text.toString()
        val newIngredients = recipeModel.ingredients.toMutableList()
        newIngredients.remove(ingr)
        recipeModel.ingredients = newIngredients
        val animation = TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0f,
                Animation.RELATIVE_TO_PARENT, 1.1f,
                Animation.RELATIVE_TO_PARENT, 0f,
                Animation.RELATIVE_TO_PARENT, 0f)
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
        recipeModel.instructions += (findViewById<EditText>(R.id.instructionText)).text.toString()
        updateRecipeView()
    }

    fun removeInstruction(v: View) {
        val linLayout = findViewById<LinearLayout>(R.id.instructionHolder)
        val ingr = v.findViewById<TextView>(R.id.instruction_text).text.toString()
        val newInstructions = recipeModel.instructions.toMutableList()
        newInstructions.remove(ingr)
        recipeModel.instructions = newInstructions
        val animation = TranslateAnimation(Animation.RELATIVE_TO_PARENT, 0f,
                Animation.RELATIVE_TO_PARENT, 1.1f,
                Animation.RELATIVE_TO_PARENT, 0f,
                Animation.RELATIVE_TO_PARENT, 0f)
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

    fun setRecipeView() {
        findViewById<EditText>(R.id.recipeTitle).setText(recipeModel.title)
        findViewById<EditText>(R.id.recipeDesc).setText(recipeModel.description)
        findViewById<EditText>(R.id.recipePrepTime).setText(recipeModel.prepTime)
        findViewById<EditText>(R.id.recipeCookTime).setText(recipeModel.cookTime)
        findViewById<EditText>(R.id.recipeCookTime).setText(recipeModel.cookTime)
        if (!recipeModel.photo.isEmpty()) {
            val storage = FirebaseStorage.getInstance()
            val storageRef = storage.reference.child(recipeModel.photo)
            (findViewById<SimpleDraweeView>(R.id.app_bar_image)).setImageURI(storageRef.downloadUrl.result, this@EditorActivity)
        }
        findViewById<RatingBar>(R.id.recipeRating).rating = recipeModel.rating

        findViewById<LinearLayout>(R.id.ingredientHolder).removeAllViews()
        recipeModel.ingredients.forEach { ingr ->
            if (!ingr.isEmpty()) {
                val view = LayoutInflater.from(this).inflate(R.layout.ingredient, null)
                view.findViewById<TextView>(R.id.ingredient_text).text = ingr
                view.onLongClick { v -> removeIngredient(v!!) }

                findViewById<LinearLayout>(R.id.ingredientHolder).addView(view)
                (findViewById<EditText>(R.id.ingredientText)).text.clear()
            }
        }

        findViewById<LinearLayout>(R.id.instructionHolder).removeAllViews()
        recipeModel.instructions.forEach { inst ->
            if (!inst.isEmpty()) {
                val view = LayoutInflater.from(this).inflate(R.layout.instruction, null)
                view.findViewById<TextView>(R.id.instruction_text).text = inst
                view.onLongClick { v -> removeInstruction(v!!) }

                findViewById<LinearLayout>(R.id.instructionHolder).addView(view)
                (findViewById<EditText>(R.id.instructionText)).text.clear()
            }

        }

    }

    fun updateRecipeView() {
        findViewById<LinearLayout>(R.id.ingredientHolder).removeAllViews()
        recipeModel.ingredients.forEach { ingr ->
            if (!ingr.isEmpty()) {
                val view = LayoutInflater.from(this).inflate(R.layout.ingredient, null)
                view.findViewById<TextView>(R.id.ingredient_text).text = ingr
                view.onLongClick { v -> removeIngredient(v!!) }

                findViewById<LinearLayout>(R.id.ingredientHolder).addView(view)
                (findViewById<EditText>(R.id.ingredientText)).text.clear()
            }
        }

        findViewById<LinearLayout>(R.id.instructionHolder).removeAllViews()
        recipeModel.instructions.forEach { inst ->
            if (!inst.isEmpty()) {
                val view = LayoutInflater.from(this).inflate(R.layout.instruction, null)
                view.findViewById<TextView>(R.id.instruction_text).text = inst
                view.onLongClick { v -> removeInstruction(v!!) }

                findViewById<LinearLayout>(R.id.instructionHolder).addView(view)
                (findViewById<EditText>(R.id.instructionText)).text.clear()
            }

        }

    }

    fun updateImage(v: View) {

        val tedBottomPicker = TedBottomPicker.Builder(this@EditorActivity)
                .setOnImageSelectedListener { uri ->
                    // here is selected uri
                    val photo = "images/test.jpg"
                    val storage = FirebaseStorage.getInstance()
                    val storageRef = storage.reference.child(photo)
                    val uploadTask = storageRef.putFile(uri)
                    (findViewById<SimpleDraweeView>(R.id.app_bar_image)).setImageURI(uri, this@EditorActivity)

                    // Register observers to listen for when the download is done or if it fails
                    uploadTask.addOnFailureListener({ e ->
                        // Handle unsuccessful uploads
                        (findViewById<SimpleDraweeView>(R.id.app_bar_image)).setImageURI(null as String?)
                        Toast.makeText(this@EditorActivity, "Upload Failed: " + e.localizedMessage, Toast.LENGTH_SHORT).show()
                    }).addOnSuccessListener({ taskSnapshot ->
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        val downloadUrl = taskSnapshot.downloadUrl
                        recipeModel.photo = photo
                        Toast.makeText(this@EditorActivity, "Upload successful, URL: " + downloadUrl, Toast.LENGTH_SHORT).show()
                    })

                }
                .create()

        tedBottomPicker.show(supportFragmentManager)
    }

    fun setRecipeFromView() {
        recipeModel.title = (findViewById<EditText>(R.id.recipeTitle)).text.toString()
        recipeModel.description = (findViewById<EditText>(R.id.recipeDesc)).text.toString()
        recipeModel.prepTime = (findViewById<EditText>(R.id.recipePrepTime)).text.toString()
        recipeModel.cookTime = (findViewById<EditText>(R.id.recipeCookTime)).text.toString()
        recipeModel.rating = findViewById<RatingBar>(R.id.recipeRating).rating

    }

    fun saveRecipe(v: View) {
        setRecipeFromView()
        Toast.makeText(this@EditorActivity, "Recipe: " + recipeModel.toString(), Toast.LENGTH_SHORT).show()
    }

}

package com.reci_p.reci_p.activities

import android.Manifest
import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.*
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.github.jorgecastilloprz.FABProgressCircle
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.reci_p.reci_p.R
import com.reci_p.reci_p.data.Recipe
import com.reci_p.reci_p.helpers.DataManager
import com.reci_p.reci_p.helpers.DataManager.Companion.realm
import gun0912.tedbottompicker.TedBottomPicker
import io.realm.RealmList
import org.jetbrains.anko.image
import org.jetbrains.anko.imageResource
import org.jetbrains.anko.sdk25.coroutines.onLongClick
import java.util.*


class EditorActivity : AppCompatActivity() {

    private var recipeModel = Recipe(creator = FirebaseAuth.getInstance().currentUser!!.uid)
    private var uploadStatus = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editor)

        val permissionlistener = object : PermissionListener {
            override fun onPermissionGranted() {
            }

            override fun onPermissionDenied(deniedPermissions: ArrayList<String>) {
                Toast.makeText(applicationContext, "Permission Denied\n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show()
            }


        }

        TedPermission.with(this)
                .setPermissionListener(permissionlistener)
                .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .check()
        if (this.intent.hasExtra("recipeId")) {
            DataManager.getRecipe(this.intent.getStringExtra("recipeId"), { recipe ->
                if (recipe != null) {
                    if (recipe.owner != FirebaseAuth.getInstance().currentUser!!.uid) {
                        this.recipeModel = Recipe(FirebaseAuth.getInstance().currentUser!!.uid, UUID.randomUUID().toString(), recipe)
                    } else {
                        this.recipeModel = recipe
                    }
                    setRecipeView()
                }
            })
        }

        setRecipeView()


    }

    fun addIngredient(v : View) {
        realm.executeTransaction {
            recipeModel.ingredients.add((findViewById<EditText>(R.id.ingredientText)).text.toString())
        }
        updateRecipeView()
    }

    private fun removeIngredient(v: View) {
        val linLayout = findViewById<LinearLayout>(R.id.ingredientHolder)
        val ingr = v.findViewById<TextView>(R.id.ingredient_text).text.toString()
        val newIngredients = recipeModel.ingredients.toMutableList()
        newIngredients.remove(ingr)
        realm.executeTransaction {
            recipeModel.ingredients = RealmList(*newIngredients.toTypedArray())
        }
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
        realm.executeTransaction {
            recipeModel.instructions.add((findViewById<EditText>(R.id.instructionText)).text.toString())
        }
        updateRecipeView()
    }

    private fun removeInstruction(v: View) {
        val linLayout = findViewById<LinearLayout>(R.id.instructionHolder)
        val ingr = v.findViewById<TextView>(R.id.instruction_text).text.toString()
        val newInstructions = recipeModel.instructions.toMutableList()
        newInstructions.remove(ingr)
        realm.executeTransaction {
            recipeModel.instructions = RealmList(*newInstructions.toTypedArray())
        }
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

    private fun setRecipeView() {
        findViewById<EditText>(R.id.recipeTitle).setText(recipeModel.title)
        findViewById<EditText>(R.id.recipeDesc).setText(recipeModel.description)
        findViewById<EditText>(R.id.recipePrepTime).setText(recipeModel.prepTime)
        findViewById<EditText>(R.id.recipeCookTime).setText(recipeModel.cookTime)
        findViewById<EditText>(R.id.recipeCookTime).setText(recipeModel.cookTime)
        if (recipeModel.photo != "" && recipeModel.photo != null) {
            Log.d("Reci-P", "HERE ${recipeModel.photo}")
            val urlString = "gs://${FirebaseApp.getInstance()!!.options!!.storageBucket}/${recipeModel.photo}"
            FirebaseStorage.getInstance().getReferenceFromUrl(urlString).downloadUrl.addOnSuccessListener { uri ->
                Log.d("Reci-P", "URI: ${uri.toString()}")
                val controller = Fresco.newDraweeControllerBuilder().setUri(uri)
                val imgView = findViewById<SimpleDraweeView>(R.id.app_bar_image)
                imgView.controller = controller.setOldController(imgView.controller).build()
            }.addOnFailureListener { exception ->
                Log.e("Reci-P", "Error getting URI for image: ${exception.localizedMessage}")
            }
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

    private fun updateRecipeView() {
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
                    val photo = "images/" + recipeModel.id + ".png"
                    val storage = FirebaseStorage.getInstance()
                    val storageRef = storage.reference.child(photo)
                    val uploadTask = storageRef.putFile(uri)
                    (findViewById<SimpleDraweeView>(R.id.app_bar_image)).setImageURI(uri, this@EditorActivity)
                    uploadStatus = true
                    val fabProgress = findViewById<FABProgressCircle>(R.id.fabProgressCircle)
                    fabProgress.show()
                    // Register observers to listen for when the download is done or if it fails
                    uploadTask.addOnFailureListener({ e ->
                        // Handle unsuccessful uploads
                        (findViewById<SimpleDraweeView>(R.id.app_bar_image)).setImageURI(null as String?)
                        Toast.makeText(this@EditorActivity, "Upload Failed: " + e.localizedMessage, Toast.LENGTH_SHORT).show()
                        fabProgress.hide()
                        uploadStatus = false
                    }).addOnSuccessListener({ taskSnapshot ->
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        val downloadUrl = taskSnapshot.downloadUrl
                        realm.executeTransaction {
                            recipeModel.photo = photo
                        }
                        fabProgress.beginFinalAnimation()
//                        Toast.makeText(this@EditorActivity, "Upload successful, URL: " + downloadUrl, Toast.LENGTH_SHORT).show()
                        uploadStatus = false
                    })

                }
                .create()

        tedBottomPicker.show(supportFragmentManager)
    }

    private fun setRecipeFromView() {
        realm.executeTransaction {
            recipeModel.title = (findViewById<EditText>(R.id.recipeTitle)).text.toString()
            recipeModel.description = (findViewById<EditText>(R.id.recipeDesc)).text.toString()
            recipeModel.prepTime = (findViewById<EditText>(R.id.recipePrepTime)).text.toString()
            recipeModel.cookTime = (findViewById<EditText>(R.id.recipeCookTime)).text.toString()
            recipeModel.rating = findViewById<RatingBar>(R.id.recipeRating).rating
        }
    }

    fun saveRecipe(v: View) {
        val fabProgress = findViewById<FABProgressCircle>(R.id.fabProgressCircle_save)
        fabProgress.show()
        if (uploadStatus) {
            Toast.makeText(this@EditorActivity, "Please wait for image upload to finish before saving.", Toast.LENGTH_SHORT).show()
            return
        }
        setRecipeFromView()
        realm.executeTransaction {
            recipeModel.modifiedTS = Date().time
            recipeModel.owner = FirebaseAuth.getInstance().currentUser!!.uid
        }

            DataManager.createRecipe(recipeModel, { recipe ->
                if (recipe != null) {
//                    Toast.makeText(this@EditorActivity, "Recipe creation successful!", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this@EditorActivity, "Recipe creation unsuccessful!", Toast.LENGTH_SHORT).show()
                    fabProgress.hide()
                }
            })
    }

}

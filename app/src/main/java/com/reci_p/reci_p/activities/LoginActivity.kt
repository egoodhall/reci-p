package com.reci_p.reci_p.activities

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.reci_p.reci_p.R
import com.reci_p.reci_p.data.User
import com.reci_p.reci_p.helpers.DataManager
import io.realm.RealmList

class LoginActivity : AppCompatActivity() {

    internal val RC_SIGN_IN = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (intent.hasExtra("signOut")) {
            signOut()
        }

        if (FirebaseAuth.getInstance().currentUser != null) launchRootActivity()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d("LoginActivity", "Resumed: $requestCode, $resultCode")
        if (resultCode === Activity.RESULT_OK) {
            when (requestCode) {
                RC_SIGN_IN -> launchRootActivity()
            }
        }
    }

    private fun launchRootActivity() {
        val firebaseUser = FirebaseAuth.getInstance().currentUser!!
        DataManager.createUser(User(firebaseUser.photoUrl.toString(), firebaseUser.uid, firebaseUser.email!!, firebaseUser.displayName!!, RealmList<String>()))
        { success: Boolean ->
            if (success) {
                Log.i("Reci-P-Login", "User returning")
            } else {
                Log.i("Reci-P-Login", "User signup failed")
            }
        }
        startActivity(Intent(applicationContext, EditorActivity::class.java))
        finish()
    }

    private fun signOut() {
        val user = FirebaseAuth.getInstance().currentUser!!
        AuthUI.getInstance().signOut(this).addOnCompleteListener { task: Task<Void> ->
            Toast.makeText(applicationContext, "Signed out ${user.displayName}", Toast.LENGTH_SHORT).show()
        }
    }

    fun signInWithGoogle(view: View) {
        startActivityForResult(AuthUI
                .getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(arrayListOf(
                        AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()
                ))
                .setIsSmartLockEnabled(false)
                .build(), RC_SIGN_IN)
    }
}



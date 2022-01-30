package com.luisansal.jetpack.domain.usecases

import android.app.Activity
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.luisansal.jetpack.core.data.preferences.AuthSharedPreferences
import com.luisansal.jetpack.core.domain.entity.UserEntity
import com.luisansal.jetpack.core.domain.firebasedao.UserDao
import org.w3c.dom.Entity
import java.util.*

class LoginFirebaseUseCase(private val authSharedPreferences: AuthSharedPreferences) {

    private val auth = Firebase.auth
    private val db = Firebase.database
    private val usersRef by lazy { db.getReference("users") }

    fun login(activity: Activity, email: String, password: String, success: (UserDao?) -> Unit, error: (Exception?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    Log.d("login", "signInWithEmail:success")
                    val userFirebase = auth.currentUser

                    usersRef.orderByChild("email").equalTo(email).addChildEventListener(object : ChildEventListener {
                        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                            val user = snapshot.getValue(UserDao::class.java)
                            authSharedPreferences.isLogged = true
                            authSharedPreferences.names = user?.names
                            authSharedPreferences.lastnames = user?.lastNames
                            authSharedPreferences.email = userFirebase?.email
                            authSharedPreferences.tokenExpires = Calendar.getInstance().apply {
                                add(Calendar.HOUR_OF_DAY, 2)
                            }.timeInMillis
                            success(user)
                        }

                        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) = Unit
                        override fun onChildRemoved(snapshot: DataSnapshot) = Unit
                        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) = Unit
                        override fun onCancelled(error: DatabaseError) {
                            error(error.toException())
                        }
                    })
                } else {
                    Log.w("login", "signInWithEmail:failure", task.exception)
                    error(task.exception)
                }
            }
    }
}
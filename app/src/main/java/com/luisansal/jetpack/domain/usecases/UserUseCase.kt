package com.luisansal.jetpack.domain.usecases

import android.app.Activity
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.ktx.Firebase
import com.luisansal.jetpack.core.domain.entity.UserEntity
import com.luisansal.jetpack.data.repository.UserRepository
import com.luisansal.jetpack.data.repository.VisitRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*

class UserUseCase(
    private val userRepository: UserRepository, private val visitRepository: VisitRepository,
    private val usersRef: DatabaseReference
) {
    private var auth: FirebaseAuth = Firebase.auth

    fun newAuthUser(activity: Activity, user: UserEntity, success: (UserEntity?) -> Unit, error: (Exception?) -> Unit) {
        auth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnCompleteListener(activity) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Newuser", "createUserWithEmail:success")
                    usersRef.child(user.id).setValue(user)
                    success(user)
//                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Newuser", "createUserWithEmail:failure", task.exception)
                    error(task.exception)
//                    Toast.makeText(baseContext, "Authentication failed.",
//                        Toast.LENGTH_SHORT).show()
//                    updateUI(null)
                }
            }
    }

    fun newUser(userEntity: UserEntity): UserEntity {
        val userExist = userRepository.getUserByDni(userEntity.dni)

        if (userExist !== null)
            return userExist

        val userId = userRepository.save(userEntity)
        return userRepository.getUserById(userId)!!
    }

    suspend fun getUserByDni(dni: String): UserEntity? = withContext(Dispatchers.IO) {
        userRepository.getUserByDni(dni)
    }

    suspend fun getUser(email: String): UserEntity? = withContext(Dispatchers.IO) {
        userRepository.getUserByEmail(email)
    }

    suspend fun getAllUser(): List<UserEntity> = withContext(Dispatchers.IO) {
        userRepository.allUserEntities
    }

    suspend fun getAllUserPaged(): LiveData<PagedList<UserEntity>> = withContext(Dispatchers.IO) {
        LivePagedListBuilder(userRepository.allUsersPaging, 50).build()
    }

    suspend fun getByNamesPaged(names: String): LiveData<PagedList<UserEntity>> = withContext(Dispatchers.IO) {
        LivePagedListBuilder(userRepository.getByNamePaging(names), 50).build()
    }

    fun getUserById(id: Long): UserEntity? {
        return userRepository.getUserById(id)
    }

    fun deleUser(dni: String): Boolean {
        return userRepository.deleteUser(dni)
    }

    suspend fun deleUsers(): Boolean = withContext(Dispatchers.IO) {
        visitRepository.deleteAll()
        userRepository.deleteUsers()
    }
}
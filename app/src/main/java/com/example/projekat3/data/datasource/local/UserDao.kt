package com.example.projekat3.data.datasource.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.projekat3.data.models.user.User
import com.example.projekat3.data.models.user.UserEntity
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

@Dao
abstract class UserDao {

    @Query("SELECT * FROM users")
    abstract fun getAll(): Observable<List<UserEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(userEntity: UserEntity): Completable

    @Query("SELECT * FROM users WHERE (username == :username AND email == :email AND password == :password)")
    abstract fun getUserByUsernameEmailPass(username: String, email: String, password: String): Observable<UserEntity>


}
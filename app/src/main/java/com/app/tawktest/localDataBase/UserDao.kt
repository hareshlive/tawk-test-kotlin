package com.app.tawktest.localDataBase

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * The Room Magic is in this file, where you map a method call to an SQL query.
 *
 * When you are using complex data types, such as Date, you have to also supply type converters.
 * To keep this example basic, no types that require type converters are used.
 * See the documentation at
 */

@Dao
interface UserDao {

    // The flow always holds/caches latest version of data. Notifies its observers when the
    // data has changed.
//    @Query("SELECT * FROM user_table ORDER BY name ASC")
    @Query("SELECT * FROM user_table")
    fun getGitHubUserList(): Flow<List<User>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User)

    @Query("DELETE FROM user_table")
    suspend fun deleteAll()

    @Update
    suspend fun update(user: User)
}

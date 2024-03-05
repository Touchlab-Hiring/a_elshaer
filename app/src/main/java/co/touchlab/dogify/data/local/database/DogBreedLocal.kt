package co.touchlab.dogify.data.local.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Entity
data class DogBreedLocal(
    val name: String,
    val imageUrl : String
) {
    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0
}

@Dao
interface DogBreedDao {
    @Query("SELECT * FROM dogbreedlocal ORDER BY uid DESC LIMIT 10")
    fun getDogBreeds(): Flow<List<DogBreedLocal>>
    @Insert
    suspend fun insertDogBreed(item: DogBreedLocal)
    @Insert
    suspend fun insertDogBreeds(item: List<DogBreedLocal>)

    @Update
    suspend fun update(item: DogBreedLocal)

    @Query("SELECT EXISTS(SELECT 1 FROM dogbreedlocal LIMIT 1)")
    suspend fun isNotEmpty(): Boolean
}

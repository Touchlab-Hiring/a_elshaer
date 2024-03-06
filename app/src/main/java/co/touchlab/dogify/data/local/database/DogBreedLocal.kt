package co.touchlab.dogify.data.local.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Entity
data class DogBreedLocal(
    @PrimaryKey
    val name: String,
    val imageUrl : String
)

@Dao
interface DogBreedDao {
    @Query("SELECT * FROM dogbreedlocal ORDER BY name ASC")
    fun getDogBreedsFlow(): Flow<List<DogBreedLocal>>

    @Query("SELECT * FROM dogbreedlocal ORDER BY name ASC")
    fun getDogBreedsList(): List<DogBreedLocal>
    @Insert
    suspend fun insertDogBreed(item : DogBreedLocal)
    @Insert
    suspend fun insertDogBreeds(items: List<DogBreedLocal>)

    @Upsert(entity = DogBreedLocal::class)
    suspend fun update(item: DogBreedLocal)

    @Query("SELECT EXISTS(SELECT 1 FROM dogbreedlocal LIMIT 1)")
    suspend fun isNotEmpty(): Boolean
    @Query("DELETE FROM dogbreedlocal")
    suspend fun deleteAllDogBreeds()
}

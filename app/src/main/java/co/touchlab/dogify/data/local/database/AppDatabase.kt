package co.touchlab.dogify.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DogBreedLocal::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dogBreedDao(): DogBreedDao
}

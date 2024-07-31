package co.touchlab.dogify.data.local.di

import android.content.Context
import androidx.room.Room
import co.touchlab.dogify.data.local.database.AppDatabase
import co.touchlab.dogify.data.local.database.DogBreedDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {
    @Provides
    fun provideDogBreedDao(appDatabase: AppDatabase): DogBreedDao {
        return appDatabase.dogBreedDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "DogBreed.db"
        ).build()
    }
}

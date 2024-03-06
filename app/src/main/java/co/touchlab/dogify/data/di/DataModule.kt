package co.touchlab.dogify.data.di

import co.touchlab.dogify.data.DogRepository
import co.touchlab.dogify.data.DogRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Singleton
    @Binds
    fun bindsWeatherSearchRepository(
        dogRepositoryImpl: DogRepositoryImpl
    ): DogRepository
}
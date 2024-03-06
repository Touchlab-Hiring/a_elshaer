package co.touchlab.dogify.testdi

import co.touchlab.dogify.FakeDogBreedsRepository
import co.touchlab.dogify.data.DogRepository
import co.touchlab.dogify.data.di.DataModule
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FakeRepoModule {
    @Provides
    @Singleton
    fun bindFakeRepo(): FakeDogBreedsRepository{
        return FakeDogBreedsRepository()
    }
}

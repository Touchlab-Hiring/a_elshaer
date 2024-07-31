package co.touchlab.dogify.testdi

import co.touchlab.dogify.FakeDogBreedsRepository
import co.touchlab.dogify.data.DogRepository
import co.touchlab.dogify.data.di.DataModule
import dagger.Binds
import dagger.Module
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [DataModule::class]
)
interface FakeDataModule {

    @Binds
    abstract fun bindRepository(
        fakeRepository: FakeDogBreedsRepository
    ): DogRepository
}

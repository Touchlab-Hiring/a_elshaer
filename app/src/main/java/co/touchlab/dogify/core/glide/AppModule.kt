package co.touchlab.dogify.core.glide

import android.content.Context
import android.graphics.drawable.Drawable
import co.touchlab.dogify.R
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    private val error = R.drawable.baseline_running_with_errors_24
    @Provides
    @Singleton
    fun provideGlideInstance(@ApplicationContext context: Context): RequestBuilder<Drawable> {
        return Glide.with(context)
            .asDrawable()
            .thumbnail(0.3f)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .transition(DrawableTransitionOptions.withCrossFade())
            .error(error)
            .timeout(10000)
    }
}
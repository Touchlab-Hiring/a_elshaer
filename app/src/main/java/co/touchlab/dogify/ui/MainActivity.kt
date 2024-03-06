package co.touchlab.dogify.ui

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import co.touchlab.dogify.R
import co.touchlab.dogify.databinding.ActivityMainBinding
import co.touchlab.dogify.databinding.ItemBreedBinding
import co.touchlab.dogify.model.DogBreed
import co.touchlab.dogify.ui.adapter.BreedAdapter
import co.touchlab.dogify.ui.adapter.GridSpacingItemDecoration
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var activityBinding: ActivityMainBinding

    @Inject
    lateinit var requestManager: RequestBuilder<Drawable>

    private val viewModel: DogBreedsViewModel by viewModels()
    private val breedAdapter by lazy { BreedAdapter(requestManager) }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityBinding.root)
        activityBinding.breedList.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(this@MainActivity, 2)
            val spacingInPixels = resources.getDimensionPixelSize(R.dimen.grid_layout_margin)
            addItemDecoration(GridSpacingItemDecoration(2, spacingInPixels))
            adapter = breedAdapter
        }
        activityBinding.swipeRefreshLayout.setOnRefreshListener { viewModel.refreshDogBreeds(true) }
        lifecycleScope.launch {
            viewModel.data.collectLatest { data ->
                Log.d("MainActivity", "Data: $data")
                breedAdapter.submitList(data)
            }

        }/**/

        viewModel.uiState.onEach { state ->
            Log.d("MainActivity", "State: $state")
            when (state) {
                is UiState.Loading -> {
                    Log.d("MainActivity", "Loading")
                    activityBinding.swipeRefreshLayout.isRefreshing = true
                }
                is UiState.Error -> {
                    Log.d("MainActivity", "Error: ${state.exception.message}")
                    activityBinding.swipeRefreshLayout.isRefreshing = false
                    //show error in a snackbar
                    Snackbar.make(
                        activityBinding.root, (state.exception.message
                            ?: getString(R.string.an_error_occurred)), Snackbar.LENGTH_LONG
                    )
                        .setAction(R.string.retry) { viewModel.refreshDogBreeds(true) }
                        .show()
                }
                UiState.Idle -> {
                    activityBinding.swipeRefreshLayout.isRefreshing = false
                }
            }
        }.launchIn(lifecycleScope)
    }
}

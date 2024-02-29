package co.touchlab.dogify

import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.lang.ref.WeakReference

class MainActivity : AppCompatActivity() {

    var breedList: RecyclerView? = null
    var adapter = BreedAdapter()
    var getBreeds = GetBreedsTask(this)
    var spinner: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        spinner = findViewById(R.id.spinner)
        breedList = findViewById(R.id.breed_list)
        breedList?.layoutManager = GridLayoutManager(this, 2)
        breedList?.adapter = adapter
        getBreeds.execute()
    }

    override fun onDestroy() {
        getBreeds.cancel(false)
        super.onDestroy()
    }

    private fun showSpinner(show: Boolean) {
        spinner?.visibility = if (show) View.VISIBLE else View.GONE
    }

    class BreedAdapter : RecyclerView.Adapter<BreedAdapter.ViewHolder>() {

        var data: MutableList<String> = ArrayList()

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_breed, parent, false)
            )
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val breed = data[position]
            holder.nameText.text = breed
        }

        override fun getItemCount(): Int {
            return data.size
        }

        fun addAll(breeds: List<String>) {
            data.addAll(breeds)
            notifyItemRangeInserted(data.size - 1, breeds.size)
        }

        fun clear() {
            val size = data.size
            data.clear()
            notifyItemRangeRemoved(0, size)
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var nameText: TextView

            init {
                nameText = itemView.findViewById(R.id.name)
            }
        }
    }

    class GetBreedsTask internal constructor(activity: MainActivity) :
        AsyncTask<Void?, Void?, List<String>>() {
        private val activityRef: WeakReference<MainActivity>
        @Deprecated("Deprecated in Java")
        override fun onPreExecute() {
            val activity = activityRef.get()
            if (activity != null) {
                activity.showSpinner(true)
                activity.adapter.clear()
            }
        }

        @Deprecated("Deprecated in Java")
        override fun doInBackground(vararg p0: Void?): List<String> {
            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
            val retrofit = Retrofit.Builder().baseUrl("https://dog.ceo/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            val service = retrofit.create(DogService::class.java)
            return if (isCancelled) {
                emptyList()
            } else getBreedNames(service)
        }

        private fun getBreedNames(service: DogService): List<String> {
            try {
                val result = service.getBreeds().execute().body()
                if (result?.message != null) {
                    return result.message
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return emptyList()
        }

        @Deprecated("Deprecated in Java")
        override fun onPostExecute(breeds: List<String>) {
            val activity = activityRef.get()
            if (activity != null) {
                activity.showSpinner(false)
                activity.adapter.addAll(breeds)
            }
        }

        init {
            activityRef = WeakReference(activity)
        }
    }
}

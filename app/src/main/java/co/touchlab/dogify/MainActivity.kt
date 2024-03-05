package co.touchlab.dogify

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    var breedList: RecyclerView? = null
    var adapter = BreedAdapter()
    var spinner: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        spinner = findViewById(R.id.spinner)
        breedList = findViewById(R.id.breed_list)
        breedList?.layoutManager = GridLayoutManager(this, 2)
        breedList?.adapter = adapter
    }

    override fun onDestroy() {
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
}

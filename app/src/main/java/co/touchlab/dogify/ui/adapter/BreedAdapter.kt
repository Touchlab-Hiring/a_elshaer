package co.touchlab.dogify.ui.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import co.touchlab.dogify.R
import co.touchlab.dogify.databinding.ItemBreedBinding
import co.touchlab.dogify.model.DogBreed
import com.bumptech.glide.RequestBuilder

class BreedAdapter(private val requestManager: RequestBuilder<Drawable>) :
    ListAdapter<DogBreed, BreedAdapter.ViewHolder>(DogBreedDiffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemBreedBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val breed = currentList[position]
        holder.bind(breed)
    }

    override fun getItemCount(): Int {
        return currentList.size
    }

    inner class ViewHolder(private val itemViewBinding: ItemBreedBinding) :
        RecyclerView.ViewHolder(itemViewBinding.root) {
        fun bind(breed: DogBreed) {
            itemViewBinding.textName.text = breed.name
            if (breed.imageUrl.isNotBlank()) {
                requestManager.load(breed.imageUrl)
                    .into(itemViewBinding.imageBreed)
            } else {
                itemViewBinding.imageBreed.setImageResource(R.drawable.twotone_downloading_24)
            }
        }
    }
}


object DogBreedDiffUtil : DiffUtil.ItemCallback<DogBreed>() {
    override fun areItemsTheSame(oldItem: DogBreed, newItem: DogBreed): Boolean {
        return (oldItem.name + oldItem.imageUrl) == (newItem.name + newItem.imageUrl)
    }

    override fun areContentsTheSame(oldItem: DogBreed, newItem: DogBreed): Boolean {
        return oldItem == newItem
    }

}

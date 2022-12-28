package com.sep.gothere.features.root_postlog.search.branch_search.adapter

import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.sep.gothere.R
import com.sep.gothere.data.NetworkVenueRepository
import com.sep.gothere.databinding.WidgetSearchBasicVisitBinding
import com.sep.gothere.features.root_postlog.search.branch_search.model.SearchWidgetBasicVisit
import kotlinx.coroutines.*

class SearchRecyclerViewAdapter(
    private val networkVenueRepository: NetworkVenueRepository,
    private val content: List<SearchWidgetBasicVisit>
) : RecyclerView.Adapter<SearchRecyclerViewAdapter.SearchViewHolder>() {

    var onVisitButtonClicked: ((SearchWidgetBasicVisit) -> Unit)? = null

    inner class SearchViewHolder(binding: WidgetSearchBasicVisitBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.visitButton.setOnClickListener {
                onVisitButtonClicked?.invoke(content[bindingAdapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val binding = WidgetSearchBasicVisitBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        WidgetSearchBasicVisitBinding.bind(holder.itemView).apply {
            MainScope().launch {
                loadProfileImage(this@apply, content[position].venueID)
            }
            venueName.text = content[position].venueName
            venueShortDesc.text = content[position].venueShortDesc
            val venueUsernameFinal = "#" + content[position].venueUsername
            venueUsername.text = venueUsernameFinal
        }
    }

    private suspend fun loadProfileImage(binding: WidgetSearchBasicVisitBinding, venueID: Long) {
        withContext(Dispatchers.Main) {
            val profileImageByteArray: ByteArray = Base64.decode(
                networkVenueRepository.getVenueProfileImageRP(venueID).data,
                Base64.DEFAULT
            )

            Glide.with(binding.venueProfileImage.context)
                .asBitmap()
                .load(profileImageByteArray)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.photo_placeholder)
                .into(binding.venueProfileImage)
        }
    }

    override fun getItemCount(): Int = content.size
}
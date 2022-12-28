package com.sep.gothere.features.root_postlog.explore.branch_explore.adapter

import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.sep.gothere.R
import com.sep.gothere.data.NetworkVenueRepository
import com.sep.gothere.databinding.WidgetExploreBasicVisitBinding
import com.sep.gothere.features.root_postlog.explore.branch_explore.model.ExploreWidgetBasicVisit
import kotlinx.coroutines.*

class ExploreRecyclerViewAdapter(
    private val networkVenueRepository: NetworkVenueRepository,
    private val content: List<ExploreWidgetBasicVisit>
) :
    RecyclerView.Adapter<ExploreRecyclerViewAdapter.ExploreViewHolder>() {

    var onVisitButtonClicked: ((ExploreWidgetBasicVisit) -> Unit)? = null

    inner class ExploreViewHolder(binding: WidgetExploreBasicVisitBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.visitButton.setOnClickListener {
                onVisitButtonClicked?.invoke(content[bindingAdapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreViewHolder {
        val binding = WidgetExploreBasicVisitBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExploreViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExploreViewHolder, position: Int) {
        WidgetExploreBasicVisitBinding.bind(holder.itemView).apply {

            MainScope().launch {
                loadProfileImage(this@apply, content[position].venueID)
            }

            MainScope().launch {
                loadCoverImage(this@apply, content[position].venueID)
            }

            venueName.text = content[position].venueName
            venueShortDesc.text = content[position].venueShortDesc
            val venueUsernameFinal = "#" + content[position].venueUsername
            venueUsername.text = venueUsernameFinal
        }
    }

    private suspend fun loadProfileImage(binding: WidgetExploreBasicVisitBinding, venueID: Long) {
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

    private suspend fun loadCoverImage(binding: WidgetExploreBasicVisitBinding, venueID: Long) {
        withContext(Dispatchers.Main) {
            val coverImageByteArray: ByteArray = Base64.decode(
                networkVenueRepository.getVenueCoverImageRP(venueID).data,
                Base64.DEFAULT
            )

            Glide.with(binding.venueCoverImage.context)
                .asBitmap()
                .load(coverImageByteArray)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.photo_placeholder)
                .into(binding.venueCoverImage)
        }
    }

    override fun getItemCount(): Int = content.size
}
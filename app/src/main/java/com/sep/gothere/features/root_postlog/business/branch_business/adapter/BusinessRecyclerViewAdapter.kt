package com.sep.gothere.features.root_postlog.business.branch_business.adapter

import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.sep.gothere.R
import com.sep.gothere.data.NetworkVenueRepository
import com.sep.gothere.databinding.WidgetBusinessBasicVisitBinding
import com.sep.gothere.features.root_postlog.business.branch_business.model.BusinessWidgetBasicVisit
import kotlinx.coroutines.*

class BusinessRecyclerViewAdapter(
    private val networkVenueRepository: NetworkVenueRepository,
    private val content: List<BusinessWidgetBasicVisit>
) : RecyclerView.Adapter<BusinessRecyclerViewAdapter.BusinessViewHolder>() {

    var onVisitButtonClicked: ((BusinessWidgetBasicVisit) -> Unit)? = null

    inner class BusinessViewHolder(binding: WidgetBusinessBasicVisitBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.visitButton.setOnClickListener {
                onVisitButtonClicked?.invoke(content[bindingAdapterPosition])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusinessViewHolder {
        val binding = WidgetBusinessBasicVisitBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BusinessViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BusinessViewHolder, position: Int) {
        WidgetBusinessBasicVisitBinding.bind(holder.itemView).apply {
            MainScope().launch {
                loadProfileImage(this@apply, content[position].venueID)
            }
            venueName.text = content[position].venueName
            venueShortDesc.text = content[position].venueShortDesc
            val venueUsernameFinal = "#" + content[position].venueUsername
            venueUsername.text = venueUsernameFinal
        }
    }

    private suspend fun loadProfileImage(binding: WidgetBusinessBasicVisitBinding, venueID: Long) {
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
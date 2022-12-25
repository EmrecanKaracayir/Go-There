package com.sep.gothere.features.root_postlog.explore.branch_explore.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sep.gothere.databinding.WidgetExploreBasicVisitBinding
import com.sep.gothere.features.root_postlog.explore.branch_explore.model.WidgetBasicVisit

class ExploreRecycleViewAdapter(private val content: List<WidgetBasicVisit>) :
    RecyclerView.Adapter<ExploreRecycleViewAdapter.ExploreViewHolder>() {

    inner class ExploreViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExploreViewHolder {
        val binding = WidgetExploreBasicVisitBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExploreViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: ExploreViewHolder, position: Int) {
        WidgetExploreBasicVisitBinding.bind(holder.itemView).apply {
            venueCoverImage.setImageBitmap(content[position].venueCoverImage)
            venueLogoImage.setImageBitmap(content[position].venueLogoImage)
            venueName.text = content[position].venueName
            venueShortDesc.text = content[position].venueShortDesc
            venueID.text = content[position].venueID
        }
    }

    override fun getItemCount(): Int = content.size;
}
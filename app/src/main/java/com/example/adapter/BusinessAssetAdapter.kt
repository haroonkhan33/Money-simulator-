package com.example.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.R
import com.example.databinding.ItemAssetCardBinding
import com.example.model.BusinessAsset

class BusinessAssetAdapter(
    private val assets: List<BusinessAsset>,
    private val onUpgradeClicked: (BusinessAsset, Int) -> Unit,
    private val onCardTapped: (BusinessAsset, Int) -> Unit
) : RecyclerView.Adapter<BusinessAssetAdapter.AssetViewHolder>() {

    inner class AssetViewHolder(val binding: ItemAssetCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetViewHolder {
        val binding = ItemAssetCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AssetViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AssetViewHolder, position: Int) {
        val asset = assets[position]
        val context = holder.itemView.context

        with(holder.binding) {
            tvAssetName.text = asset.name
            tvAssetLevel.text = context.getString(R.string.level_format, asset.level)
            tvAssetRate.text = asset.formatIncome()
            ivAssetIcon.setImageResource(asset.iconRes)
            ivAssetIcon.setColorFilter(ContextCompat.getColor(context, R.color.neon_green_primary))

            val progressInt = (asset.cycleProgress * 100).toInt().coerceIn(0, 100)
            progressProduction.progress = progressInt

            btnUpgrade.text = "UP ${asset.formatCost()}"

            btnUpgrade.setOnClickListener {
                val pos = holder.adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onUpgradeClicked(asset, pos)
                }
            }

            cardAsset.setOnClickListener {
                cardAsset.animate()
                    .scaleX(0.96f)
                    .scaleY(0.96f)
                    .setDuration(80)
                    .withEndAction {
                        cardAsset.animate()
                            .scaleX(1.0f)
                            .scaleY(1.0f)
                            .setDuration(120)
                            .start()
                    }.start()
                val pos = holder.adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onCardTapped(asset, pos)
                }
            }
        }
    }

    override fun getItemCount(): Int = assets.size
}

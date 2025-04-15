package com.example.wordbubblescluster.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wordbubblescluster.R
import com.example.wordbubblescluster.databinding.LevelCardBinding
import com.example.wordbubblescluster.model.Level
import com.example.wordbubblescluster.model.Repository

class LevelSelectAdapter(private val viewModel: MainViewModel,
                         private val levelRepository: Repository,
                         private val onClickListener: (Level) -> Unit)
    : RecyclerView.Adapter<LevelSelectAdapter.VH>() {

    inner class VH(val binding: LevelCardBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = LevelCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = VH(binding)
        return holder
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val binding = holder.binding
        val levelNumber = levelRepository.getLevelRange().first + position
        val level = levelRepository.fetchLevel(levelNumber)
        binding.levelTV.text = level.level.toString()
        viewModel.observeProfile().value?.let { profile ->
            // Level Unlocked
            if(profile.levelsCompleted >= level.level - 1) {
                // Level Unlocked, Completed
                if(profile.levelsCompleted >= level.level) {
                    binding.unlockedIV.visibility = View.VISIBLE
                    binding.unlockedIV.setImageResource(R.drawable.complete_96)
                    val color = holder.itemView.context.resources.getColor(R.color.primary, holder.itemView.context.theme)
                    binding.backgroundCV.setCardBackgroundColor(color)
                }
                // Level Unlocked, Not Completed
                else {
                    binding.unlockedIV.visibility = View.INVISIBLE
                    val color = holder.itemView.context.resources.getColor(R.color.secondary, holder.itemView.context.theme)
                    binding.backgroundCV.setCardBackgroundColor(color)
                }
                binding.root.setOnClickListener {
                    onClickListener(level)
                }
            }
            // Level Locked
            else {
                binding.unlockedIV.visibility = View.VISIBLE
                binding.unlockedIV.setImageResource(R.drawable.locked_96)
                binding.root.setOnClickListener(null)
                binding.backgroundCV.setCardBackgroundColor(Color.GRAY)

            }
        }
    }

    override fun getItemCount(): Int {
        return levelRepository.getNumLevels()
    }
}
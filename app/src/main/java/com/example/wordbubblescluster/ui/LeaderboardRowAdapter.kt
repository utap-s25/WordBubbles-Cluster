package com.example.wordbubblescluster.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wordbubblescluster.R
import com.example.wordbubblescluster.databinding.LeaderboardRowBinding
import com.example.wordbubblescluster.model.Profile

class LeaderboardRowAdapter(private val viewModel: MainViewModel,
    private val navigateToProfileOtherPlayer: (Profile)->Unit)
    : RecyclerView.Adapter<LeaderboardRowAdapter.VH>() {

    inner class VH(val binding: LeaderboardRowBinding)
        : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = LeaderboardRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val holder = VH(binding)
        return holder
    }

    override fun getItemCount(): Int {
        return viewModel.observeLeaderboard().value?.size ?: 0
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val binding = holder.binding
        viewModel.observeLeaderboard().value?.let { leaderboard ->
            val profile = leaderboard[position]
            binding.nameTV.text = profile.name
            binding.levelsCompletedTV.text = profile.levelsCompleted.toString()
            binding.profilePictureIV.setOnClickListener {
                navigateToProfileOtherPlayer(profile)
            }
            if (profile.profilePictureUuid.isNotEmpty())
                viewModel.glideFetch(profile.uid, profile.profilePictureUuid, binding.profilePictureIV)
            else
                binding.profilePictureIV.setImageResource(R.drawable.profile_circle_128)
            viewModel.observeProfile().value?.let { userProfile ->
                if(userProfile.uid == profile.uid) {
                    binding.profilePictureIV.setStrokeColorResource(R.color.secondary)
                    binding.nameAndLevelsCompletedGroup.setBackgroundResource(R.drawable.layout_bg_alt)
                    binding.profilePictureIV.setOnClickListener(null)
                } else {
                    binding.profilePictureIV.setStrokeColorResource(R.color.primary)
                    binding.nameAndLevelsCompletedGroup.setBackgroundResource(R.drawable.layout_bg)
                    binding.profilePictureIV.setOnClickListener {
                        navigateToProfileOtherPlayer(profile)
                    }
                }
            }
        }
        viewModel.observeRanking().value?.let { ranking ->
            binding.rankTV.text = ranking[position].toString()
        }
    }

}
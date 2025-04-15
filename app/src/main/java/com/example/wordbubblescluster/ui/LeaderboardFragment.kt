package com.example.wordbubblescluster.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.wordbubblescluster.R
import com.example.wordbubblescluster.databinding.FragmentLeaderboardBinding
import com.example.wordbubblescluster.model.Profile

class MarginItemDecoration(private val spaceSize: Int) : RecyclerView.ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect, view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        with(outRect) {
            if (parent.getChildAdapterPosition(view) == 0) {
                top = spaceSize
            }
            left = spaceSize
            right = spaceSize
            bottom = spaceSize
        }
    }
}

class LeaderboardFragment: Fragment(R.layout.fragment_leaderboard) {
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentLeaderboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLeaderboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun initRecyclerView() {
        val layoutManager = LinearLayoutManager(context)
        binding.leaderboardRV.layoutManager = layoutManager
        val adapter = LeaderboardRowAdapter(viewModel) {
            val action = LeaderboardFragmentDirections.actionLeaderboardFragmentToProfileOtherPlayerFragment(it)
            findNavController().navigate(action)
        }
        binding.leaderboardRV.adapter = adapter
        binding.leaderboardRV.addItemDecoration(MarginItemDecoration(20))

        viewModel.observeRanking().observe(viewLifecycleOwner) { ranking ->
            adapter.notifyDataSetChanged()
            viewModel.observeLeaderboard().value?.let { leaderboard ->
                val index = leaderboard.indexOfFirst { profile: Profile -> profile.uid == viewModel.observeProfile().value!!.uid }
                if (index >= 0)
                    binding.playerRow.rankTV.text = ranking[index].toString()
            }
        }
        viewModel.addLeaderboardSnapshotListener(requireActivity())
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setTitle("Leaderboard")
        viewModel.hideActionBarButtons()
        initRecyclerView()
        binding.playerRow.nameAndLevelsCompletedGroup.setBackgroundResource(R.drawable.layout_bg_alt)
        viewModel.observeProfile().value?.let { profile ->
            binding.playerRow.nameTV.text = profile.name
            binding.playerRow.levelsCompletedTV.text = profile.levelsCompleted.toString()
            binding.playerRow.profilePictureIV.setStrokeColorResource(R.color.secondary)
            if(profile.profilePictureUuid.isNotEmpty())
                viewModel.glideFetch(profile.uid, profile.profilePictureUuid, binding.playerRow.profilePictureIV)
        }
    }
}
package com.example.wordbubblescluster.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.example.wordbubblescluster.R
import com.example.wordbubblescluster.databinding.FragmentProfileOtherPlayerBinding

class ProfileOtherPlayerFragment: Fragment(R.layout.fragment_profile_other_player) {
    private val args: ProfileOtherPlayerFragmentArgs by navArgs()

    private val viewModel: MainViewModel by activityViewModels()

    private var _binding: FragmentProfileOtherPlayerBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileOtherPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setTitle("Profile")
        viewModel.hideActionBarButtons()
        binding.displayName.text = args.profile.name
        binding.bio.text = args.profile.bio
        binding.levelsCompleted.text = args.profile.levelsCompleted.toString()
        if(args.profile.profilePictureUuid.isNotEmpty()) {
            viewModel.glideFetch(args.profile.uid, args.profile.profilePictureUuid, binding.profilePicture)
        }
    }
}
package com.example.wordbubblescluster.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.wordbubblescluster.R
import com.example.wordbubblescluster.databinding.FragmentProfileBinding


class ProfileFragment: Fragment(R.layout.fragment_profile) {
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun initLogoutButton() {
        binding.logoutButton.setOnClickListener {
            viewModel.logout()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setTitle("My Profile")
        viewModel.hideActionBarButtons()
        initLogoutButton()
        viewModel.observeProfile().observe(viewLifecycleOwner) { profile ->
            binding.displayName.text = profile.name
            binding.levelsCompleted.text = profile.levelsCompleted.toString()
            binding.bio.text = profile.bio
            if(profile.profilePictureUuid.isNotEmpty()) {
                viewModel.glideFetch(profile.uid, profile.profilePictureUuid, binding.profilePicture)
            } else {
                binding.profilePicture.setImageResource(R.drawable.profile_circle_128)
            }
            binding.editProfileButton.setOnClickListener {
                val action = ProfileFragmentDirections.actionProfileFragmentToEditProfileFragment(profile)
                findNavController().navigate(action)
            }
        }
    }
}
package com.example.wordbubblescluster.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.wordbubblescluster.R
import com.example.wordbubblescluster.databinding.FragmentEditProfileBinding

class EditProfileFragment: Fragment(R.layout.fragment_edit_profile) {
    private val args: ProfileOtherPlayerFragmentArgs by navArgs()

    private val viewModel: MainViewModel by activityViewModels()
    private val editProfileViewModel: EditProfileViewModel by activityViewModels()

    private var _binding: FragmentEditProfileBinding? = null

    private val binding get() = _binding!!

    private val pickPhotoLauncher = registerForActivityResult(PickVisualMedia()) { uri ->
        if(uri != null) {
            editProfileViewModel.setProfilePictureUri(uri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun initEditProfilePicture() {
        val profile = args.profile
        if(profile.profilePictureUuid.isNotEmpty())
            viewModel.glideFetch(profile.uid, profile.profilePictureUuid, binding.editProfilePicture)
        binding.editProfilePicture.setOnClickListener {
            pickPhotoLauncher.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
        }
        editProfileViewModel.setProfilePictureUri(null)
        editProfileViewModel.observeProfilePictureUri().observe(viewLifecycleOwner) { uri ->
            if(uri != null)
                binding.editProfilePicture.setImageURI(uri)
        }
    }

    private fun initEditTexts() {
        val profile = args.profile
        editProfileViewModel.setName(profile.name)
        editProfileViewModel.setBio(profile.bio)
        binding.editName.setText(profile.name)
        binding.editBio.setText(profile.bio)
        binding.editName.doOnTextChanged { text, _, _, _ ->
            editProfileViewModel.setName(text.toString())
        }
        binding.editBio.doOnTextChanged { text, _, _, _ ->
            editProfileViewModel.setBio(text.toString())
        }

    }

    private fun initSubmitButton() {
        val profile = args.profile
        binding.submitButton.setOnClickListener {
            val name = editProfileViewModel.observeName().value!!
            if(name.isEmpty()) {
                binding.editNameError.visibility = View.VISIBLE
            } else {
                binding.editNameError.visibility = View.GONE
                editProfileViewModel.observeProfilePictureUri().value?.let {
                    viewModel.setNewProfilePicture(it, profile.uid)
                }
                viewModel.setNewName(profile.uid, name)
                viewModel.setNewBio(profile.uid, editProfileViewModel.observeBio().value!!)
                findNavController().popBackStack()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setTitle("Edit Profile")
        viewModel.hideActionBarButtons()
        initEditProfilePicture()
        initEditTexts()
        initSubmitButton()
        binding.editNameError.visibility = View.GONE
    }
}
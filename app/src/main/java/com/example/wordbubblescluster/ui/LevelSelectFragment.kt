package com.example.wordbubblescluster.ui

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.wordbubblescluster.R
import com.example.wordbubblescluster.databinding.FragmentLevelSelectBinding
import com.example.wordbubblescluster.isInvalid
import com.example.wordbubblescluster.model.Profile
import com.example.wordbubblescluster.model.Repository


class GridSpacingItemDecoration(
    private val spanCount: Int,
    private val spacing: Int,
    private val includeEdge: Boolean
) :
    ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view) // item position
        val column = position % spanCount // item column

        if (includeEdge) {
            outRect.left =
                spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
            outRect.right =
                (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)

            if (position < spanCount) { // top edge
                outRect.top = spacing
            }
            outRect.bottom = spacing // item bottom
        } else {
            outRect.left = column * spacing / spanCount // column * ((1f / spanCount) * spacing)
            outRect.right =
                spacing - (column + 1) * spacing / spanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)
            if (position >= spanCount) {
                outRect.top = spacing // item top
            }
        }
    }
}

class LevelSelectFragment: Fragment(R.layout.fragment_level_select) {
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentLevelSelectBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setFragmentResultListener("requestKey") { requestKey, bundle ->
            val levelCompleted = bundle.getBoolean("levelCompleted")
            val levelNumber = bundle.getInt("levelNumber")
            if (levelCompleted)
                viewModel.observeCurrentUser().value?.let {
                    viewModel.levelCompleted(it.uid, levelNumber)
                }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLevelSelectBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun Profile.isInvalid(): Boolean {
        return uid.isEmpty()
    }

    private fun initListeners() {
        // If a profile does not exist for a user, create one.
        viewModel.observeCurrentUser().observe(viewLifecycleOwner) { user ->
            if(!user.isInvalid()) {
                viewModel.observeProfile().observe(viewLifecycleOwner) { profile ->
                    if(profile.isInvalid()) {
                        val newProfile = Profile(
                            uid = user.uid,
                            name = user.name
                        )
                        viewModel.createOrUpdateProfile(newProfile)
                    }
                }
                viewModel.fetchProfile(user.uid)
            }
        }
    }

    private fun initLevelList() {
        val layoutManager = GridLayoutManager(context, 3)
        binding.levelSelectRV.layoutManager = layoutManager

        val adapter = LevelSelectAdapter(viewModel, Repository()) { level ->
            val action = LevelSelectFragmentDirections.actionLevelSelectFragmentToGameFragment(level)
            findNavController().navigate(action)
        }
        binding.levelSelectRV.adapter = adapter
        binding.levelSelectRV.addItemDecoration(GridSpacingItemDecoration(3, 30, true))

        viewModel.observeProfile().observe(viewLifecycleOwner) {
            adapter.notifyDataSetChanged()
        }
    }

    private fun initProfileButton() {
        viewModel.observeProfile().observe(viewLifecycleOwner) { profile ->
            if(profile.profilePictureUuid.isNotEmpty())
                viewModel.fetchProfilePictureActionBar(profile.uid, profile.profilePictureUuid)
            else
                viewModel.resetProfilePictureActionBar()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setTitle("Level Select")
        viewModel.showActionBarButtons()
        initListeners()
        initProfileButton()
        initLevelList()
    }
}
package com.example.wordbubblescluster.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.wordbubblescluster.R
import com.example.wordbubblescluster.databinding.FragmentLoginBinding

class LoginFragment: Fragment(R.layout.fragment_login) {
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun initLoginButton(){
        binding.loginButton.setOnClickListener {
            viewModel.login()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setTitle("")
        viewModel.hideActionBarButtons()
        initLoginButton()
    }
}
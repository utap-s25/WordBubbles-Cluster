package com.example.wordbubblescluster

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.example.wordbubblescluster.databinding.ActionBarBinding
import com.example.wordbubblescluster.databinding.ActivityMainBinding
import com.example.wordbubblescluster.ui.LevelSelectFragmentDirections
import com.example.wordbubblescluster.ui.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var actionBarBinding: ActionBarBinding
    private lateinit var authUser: AuthUser
    private val viewModel: MainViewModel by viewModels()

    private fun initActionBar(actionBar: ActionBar) {
        // Disable default action bar and enable custom title and layout
        actionBar.setDisplayShowTitleEnabled(false)
        actionBar.setDisplayShowCustomEnabled(true)
        actionBarBinding = ActionBarBinding.inflate(layoutInflater)
        // Apply the custom view
        actionBar.customView = actionBarBinding.root
        viewModel.initActionBarBinding(actionBarBinding)
        viewModel.hideActionBarButtons()
    }

    private fun NavController.safeNavigate(direction: NavDirections) {
        currentDestination?.
        getAction(direction.actionId)?.
        run {
            navigate(direction)
        }
    }

    private fun actionBarLaunchLeaderboard() {
        actionBarBinding.actionLeaderboard.setOnClickListener {
            val action = LevelSelectFragmentDirections.actionLevelSelectFragmentToLeaderboardFragment()
            navController.safeNavigate(action)
        }
    }

    private fun actionBarLaunchProfile() {
        actionBarBinding.actionProfile.setOnClickListener {
            val action = LevelSelectFragmentDirections.actionLevelSelectFragmentToProfileFragment()
            navController.safeNavigate(action)
        }
    }

    private fun initTitleObserver() {
        viewModel.observeTitle().observe(this) { title ->
            actionBarBinding.actionTitle.text = title
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // Set up layout
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        supportActionBar?.let {
            initActionBar(it)
        }

        initTitleObserver()
        actionBarLaunchProfile()
        actionBarLaunchLeaderboard()

        // Set up nav graph
        navController = findNavController(R.id.main_frame)
        val appBarConfiguration = AppBarConfiguration.Builder(setOf(R.id.loginFragment, R.id.levelSelectFragment)).build()
        binding.toolbar.setupWithNavController(navController, appBarConfiguration)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Disable back press
        val callback = object : OnBackPressedCallback(true) { // 'true' enables it initially
            override fun handleOnBackPressed() { }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    // We can only safely initialize AuthUser once onCreate has completed.
    override fun onStart() {
        super.onStart()
        authUser = AuthUser(activityResultRegistry)
        lifecycle.addObserver(authUser)
        authUser.observeUser().observe(this) { user ->
            viewModel.setCurrentAuthUser(authUser)
            viewModel.setCurrentUser(user)
            if(user.isInvalid()) {
                navController.navigate(R.id.action_global_loginFragment)
            } else {
                navController.navigate(R.id.action_global_levelSelectFragment)
            }
        }
    }
}
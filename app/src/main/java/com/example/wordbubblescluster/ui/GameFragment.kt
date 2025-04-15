package com.example.wordbubblescluster.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.wordbubblescluster.R
import com.example.wordbubblescluster.databinding.FragmentGameBinding
import com.example.wordbubblescluster.databinding.WordBubbleBinding
import com.example.wordbubblescluster.databinding.WordBubbleHintBinding

class GameFragment: Fragment(R.layout.fragment_game) {
    private val viewModel: MainViewModel by activityViewModels()
    private val args: GameFragmentArgs by navArgs()

    private var _binding: FragmentGameBinding? = null
    private val binding get() = _binding!!

    private val gameViewModel: GameViewModel by activityViewModels()

    private lateinit var wordBubbles: Array<Array<WordBubbleBinding>>

    private lateinit var wordBubbleHints: Array<MutableList<WordBubbleHintBinding>>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGameBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun initGameArea() {
        val level = args.level
        wordBubbles = Array(level.height) {Array(level.width) { WordBubbleBinding.inflate(layoutInflater) } }
        level.letters.forEachIndexed {index, char ->
            val r = index / args.level.height
            val c = index % args.level.width
            wordBubbles[r][c] = initBubble(r, c)
        }
        binding.wordBubbleArea.viewTreeObserver.addOnGlobalLayoutListener {
            val width = binding.wordBubbleArea.measuredWidth
            val height = binding.wordBubbleArea.measuredHeight
            resizeBubbles(width, height)
        }
    }

    private fun initBubble(r: Int, c: Int): WordBubbleBinding {
        val container = binding.wordBubbleArea
        val wordBubbleBinding = WordBubbleBinding.inflate(layoutInflater)
        wordBubbleBinding.root.layoutParams = LinearLayout.LayoutParams(
            0,
            0
        )
        wordBubbleBinding.bubbleText.text = args.level.letters[r * args.level.width + c].toString()
        container.addView(wordBubbleBinding.root)
        return wordBubbleBinding
    }

    private fun resizeBubbles(width: Int, height: Int) {
        for (r in 0 until wordBubbles.size) {
            for (c in 0 until wordBubbles[r].size) {
                val wordBubbleBinding = wordBubbles[r][c]
                wordBubbleBinding?.let {
                    val bubbleWidth = width / args.level.width
                    val bubbleHeight = height / args.level.height
                    it.root.layoutParams = LinearLayout.LayoutParams(
                        bubbleWidth,
                        bubbleHeight
                    )
                    it.root.x = (c * bubbleWidth).toFloat()
                    it.root.y = (r * bubbleHeight).toFloat()
                }
            }
        }
    }

    private fun initHints() {
        val level = args.level
        wordBubbleHints = Array(level.solution.size) { mutableListOf() }
        level.solution.forEachIndexed { index, word ->
            val hintContainer = initLinearLayout()
            for(letter in word.toCharArray())
                wordBubbleHints[index].add(initHintBubble(hintContainer, letter))
        }
    }

    private fun initLinearLayout(): LinearLayout {
        val container = binding.hintsArea
        val layout = LinearLayout(context)
        layout.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layout.orientation = LinearLayout.HORIZONTAL
        container.addView(layout)
        return layout
    }

    private fun initHintBubble(container: LinearLayout, letter: Char): WordBubbleHintBinding {
        val wordBubbleHintBinding = WordBubbleHintBinding.inflate(layoutInflater)
        wordBubbleHintBinding.root.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        wordBubbleHintBinding.bubbleText.text = letter.toString()
        container.addView(wordBubbleHintBinding.root)
        return wordBubbleHintBinding
    }

    private fun initListeners() {
        gameViewModel.observeWord().observe(viewLifecycleOwner) { currentWord ->
            binding.currentWord.text = currentWord
            if(currentWord.isEmpty())
                binding.submitButton.setBackgroundColor(Color.GRAY)
            else {
                val color = requireContext().resources.getColor(R.color.secondary, requireContext().theme)
                binding.submitButton.setBackgroundColor(color)
                binding.submitButton.setOnClickListener {
                    gameViewModel.submitWord()
                }
            }
        }
        gameViewModel.observeBubbles().observe(viewLifecycleOwner) { bubbles ->
            for(r in 0 until bubbles.size) {
                for (c in 0 until bubbles[r].size) {
                    val bubble = bubbles[r][c]
                    val wordBubble = wordBubbles[r][c]
                    if (bubble.isUsed) {
                        wordBubble.root.visibility = View.INVISIBLE
                        wordBubble.root.setOnClickListener(null)
                    }
                    else if (bubble.isCurrentlySelected) {
                        val color = requireContext().resources.getColor(R.color.primary_light, requireContext().theme)
                        wordBubble.bubbleBackground.setCardBackgroundColor(color)
                        wordBubble.root.setOnClickListener(null)
                    } else if (bubble.isSelected) {
                        val color = requireContext().resources.getColor(R.color.primary_dark, requireContext().theme)
                        wordBubble.bubbleBackground.setCardBackgroundColor(color)
                        wordBubble.root.setOnClickListener(null)
                    } else {
                        val color = requireContext().resources.getColor(R.color.primary, requireContext().theme)
                        wordBubble.bubbleBackground.setCardBackgroundColor(color)
                        wordBubble.root.setOnClickListener {
                            gameViewModel.selectBubble(r, c)
                        }
                    }
                }
            }
        }

        gameViewModel.observeHints().observe(viewLifecycleOwner) { hints ->
            hints.forEachIndexed { index, hint ->
                if(hint.isSolved) {
                    for(bubble in wordBubbleHints[index]) {
                        bubble.bubbleText.setTextColor(Color.WHITE)
                        val color = requireContext().resources.getColor(R.color.primary, requireContext().theme)
                        bubble.bubbleBackground.setCardBackgroundColor(color)
                    }
                } else {
                    for(bubble in wordBubbleHints[index]) {
                        bubble.bubbleText.setTextColor(Color.GRAY)
                        bubble.bubbleBackground.setCardBackgroundColor(Color.GRAY)
                    }
                }
            }
        }

        gameViewModel.observeIsSolved().observe(viewLifecycleOwner) { isSolved ->
            if(isSolved) {
                val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                builder
                    .setMessage("You solved the puzzle.")
                    .setTitle("Congratulations!")
                    .setPositiveButton("GO TO LEVEL SELECT") { _, _ ->
                        findNavController().popBackStack()
                    }
                val dialog: AlertDialog = builder.create()
                dialog.show()
                setFragmentResult("requestKey", bundleOf("levelCompleted" to true, "levelNumber" to args.level.level))
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.hideActionBarButtons()
        viewModel.setTitle("Level ${args.level.level}")
        gameViewModel.setLevel(args.level)
        initGameArea()
        initHints()
        initListeners()
    }
}
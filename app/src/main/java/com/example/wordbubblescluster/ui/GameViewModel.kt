package com.example.wordbubblescluster.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.wordbubblescluster.model.Level
import kotlin.math.abs

data class WordBubble(
    val letter: Char = ' ',
    val r: Int = 0,
    val c: Int = 0,
    var isSelected: Boolean = false,
    var isCurrentlySelected: Boolean = false,
    var isUsed: Boolean = false
) {
    fun isAdjacentTo(other: WordBubble): Boolean {
        if(r == other.r && c == other.c)
            return false
        return abs(r - other.r) <= 1 && abs(c - other.c) <= 1
    }
}

data class Hint(
    val word: String,
    var isSolved: Boolean = false
)

class GameViewModel(): ViewModel() {
    private lateinit var level: Level

    private var bubbles = MutableLiveData<Array<Array<WordBubble>>>()

    private var currentWord =  MutableLiveData("")

    private var currentWordBubbles = mutableListOf<WordBubble>()

    private var lastBubble: WordBubble? = null

    private var hints = MutableLiveData<Array<Hint>>()

    private var isSolved = MediatorLiveData<Boolean>().apply {
        addSource(hints) { hints ->
            value = hints.all { it.isSolved }
        }
    }

    private fun resetCurrentWord() {
        currentWord.value = ""
        currentWordBubbles = mutableListOf()
        lastBubble = null
    }

    private fun reset() {
        resetCurrentWord()
        isSolved.value = false
    }

    fun setLevel(level: Level) {
        reset()
        this.level = level

        val bubbles = Array(level.height) {Array(level.width) { WordBubble() } }
        level.letters.forEachIndexed { index, letter ->
            val r = index / level.height
            val c = index % level.width
            bubbles[r][c] = WordBubble(letter, r, c)
        }
        this.bubbles.value = bubbles

        val hints = Array(level.solution.size) { Hint("") }
        level.solution.forEachIndexed { index, word ->
            hints[index] = Hint(word)
        }
        this.hints.value = hints
    }

    fun observeBubbles(): LiveData<Array<Array<WordBubble>>> {
        return bubbles
    }

    fun observeWord(): LiveData<String> {
        return currentWord
    }

    fun observeHints(): LiveData<Array<Hint>> {
        return hints
    }

    fun selectBubble(r: Int, c: Int) {
        val selectedBubble = bubbles.value!![r][c]
        if(lastBubble == null || lastBubble!!.isAdjacentTo(selectedBubble)) {
            lastBubble?.isCurrentlySelected = false
            lastBubble = selectedBubble
            currentWord.value += selectedBubble.letter
            currentWordBubbles.add(selectedBubble)
            selectedBubble.isSelected = true
            selectedBubble.isCurrentlySelected = true
            bubbles.value = bubbles.value
        }
    }

    private fun markHintAsSolved(word: String) {
        val hint = hints.value!!.first { it.word == word }
        hint.isSolved = true
        hints.value = hints.value
    }

    fun submitWord() {
        for (bubble in currentWordBubbles) {
            bubble.isSelected = false
            bubble.isCurrentlySelected = false
        }
        if(level.isWordInSolution(currentWord.value!!)) {
            for (bubble in currentWordBubbles)
                bubble.isUsed = true
            markHintAsSolved(currentWord.value!!)
        }
        resetCurrentWord()
        bubbles.value = bubbles.value
    }

    fun observeIsSolved(): LiveData<Boolean> {
        return isSolved
    }
}
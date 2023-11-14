package com.kproject.brickcar.presentation.screens.game

import androidx.lifecycle.ViewModel
import com.kproject.brickcar.commom.constants.PrefsConstants
import com.kproject.brickcar.data.repository.prefs.PreferenceRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val preferenceRepository: PreferenceRepository
) : ViewModel() {
    val gameState: GameState = GameState()

    init {
        val highScore = getCurrentHighScore()
        gameState.onGameEvent(GameEvent.HighScoreChanged(highScore))
    }

    fun saveHighScore() {
        val currentHighScore = getCurrentHighScore()
        val currentScore = gameState.score
        if (currentScore > currentHighScore) {
            gameState.onGameEvent(GameEvent.HighScoreChanged(currentScore))
            preferenceRepository.savePreference(
                key = PrefsConstants.HighScore,
                value = currentScore
            )
        }
    }

    private fun getCurrentHighScore(): Int {
        return preferenceRepository.getPreference(
            key = PrefsConstants.HighScore,
            defaultValue = 0
        )
    }
}
package com.kproject.brickcar.presentation.screens.game

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.kproject.brickcar.presentation.screens.game.model.Car
import kotlin.random.Random

class GameState {
    var gameStatus: GameStatus by mutableStateOf(GameStatus.Idle)
        private set
    val isRunning: Boolean get() = gameStatus is GameStatus.Running

    var pixelSize: Float by mutableFloatStateOf(0f)
        private set
    var pixelStrokeWidth: Float by mutableFloatStateOf(0f)
        private set
    var boardSize: Size by mutableStateOf(Size.Zero)
        private set
    var velocity: Velocity by mutableStateOf(Velocity.Normal)
        private set
    var score: Int by mutableIntStateOf(0)
        private set
    var highScore: Int by mutableIntStateOf(0)
        private set

    val brickSize: Float get() = pixelSize + pixelStrokeWidth
    private val spaceFromLeft: Float get() = (brickSize * 3) + pixelStrokeWidth
    private val spaceFromRight: Float get() = boardSize.width - (brickSize * 3) + pixelStrokeWidth

    // Total size of the car, including the 3 horizontal and 4 vertical pixels
    private val carSize: Size
        get() = Size(
            width = brickSize * 3,
            height = brickSize * 4
        )

    private val distanceBetweenCars: Float get() = (carSize.height * 2) + (brickSize * 2)

    var playerCar: Car by mutableStateOf(
        Car(
            head = Offset.Zero,
            brickSize = 0f
        )
    )
        private set

    var enemyCars = mutableStateListOf<Car>()

    private var needInitializeItems: Boolean by mutableStateOf(true)

    fun initItems(density: Density) {
        if (needInitializeItems) {
            with(density) {
                pixelSize = 16.dp.toPx()
                pixelStrokeWidth = 4.dp.toPx()
            }
            generateBoardSize()
            initCars()
            needInitializeItems = false
        }
    }

    private fun generateBoardSize() {
        val width = carSize.width * 3
        val height = distanceBetweenCars * 2
        boardSize = Size(width = width, height = height)
    }

    private fun initCars() {
        playerCar = playerCar.copy(
            head = Offset(x = spaceFromLeft, y = boardSize.height - brickSize * 4),
            brickSize = brickSize
        )
        val drawOnLeft = Random.nextBoolean()
        enemyCars.add(
            Car(
                head = Offset(
                    x = if (drawOnLeft) spaceFromLeft else spaceFromRight,
                    y = 0f
                ),
                brickSize = brickSize,
            )
        )
        enemyCars.add(
            Car(
                head = Offset(
                    x = if (!drawOnLeft) spaceFromLeft else spaceFromRight,
                    y = -(distanceBetweenCars)
                ),
                brickSize = brickSize,
            )
        )
        enemyCars.add(
            Car(
                head = Offset(
                    x = if (drawOnLeft) spaceFromLeft else spaceFromRight,
                    y = -(distanceBetweenCars * 2)
                ),
                brickSize = brickSize,
            )
        )
    }

    fun onGameEvent(event: GameEvent) {
        when (event) {
            is GameEvent.PlayOrResume -> {
                gameStatus = GameStatus.Running
            }
            is GameEvent.Pause -> {
                gameStatus = GameStatus.Paused
            }
            is GameEvent.Restart -> {
                restartGame()
            }
            is GameEvent.VelocityChanged -> {
                changeVelocity(event.accelerate)
            }
            is GameEvent.HighScoreChanged -> {
                highScore = event.highScore
            }
            is GameEvent.ChangePlayerCarPosition -> {
                changePlayerCarPosition()
            }
            is GameEvent.MoveEnemyCars -> {
                moveEnemyCars()
            }
        }
    }

    private fun restartGame() {
        score = 0
        enemyCars.clear()
        initCars()
        gameStatus = GameStatus.Running
    }

    private fun changeVelocity(accelerate: Boolean) {
        velocity = if (accelerate) Velocity.Accelerated else Velocity.Normal
    }

    private fun changePlayerCarPosition() {
        if (isRunning) {
            val currentPlayerCarPosition = playerCar.head.x
            val head = if (currentPlayerCarPosition == spaceFromRight) {
                Offset(x = spaceFromLeft, y = boardSize.height - brickSize * 4)
            } else {
                Offset(x = spaceFromRight, y = boardSize.height - brickSize * 4)
            }
            playerCar = playerCar.copy(
                head = head,
                brickSize = brickSize
            )
        }
    }

    private fun moveEnemyCars() {
        val (enemyCar1, enemyCar2, enemyCar3) = enemyCars
        enemyCars[0] = enemyCar1.copy(
            head = Offset(
                x = enemyCar1.head.x,
                y = enemyCar1.head.y.plus(brickSize)
            )
        )
        enemyCars[1] = enemyCar2.copy(
            head = Offset(
                x = enemyCar2.head.x,
                y = enemyCar2.head.y.plus(brickSize)
            )
        )
        enemyCars[2] = enemyCar3.copy(
            head = Offset(
                x = enemyCar3.head.x,
                y = enemyCar3.head.y.plus(brickSize)
            )
        )
        checkCollision()
        resetEnemyCarsPositions()
    }

    private fun checkCollision() {
        enemyCars.forEach { enemyCar ->
            val hasCollision = playerCar.collidesWith(enemyCar)
            if (hasCollision) {
                gameStatus = GameStatus.GameOver
                return
            }
        }
    }

    private fun resetEnemyCarsPositions() {
        val (enemyCar1, enemyCar2, enemyCar3) = enemyCars
        val borderLimit = boardSize.height
        if (enemyCar1.head.y + pixelSize >= borderLimit) {
            score++
            val drawOnLeft = Random.nextBoolean()
            enemyCars[0] = enemyCar1.copy(
                head = Offset(
                    x = if (drawOnLeft) spaceFromLeft else spaceFromRight,
                    y = -distanceBetweenCars
                )
            )
        }

        if (enemyCar2.head.y + pixelSize >= borderLimit) {
            score++
            val drawOnLeft = Random.nextBoolean()
            enemyCars[1] = enemyCar2.copy(
                head = Offset(
                    x = if (!drawOnLeft) spaceFromLeft else spaceFromRight,
                    y = -distanceBetweenCars
                )
            )
        }

        if (enemyCar3.head.y + pixelSize >= borderLimit) {
            score++
            val drawOnLeft = Random.nextBoolean()
            enemyCars[2] = enemyCar3.copy(
                head = Offset(
                    x = if (drawOnLeft) spaceFromLeft else spaceFromRight,
                    y = -distanceBetweenCars
                )
            )
        }
    }
}

sealed class GameStatus {
    data object Idle : GameStatus()
    data object Running : GameStatus()
    data object Paused : GameStatus()
    data object GameOver : GameStatus()
}

enum class Velocity(val value: Long) {
    Normal(150),
    Accelerated(50)
}

sealed class GameEvent {
    data object PlayOrResume : GameEvent()
    data object Pause : GameEvent()
    data object Restart : GameEvent()

    data class VelocityChanged(val accelerate: Boolean) : GameEvent()
    data class HighScoreChanged(val highScore: Int) : GameEvent()

    data object ChangePlayerCarPosition : GameEvent()
    data object MoveEnemyCars : GameEvent()
}
package com.kproject.brickcar.presentation.screens.game

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.kproject.brickcar.R
import com.kproject.brickcar.presentation.screens.components.ComposableLifeCycle
import com.kproject.brickcar.presentation.screens.components.GameDialog
import com.kproject.brickcar.presentation.screens.game.model.Car
import com.kproject.brickcar.presentation.theme.PreviewTheme
import com.kproject.brickcar.presentation.theme.ScreenBackgroundColor
import kotlinx.coroutines.delay

@Composable
fun GameScreen(
    onGameInitializationFinished: () -> Unit
) {
    val gameViewModel: GameViewModel = hiltViewModel()
    val gameState = gameViewModel.gameState

    GameScreenContent(
        gameState = gameState,
        onGameEvent = gameState::onGameEvent,
        onGameInitializationFinished = onGameInitializationFinished
    )

    LaunchedEffect(gameState.gameStatus) {
        when (gameState.gameStatus) {
            is GameStatus.Running -> {
                while (gameState.isRunning) {
                    delay(gameState.velocity.value)
                    gameState.onGameEvent(GameEvent.MoveEnemyCars)
                }
            }
            is GameStatus.GameOver -> {
                gameViewModel.saveHighScore()
            }
            else -> {}
        }
    }

    GameDialog(
        gameStatus = gameState.gameStatus,
        onPlayGame = {
            gameState.onGameEvent(GameEvent.PlayOrResume)
        },
        onResumeGame = {
            gameState.onGameEvent(GameEvent.PlayOrResume)
        },
        onRestartGame = {
            gameState.onGameEvent(GameEvent.Restart)
        }
    )

    ComposableLifeCycle(
        onPause = {
            if (gameState.isRunning) {
                gameState.onGameEvent(GameEvent.Pause)
            }
        }
    )
}

@Composable
private fun GameScreenContent(
    modifier: Modifier = Modifier,
    gameState: GameState,
    onGameEvent: (GameEvent) -> Unit,
    onGameInitializationFinished: () -> Unit
) {
    val density = LocalDensity.current
    LaunchedEffect(Unit) {
        gameState.initItems(density)
        onGameInitializationFinished.invoke()
    }

    val score = stringResource(
        id = R.string.score_and_high_score,
        gameState.score,
        gameState.highScore
    )
    val scoreTextMeasure = rememberTextMeasurer()
    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        onGameEvent.invoke(GameEvent.VelocityChanged(accelerate = true))
                        tryAwaitRelease()
                        onGameEvent.invoke(GameEvent.VelocityChanged(accelerate = false))
                    },
                    onTap = {
                        onGameEvent.invoke(GameEvent.ChangePlayerCarPosition)
                    }
                )
            }
            .background(ScreenBackgroundColor)
            .padding(horizontal = 24.dp)
    ) {
        drawBorders(
            fieldSize = gameState.boardSize,
            pixelSize = gameState.pixelSize,
            pixelStrokeWidth = gameState.pixelStrokeWidth,
            brickSize = gameState.brickSize
        )

        drawScoreText(
            fieldSize = gameState.boardSize,
            brickSize = gameState.brickSize,
            score = score,
            scoreTextMeasure = scoreTextMeasure,
        )

        gameState.enemyCars.forEach { car ->
            drawEnemyCar(
                fieldSize = gameState.boardSize,
                pixelSize = gameState.pixelSize,
                pixelStrokeWidth = gameState.pixelStrokeWidth,
                brickSize = gameState.brickSize,
                car = car
            )
        }

        drawPlayerCar(
            pixelSize = gameState.pixelSize,
            pixelStrokeWidth = gameState.pixelStrokeWidth,
            car = gameState.playerCar
        )
    }
}

private fun DrawScope.drawBorders(
    fieldSize: Size,
    pixelSize: Float,
    pixelStrokeWidth: Float,
    brickSize: Float,
) {
    val color = Color.Black
    val strokeColor = Color(0xFFB8B8B8)

    val maxWidth = fieldSize.width / brickSize
    for (index in 0..maxWidth.toInt()) {
        // Bottom
        drawBrick(
            offset = Offset(
                x = (index * brickSize) + pixelStrokeWidth,
                y = fieldSize.height
            ),
            pixelSize = pixelSize,
            pixelStrokeWidth = pixelStrokeWidth,
            color = color,
            strokeColor = strokeColor
        )
    }

    val maxHeight = fieldSize.height / brickSize
    for (index in 0..maxHeight.toInt()) {
        // Left
        drawBrick(
            offset = Offset(x = pixelStrokeWidth, y = index * brickSize),
            pixelSize = pixelSize,
            pixelStrokeWidth = pixelStrokeWidth,
            color = color,
            strokeColor = strokeColor
        )

        // Right
        drawBrick(
            offset = Offset(
                x = fieldSize.width + pixelStrokeWidth,
                y = index * brickSize
            ),
            pixelSize = pixelSize,
            pixelStrokeWidth = pixelStrokeWidth,
            color = color,
            strokeColor = strokeColor
        )
    }
}

private fun DrawScope.drawScoreText(
    fieldSize: Size,
    brickSize: Float,
    score: String,
    scoreTextMeasure: TextMeasurer,
) {
    drawText(
        textMeasurer = scoreTextMeasure,
        text = score,
        style = TextStyle(
            color = Color(0xFFC4C4C4),
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
        ),
        topLeft = Offset(
            x = (fieldSize.width - (brickSize * 2) + size.width) / 2,
            y = brickSize
        ),
    )
}

private fun DrawScope.drawEnemyCar(
    fieldSize: Size,
    pixelSize: Float,
    pixelStrokeWidth: Float,
    brickSize: Float,
    car: Car
) {
    car.body.forEach { offset ->
        val isOffsetOutOfBounds = offset.y > (fieldSize.height - brickSize)
        if (!isOffsetOutOfBounds) {
            drawBrick(
                offset = offset,
                pixelSize = pixelSize,
                pixelStrokeWidth = pixelStrokeWidth,
                color = Color.Black,
                strokeColor = Color(0xFFCE0101)
            )
        }
    }
}

private fun DrawScope.drawPlayerCar(
    pixelSize: Float,
    pixelStrokeWidth: Float,
    car: Car
) {
    car.body.forEach { offset ->
        drawBrick(
            offset = offset,
            pixelSize = pixelSize,
            pixelStrokeWidth = pixelStrokeWidth,
            color = Color.Black,
            strokeColor = Color(0xFF0212AF)
        )
    }
}

private fun DrawScope.drawBrick(
    offset: Offset,
    pixelSize: Float,
    pixelStrokeWidth: Float,
    color: Color,
    strokeColor: Color,
) {
    drawRect(
        color = color,
        size = Size(pixelSize, pixelSize),
        topLeft = offset,
        style = Fill
    )
    drawRect(
        color = strokeColor,
        size = Size(pixelSize, pixelSize),
        topLeft = offset,
        style = Stroke(width = pixelStrokeWidth)
    )
}

@Preview(name = "NEXUS_5", device = Devices.NEXUS_5)
@Preview(name = "PIXEL_2_XL", device = Devices.PIXEL_2_XL)
@Preview(name = "PIXEL_3A", device = Devices.PIXEL_3A)
@Preview(name = "PIXEL_3A_XL", device = Devices.PIXEL_3A_XL)
@Composable
private fun Preview() {
    PreviewTheme {
        GameScreenContent(
            gameState = GameState(),
            onGameEvent = {},
            onGameInitializationFinished = {}
        )
    }
}
package com.kproject.brickcar.presentation.screens.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kproject.brickcar.R
import com.kproject.brickcar.presentation.screens.game.GameStatus
import com.kproject.brickcar.presentation.theme.PreviewTheme

@Composable
fun GameDialog(
    gameStatus: GameStatus,
    onPlayGame: () -> Unit,
    onResumeGame: () -> Unit,
    onRestartGame: () -> Unit
) {
    when (gameStatus) {
        GameStatus.Idle -> {
            GameDialog(onPlayGame)
        }
        GameStatus.Paused -> {
            GamePausedDialog(onResumeGame)
        }
        GameStatus.GameOver -> {
            GameOverDialog(onRestartGame)
        }
        else -> {}
    }
}

@Composable
private fun GameDialog(onPlayGame: () -> Unit) {
    var showInstructionsDialog by remember { mutableStateOf(false) }
    CustomAlertDialog(
        showDialog = true,
        cancelable = false
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CustomButton(
                title = stringResource(id = R.string.play),
                iconResId = R.drawable.baseline_play_arrow_24,
                onClick = onPlayGame
            )
            Spacer(Modifier.height(12.dp))
            CustomButton(
                title = stringResource(id = R.string.instructions),
                iconResId = R.drawable.baseline_help_24,
                onClick = { showInstructionsDialog = true }
            )
        }
    }

    // InstructionsDialog
    CustomAlertDialog(
        showDialog = showInstructionsDialog,
        onDismiss = { showInstructionsDialog = false }
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.instructions),
                fontSize = 24.sp,
                color = Color(0xFFCECECE),
                textAlign = TextAlign.Center,
                fontFamily = FontFamily(Font(R.font.arcade_classic))
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = stringResource(id = R.string.instructions_guide),
                fontSize = 18.sp,
                color = Color(0xFFCECECE),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun GamePausedDialog(onResumeGame: () -> Unit) {
    CustomAlertDialog(
        showDialog = true,
        cancelable = false
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.game_paused),
                fontSize = 24.sp,
                color = Color(0xFFCECECE),
                textAlign = TextAlign.Center,
                fontFamily = FontFamily(Font(R.font.arcade_classic))
            )

            Spacer(Modifier.height(20.dp))

            CustomButton(
                title = stringResource(id = R.string.resume),
                iconResId = R.drawable.baseline_play_arrow_24,
                onClick = onResumeGame
            )
        }
    }
}

@Composable
private fun GameOverDialog(onRestartGame: () -> Unit) {
    CustomAlertDialog(
        showDialog = true,
        cancelable = false
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.game_over),
                fontSize = 24.sp,
                color = Color(0xFFCECECE),
                textAlign = TextAlign.Center,
                fontFamily = FontFamily(Font(R.font.arcade_classic))
            )

            Spacer(Modifier.height(20.dp))

            CustomButton(
                title = stringResource(id = R.string.restart),
                iconResId = R.drawable.baseline_replay_24,
                onClick = onRestartGame
            )
        }
    }
}

@Composable
private fun CustomButton(
    modifier: Modifier = Modifier,
    title: String,
    @DrawableRes iconResId: Int,
    onClick: () -> Unit,
) {
    val scaleAnimation = remember { Animatable(1f) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        val animationDuration = 300
                        scaleAnimation.animateTo(
                            targetValue = 0.7F,
                            animationSpec = tween(animationDuration),
                        )
                        tryAwaitRelease()
                        scaleAnimation.animateTo(
                            targetValue = 1f,
                            animationSpec = tween(animationDuration),
                        )
                    },
                    onTap = { onClick.invoke() }
                )
            }
            .scale(scale = scaleAnimation.value)
            .background(
                color = Color(0xF00C0C0C),
                shape = CircleShape
            )
            .padding(8.dp)
    ) {
        Icon(
            painter = painterResource(id = iconResId),
            contentDescription = null,
            tint = Color(0xFFCECECE)
        )
        Text(
            text = title.uppercase(),
            fontSize = 22.sp,
            color = Color(0xFFCECECE),
            fontFamily = FontFamily(Font(R.font.arcade_classic)),
            modifier = Modifier.padding(6.dp)
        )
    }
}

@Composable
private fun CustomAlertDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit = {},
    cancelable: Boolean = true,
    content: @Composable () -> Unit
) {
    if (showDialog) {
        Dialog(
            onDismissRequest = { if (cancelable) onDismiss.invoke() },
            content = {
                Column(
                    modifier = Modifier
                        .background(
                            color = Color(0xFF07357C),
                            shape = MaterialTheme.shapes.extraLarge
                        )
                        .padding(24.dp)
                ) {
                    content()
                }
            }
        )
    }
}

@Preview
@Composable
private fun GameDialogPreview() {
    PreviewTheme {
        GameDialog(onPlayGame = {})
    }
}

@Preview
@Composable
private fun GamePausedDialogPreview() {
    PreviewTheme {
        GamePausedDialog(onResumeGame = {})
    }
}

@Preview
@Composable
private fun GameOverDialogPreview() {
    PreviewTheme {
        GameOverDialog(onRestartGame = {})
    }
}
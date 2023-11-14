package com.kproject.brickcar.presentation.screens.game.model

import androidx.compose.ui.geometry.Offset

data class Car(
    val head: Offset,
    val brickSize: Float
) {
    val body: List<Offset> by lazy { generateCarBody() }
    private val formlessBody: List<Offset> by lazy { generateFormlessBody() }
    private val headPositionX = head.x
    private val headPositionY = head.y

    private fun generateCarBody(): List<Offset> {
        return listOf(
            // Head
            head,

            // Arms and neck
            Offset(x = headPositionX - brickSize, y = headPositionY + brickSize),
            Offset(x = headPositionX, y = headPositionY + brickSize),
            Offset(x = headPositionX + brickSize, y = headPositionY + brickSize),

            // Stomach
            Offset(x = headPositionX, y = headPositionY + (brickSize * 2f)),

            // Legs
            Offset(x = headPositionX - brickSize, y = headPositionY + (brickSize * 3)),
            Offset(x = headPositionX + brickSize, y = headPositionY + (brickSize * 3)),
        )
    }

    private fun generateFormlessBody(): List<Offset> {
        return listOf(
            // Head
            Offset(x = headPositionX - brickSize, y = headPositionY),
            head,
            Offset(x = headPositionX + brickSize, y = headPositionY),

            // Arms and neck
            Offset(x = headPositionX - brickSize, y = headPositionY + brickSize),
            Offset(x = headPositionX, y = headPositionY + brickSize),
            Offset(x = headPositionX + brickSize, y = headPositionY + brickSize),

            // Stomach
            Offset(x = headPositionX - brickSize, y = headPositionY + (brickSize * 2f)),
            Offset(x = headPositionX, y = headPositionY + (brickSize * 2f)),
            Offset(x = headPositionX + brickSize, y = headPositionY + (brickSize * 2f)),

            // Legs
            Offset(x = headPositionX - brickSize, y = headPositionY + (brickSize * 3)),
            Offset(x = headPositionX, y = headPositionY + (brickSize * 3)),
            Offset(x = headPositionX + brickSize, y = headPositionY + (brickSize * 3)),
        )
    }

    fun collidesWith(otherCar: Car): Boolean {
        return this.formlessBody.any { playerOffset ->
            otherCar.formlessBody.any { otherOffset ->
                playerOffset == otherOffset
            }
        }
    }
}
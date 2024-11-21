package com.example.enggo

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import com.example.enggo.model.Flashcard
import org.junit.Rule
import org.junit.Test

class FlashcardUITest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testFlashcardToggleUI() {
        // Tạo một flashcard
        val flashcard = Flashcard().apply { Flashcard("Hello", "Xin chào") }

        // Set nội dung giao diện để test
        composeTestRule.setContent {
            flashcard.flashCardView()
        }

        // Kiểm tra hiển thị mặt đầu tiên
        composeTestRule.onNodeWithText("Hello").assertExists()

        // Nhấn vào card để đổi mặt
        composeTestRule.onNodeWithText("Hello").performClick()

        // Kiểm tra hiển thị mặt sau
        composeTestRule.onNodeWithText("Xin chào").assertExists()
    }

    @Test
    fun testTwoSideFlashcardView() {
        // Tạo một flashcard
        val flashcard = Flashcard().apply { Flashcard("Duck", "Vịt") }

        // Set nội dung giao diện để test
        composeTestRule.setContent {
            flashcard.TwoSideFlashCardView()
        }

        // Kiểm tra hiển thị cả hai mặt
        composeTestRule.onNodeWithText("Duck").assertExists()
        composeTestRule.onNodeWithText("Vịt").assertExists()
    }
}
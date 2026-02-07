package net.sidewayssky


import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "cse-chess",
        state = rememberWindowState(size = DpSize(750.dp, 850.dp))
    ) {
        App()
    }
}
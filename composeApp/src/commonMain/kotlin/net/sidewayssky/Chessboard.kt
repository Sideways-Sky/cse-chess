package net.sidewayssky
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.Dp
import kotlin.math.floor

import cse_chess.composeapp.generated.resources.*
import org.jetbrains.compose.resources.DrawableResource

val lightSquare = Color(0xFF2A2828)
val darkSquare = Color(0xFFE0E0E0)
val highlightColor = Color(0x5536B6D3)

@Composable
fun ChessBoard() {
    // Initialize pieces in starting positions
    var game by remember { mutableStateOf(Chess()) }
    var draggedPiece by remember { mutableStateOf<Chess.Piece?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var legalMoves by remember { mutableStateOf<Set<Chess.Position>>(emptySet()) }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(if (game.turn == Chess.Color.WHITE) Color.White else Color.Black),
        contentAlignment = Alignment.Center
    ) {
        // Calculate square size based on available width and height
        val maxBoardSize = minOf(maxWidth, maxHeight)
        val squareSize = maxBoardSize / (boardSize+1)

        // Get density to convert dp to pixels
        val density = LocalDensity.current
        val squareSizePx = with(density) { squareSize.toPx() }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "${if (game.turn == Chess.Color.WHITE) "White" else "Black"}'s Turn",
                style = MaterialTheme.typography.titleLarge,
                color = if (game.turn == Chess.Color.WHITE) Color.Black else Color.White,

                modifier = Modifier.padding(20.dp)
            )

            Box(
                modifier = Modifier
                    .size(squareSize * boardSize)
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                // Find piece at touch position
                                val col = floor(offset.x / squareSizePx).toInt()
                                val row = floor(offset.y / squareSizePx).toInt()
                                val pos = game.Position(row, col)
                                if (pos.isInBounds()) {
                                    val piece = pos.getOccupied()
                                    // Only allow dragging pieces of the current turn's color
                                    if (piece != null && piece.color == game.turn) {
                                        draggedPiece = piece
                                        dragOffset = offset
                                        legalMoves = piece.getLegalMoves()
                                    }
                                }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                dragOffset += dragAmount
                            },
                            onDragEnd = {
                                draggedPiece?.let { piece ->
                                    // Calculate drop position
                                    val newCol = floor(dragOffset.x / squareSizePx).toInt()
                                    val newRow = floor(dragOffset.y / squareSizePx).toInt()
                                    piece.tryMove(game.Position(newRow, newCol))
                                }
                                draggedPiece = null
                                dragOffset = Offset.Zero
                                legalMoves = emptySet()
                            },
                            onDragCancel = {
                                draggedPiece = null
                                dragOffset = Offset.Zero
                                legalMoves = emptySet()
                            }
                        )
                    }
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    for (row in 0 until boardSize) {
                        for (col in 0 until boardSize) {
                            val isLight = (row + col) % 2 == 0
                            val color = if (isLight) lightSquare else darkSquare

                            drawRect(
                                color = color,
                                topLeft = Offset(col * squareSizePx, row * squareSizePx),
                                size = Size(squareSizePx, squareSizePx)
                            )

                            // Highlight legal move squares
                            if (legalMoves.contains(game.Position(row, col))) {
                                drawRect(
                                    color = highlightColor,
                                    topLeft = Offset(col * squareSizePx, row * squareSizePx),
                                    size = Size(squareSizePx, squareSizePx)
                                )
                            }
                        }
                    }
                }

                game.pieces.forEach { piece ->
                    if (piece != draggedPiece) {
                        ChessPieceView(
                            piece = piece,
                            squareSize = squareSize
                        )
                    }
                }

                draggedPiece?.let { piece ->
                    ChessPieceView(
                        piece = piece,
                        squareSize = squareSize,
                        dragOffset = dragOffset
                    )
                }
            }
        }
    }
}

@Composable
fun ChessPieceView(
    piece: Chess.Piece,
    squareSize: Dp,
    dragOffset: Offset? = null
) {
    Box(
        modifier = Modifier
            .offset(
                x = if (dragOffset != null) {
                    with(LocalDensity.current) {
                        (dragOffset.x.toDp() - squareSize / 2)
                    }
                } else {
                    squareSize * piece.position.col
                },
                y = if (dragOffset != null) {
                    with(LocalDensity.current) {
                        (dragOffset.y.toDp() - squareSize / 2)
                    }
                } else {
                    squareSize * piece.position.row
                }
            )
            .size(squareSize)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painterResource(getPieceDrawableRes(piece)),
            contentDescription = "${piece.color} ${piece.type}",
            modifier = Modifier.fillMaxSize(),
        )
    }
}

fun getPieceDrawableRes(piece: Chess.Piece): DrawableResource {
    return when (piece.type) {
        Chess.Type.KING -> if (piece.color == Chess.Color.WHITE ) Res.drawable.wk else Res.drawable.bk
        Chess.Type.QUEEN -> if (piece.color == Chess.Color.WHITE ) Res.drawable.wq else Res.drawable.bq
        Chess.Type.BISHOP -> if (piece.color == Chess.Color.WHITE ) Res.drawable.wb else Res.drawable.bb
        Chess.Type.KNIGHT -> if (piece.color == Chess.Color.WHITE ) Res.drawable.wn else Res.drawable.bn
        Chess.Type.ROOK -> if (piece.color == Chess.Color.WHITE ) Res.drawable.wr else Res.drawable.br
        Chess.Type.PAWN -> if (piece.color == Chess.Color.WHITE ) Res.drawable.wp else Res.drawable.bp
    }
}
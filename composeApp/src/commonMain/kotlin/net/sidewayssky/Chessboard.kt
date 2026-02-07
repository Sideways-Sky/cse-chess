package com.example.cse310chess.net.sidewayssky
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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import org.jetbrains.compose.resources.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.Dp
import kotlin.math.floor

//import cse310chess.composeapp.generated.resources.Res
//import cse310chess.composeapp.generated.resources.chess_king
//import cse310chess.composeapp.generated.resources.chess_queen
//import cse310chess.composeapp.generated.resources.chess_rook
//import cse310chess.composeapp.generated.resources.chess_bishop
//import cse310chess.composeapp.generated.resources.chess_knight
//import cse310chess.composeapp.generated.resources.chess_pawn
import org.jetbrains.compose.resources.DrawableResource

@Composable
fun ChessBoard() {
    val boardSize = 8
    val squareSize = 80.dp

    // Get density to convert dp to pixels
    val density = LocalDensity.current
    val squareSizePx = with(density) { squareSize.toPx() }

    // Initialize pieces in starting positions
    var pieces by remember { mutableStateOf(getInitialPieces()) }
    var draggedPiece by remember { mutableStateOf<ChessPiece?>(null) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var legalMoves by remember { mutableStateOf<Set<Position>>(emptySet()) }
    var currentTurn by remember { mutableStateOf(PieceColor.WHITE) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF383f45))
            .padding(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "Chess Board - ${if (currentTurn == PieceColor.WHITE) "White" else "Black"}'s Turn",
                style = MaterialTheme.typography.titleLarge,
                color = if (currentTurn == PieceColor.WHITE) Color.White else Color.Black,
                modifier = Modifier.padding(bottom = 20.dp)
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

                                if (row in 0..7 && col in 0..7) {
                                    val piece = pieces.find {
                                        it.position.row == row && it.position.col == col
                                    }

                                    // Only allow dragging pieces of the current turn's color
                                    if (piece != null && piece.color == currentTurn) {
                                        draggedPiece = piece
                                        dragOffset = offset
                                        legalMoves = getLegalMoves(piece, pieces)
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
                                    val newPosition = Position(newRow, newCol)

                                    // Only move if the destination is a legal move
                                    if (newRow in 0..7 && newCol in 0..7 && legalMoves.contains(newPosition)) {
                                        // Remove any piece at destination
                                        pieces = pieces.filter {
                                            !(it.position.row == newRow && it.position.col == newCol)
                                        }
                                        // Update dragged piece position and mark as moved
                                        pieces = pieces.map {
                                            if (it == piece) {
                                                it.copy(position = newPosition)
                                            } else {
                                                it
                                            }
                                        }
                                        // Switch turn
                                        currentTurn = if (currentTurn == PieceColor.WHITE) {
                                            PieceColor.BLACK
                                        } else {
                                            PieceColor.WHITE
                                        }
                                    }
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
                    drawChessBoard(boardSize, squareSizePx, legalMoves)
                }

                pieces.forEach { piece ->
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

val lightSquare = Color(0xFFABB2B9)
val darkSquare = Color(0xFF2C3E50)
val highlightColor = Color(0xFFF4D03F)

fun DrawScope.drawChessBoard(boardSize: Int, squareSizePx: Float, legalMoves: Set<Position>) {
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
            if (legalMoves.contains(Position(row, col))) {
                drawRect(
                    color = highlightColor,
                    topLeft = Offset(col * squareSizePx, row * squareSizePx),
                    size = Size(squareSizePx, squareSizePx)
                )
            }
        }
    }
}

@Composable
fun ChessPieceView(
    piece: ChessPiece,
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
            colorFilter = ColorFilter.tint(
                if (piece.color == PieceColor.WHITE) Color.White else Color.Black
            )
        )
    }
}

fun getPieceDrawableRes(piece: ChessPiece): DrawableResource {
    return when (piece.type) {
        PieceType.KING -> Res.drawable.chess_king
        PieceType.QUEEN -> Res.drawable.chess_queen
        PieceType.ROOK -> Res.drawable.chess_rook
        PieceType.BISHOP -> Res.drawable.chess_bishop
        PieceType.KNIGHT -> Res.drawable.chess_knight
        PieceType.PAWN -> Res.drawable.chess_pawn
    }
}


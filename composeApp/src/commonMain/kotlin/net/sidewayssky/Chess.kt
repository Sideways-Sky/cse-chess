package com.example.cse310chess.net.sidewayssky

enum class PieceType {
    KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN
}
enum class PieceColor {
    WHITE, BLACK;
}
data class Position(val row: Int, val col: Int)
data class ChessPiece(
    val type: PieceType,
    val color: PieceColor,
    val position: Position
)
fun getLegalMoves(piece: ChessPiece, allPieces: List<ChessPiece>): Set<Position> {
    val moves = mutableSetOf<Position>()
    val currentPos = piece.position

    when (piece.type) {
        PieceType.PAWN -> {
            val direction = if (piece.color == PieceColor.WHITE) -1 else 1
            val startRow = if (piece.color == PieceColor.WHITE) 6 else 1

            // Move forward one square
            val oneForward = Position(currentPos.row + direction, currentPos.col)
            if (isInBounds(oneForward) && !isOccupied(oneForward, allPieces)) {
                moves.add(oneForward)

                // Move forward two squares from starting position
                if (currentPos.row == startRow) {
                    val twoForward = Position(currentPos.row + 2 * direction, currentPos.col)
                    if (!isOccupied(twoForward, allPieces)) {
                        moves.add(twoForward)
                    }
                }
            }

            // Capture diagonally
            for (colOffset in listOf(-1, 1)) {
                val capturePos = Position(currentPos.row + direction, currentPos.col + colOffset)
                if (isInBounds(capturePos) && isOccupiedByOpponent(capturePos, piece.color, allPieces)) {
                    moves.add(capturePos)
                }
            }
        }

        PieceType.KNIGHT -> {
            val knightMoves = listOf(
                Position(currentPos.row - 2, currentPos.col - 1),
                Position(currentPos.row - 2, currentPos.col + 1),
                Position(currentPos.row - 1, currentPos.col - 2),
                Position(currentPos.row - 1, currentPos.col + 2),
                Position(currentPos.row + 1, currentPos.col - 2),
                Position(currentPos.row + 1, currentPos.col + 2),
                Position(currentPos.row + 2, currentPos.col - 1),
                Position(currentPos.row + 2, currentPos.col + 1)
            )

            knightMoves.forEach { pos ->
                if (isInBounds(pos) && !isOccupiedByAlly(pos, piece.color, allPieces)) {
                    moves.add(pos)
                }
            }
        }

        PieceType.BISHOP -> {
            addDiagonalMoves(currentPos, piece.color, allPieces, moves)
        }

        PieceType.ROOK -> {
            addStraightMoves(currentPos, piece.color, allPieces, moves)
        }

        PieceType.QUEEN -> {
            addStraightMoves(currentPos, piece.color, allPieces, moves)
            addDiagonalMoves(currentPos, piece.color, allPieces, moves)
        }

        PieceType.KING -> {
            for (rowOffset in -1..1) {
                for (colOffset in -1..1) {
                    if (rowOffset == 0 && colOffset == 0) continue
                    val pos = Position(currentPos.row + rowOffset, currentPos.col + colOffset)
                    if (isInBounds(pos) && !isOccupiedByAlly(pos, piece.color, allPieces)) {
                        moves.add(pos)
                    }
                }
            }
        }
    }

    return moves
}

fun addStraightMoves(from: Position, color: PieceColor, allPieces: List<ChessPiece>, moves: MutableSet<Position>) {
    val directions = listOf(
        Pair(-1, 0), // Up
        Pair(1, 0),  // Down
        Pair(0, -1), // Left
        Pair(0, 1)   // Right
    )
    addDirectionalMoves(directions, from, color, allPieces, moves)
}

fun addDiagonalMoves(from: Position, color: PieceColor, allPieces: List<ChessPiece>, moves: MutableSet<Position>) {
    val directions = listOf(
        Pair(-1, -1), // Up-left
        Pair(-1, 1),  // Up-right
        Pair(1, -1),  // Down-left
        Pair(1, 1)    // Down-right
    )
    addDirectionalMoves(directions, from, color, allPieces, moves)
}

fun addDirectionalMoves(directions:  List<Pair<Int, Int>>, from: Position, color: PieceColor, allPieces: List<ChessPiece>, moves: MutableSet<Position>) {
    for ((rowDir, colDir) in directions) {
        var distance = 1
        while (true) {
            val pos = Position(from.row + distance * rowDir, from.col + distance * colDir)
            if (!isInBounds(pos)) break

            if (isOccupied(pos, allPieces)) {
                if (isOccupiedByOpponent(pos, color, allPieces)) {
                    moves.add(pos)
                }
                break
            }

            moves.add(pos)
            distance++
        }
    }
}

fun isInBounds(pos: Position): Boolean {
    return pos.row in 0..7 && pos.col in 0..7
}

fun isOccupied(pos: Position, allPieces: List<ChessPiece>): Boolean {
    return allPieces.any { it.position == pos }
}

fun isOccupiedByAlly(pos: Position, color: PieceColor, allPieces: List<ChessPiece>): Boolean {
    return allPieces.any { it.position == pos && it.color == color }
}

fun isOccupiedByOpponent(pos: Position, color: PieceColor, allPieces: List<ChessPiece>): Boolean {
    return allPieces.any { it.position == pos && it.color != color }
}

fun getInitialPieces(): List<ChessPiece> {
    val pieces = mutableListOf<ChessPiece>()

    // Black pieces (top)
    pieces.add(ChessPiece(PieceType.ROOK, PieceColor.BLACK, Position(0, 0)))
    pieces.add(ChessPiece(PieceType.KNIGHT, PieceColor.BLACK, Position(0, 1)))
    pieces.add(ChessPiece(PieceType.BISHOP, PieceColor.BLACK, Position(0, 2)))
    pieces.add(ChessPiece(PieceType.QUEEN, PieceColor.BLACK, Position(0, 3)))
    pieces.add(ChessPiece(PieceType.KING, PieceColor.BLACK, Position(0, 4)))
    pieces.add(ChessPiece(PieceType.BISHOP, PieceColor.BLACK, Position(0, 5)))
    pieces.add(ChessPiece(PieceType.KNIGHT, PieceColor.BLACK, Position(0, 6)))
    pieces.add(ChessPiece(PieceType.ROOK, PieceColor.BLACK, Position(0, 7)))

    // Black pawns
    for (col in 0..7) {
        pieces.add(ChessPiece(PieceType.PAWN, PieceColor.BLACK, Position(1, col)))
    }

    // White pawns
    for (col in 0..7) {
        pieces.add(ChessPiece(PieceType.PAWN, PieceColor.WHITE, Position(6, col)))
    }

    // White pieces (bottom)
    pieces.add(ChessPiece(PieceType.ROOK, PieceColor.WHITE, Position(7, 0)))
    pieces.add(ChessPiece(PieceType.KNIGHT, PieceColor.WHITE, Position(7, 1)))
    pieces.add(ChessPiece(PieceType.BISHOP, PieceColor.WHITE, Position(7, 2)))
    pieces.add(ChessPiece(PieceType.QUEEN, PieceColor.WHITE, Position(7, 3)))
    pieces.add(ChessPiece(PieceType.KING, PieceColor.WHITE, Position(7, 4)))
    pieces.add(ChessPiece(PieceType.BISHOP, PieceColor.WHITE, Position(7, 5)))
    pieces.add(ChessPiece(PieceType.KNIGHT, PieceColor.WHITE, Position(7, 6)))
    pieces.add(ChessPiece(PieceType.ROOK, PieceColor.WHITE, Position(7, 7)))

    return pieces
}
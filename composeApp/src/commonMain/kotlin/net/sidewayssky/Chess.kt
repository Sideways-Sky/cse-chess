package net.sidewayssky
const val boardSize = 8

class Chess {
    var turn: Color = Color.WHITE
    var pieces: List<Piece> = getInitialPieces()

    inner class Position(val row: Int, val col: Int) {
        fun isInBounds(): Boolean {
            return row in 0..<boardSize && col in 0..<boardSize
        }
        fun isOccupied(): Boolean {
            return pieces.any { it.position == this }
        }
        fun getOccupied(): Piece? {
            return pieces.find { it.position == this }
        }
        fun isOccupiedBy(color: Color): Boolean {
            return pieces.any { it.position == this && it.color == color }
        }
        fun offset(rowOffset: Int, colOffset: Int): Position {
            return Position(row + rowOffset, col+ colOffset)
        }
        override fun equals(other: Any?): Boolean {
            if(other is Position){
                return other.row == row && other.col == col
            }
            return super.equals(other)
        }
        override fun hashCode(): Int {
            var result = row
            result = 31 * result + col
            return result
        }
    }

    enum class Color {
        WHITE, BLACK;

        fun invert(): Color {
            return when (this) {
                WHITE -> BLACK
                BLACK -> WHITE
            }
        }
    }

    enum class Type {
        KING, QUEEN, ROOK, BISHOP, KNIGHT, PAWN
    }

    inner class Piece(
        val type: Type,
        val color: Color,
        var position: Position
    ) {
        fun tryMove(pos: Position){
            val legalMoves = getLegalMoves()
            if (legalMoves.contains(pos)) {
                // Remove any piece at destination
                pieces = pieces.filter {
                    !(it.position.row == pos.row && it.position.col == pos.col)
                }

                position = pos
                turn = turn.invert()
            }
        }
        fun getLegalMoves(): Set<Position> {
            val moves = mutableSetOf<Position>()
            when (type) {
                Type.PAWN -> {
                    val direction = if (color == Color.WHITE) -1 else 1
                    val startRow = if (color == Color.WHITE) (boardSize - 2) else 1

                    // Move forward one square
                    val oneForward = position.offset(direction, 0)
                    if (oneForward.isInBounds() && !oneForward.isOccupied()) {
                        moves.add(oneForward)

                        // Move forward two squares from starting Position
                        if (position.row == startRow) {
                            val twoForward = position.offset(direction*2, 0)
                            if (!twoForward.isOccupied()) {
                                moves.add(twoForward)
                            }
                        }
                    }

                    // Capture diagonally
                    for (colOffset in listOf(-1, 1)) {
                        val capturePos = Position(position.row + direction, position.col + colOffset)
                        if (capturePos.isInBounds() && capturePos.isOccupiedBy(color.invert())) {
                            moves.add(capturePos)
                        }
                    }
                }

                Type.KNIGHT -> {
                    val knightMoves = listOf(
                        position.offset(-2, -1),
                        position.offset(-2, 1),
                        position.offset(-1, -2),
                        position.offset(-1, 2),
                        position.offset(1, -2),
                        position.offset(1, 2),
                        position.offset(2, -1),
                        position.offset(2, 1)
                    )

                    knightMoves.forEach { pos ->
                        if (pos.isInBounds() && !pos.isOccupiedBy(color)) {
                            moves.add(pos)
                        }
                    }
                }

                Type.BISHOP -> {
                    addDiagonalMoves(moves)
                }

                Type.ROOK -> {
                    addStraightMoves(moves)
                }

                Type.QUEEN -> {
                    addDiagonalMoves(moves)
                    addStraightMoves(moves)
                }

                Type.KING -> {
                    for (rowOffset in -1..1) {
                        for (colOffset in -1..1) {
                            if (rowOffset == 0 && colOffset == 0) continue
                            val pos = position.offset(rowOffset, colOffset)
                            if (pos.isInBounds() && !pos.isOccupiedBy(color)) {
                                moves.add(pos)
                            }
                        }
                    }
                }
            }
            return moves
        }
        private fun addStraightMoves(moves: MutableSet<Position>) {
            val directions = listOf(
                Pair(-1, 0), // Up
                Pair(1, 0),  // Down
                Pair(0, -1), // Left
                Pair(0, 1)   // Right
            )
            addDirectionalMoves(directions, moves)
        }
        private fun addDiagonalMoves(moves: MutableSet<Position>) {
            val directions = listOf(
                Pair(-1, -1), // Up-left
                Pair(-1, 1),  // Up-right
                Pair(1, -1),  // Down-left
                Pair(1, 1)    // Down-right
            )
            addDirectionalMoves(directions, moves)
        }
        private fun addDirectionalMoves(directions:  List<Pair<Int, Int>>, moves: MutableSet<Position>) {
            for ((rowDir, colDir) in directions) {
                var distance = 1
                while (true) {
                    val pos = position.offset(distance * rowDir, distance * colDir)
                    if (!pos.isInBounds()) break

                    if (pos.isOccupied()) {
                        if (pos.isOccupiedBy(color.invert())) {
                            moves.add(pos)
                        }
                        break
                    }

                    moves.add(pos)
                    distance++
                }
            }
        }
    }

    fun getInitialPieces(): List<Piece> {
        val pieces = mutableListOf<Piece>()

        // Black pieces (top)
        pieces.add(Piece(Type.ROOK, Color.BLACK, Position(0, 0)))
        pieces.add(Piece(Type.KNIGHT, Color.BLACK, Position(0, 1)))
        pieces.add(Piece(Type.BISHOP, Color.BLACK, Position(0, 2)))
        pieces.add(Piece(Type.QUEEN, Color.BLACK, Position(0, 3)))
        pieces.add(Piece(Type.KING, Color.BLACK, Position(0, 4)))
        pieces.add(Piece(Type.BISHOP, Color.BLACK, Position(0, 5)))
        pieces.add(Piece(Type.KNIGHT, Color.BLACK, Position(0, 6)))
        pieces.add(Piece(Type.ROOK, Color.BLACK, Position(0, 7)))

        // Black pawns
        for (col in 0..7) {
            pieces.add(Piece(Type.PAWN, Color.BLACK, Position(1, col)))
        }

        // White pawns
        for (col in 0..7) {
            pieces.add(Piece(Type.PAWN, Color.WHITE, Position(6, col)))
        }

        // White pieces (bottom)
        pieces.add(Piece(Type.ROOK, Color.WHITE, Position(7, 0)))
        pieces.add(Piece(Type.KNIGHT, Color.WHITE, Position(7, 1)))
        pieces.add(Piece(Type.BISHOP, Color.WHITE, Position(7, 2)))
        pieces.add(Piece(Type.QUEEN, Color.WHITE, Position(7, 3)))
        pieces.add(Piece(Type.KING, Color.WHITE, Position(7, 4)))
        pieces.add(Piece(Type.BISHOP, Color.WHITE, Position(7, 5)))
        pieces.add(Piece(Type.KNIGHT, Color.WHITE, Position(7, 6)))
        pieces.add(Piece(Type.ROOK, Color.WHITE, Position(7, 7)))

        return pieces
    }
}
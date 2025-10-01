package chess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    private final ChessPiece[][] board = new ChessPiece[8][8];

    public ChessBoard() {
    }

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        board[8-position.getRow()][position.getColumn() - 1] = piece;
    }

    public void movePiece(ChessMove move){
        ChessPosition startPosition = move.getStartPosition();
        ChessPiece piece = getPiece(startPosition);
        if (move.getPromotionPiece() != null){
            piece = new ChessPiece(piece.pieceColor(), move.getPromotionPiece());
        }
        addPiece(move.getEndPosition(), piece);
        board[8-startPosition.getRow()][startPosition.getColumn() - 1] = null;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return board[8 - position.getRow()][position.getColumn() - 1];
    }

    public ChessPosition getPosition(ChessPiece piece) {
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                if (board[i][j].equals(piece)){
                   return new ChessPosition(8-i, j-1);
                }
            }
        }
        throw new IllegalArgumentException("Piece is not on board");
    }

    public Collection<ChessPosition> threatPositions(ChessPosition position, ChessGame.TeamColor teamColor){
        Collection<ChessPosition> threatPieces = new ArrayList<>();
        ChessPiece nextPiece;
        for (int i = 0; i < 8; i++){
            for (int j = 0; j < 8; j++){
                nextPiece = board[i][j];
                if (nextPiece != null && nextPiece.pieceColor() != teamColor){
                    ChessPosition piecePosition = new ChessPosition(8-i, j-1);
                    Collection<ChessMove> nextPieceMoves = nextPiece.pieceMoves(this, piecePosition);
                    for (ChessMove move:nextPieceMoves){
                        if (move.getEndPosition().equals(position)){
                            threatPieces.add(piecePosition);
                        }
                    }
                }
            }
        }
        return threatPieces;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        final ChessPiece.PieceType[] orderOfPieces = {ChessPiece.PieceType.ROOK, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.QUEEN, ChessPiece.PieceType.KING, ChessPiece.PieceType.BISHOP, ChessPiece.PieceType.KNIGHT, ChessPiece.PieceType.ROOK};
        for(int i = 0; i < 8; i++){
            board[0][i] = new ChessPiece(ChessGame.TeamColor.BLACK, orderOfPieces[i]);
        }
        for(int i = 0; i < 8; i++){
            board[1][i] = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN);
        }
        for(int i = 0; i < 8; i++){
            board[6][i] = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        }
        for(int i = 0; i < 8; i++){
            board[7][i] = new ChessPiece(ChessGame.TeamColor.WHITE, orderOfPieces[i]);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessBoard that = (ChessBoard) o;
        return Objects.deepEquals(board, that.board);
        // I looked this one up, deepEquals checks each individual value in the embedded arrays
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(board);
    }

    @Override
    public String toString() {
        String boardString = "";
        for(ChessPiece[] rows:board){
            for(ChessPiece piece: rows) {
                boardString += piece.toChar();
            }
            boardString += "\n";
        }
        return boardString;
    }

    //TODO: Run test to make sure toString works
}

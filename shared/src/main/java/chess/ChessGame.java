package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private ChessBoard board = new ChessBoard();
    private TeamColor teamTurn = TeamColor.WHITE;

    public ChessGame() {

    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    public TeamColor notColor(TeamColor color){
        if (color == TeamColor.WHITE){
            return TeamColor.BLACK;
        }
        return TeamColor.WHITE;
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null) {
            return new ArrayList<>();
        }
        return piece.pieceMoves(board, startPosition);
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        if (validMoves(startPosition).contains(move)){
            board.movePiece(move);
        }
        else{
            throw new InvalidMoveException("Invalid move");
        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = board.getPosition(new ChessPiece(teamColor, ChessPiece.PieceType.KING));
        Collection<ChessPosition> threatPositions = board.positionsOfThreatPieces(kingPosition, teamColor);
        return !threatPositions.isEmpty();
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        ChessPosition kingPosition = board.getPosition(new ChessPiece(teamColor, ChessPiece.PieceType.KING));
        Collection<ChessPosition> threatPositions = board.positionsOfThreatPieces(kingPosition, teamColor);
        // If there is only one threat piece, you can capture or block the piece
        // Otherwise move the king
        if (threatPositions.size() == 1){
            // The enemy of my enemy is my friend
            ChessPosition threatPosition = threatPositions.iterator().next();
            Collection<ChessPosition> saviorPieces = board.positionsOfThreatPieces(threatPosition, notColor(teamColor));
            if (!saviorPieces.isEmpty()){
                return false;
            }
            ChessPiece threatPiece = board.getPiece(threatPosition);
            ChessPiece.PieceType threatType = threatPiece.getPieceType();
            if(threatType == ChessPiece.PieceType.QUEEN || threatType == ChessPiece.PieceType.BISHOP || threatType == ChessPiece.PieceType.ROOK){
                // Find the direction of the king from the threatPiece by comparing positions
                // Iteratively check every square in the way of the king to see if a piece can move to it
            }
        }
        // Get possible moves for King
        // Check the King's current space and all spaces that the king could move for threat positions
        // If there is a space that the king can move to with no threatPositions, King can get out of check
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ChessGame chessGame)) {
            return false;
        }
        return board.equals(chessGame.board) && teamTurn == chessGame.teamTurn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(board.hashCode(), teamTurn);
    }
}

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
    private GameState state = GameState.ONGOING;

    public ChessGame() {
        board.resetBoard();
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

    public GameState getState(){
        return state;
    }

    public void resign(){
        state = GameState.RESIGNED;
    }
    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK,
        BOTH
    }

    public enum GameState {
        ONGOING,
        RESIGNED,
        CHECK,
        CHECKMATE,
        STALEMATE
    }

    public TeamColor notColor(TeamColor color){
        if (color == TeamColor.WHITE){
            return TeamColor.BLACK;
        }
        return TeamColor.WHITE;
    }

    private boolean isThreat(ChessPosition threatPosition, ChessPosition endPosition, TeamColor teamColor){
        ChessPiece threatPiece = board.getPiece(threatPosition);
        if (threatPiece != null && threatPiece.pieceColor() != teamColor){
            Collection<ChessMove> nextPieceMoves = threatPiece.pieceMoves(board, threatPosition);
            for (ChessMove move:nextPieceMoves){
                if (move.getEndPosition().equals(endPosition)){
                    return true;
                }
            }
        }
        return false;
    }

    private Collection<ChessPosition> positionsOfThreatPieces(ChessPosition position, ChessGame.TeamColor teamColor){
        Collection<ChessPosition> threatPieces = new ArrayList<>();
        ChessPosition potentialThreat;
        for (int i = 1; i < 9; i++){
            for (int j = 1; j < 9; j++){
                potentialThreat = new ChessPosition(i, j);
                if (isThreat(potentialThreat, position, teamColor)) {
                    threatPieces.add(potentialThreat);
                }
            }
        }
        return threatPieces;
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
        Collection<ChessMove> possibleMoves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();
        ChessPiece capturedPiece;
        for (ChessMove move: possibleMoves){
            capturedPiece = board.getPiece(move.getEndPosition());
            board.movePiece(move);
            if (!isInCheck(piece.getTeamColor())){
                validMoves.add(move);
            }
            board.undoMove(move, capturedPiece);
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPosition startPosition = move.getStartPosition();
        ChessPiece piece = board.getPiece(startPosition);
        if (piece == null){
            throw new InvalidMoveException("Error: The starting square of that move is empty");
        }
        else if(state == GameState.RESIGNED || state == GameState.STALEMATE || state == GameState.CHECKMATE){
            throw new InvalidMoveException("Error: The game is over");
        }
        else if(piece.pieceColor() != teamTurn){
            throw new InvalidMoveException("Error: It is not your turn");
        }
        else if (validMoves(startPosition).contains(move)){
            board.movePiece(move);
            setTeamTurn(notColor(teamTurn));
        }
        else{
            throw new InvalidMoveException("Error: Invalid move");
        }
        if(isInCheckmate(teamTurn)){
            state = GameState.CHECKMATE;
        } else if(isInCheck(teamTurn)){
            state = GameState.CHECK;
        } else if (isInStalemate(teamTurn)){
            state = GameState.STALEMATE;
        } else{
            state = GameState.ONGOING;
        }
    }

    public void makeMove(ChessMove move, TeamColor color) throws InvalidMoveException{
        ChessPosition startPosition = move.getStartPosition();
        ChessPiece piece = board.getPiece(startPosition);
        if (piece != null && color != TeamColor.BOTH && piece.pieceColor() != color){
            throw new InvalidMoveException("Error: That piece belongs to the other player");
        }
        makeMove(move);
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = board.getPosition(new ChessPiece(teamColor, ChessPiece.PieceType.KING));
        Collection<ChessPosition> threatPositions = positionsOfThreatPieces(kingPosition, teamColor);
        return !threatPositions.isEmpty();
    }

    private boolean movesPreventCheck(Collection<ChessMove> moves, TeamColor teamColor){
        ChessPiece capturedPiece;
        for (ChessMove move: moves){
            capturedPiece = board.getPiece(move.getEndPosition());
            board.movePiece(move);
            if (!isInCheck(teamColor)){
                return true;
            }
            board.undoMove(move, capturedPiece);
        }
        return false;
    }

    private boolean movesPreventCheck(Collection<ChessPosition> startPositions, ChessPosition endPosition, TeamColor teamColor){
        ChessPiece capturedPiece;
        ChessMove move;
        for (ChessPosition startPosition: startPositions){
            capturedPiece = board.getPiece(endPosition);
            move = new ChessMove(startPosition, endPosition, null);
            board.movePiece(move);
            if (!isInCheck(teamColor)){
                return true;
            }
            board.undoMove(move, capturedPiece);
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        ChessPosition kingPosition = board.getPosition(new ChessPiece(teamColor, ChessPiece.PieceType.KING));
        Collection<ChessPosition> threatPositions = positionsOfThreatPieces(kingPosition, teamColor);
        if (threatPositions.isEmpty()){
            return false;
        }
        // Move the king
        ChessPiece kingPiece = board.getPiece(kingPosition);
        Collection<ChessMove> kingMoves = kingPiece.pieceMoves(board, kingPosition);
        if(movesPreventCheck(kingMoves, teamColor)){
            return false;
        }
        // If there is only one piece threatening the king, you can capture or block the piece
        if (threatPositions.size() == 1){
            // CAPTURE
            ChessPosition threatPosition = threatPositions.iterator().next();
            // An enemy to my enemy is my friend
            Collection<ChessPosition> saviorPieces = positionsOfThreatPieces(threatPosition, notColor(teamColor));
            if (movesPreventCheck(saviorPieces, threatPosition, teamColor)){
                return false;
            }
            // BLOCK
            ChessPiece threatPiece = board.getPiece(threatPosition);
            ChessPiece.PieceType threatType = threatPiece.getPieceType();
            if(threatType == ChessPiece.PieceType.QUEEN || threatType == ChessPiece.PieceType.BISHOP || threatType == ChessPiece.PieceType.ROOK){
                ChessPosition.ChessVector threatVector = ChessPosition.getVector(kingPosition, threatPosition);
                ChessPosition blockPosition = kingPosition.getNextPosition(threatVector.rowChange(), threatVector.colChange());
                for (int i = 0; i < threatVector.magnitude() - 1; i++){
                    saviorPieces = positionsOfThreatPieces(blockPosition, notColor(teamColor));
                    if (movesPreventCheck(saviorPieces, blockPosition, teamColor)){
                        return false;
                    }
                    blockPosition = blockPosition.getNextPosition(threatVector.rowChange(), threatVector.colChange());
                }
            }
        }
        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)){
            return false;
        }

        ChessPosition nextPosition;
        ChessPiece nextPiece;
        for (int i = 1; i < 9; i++){
            for (int j = 1; j < 9; j++) {
                nextPosition = new ChessPosition(i, j);
                nextPiece = board.getPiece(nextPosition);
                if(nextPiece != null && nextPiece.pieceColor() == teamColor){
                    if (!validMoves(nextPosition).isEmpty()){
                        return false;
                    }
                }
            }
        }
        return true;
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

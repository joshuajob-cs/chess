package ui;

import chess.ChessBoard;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

public class BoardUI{
    SquareUI[][] ui = new SquareUI[10][10];

    public BoardUI(ChessBoard board){
        String[] letters = {" a ", " b ", " c ", " d ", " e ", " f ", " g ", " h "};
        for(int i = 0; i < 8; i++){
            ui[0][i+1] = new SquareUI(null, null, letters[i]);
            ui[9][i+1] = new SquareUI(null, null, letters[i]);
        }
        String[] numbers = {" 8 ", " 7 ", " 6 ", " 5 ", " 4 ", " 3 ", " 2 ", " 1 "};
        for(int i = 0; i < 8; i++){
            ui[i+1][0] = new SquareUI(null, null, numbers[i]);
            ui[i+1][9] = new SquareUI(null, null, numbers[i]);
        }
        for(int i = 0; i < 8; i++){
            for(int j = 0; j < 8; j++){
                ui[i + 1][j + 1] = readSquare(board, i, j);
            }
        }
    }

    private SquareUI readSquare(ChessBoard board, int i, int j){
        ChessPiece piece = board.getPiece(new ChessPosition(8-i, j+1));
        String bg;
        boolean isWhite = (i%2 == 1) == (j%2 == 1);
        if (isWhite){
            bg = SET_BG_COLOR_WHITE;
        }
        else{
            bg = SET_BG_COLOR_BLACK;
        }
        if (piece == null){
            return new SquareUI(bg, null, null);
        }
        String textColor;
        char pieceChar = piece.toChar();
        if (Character.isUpperCase(pieceChar)){
            textColor = SET_TEXT_COLOR_RED;
        }
        else{
            textColor = SET_TEXT_COLOR_BLUE;
        }
        String text = " " + Character.toUpperCase(pieceChar) + " ";
        return new SquareUI(bg, textColor, text);
    }

    public String[][] get(){
        String[][] ret = new String[10][10];
        for(int i = 0; i < 10; i++){
            for(int j = 0; j < 10; j++){
                if (ui[i][j] == null){
                    ret[i][j] = SET_BG_COLOR_LIGHT_GREY + SET_TEXT_COLOR_BLACK + "   ";
                }
                else {
                    ret[i][j] = "";
                    ret[i][j] += (ui[i][j].bg() == null) ? SET_BG_COLOR_LIGHT_GREY : ui[i][j].bg();
                    ret[i][j] += (ui[i][j].textColor() == null) ? SET_TEXT_COLOR_BLACK : ui[i][j].textColor();
                    ret[i][j] += (ui[i][j].text() == null) ? "   " : ui[i][j].text();
                }
            }
        }
        return ret;
    }

    private record SquareUI(String bg, String textColor, String text){}
}

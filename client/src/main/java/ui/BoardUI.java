package ui;

import chess.*;

import java.util.ArrayList;

import static ui.EscapeSequences.*;

class BoardUI{
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

    private void setSquare(ChessPosition pos, String bg){
        SquareUI oldSquare = ui[7 - pos.getRow()][pos.getColumn()];
        ui[7 - pos.getRow()][pos.getColumn()] = new SquareUI(bg, oldSquare.textColor(), oldSquare.text());
    }

    public static void printBoard(ChessBoard board, ChessGame.TeamColor color){
        print(new BoardUI(board), color);
    }

    private static void print(BoardUI board, ChessGame.TeamColor color){
        String[][] printable = board.get();
        if (color == ChessGame.TeamColor.WHITE) {
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    System.out.print(printable[i][j]);
                }
                System.out.print(RESET_BG_COLOR + RESET_TEXT_COLOR + "\n");
            }
        } else{
            for (int i = 9; i >= 0; i--) {
                for (int j = 9; j >= 0; j--) {
                    System.out.print(printable[i][j]);
                }
                System.out.print(RESET_BG_COLOR + RESET_TEXT_COLOR + "\n");
            }
        }
    }

    public static void highlight(ChessBoard board, ChessGame.TeamColor color, ArrayList<ChessMove> squares){
        var boardUI = new BoardUI(board);
        for(ChessMove square: squares){
            boardUI.setSquare(square.getEndPosition(), SET_BG_COLOR_YELLOW);
        }
        print(boardUI, color);
    }

    private record SquareUI(String bg, String textColor, String text){}
}

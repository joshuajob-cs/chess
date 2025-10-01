package chess;

import java.util.Objects;

import static java.lang.Math.abs;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public record ChessPosition (int row, int col){
    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        if (row > 0 && row < 9 &&
                col > 0 && col < 9){
            return row;
        }
        else{
            return -1;
        }
    }

    /**
     * @return which column this position is in
     * 1 codes for the left row
     */
    public int getColumn() {
        if (row > 0 && row < 9 &&
                col > 0 && col < 9) {
            return col;
        }
        else{
            return -1;
        }
    }

    public ChessPosition getNextPosition(int rowChange, int colChange){
        return new ChessPosition(row + rowChange, col + colChange);
    }

    public record ChessVector(int rowChange, int colChange, int magnitude){}

    public static ChessVector getVector(ChessPosition fromPosition, ChessPosition toPosition){
        int rowChange = toPosition.getRow() - fromPosition.getRow();
        int colChange = toPosition.getColumn() - fromPosition.getColumn();
        int amplitude;
        if (abs(rowChange) == abs(colChange)){
            amplitude = abs(rowChange);
        }
        else if(rowChange == 0){
            amplitude = abs(colChange);
        }
        else if(colChange == 0){
            amplitude = abs(rowChange);
        }
        else{
            throw new IllegalArgumentException("Can not solve for vectors if vector's direction would contain float value");
        }
        return new ChessVector(rowChange/amplitude, colChange/amplitude, amplitude);
    }

    @Override
    public String toString() {
        return "(" + getRow() +
                ", " + getColumn() +
                ')';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPosition that = (ChessPosition) o;
        // This was the only line I was confused by, so I looked it up
        // If the function receives a subclass of ChessPosition it is cast as a ChessPosition
        return getRow() == that.getRow() && getColumn() == that.getColumn();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRow(), getColumn());
    }
}

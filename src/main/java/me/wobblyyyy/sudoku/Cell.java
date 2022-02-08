package me.wobblyyyy.sudoku;

import java.util.function.ObjLongConsumer;

public class Cell {
    private final int x;
    private final int y;
    private final int group;
    private int value;

    public Cell(int x,
                int y,
                int value) {
        if (x > Board.BOARD_SIZE) x = Board.BOARD_SIZE;
        if (x < 1) x = 1;
        if (y > Board.BOARD_SIZE) y = Board.BOARD_SIZE;
        if (y < 1) y = 1;

        this.x = x;
        this.y = y;
        this.value = value;

        boolean xl4 = x < 4;
        boolean yl4 = y < 4;
        boolean xl7 = x < 7;
        boolean yl7 = y < 7;

        if (xl4 && yl4) group = 1;
        else if (xl7 && yl4) group = 2;
        else if (yl4) group = 3;
        else if (xl4 && yl7) group = 4;
        else if (xl7 && yl7) group = 5;
        else if (xl7) group = 6;
        else if (xl4) group = 7;
        else if (xl7) group = 8;
        else group = 9;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int value() {
        return value;
    }

    public void value(int value) {
        this.value = value;
    }

    public int group() {
        return group;
    }
    
    public boolean hasValue() {
        return value != 0;
    }

    @Override
    public String toString() {
        if (value != 0) {
            return " " + String.valueOf(value);
        } else {
            return "  ";
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Cell) {
            Cell c = (Cell) obj;

            boolean sameX = this.x == c.x;
            boolean sameY = this.y == c.y;

            return sameX && sameY;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return (x * 1_000_000) + (y * 100);
    }
}

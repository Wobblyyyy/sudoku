package me.wobblyyyy.sudoku;

public class Render {
    private final Board board;

    public Render(Board board) {
        this.board = board;
    }

    public void clearScreen() {
        // System.out.print("\033[H\033[2J]]");
        System.out.print("\033[H\033[2J");
    }

    public void render() {
        System.out.println(board);
    }
}

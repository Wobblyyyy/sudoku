package me.wobblyyyy.sudoku;

import java.util.Scanner;

public class Sudoku {
    private final Board board;
    private final Render render;
    private final Scanner scanner;
    private boolean shouldQuit = false;

    public Sudoku() {
        this.board = Board.newBoard();
        this.render = new Render(board);
        this.scanner = new Scanner(System.in);
    }

    public boolean isDone() {
        return false;
    }

    public void loop() {

        if (scanner.hasNext()) {
            try {
                String line = scanner.nextLine();
                System.out.printf("'%s'", line);
                render.clearScreen();

                for (char c : line.toCharArray()) {
                    switch (c) {
                        case '0':
                        case '1':
                        case '2':
                        case '3':
                        case '4':
                        case '5':
                        case '6':
                        case '7':
                        case '8':
                        case '9':
                            board.tryPut(board.cursorCell(), Integer.parseInt(String.valueOf(c)));
                            break;
                        case ' ':
                            board.tryPut(board.cursorCell(), Integer.parseInt(String.valueOf(0)));
                            break;
                        case 'W':
                        case 'K':
                        case 'w':
                        case 'k':
                            board.move(Board.Direction.LEFT);
                            break;
                        case 'A':
                        case 'H':
                        case 'a':
                        case 'h':
                            board.move(Board.Direction.UP);
                            break;
                        case 'S':
                        case 'J':
                        case 's':
                        case 'j':
                            board.move(Board.Direction.RIGHT);
                            break;
                        case 'D':
                        case 'L':
                        case 'd':
                        case 'l':
                            board.move(Board.Direction.DOWN);
                            break;
                        case 'q':
                            shouldQuit = true;
                            break;
                        default:
                            System.out.printf("Invalid input '%s'%n", line);
                            break;
                    }
                }

                render.render();
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("Invalid input!");
                shouldQuit = true;
            }
        }
    }

    public static void main(String[] args) {
        Sudoku game = new Sudoku();

        game.board.autoSolve();
        game.board.removeRandom();

        game.render.clearScreen();
        game.render.render();

        while (!game.isDone() && !game.shouldQuit)
            game.loop();
    }
}

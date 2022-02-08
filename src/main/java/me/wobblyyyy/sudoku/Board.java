package me.wobblyyyy.sudoku;

import me.wobblyyyy.pathfinder2.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class Board extends ArrayList<Cell> {
    public static final int BOARD_SIZE = 9;
    private static final String TEMPLATE = """
+-----------+-----------+-----------+  **********************************
| %s %s %s  | %s %s %s  | %s %s %s  |  **           Controls           **
| %s %s %s  | %s %s %s  | %s %s %s  |  **********************************
| %s %s %s  | %s %s %s  | %s %s %s  |  ** SPACE             clear cell **
+-----------+-----------+-----------+  ** W, K, or UP        cursor up **
| %s %s %s  | %s %s %s  | %s %s %s  |  ** A, H, or LEFT    cursor left **
| %s %s %s  | %s %s %s  | %s %s %s  |  ** S, J, or DOWN    cursor down **
| %s %s %s  | %s %s %s  | %s %s %s  |  ** D, L, or RIGHT  cursor right **
+-----------+-----------+-----------+  ** 1-9            insert number **
| %s %s %s  | %s %s %s  | %s %s %s  |  ** Q                  quit game **
| %s %s %s  | %s %s %s  | %s %s %s  |  **                              **
| %s %s %s  | %s %s %s  | %s %s %s  |  **                              **
+-----------+-----------+-----------+  ********************************** 
                                       
Cursor Position X: %s
Cursor Position Y: %s
Cursor Group:      %s
Cursor Value:      %s
Message:           %s
"""; // thank god for vim macros

    private Cell cursorCell;
    private String message = "";

    public Board(List<Cell> list) {
        super(list);
    }

    public static Board newBoard() {
        List<Cell> cells = new ArrayList<>(BOARD_SIZE * BOARD_SIZE);

        for (int x = 1; x <= BOARD_SIZE; x++) {
            for (int y = 1; y <= BOARD_SIZE; y++) {
                cells.add(new Cell(x, y, 0));
            }
        }

        Board board = new Board(cells);
        board.cursorCell = cells.get(0);

        return board;
    }

    public List<Cell> where(Predicate<Cell> predicate) {
        List<Cell> filtered = new ArrayList<>(BOARD_SIZE);

        for (Cell cell : this)
            if (predicate.test(cell)) 
                filtered.add(cell);

        return filtered;
    }

    private static boolean isValid(Iterable<Cell> cells) {
        List<Integer> values = new ArrayList<>(BOARD_SIZE);

        for (Cell cell : cells) {
            int value = cell.value();
            if (values.contains(value) && value != 0) {
                return false;
            } else {
                values.add(value);
            }
        }

        return true;
    }

    public List<Cell> whereX(int x) {
        return where((cell) -> cell.x() == x);
    }

    public List<Cell> whereY(int y) {
        return where((cell) -> cell.y() == y);
    }

    public List<Cell> whereGroup(int group) {
        return where((cell) -> cell.group() == group);
    }

    public boolean wouldNewValueBeValid(Cell cell,
                                        int newValue) {
        Cell c = where((p) -> p.x() == cell.x() && p.y() == cell.y()).get(0);
        int oldValue = c.value();
        c.value(newValue);
        List<Cell> listX = whereX(cell.x());
        List<Cell> listY = whereY(cell.y());
        List<Cell> listGroup = whereGroup(cell.group());
        
        boolean validX = isValid(listX);
        boolean validY = isValid(listY);
        boolean validGroup = isValid(listGroup);
        
        if (validX && validY && validGroup) {
            message = StringUtils.format(
                    "set cell (%s, %s) to have value %s",
                    c.x(),
                    c.y(),
                    c.value()
            );
            return true;
        } else {
            c.value(oldValue);
            message = StringUtils.format(
                    "could not set cell (%s, %s) because INVALID %s",
                    c.x(),
                    c.y(),
                    validX
                        ? validY
                            ? "COLUMN"
                            : "GROUP"
                        : "ROW"
            );
            return false;
        }
    }

    private boolean solve(int[][] board) {
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int column = 0; column < BOARD_SIZE; column++) {
                if (board[row][column] == 0) {
                    for (int k = 1; k <= 9; k++) {
                        board[row][column] = k;
                        if (isValid(board, row, column) && solve(board)) {
                            return true;
                        }
                        board[row][column] = 0;
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isValid(int[][] board, int row, int column) {
        return (rowConstraint(board, row)
          && columnConstraint(board, column) 
          && subsectionConstraint(board, row, column));
    }

    private boolean rowConstraint(int[][] board, int row) {
        boolean[] constraint = new boolean[BOARD_SIZE];
        return IntStream.range(0, BOARD_SIZE)
          .allMatch(column -> checkConstraint(board, row, constraint, column));
    }

    private boolean columnConstraint(int[][] board, int column) {
        boolean[] constraint = new boolean[BOARD_SIZE];
        return IntStream.range(0, BOARD_SIZE)
          .allMatch(row -> checkConstraint(board, row, constraint, column));
    }

    private boolean subsectionConstraint(int[][] board, int row, int column) {
        boolean[] constraint = new boolean[BOARD_SIZE];
        int subsectionRowStart = (row / 3) * 3;
        int subsectionRowEnd = subsectionRowStart + 3;

        int subsectionColumnStart = (column / 3) * 3;
        int subsectionColumnEnd = subsectionColumnStart + 3;

        for (int r = subsectionRowStart; r < subsectionRowEnd; r++) {
            for (int c = subsectionColumnStart; c < subsectionColumnEnd; c++) {
                if (!checkConstraint(board, r, constraint, c)) return false;
            }
        }
        return true;
    }

    public boolean checkConstraint(int[][] board, int row, boolean[] constraint, int column) {
        if (board[row][column] != 0) {
            if (!constraint[board[row][column] - 1]) {
                constraint[board[row][column] - 1] = true;
            } else {
                return false;
            }
        }
        return true;
    }

    public void autoSolve() {
        int[][] board = new int[9][9];

        for (Cell cell : this)
            board[cell.x() - 1][cell.y() - 1] = cell.value();

        solve(board);
        clear();

        for (int x = 0; x < board.length; x++) {
            for (int y = 0; y < board.length; y++) {
                add(new Cell(x + 1, y + 1, board[x][y]));
            }
        }
    }

    public void removeRandom() {
        for (Cell cell : this) {
            if (Math.random() > 0.5) cell.value(0);
        }
    }

    @Override
    public String toString() {
        Object[] params = new Object[(BOARD_SIZE * BOARD_SIZE) + 5];

        for (int x = 0; x < BOARD_SIZE; x++)
            for (int y = 0; y < BOARD_SIZE; y++) {
                int idx = (9 * y) + x;

                Cell cell = get(idx);
                String str = cell.toString();

                boolean xMatch = cursorCell.x() == cell.x();
                boolean yMatch = cursorCell.y() == cell.y();

                if (xMatch && yMatch) str = str.replace(' ', '*');
                params[idx] = str;
            }

        int idx = BOARD_SIZE * BOARD_SIZE;
        params[idx++] = cursorCell.x();
        params[idx++] = cursorCell.y();
        params[idx++] = cursorCell.group();
        params[idx++] = cursorCell.value();
        params[idx++] = message;

        return StringUtils.format(TEMPLATE, params);
    }

    public Cell move(Cell current,
                     Direction direction) {
        int x = current.x();
        int y = current.y();

        Cell cell;

        switch (direction) {
            case UP: cell = new Cell(x, y - 1, 0); break;
            case DOWN: cell = new Cell(x, y + 1, 0); break;
            case LEFT: cell = new Cell(x - 1, y, 0); break;
            case RIGHT: cell = new Cell(x + 1, y, 0); break;
            default: throw new RuntimeException();
        }

        message = StringUtils.format(
                "moved cursor from (%s, %s) to (%s, %s)",
                x,
                y,
                cell.x(),
                cell.y()
        );

        return cell;
    }

    public void move(Direction direction) {
        cursorCell = move(cursorCell, direction);
    }

    public void tryPut(Cell cell,
                       int newValue) {
        if (wouldNewValueBeValid(cell, newValue)) {
            Cell c = where((a) -> {
                return a.x() == cell.x() && a.y() == cell.y();
            }).get(0);
            c.value(newValue);
        }
    }
    
    public Cell cursorCell() {
        return cursorCell;
    }

    public enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT
    }
}


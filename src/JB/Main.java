package JB;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Main extends Application {

    static class Cell {
        int row;
        int column;

        public Cell(int row, int column) {
            this.row = row;
            this.column = column;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Cell cell = (Cell) o;
            return row == cell.row && column == cell.column;
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, column);
        }
    }

    static final int SIZE = 20;
    static final int CELL_SIZE = 35;

    static boolean[][] maze = new boolean[SIZE][SIZE];
    static Cell start;
    static Cell finish;
    static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception {
        stage = primaryStage;
        initMaze();
        Human human = new Human(start);
        human.traverse();
        printAll(human.path);
    }


    public static void main(String[] args) {
        launch(args);
    }


    private static void printAll(List<Cell> path) throws InterruptedException {
        GridPane root = new GridPane();

        //maze
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (maze[r][c] == true) {
                    root.add(getCell(Color.WHITE), c, r);
                } else {
                    root.add(getCell(Color.GREY), c, r);
                }
            }
        }

        //path
        for (int i = 0; path != null && i < path.size(); i++) {
            root.add(getCell(Color.ORANGE), path.get(i).column, path.get(i).row);
        }

        //start end finish
        root.add(getCell(Color.GREEN), start.column, start.row);
        root.add(getCell(Color.RED), finish.column, finish.row);

        stage.setTitle("Maze");
        stage.setScene(new Scene(root, CELL_SIZE * SIZE, CELL_SIZE * SIZE));
        stage.show();
    }

    private static Rectangle getCell(Color color) {
        Rectangle rectangle = new Rectangle(CELL_SIZE - 1, CELL_SIZE - 1, color);
        rectangle.setStroke(Color.LIGHTGRAY);
        return rectangle;
    }

    private static List<Cell> getFinalPath() {
        int count = 0;
        List<List<Cell>> paths = new ArrayList<>();

        while (count < 4) {
            List<Cell> path = generateOnePath();
            if (path.get(path.size() - 1).equals(finish)) {
                paths.add(path);
                count++;
            }
        }

        int min = paths.get(0).size();
        int minIndex = 0;

        for (int i = 1; i < paths.size(); i++) {
            if (paths.get(i).size() < min) {
                min = paths.get(i).size();
                minIndex = i;
            }
        }

        return paths.get(minIndex);
    }

    private static List<Cell> generateOnePath() {
        List<Cell> path = new ArrayList<>();
        path.add(start);
        Cell currentCell = start;
        while (path.size() < 100 && !currentCell.equals(finish)) {
            List<Cell> directions = getPossibleDirections(currentCell);
            for (int i = 0; i < directions.size(); i++) {
                if (path.contains(directions.get(i))) {
                    directions.remove(directions.get(i));
                }
            }
            if (directions.size() == 0) {
                break;
            }
            Cell next = directions.get((int) (Math.random() * directions.size()));
            path.add(next);
            currentCell = next;
        }

        return path;
    }

    private static List<Cell> getPossibleDirections(Cell currentCell) {
        List<Cell> result = new ArrayList<>();

        result.add(new Cell(currentCell.row - 1, currentCell.column));
        result.add(new Cell(currentCell.row + 1, currentCell.column));
        result.add(new Cell(currentCell.row, currentCell.column - 1));
        result.add(new Cell(currentCell.row, currentCell.column + 1));

        for (int i = 0; i < result.size(); i++) {
            if (result.get(i).row < 0 || result.get(i).row >= SIZE
                    || result.get(i).column < 0 || result.get(i).column >= SIZE) {
                result.remove(result.get(i));
            }
        }

        return result;
    }

    private static void printMaze() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                System.out.print(maze[r][c] ? "_" : "X");
            }
            System.out.println();
        }
    }

    private static void initMaze() {
        //start and finish point
        int x = (int) (Math.random() * SIZE);
        int y = (int) (Math.random() * SIZE);
        int w = (int) (Math.random() * 4);
        if (w == 0) {
            maze[0][x] = true;
            start = new Cell(0, x);
            finish = new Cell(SIZE - 1, y);
            maze[SIZE - 1][y] = true;
        } else if (w == 1) {
            maze[SIZE - 1][x] = true;
            start = new Cell(SIZE - 1, x);
            finish = new Cell(0, y);
            maze[0][y] = true;
        } else if (w == 2) {
            maze[x][0] = true;
            start = new Cell(x, 0);
            finish = new Cell(y, SIZE - 1);
            maze[y][SIZE - 1] = true;
        } else {
            maze[x][SIZE - 1] = true;
            start = new Cell(x, SIZE - 1);
            finish = new Cell(y, 0);
            maze[y][0] = true;
        }

        //one good path
        List<Cell> path = getFinalPath();

        //additional corridors
        for (int i = 0; i < 20; i++) {
            Cell random = new Cell((int) (Math.random() * SIZE), (int) (Math.random() * SIZE));
            //Cell random = path.get((int) (Math.random() * path.size()));
            if (Math.random() < 0.5) {
                //up-down
                int corridorSize = (int) (Math.random() * 5 + 5);
                int sign = Math.random() < 0.5 ? -1 : 1;
                for (int offset = 0; offset < corridorSize; offset++) {
                    int row = random.row + sign * offset;
                    int column = random.column;
                    if (row < 0 || row >= SIZE) {
                        break;
                    }
                    path.add(new Cell(row, column));
                }
            } else {
                //left-right
                int corridorSize = (int) (Math.random() * 5 + 5);
                int sign = Math.random() < 0.5 ? -1 : 1;
                for (int offset = 0; offset < corridorSize; offset++) {
                    int row = random.row;
                    int column = random.column + sign * offset;
                    if (column < 0 || column >= SIZE) {
                        break;
                    }
                    path.add(new Cell(row, column));
                }
            }
        }

        for (int i = 0; i < path.size(); i++) {
            maze[path.get(i).row][path.get(i).column] = true;
        }

        printMaze();
    }
}

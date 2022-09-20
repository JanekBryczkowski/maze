package JB;

import java.util.ArrayList;
import java.util.List;

import static JB.Orientation.*;

public class Human {
    Main.Cell current;
    Orientation orientaction;
    List<Main.Cell> path;

    public Human(Main.Cell current) {
        this.current = new Main.Cell(current.row,current.column);
        this.orientaction = UP;
        path = new ArrayList<>();
        path.add(this.current);
    }

    public void turnLeft() {
        if (orientaction == UP) orientaction = LEFT;
        else if (orientaction == LEFT) orientaction = DOWN;
        else if (orientaction == DOWN) orientaction = RIGHT;
        else orientaction = UP;
    }

    public void turnRight() {
        if (orientaction == UP) orientaction = RIGHT;
        else if (orientaction == LEFT) orientaction = UP;
        else if (orientaction == DOWN) orientaction = LEFT;
        else orientaction = DOWN;
    }

    public boolean canGoLeft() {
        int row = current.row + getLeftHandOrientation(orientaction).row;
        int column = current.column + getLeftHandOrientation(orientaction).column;

        if (row < 0 || row >= Main.SIZE|| column < 0 || column >= Main.SIZE){
            return false;
        }
        else {
            return Main.maze[row][column];
        }

    }

    public boolean canGoStraight(){
        int row = current.row + orientaction.row;
        int column = current.column + orientaction.column;

        if (row < 0 || row >= Main.SIZE|| column < 0 || column >= Main.SIZE){
            return false;
        }
        else {
            return Main.maze[row][column];
        }
    }

    public void traverse(){
        while (current.row != Main.finish.row || current.column != Main.finish.column){
            if (canGoLeft()){
                turnLeft();
                goOneStep();
                continue;
            }
            else if (canGoStraight()){
                goOneStep();
                continue;
            }
            else {
                turnRight();
                continue;
            }
        }
    }




    public void goOneStep() {
        current.row = current.row + orientaction.row;
        current.column = current.column + orientaction.column;
        Main.Cell temp = new Main.Cell(current.row,current.column);
        path.add(temp);
    }


}

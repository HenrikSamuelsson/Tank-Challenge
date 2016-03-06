import java.util.*;
import java.awt.*;

public class Solution {
    int round;
    int rowPos;
    int colPos;
    ArrayStructure s;
    DIRECTION direction;
    static final boolean DEBUG_ADD = true;
    
    
    
    public Solution() {
        s = new ArrayStructure();
        direction = DIRECTION.SOUTH;
        rowPos = 0;
        colPos = 0;
        s.add(rowPos, colPos, new Cell(CELL_TYPE.T));
        round = 0;
    }
    
    public enum CELL_TYPE {
        T, // the tank
        F, // free 
        S, // something, enemy or wall
        E, // enemy, not identified exactly
        W, // wall
        R, // round target
        P, // purple enemy, the smarter type of spider
        O, // orange enemy, the stupid type of spider
        U, // unknown, not yet explored cell
    } 
    
    public enum DIRECTION {
        NORTH,
        EAST,
        SOUTH,
        WEST,
    }

    /**
     * Executes a single step of the tank's programming. The tank can only move, 
     * turn, or fire its cannon once per turn. Between each update, the tank's 
     * engine remains running and consumes 1 fuel. This function will be called 
     * repeatedly until there are no more targets left on the grid, or the tank 
     * runs out of fuel.
     */
    public void update() {
        
        if(round == 1) {
            updateMap();
            printMap();
        }
        round++;
    }
    
    public void updateMap() {
        // run all lidars to get data about what is around the tank
        int frontDistance = API.lidarFront();
        int backDistance = API.lidarBack();
        int leftDistance = API.lidarLeft();
        int rightDistance = API.lidarRight();
        boolean enemyInFront = API.identifyTarget();
        
        // store all free cells in front of the tank
        for(int i = 1; i < frontDistance; i++) {
            if(direction == DIRECTION.SOUTH) {
             s.add(rowPos + i, colPos, new Cell(CELL_TYPE.F));
            }
            
        }
        // store data about what is in front of the tank
        if(enemyInFront) {
            s.add(rowPos + frontDistance, colPos, new Cell(CELL_TYPE.E));
        }
        else {
            s.add(rowPos + frontDistance, colPos, new Cell(CELL_TYPE.W));
        }
        
        // store all free cells in the back of the tank
        for(int i = 1; i < backDistance; i++) {
            if(direction == DIRECTION.SOUTH) {
                s.add(rowPos - i, colPos, new Cell(CELL_TYPE.F));
            }
        }
        s.add(rowPos - backDistance, colPos, new Cell(CELL_TYPE.S));
         System.out.println("Adding S at " + (rowPos - backDistance) + " "+ colPos);
        
    }
    
    public void printMap() {
        Cell[][] data = s.toArray();
        for (int r = 0; r < data.length; ++r) {
            for (int c = 0; c < data[c].length; ++c) {
                if (data[r][c] == null) {
                    System.out.print("U");
                }
                else {
                    Cell cell = data[r][c];
                    System.out.print(cell.cellType.name());
                }
            }
            System.out.println();
        }
    }
    
    
   public class ArrayStructure {
        private HashMap<Point, Cell> map = new HashMap<Point, Cell>();
        private int maxRow = 0;
        private int minRow = 0;
        private int maxCol = 0;
        private int minCol = 0;

        public ArrayStructure() {
        }

        public void add(int row, int column, Cell cell) {
            map.put(new Point(row, column), cell);
            maxRow = Math.max(row, maxRow);
            minRow = Math.min(row, minRow);
            maxCol = Math.max(column, maxCol);
            maxCol = Math.min(column, minCol);
            
            if (DEBUG_ADD) {
                System.out.println ("maxRow = " + maxRow);
                System.out.println ("minRow = " + minRow);
                System.out.println ("maxCol = " + maxCol);
                System.out.println ("minCol = " + minCol);
            }
        }
    
        public Cell[][] toArray() {
            Cell[][] result = new Cell[maxRow - minRow + 1][maxCol - minCol + 1];
            for (int row = 0; row <= maxRow - minRow; ++row)
                for (int column = 0; column <= maxCol - minCol; ++column) {
                    Point p = new Point(row + minRow, column + minCol);
                    result[row][column] = map.containsKey(p) ? map.get(p) : null;
                }
            return result;
        }
    }

    
    
    public class Cell {
        
        CELL_TYPE cellType;
        int distance;  //how many steps from the tank
        
        public Cell (CELL_TYPE cellType) {
            this.cellType = cellType;
        }
    }
}
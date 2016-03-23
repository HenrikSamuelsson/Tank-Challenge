import java.util.*;
import java.awt.*;

public class Solution {
    int round;
    int rowPos;
    int colPos;
    ArrayStructure s;
    DIRECTION direction;
    static final boolean DEBUG_ADD = false;
    
    
    
    public Solution() {
        s = new ArrayStructure();
        direction = DIRECTION.WEST; // there is no way to know the starting direction so this value is chosen arbitrarly
        rowPos = 0;
        colPos = 0;
        s.add(rowPos, colPos, new Cell(CELL_TYPE.T));
        round = 0;
    }
    
    public enum CELL_TYPE {
        T, // the tank
        F, // free 
        S, // something, enemy or wall
        E, // enemy, not exactly identified
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
        
        if(round == 0) {
            updateMap();
            printMap();
        }
        round++;
    }
    
    /**
     * Tries to move the tank one step forward. The fuel consumption is used as 
     * an indication of if the move succeded.
     * 
     * @return true if the move forward seemed to be completed
     */
    boolean forward() {
        int fuelBefore, fuelAfter, diff;
        
        fuelBefore = API.currentFuel();
        API.moveForward();
        fuelAfter = API.currentFuel();
        diff = fuelBefore - fuelAfter;
        if ( diff < 12  && diff > 1) {
            // fuel consumption indicates that we hit something when moving
            return false;    
        } else {
            return true;
        }

    }
    
    /**
     * Runs all lidars and the target identification system. The result of the 
     * sensor read is used to update the map of the arena with additional data.
     */
    public void updateMap() {
        // run all lidars to get data about what is around the tank
        int frontDistance = API.lidarFront();
        int backDistance = API.lidarBack();
        int leftDistance = API.lidarLeft();
        int rightDistance = API.lidarRight();
        boolean enemyInFront = API.identifyTarget();
        
        // store data about what is in front of the tank
        if(direction == DIRECTION.SOUTH) {
            for(int i = 1; i < frontDistance; i++) {
                s.add(rowPos + i, colPos, new Cell(CELL_TYPE.F));
            }
            if(enemyInFront) {
                s.add(rowPos + frontDistance, colPos, new Cell(CELL_TYPE.E));
            }
            else {
                s.add(rowPos + frontDistance, colPos, new Cell(CELL_TYPE.W));
            }
        }
        
        if(direction == DIRECTION.NORTH) {
            for(int i = 1; i < frontDistance; i++) {
                s.add(rowPos - i, colPos, new Cell(CELL_TYPE.F));
            }
            if(enemyInFront) {
                s.add(rowPos - frontDistance, colPos, new Cell(CELL_TYPE.E));
            }
            else {
                s.add(rowPos - frontDistance, colPos, new Cell(CELL_TYPE.W));
            }
        }
        
        if(direction == DIRECTION.WEST) {
            for(int i = 1; i < frontDistance; i++) {
                s.add(rowPos, colPos - i, new Cell(CELL_TYPE.F));
            }
            if(enemyInFront) {
                s.add(rowPos, colPos - frontDistance, new Cell(CELL_TYPE.E));
            }
            else {
                s.add(rowPos, colPos - frontDistance, new Cell(CELL_TYPE.W));
            }
        }
        
        if(direction == DIRECTION.EAST) {
            for(int i = 1; i < frontDistance; i++) {
                s.add(rowPos, colPos + i, new Cell(CELL_TYPE.F));
            }
            if(enemyInFront) {
                s.add(rowPos, colPos + frontDistance, new Cell(CELL_TYPE.E));
            }
            else {
                s.add(rowPos, colPos + frontDistance, new Cell(CELL_TYPE.W));
            }
        }
        
        // update data about content to the left of the tank
        if (direction == DIRECTION.SOUTH) {
            for(int i = 1; i < leftDistance; i++) {
                s.add(rowPos, colPos + i, new Cell(CELL_TYPE.F));
            }
            s.add(rowPos, colPos + leftDistance, new Cell(CELL_TYPE.W));
        }
        
        if (direction == DIRECTION.NORTH) {
            for(int i = 1; i < leftDistance; i++) {
                s.add(rowPos, colPos - i, new Cell(CELL_TYPE.F));
            }
            s.add(rowPos, colPos - leftDistance, new Cell(CELL_TYPE.W));
        }
        
        if (direction == DIRECTION.EAST) {
           for(int i = 1; i < leftDistance; i++) {
                s.add(rowPos - i, colPos, new Cell(CELL_TYPE.F));
            }
            s.add(rowPos - leftDistance, colPos, new Cell(CELL_TYPE.W));
        }
        
        if (direction == DIRECTION.WEST) {
           for(int i = 1; i < leftDistance; i++) {
                s.add(rowPos + i, colPos, new Cell(CELL_TYPE.F));
            }
            s.add(rowPos + leftDistance, colPos, new Cell(CELL_TYPE.W));
        }
        
        // update data about content to the right of the tank
        if (direction == DIRECTION.SOUTH) {
           for(int i = 1; i < rightDistance; i++) {
                s.add(rowPos, colPos - i, new Cell(CELL_TYPE.F));
            }
            s.add(rowPos, colPos - rightDistance, new Cell(CELL_TYPE.W));
        }
        
        if (direction == DIRECTION.NORTH) {
           for(int i = 1; i < rightDistance; i++) {
                s.add(rowPos, colPos + i, new Cell(CELL_TYPE.F));
            }
            s.add(rowPos, colPos + rightDistance, new Cell(CELL_TYPE.W));
        }
        
        if (direction == DIRECTION.EAST) {
           for(int i = 1; i < rightDistance; i++) {
                s.add(rowPos + i, colPos, new Cell(CELL_TYPE.F));
            }
            s.add(rowPos + rightDistance, colPos, new Cell(CELL_TYPE.W));
        }
        
         if (direction == DIRECTION.WEST) {
           for(int i = 1; i < rightDistance; i++) {
                s.add(rowPos - i, colPos, new Cell(CELL_TYPE.F));
            }
            s.add(rowPos - rightDistance, colPos, new Cell(CELL_TYPE.W));
        }
        
        // store data about what is in the back of the tank
        if(direction == DIRECTION.SOUTH) {
            for(int i = 1; i < backDistance; i++) {
                s.add(rowPos - i, colPos, new Cell(CELL_TYPE.F));
            }
            s.add(rowPos - backDistance, colPos, new Cell(CELL_TYPE.S));
        }
        if(direction == DIRECTION.NORTH) {
            for(int i = 1; i < backDistance; i++) {
                s.add(rowPos + i, colPos, new Cell(CELL_TYPE.F));
            }
            s.add(rowPos + backDistance, colPos, new Cell(CELL_TYPE.S));
        }
        if(direction == DIRECTION.WEST) {
            for(int i = 1; i < backDistance; i++) {
                s.add(rowPos, colPos + i, new Cell(CELL_TYPE.F));
            }
            s.add(rowPos, colPos + backDistance, new Cell(CELL_TYPE.S));
        }
        if(direction == DIRECTION.EAST) {
            for(int i = 1; i < backDistance; i++) {
                s.add(rowPos, colPos - i, new Cell(CELL_TYPE.F));
            }
            s.add(rowPos, colPos - backDistance, new Cell(CELL_TYPE.S));
        }
        
        
    }
    
    public void printMap() {
        Cell[][] data = s.toArray();
        for (int r = 0; r < data.length; ++r) {
            for (int c = 0; c < data[r].length; ++c) {
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
            minCol = Math.min(column, minCol);
            
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

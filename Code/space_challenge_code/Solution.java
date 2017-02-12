import java.util.*;
/**
 * Each location in space is called a cell. We collect data about each cell
 * that we find as we travel around exploring space.
 */
class Cell {
    int yPos;	// vertical positon of this cell
    int xPos;	// horizontal position of this cell
    boolean beenHere;   // true if our space ship have been in this cell
    boolean wasEmpty;   // true if cell ever have been noted to be empty
    boolean holdsBlock; // true if cell holds a block or blocking border
    
    public Cell() {
        
    }
}

/**
 * For storing information about the space ship state.
 */
class SpaceShipState {
	int yPos;
	int xPos;
}

public class Solution {
    /**
     * Current time, increased one step each time update() is run
     */
	int time;
	
    /** 
     * All cells dicovererd in space are stored in this list. This data can
     * then be used when deciding where to navigate next in space.
     */
    List<Cell> cells;
    
    
	public Solution() {
        // If you need initialization code, you can write it here!
        // Do not remove.
        turnCounter = 0;
        cells = new ArrayList<Cell>();
        time = 0;
    }
    
    
    int turnCounter;
    
    
    /**
     * Executes a single step of the ship's programming. The ship can only move, 
     * turn, or fire its cannon once per turn. Between each update, the ship's 
     * engine remains running and consumes 1 fuel. This function will be called 
     * repeatedly until there are no more targets left on the grid, or the  ship 
     * runs out of fuel.
     */
    public void update() {
    	time++;
        // shot if there is an enemy in front of us
        if(API.identifyTarget())
        {
            API.fireLaser();
            return;
        }
        else
        {
            if(turnCounter > 2 && API.lidarFront() > 1 )
            {
                API.moveForward();
                turnCounter = 0;
            }
            else
            {
                API.turnLeft();
                turnCounter++;
            }
            if(1 == API.lidarRight())
            {
                API.turnRight();
                turnCounter++;
            }
            else if (1 == API.lidarLeft())
            {
                API.turnRight();
                turnCounter++;
            }
            else if(1 == API.lidarBack())
            {
                API.turnRight();
                turnCounter++;
            }
        }
    }

    public void updateMap(Cell currentCell)
    {
    
    }
}
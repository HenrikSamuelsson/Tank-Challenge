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
    
    public Cell(int xPos, int yPos) {
    	this.xPos = xPos;
    	this.yPos = yPos;
    }
    
    public boolean equals(Cell cell) {
    	if (cell == null) {
    		return false;
    	}
    	
    	return (this.xPos == cell.xPos && cell.yPos == cell.yPos);
    }

}

enum Direction {
	POSITIVE_Y,
	POSITIVE_X,
	NEGATIVE_Y,
	NEGATIVE_X
}

/**
 * For storing information about the current ship state.
 */
class ShipState {
	int yPos;
	int xPos;
	
	/**
	 * Ship can point in four different directions represented by the enum
	 * called Direction. Turning right increases the direction modulo 4.
	 * Turning left decreases the direction modulo 4.
	 */
	Direction direction;
}

public class Solution {
    /**
     * Current time, increased one step when on each update() invocation
     */
	int time;
	
    /** 
     * All cells dicovererd in space are stored in this list. This data can
     * then be used when deciding where to navigate next in space.
     */
    List<Cell> cells;
    
    ShipState shipState; 
    
	public Solution() {
        // If you need initialization code, you can write it here!
        // Do not remove.
        turnCounter = 0;
        cells = new ArrayList<Cell>();
        time = 0;
        
        // create and initiate the ship state
        // whatever cell we spawn in is considered to be 1,1 
        shipState = new ShipState();
        shipState.xPos = 1;
        shipState.yPos = 1;
     
        // we do not know the direction we spawn in but it does not really 
        // matter lets jus call it POSITIVE_Y
        shipState.direction = Direction.POSITIVE_Y;
        
        // add the first cell to our list of cells
        Cell firstCell =  new Cell(1,1);
        firstCell.beenHere = true;
        firstCell.holdsBlock = false;
        firstCell.wasEmpty = true;
        cells.add(firstCell);
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
    	if(time == 1) {
    		collectData(new Cell(shipState.xPos, shipState.yPos));
    		printData(cells);
    	}
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

    
    /**
     * Collects data about the environment by running the sensors that the ship has.
     * 
     * Moving around and runnning this function repeatedly will add more and more 
     * data about the space.
     */
    public void collectData(Cell currentCell) { 
    	Cell tempCell;
    	
    	int distance = API.lidarFront();
    	for (int i = 1; i < distance; i++)
    	{
    		switch (shipState.direction) {
    		case POSITIVE_Y:
    			tempCell = new Cell(currentCell.xPos, currentCell.yPos + i);
    			break;
    		case NEGATIVE_Y:
    			tempCell = new Cell(currentCell.xPos, currentCell.yPos - 1);
    			break;
    		case POSITIVE_X:
    			tempCell = new Cell(currentCell.xPos + i, currentCell.yPos);
    			break;
    		case NEGATIVE_X:
    			tempCell = new Cell(currentCell.xPos - i, currentCell.yPos);
    			break;
    		default:
    			// should newer happen but the compiler complained about
    			// tempCell might not have been initialized
    			tempCell = new Cell(1, 1);
    		}
    			
    		if (!cells.contains(tempCell)){
    			// this is a new cell that shall be added to our list
    			// but shall first fill in what we know about the cell
    			tempCell.beenHere = false;
    			tempCell.holdsBlock = false;
    			tempCell.wasEmpty = true;
    			cells.add(tempCell);
    		}
    	}
    	
    	boolean isTarget = API.identifyTarget();
    	switch (shipState.direction) {
		case POSITIVE_Y:
			tempCell = new Cell(currentCell.xPos, currentCell.yPos + distance);
			break;
		case NEGATIVE_Y:
			tempCell = new Cell(currentCell.xPos, currentCell.yPos - distance);
			break;
		case POSITIVE_X:
			tempCell = new Cell(currentCell.xPos + distance, currentCell.yPos);
			break;
		case NEGATIVE_X:
			tempCell = new Cell(currentCell.xPos - distance, currentCell.yPos);
			break;
		default:
			// should newer happen but the compiler complained about
			// tempCell might not have been initialized
			tempCell = new Cell(1, 1);
		}
			
		if (!cells.contains(tempCell)){
			// this is a new cell that shall be added to our list
			// but shall first fill in what we know about the cell
			if (isTarget) {
				tempCell.beenHere = false;
				tempCell.holdsBlock = false;
				tempCell.wasEmpty = false;
			}
			else {
				tempCell.beenHere = false;
				tempCell.holdsBlock = true;
				tempCell.wasEmpty = false;
			}
			cells.add(tempCell);
		}
    }

    /**
     * Prints ASCII pattern in the console that visualises the collected data.
     */
    public void printData(List<Cell> cellData) {
    	// collect extreme values to know how large map we need to draw
    	int maxX = 1, maxY = 1, minY = 1, minX = 1;
    	for (Cell c : cellData) {
    		if (c.xPos > maxX) {maxX = c.xPos;}
    		if (c.yPos > maxY) {maxY = c.yPos;}
    		if (c.xPos < minX) {minX = c.xPos;}
    		if (c.yPos < maxY) {maxY = c.yPos;}
    	}
    	for (int row = maxY; row >= minY; row--) {
    		for (int col = minX; col <= maxX; col++) {
    			for (Cell c : cellData) {
    				if(c.yPos == row && c.xPos == col) {
    					if(c.holdsBlock) {System.out.print('O');}
    					else if(c.wasEmpty) {System.out.print(' ');}
    					else {System.out.print('?');}
    				}
    			}
    		}
    		System.out.println();
    	}
    }

}

 import java.util.*;

/**
 * Each location in space is called a cell. We collect data about each cell
 * that we find as we travel around exploring space.
 */
class Cell {
    int yPos;	// vertical positon of this cell
    int xPos;	// horizontal position of this cell
    Content content;
    
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

enum Content {
    SOMETHING,      // lidar have registered something a target or a block
    EMPTY_SPACE,    // a cell that we can visit, can hold a target from time to time
    BLOCK,          // wall or rock 
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
        firstCell.content = Content.EMPTY_SPACE;
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
                navigateForward();
                turnCounter = 0;
            }
            else
            {
                navigateLeft();
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
    			tempCell = new Cell(currentCell.xPos, currentCell.yPos - i);
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
    			tempCell.content = Content.EMPTY_SPACE;
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
				tempCell.content = Content.EMPTY_SPACE;
			}
			else {
				tempCell.content = Content.BLOCK;
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
    	System.out.println("cell count: " + cellData.size());
    	System.out.println("maxX: " + maxX);
    	System.out.println("minX: " + minX);
    	System.out.println("maxY: " + maxY);
    	System.out.println("minY: " + minY);
    	for (int row = maxY; row >= minY; row--) {
    		for (int col = minX; col <= maxX; col++) {
    			for (Cell c : cellData) {
    				if(c.yPos == row && c.xPos == col) {
    					switch(c.content) {
    				        case BLOCK:
    				            System.out.print("O");
    				            break;
    				        case SOMETHING:
    				            System.out.print("X");
    				            break;
				            case EMPTY_SPACE:
				                System.out.print("_");
				                break;
				            default:
				                System.out.print("?");
    					}
    				}
    			}
    		}
    		System.out.println();
    	}
    }
    
    /**
     * Use instead of API.moveForward() to move the ship forward. 
     */
    public void navigateForward() {
        // collect some data about the environment
        int df = API.lidarFront();
        int db = API.lidarBack();
        int dl = API.lidarLeft();
        int dr = API.lidarRight();
        
        // move the ship forward
        API.moveForward();
        
        // collect some data about the new environment
        int dfNew = API.lidarFront();
        int dbNew = API.lidarBack();
        int dlNew = API.lidarLeft();
        int drNew = API.lidarRight();
        
        // verify that we seem to have moved
        if( (dfNew-df != 0) || (dbNew - db != 0) ) {
            // we have likely moved to a new position so log our new positon
            switch (shipState.direction) {
                case POSITIVE_Y:
                    shipState.yPos += 1;
                    break;
                case POSITIVE_X:
                    shipState.xPos += 1;
                    break;
                case NEGATIVE_Y:
                    shipState.yPos  = 1;
                    break;
                case NEGATIVE_X:
                    shipState.xPos -= 1;
                    break;
                default:
                    // should not happen
            }
        }
    }
    
    /**
     * Use instead of API.turnLeft() to turn the ship left.
     */
    public void navigateLeft() {
        // turn the ship
        API.turnLeft();
        
        // log that the ship have turned around 90 degrees
        switch (shipState.direction) {
            case POSITIVE_Y:
                shipState.direction = Direction.NEGATIVE_X;
                break;
            case NEGATIVE_X:
                shipState.direction = Direction.NEGATIVE_Y;
                break;
            case NEGATIVE_Y:
                shipState.direction = Direction.POSITIVE_X;
                break;
            case POSITIVE_X:
                shipState.direction = Direction.POSITIVE_Y;
                break;
            default:
                // should never happen
        }
    }
}


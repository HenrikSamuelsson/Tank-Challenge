public class Solution {
    
    int time;
    int shotsFired;
    int[][] map;
    Position pos;
    
    // Used to force the tank to follow a predefined path
    char[] path = {'P', 'L', 'F', 'R', 'F', 'F', 'F', 'F', 'F', 'F'};
    
    public enum Direction {
        SOUTH, EAST, NORTH, WEST
    }
    
   // Current position on the map.
    public class Position {
    	// Constructor, we always assume that we start at postion 19, 19
    	// and face south.
    	Position(){
            this.col = 19;
            this.row = 19;
            this.direction = Direction.SOUTH;
        }
        
        int col;
        int row;
        Direction direction;
    }
    
    public Solution() {
        final int COLS_MAX = 39;
        final int ROWS_MAX = 39;
        map = new int[ROWS_MAX][COLS_MAX];
    
        pos = new Position();
        shotsFired = 0;
        time = -1;
        
        /**
         * Currently found targets and corresponding threat level.
         * Lower number means lower threat.
         */
        //int currentTargets[4];
    }
    
     // Scans the current surroundings and puts findings in a map. 
    void mapUpdate(Position p) {
        int distance;
        boolean enemy = false;
        distance = API.lidarFront();
        if (API.identifyTarget()) {
            enemy = true;
        }
        switch (p.direction) {
            case SOUTH:
                // TODO update map
                break;
            case EAST:
                // TODO update map
                break;
            case WEST:
                // TODO update map
                break;
            case NORTH:
                // TODO update map
                break;
        }
    }

    /**
     * Executes a single step of the tank's programming. The tank can only move,
     * turn, or fire its cannon once per turn. Between each update, the tank's
     * engine remains running and consumes 1 fuel. This function will be called
     * repeatedly until there are no more targets left on the grid, or the tank
     * runs out of fuel.
     */
    public void update() {
        mapUpdate(this.pos);
        time++;
        System.out.println("fuel = " + API.currentFuel() + " time " + time);
        if (8 > time) {
            switch (path[time]) {
            case 'R':
                API.turnRight();
                break;
                
            case 'L':
                API.turnLeft();
                break;
                
            case 'F':
                API.moveForward();
                break;
                
            case 'P':
                // stand still
                break;
                
            default:
                // do nothing;
            }
        
        }
       else if (API.identifyTarget()) {
            System.out.println("Enemy detected");
            if(2 > shotsFired++)
            API.fireCannon();
       }
      
    }
}

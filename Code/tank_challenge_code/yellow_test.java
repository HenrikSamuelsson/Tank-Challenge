public class Solution {
    
    int time;
    int shotsFired;
    int[][] map;
    double danger;
    Position pos;
    static final int COLS_MAX = 39;
    static final int ROWS_MAX = 39;
    static final boolean DEBUG_ON = true;
    
    int randNum = 0;
    long a = 53917;   
    long c = 11;            
    long previous = 0;
    
    int previousFuel;
    int delta;
    
    // Used to force the tank to follow a predefined path
    char[] path = {'L', 'L', 'L', 'L', 'F', 'L', 'L', 'L', 'L', 'P'};
    
    public enum Direction {
        SOUTH, EAST, NORTH, WEST
    }
    
   // Current position on the map.
    public class Position {
    	// Constructor, we always assume that we start at postion 19, 19
    	// and face north.
    	Position(){
            this.col = 19;
            this.row = 19;
            this.direction = Direction.NORTH;
        }
        
        int col;
        int row;
        Direction direction;
    }
    
    public Solution() {
       
        map = new int[ROWS_MAX][COLS_MAX];
    
        pos = new Position();
        shotsFired = 0;
        time = 0;
        danger = 0;
        resetMap('.');
       
        
        /**
         * Currently found targets and corresponding threat level.
         * Lower number means lower threat.
         */
        //int currentTargets[4];
    }
    
    void rseed(long seed) {
        previous = seed;
    }

    long rand() {
        long r = a * previous + c;
        // Note: typically, one chooses only a couple of bits of this value, see link
        previous = r;
        return r;
    }
    
    /**
     * Scans the current surroundings and puts findings in a map.
     */
    void mapUpdate(Position p) {
        if(DEBUG_ON)
            System.out.println("mapUpdate GO!");
        // 'E' some kind of enemy
    	// 'S' spider
        // 'W' a wall
        // 'O' object, wall or enemy
        // '.' unknown
        // ' ' empty
        // 'T' the tank itself
        int distance;
        boolean enemy = false;
        int i;
        
        // map up the tank itself
        map[pos.row][pos.col] = 'T';
        
        // map up what is in front of the tank
        distance = API.lidarFront();
        if (API.identifyTarget()) {
            enemy = true;
        }
        switch (pos.direction) {
            case SOUTH:
                for(i = 1; i < distance; i++)
                    map[pos.row + i][pos.col] = ' ';
                if(enemy)
                    map[pos.row + distance][pos.col] = 'E';
                else
                    map[pos.row + distance][pos.col] = 'W';
                break;
            case EAST:
                for(i = 1; i < distance; i++)
                    map[pos.row][pos.col + i] = ' ';
                if(enemy)
                    map[pos.row][pos.col + distance] = 'E';
                else
                    map[pos.row][pos.col + distance] = 'W';
                break;
            case WEST:
                for(i = 1; i < distance; i++)
                    map[pos.row][pos.col - i] = ' ';
                if(enemy)
                    map[pos.row][pos.col - distance] = 'E';
                else
                    map[pos.row][pos.col - distance] = 'W';
                break;
            case NORTH:
                for(i = 1; i < distance; i++)
                    map[pos.row - i][pos.col] = ' ';
                if(enemy)
                    map[pos.row - distance][pos.col] = 'E';
                else
                    map[pos.row - distance][pos.col] = 'W';
                break;
        }
        
        // map up the left side of the tank
        distance = API.lidarLeft();
      
        switch (pos.direction) {
            case SOUTH:
                for(i = 1; i < distance; i++)
                    map[pos.row][pos.col + i] = ' ';
                updateCellInfo(pos.row, pos.col + distance);
                break;
            case EAST:
                for(i = 1; i < distance; i++)
                    map[pos.row - i][pos.col] = ' ';
                updateCellInfo(pos.row - distance, pos.col);
                break;
            case WEST:
                for(i = 1; i < distance; i++)
                    map[pos.row + i][pos.col] = ' ';
                updateCellInfo(pos.row + distance, pos.col);
                break;
            case NORTH:
                for(i = 1; i < distance; i++)
                    map[pos.row][pos.col - i] = ' ';
                updateCellInfo(pos.row, pos.col - distance);
                break;
        }
        
        // map up the right side of the tank
        distance = API.lidarRight();
        switch (pos.direction) {
            case SOUTH:
                for(i = 1; i < distance; i++)
                    map[pos.row][pos.col - i] = ' ';
                updateCellInfo(pos.row, pos.col - distance);
                break;
            case EAST:
                for(i = 1; i < distance; i++)
                    map[pos.row + i][pos.col] = ' ';
                updateCellInfo(pos.row + distance, pos.col);
                break;
            case WEST:
                for(i = 1; i < distance; i++)
                    map[pos.row - i][pos.col] = ' ';
                updateCellInfo(pos.row - distance, pos.col);
                break;
            case NORTH:
                for(i = 1; i < distance; i++)
                    map[pos.row][pos.col + i] = ' ';
                updateCellInfo(pos.row, pos.col + distance);
                break;
        }
        
        // map up the back side of the tank
        distance = API.lidarBack();
        switch (pos.direction) {
            case SOUTH:
                for(i = 1; i < distance; i++)
                    map[pos.row][pos.col - i] = ' ';
                updateCellInfo(pos.row, pos.col - distance);
                break;
            case EAST:
                for(i = 1; i < distance; i++)
                    map[pos.row ][pos.col - 1] = ' ';
                updateCellInfo(pos.row, pos.col - distance);
                break;
            case WEST:
                for(i = 1; i < distance; i++)
                    map[pos.row][pos.col + i] = ' ';
                updateCellInfo(pos.row, pos.col + distance);
                break;
            case NORTH:
                for(i = 1; i < distance; i++)
                    map[pos.row + i][pos.col] = ' ';
                updateCellInfo(pos.row + distance, pos.col);
                break;
        }
        
    }
    
    /**
     * Helper function for the lidar detections, updates a given coordinate
     * based on what is known about this coordinate.
     */
    void updateCellInfo(int r, int c) {
    	if ('.' == map[r][c]) {
    		// was totally unknown but we sawe something there now
    		// so we now only mark it as an object
    		map[r][c] = 'O';
    	} else if (' ' == map[r][c]) {
    		// last time we locked so was this empty
    		// seeing something there now indicates that this must be a spider 
    		// that have moved here
    		map[r][c] = 'S';
    		System.out.println("indicating S now");
    	}
    }
    

    /**
     * Prints the map in the console window.
     */
     void printMap() {
        for (int r = 0; r < Solution.ROWS_MAX; r++)
        {
            for (int c = 0; c < Solution.COLS_MAX; c++)
            {
                System.out.printf("%c", map[r][c]);
            }
            System.out.println();
        }
        System.out.println();
     }
     
    /**
    * Resets the map by setting every entry to the given char.
    */
    void resetMap(char e) {
        for (int r = 0; r < Solution.ROWS_MAX; r++)
        {
            for (int c = 0; c < Solution.COLS_MAX; c++)
            {
                map[r][c] = e;
            }
        }
    }
    
    /**
     * Run in conjunction to a move or rotation of our tank to update the
     * location and direction of the tank.
     * 
     * TODO this function assumes that the move is always succesfuel.
     * this may not be the case if we try to move into a wall or an enemy.
     */
    void updatePosition(Position p, char move) {
        if('L' == move) {
            // left turn, update the direction based on current direction
            switch(pos.direction) {
                case SOUTH:
                    pos.direction = Direction.EAST;
                    break;
                case EAST:
                     pos.direction = Direction.NORTH;
                    break;
                case NORTH:
                     pos.direction = Direction.WEST;
                    break;
                case WEST:
                     pos.direction = Direction.SOUTH;
                    break;
            }
        }
        
        if('R' == move) {
            // right turn, update the direction based on current direction
            switch(pos.direction) {
                case SOUTH:
                    pos.direction = Direction.WEST;
                    break;
                case WEST:
                    pos.direction = Direction.NORTH;
                    break;
                case NORTH:
                    pos.direction = Direction.EAST;
                    break;
                case EAST:
                    pos.direction = Direction.SOUTH;
                    break;
            }
        }
        
        if('F' == move) {
            // forward, update the position based on current direction
            switch(pos.direction) {
                case SOUTH:
                    pos.row++;
                    break;
                case WEST:
                    pos.col--;
                    break;
                case NORTH:
                    pos.row--;
                    break;
                case EAST:
                    pos.col++;
                    break;
            }
        }
        
        if('B' == move) {
            // backwards, update the position based on current direction
            switch(pos.direction) {
                case SOUTH:
                    pos.row--;
                    break;
                case WEST:
                    pos.col++;
                    break;
                case NORTH:
                    pos.row++;
                    break;
                case EAST:
                    pos.col--;
                    break;
            }
        }
        
        //System.out.printf("row = %d ", pos.row);
        //System.out.printf("col = %d ", pos.col);
        //System.out.println("direction = " + pos.direction);
        
    }
      
    /**
     * Executes a single step of the tank's programming. The tank can only move,
     * turn, or fire its cannon once per turn. Between each update, the tank's
     * engine remains running and consumes 1 fuel. This function will be called
     * repeatedly until there are no more targets left on the grid, or the tank
     * runs out of fuel.
     */
    public void update() {
        if (0 == time) {
             previousFuel = API.currentFuel();
        } else {
            delta = previousFuel - API.currentFuel();
            if(delta > 7)
                ;//System.out.println("Arrgh");
            else 
                ;//System.out.println("Haha");
        }
            
            
        
        
        if(true) {
           
           
           if(9 > time) {
                mapUpdate(pos);
                printMap();
           }
            
            
          
            // big change in fuel means that something is attacking us
            
            if ( false ) {
                System.out.println("Under Attack");
                
                if (API.identifyTarget() && (API.lidarFront() < 3)) {
                    API.fireCannon();
                } else {   
                    // rotate the tank to find the attacker
                    API.turnRight();
                }
                    
                //house keeping
                if(danger > 0)
                    danger += -1;
                        
                
                    
            }
            //else if (API.identifyTarget()) {
            //   API.fireCannon();
            //}
    
            
            /*
            if(danger > 0) {
                API.fireCannon();
            }
            */
            
            if (time > 8) {
                API.fireCannon();
            } else {
                mapUpdate(pos);
                //randNum  = (int) rand();
                //randNum = randNum % 3;
                switch (path[time]) {
                case 'R':
                    System.out.println("Right");
                    updatePosition(pos, 'R');
                    API.turnRight();
                    break;
                    
                case 'L':
                    System.out.println("Left");
                    updatePosition(pos, 'L');
                    API.turnLeft();
                    break;
                    
                case 'F':
                    System.out.println("Forward");
                    updatePosition(pos, 'F');
                    if(API.lidarFront() != 1) {
                        API.moveForward();
                    }
                    break;
                    
                case 3:
                    System.out.println("Back");
                    updatePosition(pos, 'B');
                    API.moveBackward();
                    break;
                    
                case '4':
                    System.out.println("Pause");
                    break;
                    
                case 5:
                    System.out.println("Fire");
                    API.fireCannon();
                default:
                    // do nothing;
                }
        
            }
        }
        
        /*
        if(time == 10) {
            printMap();
        }
        */
        
      
       
        /*
        if (API.identifyTarget()) {
            //System.out.println("Enemy!");
                danger +=1.8;
        } else {
            //System.out.println("No Enemy detected");
                if(danger > 0)
                    danger += -1;
        }
        if(danger > 0)
        API.fireCannon();
        */
        
       
        time++;
        previousFuel = API.currentFuel();
    }
}
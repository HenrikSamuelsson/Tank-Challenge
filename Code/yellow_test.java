public class Solution {
    
    int time;
    int shotsFired;
    int[][] map;
    double danger;
    Position pos;
     static final int COLS_MAX = 39;
        static final int ROWS_MAX = 39;
    
    // Used to force the tank to follow a predefined path
    char[] path = {'F', 'R', 'F', 'R', 'P', 'P', 'P', 'P', 'P', 'P'};
    
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
    
    /**
     * Scans the current surroundings and puts findings in a map.
     */
    void mapUpdate(Position p) {
        // 'E' some kind of enemy
        // 'W' a wall
        // 'S' something, wall or enemy
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
        switch (p.direction) {
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
      
        switch (p.direction) {
            case SOUTH:
                for(i = 1; i < distance; i++)
                    map[pos.row][pos.col + i] = ' ';
                map[pos.row][pos.col + distance] = 'S';
                break;
            case EAST:
                map[pos.row - distance][pos.col] = 'S';
                for(i = 1; i < distance; i++)
                    map[pos.row - i][pos.col] = ' ';
                break;
            case WEST:
                for(i = 1; i < distance; i++)
                    map[pos.row + i][pos.col] = ' ';
                map[pos.row + distance][pos.col] = 'S';
                break;
            case NORTH:
                for(i = 1; i < distance; i++)
                    map[pos.row][pos.col - i] = ' ';
                map[pos.row][pos.col - distance] = 'S';
                break;
        }
        
        // map up the right side of the tank
        distance = API.lidarRight();
        switch (p.direction) {
            case SOUTH:
                for(i = 1; i < distance; i++)
                    map[pos.row][pos.col - i] = ' ';
                map[pos.row][pos.col - distance] = 'S';
                break;
            case EAST:
                for(i = 1; i < distance; i++)
                    map[pos.row + i][pos.col] = ' ';
                map[pos.row + distance][pos.col] = 'S';
                break;
            case WEST:
                for(i = 1; i < distance; i++)
                    map[pos.row - i][pos.col] = ' ';
                map[pos.row - distance][pos.col] = 'S';
                break;
            case NORTH:
                for(i = 1; i < distance; i++)
                    map[pos.row][pos.col + i] = ' ';
                map[pos.row][pos.col + distance] = 'S';
                break;
        }
        
        // map up the back side of the tank
        distance = API.lidarBack();
        switch (p.direction) {
            case SOUTH:
                for(i = 1; i < distance; i++)
                    map[pos.row][pos.col - i] = ' ';
                map[pos.row][pos.col - distance] = 'S';
                break;
            case EAST:
                for(i = 1; i < distance; i++)
                    map[pos.row ][pos.col - 1] = ' ';
                map[pos.row][pos.col - distance] = 'S';
                break;
            case WEST:
                for(i = 1; i < distance; i++)
                    map[pos.row][pos.col + i] = ' ';
                map[pos.row][pos.col + distance] = 'S';
                break;
            case NORTH:
                for(i = 1; i < distance; i++)
                    map[pos.row + i][pos.col] = ' ';
                map[pos.row + distance][pos.col] = 'S';
                break;
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
     * Executes a single step of the tank's programming. The tank can only move,
     * turn, or fire its cannon once per turn. Between each update, the tank's
     * engine remains running and consumes 1 fuel. This function will be called
     * repeatedly until there are no more targets left on the grid, or the tank
     * runs out of fuel.
     */
    public void update() {
        if(time < 1) {
            mapUpdate(this.pos);
            printMap();
        }
        time++;
       
       /*
        System.out.println("fuel = " + API.currentFuel() + " time " + time);
        if (4 > time) {
            switch (path[time]) {
            case 'R':
                System.out.println("Right");
                API.turnRight();
                break;
                
            case 'L':
                System.out.println("Left");
                API.turnLeft();
                break;
                
            case 'F':
                System.out.println("Forward");
                API.moveForward();
                break;
                
            case 'P':
                System.out.println("Pause");
                break;
                
            case 'S':
                System.out.println("Fire");
                API.fireCannon();
            default:
                // do nothing;
            }
        
        }
     
       
        if (API.identifyTarget()) {
            System.out.println("Enemy!");
                danger +=1.8;
        } else {
            System.out.println("No Enemy detected");
                if(danger > 0)
                    danger += -1;
        }
        if(danger > 0)
        API.fireCannon();
    */
      
    }
}

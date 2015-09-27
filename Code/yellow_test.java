/**
 * Test code used to see how the yellow spiders behave.
 */

public class Solution {
    
    
    int time;
    int shotsFired;
    
    // Used to force the tank to follow a predefined path
    char[] path = {'P', 'L', 'F', 'R', 'F', 'F', 'F', 'F', 'F', 'F'};
    
    public Solution() {
        final int COLS_MAX = 39;
        final int ROWS_MAX = 39;
        int[][] map = new int[ROWS_MAX][COLS_MAX];
    
       
        shotsFired = 0;
        time = -1;
        
        /**
         * Currently found targets and corresponding threat level.
         * Lower number means lower threat.
         */
        //int currentTargets[4];
    }

    /**
     * Executes a single step of the tank's programming. The tank can only move,
     * turn, or fire its cannon once per turn. Between each update, the tank's
     * engine remains running and consumes 1 fuel. This function will be called
     * repeatedly until there are no more targets left on the grid, or the tank
     * runs out of fuel.
     */
    public void update() {
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




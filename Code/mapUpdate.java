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
                updateCellInfo(pos.row - i, pos.col);
                break;
            case EAST:
                for(i = 1; i < distance; i++)
                    map[pos.row ][pos.col - i] = ' ';
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
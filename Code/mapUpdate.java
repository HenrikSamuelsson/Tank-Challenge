/**
     * Scans the current surroundings and puts findings in a map.
     */
    void mapUpdate(Position p) {
        // ´E´ some kind of enemy
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
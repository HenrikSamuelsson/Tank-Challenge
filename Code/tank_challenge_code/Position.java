// Current position on the map.
public class Position {
	// Constructor, we always assume that we start at postion 19, 19
	// and face south.
	Position() {
		this.col = 19;
		this.row = 19;
		this.direction = Direction.SOUTH;
	}

	int col;
	int row;
	Direction direction;
}
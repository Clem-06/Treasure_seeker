package org.concordia;

import java.util.ArrayList;

public class Player1 {

	public char texture = '1';
	public int x;
	public int y;
	public int score;

	private int totalPositionsEvaluated;
	public int totalPruned = 0;


	public Player1(int x, int y) {
		this.x = x;
		this.y = y;
		this.score = 0;
	}

	public /*Student decides the return type*/ void findPath(GameState state) {
	}

	public /*Student decides the return type*/ void predictPath(GameState state) {
	}


	int nearestTreasure(GameState s, boolean[] treasures) {//magic function, creates squares of increasing size until it finds a treasure or reaches maxDistance
		int maxDistance = 15;
		int treasureTargetX = s.p1_x;
		int treasureTargeyY = s.p1_y;

		for (int d = 0; d <= maxDistance; d++) {

			// Top and bottom rows
			for (int dx = -d; dx <= d; dx++) {
				int nx = treasureTargetX + dx;
//creating the upper and lower bounds for the square
				int nyTop = treasureTargeyY - d;
				int nyBottom = treasureTargeyY + d;

				if (inBounds(nx, nyTop)) {
					if (s.tiles[nyTop][nx].treasurePresent && treasures[XYtoI(nx, nyTop)]) return d;
				}

				if (inBounds(nx, nyBottom)) {
					if (s.tiles[nyBottom][nx].treasurePresent && treasures[XYtoI(nx, nyBottom)]) return d;
				}
			}

			// Left and right columns (excluding corners already checked)
			for (int dy = -d + 1; dy <= d - 1; dy++) {
				int ny = treasureTargeyY + dy;

				int nxLeft = treasureTargetX - d;
				int nxRight = treasureTargetX + d;

				if (inBounds(nxLeft, ny)) {
					if (s.tiles[ny][nxLeft].treasurePresent && treasures[XYtoI(nxLeft, ny)]) return d;
				}

				if (inBounds(nxRight, ny)) {
					if (s.tiles[ny][nxRight].treasurePresent && treasures[XYtoI(nxRight, ny)]) return d;
				}
			}
		}

		return 30;
	}


	float eval(GameState s, boolean[] treasures) {//removed tile t to go back to alwats evluating p1 position

		totalPositionsEvaluated++;

		float pointsScore = s.p1_score - s.p2_score;
		float distanceScore = nearestTreasure(s, treasures);

		float eval = 10 * pointsScore - distanceScore;


		//System.out.println("Points Score: " + pointsScore + " Distance Score: -" + distanceScore);

		return eval;
	}


	boolean inBounds(int x, int y) {//true if coordinates are valid bounds in map
		return x >= 0 && x < 80 && y >= 0 && y < 30;
	}


	float search(GameState stateToSearch, boolean[] treasures, int depth, Tile[] bestTileHolder, boolean isRoot) {//recursivey evaluates all neighbors while updating
		// treasure array and simulation, returns best eval out of all children, root stores bestTile passed in array

		Tile current = stateToSearch.tiles[stateToSearch.p1_y][stateToSearch.p1_x];

		if (depth == 0) {            // base case of recursion
			return eval(stateToSearch, treasures);
		}

		float bestEval = -500;

		ArrayList<Tile> tilesOfInterest = new ArrayList<>(); //diagonals and adjacent treasure to be added

		for (int i = 0; i < 9; i++) {
			if (current.neighbours[i] != null) {
				if (current.neighbours[i].treasurePresent || isDiagonal(i)) {
					tilesOfInterest.add(current.neighbours[i]);
				}
			}
		}

		for (Tile n : tilesOfInterest) {
			if (n == null || n.collision) continue;

			//create the simulated Gamestate INSIDE NEIGHTBORS LOOP SO WE DONT NEED TO SAVE OLD VALUES AND UNDO MOVES, JUST CREAGTE NEW ONE
			GameState simulation = new GameState(
					stateToSearch.tiles,
					stateToSearch.p1,
					stateToSearch.p2,
					stateToSearch.rounds_left);

			simulation.p1_score = stateToSearch.p1_score;
			simulation.p2_score = stateToSearch.p2_score;

			simulation.p1_x = n.x; //update sim player position to tile n
			simulation.p1_y = n.y;


			//update simulation score if n contains treasure
			if (n.treasurePresent && treasures[XYtoI(n.x, n.y)]) {//checking treasure still there before updating simulation score
				simulation.p1_score += n.treasure.value;
				treasures[XYtoI(n.x, n.y)] = false; //update treasure map to remove collected treasure
			}

			float neighborEval = search(simulation, treasures, depth - 1, null, false); //no need to pass bestTileHodler down children arent root

			//undo treasure removal
			if (n.treasurePresent) { //add treasure back to map for other branches - dont check if still on treasures map as we removed it
				treasures[XYtoI(n.x, n.y)] = true;
			}

			if (depth == 1) {
				//System.out.println("Candidate Eval:  " + neighborEval);
			}

			if (neighborEval > bestEval) {
				bestEval = neighborEval;
				if (isRoot) {
					bestTileHolder[0] = n; //put the best move into the output array if node is root
				}
			}
		}
		return bestEval;
	}

	public Tile moveDecision(GameState originalState) {
		totalPositionsEvaluated = 0;

		boolean[] treasures = new boolean[MapLoader.MAP_HEIGHT * MapLoader.MAP_WIDTH];

		for (int y = 0; y < MapLoader.MAP_HEIGHT; y++) {
			for (int x = 0; x < MapLoader.MAP_WIDTH; x++) {
				if (originalState.tiles[y][x].treasurePresent) {
					treasures[XYtoI(x, y)] = true;
				}
			}
		}

		Tile[] bestTileHolder = new Tile[1]; //create array we pass by reference in search to get best tile

		search(originalState, treasures, 12, bestTileHolder, true);

		System.out.println("Current Gamestate's true Eval: " + eval(originalState, treasures));

		System.out.println("Total positions searched: ");
		System.out.printf("%.1E%n", (double) totalPositionsEvaluated);
		System.out.println("Rounds left: " + originalState.rounds_left);
		System.out.printf("");


		return bestTileHolder[0];
	}


	boolean isDiagonal(int x) { //helper function for neighbor diagonals
		return (x == 0 || x == 2 || x == 5 || x == 7);
	}

	int XYtoI(int x, int y) { //helper function to convery X,Y coordinates to index in flattened array - treasures
		return y * MapLoader.MAP_WIDTH + x;
	}

	public int getTeleport() {
		return 0;
	}
}

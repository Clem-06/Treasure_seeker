package org.concordia;

public class Player1 {

	public char texture = '1';
	public int x;
	public int y;
	public int score;

	private int positionsSearched;

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


	int nearestTreasure(GameState s) {//magic function, creates squares of increasing size until it finds a treasure or reaches maxDistance
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
					if (s.tiles[nyTop][nx].treasurePresent) return d;
				}

				if (inBounds(nx, nyBottom)) {
					if (s.tiles[nyBottom][nx].treasurePresent) return d;
				}
			}

			// Left and right columns (excluding corners already checked)
			for (int dy = -d + 1; dy <= d - 1; dy++) {
				int ny = y + dy;

				int nxLeft = x - d;
				int nxRight = x + d;

				if (inBounds(nxLeft, ny)) {
					if (s.tiles[ny][nxLeft].treasurePresent) return d;
				}

				if (inBounds(nxRight, ny)) {
					if (s.tiles[ny][nxRight].treasurePresent) return d;
				}
			}
		}

		return -1;
	}


	float eval(GameState s) {//removed tile t to go back to alwats evluating p1 position

		float pointsScore = s.p1_score - s.p2_score;
		float distanceScore = nearestTreasure(s);

		float eval = 15 * pointsScore - distanceScore;


		//System.out.println("Points Score: " + pointsScore + " Distance Score: -" + distanceScore);

		return eval;
	}


	boolean inBounds(int x, int y) {//true if coordinates are valid bounds in map
		return x >= 0 && x < 80 && y >= 0 && y < 30;
	}


	float search(GameState stateToSearch, int depth) {//recursivey evaluates all neighbors

		Tile current = stateToSearch.tiles[stateToSearch.p1_y][stateToSearch.p1_x];

		if(depth < 0)System.err.println("BIG PROBLEM NEGATIVE SEARCH DEPTH");

		if (depth == 0) {            //base case of recursion
			return eval(stateToSearch);
		} else {

			//create the simulated Gamestate
			GameState simulation = new GameState(
					stateToSearch.tiles,
					stateToSearch.p1,
					stateToSearch.p2,
					stateToSearch.rounds_left);

			simulation.p1_score = stateToSearch.p1_score;
			simulation.p2_score = stateToSearch.p2_score;
			simulation.p1_x = stateToSearch.p1_x;
			simulation.p1_y = stateToSearch.p1_y;


			float bestEval = -500;

			for (Tile n : current.neighbours) {
				if (n == null || n.collision) continue;

			//save original position and score of simulation before changing them
				int oldx = simulation.p1_x;
				int oldy = simulation.p1_y;
				int oldScore = simulation.p1_score;

				//update simulation score if n contains treasure
				if (n.treasurePresent) {
					simulation.p1_score += n.treasure.value;
					System.out.println("Treasure picked up!!!, value:  " + n.treasure.value + "  At depth: " + depth + "  At coordiantes: " );
				}

				float neighborEval = search(simulation, depth - 1);//BIG ISSUE - TREASURES ARE NOT UPDATED DOWN RECURSION TREE!!!!!!

				//unmove  tile n on simulation

				if (neighborEval > bestEval) {
					bestEval = neighborEval;
				}

				//return simulation P1 to original values
				simulation.p1_x = oldx;
				simulation.p1_y = oldy;
				simulation.p1_score = oldScore;
			}
//
//			String indent = "  ".repeat(5- depth);
//
//			if (depth > 5) {
//				System.out.println(indent + "Move to (" + n.x + "," + n.y + ")");
//			}


			return bestEval;
		}


	}


	public Tile moveDecision(GameState originalState) {

		Tile current = originalState.tiles[originalState.p1_y][originalState.p1_x];

		float bestEval = -500;
		Tile bestTile = null;

		//create the simulated Gamestate
		GameState simulation = new GameState(
				originalState.tiles,
				originalState.p1,
				originalState.p2,
				originalState.rounds_left);

		simulation.p1_score = originalState.p1_score;
		simulation.p2_score = originalState.p2_score;
		simulation.p1_x = originalState.p1_x;
		simulation.p1_y = originalState.p1_y;


		for (Tile n : current.neighbours) {
			if (n == null || n.collision) continue;


			//save original position and score of simulation before changing them (more imporatnt in search)
			int oldx = simulation.p1_x;
			int oldy = simulation.p1_y;
			int oldScore = simulation.p1_score;


			//change position of player in simulation based on tile n
			simulation.p1_x = n.x;
			simulation.p1_y = n.y;

			//update simulation score if n contains treasure
			if (n.treasurePresent) {
				simulation.p1_score += n.treasure.value;
			}

			int searchDepth = 2; //0 is checking just neighbors eval

			float neighborEval = search(simulation, searchDepth);

			System.out.println("Candidate's eval :  " + neighborEval);
			System.out.println();
			System.out.println();

			if (neighborEval > bestEval) {
				bestEval = neighborEval;
				bestTile = n;
			}

			//return simulation P1 to original values
			simulation.p1_x = oldx;
			simulation.p1_y = oldy;
			simulation.p1_score = oldScore;

		}
		System.out.println("Best Eval of ths ruoudn:  " + bestEval);
		return bestTile;
	}

	int idx(int x, int y) {
		return y * MapLoader.MAP_WIDTH + x;
	}

	public int getTeleport() {
		return 0;
	}
}

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


	int nearestTreasure(GameState s, int x, int y) {
		int maxDistance = 15;

		for (int d = 0; d <= maxDistance; d++) {

			// Top and bottom rows
			for (int dx = -d; dx <= d; dx++) {
				int nx = x + dx;

				int nyTop = y - d;
				int nyBottom = y + d;

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


	float eval(GameState s, Tile t){

		float scoreDiff = s.p1_score-s.p2_score;

		float eval = 15 * scoreDiff - nearestTreasure(s, t.x, t.y);
		// System.out.println("Eval of this negihbot: " + eval);

		return eval;
	}


	boolean inBounds(int x, int y) {
		return x >= 0 && x < 80 && y >= 0 && y < 30;
	}


	float search(GameState sim, int depth){//recursivey evaluates all neighbors
		Tile current = sim.tiles[sim.p1_y][sim.p1_x];

		if(depth == 0){//base case of recursion
			float bestEval = -500;
			Tile bestTile = null;

			for (Tile n : current.neighbours) {
				if (n == null || n.collision) continue;

				float neighborEval = eval(sim,n);

				if(neighborEval > bestEval){
					bestEval = neighborEval;
					bestTile = n;
				}

			}
			System.out.println("Best Eval of ths ruoudn:  " + bestEval);
			return bestTile;
		}



	}


	public Tile moveDecision(GameState s) {

		Tile current = s.tiles[s.p1_y][s.p1_x];

		float bestEval = -500;
		Tile bestTile = null;

		for (Tile n : current.neighbours) {
			if (n == null || n.collision) continue;

			float neighborEval = eval(s,n);

			if(neighborEval > bestEval){
				bestEval = neighborEval;
				bestTile = n;
			}

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

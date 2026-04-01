package org.concordia;

public class Player1 {

	public char texture = '1';
	public int x;
	public int y;
	public int score;

	public int totalPruned = 0;


	int maxhattanToNearestTreasure(GameState s) {
		int best = 10000;
		Tile closest = null;

		int px = s.p1_x;
		int py = s.p1_y;

		for (int y = 0; y < MapLoader.MAP_HEIGHT; y++) {
			for (int x = 0; x < MapLoader.MAP_WIDTH; x++) {
				Tile t = s.tiles[y][x];

				if (t.treasurePresent) {
					int dist = Math.max(Math.abs(px - x), Math.abs(py - y));  // MaxHattan takes max of xdiff and ydiff to take into account diagonal movement
					if (dist < best) {
						closest = t;
						best = dist;
					}
				}
			}
		}
//       closest.display();
//        System.out.println("Steps away from target: " + best);
		return best;
	}

	public Player1(int x, int y) {
		this.x = x;
		this.y = y;
		this.score = 0;
	}

	float evaluateWithCollected(GameState state, boolean[] collected) {
		int scorePointsDiff = state.p1_score - state.p2_score;

		int best = 10000;
		int px = state.p1_x;
		int py = state.p1_y;

		for (int y = 0; y < MapLoader.MAP_HEIGHT; y++) {
			for (int x = 0; x < MapLoader.MAP_WIDTH; x++) {
				Tile t = state.tiles[y][x];

				if (t.treasurePresent && !collected[idx(x, y)]) {
					int dist = Math.max(Math.abs(px - x), Math.abs(py - y));
					if (dist < best) best = dist;
				}
			}
		}

		return 15 * scorePointsDiff - best;
	}


	float search(GameState s, int depth, boolean[] collected, float alpha) {
		if (depth == 0) {
			return evaluateWithCollected(s, collected);
		}

		Tile current = s.tiles[s.p1_y][s.p1_x];

		float best = -500_000;

		for (Tile n : current.neighbours) {
			if (n == null || n.collision) continue;

			int oldX = s.p1_x;   //save old state
			int oldY = s.p1_y;
			int oldScore = s.p1_score;

			int i = idx(n.x, n.y);
			boolean collectedHere = false;

			s.p1_x = n.x;
			s.p1_y = n.y;

			if (n.treasurePresent && !collected[i]) {
				s.p1_score += n.treasure.value;
				collected[i] = true;
				collectedHere = true;
			}

			float potentialEval = evaluateWithCollected(s, collected) +  10 * (depth-1);
			if (depth==5){
				System.out.println("Potential eval: " + potentialEval + " current alpha: " + alpha);
			}
			if(potentialEval <= alpha){
				totalPruned = totalPruned + 1;
				continue;
			}

			float deeper = search(s, depth - 1, collected,alpha);

			if (deeper > best) {
				best = deeper;
			}

			s.p1_x = oldX;  //put sim back to old state after recursion
			s.p1_y = oldY;
			s.p1_score = oldScore;

			if (collectedHere) {
				collected[i] = false;
			}




		}

		return best;
	}

	public /*Student decides the return type*/ void findPath(GameState state) {
	}

	public /*Student decides the return type*/ void predictPath(GameState state) {
	}

	public Tile moveDecision(GameState state) {
		GameState sim = new GameState(state.tiles, new Player1(state.p1_x, state.p1_y), new Player2(state.p2_x, state.p2_y), state.rounds_left);

		sim.p1_x = state.p1_x;
		sim.p1_y = state.p1_y;
		sim.p1_score = state.p1_score;
		sim.p2_score = state.p2_score;

		boolean[] collected = new boolean[MapLoader.MAP_HEIGHT * MapLoader.MAP_WIDTH];

		Tile current = sim.tiles[sim.p1_y][sim.p1_x];

		float best = -500_000;
		Tile bestTile = null;

		for (Tile n : current.neighbours) { //exactly the same as recursive search but root must look for tiles
			if (n == null || n.collision) continue;

			int oldX = sim.p1_x;
			int oldY = sim.p1_y;
			int oldScore = sim.p1_score;

			int i = idx(n.x, n.y);
			boolean collectedHere = false;

			sim.p1_x = n.x;
			sim.p1_y = n.y;

			if (n.treasurePresent && !collected[i]) {
				sim.p1_score += n.treasure.value;
				collected[i] = true;
				collectedHere = true;
			}

			float val = search(sim, 5, collected, best); // depth-1

			if (val > best) {
				best = val;
				bestTile = n;
			}

			sim.p1_x = oldX;
			sim.p1_y = oldY;
			sim.p1_score = oldScore;

			if (collectedHere) {
				collected[i] = false;
			}
		}

		System.out.println("Total pruned: " + totalPruned);

		return bestTile;
	}

	private float asessTerritoryDiff(GameState state) {
		float distanceSum = 0;
		int px = state.p1_x;
		int py = state.p1_y;

		for (int y = 0; y < MapLoader.MAP_HEIGHT; y++) {
			for (int x = 0; x < MapLoader.MAP_WIDTH; x++) {
				Tile t = state.tiles[y][x];

				if (t.treasurePresent) {
					int dist = Math.abs(px - x) + Math.abs(py - y);
					distanceSum += dist;
				}
			}
		}
		return distanceSum;
	}

	int idx(int x, int y) {
		return y * MapLoader.MAP_WIDTH + x;
	}

	public int getTeleport() {
		return 0;
	}
}

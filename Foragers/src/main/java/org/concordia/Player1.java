package org.concordia;

import static org.concordia.MapLoader.MAP_HEIGHT;
import static org.concordia.MapLoader.MAP_WIDTH;

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

	float evaluateWithCollected(GameState state, boolean[] treasureArr, boolean[] collected) {
		int scorePointsDiff = state.p1_score - state.p2_score;

		int best = Integer.MAX_VALUE;
		int px = state.p1_x;
		int py = state.p1_y;

		for (int i = 0; i < treasureArr.length; i++) { //maxhattan logic with new flattened treasure map
			if (treasureArr[i] && !collected[i]) {

				int y = i / MapLoader.MAP_WIDTH;
				int x = i % MapLoader.MAP_WIDTH;

				int dist = Math.max(Math.abs(px - x), Math.abs(py - y));

				if (dist < best) best = dist;
			}
		}
		return 1500 * scorePointsDiff -  best;
	}



	float search(GameState s, boolean[] treasureArr, int depth, boolean[] collected, float alpha) {
		if (depth == 0) {
			positionsSearched++;
			return evaluateWithCollected(s, treasureArr, collected);
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
			//PRUNING
//			float potentialMax = optimisticBound(s, depth, treasureArr, collected);
//			if (potentialMax <= alpha) {
//				s.p1_x = oldX;  //restore sim before pruning
//				s.p1_y = oldY;
//				s.p1_score = oldScore;
//
//				if (collectedHere) {
//					collected[i] = false;
//				}
//				totalPruned++;
//				continue;
//			}

			float deeper = search(s, treasureArr, depth - 1, collected, alpha);

			if (deeper > best) {
				best = deeper;
			}

			//alpha = Math.max(alpha, best);

			s.p1_x = oldX;  //restore sim after recursion
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
		boolean[] startTreasureArr = new boolean[MapLoader.MAP_HEIGHT * MapLoader.MAP_WIDTH];

		for (int y = 0; y < MapLoader.MAP_HEIGHT; y++) {
			for (int x = 0; x < MapLoader.MAP_WIDTH; x++) {
				if (state.tiles[y][x].treasurePresent) {
					startTreasureArr[idx(x, y)] = true;
				}
			}
		}

		//visualizeTreasure(startTreasureArr);  //Check treasure map looks like the actual treasures

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

			float val = search(sim, startTreasureArr, 5, collected, best); // depth-1

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

		String scientific = String.format("%e", (double) positionsSearched);
		System.out.println("Positions searched: " + scientific);


		int realTreasureCount = 0;
		for (int y = 0; y < MapLoader.MAP_HEIGHT; y++)
			for (int x = 0; x < MapLoader.MAP_WIDTH; x++)
				if (state.tiles[y][x].treasurePresent) realTreasureCount++;

		System.out.println("Real treasures remaining: " + realTreasureCount);

		return bestTile;
	}

	int idx(int x, int y) {
		return y * MapLoader.MAP_WIDTH + x;
	}

	public int getTeleport() {
		return 0;
	}
}

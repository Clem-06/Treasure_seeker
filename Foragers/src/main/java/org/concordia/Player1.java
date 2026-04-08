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

	public /*Student decides the return type*/ void findPath(GameState state) {
	}

	public /*Student decides the return type*/ void predictPath(GameState state) {
	}


	int nearestTreasure(GameState s, int x, int y) {
		int maxDistance = 5;

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

	boolean inBounds(int x, int y) {
		return x >= 0 && x < 80 && y >= 0 && y < 30;
	}

	int Maxhattan(GameState s, int px, int py) {
		int best = Integer.MAX_VALUE;

		for (int y = 0; y < MapLoader.MAP_HEIGHT; y++) {
			for (int x = 0; x < MapLoader.MAP_WIDTH; x++) {
				Tile t = s.tiles[y][x];

				if (t.treasurePresent) {
					int dist = Math.max(Math.abs(px - x), Math.abs(py - y));
					if (dist < best) best = dist;
				}
			}
		}
		return best;
	}


	public Tile moveDecision(GameState s) {

		Tile current = s.tiles[s.p1_y][s.p1_x];
		Tile best = null;

		for (Tile n : current.neighbours) {
			if (n == null || n.collision) continue;


			best = n;
		}

		//System.out.println("Nearest Treasure function:" + nearestTreasure(s, s.p1_x, s.p1_y));
		System.out.println("Maxhattan function:" + Maxhattan(s, s.p1_x, s.p1_y));

		return best;
	}

	int idx(int x, int y) {
		return y * MapLoader.MAP_WIDTH + x;
	}

	public int getTeleport() {
		return 0;
	}
}

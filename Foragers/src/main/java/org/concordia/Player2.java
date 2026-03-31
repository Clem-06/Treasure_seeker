package org.concordia;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Player2 {

    public char texture = '2';
    public int x;
    public int y;
    public int score;

    // STUDENT MAY PLACE ANY EXTRA FIELDS THEY WANT HERE -------------------

    // ----------------------------------------------------------------------

    public Player2(int x, int y) {
        this.x = x;
        this.y = y;
        this.score = 0;
    }

    public /*Student decides the return type*/ void findPath(GameState state) {
    }

    public /*Student decides the return type*/ void predictPath(GameState state) {
    }

    int manhattanToNearestTreasure(GameState s) {
        int best = 10000;
        Tile closest = null;

        int px = s.p2_x;
        int py = s.p2_y;

        for (int y = 0; y < MapLoader.MAP_HEIGHT; y++) {
            for (int x = 0; x < MapLoader.MAP_WIDTH; x++) {
                Tile t = s.tiles[y][x];

                if (t.treasurePresent) {
                    int dist = Math.abs(px - x) + Math.abs(py - y);
                    if (dist < best) {
                        closest = t;
                        best = dist;
                    }
                }
            }
        }
        return best;
    }

    public Tile moveDecision(GameState state) {
        Tile current = state.tiles[y][x];

//        System.out.println("Current P2 manahtan to nearest treasure:  " + manhattanToNearestTreasure(state));


        int step = state.rounds_left;
        int phase = 3 - ((step / 15) % 4); // ensures N → W → S → E order

        int r = ThreadLocalRandom.current().nextInt(0, 3);

        Tile next = null;

        switch (phase) {
            case 0: // N
                if (r == 0) next = current.getN();
                else if (r == 1) next = current.getNW();
                else next = current.getNE();
                break;

            case 1: // W
                if (r == 0) next = current.getW();
                else if (r == 1) next = current.getNW();
                else next = current.getSW();
                break;

            case 2: // S
                if (r == 0) next = current.getS();
                else if (r == 1) next = current.getSW();
                else next = current.getSE();
                break;

            case 3: // E
                if (r == 0) next = current.getE();
                else if (r == 1) next = current.getNE();
                else next = current.getSE();
                break;
        }

        return next;
    }

    public void updatePlayer(GameState state) {
        this.x = state.p2_x;
        this.y = state.p2_y;
        this.score = state.p2_score;
    }

    public int getTeleport() {
        return 0;
    }
}

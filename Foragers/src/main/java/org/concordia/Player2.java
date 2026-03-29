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

    public Tile moveDecision(GameState state) {
        Tile current = state.tiles[y][x];

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

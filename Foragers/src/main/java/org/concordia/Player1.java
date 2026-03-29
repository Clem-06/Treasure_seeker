package org.concordia;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class Player1 {

    public char texture = '1';
    public int x;
    public int y;
    public int score;

    // STUDENT MAY PLACE ANY EXTRA FIELDS THEY WANT HERE -------------------

    // ----------------------------------------------------------------------

    public Player1(int x, int y) {
        this.x = x;
        this.y = y;
        this.score = 0;
    }

    public /*Student decides the return type*/ void findPath(GameState state) {}

    public /*Student decides the return type*/ void predictPath(GameState state) {}

    public Tile moveDecision(GameState state) {
        Tile current = state.tiles[y][x];

        Tile next = null;

        int xdiff = state.p2_x - state.p1_x;
        int ydiff = state.p2_y - state.p1_y;

        if (xdiff > 0 && ydiff > 0) {
            next = current.getSE();
        } else if (xdiff == 0 && ydiff > 0) {
            next = current.getS();
        } else if (xdiff < 0 && ydiff > 0) {
            next = current.getSW();
        } else if (xdiff > 0 && ydiff == 0) {
            next = current.getE();
        } else if (xdiff < 0 && ydiff == 0) {
            next = current.getW();
        } else if (xdiff > 0 && ydiff < 0) {
            next = current.getNE();
        } else if (xdiff == 0 && ydiff < 0) {
            next = current.getN();
        } else if (xdiff < 0 && ydiff < 0) {
            next = current.getNW();
        }
        return next;
    }

    public void updatePlayer(GameState state) {
        this.x     = state.p1_x;
        this.y     = state.p1_y;
        this.score = state.p1_score;
    }

    public int getTeleport() {
    return 0;
    }
}

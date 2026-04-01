package org.concordia;

public class Player1 {

    public char texture = '1';
    public int x;
    public int y;
    public int score;

    class MoveEval {
        Tile tile;
        float eval;

        MoveEval(Tile tile, float eval) {
            this.tile = tile;
            this.eval = eval;
        }
    }


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

    public float evaluate(GameState state) {

        int scorePointsDiff = state.p1_score - state.p2_score;
        float maxhattanPoints = maxhattanToNearestTreasure(state);
        //float territoryPoints = asessTerritoryDiff(state, true);

        float scoreCoeff = 15;
        float distanceCoeff = 1;
        float territoryCoeff = 0;

        return scoreCoeff * scorePointsDiff - distanceCoeff * maxhattanPoints;  // + territoryCoeff * territoryPoints;
    }


    MoveEval search(GameState s, int depth) {
        if (depth == 0) {
            return new MoveEval(null, evaluate(s));
        }
        Tile current = s.tiles[s.p1_y][s.p1_x];


        float best = -500_000;
        Tile bestTile = null;

        for (Tile n : current.neighbours) {
            if (n == null || n.collision) continue;

            GameState sim = new GameState(s.tiles, new Player1(s.p1_x, s.p1_y), new Player2(s.p2_x, s.p2_y), s.rounds_left);

            sim.p1_x = s.p1_x;
            sim.p1_y = s.p1_y;
            sim.p1_score = s.p1_score;
            sim.p2_score = s.p2_score;

            applyMove(sim, n);

            MoveEval deeper = search(sim, depth - 1);

            if (deeper.eval > best) {
                best = deeper.eval;
                bestTile = n;
            }
        }
        if (bestTile == null) {
            System.out.println("P1 SEARCH FOUND NO VALID MOVES AT DEPTH " + depth);
        }
        return new MoveEval(bestTile, best);
    }



    void applyMove(GameState sim, Tile next) {
        sim.p1_x = next.x;
        sim.p1_y = next.y;

        if (next.treasurePresent) {
            sim.p1_score += next.treasure.value;
        }
    }


    public /*Student decides the return type*/ void findPath(GameState state) {
    }

    public /*Student decides the return type*/ void predictPath(GameState state) {
    }

    public Tile moveDecision(GameState state) {

        MoveEval bestMove = search(state, 2);   //reccursive search - intial state, depth
        return bestMove.tile;
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




    public int getTeleport() {
        return 0;
    }
}

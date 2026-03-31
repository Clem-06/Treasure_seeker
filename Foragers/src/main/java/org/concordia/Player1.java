package org.concordia;

public class Player1 {

    public char texture = '1';
    public int x;
    public int y;
    public int score;

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

    private float asessTerritoryDiff(GameState state, boolean isPlayer1) {
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


    float search(GameState s, int depth) {
        if (depth == 0) return evaluate(s);

        Tile current = s.tiles[s.p1_y][s.p1_x];
        float best = -500_000;

        for (Tile n : current.neighbours) {
            if (n == null || n.collision) continue;

            GameState sim = new GameState(
                    s.tiles, // shared (read-only!)
                    new Player1(s.p1_x, s.p1_y),
                    new Player2(s.p2_x, s.p2_y),
                    s.rounds_left
            );

            sim.p1_x = s.p1_x;
            sim.p1_y = s.p1_y;
            sim.p1_score = s.p1_score;

            applyMove(sim, n);

            float val = search(sim, depth - 1);
            best = Math.max(best, val);
        }

        return best;
    }


    public /*Student decides the return type*/ void findPath(GameState state) {
    }

    public /*Student decides the return type*/ void predictPath(GameState state) {
    }

    public Tile moveDecision(GameState state) {
        Tile current = state.tiles[y][x];

        Tile best = null;
        float bestScore = -500_000;

        for (Tile n : current.neighbours) {
            if (n == null || n.collision) continue;

            GameState sim = new GameState(
                    state.tiles,
                    new Player1(state.p1_x, state.p1_y),
                    new Player2(state.p2_x, state.p2_y),
                    state.rounds_left
            );
            sim.p1_x = state.p1_x;
            sim.p1_y = state.p1_y;
            sim.p1_score = state.p1_score;

            applyMove(sim, n);

            float possibleEval = evaluate(sim);
//            System.out.println("Evaluated Score : " + possibleEval);

            if (possibleEval > bestScore) {
                best = n;
                bestScore = possibleEval;
//                System.out.println("NEW BEST SCORE ");
            }
        }
//        System.out.println("Evaluation of best move: " + bestScore);
        return best;
    }

    void applyMove(GameState s, Tile next) {
        s.p1_x = next.x;
        s.p1_y = next.y;

        if (next.treasurePresent) {
            s.p1_score += next.treasure.value;
        }
    }

    public int getTeleport() {
        return 0;
    }
}

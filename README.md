# COEN 352 – Forager AI

**Authors:** Clement Lepage · Jules Wygas

A competitive two-player treasure-collection game where both players race to maximize points across 50 rounds. This project implements the AI decision-making for both players.

## Approach

Rather than using A\*, which only finds the shortest path to the nearest treasure, we implemented a **minimax-inspired search tree** — the same family of algorithms used in chess engines. Every turn, the AI recursively simulates up to **12 moves ahead**, evaluating positions by score and proximity to treasure clusters, not just the nearest single target.

This lets the player recognize when an isolated nearby treasure is a trap and route toward a dense cluster instead — something a greedy pathfinder can't do.

## Notable optimizations

- **Chebyshev distance** ("Maxhattan") for nearest-treasure estimation, since diagonal moves are free
- **Expanding-square treasure search** — reduces average distance checks from ~3,200 to ~80
- **Diagonal-first exploration** cuts the branching factor from 8 to 4, enabling depth 12 within the 2-second move budget
- **Opponent awareness** — removes treasures the opponent will reach first, avoiding unwinnable chases

## Results

| Depth | Avg positions searched | Key change |
|---|---|---|
| 6 | ~250,000 | Baseline |
| 8 | ~16,000,000 | Square-by-square treasure detection |
| 12 | ~16,000,000 | Diagonal-only branching (same volume, 6× deeper) |

Player 2 runs at depth 6 as a control — depth 12 wins consistently across map layouts and spawn configurations.

---

📄 [Full technical report](./COEN-352-Programming-Assignment-2-Report.pdf)

# chess-engine
A chess engine written in Java. ELO TBD

## Features
#### 0x88 Board representation
The chess board is represented as a 16x8 array of bytes. It's basically two 8x8 boards next to each other, the left one being the real board, and the right being illegal positions. When generating moves, we can check if a move is out of bounds by simply ANDing the index with 0x88. A non-zero result means the position is on the "wrong" board and out of bounds.
#### NegMax with alpha beta pruning
NegMax is a variation of the minimax algorithm. Minimax/NegMax is used to find the best move in turn-based, two player games. Alpha beta pruning is an improvement which reduces the number of positions that are searched.
#### Static evaluation using piece value tables
To evaluate a leaf node, a heuristic function is used to approximate who's winning and by how much. The material value of all the pieces on the board are calculated. Then, piece-square tables are applied to assign values to pieces depending on what squares they are on. More sophisticated heuristics can be applied for a better approximation, but at the cost of speed.
#### Transposition table with Zobrist Hashing
When searching, we usually encounter the same position multiple times. In to order to avoid this, we generate a hash (using something called Zobrist Hash) for every position searched and store them in a Transposition table. This greatly reduces the search-space and isn't very costly as long as the hashing is cheap (which it is).
#### Move ordering
When generating possible moves to search, we want to order them from best to worst in order to prune as many moves as possible. Since we dont know what moves are best, we make educated guesses. For example, capturing a queen with a pawn is very likely to be a good move.
#### Quiescence Search
When the desired depth of the minimax search is reached, we continue searching, but only with capturing moves, until we reach a "quiet" position.
#### Killer heuristic
A technique used to improve alpha beta. When a cut-off happens, we save the move as a killer move and order it above non-capture moves in sibling nodes.
#### History heuristic
A more generalized variant of Killer heuristic. TODO
#### Perft
Perft is used to count all the leaf nodes of a position at a certain depth. When debugging, we can compare the number of nodes with predetermined values to check if all possible moves have been generated.
#### UCI protocol
UCI is a protocol used to communicate with other chess engines and frameworks. When testing a chess engine, it's important to compare it to other engines.
#### Iterative deepening
Instead of searching to a certain depth, we begin searching at depth 1 and then increment the depth and search again until we reach the desired depth or the time runs out. This turns out to be more efficient than immediately searching to a certain depth since the cache and killer moves are stored between searches. Iterative deepening is also useful when playing under time controls.

## Improvements
- Improve static evaluation method with pawn structure
- Check extension (https://www.chessprogramming.org/Check_Extensions)
- Eval and pawn cache (partially implemented)
- History heuristic (partially implemented)

## Bugs

## Links
- https://www.chessprogramming.org
- Transposition table: http://web.archive.org/web/20080315233307/http://www.seanet.com/~brucemo/topics/hashing.htm

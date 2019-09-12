# chess-engine

##Features
- GUI
- Command line interface
- NegMax with alpha beta pruning
- perft
- Transposition tables

##TODO
- En passant moves
- Hashing of castling & en passant moves
- Quiescence Search
- Better move ordering


###Stats

2019-08-29
Pruning \
`111456 moves calculated. Time: 2885ms. Evaluations per second: 38632.93` 
`Best move: PAWN E2->E4`

Perft depth 5 \
`4865351 moves calculated. Time: 34472ms. Evaluations per second: 141139.22`

with 0x88 board \
`4865351 moves calculated in 1870ms. Evaluations per second: 2601792.0`

minmax with hashing and 0x88 depth 7 \
`358312 moves calculated in 6006ms. Evaluations per second: 59659.008
Best move: b1->c3, value: 95
Sorting time: 3102
MoveGen time: 2599
Eval time: 122`

minmax without hashing 0x88 depth 6 \
`82501 moves calculated in 2599ms. Evaluations per second: 31743.363
Best move: e2->e3, value: -45
Sorting time: 1604
MoveGen time: 898
Eval time: 59`

###Bugs
[  ][  ][  ][  ][  ][  ][♔][♖]
[  ][  ][♙][  ][  ][  ][♙][  ]
[  ][  ][♙][  ][♗][♙][  ][  ]
[♙][  ][♝][  ][♙][  ][  ][♙]
[♟][  ][  ][  ][  ][  ][♟][♚]
[  ][♖][♟][  ][  ][  ][  ][♞]
[  ][♟][♜][  ][  ][  ][  ][♟]
[  ][  ][  ][  ][♜][  ][  ][  ]
g4h5


##Links
Transposition table: http://web.archive.org/web/20080315233307/http://www.seanet.com/~brucemo/topics/hashing.htm

# chess-engine

##Features
- GUI
- NegMax with alpha beta pruning
- perft
- TODO Transposition tables
- TODO Quiescence Search


###Stats
2019-08-29

Pruning
`111456 moves calculated. Time: 2885ms. Evaluations per second: 38632.93` 
`Best move: PAWN E2->E4`

Perft
`4865351 moves calculated. Time: 34472ms. Evaluations per second: 141139.22`

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

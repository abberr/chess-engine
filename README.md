# chess-engine

##Features
- GUI
- Command line interface
- NegMax with alpha beta pruning
- perft
- Transposition table
- Quiescence Search

##TODO
- Better move ordering
- Improve evaluation method with pawn structure
- Null move pruning (https://www.chessprogramming.org/Null_Move_Pruning)
- Check extension (https://www.chessprogramming.org/Check_Extensions)
- UCI-protocol compatibility


###Stats

perft with depth 5\
`4865351 moves calculated in 1870ms. Evaluations per second: 2601792.0`

depth 6\
`119060324 moves calculated in 38274ms. Evaluations per second: 3110736.2`


MINMAX:

minmax with hashing and 0x88 depth 7 \
`368865 moves calculated in 6363ms. Evaluations per second: 57970.297
 Best move: b1c3, value: 95
 Sorting time: 4007
 MoveGen time: 2121
 Eval time: 109`
 
same but without sorting
`5519496 moves calculated in 34370ms. Evaluations per second: 160590.52
Best move: b1c3, value: 95
Sorting time: 2
MoveGen time: 31086
Eval time: 1539`

minmax without hashing 0x88 depth 6 \
`82501 moves calculated in 2599ms. Evaluations per second: 31743.363
Best move: e2->e3, value: -45
Sorting time: 1604
MoveGen time: 898
Eval time: 59`

###Bugs


##Links
Transposition table: http://web.archive.org/web/20080315233307/http://www.seanet.com/~brucemo/topics/hashing.htm
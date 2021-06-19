# Simple Economy mod

A mod adds simple economy system.

## Blocks

### Cash Box

Creative Tab: DECORATION BLOCKS

A cash box with player based locks.
If you want to add player possible to access, 
Click '+' button and select player from list.
It's allowed to access by operator (level 3 or more).

## Commands

### `/get-money <player>`

Operator: Not required without <player>. Required with <player>.

Shows how much money do <player> or me have.

### `/send-money mount [from <from-player|null>] to <to-player>`

Operator: Not required to send from me. Required to send from other player.

Give money from <from-player> or me if not specified to <to-player>.
'null' can be used for get money from nothing.

### `/take-money mount from <player>`

Operator: Level 3 is required.

Take money from <player>. Taken money will be discarded.

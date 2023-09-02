# Enigma Showdown
Enigma Showdown is a framework for allowing anyone to create an AI to play a real time game.
This codebase contains that framework, along with the game, Enigma Showdown: Conquest.
Conquest is a game fully implemented using the framework, and the `view` module contains code for visualizing Conquest.

## Modules

* core
  * a
* common
  * Depends on core
* server
  * Depends on common
  * Contains logic for handling the connecting of clients
* 
* client
  * Depends on core
  * Contains logic used to connect to a server.
  * Parse and serialize

## Objective

This game is played by programming an AI to move the player through the level to reach the goal flag.

## Mechanics

### The player

The player has a hitbox of the size 1.8

### The map

Each level has its own unique map with certain characteristics

#### Ground type

The ground type alters how the player is able to move.

#### Barrier type

The barrier determines

### Electronics

A level may have electronics in it, which are basically boolean variables that can be altered by the state of the map (e.g. levers, pressure plates).
A hidden "electronics panel", may have logic gates inside it to assign a boolean value based on other boolean variables.
The visualization should be able to show and hide the electronics panel, as not all of the logic will be shown on the map itself.



## Puzzle ideas

### Water to put fire out

You must fight multiple waves of enemies. Every once in a while, an enemy will light you on fire.
When this happens, you must go to the water to put yourself out.


## Similar Games

* https://screeps.com/
* https://codecombat.com/
* https://spacetraders.io/

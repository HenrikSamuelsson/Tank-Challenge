# Tank Challenge
Thoughts and Java code related to the Tank Challenge.

## Participants
The tank - This is our player. Will be dropped somwhere on the map at startup.

Walls - Obstacels blocks on the track. Cannot be shot, causes 10 fuel penalty if we run into them.

Rings - Red static rings on the track. Can be shot, currently unknown what happens if running into to them.

Yellow spiders - Are blind, moves forward until hitting something and the turns around 180 degrees. Will cause 50 fuel penalty if they hit us.

Purple spiders - Will se us and hunt us, causes 50 fuel penalty each time they run into us. These spiders will primarly try to reach the same height as the tank and secondary move horizontally aginst the tank. This means that is generally safer to stand and shot horisontally since the majority of the spiders will attack from east or west.

## Actions
Actions are defined as turning, moving or fireing.

Only one action is allowed on each run. Ordering multiple actions in one round will run the first ordererd action and discard all the following actions.

An turn right or left will be actuated on the same round that it is ordered. This means that turning and then scanning for enemies will return the result as if the tank have already turned.

A fired shot will hit eventual enemy in the following round. This means that we will always take damage if the enemy is on the way towards us and on an adjacent square.

## Strategies
There are safe spots against the purple spiders as long as you shoot on every round. The most obvious safe spot is a wall formation with walls on three sides directly adjacent to the tank. Less obvious spots can be a "room" without enemies and only one entry.

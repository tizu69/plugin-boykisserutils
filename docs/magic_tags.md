# BKU: Magic Tags

-   `bku_kill_on_dismount`: Kill when all seated entities dismount. Internally
    used by car, so that the car despawns when the player doesn't need it anymore.

## Internal

These are so that you can `/kill` specific types of entities. Instead of running
`/kill @e[type=horse]`, run `/kill @e[tag=bku__car]`!

-   `bku__car`: Used for `/car`-spawned cars.

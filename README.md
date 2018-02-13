# SpongyBackpacks
SpongyBackpacks is a simple Sponge plugins that provides backpacks for your players, accessible trough commands. The plugin is plug & play and comes with good defaults, so you don't need to worry about configuring it, unless you want to apply some tweaks/limits.

## Commands and descriptions
* /sb create <backpack name> <rows, 1 to 6> [user]: creates a new backpack for you or **user**, if specified.
    By default, players can only have 2 backpacks, and 2 rows. If you want to change this, just set the option **spongybackpacks.limit.rows/backpacks** to a different value on your permission plugin.
    Permissions:
        - spongybackpacks.command.create
* /sb open <backpack name> [user]: opens the backpack with this specific name for you or for a player, if specified
    Permissions:
        - spongybackpacks.command.open.own
        - spongybackpacks.command.open.others
* /sb list [user]: lists the backpack of the specific user, or yours
    Permissions:
        - spongybackpacks.command.list.own
        - spongybackpacks.command.list.others
* /sb close <player>: closes the inventory of player
    Permissions:
        - spongybackpacks.command.close
* /sb delete <backpack name> <drop items (true/false)> [user]: delete this backpack from your backpacks or from the user, if specified
    If drop items is true, the items will be dropped around you. If it's false, the items will be voided.
    Permissions:
        - spongybackpacks.command.delete.own
        - spongybackpacks.command.delete.others
* /sb|spongybackpacks|bp|sbp|backpacks: main command
    Permissions:
        - spongybackpacks.command.main
        
This plugin also adds the ability for players to open their ender chest by shift right clicking with the ender chest item in hand, if they have the **spongybackpacks.enderchest** permission. Make sure to grant them this permission before using that feature!

If you find any issues, report them to the [plugin's issue tracker](https://github.com/Eufranio/SpongyBackpacks/issues). If you want, you can donate for me trough PayPal, my paypal email is **frani@magitechserver.com**.
    

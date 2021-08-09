package net.forthecrown.grenadier;

/**
 * Class that holds constants for permission levels.
 * @see CommandSource#hasPermission(int)
 */
public interface PermissionLevels {
    /**
     * Level 0, everyone has this level.
     */
    int ALL = 0;

    /**
     * Level 1, Moderators
     */
    int MODERATORS = 1;

    /**
     * Level 2, GameMasters, I don't know what that means lol
     */
    int GAMEMASTERS = 2;

    /**
     * Level 3, Admins
     */
    int ADMINS = 3;

    /**
     * Level 4, Owners
     */
    int OWNERS = 4;
}

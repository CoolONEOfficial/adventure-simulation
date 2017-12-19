package ru.coolone.adventure_emulation;

/**
 * Player state, e.g. walk, jump, slide etc.
 */
class PersonMode<PlayerModeId extends Enum> {
    private static final String TAG = com.brashmonkey.spriter.Player.PlayerListener.class.getSimpleName();

    public PersonMode(
            PlayerModeId selfId,
            Player.AnimationNum[] animationNums,
            boolean[] accessMap
    ) {
        // Self id
        this.selfId = selfId;

        // Animation nums
        if (animationNums.length == 1 || // One animation
                animationNums.length == 3) // Start, loop and end animations
            this.animationNums = animationNums;
        else
            throw new AssertionError("Animation nums array length != 1 or 3");

        // Access map
        if (accessMap.length == Player.PlayerModeId.COUNT.ordinal())
            this.accessMap = accessMap;
        else
            throw new AssertionError("Access map length != player mode ids count");
    }

    /**
     * Change access map
     */
    boolean[] accessMap;

    /**
     * Id in modes array
     */
    final PlayerModeId selfId;

    /**
     * Animation nums
     */
    Player.AnimationNum[] animationNums;

    /**
     * @return Start, end, and loop animation bool
     */
    boolean isStartLoopEndAnimations() {
        return animationNums.length == 3;
    }

    /**
     * @return Only loop animation bool
     */
    boolean isOneAnimation() {
        return animationNums.length == 1;
    }
}

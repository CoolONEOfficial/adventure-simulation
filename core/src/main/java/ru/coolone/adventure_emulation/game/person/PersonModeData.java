package ru.coolone.adventure_emulation.game.person;

/**
 * Person mode data (state, e.g. walk, jump, slide etc.)
 *
 * @author coolone
 */
public class PersonModeData<PersonModeId extends Enum, PersonAnimationNum extends Enum> {
    private static final String TAG = com.brashmonkey.spriter.Player.PlayerListener.class.getSimpleName();
    /**
     * Id in modes array
     */
    final PersonModeId selfId;
    /**
     * Can be person move
     */
    public boolean movable;
    /**
     * Person body move velocity
     */
    public float moveVelocity;
    public float moveMaxVelocity;
    /**
     * Animation nums
     */
    public PersonAnimationNum[] animationNums;
    /**
     * Change mode access map
     */
    boolean[] accessMap;

    private PersonModeData(
            PersonModeId selfId,
            boolean movable, float moveVelocity, float moveMaxVelocity,
            PersonAnimationNum[] animationNums,
            boolean[] accessMap
    ) {
        // Self id
        this.selfId = selfId;

        // Move
        this.movable = movable;
        this.moveVelocity = moveVelocity;
        this.moveMaxVelocity = moveMaxVelocity;

        // Animation nums
        if (animationNums.length == 1 || // One animation
                animationNums.length == 3) // Start, loop and end animations
            this.animationNums = animationNums;
        else
            throw new AssertionError(
                    "Animation nums array length != 1 (only loop) or 3 (start, loop, end)"
            );

        // Access map
        this.accessMap = accessMap;
    }

    /**
     * Constructor without move velocity
     *
     * @see private constructor
     */
    public PersonModeData(
            PersonModeId selfId,
            PersonAnimationNum[] animationNums,
            boolean[] accessMap
    ) {
        this(
                selfId,
                false, 0.0f, 0.0f,
                animationNums,
                accessMap
        );
    }

    /**
     * Constructor with move velocity
     *
     * @see private constructor
     */
    public PersonModeData(
            PersonModeId selfId,
            float moveVelocity,
            float moveMaxVelocity,
            PersonAnimationNum[] animationNums,
            boolean[] accessMap
    ) {
        this(
                selfId,
                true, moveVelocity, moveMaxVelocity,
                animationNums,
                accessMap
        );
    }

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

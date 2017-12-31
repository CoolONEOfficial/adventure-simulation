package ru.coolone.adventure_emulation.scripts.person;

import java.util.ArrayList;

/**
 * {@link Person} mode
 *
 * @param <PersonModeId> @{@link PersonMode} ids enum
 * @param <AnimationId>  Animation ids enum
 * @author coolone
 * @see Person
 */
public class PersonMode<PersonModeId extends Enum, AnimationId extends Enum> {
    private static final String TAG = PersonMode.class.getSimpleName();

    /**
     * Person behavior
     *
     * @see Behavior
     */
    final Behavior<PersonModeId> behavior;

    /**
     * Animation ids
     * Keys is @{@link AnimationType}
     *
     * @see AnimationType
     */
    private final AnimationId[] animationIds;
    final boolean animationStartLoopEnd;

    /**
     * Map of @{@link ChangeMode}'s of @{@link PersonMode}'s
     */
    final ChangeMode[] changeMap;

    /**
     * Change modes
     */
    public enum ChangeMode {
        /**
         * Change without end animation anyway
         */
        ALLOWED_HARD,
        /**
         * Change with end animation if he available
         */
        ALLOWED_SOFT,
        /**
         * Change not allowed
         */
        NOT_ALLOWED
    }

    /**
     * Movable flag
     */
    public final boolean movable;

    /**
     * Move speed's
     * Using only if @movable active
     */
    public final float moveAcceleration;
    public final float moveVelocity;

    /**
     * Animation id types
     */
    enum AnimationType {
        LOOP,
        START,
        END
    }

    /**
     * Listeners dynamic array
     *
     * @see Listener
     */
    private ArrayList<Listener> listeners = new ArrayList<Listener>();

    /**
     * @param movable          @see movable
     * @param moveAcceleration @see moveAcceleration
     * @param animationIds     @see animationIds
     * @param changeMap        @see changeMap
     * @param behavior         @see {@link Behavior}
     * @param listener         @see @{@link Listener}
     */
    private PersonMode(
            boolean movable,
            float moveAcceleration,
            float moveVelocity,
            AnimationId[] animationIds,
            ChangeMode[] changeMap,
            Behavior<PersonModeId> behavior,
            Listener listener
    ) {
        this.movable = movable;
        this.moveAcceleration = moveAcceleration;
        this.moveVelocity = moveVelocity;
        this.animationIds = animationIds;
        if (animationIds.length != 1 &&
                animationIds.length != 3)
            throw new RuntimeException("Animation ids count " + animationIds.length + " is wrong");
        animationStartLoopEnd = animationIds.length == 3;
        this.changeMap = changeMap;
        this.behavior = behavior;
        addListener(listener);
    }

    public PersonMode(
            AnimationId[] animationIds,
            ChangeMode[] changeMap,
            Behavior<PersonModeId> behavior
    ) {
        this(false, 0.0f, 0.0f,
                animationIds,
                changeMap,
                behavior,
                null);
    }

    /**
     * With @{@link Listener}
     *
     * @see Listener
     */
    public PersonMode(
            AnimationId[] animationIds,
            ChangeMode[] changeMap,
            Behavior<PersonModeId> behavior,
            Listener listener
    ) {
        this(false, 0.0f, 0.0f,
                animationIds,
                changeMap,
                behavior,
                listener);
    }

    /**
     * With @moveAcceleration
     */
    public PersonMode(
            float moveAcceleration, float moveVelocity,
            AnimationId[] animationIds,
            ChangeMode[] changeMap,
            Behavior<PersonModeId> behavior
    ) {
        this(true, moveAcceleration, moveVelocity,
                animationIds,
                changeMap,
                behavior,
                null);
    }

    /**
     * With @moveAcceleration and @{@link Listener}
     *
     * @see Listener
     */
    public PersonMode(
            float moveAcceleration, float moveVelocity,
            AnimationId[] animationIds,
            ChangeMode[] changeMap,
            Behavior<PersonModeId> behavior,
            Listener listener
    ) {
        this(true, moveAcceleration, moveVelocity,
                animationIds,
                changeMap,
                behavior,
                listener);
    }

    void addListener(Listener listener) {
        if (listener != null)
            listeners.add(listener);
    }

    boolean removeListener(Listener listener) {
        int index = listeners.indexOf(listener);
        if (index != -1) {
            listeners.remove(listener);

            return true;
        }
        return false;
    }

    AnimationId getAnimationId(AnimationType type) {
        return (animationIds.length > type.ordinal())
                ? animationIds[type.ordinal()]
                : null;
    }

    /**
     * @param <PersonModeId> @{@link PersonMode} id enum
     */
    public interface Behavior<PersonModeId extends Enum> {
        /**
         * Called on act
         *
         * @return End boolean
         */
        boolean checkEnd();

        /**
         * Called on end of mode
         *
         * @return Next @{@link PersonMode} id
         */
        PersonModeId getNextModeId();

        /**
         * Called on end of end animation and if mode canceled
         *
         * @return Default next @{@link PersonMode} id
         */
        PersonModeId getDefaultNextModeId();
    }

    /**
     * {@link PersonMode} events listener
     */
    static abstract public class Listener {
        /**
         * Called on activate this @{@link PersonMode}
         */
        protected void onActivate() {
        }

        /**
         * Called on deactivate this @{@link PersonMode}
         */
        protected void onDeactivate() {
        }

        /**
         * Called on act @{@link ru.coolone.adventure_emulation.scripts.person.PersonComposite}
         */
        protected void onAct() {
        }

        /**
         * Called on move ended
         *
         * @see PersonMode:movable
         */
        protected void onMoveEnded() {
        }
    }

    void onActivate() {
        for (Listener mListener : listeners)
            mListener.onActivate();
    }

    void onDeactivate() {
        for (Listener mListener : listeners)
            mListener.onDeactivate();
    }

    void onAct() {
        for (Listener mListener : listeners)
            mListener.onAct();
    }

    void onMoveEnded() {
        for (Listener mListener : listeners)
            mListener.onMoveEnded();
    }
}

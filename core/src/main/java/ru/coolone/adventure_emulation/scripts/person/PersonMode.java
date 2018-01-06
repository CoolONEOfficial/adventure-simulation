package ru.coolone.adventure_emulation.scripts.person;

import java.util.ArrayList;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * {@link Person} mode
 *
 * @param <PersonModeId> @{@link PersonMode} ids enum
 * @param <AnimationId>  Animation ids enum
 * @author coolone
 * @see Person
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class PersonMode<PersonModeId extends Enum, AnimationId extends Enum> {
    private static final String TAG = PersonMode.class.getSimpleName();

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
     * Animation ids
     * Keys is @{@link AnimationType}
     *
     * @see AnimationType
     */
    @NonNull
    private final AnimationId[] animationIds;
    final boolean animationStartLoopEnd;

    /**
     * Map of @{@link ChangeMode}'s of @{@link PersonMode}'s
     */
    @NonNull
    final ChangeMode[] changeMap;

    /**
     * Person behavior
     *
     * @see Behavior
     */
    @NonNull
    final Behavior<PersonModeId> behavior;

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
    public final ArrayList<Listener> listeners = new ArrayList<>();

    public PersonMode(
            float moveAcceleration, float moveVelocity,
            AnimationId[] animationIds,
            ChangeMode[] changeMap,
            Behavior<PersonModeId> behavior
    ) {
        this(
                moveVelocity > 0f, moveAcceleration, moveVelocity,
                animationIds, animationIds.length == 3,
                changeMap,
                behavior
        );
    }

    public PersonMode(
            AnimationId[] animationIds,
            ChangeMode[] changeMap,
            Behavior<PersonModeId> behavior
    ) {
        this(
                0.0f, 0.0f,
                animationIds,
                changeMap,
                behavior
        );
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

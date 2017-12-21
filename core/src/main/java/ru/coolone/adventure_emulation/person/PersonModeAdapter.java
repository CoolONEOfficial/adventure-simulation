package ru.coolone.adventure_emulation.person;

import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Mainline;

/**
 * Adapter, for {@link PersonModeData}, that will be
 *
 * @see PersonModeData
 */
public class PersonModeAdapter<PersonModeId extends Enum>
        implements com.brashmonkey.spriter.Player.PlayerListener {
    private static final String TAG = PersonModeAdapter.class.getSimpleName();
    /**
     * Player modes static array link
     */
    private final Person person;
    /**
     * Person mode events listener
     *
     * @see PersonModeListener
     */
    private final PersonModeListener listener;
    /**
     * Person mode
     */
    private PersonModeData mode;

    public PersonModeAdapter(
            Person person,
            PersonModeData mode,
            PersonModeListener<PersonModeId> listener) {
        this.person = person;
        this.mode = mode;
        this.listener = listener;
    }

    /**
     * Changes modeId to self id
     */
    public void activate() {
        // Check change access
        if (person.getCurrentMode().accessMap[mode.selfId.ordinal()] &&
                person.getCurrentMode().selfId != mode.selfId) {
            // Animation
            person.getSpriter().player.setAnimation(
                    mode.animationNums[(mode.isStartLoopEndAnimations()
                            ? AnimationNumId.START
                            : AnimationNumId.LOOP
                    ).ordinal()].ordinal());

            // Remove old player listener
            person.getSpriter().player.removeListener(person.getCurrentModeAdapter());

            // Change mode
            person.modeId = mode.selfId;

            // Add player listener to changed mode
            person.getSpriter().player.addListener(this);

            // Call onSet
            listener.onSet();
        }
    }

    public boolean checkEnd() {
        // Check
        boolean checkResult = listener.checkEnd();
        if (checkResult) {
            if (mode.isStartLoopEndAnimations())
                // Start end animation
                person.getSpriter().player.setAnimation(
                        mode.animationNums[AnimationNumId.END.ordinal()].ordinal()
                );
            else
                // To next mode
                toNextMode();
        }

        return checkResult;
    }

    public void toNextMode() {
        // Set next mode
        PersonModeId nextModeId = (PersonModeId) listener.getNextModeId();
        if (nextModeId != null)
            person.getModeAdapters()[nextModeId.ordinal()].activate();
    }

    @Override
    public void animationFinished(Animation animation) {
        if (mode.isStartLoopEndAnimations()) {
            // Check end of start animation
            if (animation.id == mode.animationNums[AnimationNumId.START.ordinal()].ordinal()) {
                // Start loop animation
                person.getSpriter().player.setAnimation(
                        mode.animationNums[AnimationNumId.LOOP.ordinal()].ordinal()
                );
            }

            // Check end of end animation
            else if (animation.id == mode.animationNums[AnimationNumId.END.ordinal()].ordinal()) {
                // To next mode
                toNextMode();
            }
        }
    }

    @Override
    public void animationChanged(Animation oldAnim, Animation newAnim) {
    }

    @Override
    public void preProcess(com.brashmonkey.spriter.Player player) {
    }

    @Override
    public void postProcess(com.brashmonkey.spriter.Player player) {
    }

    @Override
    public void mainlineKeyChanged(Mainline.Key prevKey, Mainline.Key newKey) {
    }

    /**
     * Animation nums ids
     */
    public enum AnimationNumId {
        LOOP,
        START,
        END
    }

    /**
     * Person events listener
     */
    static public abstract class PersonModeListener<PersonModeId extends Enum> {
        /**
         * Checks end of mode
         * Calling in every act
         *
         * @return Mode end bool
         */
        protected boolean checkEnd() {
            return false;
        }

        /**
         * @return Mode, that will be setted after end previous mode
         */
        protected PersonModeId getNextModeId() {
            return null;
        }

        /**
         * Called, when player mode has been activate
         */
        protected void onSet() {
        }

        /**
         * Called, when player act ended
         */
        protected void onAct() {
        }
    }
}

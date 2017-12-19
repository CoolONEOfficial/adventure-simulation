package ru.coolone.adventure_emulation;

import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Mainline;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;

/**
 * Created by coolone on 19.12.17.
 */
class PersonModeAdapter
        implements com.brashmonkey.spriter.Player.PlayerListener {
    private static final String TAG = PersonModeAdapter.class.getSimpleName();

    PersonModeAdapter(
            Person person,
            PersonMode mode,
            PlayerModeListener listener) {
        this.person = person;
        this.mode = mode;
        this.listener = listener;
    }

    /**
     * Player modes static array link
     */
    private final Person person;

    private PersonMode mode;

    private final PlayerModeListener listener;

    /**
     * Changes modeId to self id
     */
    void set() {
        // Check change access
        if (person.getCurrentMode().accessMap[mode.selfId.ordinal()]) {
            // Animation
            person.getSpriter().player.setAnimation(
                    mode.animationNums[(mode.isStartLoopEndAnimations()
                            ? Player.AnimationNumId.START
                            : Player.AnimationNumId.LOOP
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

    boolean checkEnd() {
        // Check
        boolean checkResult = listener.checkEnd();
        if (checkResult) {
            if (mode.isStartLoopEndAnimations())
                // Start end animation
                person.getSpriter().player.setAnimation(
                        mode.animationNums[Player.AnimationNumId.END.ordinal()].ordinal()
                );
            else
                // To next mode
                toNextMode();
        }

        return checkResult;
    }

    void toNextMode() {
        // Set next mode
        Player.PlayerModeId nextModeId = listener.getNextMode();
        if (nextModeId != null)
            person.getModeAdapters()[nextModeId.ordinal()].set();
    }

    @Override
    public void animationFinished(Animation animation) {
        if (mode.isStartLoopEndAnimations()) {
            // Check end of start animation
            if (animation.id == mode.animationNums[Player.AnimationNumId.START.ordinal()].ordinal()) {
                // Start loop animation
                person.getSpriter().player.setAnimation(
                        mode.animationNums[Player.AnimationNumId.LOOP.ordinal()].ordinal()
                );
            }

            // Check end of end animation
            else if (animation.id == mode.animationNums[Player.AnimationNumId.END.ordinal()].ordinal()) {
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

    static class PlayerModeListener {
        /**
         * Checks end of mode
         * Calling in every act
         *
         * @return Mode end bool
         */
        boolean checkEnd() {
            return false;
        }

        /**
         * @return Mode, that will be setted after end previous
         */
        Player.PlayerModeId getNextMode() {
            return null;
        }

        /**
         * Called, when player mode has been set
         */
        void onSet() {

        }
    }
}

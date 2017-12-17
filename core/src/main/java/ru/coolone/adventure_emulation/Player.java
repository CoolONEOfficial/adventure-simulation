package ru.coolone.adventure_emulation;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import com.badlogic.gdx.utils.Array;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Mainline;
import com.brashmonkey.spriter.Player.PlayerListener;
import com.uwsoft.editor.renderer.components.physics.PhysicsBodyComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import java.util.HashMap;

/**
 * Created by coolone on 13.12.17.
 */

public class Player implements InputProcessor {

    private static final String TAG = Player.class.getSimpleName();

    public Player(World world) {
        this.world = world;
    }

    /**
     * Box2d world
     */
    private World world;

    /**
     * Spriter animation ids
     */
    public enum AnimationNum {
        IDLE,
        WALK,
        BREAK_START,
        BREAK_LOOP,
        BREAK_END,
        SLIDE_START,
        SLIDE_LOOP,
        SLIDE_END,
        CROUCH_START,
        CROUCH_LOOP,
        CROUCH_END,
        JUMP_START,
        JUMP_LOOP,
        JUMP_END,
        HIT
    }

    /**
     * Move direction
     */
    public enum MoveDirection {
        NONE,
        LEFT,
        RIGHT
    }

    private MoveDirection move = MoveDirection.NONE;

    /**
     * PlayerModeId
     */
    public enum PlayerModeId {
        IDLE,
        WALK,
        JUMP,
        SLIDE,
        CROUCH,
        BREAK
    }

    public final PlayerMode[] modes = new PlayerMode[]{
            new PlayerMode(
                    PlayerModeId.IDLE,
                    new AnimationNum[]{
                            AnimationNum.IDLE
                    },
                    new PlayerModeListener() {
                        @Override
                        public boolean checkEnd() {
                            return false;
                        }

                        @Override
                        public PlayerModeId endMode() {
                            return null;
                        }
                    }
            ),
            new PlayerMode( // WALK
                    PlayerModeId.WALK,
                    new AnimationNum[]{
                            AnimationNum.WALK
                    },
                    new PlayerModeListener() {
                        @Override
                        public boolean checkEnd() {
                            return physic.body.getLinearVelocity().x == 0;
                        }

                        @Override
                        public PlayerModeId endMode() {
                            return physic.body.getLinearVelocity().x == 0
                                    ? PlayerModeId.IDLE
                                    : PlayerModeId.BREAK;
                        }
                    }
            ),
            new PlayerMode( // JUMP
                    PlayerModeId.JUMP,
                    new AnimationNum[]{
                            AnimationNum.JUMP_LOOP,
                            AnimationNum.JUMP_START,
                            AnimationNum.JUMP_END
                    },
                    new PlayerModeListener() {
                        @Override
                        public boolean checkEnd() {
                            return spriter.player.getAnimation().id == AnimationNum.JUMP_LOOP.ordinal() &&
                                    isPlayerGrounded();
                        }

                        @Override
                        public PlayerModeId endMode() {
                            return physic.body.getLinearVelocity().x == 0
                                    ? PlayerModeId.IDLE
                                    : PlayerModeId.BREAK;
                        }
                    }
            ),
            new PlayerMode( // SLIDE
                    PlayerModeId.SLIDE,
                    new AnimationNum[]{
                            AnimationNum.SLIDE_LOOP,
                            AnimationNum.SLIDE_START,
                            AnimationNum.SLIDE_END
                    },
                    new PlayerModeListener() {
                        @Override
                        public boolean checkEnd() {
                            return physic.body.getLinearVelocity().x == 0;
                        }

                        @Override
                        public PlayerModeId endMode() {
                            return PlayerModeId.IDLE;
                        }
                    }
            ),
            new PlayerMode( // CROUCH
                    PlayerModeId.CROUCH,
                    new AnimationNum[]{
                            AnimationNum.CROUCH_LOOP,
                            AnimationNum.CROUCH_START,
                            AnimationNum.CROUCH_END
                    },
                    new PlayerModeListener() {
                        @Override
                        public boolean checkEnd() {
                            return false;
                        }

                        @Override
                        public PlayerModeId endMode() {
                            return PlayerModeId.IDLE;
                        }
                    }
            ),
            new PlayerMode( // BREAK
                    PlayerModeId.BREAK,
                    new AnimationNum[]{
                            AnimationNum.BREAK_LOOP,
                            AnimationNum.BREAK_START,
                            AnimationNum.BREAK_END
                    },
                    new PlayerModeListener() {
                        @Override
                        public boolean checkEnd() {
                            return physic.body.getLinearVelocity().x == 0;
                        }

                        @Override
                        public PlayerModeId endMode() {
                            return PlayerModeId.IDLE;
                        }
                    }
            )
    };

    /**
     * Player mode id
     */
    private PlayerModeId modeId = PlayerModeId.IDLE;

    public PlayerModeId getModeId() {
        return modeId;
    }

    public PlayerMode getMode() {
        return modes[modeId.ordinal()];
    }

    /**
     * Components
     */
    private PhysicsBodyComponent physic;
    private SpriterComponent spriter;

    /**
     * Horizontal flipped image flag
     */
    private boolean flipped = false;

    public static final int JUMP_VELOCITY = 1000;
    public static final int MOVE_VELOCITY = 50;

    public class CompositeScript implements IScript {

        @Override
        public void init(Entity entity) {
            physic = ComponentRetriever.get(entity, PhysicsBodyComponent.class);
        }

        @Override
        public void act(float delta) {
            // FIXME: 13.12.17
            if (!physic.body.isFixedRotation())
                physic.body.setFixedRotation(true);

            // Move
            switch (move) {
                case LEFT:
                    physic.body.applyLinearImpulse(
                            new Vector2(-MOVE_VELOCITY, 0),
                            physic.body.getPosition(),
                            true
                    );
                    break;
                case RIGHT:
                    physic.body.applyLinearImpulse(
                            new Vector2(MOVE_VELOCITY, 0),
                            physic.body.getPosition(),
                            true
                    );
                    break;
            }

            // Check mode end
            getMode().checkEnd();
        }

        @Override
        public void dispose() {

        }
    }

    public class SpriterScript implements IScript {
        @Override
        public void init(Entity entity) {
            spriter = ComponentRetriever.get(entity, SpriterComponent.class);
        }

        @Override
        public void act(float delta) {
        }

        @Override
        public void dispose() {

        }
    }

    @Override
    public boolean keyDown(int keycode) {
        Gdx.app.log(TAG, "Key down (" + keycode + ')');

        // Change modeId (and/or) animation

        // Create
        PlayerModeId newModeId = null;

        // Set
        switch (keycode) {
            case Input.Keys.LEFT:
            case Input.Keys.RIGHT:
                if (move == MoveDirection.NONE) {
                    // Move to..
                    move = (keycode == Input.Keys.LEFT)
                            ? MoveDirection.LEFT   // ..left
                            : MoveDirection.RIGHT; // ..right

                    // Flip image
                    if ((keycode == Input.Keys.LEFT && !flipped) ||
                            (keycode == Input.Keys.RIGHT && flipped)) {
                        spriter.player.flipX();
                        flipped = keycode == Input.Keys.LEFT;
                    }

                    // Mode
                    newModeId = PlayerModeId.WALK;
                }
                break;
            case Input.Keys.UP:
                // Jump physic body
                physic.body.applyLinearImpulse(
                        new Vector2(0, JUMP_VELOCITY),
                        physic.body.getPosition(),
                        true
                );

                // Jump
                newModeId = PlayerModeId.JUMP;
                break;
            case Input.Keys.DOWN:
                if (modeId == PlayerModeId.WALK)
                    if (move == MoveDirection.RIGHT ||
                            move == MoveDirection.LEFT) {
                        // Slide
                        newModeId = PlayerModeId.SLIDE;
                    } else {
                        // Crouch
                        newModeId = PlayerModeId.CROUCH;
                    }
                break;
        }

        // Apply
        if (newModeId != null)
            modes[newModeId.ordinal()].set();

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        Gdx.app.log(TAG, "Key up (" + keycode + ')');

        // Check move direction
        boolean stopMove = false;
        switch (keycode) {
            case Input.Keys.RIGHT:
                stopMove = move == MoveDirection.RIGHT;
                break;
            case Input.Keys.LEFT:
                stopMove = move == MoveDirection.LEFT;
                break;
        }
        if (stopMove) {
            // Stop moving
            move = MoveDirection.NONE;

            // End mode
            getMode().endMode();
        }

        // Stop crouch
        switch (keycode) {
            case Input.Keys.DOWN:
                // Change animation
                if (modeId == PlayerModeId.CROUCH)
                    getMode().endMode();
                break;
        }

        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    /**
     * Binding touch coords to keyboard key
     *
     * @param screenX Touch x
     * @param screenY Touch y
     * @return Key number
     */
    private int touchToKey(int screenX, int screenY) {
        // Bind touch to key
        int key;
        if (screenX < Gdx.graphics.getWidth() / 3)
            key = Input.Keys.LEFT;
        else if (screenX > Gdx.graphics.getWidth() / 3 * 2)
            key = Input.Keys.RIGHT;
        else if (screenY < Gdx.graphics.getHeight() / 2)
            key = Input.Keys.UP;
        else
            key = Input.Keys.DOWN;

        return key;
    }

    private static HashMap<Integer, Vector2> touchMap = new HashMap<Integer, Vector2>();

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            touchMap.put(pointer, new Vector2(screenX, screenY));

            // Bind to key
            keyDown(touchToKey(screenX, screenY));
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            // Bind touch to key
            Vector2 downCoord = touchMap.get(pointer);
            keyUp(touchToKey((int) downCoord.x, (int) downCoord.y));

            touchMap.remove(pointer);
        }

        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

    /**
     * Function, checks player touch's some bodies
     *
     * @return Player grounded bool
     */
    public boolean isPlayerGrounded() {
        // Check all contacts
        Array<Contact> contactList = world.getContactList();
        for (int i = 0; i < contactList.size; i++) {
            Contact contact = contactList.get(i);
            // Check touch to sensor fixture
            if (contact.isTouching() &&
                    (physic.body.getFixtureList().contains(contact.getFixtureA(), false) ||
                            physic.body.getFixtureList().contains(contact.getFixtureB(), false))) {

                // Check below
                boolean below = true;
                WorldManifold manifold = contact.getWorldManifold();
                for (Vector2 mPoint : manifold.getPoints()) {
                    below &= (mPoint.y < getPosition().y);
                }
                return below;
            }
        }
        return false;
    }

    public Vector2 getPosition() {
        return physic.body.getPosition();
    }

    public float getAngle() {
        return physic.body.getAngle();
    }

    /**
     * Player state, e.g. walk, jump, slide etc.
     */
    class PlayerMode
            implements PlayerListener {
        public PlayerMode(
                PlayerModeId selfId,
                AnimationNum[] animationNums,
                PlayerModeListener listener
        ) {
            // Self id
            this.selfId = selfId;

            // Animation nums
            if (animationNums.length == 1 || // One animation
                    animationNums.length == 3) // Start, loop and end animations
                this.animationNums = animationNums;
            else
                throw new AssertionError("Animation nums array length != 1 or 3");

            // Listener
            this.listener = listener;
        }

        private PlayerModeListener listener;

        boolean checkEnd() {
            // Check end
            boolean checkResult = listener.checkEnd();
            if (checkResult) {
                if (isStartLoopEndAnimations())
                    // Start end animation
                    spriter.player.setAnimation(
                            animationNums[AnimationNumId.END.ordinal()].ordinal()
                    );
                else
                    // End mode
                    endMode();
            }

            return checkResult;
        }

        private void endMode() {
            // Set next mode
            PlayerModeId nextModeId = listener.endMode();
            if (nextModeId != null)
                modes[nextModeId.ordinal()].set();
        }

        /**
         * Id in modes array
         */
        final PlayerModeId selfId;

        /**
         * Animation nums
         */
        AnimationNum[] animationNums;

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

        /**
         * Changes modeId to self id
         */
        void set() {
            // Animation
            spriter.player.setAnimation(
                    animationNums[(isStartLoopEndAnimations()
                            ? AnimationNumId.START
                            : AnimationNumId.LOOP
                    ).ordinal()].ordinal());

            // Remove old player listener
            spriter.player.removeListener(getMode());

            // Change mode
            modeId = selfId;

            // Add player listener
            spriter.player.addListener(getMode());
        }

        @Override
        public void animationFinished(Animation animation) {
            if (isStartLoopEndAnimations()) {
                // Check end of start animation
                if (animation.id == animationNums[AnimationNumId.START.ordinal()].ordinal()) {
                    // Start loop animation
                    spriter.player.setAnimation(
                            animationNums[AnimationNumId.LOOP.ordinal()].ordinal()
                    );
                }

                // Check end of end animation
                else if (animation.id == animationNums[AnimationNumId.END.ordinal()].ordinal()) {
                    // To next mode
                    endMode();
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
    }

    /**
     * Animation nums ids
     */
    enum AnimationNumId {
        LOOP,
        START,
        END
    }

    interface PlayerModeListener {
        /**
         * Checks end of mode
         * Calling in every act
         *
         * @return Mode end bool
         */
        boolean checkEnd();


        /**
         * @return Mode, that will be setted after end previous
         */
        PlayerModeId endMode();
    }
}

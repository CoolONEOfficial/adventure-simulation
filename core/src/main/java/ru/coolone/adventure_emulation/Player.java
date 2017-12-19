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
import com.uwsoft.editor.renderer.components.physics.PhysicsBodyComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import java.util.ArrayList;
import java.util.HashMap;

import ru.coolone.adventure_emulation.PersonModeAdapter.PlayerModeListener;
import ru.coolone.adventure_emulation.Player.PlayerModeId;

/**
 * Player behavior class
 *
 * @author coolone
 */

public class Player extends Person<PlayerModeId>
        implements InputProcessor {

    private static final String TAG = Player.class.getSimpleName();

    public Player(World world) {
        this.world = world;
        modeId = PlayerModeId.IDLE;
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
        JUMP_END
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
        BREAK,

        COUNT
    }

    public static final PersonMode[] modes = new PersonMode[]{
            new PersonMode<PlayerModeId>(
                    PlayerModeId.IDLE,
                    new AnimationNum[]{
                            AnimationNum.IDLE
                    },
                    new boolean[]{
                            true, // Idle
                            true, // Walk
                            true, // Jump
                            true, // Slide
                            true, // Crouch
                            true  // Break
                    }
            ),
            new PersonMode<PlayerModeId>( // WALK
                    PlayerModeId.WALK,
                    new AnimationNum[]{
                            AnimationNum.WALK
                    },
                    new boolean[]{
                            true, // Idle
                            true, // Walk
                            true, // Jump
                            true, // Slide
                            true, // Crouch
                            true  // Break
                    }
            ),
            new PersonMode<PlayerModeId>( // JUMP
                    PlayerModeId.JUMP,
                    new AnimationNum[]{
                            AnimationNum.JUMP_LOOP,
                            AnimationNum.JUMP_START,
                            AnimationNum.JUMP_END
                    },
                    new boolean[]{
                            true,  // Idle
                            false, // Walk
                            true,  // Jump
                            false, // Slide
                            false, // Crouch
                            false  // Break
                    }
            ),
            new PersonMode<PlayerModeId>( // SLIDE
                    PlayerModeId.SLIDE,
                    new AnimationNum[]{
                            AnimationNum.SLIDE_LOOP,
                            AnimationNum.SLIDE_START,
                            AnimationNum.SLIDE_END
                    },
                    new boolean[]{
                            true,  // Idle
                            false, // Walk
                            false, // Jump
                            true,  // Slide
                            true,  // Crouch
                            false  // Break
                    }
            ),
            new PersonMode<PlayerModeId>( // CROUCH
                    PlayerModeId.CROUCH,
                    new AnimationNum[]{
                            AnimationNum.CROUCH_LOOP,
                            AnimationNum.CROUCH_START,
                            AnimationNum.CROUCH_END
                    },
                    new boolean[]{
                            true,  // Idle
                            false, // Walk
                            false, // Jump
                            false, // Slide
                            true,  // Crouch
                            false  // Break
                    }
            ),
            new PersonMode<PlayerModeId>( // BREAK
                    PlayerModeId.BREAK,
                    new AnimationNum[]{
                            AnimationNum.BREAK_LOOP,
                            AnimationNum.BREAK_START,
                            AnimationNum.BREAK_END
                    },
                    new boolean[]{
                            true, // Idle
                            true, // Walk
                            true, // Jump
                            true, // Slide
                            true, // Crouch
                            true  // Break
                    }
            )
    };

    public PersonModeAdapter[] modeAdapters = new PersonModeAdapter[]{
            new PersonModeAdapter( // IDLE
                    this,
                    modes[PlayerModeId.IDLE.ordinal()],
                    new PlayerModeListener()
            ),
            new PersonModeAdapter( // WALK
                    this,
                    modes[PlayerModeId.WALK.ordinal()],
                    new PlayerModeListener() {
                        @Override
                        public boolean checkEnd() {
                            return physic.body.getLinearVelocity().x == 0;
                        }

                        @Override
                        public PlayerModeId getNextMode() {
                            return physic.body.getLinearVelocity().x == 0
                                    ? PlayerModeId.IDLE
                                    : PlayerModeId.BREAK;
                        }

                        @Override
                        void onSet() {
                            boolean leftPressed =
                                    downKeys.contains(Input.Keys.LEFT);
                            boolean rightPressed =
                                    downKeys.contains(Input.Keys.RIGHT);

                            if (leftPressed ||
                                    rightPressed) {
                                // Start moving to..
                                move = (leftPressed)
                                        ? MoveDirection.LEFT   // ..left
                                        : MoveDirection.RIGHT; // ..right

                                // Flip image
                                if ((leftPressed && !flipped) ||
                                        (rightPressed && flipped)) {
                                    spriter.player.flipX();
                                    flipped = leftPressed;
                                }
                            }
                        }
                    }
            ),
            new PersonModeAdapter( // JUMP
                    this,
                    modes[PlayerModeId.JUMP.ordinal()],
                    new PlayerModeListener() {
                        @Override
                        public boolean checkEnd() {
                            return spriter.player.getAnimation().id == AnimationNum.JUMP_LOOP.ordinal() &&
                                    isPlayerGrounded();
                        }

                        @Override
                        public PlayerModeId getNextMode() {
                            return physic.body.getLinearVelocity().x == 0
                                    ? PlayerModeId.IDLE
                                    : PlayerModeId.BREAK;
                        }

                        @Override
                        void onSet() {
                            // Apply jump impulse
                            physic.body.applyLinearImpulse(
                                    new Vector2(0, JUMP_VELOCITY),
                                    physic.body.getPosition(),
                                    true
                            );
                        }
                    }
            ),
            new PersonModeAdapter( // SLIDE
                    this,
                    modes[PlayerModeId.SLIDE.ordinal()],
                    new PlayerModeListener() {
                        @Override
                        public boolean checkEnd() {
                            return physic.body.getLinearVelocity().x == 0;
                        }

                        @Override
                        public PlayerModeId getNextMode() {
                            return PlayerModeId.IDLE;
                        }

                        @Override
                        void onSet() {
                            // Stop moving
                            move = MoveDirection.NONE;
                        }
                    }
            ),
            new PersonModeAdapter( // CROUCH
                    this,
                    modes[PlayerModeId.CROUCH.ordinal()],
                    new PlayerModeListener() {
                        @Override
                        public PlayerModeId getNextMode() {
                            return PlayerModeId.IDLE;
                        }
                    }
            ),
            new PersonModeAdapter( // BREAK
                    this,
                    modes[PlayerModeId.BREAK.ordinal()],
                    new PlayerModeListener() {
                        @Override
                        public boolean checkEnd() {
                            return physic.body.getLinearVelocity().x == 0;
                        }

                        @Override
                        public PlayerModeId getNextMode() {
                            return PlayerModeId.IDLE;
                        }
                    }
            )
    };

    @Override
    PersonModeAdapter[] getModeAdapters() {
        return modeAdapters;
    }

    @Override
    SpriterComponent getSpriter() {
        return spriter;
    }

    public PlayerModeId getModeId() {
        return modeId;
    }

    @Override
    PersonMode[] getModes() {
        return modes;
    }

    public PersonMode getCurrentMode() {
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

    private static final int JUMP_VELOCITY = 1000;
    private static final int MOVE_VELOCITY = 50;

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
            if (modeId != null)
                getCurrentModeAdapter().checkEnd();
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

    ArrayList<Integer> downKeys = new ArrayList<Integer>();

    @Override
    public boolean keyDown(int keycode) {
        Gdx.app.log(TAG, "Key down (" + keycode + ')');

        downKeys.add(keycode);

        // Change modeId (and/or) animation

        // Create
        PlayerModeId newModeId = null;

        // Set
        switch (keycode) {
            case Input.Keys.LEFT:
            case Input.Keys.RIGHT:
                if (move == MoveDirection.NONE) {
                    // Mode
                    newModeId = PlayerModeId.WALK;
                }
                break;
            case Input.Keys.UP:
                // Jump
                newModeId = PlayerModeId.JUMP;
                break;
            case Input.Keys.DOWN:
                newModeId = physic.body.getLinearVelocity().x != 0
                        ? PlayerModeId.SLIDE
                        : PlayerModeId.CROUCH;
                break;
        }

        // Apply
        if (newModeId != null)
            getModeAdapters()[newModeId.ordinal()].set();

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        Gdx.app.log(TAG, "Key up (" + keycode + ')');

        downKeys.remove(downKeys.indexOf(keycode));

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
            getCurrentModeAdapter().toNextMode();
        }

        // Stop crouch
        switch (keycode) {
            case Input.Keys.DOWN:
                // Change animation
                if (modeId == PlayerModeId.CROUCH)
                    spriter.player.setAnimation(
                            getCurrentMode().animationNums[AnimationNumId.END.ordinal()].ordinal()
                    );
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
     * Animation nums ids
     */
    enum AnimationNumId {
        LOOP,
        START,
        END
    }
}


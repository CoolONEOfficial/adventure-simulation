package ru.coolone.adventure_emulation;

import com.badlogic.ashley.core.Component;
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

import java.util.Collection;
import java.util.HashMap;

/**
 * Created by coolone on 13.12.17.
 */

public class Player implements InputProcessor, PlayerListener {

    private static final String TAG = Player.class.getSimpleName();

    public Player(World world) {
        this.world = world;
    }

    /**
     * Box2d world
     */
    World world;

    /**
     * Spriter animation ids
     */
    public enum AnimationId {
        IDLE,
        WALK,
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
        DEFAULT,
        JUMP,
        SLIDE,
        CROUCH
    }
    public static final PlayerMode[] modes = new PlayerMode[] {
            new PlayerMode(),
            new PlayerMode( // JUMP
                    true,
                    false
            ),
            new PlayerMode( // SLIDE
                    false,
                    false
            ),
            new PlayerMode( // CROUCH
                    false,
                    false
            )
    };

    private PlayerModeId modeId = PlayerModeId.DEFAULT;

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
     * Mirror image by X flag
     */
    private boolean flippedX = false;

    /**
     * Player entity
     */
    private Entity entity;

    public static final int JUMP_VELOCITY = 1000;
    public static final int MOVE_VELOCITY = 50;
    public static final int MIN_SLIDE_VELOCITY = 0;

    public class CompositeScript implements IScript {

        @Override
        public void init(Entity entity) {
            Player.this.entity = entity;

            physic = ComponentRetriever.get(entity, PhysicsBodyComponent.class);

            Collection<Component> components = ComponentRetriever.getComponents(entity);
            for (Component mComponent : components)
                Gdx.app.log(TAG, "mComponent:" + mComponent.getClass().getSimpleName());
            Gdx.app.log(TAG, "Components count: " + ComponentRetriever.getComponents(entity).size());
        }

        @Override
        public void act(float delta) {
            // FIXME: 13.12.17
            if (!physic.body.isFixedRotation())
                physic.body.setFixedRotation(true);

            if (modeId != PlayerModeId.SLIDE)
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

            // Check end of modeId
            switch (modeId) {
                case JUMP:
                    if (spriter.player.getAnimation().id == AnimationId.JUMP_LOOP.ordinal() &&
                            isPlayerGrounded(20))
                        spriter.player.setAnimation(AnimationId.JUMP_END.ordinal());
                    break;
                case SLIDE:
                    if (Math.abs(physic.body.getLinearVelocity().x) <= MIN_SLIDE_VELOCITY)
                        spriter.player.setAnimation(AnimationId.SLIDE_END.ordinal());
                    break;
            }
        }

        @Override
        public void dispose() {

        }
    }

    public class SpriterScript implements IScript {

        @Override
        public void init(Entity entity) {
            spriter = ComponentRetriever.get(entity, SpriterComponent.class);
            spriter.player.addListener(Player.this);

            Collection<Component> components = ComponentRetriever.getComponents(entity);
            for (Component mComponent : components)
                Gdx.app.log(TAG, "mComponent:" + mComponent.getClass().getSimpleName());
            Gdx.app.log(TAG, "Components count: " + ComponentRetriever.getComponents(entity).size());
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
        AnimationId newAnimationId = null;

        // Set
        switch (keycode) {
            case Input.Keys.LEFT:
            case Input.Keys.RIGHT:
                if (move == MoveDirection.NONE &&
                        getMode().movable) {
                    // Move to left
                    move = (keycode == Input.Keys.LEFT)
                            ? MoveDirection.LEFT
                            : MoveDirection.RIGHT;

                    // Flip image
                    if ((keycode == Input.Keys.LEFT && !flippedX) ||
                            (keycode == Input.Keys.RIGHT && flippedX)) {
                        spriter.player.flipX();
                        flippedX = keycode == Input.Keys.LEFT;
                    }

                    // Change animation
                    if (modeId == PlayerModeId.DEFAULT)
                        newAnimationId = AnimationId.WALK;
                }
                break;
            case Input.Keys.UP:
                if (getMode().jumpable) {
                    // Jump physic body
                    physic.body.applyLinearImpulse(
                            new Vector2(0, JUMP_VELOCITY),
                            physic.body.getPosition(),
                            true
                    );

                    // Jump
                    newModeId = PlayerModeId.JUMP;
                    newAnimationId = AnimationId.JUMP_START;
                }
                break;
            case Input.Keys.DOWN:
                if (modeId == PlayerModeId.DEFAULT)
                    if (move == MoveDirection.RIGHT ||
                            move == MoveDirection.LEFT) {
                        // Slide
                        newModeId = PlayerModeId.SLIDE;
                        newAnimationId = AnimationId.SLIDE_START;
                    } else {
                        // Crouch
                        newModeId = PlayerModeId.CROUCH;
                        newAnimationId = AnimationId.CROUCH_START;
                    }
                break;
        }

        // Apply
        if(newModeId != null)
            modeId = newModeId;
        if(newAnimationId != null)
        spriter.player.setAnimation(newAnimationId.ordinal());

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

            // Change animation
            if (modeId == PlayerModeId.DEFAULT)
                spriter.player.setAnimation(AnimationId.IDLE.ordinal());
        }

        // Stop crouch
        switch (keycode) {
            case Input.Keys.DOWN:
                // Change animation
                if(modeId == PlayerModeId.CROUCH)
                    spriter.player.setAnimation(AnimationId.CROUCH_END.ordinal());
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

    @Override
    public void animationFinished(Animation animation) {
        AnimationId finishedId = AnimationId.values()[animation.id];
        AnimationId startId = null;

        // Start loop animations
        switch (finishedId) {
            case JUMP_START:
                startId = AnimationId.JUMP_LOOP;
                break;
            case SLIDE_START:
                startId = AnimationId.SLIDE_LOOP;
                break;
            case CROUCH_START:
                startId = AnimationId.CROUCH_LOOP;
                break;
        }
        if (startId != null)
            spriter.player.setAnimation(startId.ordinal());

        // Bind end animations
        switch (finishedId) {
            case JUMP_END:
            case SLIDE_END:
            case CROUCH_END:
                // To default modeId
                modeId = PlayerModeId.DEFAULT;

                // Update animation
                updateAnimation();

                // Handle pressed keys
                for (int mKeyId: Platformer.pressedKeys) {
                    keyDown(mKeyId);
                }
                break;
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
     * Function, checks player touch's some bodies
     *
     * @return Player grounded bool
     */
    public boolean isPlayerGrounded() {
        return isPlayerGrounded(0f);
    }

    public boolean isPlayerGrounded(float offset) {
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
                    below &= (mPoint.y < getPosition().y + offset);
                }
//                for (int mContactId = 0; mContactId < manifold.getNumberOfContactPoints(); mContactId++) {
//                    below &= (manifold.getPoints()[mContactId].y < getPosition().y);
//                }
//                if (below) {
//                    Fixture groundFixture = null;
//                    if (contact.getFixtureA().getUserData() != null
//                            && contact.getFixtureA().getUserData().equals("p")) {
//                        groundFixture = contact.getFixtureA();
//                    }
//
//                    if (contact.getFixtureB().getUserData() != null
//                            && contact.getFixtureB().getUserData().equals("p")) {
//                        groundFixture = contact.getFixtureB();
//                    }
//
//                    if (groundFixture != null) {
//                        Gdx.app.log(TAG, "Griund fixture angle: " + groundFixture.getBody().getAngle());
//
//                        // Set angle
//                        spriter.player.setAngle(groundFixture.getBody().getAngle());
//                    }
//                }
                return below;
            }
        }
        return false;
    }

    private void updateAnimation() {
        // Get animation id
        AnimationId animationId;
        switch (modeId) {
            case JUMP:
                if (spriter.player.getAnimation().id == AnimationId.JUMP_START.ordinal())
                    animationId = AnimationId.JUMP_START;
                else
                    animationId = AnimationId.JUMP_LOOP;
                break;
            default:
                switch (move) {
                    case RIGHT:
                    case LEFT:
                        animationId = AnimationId.WALK;
                        break;
                    default:
                        animationId = AnimationId.IDLE;
                }
                break;
        }

        // Set animation id
        spriter.player.setAnimation(animationId.ordinal());
    }

    public Vector2 getPosition() {
        return physic.body.getPosition();
    }

    public float getAngle() {
        return physic.body.getAngle();
    }
}

class PlayerMode {
    public PlayerMode() {
        this(true, true);
    }

    public PlayerMode(
            boolean movable,
            boolean jumpable
    ) {
        this.movable = movable;
        this.jumpable = jumpable;
    }

    public boolean movable;
    public boolean jumpable;
}

package ru.coolone.platformer;

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
        CROUCH_DOWN,
        STAND_UP,
        CROUCH_IDLE,
        JUMP_START,
        FALL_START,
        JUMP_LOOP,
        HIT_0
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
     * Mode
     */
    public enum Mode {
        DEFAULT,
        JUMP,
        SLIDE
    }

    private Mode mode = Mode.DEFAULT;

    public Mode getMode() {
        return mode;
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

    public static final int MIN_SLIDE_VELOCITY = 0;
    public static final int JUMP_VELOCITY = 1000;
    public static final int MOVE_VELOCITY = 50;

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

            if(mode != Mode.SLIDE)
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

            // Check end of mode
            boolean end = false;
            switch (mode) {
                case JUMP:
                    end = spriter.player.getAnimation().id == AnimationId.JUMP_LOOP.ordinal() &&
                            isPlayerGrounded();
                    break;
                case SLIDE:
                    end = Math.abs(physic.body.getLinearVelocity().x) <= MIN_SLIDE_VELOCITY;
                    break;
            }
            if(end) {
                // To default mode
                mode = Mode.DEFAULT;

                // Update animation
                updateAnimation();
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

        switch (keycode) {
            case Input.Keys.LEFT:
            case Input.Keys.RIGHT:

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
                if (mode == Mode.DEFAULT)
                    spriter.player.setAnimation(AnimationId.WALK.ordinal());
                break;
            case Input.Keys.UP:
                if (mode == Mode.DEFAULT) {
                    // Jump
                    physic.body.applyLinearImpulse(
                            new Vector2(0, JUMP_VELOCITY),
                            physic.body.getPosition(),
                            true
                    );

                    // Jump mode
                    mode = Mode.JUMP;

                    // Change animation
                    spriter.player.setAnimation(AnimationId.JUMP_START.ordinal());
                }
                break;
            case Input.Keys.DOWN:
                if (mode == Mode.DEFAULT &&
                        (Gdx.input.isKeyPressed(Input.Keys.RIGHT)
                                || Gdx.input.isKeyPressed(Input.Keys.LEFT))) {
                    // Slide mode
                    mode = Mode.SLIDE;

                    // Change animation
                    spriter.player.setAnimation(AnimationId.SLIDE_START.ordinal());
                }
                break;
        }

        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        Gdx.app.log(TAG, "Key up (" + keycode + ')');

        switch (keycode) {
            case Input.Keys.RIGHT:
            case Input.Keys.LEFT:
                // Stop moving
                move = MoveDirection.NONE;

                // Change animation
                if (mode == Mode.DEFAULT)
                    spriter.player.setAnimation(AnimationId.IDLE.ordinal());
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
     * @param screenX Touch x
     * @param screenY Touch y
     * @return Key number
     */
    private int touchToKey(int screenX, int screenY) {
        if (screenX < Gdx.graphics.getWidth() / 3)
            return Input.Keys.LEFT;
        else if (screenX > Gdx.graphics.getWidth() / 3 * 2)
            return Input.Keys.RIGHT;
        else return Input.Keys.UP;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            // Bind to key
            keyDown(touchToKey(screenX, screenY));
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if(Gdx.app.getType() == Application.ApplicationType.Android) {
            // Bind touch to key
            keyUp(touchToKey(screenX, screenY));
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
        // Start loop animation
        if (animation.id == AnimationId.JUMP_START.ordinal()) { // for jump
            spriter.player.setAnimation(AnimationId.JUMP_LOOP.ordinal());
        } else if(animation.id == AnimationId.SLIDE_START.ordinal()) { // for slide
            spriter.player.setAnimation(AnimationId.SLIDE_LOOP.ordinal());
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
        switch (mode) {
            case JUMP:
                if (spriter.player.getAnimation().id == AnimationId.JUMP_START.ordinal())
                    animationId = AnimationId.JUMP_START;
                else
                    animationId = AnimationId.JUMP_LOOP;
                break;
            default:
                if (Gdx.input.isKeyPressed(Input.Keys.LEFT) ||
                        Gdx.input.isKeyPressed(Input.Keys.RIGHT))
                    animationId = AnimationId.WALK;
                else
                    animationId = AnimationId.IDLE;
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

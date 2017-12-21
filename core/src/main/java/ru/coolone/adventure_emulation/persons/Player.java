package ru.coolone.adventure_emulation.persons;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.WorldManifold;
import com.badlogic.gdx.utils.Array;
import com.uwsoft.editor.renderer.components.physics.PhysicsBodyComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.scripts.IScript;
import com.uwsoft.editor.renderer.utils.ComponentRetriever;

import ru.coolone.adventure_emulation.InputGroups;
import ru.coolone.adventure_emulation.InputGroups.InputGroupId;
import ru.coolone.adventure_emulation.person.Person;
import ru.coolone.adventure_emulation.person.PersonModeAdapter;
import ru.coolone.adventure_emulation.person.PersonModeAdapter.AnimationNumId;
import ru.coolone.adventure_emulation.person.PersonModeAdapter.PersonModeListener;
import ru.coolone.adventure_emulation.person.PersonModeData;
import ru.coolone.adventure_emulation.persons.Player.ModeId;

/**
 * Player behavior class
 *
 * @author coolone
 */

public class Player extends Person<ModeId>
        implements InputGroups.InputGroupsListener {

    private static final String TAG = Player.class.getSimpleName();
    /**
     * Move body velocities for movable modes
     *
     * @see PersonModeData
     */
    private static final float IDLE_MOVE_VELOCITY = 50f;
    private static final float IDLE_MOVE_MAX_VELOCITY = 10f;
    private static final float JUMP_MOVE_VELOCITY = 25f;
    private static final float JUMP_MOVE_MAX_VELOCITY = 10f;
    private static final float CROUCH_MOVE_VELOCITY = 50f;
    private static final float CROUCH_MOVE_MAX_VELOCITY = 6f;
    /**
     * Data, that will be using in @{@link PersonModeAdapter}
     *
     * @see PersonModeAdapter
     */
    private static final PersonModeData[] modes = new PersonModeData[]{
            new PersonModeData<ModeId, AnimationNum>(
                    ModeId.IDLE,
                    new AnimationNum[]{
                            AnimationNum.IDLE
                    },
                    new boolean[]{
                            true, // Idle
                            true, // Walk
                            true, // Jump
                            true, // Slide
                            true, // Crouch
                            true, // Crouch walk
                            true  // Break
                    }
            ),
            new PersonModeData<ModeId, AnimationNum>( // WALK
                    ModeId.WALK,
                    IDLE_MOVE_VELOCITY,
                    IDLE_MOVE_MAX_VELOCITY,
                    new AnimationNum[]{
                            AnimationNum.WALK
                    },
                    new boolean[]{
                            true, // Idle
                            true, // Walk
                            true, // Jump
                            true, // Slide
                            true, // Crouch
                            true, // Crouch walk
                            true  // Break
                    }
            ),
            new PersonModeData<ModeId, AnimationNum>( // JUMP
                    ModeId.JUMP,
                    JUMP_MOVE_VELOCITY,
                    JUMP_MOVE_MAX_VELOCITY,
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
                            true, // Crouch walk
                            false  // Break
                    }
            ),
            new PersonModeData<ModeId, AnimationNum>( // SLIDE
                    ModeId.SLIDE,
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
                            true,  // Crouch walk
                            false  // Break
                    }
            ),
            new PersonModeData<ModeId, AnimationNum>( // CROUCH
                    ModeId.CROUCH,
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
                            true,  // Crouch walk
                            false  // Break
                    }
            ),
            new PersonModeData<ModeId, AnimationNum>( // CROUCH_WALK
                    ModeId.CROUCH_WALK,
                    CROUCH_MOVE_VELOCITY,
                    CROUCH_MOVE_MAX_VELOCITY,
                    new AnimationNum[]{
                            AnimationNum.CROUCH_WALK
                    },
                    new boolean[]{
                            true, // Idle
                            true, // Walk
                            true, // Jump
                            true, // Slide
                            true, // Crouch
                            true, // Crouch walk
                            true  // Break
                    }
            ),
            new PersonModeData<ModeId, AnimationNum>( // BREAK
                    ModeId.BREAK,
                    new AnimationNum[]{
                            AnimationNum.BREAK_LOOP,
                            AnimationNum.BREAK_START,
                            AnimationNum.BREAK_END
                    },
                    new boolean[]{
                            true,  // Idle
                            true,  // Walk
                            true,  // Jump
                            true,  // Slide
                            true,  // Crouch
                            false, // Crouch walk
                            true   // Break
                    }
            )
    };
    /**
     * Jump body velocity
     */
    private static final float JUMP_VELOCITY = 1000f;
    /**
     * Box2d world
     */
    private World world;
    private MoveDirection move = MoveDirection.NONE;
    /**
     * Components
     */
    private PhysicsBodyComponent physic;
    private SpriterComponent spriter;
    private PersonModeAdapter[] modeAdapters = new PersonModeAdapter[]{
            new PersonModeAdapter<ModeId>( // IDLE
                    this,
                    modes[ModeId.IDLE.ordinal()],
                    new PersonModeListener<ModeId>() {
                    }
            ),
            new PersonModeAdapter<ModeId>( // WALK
                    this,
                    modes[ModeId.WALK.ordinal()],
                    new PersonModeListener<ModeId>() {
                        @Override
                        protected boolean checkEnd() {
                            return physic.body.getLinearVelocity().x == 0;
                        }

                        @Override
                        public ModeId getNextModeId() {
                            return physic.body.getLinearVelocity().x == 0
                                    ? ModeId.IDLE
                                    : ModeId.BREAK;
                        }
                    }
            ),
            new PersonModeAdapter<ModeId>( // JUMP
                    this,
                    modes[ModeId.JUMP.ordinal()],
                    new PersonModeListener<ModeId>() {
                        @Override
                        public boolean checkEnd() {
                            return spriter.player.getAnimation().id == AnimationNum.JUMP_LOOP.ordinal() &&
                                    isPlayerGrounded();
                        }

                        @Override
                        public ModeId getNextModeId() {
                            return physic.body.getLinearVelocity().x == 0
                                    ? ModeId.IDLE
                                    : ModeId.BREAK;
                        }

                        @Override
                        protected void onSet() {
                            // Apply jump impulse
                            physic.body.applyLinearImpulse(
                                    new Vector2(0, JUMP_VELOCITY),
                                    physic.body.getPosition(),
                                    true
                            );
                        }
                    }
            ),
            new PersonModeAdapter<ModeId>( // SLIDE
                    this,
                    modes[ModeId.SLIDE.ordinal()],
                    new PersonModeListener<ModeId>() {
                        @Override
                        public boolean checkEnd() {
                            return physic.body.getLinearVelocity().x == 0;
                        }

                        @Override
                        public ModeId getNextModeId() {
                            return InputGroups.getActiveGroups().contains(InputGroupId.DOWN)
                                    ? ModeId.CROUCH
                                    : ModeId.IDLE;
                        }

                        @Override
                        protected void onSet() {
                            // Stop moving
                            move = MoveDirection.NONE;
                        }
                    }
            ),
            new PersonModeAdapter<ModeId>( // CROUCH
                    this,
                    modes[ModeId.CROUCH.ordinal()],
                    new PersonModeListener<ModeId>() {
                        @Override
                        public ModeId getNextModeId() {
                            return ModeId.IDLE;
                        }
                    }
            ),
            new PersonModeAdapter<ModeId>( // CROUCH_WALK
                    this,
                    modes[ModeId.CROUCH_WALK.ordinal()],
                    new PersonModeListener<ModeId>() {
                        @Override
                        public ModeId getNextModeId() {
                            return physic.body.getLinearVelocity().x == 0 ?
                                    (
                                            InputGroups.getActiveGroups().contains(InputGroupId.DOWN)
                                                    ? ModeId.CROUCH
                                                    : ModeId.IDLE
                                    )
                                    : ModeId.SLIDE;
                        }
                    }
            ),
            new PersonModeAdapter<ModeId>( // BREAK
                    this,
                    modes[ModeId.BREAK.ordinal()],
                    new PersonModeListener<ModeId>() {
                        @Override
                        public boolean checkEnd() {
                            return physic.body.getLinearVelocity().x == 0;
                        }

                        @Override
                        public ModeId getNextModeId() {
                            return ModeId.IDLE;
                        }
                    }
            )
    };
    /**
     * Horizontal flipped image flag
     */
    private boolean flipped = false;

    public Player(World world) {
        this.world = world;
        modeId = ModeId.IDLE;
    }

    public MoveDirection getMove() {
        return move;
    }

    @Override
    public PersonModeAdapter[] getModeAdapters() {
        return modeAdapters;
    }

    @Override
    public SpriterComponent getSpriter() {
        return spriter;
    }

    @Override
    public PhysicsBodyComponent getPhysic() {
        return physic;
    }

    public ModeId getModeId() {
        return modeId;
    }

    @Override
    public PersonModeData[] getModes() {
        return modes;
    }

    @Override
    public PersonModeData getCurrentMode() {
        return modes[modeId.ordinal()];
    }

    @Override
    public void onInputGroupActivate(InputGroupId groupId) {
        // Change modeId (and/or) animation
        ModeId newModeId = null;
        switch (groupId) {
            case LEFT:
            case RIGHT:
                // Walk
                if (move == MoveDirection.NONE) {
                    newModeId = getModeId() == ModeId.CROUCH
                            ? ModeId.CROUCH_WALK
                            : ModeId.WALK;
                }
                break;
            case UP:
                // Jump
                newModeId = ModeId.JUMP;
                break;
            case DOWN:
                // Crouch
                newModeId = physic.body.getLinearVelocity().x != 0
                        ? ModeId.SLIDE
                        : ModeId.CROUCH;
                break;
        }

        // Activate newModeId
        if (newModeId != null)
            getModeAdapters()[newModeId.ordinal()].activate();

        if (getCurrentMode().movable) {
            // Check move
            boolean leftPressed =
                    InputGroups.getActiveGroups().contains(InputGroupId.LEFT);
            boolean rightPressed =
                    InputGroups.getActiveGroups().contains(InputGroupId.RIGHT);

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

    @Override
    public void onInputGroupDeactivate(InputGroupId groupId) {

        switch (groupId) {
            case LEFT:
            case RIGHT:
                if ((groupId == InputGroupId.RIGHT && move == MoveDirection.RIGHT) ||
                        (groupId == InputGroupId.LEFT && move == MoveDirection.LEFT)) {
                    // Stop moving
                    move = MoveDirection.NONE;

                    // End mode
                    getCurrentModeAdapter().toNextMode();
                }
                break;
            case DOWN:
                // Change animation
                if (modeId == ModeId.CROUCH)
                    spriter.player.setAnimation(
                            getCurrentMode().animationNums[AnimationNumId.END.ordinal()].ordinal()
                    );
                break;
        }
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
        CROUCH_WALK,
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

    /**
     * Person mode ids
     */
    public enum ModeId {
        IDLE,
        WALK,
        JUMP,
        SLIDE,
        CROUCH,
        CROUCH_WALK,
        BREAK
    }

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

            final PersonModeData currentMode = getCurrentMode();

            // Move
            if (currentMode.movable) {
                // Apply move
                switch (move) {
                    case LEFT:
                        physic.body.applyLinearImpulse(
                                new Vector2(-currentMode.moveVelocity, 0),
                                physic.body.getPosition(),
                                true
                        );
                        break;
                    case RIGHT:
                        physic.body.applyLinearImpulse(
                                new Vector2(currentMode.moveVelocity, 0),
                                physic.body.getPosition(),
                                true
                        );
                        break;
                }

                // Limit velocity
                if (Math.abs(physic.body.getLinearVelocity().x) > currentMode.moveMaxVelocity)
                    physic.body.setLinearVelocity(
                            new Vector2(
                                    physic.body.getLinearVelocity()
                            ) {{
                                x = (physic.body.getLinearVelocity().x > 0)
                                        ? currentMode.moveMaxVelocity
                                        : -currentMode.moveMaxVelocity;
                            }}
                    );
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
}

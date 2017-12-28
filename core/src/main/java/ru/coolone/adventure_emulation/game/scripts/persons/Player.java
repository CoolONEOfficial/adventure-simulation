package ru.coolone.adventure_emulation.game.scripts.persons;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

import ru.coolone.adventure_emulation.GameCore;
import ru.coolone.adventure_emulation.game.scripts.person.Person;
import ru.coolone.adventure_emulation.game.scripts.person.PersonMode;
import ru.coolone.adventure_emulation.input.InputGroups;

/**
 * Player behavior to CompositeItem and spriter animation
 */

public class Player extends Person<Player.PlayerModeId, Player.AnimationId>
        implements InputGroups.InputGroupsListener {

    /**
     * @param core Link to @{@link GameCore}
     * @param name Name of CompositeItem
     */
    public Player(GameCore core, String name) {
        super(core, name,
                InputGroups.InputGroupId.MOVE_LEFT,
                InputGroups.InputGroupId.MOVE_RIGHT);

        // Default mode id
        currentModeId = PlayerModeId.IDLE;

        // Listen input
        InputGroups.addListener(this);
    }

    @Override
    protected PersonMode<PlayerModeId, AnimationId>[] getModes() {
        return playerModes;
    }

    /**
     * Animation ids
     */
    public enum AnimationId {
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
     * Id's of @playerModes
     */
    public enum PlayerModeId {
        IDLE,
        WALK,
        BREAK,
        SLIDE,
        CROUCH,
        CROUCH_WALK,
        JUMP
    }

    /**
     * Move speed constants for @{@link PersonMode}'s
     */
    private static final float WALK_MOVE_ACCELERATION = 3000f;
    private static final float WALK_MOVE_VELOCITY = 15f;

    private static final float CROUCH_WALK_MOVE_ACCELERATION = 3000f;
    private static final float CROUCH_WALK_MOVE_VELOCITY = 6f;

    private static final float JUMP_MOVE_ACCELERATION = 1000f;
    private static final float JUMP_MOVE_VELOCITY = 10f;

    /**
     * Array of @{@link PersonMode}'s
     */
    private PersonMode[] playerModes = new PersonMode[]{

            // IDLE
            new PersonMode<PlayerModeId, AnimationId>(
                    new AnimationId[]{
                            AnimationId.IDLE
                    },
                    new boolean[]{
                            false, // IDLE
                            true, // WALK
                            true, // BREAK
                            true, // SLIDE
                            true, // CROUCH
                            true, // CROUCH_WALK
                            true, // JUMP
                    },
                    new PersonMode.Behavior<PlayerModeId>() {
                        @Override
                        public boolean checkEnd() {
                            return false;
                        }

                        @Override
                        public PlayerModeId getNextModeId() {
                            return null;
                        }
                    }
            ),

            // WALK
            new PersonMode<PlayerModeId, AnimationId>(
                    WALK_MOVE_ACCELERATION, WALK_MOVE_VELOCITY,
                    new AnimationId[]{
                            AnimationId.WALK
                    },
                    new boolean[]{
                            true, // IDLE
                            false, // WALK
                            true, // BREAK
                            true, // SLIDE
                            true, // CROUCH
                            true, // CROUCH_WALK
                            true, // JUMP
                    },
                    new PersonMode.Behavior<PlayerModeId>() {
                        @Override
                        public boolean checkEnd() {
                            return false;
                        }

                        @Override
                        public PlayerModeId getNextModeId() {
                            return PlayerModeId.BREAK;
                        }
                    },
                    new PersonMode.Listener() {
                        @Override
                        protected void onMoveEnded() {
                            super.onMoveEnded();
                            toNextMode(null);
                        }
                    }
            ),

            // BREAK
            new PersonMode<PlayerModeId, AnimationId>(
                    new AnimationId[]{
                            AnimationId.BREAK_LOOP,
                            AnimationId.BREAK_START,
                            AnimationId.BREAK_END
                    },
                    new boolean[]{
                            true, // IDLE
                            true, // WALK
                            false, // BREAK
                            true, // SLIDE
                            true, // CROUCH
                            true, // CROUCH_WALK
                            true, // JUMP
                    },
                    new PersonMode.Behavior<PlayerModeId>() {
                        @Override
                        public boolean checkEnd() {
                            return physic.body.getLinearVelocity().x == 0;
                        }

                        @Override
                        public PlayerModeId getNextModeId() {
                            return PlayerModeId.IDLE;
                        }
                    }
            ),

            // SLIDE
            new PersonMode<PlayerModeId, AnimationId>(
                    new AnimationId[]{
                            AnimationId.SLIDE_LOOP,
                            AnimationId.SLIDE_START,
                            AnimationId.SLIDE_END
                    },
                    new boolean[]{
                            true, // IDLE
                            true, // WALK
                            true, // BREAK
                            false, // SLIDE
                            true, // CROUCH
                            true, // CROUCH_WALK
                            true, // JUMP
                    },
                    new PersonMode.Behavior<PlayerModeId>() {
                        @Override
                        public boolean checkEnd() {
                            return false;
                        }

                        @Override
                        public PlayerModeId getNextModeId() {
                            ArrayList<InputGroups.InputGroupId> activeGroups = InputGroups.getActiveGroups();

                            return activeGroups.contains(InputGroups.InputGroupId.CROUCH)
                                    ? PlayerModeId.CROUCH
                                    : PlayerModeId.IDLE;
                        }
                    }
            ),

            // CROUCH
            new PersonMode<PlayerModeId, AnimationId>(
                    new AnimationId[]{
                            AnimationId.CROUCH_LOOP,
                            AnimationId.CROUCH_START,
                            AnimationId.CROUCH_END
                    },
                    new boolean[]{
                            true, // IDLE
                            true, // WALK
                            true, // BREAK
                            true, // SLIDE
                            false, // CROUCH
                            true, // CROUCH_WALK
                            true, // JUMP
                    },
                    new PersonMode.Behavior<PlayerModeId>() {
                        @Override
                        public boolean checkEnd() {
                            return false;
                        }

                        @Override
                        public PlayerModeId getNextModeId() {
                            return PlayerModeId.IDLE;
                        }
                    }
            ),

            // CROUCH_WALK
            new PersonMode<PlayerModeId, AnimationId>(
                    CROUCH_WALK_MOVE_ACCELERATION, CROUCH_WALK_MOVE_VELOCITY,
                    new AnimationId[]{
                            AnimationId.CROUCH_WALK
                    },
                    new boolean[]{
                            true, // IDLE
                            true, // WALK
                            true, // BREAK
                            true, // SLIDE
                            true, // CROUCH
                            false, // CROUCH_WALK
                            true, // JUMP
                    },
                    new PersonMode.Behavior<PlayerModeId>() {
                        @Override
                        public boolean checkEnd() {
                            return false;
                        }

                        @Override
                        public PlayerModeId getNextModeId() {
                            return PlayerModeId.IDLE;
                        }
                    }
            ),

            // JUMP
            new PersonMode<PlayerModeId, AnimationId>(
                    JUMP_MOVE_ACCELERATION, JUMP_MOVE_VELOCITY,
                    new AnimationId[]{
                            AnimationId.JUMP_LOOP,
                            AnimationId.JUMP_START,
                            AnimationId.JUMP_END
                    },
                    new boolean[]{
                            true, // IDLE
                            false, // WALK
                            true, // BREAK
                            true, // SLIDE
                            true, // CROUCH
                            true, // CROUCH_WALK
                            false, // JUMP
                    },
                    new PersonMode.Behavior<PlayerModeId>() {
                        @Override
                        public boolean checkEnd() {
                            return spriter.spriter.player.getAnimation().id ==
                                    AnimationId.JUMP_LOOP.ordinal() &&
                                    isGrounded();
                        }

                        @Override
                        public PlayerModeId getNextModeId() {
                            return PlayerModeId.IDLE;
                        }
                    },
                    new PersonMode.Listener() {
                        @Override
                        protected void onActivate() {
                            super.onActivate();
                            physic.body.applyLinearImpulse(
                                    new Vector2(
                                            0f,
                                            1000f
                                    ),
                                    physic.body.getPosition(),
                                    true
                            );
                        }
                    }
            )
    };

    @Override
    public void onInputGroupActivate(InputGroups.InputGroupId groupId) {
        PlayerModeId activateModeId = null;

        ArrayList<InputGroups.InputGroupId> activeGroups = InputGroups.getActiveGroups();

        // Check input group
        switch (groupId) {
            case JUMP:
                activateModeId = PlayerModeId.JUMP;
                break;
            case MOVE_LEFT:
            case MOVE_RIGHT:
                activateModeId = getCurrentModeId() == PlayerModeId.CROUCH
                        ? PlayerModeId.CROUCH_WALK
                        : PlayerModeId.WALK;
                break;
            case CROUCH:
                activateModeId = (getMoveDir() != MoveDir.NONE)
                        ? PlayerModeId.SLIDE
                        : PlayerModeId.CROUCH;
                break;
        }

        // Activate @PersonMode
        if (activateModeId != null)
            activateMode(activateModeId, groupId);

        super.onInputGroupActivate(groupId);
    }

    @Override
    public void onInputGroupDeactivate(InputGroups.InputGroupId groupId) {
        super.onInputGroupDeactivate(groupId);

        switch (groupId) {
            case CROUCH:
                switch (getCurrentModeId()) {
                    case CROUCH:
                    case CROUCH_WALK:
                        // Stand up
                        toNextMode(groupId);
                        break;
                }
                break;
        }
    }

    @Override
    public void dispose() {
        super.dispose();

        // Stop listen input
        InputGroups.removeListener(this);
    }

    //    private PersonModeAdapter[] modeAdapters = new PersonModeAdapter[]{
//            new PersonModeAdapter<ModeId>( // IDLE
//                    this,
//                    modes[ModeId.IDLE.ordinal()],
//                    new PersonModeListener<ModeId>() {
//                    }
//            ),
//            new PersonModeAdapter<ModeId>( // WALK
//                    this,
//                    modes[ModeId.WALK.ordinal()],
//                    new PersonModeListener<ModeId>() {
//                        @Override
//                        protected boolean checkEnd() {
//                            return physic.body.getLinearVelocity().x == 0;
//                        }
//
//                        @Override
//                        public ModeId getNextModeId() {
//                            return physic.body.getLinearVelocity().x == 0
//                                    ? ModeId.IDLE
//                                    : ModeId.BREAK;
//                        }
//                    }
//            ),
//            new PersonModeAdapter<ModeId>( // JUMP
//                    this,
//                    modes[ModeId.JUMP.ordinal()],
//                    new PersonModeListener<ModeId>() {
//                        @Override
//                        public boolean checkEnd() {
//                            return spriter.player.getAnimation().id == AnimationNum.JUMP_LOOP.ordinal() &&
//                                    isGrounded();
//                        }
//
//                        @Override
//                        public ModeId getNextModeId() {
//                            return isGrounded()
//                                    ? ModeId.IDLE
//                                    : null;
//                        }
//
//                        @Override
//                        protected void onSet() {
//                            // Apply jump impulse
//                            physic.body.applyLinearImpulse(
//                                    new Vector2(0, JUMP_VELOCITY),
//                                    physic.body.getPosition(),
//                                    true
//                            );
//                        }
//                    }
//            ),
//            new PersonModeAdapter<ModeId>( // SLIDE
//                    this,
//                    modes[ModeId.SLIDE.ordinal()],
//                    new PersonModeListener<ModeId>() {
//                        @Override
//                        public boolean checkEnd() {
//                            return physic.body.getLinearVelocity().x == 0;
//                        }
//
//                        @Override
//                        public ModeId getNextModeId() {
//                            return InputGroups.getActiveGroups().contains(InputGroupId.CROUCH)
//                                    ? ModeId.CROUCH
//                                    : ModeId.IDLE;
//                        }
//
//                        @Override
//                        protected void onSet() {
//                            // Stop moving
//                            move = MoveDirection.NONE;
//                        }
//                    }
//            ),
//            new PersonModeAdapter<ModeId>( // CROUCH
//                    this,
//                    modes[ModeId.CROUCH.ordinal()],
//                    new PersonModeListener<ModeId>() {
//                        @Override
//                        public ModeId getNextModeId() {
//                            return ModeId.IDLE;
//                        }
//                    }
//            ),
//            new PersonModeAdapter<ModeId>( // CROUCH_WALK
//                    this,
//                    modes[ModeId.CROUCH_WALK.ordinal()],
//                    new PersonModeListener<ModeId>() {
//                        @Override
//                        public ModeId getNextModeId() {
//                            return InputGroups.getActiveGroups().contains(InputGroupId.CROUCH)
//                                    ? ModeId.CROUCH
//                                    : ModeId.IDLE;
//                        }
//                    }
//            ),
//            new PersonModeAdapter<ModeId>( // BREAK
//                    this,
//                    modes[ModeId.BREAK.ordinal()],
//                    new PersonModeListener<ModeId>() {
//                        @Override
//                        public boolean checkEnd() {
//                            return physic.body.getLinearVelocity().x == 0;
//                        }
//
//                        @Override
//                        public ModeId getNextModeId() {
//                            return ModeId.IDLE;
//                        }
//                    }
//            )
//    };


//    private static final PersonModeData[] modes = new PersonModeData[]{
//            new PersonModeData<ModeId, AnimationNum>( // IDLE
//                    ModeId.IDLE,
//                    new AnimationNum[]{
//                            AnimationNum.IDLE
//                    },
//                    new boolean[]{
//                            true,  // Idle
//                            true,  // Walk
//                            true,  // Jump
//                            false, // Slide
//                            true,  // Crouch
//                            false, // Crouch walk
//                            true   // Break
//                    }
//            ),
//            new PersonModeData<ModeId, AnimationNum>( // WALK
//                    ModeId.WALK,
//                    IDLE_MOVE_VELOCITY,
//                    IDLE_MOVE_MAX_VELOCITY,
//                    new AnimationNum[]{
//                            AnimationNum.WALK
//                    },
//                    new boolean[]{
//                            true, // Idle
//                            true, // Walk
//                            true, // Jump
//                            true, // Slide
//                            true, // Crouch
//                            true, // Crouch walk
//                            true  // Break
//                    }
//            ),
//            new PersonModeData<ModeId, AnimationNum>( // JUMP
//                    ModeId.JUMP,
//                    JUMP_MOVE_VELOCITY,
//                    JUMP_MOVE_MAX_VELOCITY,
//                    new AnimationNum[]{
//                            AnimationNum.JUMP_LOOP,
//                            AnimationNum.JUMP_START,
//                            AnimationNum.JUMP_END
//                    },
//                    new boolean[]{
//                            true,  // Idle
//                            false, // Walk
//                            true,  // Jump
//                            false, // Slide
//                            false, // Crouch
//                            true,  // Crouch walk
//                            true   // Break
//                    }
//            ),
//            new PersonModeData<ModeId, AnimationNum>( // SLIDE
//                    ModeId.SLIDE,
//                    new AnimationNum[]{
//                            AnimationNum.SLIDE_LOOP,
//                            AnimationNum.SLIDE_START,
//                            AnimationNum.SLIDE_END
//                    },
//                    new boolean[]{
//                            true,  // Idle
//                            false, // Walk
//                            false, // Jump
//                            true,  // Slide
//                            true,  // Crouch
//                            true,  // Crouch walk
//                            false  // Break
//                    }
//            ),
//            new PersonModeData<ModeId, AnimationNum>( // CROUCH
//                    ModeId.CROUCH,
//                    new AnimationNum[]{
//                            AnimationNum.CROUCH_LOOP,
//                            AnimationNum.CROUCH_START,
//                            AnimationNum.CROUCH_END
//                    },
//                    new boolean[]{
//                            true,  // Idle
//                            false, // Walk
//                            false, // Jump
//                            false, // Slide
//                            true,  // Crouch
//                            true,  // Crouch walk
//                            false  // Break
//                    }
//            ),
//            new PersonModeData<ModeId, AnimationNum>( // CROUCH_WALK
//                    ModeId.CROUCH_WALK,
//                    CROUCH_MOVE_VELOCITY,
//                    CROUCH_MOVE_MAX_VELOCITY,
//                    new AnimationNum[]{
//                            AnimationNum.CROUCH_WALK
//                    },
//                    new boolean[]{
//                            true, // Idle
//                            true, // Walk
//                            true, // Jump
//                            true, // Slide
//                            true, // Crouch
//                            true, // Crouch walk
//                            true  // Break
//                    }
//            ),
//            new PersonModeData<ModeId, AnimationNum>( // BREAK
//                    ModeId.BREAK,
//                    new AnimationNum[]{
//                            AnimationNum.BREAK_LOOP,
//                            AnimationNum.BREAK_START,
//                            AnimationNum.BREAK_END
//                    },
//                    new boolean[]{
//                            true,  // Idle
//                            true,  // Walk
//                            true,  // Jump
//                            true,  // Slide
//                            true,  // Crouch
//                            false, // Crouch walk
//                            true   // Break
//                    }
//            )
//    };
}

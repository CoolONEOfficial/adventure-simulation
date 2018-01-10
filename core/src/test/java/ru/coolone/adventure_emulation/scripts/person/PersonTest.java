package ru.coolone.adventure_emulation.scripts.person;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Entity;
import com.brashmonkey.spriter.Animation;
import com.brashmonkey.spriter.Mainline;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import lombok.val;
import ru.coolone.adventure_emulation.AbsTest;
import ru.coolone.adventure_emulation.Core;
import ru.coolone.adventure_emulation.InputGroups;
import ru.coolone.adventure_emulation.screen.ScreenManager;
import ru.coolone.adventure_emulation.script.Script;
import ru.coolone.adventure_emulation.scripts.persons.Player;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * @author coolone
 * @since 08.01.18
 */
public class PersonTest extends AbsTest {
    private Person<ModeId, AnimationId> person;

    private enum AnimationId {
        MODE_ONE_START,
        MODE_ONE_LOOP,
        MODE_ONE_END,

        MODE_TWO
    }

    private enum ModeId {
        MODE_ONE,
        MODE_TWO
    }

    private final InputGroups.InputGroupId inputGroupLeft = InputGroups.InputGroupId.values()[
            (int) (Math.random() * ((InputGroups.InputGroupId.values().length / 2) - 1))];

    private final InputGroups.InputGroupId inputGroupRight = InputGroups.InputGroupId.values()[
            ((InputGroups.InputGroupId.values().length / 2) - 1) +
                    (int) (Math.random() * ((InputGroups.InputGroupId.values().length / 2) - 1))];

    @SuppressWarnings("unchecked")
    private static final PersonMode<ModeId, AnimationId>[] personModes = new PersonMode[]{
            // MODE_ONE
            new PersonMode(
                    100f, 120f,
                    new AnimationId[]{
                            AnimationId.MODE_ONE_LOOP,
                            AnimationId.MODE_ONE_START,
                            AnimationId.MODE_ONE_END
                    },
                    new PersonMode.ChangeMode[]{
                            PersonMode.ChangeMode.ALLOWED_HARD,
                            PersonMode.ChangeMode.ALLOWED_HARD
                    },
                    new PersonMode.Behavior() {
                        @Override
                        public boolean checkEnd() {
                            return false;
                        }

                        @Override
                        public Enum getNextModeId() {
                            return ModeId.MODE_TWO;
                        }

                        @Override
                        public Enum getDefaultNextModeId() {
                            return ModeId.MODE_TWO;
                        }
                    }
            ),

            // MODE_TWO
            new PersonMode<Player.PlayerModeId, AnimationId>(
                    new AnimationId[]{
                            AnimationId.MODE_TWO
                    },
                    new PersonMode.ChangeMode[]{
                            PersonMode.ChangeMode.ALLOWED_HARD,
                            PersonMode.ChangeMode.ALLOWED_HARD
                    },
                    new PersonMode.Behavior() {
                        @Override
                        public boolean checkEnd() {
                            return false;
                        }

                        @Override
                        public ModeId getNextModeId() {
                            return ModeId.MODE_ONE;
                        }

                        @Override
                        public ModeId getDefaultNextModeId() {
                            return ModeId.MODE_ONE;
                        }
                    }
            )
    };

    private SpriterComponent personSpriter;
    private int spriterAnimationId;

    @SuppressWarnings("unchecked")
    @BeforeMethod
    @Override
    public void initMethod() throws Exception {
        super.initMethod();

        // --- Core ---
        val core = mock(Core.class);

        // -- Input groups --
        val inputGroups = mock(InputGroups.class);
        when(core.getInputGroups()).thenReturn(inputGroups);

        // -- Screen manager --
        val screenManager = mock(ScreenManager.class);
        when(core.getScreenManager()).thenReturn(screenManager);

        // - Root item -
        val rootItem = mock(ItemWrapper.class);
        when(screenManager.getRootItem()).thenReturn(rootItem);

        // Composite
        val compositeWrapper = mock(ItemWrapper.class);
        when(rootItem.getChild("person")).thenReturn(compositeWrapper);

        val composite = mock(Entity.class);
        when(compositeWrapper.getEntity()).thenReturn(composite);

        // Spriter
        val spriterWrapper = mock(ItemWrapper.class);
        when(compositeWrapper.getChild("spriter")).thenReturn(spriterWrapper);

        val spriter = mock(Entity.class);
        when(spriterWrapper.getEntity()).thenReturn(spriter);

        // Spriter player
        val player = mock(com.brashmonkey.spriter.Player.class);
        doAnswer(
                invocation -> {
                    spriterAnimationId = ((Animation) invocation.getArgument(0)).id;
                    return null;
                }
        ).when(player)
                .setAnimation((Animation) any());

        // Animation class with public constructor
        class MyAnimation extends Animation {
            public MyAnimation(Mainline mainline, int id, String name, int length,
                               boolean looping, int timelines) {
                super(mainline, id, name, length, looping, timelines);
            }
        }

        when(player.getAnimation()).thenAnswer(
                invocation -> {
                    // Return current animation
                    int index = spriterAnimationId;

                    return new MyAnimation(
                            mock(Mainline.class),
                            index,
                            String.valueOf(index),
                            2,
                            false,
                            1
                    ) {{
                    }};

                }
        );

        person = new Person(
                core,
                "person",
                inputGroupLeft, inputGroupRight
        ) {
            @Override
            protected PersonMode[] getModes() {
                return personModes;
            }
        };
        person.init(new Entity() {{
            for (Class<? extends Component> mComponentClass : Script.componentClasses)
                add(mComponentClass.newInstance());
        }});
        person.spriter.init(new Entity() {{
            for (Class<? extends Component> mComponentClass : Script.componentClasses)
                add(mComponentClass.newInstance());

            personSpriter = getComponent(SpriterComponent.class);
            personSpriter.player = player;
        }});
    }

    @Test
    public void testGetModes() throws Exception {
        assertEquals(person.getModes(), personModes);
    }

    @DataProvider(name = "activateModes")
    public static Object[][] activateModes() {
        return new Object[][]{
                {ModeId.MODE_ONE, ModeId.MODE_TWO},
                {ModeId.MODE_TWO, ModeId.MODE_ONE}
        };
    }

    @Test(dataProvider = "activateModes")
    public void testActivateMode(ModeId fromMode, ModeId toMode) throws Exception {
        person.currentModeId = fromMode;
        person.getModes()[fromMode.ordinal()]
                .personModeListeners.add(
                new PersonMode.PersonModeListener() {
                    @Override
                    protected void onDeactivate() {
                        super.onDeactivate();
                        assertEquals(person.currentModeId, toMode);
                    }
                }
        );
        person.activateMode(toMode, null);
        if (person.getCurrentMode().animationStartLoopEnd)
            person.animationFinished(person.spriter.getAnimation());
    }

    @DataProvider(name = "nextModes")
    public static Object[][] nextModes() {
        return new Object[][]{
                {ModeId.MODE_ONE},
                {ModeId.MODE_TWO}
        };
    }

    @Test(dataProvider = "nextModes")
    public void testToNextMode(ModeId fromMode) throws Exception {
        ModeId toMode = personModes[fromMode.ordinal()].behavior.getNextModeId();
        testActivateMode(fromMode, toMode);
    }

    @Test
    public void testGetCurrentMode() throws Exception {
        person.currentModeId = ModeId.values()[(int) (Math.random() * (ModeId.values().length - 1))];
        assertEquals(
                person.getCurrentMode(),
                person.getModes()[person.getCurrentModeId().ordinal()]
        );
    }

    private int moveStopCount;

    @Test
    public void testSetMoveDir() throws Exception {
        person.setMoveDir(Person.MoveDir.LEFT);
        assertEquals(person.getMoveDir(), Person.MoveDir.LEFT);

        val listener = new PersonMode.PersonModeListener() {
            @Override
            protected void onMoveEnded() {
                super.onMoveEnded();
                moveStopCount++;
            }
        };

        person.currentModeId = ModeId.MODE_ONE;
        person.getCurrentMode().personModeListeners.add(listener);

        val oldMoveStopCount = moveStopCount;
        person.setMoveDir(Person.MoveDir.NONE);
        assertEquals(moveStopCount, oldMoveStopCount + 1);

        person.getCurrentMode().personModeListeners.remove(listener);
    }

    @Test
    public void testIsGrounded() throws Exception {
    }

}
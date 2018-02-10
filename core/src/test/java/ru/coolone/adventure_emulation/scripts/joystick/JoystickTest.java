package ru.coolone.adventure_emulation.scripts.joystick;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;

import lombok.NoArgsConstructor;
import lombok.val;
import ru.coolone.adventure_emulation.AbsTest;
import ru.coolone.adventure_emulation.Core;
import ru.coolone.adventure_emulation.input.InputGroups;
import ru.coolone.adventure_emulation.other.EntityBuilder;
import ru.coolone.adventure_emulation.screen.ScreenManager;
import ru.coolone.adventure_emulation.screen.ScreenScene;

import static org.mockito.ArgumentMatchers.any;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static ru.coolone.adventure_emulation.other.EntityBuilder.triggerLayerNames;
import static ru.coolone.adventure_emulation.scripts.joystick.Joystick.triggerNames;

/**
 * @author coolone
 * @since 08.01.18
 */
@NoArgsConstructor
public class JoystickTest extends AbsTest {
    private Joystick.TriggerId checkTriggerId = Joystick.TriggerId.values()[
            ((Joystick.TriggerId.values().length / 2) - 1) +
                    (int) (Math.random() * ((Joystick.TriggerId.values().length / 2) - 1))];

    private InputGroups.InputGroupId checkInputGroupId = InputGroups.InputGroupId.values()[
            (int) (Math.random() * ((InputGroups.InputGroupId.values().length / 2) - 1))];

    private static final Entity triggerCenterEntity = new EntityBuilder()
            .addName("triggerCenter")
            .addLayerMocks(triggerLayerNames)
            .entity;

    private static final Entity triggerLeftEntity = new EntityBuilder()
            .addName("triggerLeft")
            .addLayerMocks(triggerLayerNames)
            .entity;

    private static final Entity triggerRightEntity = new EntityBuilder()
            .addName("triggerRight")
            .addLayerMocks(triggerLayerNames)
            .entity;

    private static final Entity triggerUpEntity = new EntityBuilder()
            .addName("triggerUp")
            .addLayerMocks(triggerLayerNames)
            .entity;

    private static final Entity triggerDownEntity = new EntityBuilder()
            .addName("triggerDown")
            .addLayerMocks(triggerLayerNames)
            .entity;

    private static final Entity triggerRightUpEntity = new EntityBuilder()
            .addName("triggerRightUp")
            .addLayerMocks(triggerLayerNames)
            .entity;

    private static final Entity triggerRightDownEntity = new EntityBuilder()
            .addName("triggerRightDown")
            .addLayerMocks(triggerLayerNames)
            .entity;

    private static final Entity triggerLeftUpEntity = new EntityBuilder()
            .addName("triggerLeftUp")
            .addLayerMocks(triggerLayerNames)
            .entity;

    private static final Entity triggerLeftDownEntity = new EntityBuilder()
            .addName("triggerLeftDown")
            .addLayerMocks(triggerLayerNames)
            .entity;
    
    private static final Entity bgEntity = new EntityBuilder()
            .addName("bg")
            .entity;

    private static final Entity stickEntity = new EntityBuilder()
            .addName("stick")
            .entity;

    private static final Entity joystickEntity = new EntityBuilder()
            .addName("joystick")
            .addChilds(
                    stickEntity,
                    bgEntity,
                    triggerCenterEntity,
                    triggerLeftEntity,
                    triggerRightEntity,
                    triggerUpEntity,
                    triggerDownEntity,
                    triggerRightUpEntity,
                    triggerRightDownEntity,
                    triggerLeftUpEntity,
                    triggerLeftDownEntity
            ).entity;

    private static final Entity rootEntity = new EntityBuilder()
            .addName("root")
            .addChilds(
                    joystickEntity
            ).entity;

    private Joystick joystick;

    @SuppressWarnings("unchecked")
    @BeforeClass
    @Override
    protected void setUpClass() throws Exception {
        super.setUpClass();

        // --- Core ---
        val core = spy(new Core());

        // -- Input groups --
        val inputGroups = new InputGroups();
        when(core.getInputGroups()).thenReturn(inputGroups);

        // -- Screen manager --
        val screenManager = spy(new ScreenManager(
                mock(SceneLoader.class),
                new HashMap<>(),
                core
        ));
        when(screenManager.getRootItem()).thenReturn(new ItemWrapper(rootEntity));

        when(core.getScreenManager()).thenReturn(screenManager);

        val triggerInputGroups = new EnumMap(Joystick.TriggerId.class);
        triggerInputGroups.put(
                checkTriggerId,
                checkInputGroupId
        );
        joystick = new Joystick(
                core,
                "joystick",
                triggerInputGroups
        );
    }

    @Test
    public void testUpTrigger() throws Exception {
        val upTrigger = joystick.getTrigger(Joystick.TriggerId.UP);

        joystick.getTrigger(Joystick.TriggerId.CENTER).setVisible(false);
        joystick.getTrigger(Joystick.TriggerId.LEFT_UP).setVisible(false);
        joystick.getTrigger(Joystick.TriggerId.RIGHT_UP).setVisible(false);

        upTrigger.activate();

        assertTrue(joystick.getTrigger(Joystick.TriggerId.CENTER).isVisible());
        assertTrue(joystick.getTrigger(Joystick.TriggerId.LEFT_UP).isVisible());
        assertTrue(joystick.getTrigger(Joystick.TriggerId.RIGHT_UP).isVisible());
    }

    @Test
    public void testGetTrigger() throws Exception {
        int checkIndex = (int) (Math.random() * (Joystick.TriggerId.values().length - 1));
        assertEquals(
                joystick.getTrigger(Joystick.TriggerId.values()[checkIndex]),
                joystick.triggers[checkIndex]
        );
    }

    @Test
    public void testGetCurrentTrigger() throws Exception {
        assertEquals(
                joystick.getCurrentTrigger(),
                joystick.getTrigger(joystick.getCurrentTriggerId())
        );
    }
}
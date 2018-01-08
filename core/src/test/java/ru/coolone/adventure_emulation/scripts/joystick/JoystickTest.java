package ru.coolone.adventure_emulation.scripts.joystick;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.utils.Array;
import com.uwsoft.editor.renderer.components.MainItemComponent;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.EnumMap;

import lombok.val;
import ru.coolone.adventure_emulation.AbsTest;
import ru.coolone.adventure_emulation.Core;
import ru.coolone.adventure_emulation.input.InputGroups;
import ru.coolone.adventure_emulation.screen.ScreenManager;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;
import static ru.coolone.adventure_emulation.scripts.joystick.Joystick.triggerNames;

/**
 * Created by coolone on 08.01.18.
 */
public class JoystickTest extends AbsTest {
    private Joystick.TriggerId checkTriggerId = Joystick.TriggerId.values()[
            Joystick.TriggerId.values().length / 2 +
                    (int) (Math.random() * Joystick.TriggerId.values().length / 2)];

    private InputGroups.InputGroupId checkInputGroupId = InputGroups.InputGroupId.values()[
            (int) (Math.random() * InputGroups.InputGroupId.values().length / 2)];

    private Joystick joystick;

    @SuppressWarnings("unchecked")
    @BeforeMethod
    @Override
    public void initMethod() throws Exception {
        super.initMethod();

        // --- Core ---
        val core = mock(Core.class);

        // -- Input groups --
        val inputGroups = new InputGroups();
        when(core.getInputGroups()).thenReturn(inputGroups);

        // -- Screen manager --
        val screenManager = mock(ScreenManager.class);
        when(core.getScreenManager()).thenReturn(screenManager);

        // - Root item wrapper -
        val rootItem = mock(ItemWrapper.class);
        when(screenManager.getRootItem()).thenReturn(rootItem);

        // - Composite -
        val composite = mock(ItemWrapper.class);
        when(rootItem.getChild("joystick")).thenReturn(composite);

        // Stick
        val stickWrapper = mock(ItemWrapper.class);
        when(composite.getChild("stick")).thenReturn(stickWrapper);

        val stick = mock(Entity.class);
        when(stickWrapper.getEntity()).thenReturn(stick);

        // Bg
        val bgWrapper = mock(ItemWrapper.class);
        when(composite.getChild("bg")).thenReturn(bgWrapper);

        val bg = mock(Entity.class);
        when(bgWrapper.getEntity()).thenReturn(bg);

        when(
                composite.addScript(any())
        ).thenReturn(null);

        // Triggers
        for (int mTriggerId = 0; mTriggerId < Joystick.TriggerId.COUNT.ordinal(); mTriggerId++) {

            // Add script
            val mTriggerName = triggerNames[mTriggerId];

            val mTriggerWrapper = mock(ItemWrapper.class);
            when(composite.getChild(mTriggerName)).thenReturn(mTriggerWrapper);

            val mTrigger = mock(Entity.class);
            when(mTriggerWrapper.getEntity()).thenReturn(mTrigger);

            when(mTrigger.getComponents()).thenReturn(
                    new ImmutableArray(
                            new Array() {{
                                add(new MainItemComponent());
                            }}
                    )
            );

            when(composite.getChild(mTriggerName)
                    .addScript(any())).thenReturn(null);
        }

        joystick = new Joystick(
                core,
                "joystick",
                new EnumMap(Joystick.TriggerId.class) {{
                    put(
                            checkTriggerId,
                            checkInputGroupId
                    );
                }}
        );
    }

    @Test
    public void testGetTrigger() throws Exception {
        int checkIndex = (int) (Math.random() * Joystick.TriggerId.values().length);
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
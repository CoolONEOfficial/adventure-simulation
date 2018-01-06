package ru.coolone.adventure_emulation.scripts;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.InputMultiplexer;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.data.LayerItemVO;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;

import ru.coolone.adventure_emulation.AbsTest;
import ru.coolone.adventure_emulation.Core;
import ru.coolone.adventure_emulation.input.InputGroups;
import ru.coolone.adventure_emulation.screen.ScreenManager;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;

/**
 * @author coolone
 */
public class ButtonTest extends AbsTest {

    @InjectMocks private Button button;

    private int bClickCount = 0;
    private int bDownCount = 0;
    private int bUpCount = 0;
    final Button.ButtonListener buttonListener = new Button.ButtonListener() {
        @Override
        public void onButtonClick() {
            bClickCount++;
        }

        @Override
        public void onButtonDown() {
            bDownCount++;
        }

        @Override
        public void onButtonUp() {
            bUpCount++;
        }
    };

    private Core core;

    private ScreenManager coreScreenManager;
    private InputGroups coreInputGroups;

    @BeforeMethod
    @Override
    public void init() throws Exception {
        // --- Core mocking ---
        core = mock(Core.class);

        coreScreenManager = mock(ScreenManager.class);
        Mockito.when(core.getScreenManager()).thenReturn(coreScreenManager);

        // -- Screen manager --
        ItemWrapper screenManagerRoot = mock(ItemWrapper.class);
        Mockito.when(coreScreenManager.getRootItem()).thenReturn(screenManagerRoot);

        // - Root entity -
        Entity rootEntity = mock(Entity.class);
        Mockito.when(rootEntity.getComponent(TransformComponent.class)).thenReturn(
                new TransformComponent() {{
                    x = 100;
                    y = 200;
                }}
        );
        Mockito.when(rootEntity.getComponent(DimensionsComponent.class)).thenReturn(
                new DimensionsComponent() {{
                    width = 500;
                    height = 80;
                }}
        );
        Mockito.when(rootEntity.getComponent(LayerMapComponent.class)).thenReturn(
                new LayerMapComponent() {{
                    addLayer(
                            new LayerItemVO("normal")
                    );
                    addLayer(
                            new LayerItemVO("pressed")
                    );
                }}
        );

        // - Root ItemWrapper -
        ItemWrapper itemWrapper = mock(ItemWrapper.class);
        Mockito.when(itemWrapper.getEntity()).thenReturn(rootEntity);

        Mockito.when(screenManagerRoot.getChild("button")).thenReturn(
                itemWrapper
        );

        // -- Input groups --
        coreInputGroups = mock(InputGroups.class);
        Mockito.when(core.getInputGroups()).thenReturn(coreInputGroups);

        // - Multiplexer -
        InputMultiplexer multiplexer = mock(InputMultiplexer.class);
        Mockito.when(coreInputGroups.getMultiplexer()).thenReturn(multiplexer);

        // --- Create button with mocked core ---
        button = new Button(core, "button");
        button.listeners.add(buttonListener);

        super.init();
    }

    @Test
    public void testClick() throws Exception {
        int countOld = bClickCount;
        button.click();
        assertEquals(bClickCount, countOld + 1);
    }

    @Test
    public void testDown() throws Exception {
        int countOld = bDownCount;
        button.down();
        assertEquals(bDownCount, countOld + 1);
    }

    @Test
    public void testUp() throws Exception {
        int countOld = bUpCount;
        button.up();
        assertEquals(bUpCount, countOld + 1);
    }
}
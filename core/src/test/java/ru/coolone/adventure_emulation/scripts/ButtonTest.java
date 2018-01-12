package ru.coolone.adventure_emulation.scripts;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.utils.Array;
import com.uwsoft.editor.renderer.components.DimensionsComponent;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.components.TransformComponent;
import com.uwsoft.editor.renderer.data.LayerItemVO;
import com.uwsoft.editor.renderer.utils.ItemWrapper;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import lombok.NoArgsConstructor;
import lombok.val;
import ru.coolone.adventure_emulation.AbsTest;
import ru.coolone.adventure_emulation.Core;
import ru.coolone.adventure_emulation.input.InputGroups;
import ru.coolone.adventure_emulation.screen.ScreenManager;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.testng.Assert.assertEquals;

/**
 * @author coolone
 * @since 08.01.18
 */
@NoArgsConstructor
public class ButtonTest extends AbsTest {

    private Button button;

    private int bClickCount = 0;
    private int bDownCount = 0;
    private int bUpCount = 0;
    private final Button.ButtonListener buttonListener = new Button.ButtonListener() {
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

    @SuppressWarnings("unchecked")
    @BeforeClass
    @Override
    protected void setUpClass() throws Exception {
        super.setUpClass();

        // --- Core mocking ---
        val core = mock(Core.class);

        val coreScreenManager = mock(ScreenManager.class);
        when(core.getScreenManager()).thenReturn(coreScreenManager);

        // -- Screen manager --
        val screenManagerRoot = mock(ItemWrapper.class);
        when(coreScreenManager.getRootItem()).thenReturn(screenManagerRoot);

        // - Root entity -
        val rootEntity = mock(Entity.class);
        when(rootEntity.getComponents()).thenReturn(
                new ImmutableArray<>(
                        new Array() {{
                            add(
                                    new TransformComponent() {{
                                        x = 100;
                                        y = 200;
                                    }}
                            );
                            add(
                                    new TransformComponent() {{
                                        x = 100;
                                        y = 200;
                                    }}
                            );
                            add(
                                    new DimensionsComponent() {{
                                        width = 500;
                                        height = 80;
                                    }}
                            );
                            { // because nested double brackets
                                add(
                                        new LayerMapComponent() {{
                                            addLayer(
                                                    new LayerItemVO("normal")
                                            );
                                            addLayer(
                                                    new LayerItemVO("pressed")
                                            );
                                        }}
                                );
                            }
                        }}
                )
        );

        // - Root ItemWrapper -
        val itemWrapper = mock(ItemWrapper.class);
        when(itemWrapper.getEntity()).thenReturn(rootEntity);

        when(screenManagerRoot.getChild("button")).thenReturn(
                itemWrapper
        );

        // -- Input groups --
        val coreInputGroups = mock(InputGroups.class);
        when(core.getInputGroups()).thenReturn(coreInputGroups);

        // - Multiplexer -
        val multiplexer = mock(InputMultiplexer.class);
        when(coreInputGroups.getMultiplexer()).thenReturn(multiplexer);

        // --- Create button with mocked core ---
        button = new Button(core, "button");
        button.buttonListeners.add(buttonListener);
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
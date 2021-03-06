package ru.coolone.adventure_emulation.screen;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.physics.box2d.World;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.components.NodeComponent;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;

import lombok.val;
import ru.coolone.adventure_emulation.AbsTest;
import ru.coolone.adventure_emulation.Core;
import ru.coolone.adventure_emulation.camera.Camera;
import ru.coolone.adventure_emulation.other.vectors.Vector2;
import ru.coolone.adventure_emulation.other.vectors.Vector3;
import ru.coolone.adventure_emulation.screens.MenuScreen;
import ru.coolone.adventure_emulation.script.Script;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;

/**
 * @author coolone
 * @since 20.01.18
 */
public class ScreenManagerTest extends AbsTest {
    private ScreenManager screenManager;

    @SuppressWarnings("unchecked")
    private HashMap<Class<? extends ScreenScene>, ScreenScene> screenMap = spy(HashMap.class);

    private Core core = mock(Core.class);
    private Screen coreCurrentScreen;

    @BeforeClass
    @Override
    protected void setUpClass() throws Exception {
        super.setUpClass();
        doAnswer(invocation -> {
            coreCurrentScreen = invocation.getArgument(0);
            return null;
        }).when(core).setScreen(any(Screen.class));
        when(core.getScreen()).thenReturn(coreCurrentScreen);

        val loader = mock(SceneLoader.class);

        val batch = mock(Batch.class);
        when(loader.getBatch()).thenReturn(batch);

        loader.world = mock(World.class);

        screenManager = spy(new ScreenManager(loader, screenMap, core));
    }

    @Test
    public void testOpenScreen() throws Exception {
        val rootEntity = new Entity();
        //noinspection unchecked
        for (Class<? extends Component> mComponentClass : Script.componentClasses)
            rootEntity.add(mComponentClass.newInstance());
        rootEntity.add(new NodeComponent());

        when(screenManager.loader.getRoot())
                .thenReturn(rootEntity);

        val gameScreenMock = mock(MenuScreen.class);
        when(screenManager.getScreen(MenuScreen.class))
                .thenReturn(gameScreenMock);

        screenManager.openScreen(MenuScreen.class);

        assertEquals(screenManager.loader.getRoot(), rootEntity);
        assertEquals(screenManager.getCurrentScreen(), gameScreenMock);
        verify(core).setScreen(gameScreenMock);
    }

    @Test
    public void testGetBatch() throws Exception {
        assertNotNull(screenManager.getBatch());
        assertEquals(screenManager.getBatch(), screenManager.loader.getBatch());
    }

    @Test
    public void testGetWorld() throws Exception {
        assertNotNull(screenManager.getWorld());
        assertEquals(screenManager.getWorld(), screenManager.loader.world);
    }

    @Test
    public void testGetScreen() throws Exception {
        assertEquals(screenMap.size(), 0);
        assertNotNull(screenManager.getScreen(MenuScreen.class));
        assertEquals(screenMap.size(), 1);
        assertNotNull(screenManager.getScreen(MenuScreen.class));
        assertEquals(screenMap.size(), 1);
    }

    @Test
    public void testScreenToWorldCoord() throws Exception {
        val checkCoord = new Vector2(
                (float) (Math.random() * 100.),
                (float) (Math.random() * 400.)
        );

        val camera = mock(Camera.class);
        when(camera.unproject(any())).thenReturn(
                new Vector3(checkCoord)
        );

        when(screenManager.getCamera()).thenReturn(camera);

        val worldCoord = screenManager.screenToWorldCoord(new Vector2());

        assertEquals(worldCoord, checkCoord);
    }

    @Test
    public void testUpdateEngine() throws Exception {
        val engine = mock(Engine.class);
        doNothing().when(engine).update(anyFloat());

        when(screenManager.loader.getEngine()).thenReturn(engine);

        screenManager.updateEngine(0.1f);

        verify(screenManager.loader.getEngine()).update(anyFloat());
    }
}
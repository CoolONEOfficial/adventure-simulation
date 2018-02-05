package ru.coolone.adventure_emulation.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.mock.input.MockInput;
import com.uwsoft.editor.renderer.SceneLoader;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;

import lombok.val;
import ru.coolone.adventure_emulation.AbsTest;
import ru.coolone.adventure_emulation.Core;
import ru.coolone.adventure_emulation.input.InputGroups;
import ru.coolone.adventure_emulation.other.EntityBuilder;
import ru.coolone.adventure_emulation.screen.ScreenManager;
import ru.coolone.adventure_emulation.screen.ScreenScene;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.testng.Assert.assertNotNull;
import static ru.coolone.adventure_emulation.other.EntityBuilder.buttonLayerNames;

/**
 * @author coolone
 * @since 05.02.18
 */
public class MenuScreenTest extends AbsTest {
    @SuppressWarnings("unused")
    private static final String TAG = MenuScreenTest.class.getSimpleName();

    private MenuScreen menuScreen;

    private ScreenManager screenManager;

    @BeforeClass
    @Override
    protected void setUpClass() throws Exception {
        super.setUpClass();

        Gdx.input = new MockInput();

        val core = spy(new Core());

        val inputGroups = new InputGroups();
        when(core.getInputGroups()).thenReturn(inputGroups);
        val map = new HashMap<Class<? extends ScreenScene>, ScreenScene>();
        val loader = mock(SceneLoader.class);
        loader.rootEntity = rootEntity;
        when(loader.getRoot()).thenReturn(rootEntity);
        screenManager = new ScreenManager(loader, map, core);
        when(core.getScreenManager()).thenReturn(screenManager);

        menuScreen = new MenuScreen(core);
    }

    private static final Entity buttonPlay = new EntityBuilder()
            .addName("buttonPlay")
            .addLayerMocks(buttonLayerNames)
            .entity;
    private static final Entity buttonExit = new EntityBuilder()
            .addName("buttonExit")
            .addLayerMocks(buttonLayerNames)
            .entity;
    private static final Entity rootEntity = spy(new EntityBuilder()
            .addName("root")
            .addChilds(
                    buttonPlay,
                    buttonExit
            ).entity);

    @Test
    public void testShow() throws Exception {
        screenManager.screenMap.put(MenuScreen.class, menuScreen);
        screenManager.openScreen(MenuScreen.class);

        assertNotNull(menuScreen.buttonPlay);
        assertNotNull(menuScreen.buttonExit);
    }

    @Test
    public void testHide() throws Exception {
        screenManager.screenMap.put(MenuScreen.class, menuScreen);
        screenManager.openScreen(MenuScreen.class);

        menuScreen.buttonPlay = spy(menuScreen.buttonPlay);
        menuScreen.buttonExit = spy(menuScreen.buttonExit);

        menuScreen.hide();

        verify(menuScreen.buttonPlay).dispose();
        verify(menuScreen.buttonExit).dispose();
    }

}
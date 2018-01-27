package ru.coolone.adventure_emulation.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.mock.input.MockInput;
import com.brashmonkey.spriter.Player;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.components.NodeComponent;
import com.uwsoft.editor.renderer.components.ScriptComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;

import lombok.val;
import ru.coolone.adventure_emulation.AbsTest;
import ru.coolone.adventure_emulation.Core;
import ru.coolone.adventure_emulation.EntityFactory;
import ru.coolone.adventure_emulation.input.InputGroups;
import ru.coolone.adventure_emulation.screen.ScreenManager;
import ru.coolone.adventure_emulation.screen.ScreenScene;

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author coolone
 * @since 23.01.18
 */
public class GameScreenTest extends AbsTest {

    private GameScreen gameScreen;

    @BeforeClass
    @Override
    protected void setUpClass() throws Exception {
        super.setUpClass();

        Gdx.input = new MockInput();
        val core = spy(new Core());
        val inputGroups = new InputGroups();
        when(core.getInputGroups()).thenReturn(inputGroups);

        gameScreen = new GameScreen(core);
    }

    private static final Entity playerSpriterEntity = EntityFactory.createEntity(
            "spriter",
            ScriptComponent.class,
            SpriterComponent.class
    );

    private static final Entity playerEntity = EntityFactory.createEntity(
            "player",
            ScriptComponent.class,
            NodeComponent.class
    );

    private static final Entity rootEntity = spy(
            EntityFactory.createEntity(
                    "root",
                    ScriptComponent.class,
                    NodeComponent.class
            )
    );

    static {
        playerSpriterEntity.getComponent(SpriterComponent.class)
                .player = mock(Player.class);

        playerEntity.getComponent(NodeComponent.class)
                .addChild(
                        playerSpriterEntity
                );
        rootEntity.getComponent(NodeComponent.class)
                .addChild(
                        playerEntity
                );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void testShow() throws Exception {
        val loader = mock(SceneLoader.class);
        loader.rootEntity = rootEntity;
        when(loader.getRoot()).thenReturn(rootEntity);
        val map = new HashMap<Class<? extends ScreenScene>, ScreenScene>();
        val core = spy(new Core());
        val inputGroups = new InputGroups();
        when(core.getInputGroups()).thenReturn(inputGroups);
        val screenManager = new ScreenManager(loader, map, core);
        core.screenManager = screenManager;
        screenManager.openScreen(GameScreen.class);
        gameScreen.show();

        assertNotNull(gameScreen.player);
        assertNotNull(gameScreen.joystick);
        assertNotNull(gameScreen.downButton);
        assertNotNull(gameScreen.upButton);
        assertNotNull(gameScreen.leftButton);
        assertNotNull(gameScreen.rightButton);
        assertNotNull(gameScreen.font);
        assertNotNull(gameScreen.debugRenderer);

        assertTrue(gameScreen.core.getInputGroups().getListeners().contains(gameScreen));
    }

    @Test
    public void testRender() throws Exception {
    }

    @Test
    public void testHide() throws Exception {
    }

    @Test
    public void testDispose() throws Exception {
    }

    @Test
    public void testOnInputGroupActivate() throws Exception {
    }

    @Test
    public void testOnInputGroupDeactivate() throws Exception {
    }

}
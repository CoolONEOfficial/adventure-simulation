package ru.coolone.adventure_emulation.screens;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.headless.mock.input.MockInput;
import com.brashmonkey.spriter.Player;
import com.uwsoft.editor.renderer.SceneLoader;
import com.uwsoft.editor.renderer.components.LayerMapComponent;
import com.uwsoft.editor.renderer.components.spriter.SpriterComponent;
import com.uwsoft.editor.renderer.data.LayerItemVO;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;

import lombok.val;
import ru.coolone.adventure_emulation.AbsTest;
import ru.coolone.adventure_emulation.Core;
import ru.coolone.adventure_emulation.input.InputGroups;
import ru.coolone.adventure_emulation.other.EntityBuilder;
import ru.coolone.adventure_emulation.screen.ScreenManager;
import ru.coolone.adventure_emulation.screen.ScreenScene;
import ru.coolone.adventure_emulation.scripts.Button;

import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * @author coolone
 * @since 23.01.18
 */
public class GameScreenTest extends AbsTest {
    @SuppressWarnings("unused")
    private static final String TAG = GameScreenTest.class.getSimpleName();

    private GameScreen gameScreen;

    private ScreenManager screenManager;

    @BeforeClass
    @Override
    protected void setUpClass() throws Exception {
        super.setUpClass();

        Gdx.input = new MockInput();
        Core.DEBUG = false;

        val core = spy(new Core());

        val inputGroups = new InputGroups();
        when(core.getInputGroups()).thenReturn(inputGroups);
        val map = new HashMap<Class<? extends ScreenScene>, ScreenScene>();
        val loader = mock(SceneLoader.class);
        loader.rootEntity = rootEntity;
        when(loader.getRoot()).thenReturn(rootEntity);
        screenManager = new ScreenManager(loader, map, core);
        when(core.getScreenManager()).thenReturn(screenManager);

        gameScreen = new GameScreen(core);
    }

    private static final Entity playerSpriterEntity = new EntityBuilder()
            .addName("spriter")
            .addCustom(SpriterComponent.class)
            .entity;

    private static final Entity playerEntity = new EntityBuilder()
            .addName("player")
            .addChilds(playerSpriterEntity)
            .entity;

    private static final String[] triggerLayerNames = {
            "active",
            "passive"
    };

    private static final Entity joystickTriggerCenterEntity = new EntityBuilder()
            .addName("triggerCenter")
            .addLayerMocks(triggerLayerNames)
            .entity;

    private static final Entity joystickTriggerLeftEntity = new EntityBuilder()
            .addName("triggerLeft")
            .addLayerMocks(triggerLayerNames)
            .entity;

    private static final Entity joystickTriggerRightEntity = new EntityBuilder()
            .addName("triggerRight")
            .addLayerMocks(triggerLayerNames)
            .entity;

    private static final Entity joystickTriggerUpEntity = new EntityBuilder()
            .addName("triggerUp")
            .addLayerMocks(triggerLayerNames)
            .entity;

    private static final Entity joystickTriggerDownEntity = new EntityBuilder()
            .addName("triggerDown")
            .addLayerMocks(triggerLayerNames)
            .entity;

    private static final Entity joystickTriggerRightUpEntity = new EntityBuilder()
            .addName("triggerRightUp")
            .addLayerMocks(triggerLayerNames)
            .entity;

    private static final Entity joystickTriggerRightDownEntity = new EntityBuilder()
            .addName("triggerRightDown")
            .addLayerMocks(triggerLayerNames)
            .entity;

    private static final Entity joystickTriggerLeftUpEntity = new EntityBuilder()
            .addName("triggerLeftUp")
            .addLayerMocks(triggerLayerNames)
            .entity;

    private static final Entity joystickTriggerLeftDownEntity = new EntityBuilder()
            .addName("triggerLeftDown")
            .addLayerMocks(triggerLayerNames)
            .entity;

    private static final Entity joystickBgEntity = new EntityBuilder()
            .addName("bg")
            .entity;

    private static final Entity joystickStickEntity = new EntityBuilder()
            .addName("stick")
            .entity;

    private static final Entity joystickEntity = new EntityBuilder()
            .addName("joystick")
            .addChilds(
                    joystickStickEntity,
                    joystickBgEntity,
                    joystickTriggerCenterEntity,
                    joystickTriggerLeftEntity,
                    joystickTriggerRightEntity,
                    joystickTriggerUpEntity,
                    joystickTriggerDownEntity,
                    joystickTriggerRightUpEntity,
                    joystickTriggerRightDownEntity,
                    joystickTriggerLeftUpEntity,
                    joystickTriggerLeftDownEntity
            ).entity;

    private static final String[] buttonLayerNames = new String[]{
            Button.LAYER_NAME_ACTIVE,
            Button.LAYER_NAME_PASSIVE
    };

    private static final Entity buttonDownEntity = new EntityBuilder()
            .addName("buttonDown")
            .addLayerMocks(buttonLayerNames)
            .entity;

    private static final Entity buttonUpEntity = new EntityBuilder()
            .addName("buttonUp")
            .addLayerMocks(buttonLayerNames)
            .entity;

    private static final Entity buttonLeftEntity = new EntityBuilder()
            .addName("buttonLeft")
            .addLayerMocks(buttonLayerNames)
            .entity;

    private static final Entity buttonRightEntity = new EntityBuilder()
            .addName("buttonRight")
            .addLayerMocks(buttonLayerNames)
            .entity;

    private static final Entity rootEntity = spy(new EntityBuilder()
            .addName("root")
            .addChilds(
                    playerEntity,
                    joystickEntity,
                    buttonDownEntity,
                    buttonUpEntity,
                    buttonLeftEntity,
                    buttonRightEntity
            ).entity);

    static {
        playerSpriterEntity.getComponent(SpriterComponent.class)
                .player = mock(Player.class);

        val joystickTriggerDownLayers = joystickTriggerDownEntity.getComponent(LayerMapComponent.class);
        joystickTriggerDownLayers.addLayer(
                new LayerItemVO("active")
        );
        joystickTriggerDownLayers.addLayer(
                new LayerItemVO("passive")
        );
    }

    @Test
    public void testRender() throws Exception {
        // TODO: testRender
    }


    @DataProvider
    public static Object[][] moveModes() {
        return new Object[][]
                {
                        {
                                GameScreen.MoveMode.JOYSTICK
                        },
                        {
                                GameScreen.MoveMode.BUTTONS
                        }
                };
    }

    @SuppressWarnings("unchecked")
    @Test(dataProvider = "moveModes")
    public void testShow(GameScreen.MoveMode moveMode) throws Exception {
        screenManager.screenMap.put(GameScreen.class, gameScreen);
        screenManager.openScreen(GameScreen.class);
        gameScreen.setMoveMode(moveMode);

        assertNotNull(gameScreen.joystick);
        assertNotNull(gameScreen.downButton);
        assertNotNull(gameScreen.upButton);
        assertNotNull(gameScreen.leftButton);
        assertNotNull(gameScreen.rightButton);
        assertNotNull(gameScreen.player);

        assertTrue(gameScreen.core.getInputGroups().getListeners().contains(gameScreen));
    }

    @Test(dataProvider = "moveModes")
    public void testHide(GameScreen.MoveMode moveMode) throws Exception {
        screenManager.screenMap.put(GameScreen.class, gameScreen);
        screenManager.openScreen(GameScreen.class);
        gameScreen.setMoveMode(moveMode);

        gameScreen.player = spy(gameScreen.player);
        gameScreen.joystick = spy(gameScreen.joystick);
        gameScreen.leftButton = spy(gameScreen.leftButton);
        gameScreen.rightButton = spy(gameScreen.rightButton);
        gameScreen.upButton = spy(gameScreen.upButton);
        gameScreen.downButton = spy(gameScreen.downButton);

        gameScreen.hide();

        verify(gameScreen.joystick).dispose();
        verify(gameScreen.leftButton).dispose();
        verify(gameScreen.rightButton).dispose();
        verify(gameScreen.upButton).dispose();
        verify(gameScreen.downButton).dispose();
        verify(gameScreen.player).dispose();
    }

    @Test
    public void testDispose() throws Exception {
        assertFalse(gameScreen.core.getInputGroups().getListeners().contains(gameScreen));
    }

    @Test
    public void testOnInputGroupActivate() throws Exception {
        // TODO: testOnInputGroupActivate
    }
}
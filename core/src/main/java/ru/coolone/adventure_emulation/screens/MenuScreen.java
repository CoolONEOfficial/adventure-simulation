package ru.coolone.adventure_emulation.screens;

import com.badlogic.gdx.Gdx;

import ru.coolone.adventure_emulation.Core;
import ru.coolone.adventure_emulation.screen.ScreenScene;
import ru.coolone.adventure_emulation.scripts.Button;

/**
 * Main menu scene
 *
 * @author coolone
 */

public class MenuScreen extends ScreenScene {
    @SuppressWarnings("unused")
    private static final String TAG = MenuScreen.class.getSimpleName();

    public static final String name = "MenuScene";
    /**
     * ButtonBase open's @{@link GameScreen}
     */
    Button buttonPlay;
    /**
     * ButtonBase exit's application
     */
    Button buttonExit;

    @SuppressWarnings("WeakerAccess")
    public MenuScreen(
            Core core
    ) {
        super(core, name);
    }

    @Override
    public void show() {
        super.show();

        // Button play
        buttonPlay = new Button(
                core,
                "buttonPlay"
        );
        buttonPlay.buttonListeners.add(
                new Button.ButtonListener() {
                    @Override
                    public void onButtonDown() {
                    }

                    @Override
                    public void onButtonUp() {
                        core.getScreenManager()
                                .openScreen(GameScreen.class);
                    }
                }
        );

        // Button exit
        buttonExit = new Button(
                core,
                "buttonExit"
        );
        buttonExit.buttonListeners.add(
                new Button.ButtonListener() {
                    @Override
                    public void onButtonDown() {
                    }

                    @Override
                    public void onButtonUp() {
                        Gdx.app.exit();
                    }
                }
        );
    }

    @Override
    public void hide() {
        super.hide();

        buttonExit.dispose();
        buttonPlay.dispose();
    }
}

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
    private static final String TAG = MenuScreen.class.getSimpleName();

    private static final String name = "MenuScene";
    /**
     * ButtonBase open's @{@link GameScreen}
     */
    private Button buttonPlay;
    /**
     * ButtonBase exit's application
     */
    private Button buttonExit;

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
        buttonPlay.listeners.add(
                new Button.ButtonListener() {
                    @Override
                    public void onButtonClick() {
                        core.getScreenManager()
                                .openScreen(GameScreen.class);
                    }

                    @Override
                    public void onButtonDown() {
                    }

                    @Override
                    public void onButtonUp() {
                    }
                }
        );

        // Button exit
        buttonExit = new Button(
                core,
                "buttonExit"
        );
        buttonExit.listeners.add(
                new Button.ButtonListener() {
                    @Override
                    public void onButtonClick() {
                        Gdx.app.exit();
                    }

                    @Override
                    public void onButtonDown() {
                    }

                    @Override
                    public void onButtonUp() {
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

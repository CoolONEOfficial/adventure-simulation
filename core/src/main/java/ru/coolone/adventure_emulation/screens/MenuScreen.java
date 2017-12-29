package ru.coolone.adventure_emulation.screens;

import com.badlogic.gdx.Gdx;

import ru.coolone.adventure_emulation.GameCore;
import ru.coolone.adventure_emulation.scripts.button.Button;
import ru.coolone.adventure_emulation.screen.ScreenScene;

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
            GameCore core
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
        buttonPlay.addListener(
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
        buttonExit.addListener(
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
    public void render(float delta) {
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        buttonExit.dispose();
        buttonPlay.dispose();
    }

    @Override
    public void dispose() {

    }
}

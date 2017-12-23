package ru.coolone.adventure_emulation.screens;

import com.badlogic.gdx.Gdx;

import ru.coolone.adventure_emulation.GameCore;
import ru.coolone.adventure_emulation.SceneScreen;
import ru.coolone.adventure_emulation.game.scripts.Button;

/**
 * Created by coolone on 22.12.17.
 */

public class MenuScreen extends SceneScreen {
    private static final String TAG = MenuScreen.class.getSimpleName();

    private static final String name = "MenuScene";
    /**
     * Button open's @{@link GameScreen}
     */
    Button buttonGame;
    /**
     * Button exit's application
     */
    Button buttonExit;

    public MenuScreen(
            GameCore core
    ) {
        super(core, name);
    }

    @Override
    public void show() {
        super.show();

        // Button
        buttonGame = new Button(
                core,
                "buttonGame"
        );
        buttonGame.addListener(
                new Button.ButtonListener() {
                    @Override
                    public void onButtonClick() {
                        core.setScreen(core.gameScreen);
                    }

                    @Override
                    public void onButtonDown() {
                    }

                    @Override
                    public void onButtonUp() {
                    }
                }
        );

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

    }

    @Override
    public void dispose() {

    }
}

package ru.coolone.adventure_emulation.screens;

import com.badlogic.gdx.Gdx;
import com.uwsoft.editor.renderer.SceneLoader;

import ru.coolone.adventure_emulation.GameCore;
import ru.coolone.adventure_emulation.SceneScreen;
import ru.coolone.adventure_emulation.game.scripts.Button;

/**
 * Created by coolone on 22.12.17.
 */

public class MenuScreen extends SceneScreen
        implements Button.ButtonListener {
    private static final String TAG = MenuScreen.class.getSimpleName();
    Button button;

    public MenuScreen(String sceneName, SceneLoader loader) {
        super(sceneName, loader);
    }

    @Override
    public void show() {
        super.show();

        // Button
        button = new Button(
                GameCore.getInstance().getRootItem(),
                "button"
        );
    }

    @Override
    public void render(float delta) {
        // Overlap2d scene
        GameCore.getInstance()
                .loader
                .getEngine()
                .update(Gdx.graphics.getDeltaTime());
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

    @Override
    public void onButtonClick() {
        Gdx.app.log(TAG, "Click");
    }

    @Override
    public void onButtonDown() {
        Gdx.app.log(TAG, "Down");
    }

    @Override
    public void onButtonUp() {
        Gdx.app.log(TAG, "Up");
    }
}

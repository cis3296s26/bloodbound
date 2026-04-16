package com.zipporah.game.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class CreditScreen implements Screen {
    private final ScreenManager game;
    Texture background;

    FitViewport viewport;
    BitmapFont font;
    Texture homeButtonTexture;

    float homeButtonX = 1205f;
    float homeButtonY = 670f;
    float homeButtonWidth = 42f;
    float homeButtonHeight = 39f;

    private final String[] lines = {
            "Thank you for playing Bloodbound!",
            "",
            "Look at the rankings tab",
            "to see your scores!",
            "",
            "",
            "Developers:",
            "Danny Gray",
            "Nazarii Dubelovskyi",
            "Pablo Acosta",
            "Tyler Sutowski",
            "Zipporah Mooney\n"
    };

    private float scrollY = 0f;
    private static final float SCROLL_SPEED = 80f;
    private static final float LINE_HEIGHT = 50f;
    private static final float FONT_SCALE = 2f;

    private float endPauseTimer = -1f;
    private static final float END_PAUSE_DURATION = 1f;
    private boolean textFinished = false;

    public CreditScreen(ScreenManager game) {
        this.game = game;
    }

    @Override
    public void show() {
        viewport = new FitViewport(1280, 720);
        font = new BitmapFont();
        font.setColor(Color.RED);
        homeButtonTexture = new Texture(Gdx.files.internal("Buttons/HomeButton.png"));
        background = new Texture(Gdx.files.internal("Maps/creditBackground.png"));

        // start text below the screen
        scrollY = -600;
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();

        Vector2 mouse = viewport.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        boolean homeHovering =
                mouse.x >= homeButtonX && mouse.x <= homeButtonX + homeButtonWidth &&
                        mouse.y >= homeButtonY && mouse.y <= homeButtonY + homeButtonHeight;

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && homeHovering) {
            game.setScreen(new HomeScreen(game));
            return;
        }

        if (!textFinished) {
            scrollY += SCROLL_SPEED * delta;

            // finished when the last line has scrolled off the top
            float bottomLineY = scrollY;
            if (bottomLineY > 720f + LINE_HEIGHT) {
                textFinished = true;
                endPauseTimer = END_PAUSE_DURATION;
            }
        } else {
            endPauseTimer -= delta;
            if (endPauseTimer <= 0f) {
                game.setScreen(new HomeScreen(game));
                return;
            }
        }

        game.batch.setProjectionMatrix(viewport.getCamera().combined);
        game.batch.begin();
        game.batch.draw(background, 0, 0, 1280, 720);

        float screenWidth  = 1280f;
        float screenHeight = 720f;

        for (int i = 0; i < lines.length; i++) {
            float lineWorldY = scrollY + (lines.length - 1 - i) * LINE_HEIGHT;

            if (lineWorldY < -LINE_HEIGHT || lineWorldY > screenHeight + LINE_HEIGHT) continue;

            font.getData().setScale(FONT_SCALE);
            GlyphLayout layout = new GlyphLayout(font, lines[i]);
            float textX = (screenWidth - layout.width) / 2f;
            font.draw(game.batch, lines[i], textX, lineWorldY);
        }

        // reset font before drawing UI
        font.getData().setScale(1f);
        font.setColor(Color.WHITE);

        game.batch.draw(homeButtonTexture, homeButtonX, homeButtonY, homeButtonWidth, homeButtonHeight);

        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {
        if (width <= 0 || height <= 0) return;
        viewport.update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (font != null) font.dispose();
        if (homeButtonTexture != null) homeButtonTexture.dispose();
        if (background != null) background.dispose();
    }
}

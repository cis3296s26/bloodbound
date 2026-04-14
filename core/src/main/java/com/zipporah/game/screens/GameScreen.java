package com.zipporah.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.zipporah.game.Enemy;
import com.zipporah.game.Player;
import com.zipporah.game.enemies.Karasu;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.audio.Sound;

import java.util.ArrayList;
import java.util.Iterator;
import com.badlogic.gdx.math.Vector2;
import com.zipporah.game.enemies.Skeleton;

public class GameScreen implements Screen {

    protected final ScreenManager game;
    Player player = new Player();
    ArrayList<Enemy> enemies = new ArrayList<>();

    TiledMap map;
    OrthogonalTiledMapRenderer renderer;
    float scale = 4f; // change back to 4f just for test
    ExtendViewport viewport;
    FitViewport viewportHUD;
    Texture homeButtonTexture;
    float homeButtonX = 1205f;
    float homeButtonY = 670f;
    float homeButtonWidth = 42f;
    float homeButtonHeight = 39f;

    // array for all colossion rectangles from tiled map
    public static Array<Rectangle> collisionRectangles = new Array<>();
    // array for all wall collisions
    public static Array<Rectangle> wallRectangles = new Array<>();
    float Map_Height = 208f;
    // array for ladders
    public static Array<Rectangle> ladderRectangles = new Array<>();
    // array for spikes
    public static Array<Rectangle> spikeRectangles = new Array<>();
    // array for enemys
    public static Array<Rectangle> enemyRectangles = new Array<>();

    Texture keyTexture;
    int keyCount = 0;

    // chests
    Texture openChestTexture;
    Texture closeChestTexture;
    Vector2 chest1Position = new Vector2();
    Vector2 chest2Position = new Vector2();
    boolean chest1Open = false;
    boolean chest2Open = false;
    boolean haveChest1key = false;
    boolean haveChest2key = false;
    float chestInteractionRange = 150f;

    // doors
    Texture openDoorTexture;
    Texture closeDoorTexture;
    Texture openDoor2Texture;
    Texture closeDoor2Texture;
    Vector2 finalDoorPosition = new Vector2();
    Rectangle finalDoorRect = new Rectangle();
    boolean finalDoorOpen = false;
    protected boolean lastDoorTransitions = true;
    Vector2 firstDoorPosition = new Vector2();
    Vector2 lastDoorPosition = new Vector2();
    boolean firstDoorOpen = false;
    boolean lastDoorOpen = false;
    Rectangle firstDoorRect = new Rectangle();
    Rectangle lastDoorRect = new Rectangle();
    float doorInteractionRange = 150f;
    float doorOpenTime = 0f;

    // physics
    float gravity = -1500f;
    float hitbox_width = 60f;
    float hitbox_height = 80f;
    boolean onLadder = false;
    float ladderCenterX = 0f;
    boolean touchingLadder = false;
    Rectangle spriteBox = new Rectangle();

    // sound
    Sound skeletonHurt;

    // music
    Music music1;

    protected final ShapeRenderer shapeRenderer = new ShapeRenderer();

    // Karasu karasu;

    private void getCollisionObject() {
        MapLayer layer = map.getLayers().get("collision");
        for (MapObject obj : layer.getObjects()) {
            if (obj instanceof RectangleMapObject) {
                Rectangle r = ((RectangleMapObject) obj).getRectangle();

                // scale cords so they fit with the specific map dimensions
                collisionRectangles.add(new Rectangle(
                        r.x * scale,
                        r.y * scale,
                        r.width * scale,
                        r.height * scale));
            }
        }
    }

    private void getWallObjects() {
        MapLayer layer = map.getLayers().get("walls");
        for (MapObject obj : layer.getObjects()) {
            if (obj instanceof RectangleMapObject) {
                Rectangle r = ((RectangleMapObject) obj).getRectangle();
                wallRectangles.add(new Rectangle(
                        r.x * scale,
                        r.y * scale,
                        r.width * scale,
                        r.height * scale));
            }
        }
    }

    private void getLadderObjects() {
        MapLayer layer = map.getLayers().get("ladder");
        for (MapObject obj : layer.getObjects()) {
            if (obj instanceof RectangleMapObject) {
                Rectangle r = ((RectangleMapObject) obj).getRectangle();
                ladderRectangles.add(new Rectangle(
                        r.x * scale,
                        r.y * scale,
                        r.width * scale,
                        r.height * scale));
            }
        }
    }

    private void getSpikeObjects() {
        MapLayer layer = map.getLayers().get("spike");
        for (MapObject obj : layer.getObjects()) {
            if (obj instanceof RectangleMapObject) {
                Rectangle r = ((RectangleMapObject) obj).getRectangle();
                spikeRectangles.add(new Rectangle(
                        r.x * scale,
                        r.y * scale,
                        r.width * scale,
                        r.height * scale));
            }
        }
    }

    private void getChestObjects() {
        MapLayer layer1 = map.getLayers().get("chest1");
        for (MapObject obj : layer1.getObjects()) {
            if (obj instanceof RectangleMapObject) {
                Rectangle r = ((RectangleMapObject) obj).getRectangle();
                chest1Position.set(r.x * scale, r.y * scale);
            }
        }

        MapLayer layer2 = map.getLayers().get("chest2");
        for (MapObject obj : layer2.getObjects()) {
            if (obj instanceof RectangleMapObject) {
                Rectangle r = ((RectangleMapObject) obj).getRectangle();
                chest2Position.set(r.x * scale, r.y * scale);
            }
        }
    }

    private void getFinalDoorObjects() {
        MapLayer layer = map.getLayers().get("final_door");
        if (layer == null) return;
        for (MapObject obj : layer.getObjects()) {
            if (obj instanceof RectangleMapObject) {
                Rectangle r = ((RectangleMapObject) obj).getRectangle();
                finalDoorPosition.set(r.x * scale, r.y * scale);
                finalDoorRect.set(r.x * scale, r.y * scale, r.width * scale, r.height * scale);
            }
        }
        wallRectangles.add(finalDoorRect);
    }

    private void getDoorObjects() {
        MapLayer layer1 = map.getLayers().get("first_door");
        for (MapObject obj : layer1.getObjects()) {
            if (obj instanceof RectangleMapObject) {
                Rectangle r = ((RectangleMapObject) obj).getRectangle();
                firstDoorPosition.set(r.x * scale, r.y * scale);
                firstDoorRect.set(r.x * scale, r.y * scale, r.width * scale, r.height * scale);
            }
        }

        MapLayer layer2 = map.getLayers().get("last_door");
        for (MapObject obj : layer2.getObjects()) {
            if (obj instanceof RectangleMapObject) {
                Rectangle r = ((RectangleMapObject) obj).getRectangle();
                lastDoorPosition.set(r.x * scale, r.y * scale);
                lastDoorRect.set(r.x * scale, r.y * scale, r.width * scale, r.height * scale);
            }
        }

        // start walls as a wall object so can't pass
        wallRectangles.add(firstDoorRect);
        wallRectangles.add(lastDoorRect);
    }

    public GameScreen(ScreenManager game) {
        this.game = game;
    }

    @Override
    public void show() {
        viewport = new ExtendViewport(1280, 720);
        viewportHUD = new FitViewport(1280, 720);
        game.batch.setProjectionMatrix(viewport.getCamera().combined);

        homeButtonTexture = new Texture(Gdx.files.internal("Buttons/HomeButton.png"));

        // // render in map
        // // moving this is initLevel method
        // map = new TmxMapLoader().load("level_1.tmx");
        // renderer = new OrthogonalTiledMapRenderer(map, scale);

        // music
        // music1 =
        // Gdx.audio.newMusic(Gdx.files.internal("Music/spencer_yk-castle-of-athanasius-151010.mp3"));
        // music1.setLooping(true);
        // music1.setVolume(0.30f);
        // music1.play();

        collisionRectangles.clear();
        wallRectangles.clear();
        ladderRectangles.clear();
        spikeRectangles.clear();
        enemyRectangles.clear();

        initLevel();

        getCollisionObject();
        getWallObjects();
        getLadderObjects();
        getSpikeObjects();
        getChestObjects();
        getDoorObjects();
        getFinalDoorObjects();

        closeChestTexture = new Texture(Gdx.files.internal("Maps/chest_closed.png"));
        openChestTexture = new Texture(Gdx.files.internal("Maps/chest_open.png"));
        closeDoorTexture = new Texture(Gdx.files.internal("Maps/door_closed.png"));
        openDoorTexture = new Texture(Gdx.files.internal("Maps/door_open.png"));
        closeDoor2Texture = new Texture(Gdx.files.internal("Maps/door2_closed.png"));
        openDoor2Texture = new Texture(Gdx.files.internal("Maps/door2_open.png"));
        keyTexture = new Texture(Gdx.files.internal("Maps/key.png"));
        homeButtonTexture = new Texture(Gdx.files.internal("Buttons/HomeButton.png"));

        OrthographicCamera cam = (OrthographicCamera) viewport.getCamera();
        cam.position.set(640, 360, 0);
        cam.update();
        game.batch.setProjectionMatrix(cam.combined);

        player.idle_init();
        player.walk_init();
        player.jump_init();
        player.sprint_init();
        player.attack_init();
        player.hurt_init();
        player.dead_init();

        // Karasu's the final boss
        // enemies.add(new Karasu(1750, 50, 180, 60f, 70f, 62, 120));

        // First Door Skeleton
        enemies.add(new Skeleton(1800, 50, 200, 60f, 70f, 62, 120));

        // Second Chest Skeleton
        enemies.add(new Skeleton(3774, 128, 200, 60f, 70f, 62, 120));

        // sounds
        skeletonHurt = Gdx.audio.newSound(Gdx.files.internal("Sounds/Enemy/crunch_splat.wav"));
        // playerDead =
        // Gdx.audio.newSound(Gdx.files.internal("Sounds/Player/death_9_meghan.wav"));
    }

    // load assets in a method so its easier to override in GameScreen2
    // gamescreen2 will have its own method that will be called withing the show
    // function

    protected void initLevel() {
        // render in map
        // switching to test level 2
        map = new TmxMapLoader().load("level_1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, scale);

        music1 = Gdx.audio.newMusic(Gdx.files.internal("Music/spencer_yk-castle-of-athanasius-151010.mp3"));
        music1.setLooping(true);
        music1.setVolume(0.30f);
        music1.play();
    }

    @Override
    public void resize(int width, int height) {
        // If the window is minimized on a desktop (LWJGL3) platform, width and height
        // are 0, which causes problems.
        // In that case, we don't resize anything, and wait for the window to be a
        // normal size before updating.
        if (width <= 0 || height <= 0)
            return;

        // Resize your application here. The parameters represent the new window size.
        viewport.update(width, height, true);
        viewportHUD.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        delta = Math.min(delta, 0.05f); // attempt to bug fix for windows player falling through map
        input(delta);
        logic(delta);
        draw(delta);
    }

    protected void input(float delta) {
        player.input(delta);

        Vector2 hudMouse = viewportHUD.unproject(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        boolean homeButtonHovering =
                hudMouse.x >= homeButtonX &&
                hudMouse.x <= homeButtonX + homeButtonWidth &&
                hudMouse.y >= homeButtonY &&
                hudMouse.y <= homeButtonY + homeButtonHeight;

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && homeButtonHovering) {
            if (music1 != null) {
                music1.stop();
            }
            game.setScreen(new HomeScreen(game));
            return;
        }

        if (touchingLadder || onLadder) {
            if (Gdx.input.isKeyPressed(Input.Keys.W) ||
                    Gdx.input.isKeyPressed(Input.Keys.UP)) {
                onLadder = true;
                player.y += player.spriteSpeed * delta;
                player.currFrame = player.walk.getKeyFrame(player.time, true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S) ||
                    Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                onLadder = true;
                player.y -= player.spriteSpeed * delta;
                player.currFrame = player.walk.getKeyFrame(player.time, true);
            }
            // get off ladder by pressing left or right
            if (Gdx.input.isKeyPressed(Input.Keys.A) ||
                    Gdx.input.isKeyPressed(Input.Keys.LEFT) ||
                    Gdx.input.isKeyPressed(Input.Keys.D) ||
                    Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                onLadder = false;
            }
        }

        // chest interaction
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            // chest 1
            if (!chest1Open) {
                float dist1 = Vector2.dst(player.x, player.y, chest1Position.x, chest1Position.y);
                if (dist1 < chestInteractionRange) {
                    chest1Open = true;
                    haveChest1key = true;
                    keyCount++;
                }
            }

            // chest 2
            if (!chest2Open) {
                float dist2 = Vector2.dst(player.x, player.y, chest2Position.x, chest2Position.y);
                if (dist2 < chestInteractionRange) {
                    chest2Open = true;
                    haveChest2key = true;
                    keyCount++;
                }
            }

            // door interaction
            // first door needs first key
            if (!firstDoorOpen && haveChest1key) {
                float distToDoor1 = Vector2.dst(player.x, player.y, firstDoorPosition.x, firstDoorPosition.y);
                if (distToDoor1 < doorInteractionRange) {
                    firstDoorOpen = true;
                    // door can be walked through
                    wallRectangles.removeValue(firstDoorRect, true);
                    float doorOpenTime = 0.1f;
                    keyCount--;
                }
            }

            if (!lastDoorOpen && haveChest2key) {
                float distToDoor2 = Vector2.dst(player.x, player.y, lastDoorPosition.x, lastDoorPosition.y);
                if (distToDoor2 < doorInteractionRange) {
                    lastDoorOpen = true;
                    // door can be walked through
                    wallRectangles.removeValue(lastDoorRect, true);
                    float doorOpenTime = 0.1f;
                    keyCount--;

                    // switching game screens
                    if (lastDoorTransitions) {
                        Gdx.app.postRunnable(new Runnable() {
                            @Override
                            public void run() {
                                if (music1 != null) music1.stop();
                                game.setScreen(new GameScreen2(game));
                            }
                        });
                    }
                }
            }
        }

        // test game level 2
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            game.setScreen(new GameScreen2(game));
        }
    }

    protected void logic(float delta) {
        player.time += Gdx.graphics.getDeltaTime();
        game.timer.update();

        // if the player is daed go to the homescreen
        if (player.isDead) {
            // playerDead.play(0.25f);
            boolean animationDone = player.updateSpriteDead(delta);
            if (animationDone) {
                game.setScreen(new HomeScreen(game));
                // game.setScreen(new GameScreen2(game));
            }
            return;
        }

        if (onLadder) {
            // on ladder, disable gravity and lock x
            player.velocityY = 0;
            player.x = ladderCenterX;
        } else {
            // normal gravity, pull player down
            player.velocityY += gravity * delta;
            player.y += player.velocityY * delta;
        }

        // change players hitbox with the position due to gravity
        float changedHitbox = (player.sprit_size - hitbox_width) / 2f;
        spriteBox.set(player.x + changedHitbox, player.y, hitbox_width, hitbox_height);

        // this is where the player interacts with the collisions*****
        // ceiling loop
        for (Rectangle rectangle : collisionRectangles) {
            float rectBottom = rectangle.y;
            if (spriteBox.overlaps(rectangle) && player.velocityY >= 0 && spriteBox.y < rectBottom) {
                player.velocityY = 0;
                player.y = rectBottom - hitbox_height;
                spriteBox.set(player.x + changedHitbox, player.y, hitbox_width, hitbox_height);
            }
        }

        if (doorOpenTime > 0) {
            doorOpenTime -= delta;
        }

        // floor loop
        for (Rectangle rectangle : collisionRectangles) {
            if (spriteBox.overlaps(rectangle) && player.velocityY <= 0 && !onLadder) {
                player.y = rectangle.y + rectangle.height;
                player.velocityY = 0;
                player.jumping = false;
                spriteBox.set(player.x + changedHitbox, player.y, hitbox_width, hitbox_height);
            }
        }

        // wall loop
        for (Rectangle rectangle : wallRectangles) {
            spriteBox.set(player.x + changedHitbox, player.y, hitbox_width, hitbox_height);

            if (spriteBox.overlaps(rectangle)) {
                float playerCenterX = spriteBox.x + spriteBox.width / 2f;
                float rectCenterX = rectangle.x + rectangle.width / 2f;

                if (playerCenterX < rectCenterX) {
                    player.x = rectangle.x - hitbox_width - changedHitbox;
                } else {
                    player.x = rectangle.x + rectangle.width - changedHitbox;
                }
                spriteBox.set(player.x + changedHitbox, player.y, hitbox_width, hitbox_height);
            }
        }

        // check if on ladder
        touchingLadder = false;
        for (Rectangle ladder : ladderRectangles) {
            if (spriteBox.overlaps(ladder)) {
                touchingLadder = true;
                ladderCenterX = ladder.x + ladder.width / 2f - player.sprit_size / 2f;

                // get off ladder if feet at top
                if (onLadder && spriteBox.y >= ladder.y + ladder.height - hitbox_height) {
                    onLadder = false;
                    player.jumping = false;
                }
                break;
            }
        }

        // if not touching ladder get off
        if (!touchingLadder) {
            onLadder = false;
        }

        // use this for boss battle
        // enemy collision detection
        // if (!player.isDead && karasu != null && !karasu.isRemoved()) {
        // if (karasu.isAttacking() && spriteBox.overlaps(karasu.innerBoundaries) &&
        // !player.isHurt
        // && player.hurtCooldown <= 0f) {
        // player.isHurt = true;
        // player.curr_health -= 10;
        // player.health_percentage = player.curr_health / player.max_health;
        // if(player.curr_health == 0) {
        // player.isDead = true;
        // }
        // player.hurtCooldown = 1.0f;
        //
        // float knockback = 40f;
        // if (player.x < karasu.x) {
        // player.x -= knockback;
        // } else {
        // player.x += knockback;
        // }
        //
        // player.velocityY = 250f;
        // }
        // }

        if (!player.isDead && !player.isHurt && player.hurtCooldown <= 0f) {
            for (Enemy enemy : enemies) {
                if (enemy != null && !enemy.isRemoved()) {
                    if (spriteBox.overlaps(enemy.attackBox)) {
                        player.isHurt = true;
                        player.curr_health -= 10;
                        player.health_percentage = player.curr_health / player.max_health;
                        player.hurtCooldown = 1.0f;
                        if (player.curr_health <= 0) player.isDead = true;
                        float knockback = 40f;
                        player.x += (player.x < enemy.x) ? -knockback : knockback;
                        player.velocityY = 250f;
                    }
                }
            }
        }

        // spike collision detection, set death to true
        if (!player.isDead) {
            for (int i = 0; i < spikeRectangles.size; i++) {
                if (spriteBox.overlaps(spikeRectangles.get(i))) {
                    player.isDead = true;
                    player.velocityY = 0;
                }
            }
        }

        OrthographicCamera cam = (OrthographicCamera) viewport.getCamera();

        for (Enemy enemy : enemies) {
            if (enemy != null && !enemy.isRemoved()) {
                enemy.botLogic(player.x, player.y, delta);
            }
        }

        for (Enemy enemy : enemies) {
            if (enemy != null && enemy.shouldAwardPoints()) {
                game.timer.addPoints(10);
            }
        }

        // level 2 got different width and height
        // float mapWorldWidth = 240 * 16 * scale;
        // float mapWorldHeight = 13 * 16 * scale;
        int mapWidth = map.getProperties().get("width", Integer.class);
        int mapHeight = map.getProperties().get("height", Integer.class);
        int tileWidth = map.getProperties().get("tilewidth", Integer.class);
        int tileHeight = map.getProperties().get("tileheight", Integer.class);
        // System.out.println("PLAYER SPAWN: " + player.x + ", " + player.y);

        float mapWorldWidth = mapWidth * tileWidth * scale;
        float mapWorldHeight = mapHeight * tileHeight * scale;

        float half_of_width = viewport.getWorldWidth() / 2f;
        float half_of_height = viewport.getWorldHeight() / 2f;

        float targetX = player.x + player.sprit_size / 2f;
        float targetY = player.y + player.sprit_size / 2f;

        targetX = Math.max(half_of_width, Math.min(targetX, mapWorldWidth - half_of_width));
        targetY = Math.max(half_of_height, Math.min(targetY, mapWorldHeight - half_of_height));

        cam.position.x += (targetX - cam.position.x) * 0.1f;
        cam.position.y += (targetY - cam.position.y) * 0.1f;
        cam.update();

        game.batch.setProjectionMatrix(cam.combined);

        // Handle Projectile and Enemy Collisions
        for (Enemy enemy : enemies) {
            if (enemy != null && !enemy.isRemoved()) {
                Iterator<Player.Projectile> projectilesIterator = player.projectiles.iterator();
                while (projectilesIterator.hasNext()) {
                    Player.Projectile projectile = projectilesIterator.next();
                    if (projectile.box.overlaps(enemy.innerBoundaries)) {
                        enemy.health -= projectile.damage;
                        skeletonHurt.play(0.40f);
                        enemy.triggerHurt();
                        projectilesIterator.remove();
                    }
                }
            }
        }
    }

    protected void draw(float delta) {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();

        OrthographicCamera cam = (OrthographicCamera) viewport.getCamera();

        // draw the tiled map, renders at scale of 4 so tiles are 64 units each
        renderer.setView(cam);
        renderer.render();

        game.batch.setProjectionMatrix(cam.combined);

        game.batch.begin();

        // draw chests
        float chestWidth = 16 * scale;
        float chestHeight = 32 * scale;
        game.batch.draw(chest1Open ? openChestTexture : closeChestTexture, chest1Position.x, chest1Position.y,
                chestWidth, chestHeight);
        game.batch.draw(chest2Open ? openChestTexture : closeChestTexture, chest2Position.x, chest2Position.y,
                chestWidth, chestHeight);

        // draw doors
        float doorWidth = 16 * scale;
        float doorHeight = 32 * scale;
        game.batch.draw(firstDoorOpen ? openDoorTexture : closeDoorTexture, firstDoorPosition.x, firstDoorPosition.y,
                doorWidth, doorHeight);
        game.batch.draw(lastDoorOpen ? openDoorTexture : closeDoorTexture, lastDoorPosition.x, lastDoorPosition.y,
                doorWidth, doorHeight);

        for (Enemy enemy : enemies) {
            if (enemy != null && !enemy.isRemoved()) {
                enemy.draw(game.batch, enemy.time, delta);
            }
        }

        float drawX;
        float scaleX;

        if (player.facing_right) {
            drawX = player.x;
            scaleX = 1;
        } else {
            drawX = player.x + player.sprit_size;
            scaleX = -1;
        }

        if (player.attacking)
            player.updateSpriteAttack(delta);

        Iterator<Player.Projectile> projectilesIterator = player.projectiles.iterator();
        while (projectilesIterator.hasNext()) {
            Player.Projectile projectile = projectilesIterator.next();
            projectile.update(delta);
            if (projectile.lifetime <= 0)
                projectilesIterator.remove();
            else {
                TextureRegion projectileFrame = projectile.projectileAnimation.getKeyFrame(projectile.animationDuration,
                        true);
                game.batch.draw(projectileFrame, projectile.x, projectile.y, 0, 0, 64, 48, projectile.scaleX, 1, 0);
            }
        }

        game.batch.draw(player.currFrame, drawX, player.y, 0, 0, player.sprit_size, player.sprit_size, scaleX, 1, 0);

        game.batch.end();

        viewportHUD.apply();
        game.batch.setProjectionMatrix(viewportHUD.getCamera().combined);
        player.bar_width = player.health_percentage * player.hpForeground1.getWidth();

        game.batch.begin();
        game.timer.draw(game.batch);
        game.batch.draw(player.hpBackground1, 10, 700, player.hpBackground1.getWidth() * player.w_scale, player.hpBackground1.getHeight());
        game.batch.draw(player.hpForeground1, 11, 700, player.bar_width * player.w_scale, player.hpForeground1.getHeight());
        game.batch.draw(keyTexture, 882, 672, 64f, 64f);
        game.timer.font.draw(game.batch, String.format("%dx", keyCount), 946, 700);
        game.batch.draw(homeButtonTexture, homeButtonX, homeButtonY, homeButtonWidth, homeButtonHeight);
        game.batch.end();

        // Test Projectile and Karasu Hitboxes with these
        // shapeRenderer.setProjectionMatrix(cam.combined);
        // shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        // for (Player.Projectile projectile : player.projectiles) {
        // shapeRenderer.rect(projectile.box.x, projectile.box.y, projectile.box.width,
        // projectile.box.height);
        // }
        // shapeRenderer.end();

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        // left screen
        if (music1 != null) {
            music1.stop();
        }

    }

    @Override
    public void dispose() {
        if (music1 != null) {
            music1.dispose();
        }
        if (homeButtonTexture != null) {
            homeButtonTexture.dispose();
        }

    }
}

package dev.xernas.hydrogen;

import dev.xernas.hydrogen.asset.AssetManager;
import dev.xernas.hydrogen.ecs.SceneManager;
import dev.xernas.hydrogen.rendering.DefaultRenderer;
import dev.xernas.hydrogen.rendering.Renderer;
import dev.xernas.hydrogen.task.Task;
import dev.xernas.hydrogen.task.TaskManager;
import dev.xernas.hydrogen.utils.ui.PositionConverter;
import dev.xernas.hydrogen.utils.ui.UnitHelper;
import dev.xernas.photon.Library;
import dev.xernas.photon.PhotonAPI;
import dev.xernas.photon.api.IRenderer;
import dev.xernas.photon.api.framebuffer.IFramebuffer;
import dev.xernas.photon.api.model.IMesh;
import dev.xernas.photon.api.shader.IShader;
import dev.xernas.photon.api.texture.ITexture;
import dev.xernas.photon.api.window.Window;
import dev.xernas.photon.exceptions.PhotonException;

import static dev.xernas.hydrogen.AppConstants.FRAMETIME;

public abstract class Application {

    private static final AssetManager hydroAssetManager = new AssetManager(Application.class.getClassLoader(), "shaders", "textures");
    private static final TaskManager taskManager = new TaskManager();

    private static boolean running = false;
    private static float deltaTime;
    private static int fps = 0;
    private static IRenderer<IFramebuffer, IShader, IMesh, ITexture> photonRenderer;

    public abstract Library getLibrary();
    public abstract String getName();
    public abstract String getVersion();
    public abstract boolean isDebug();

    public abstract Window getWindow();
    public abstract AssetManager getAssetManager();

    public Renderer getRenderer(IRenderer<IFramebuffer, IShader, IMesh, ITexture> renderer, Window window) {
        return new DefaultRenderer(renderer, window);
    }

    public abstract void onStartup() throws HydrogenException;

    public void onShutdown() {

    }

    public final void launch() throws PhotonException {
        // ---- Setup ---- //
        PhotonAPI.init(getLibrary(), getName(), getVersion(), isDebug());
        Window window = getWindow();
        window.start();

        // Asset manager
        hydroAssetManager.loadShaders();
        hydroAssetManager.loadTextures();
        AssetManager remoteAssetManager = getAssetManager();
        remoteAssetManager.loadShaders();
        remoteAssetManager.loadTextures();

        // UI Inits
        PositionConverter.init(window);
        UnitHelper.init(window);

        // Renderer stuff
        photonRenderer = PhotonAPI.getRenderer(window, false);
        photonRenderer.start();

        Renderer renderer = getRenderer(photonRenderer, window);

        // Scene manager
        onStartup();
        try {
            SceneManager.startup(window, renderer);
        } catch (PhotonException e) {
            throw new HydrogenException("Failed to start SceneManager", e);
        }

        // ---- Main loop ---- //
        running = true;
        long frameCounter = 0;
        int frames = 0;
        long lastTime = System.nanoTime();
        float unprocessedTime = 0;

        // Show window
        window.show();

        // Run loop
        while (running) {
            boolean render = false;
            long startTime = System.nanoTime();
            long passedTime = startTime - lastTime;
            lastTime = startTime;

            deltaTime = (passedTime / (float) AppConstants.SECOND);
            unprocessedTime += deltaTime;
            frameCounter += passedTime;

            // Update window
            window.update(photonRenderer);
            SceneManager.input();

            // Update objects
            while (unprocessedTime > FRAMETIME) {
                render = true;
                unprocessedTime -= FRAMETIME;

                taskManager.getTasks().forEach(Task::tick);

                if (!window.isOpen()) {
                    running = false;
                    return;
                }
                SceneManager.update(); // Update scene objects
                if (frameCounter >= AppConstants.SECOND) {
                    // Executes every second
                    fps = frames;
                    frames = 0;
                    frameCounter = 0;
                }
                taskManager.getTasks().forEach(task -> {
                    float timer = task.getTimer();
                    if (timer == 0) return;
                    if (task.getTickCounter() >= task.getTickInterval()) {
                        task.update(timer);
                        task.resetCounter();
                    }
                });
            }

            if (render) {
                renderer.render(SceneManager.getCurrentScene()); // Render frame with renderer according to scene
                frames++;
            }
        }

        // ---- Shutdown ---- //
        onShutdown();
        SceneManager.shutdown();
        // ---- Cleanup ---- //
        photonRenderer.dispose();
        window.dispose();
    }

    public final void resetPhotonRenderer() throws PhotonException {
        photonRenderer.dispose();
        photonRenderer.start();
    }

    public static void stop() {
        running = false;
    }

    public static boolean isRunning() {
        return running;
    }

    public static float getDeltaTime() {
        return deltaTime;
    }

    public static int getFPS() {
        return fps;
    }

    public static AssetManager getHydrogenAssetManager() {
        return hydroAssetManager;
    }

    public static TaskManager getTaskManager() {
        return taskManager;
    }
}

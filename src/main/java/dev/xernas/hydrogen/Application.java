package dev.xernas.hydrogen;

import dev.xernas.photon.Library;
import dev.xernas.photon.PhotonAPI;
import dev.xernas.photon.api.IRenderer;
import dev.xernas.photon.api.framebuffer.IFramebuffer;
import dev.xernas.photon.api.model.IMesh;
import dev.xernas.photon.api.shader.IShader;
import dev.xernas.photon.api.texture.ITexture;
import dev.xernas.photon.api.window.Window;
import dev.xernas.photon.exceptions.PhotonException;

import java.awt.*;

import static dev.xernas.hydrogen.AppConstants.FRAMETIME;

public abstract class Application {

    private static boolean running = false;
    private static float deltaTime;
    private static int fps = 0;
    private static IRenderer<IFramebuffer, IShader, IMesh, ITexture> photonRenderer;

    public abstract Library getLibrary();
    public abstract String getName();
    public abstract String getVersion();
    public abstract boolean isDebug();

    public abstract Window getWindow();
    public Renderer getRenderer(IRenderer<IFramebuffer, IShader, IMesh, ITexture> renderer) {
        return new DefaultRenderer(renderer);
    }

    public abstract void onStartup();

    public Frame render(Renderer renderer, Frame frame) throws PhotonException {
        return frame;
    }

    public void onShutdown() {

    }

    public final void launch() throws PhotonException {
        // ---- Setup ---- //
        PhotonAPI.init(getLibrary(), getName(), getVersion(), isDebug());
        Window window = getWindow();
        window.start();

        // Renderer stuff
        photonRenderer = PhotonAPI.getRenderer(window, false);
        photonRenderer.start();
        Renderer renderer = getRenderer(photonRenderer);

        // Scene manager
        onStartup();
        try {
            SceneManager.startup(renderer);
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

            // Update objects
            while (unprocessedTime > FRAMETIME) {
                render = true;
                unprocessedTime -= FRAMETIME;
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
            }

            if (render) {
                Frame frame = renderer.render(SceneManager.getCurrentScene()); // Render frame with renderer according to scene
                Frame appFrame = render(renderer, frame); // Application-level rendering
                renderer.display(appFrame); // Display the final frame
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

    public static boolean isRunning() {
        return running;
    }

    public static float getDeltaTime() {
        return deltaTime;
    }

    public static int getFPS() {
        return fps;
    }

}

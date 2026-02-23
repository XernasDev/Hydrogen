package dev.xernas.hydrogen.ecs;

import dev.xernas.hydrogen.Application;
import dev.xernas.hydrogen.HydrogenException;
import dev.xernas.hydrogen.rendering.Renderer;
import dev.xernas.photon.api.window.Window;
import dev.xernas.photon.exceptions.PhotonException;

import java.util.ArrayList;
import java.util.List;

public class SceneManager {


    private static Renderer renderer;
    private static Window window;
    private static final List<Scene> scenes = new ArrayList<>();
    private static int currentSceneIndex = 0;

    public static void startup(Window window, Renderer renderer) throws PhotonException {
        SceneManager.renderer = renderer;
        SceneManager.window = window;
        Scene firstScene;
        try {
            firstScene = getScene(0);
        } catch (HydrogenException e) {
            throw new HydrogenException("No scenes have been created! Create at least one scene before starting the SceneManager.");
        }
        firstScene.load(window, renderer);
    }

    public static void update() {
        getCurrentScene().update();
    }

    public static void input() {
        getCurrentScene().input(window.getInput());
    }

    public static void shutdown() {
        getCurrentScene().stop();
    }

    public static Scene newScene(String name) {
        Scene scene = new Scene(name);
        scenes.add(scene);
        return scene;
    }

    public static Scene getScene(int index) throws HydrogenException {
        try {
            return scenes.get(index);
        } catch (IndexOutOfBoundsException e) {
            throw new HydrogenException("No scene found at index: " + index);
        }
    }

    public static void changeScene(Application app, Scene scene) throws PhotonException {
        //
        if (renderer == null) throw new IllegalStateException("SceneManager not initialized. Call startup() first.");
        scene.load(window, renderer);
        //
        currentSceneIndex = scenes.indexOf(scene);
        app.resetPhotonRenderer();
    }

    public static Scene getCurrentScene() {
        return scenes.get(currentSceneIndex);
    }

}

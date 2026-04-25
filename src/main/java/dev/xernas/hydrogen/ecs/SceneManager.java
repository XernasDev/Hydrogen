package dev.xernas.hydrogen.ecs;

import dev.xernas.hydrogen.Application;
import dev.xernas.hydrogen.HydrogenException;
import dev.xernas.hydrogen.rendering.Renderer;
import dev.xernas.photon.api.window.Window;
import dev.xernas.photon.exceptions.PhotonException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SceneManager {

    private static Application app;
    private static Renderer renderer;
    private static Window window;
    private static final List<Scene> scenes = new ArrayList<>();
    private static final Map<String, Scene> sceneByName = new HashMap<>();
    private static int currentSceneIndex = 0;

    public static void startup(Application app, Window window, Renderer renderer) throws PhotonException {
        SceneManager.app = app;
        SceneManager.renderer = renderer;
        SceneManager.window = window;
        Scene firstScene;
        try {
            firstScene = getScene(0);
        } catch (HydrogenException e) {
            throw new HydrogenException("No scenes have been created! Create at least one scene before starting the SceneManager.");
        }
        firstScene.load(app, window, renderer);
    }

    public static void update() {
        getCurrentScene().update();
    }

    public static void input() {
        getCurrentScene().input(window.getInput());
    }

    public static void shutdown() throws PhotonException {
        getCurrentScene().stop();
    }

    public static Scene newScene(String name) {
        Scene scene = new Scene(name);
        scenes.add(scene);
        sceneByName.put(name, scene);
        return scene;
    }

    public static Scene getScene(int index) throws HydrogenException {
        try {
            return scenes.get(index);
        } catch (IndexOutOfBoundsException e) {
            throw new HydrogenException("No scene found at index: " + index);
        }
    }

    public static Scene getScene(String name) throws HydrogenException {
        Scene scene = sceneByName.get(name);
        if (scene == null) throw new HydrogenException("No scene found for name : " + name);
        return scene;
    }

    public static void changeScene(Scene scene) throws PhotonException {
        if (renderer == null) throw new IllegalStateException("SceneManager not initialized. Call startup() first.");

        getCurrentScene().stop();
        scene.load(app, window, renderer);
        currentSceneIndex = scenes.indexOf(scene);
    }

    public static void changeScene(int index) throws PhotonException {
        Scene scene = getScene(index);
        changeScene(scene);
    }

    public static void changeScene(String sceneName) throws PhotonException {
        Scene scene = getScene(sceneName);
        changeScene(scene);
    }

    public static Scene getCurrentScene() {
        return scenes.get(currentSceneIndex);
    }

}

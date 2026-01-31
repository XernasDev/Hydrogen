package dev.xernas.hydrogen;

import dev.xernas.photon.exceptions.PhotonException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SceneManager {


    private static Renderer renderer;
    private static final List<Scene> scenes = new ArrayList<>();

    public static void startup(Renderer renderer) throws PhotonException {
        SceneManager.renderer = renderer;
        Scene firstScene;
        try {
            firstScene = getScene(0);
        } catch (HydrogenException e) {
            throw new HydrogenException("No scenes have been created! Create at least one scene before starting the SceneManager.");
        }
        renderer.loadScene(firstScene);
    }

    public static void update() {

    }

    public static void shutdown() {

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
        renderer.loadScene(scene);
        //
        app.resetPhotonRenderer();
    }

    public static Scene getCurrentScene() {
        return null;
    }

}

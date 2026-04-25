package dev.xernas.hydrogen.rendering;

import dev.xernas.hydrogen.ecs.Actor;
import dev.xernas.hydrogen.ecs.Scene;
import dev.xernas.photon.exceptions.PhotonException;

import java.awt.*;

public interface Renderer {

    void render(Scene scene) throws PhotonException;

    void loadScene(Scene scene) throws PhotonException;

    void loadActor(Actor actor) throws PhotonException;

    void unloadScene() throws PhotonException;

    void unloadActor(Actor actor) throws PhotonException;

    void reloadActor(Actor actor);

    void setClearColor(Color color);

    default void changeScene(Scene scene) throws PhotonException {
        unloadScene();
        loadScene(scene);
    }

}

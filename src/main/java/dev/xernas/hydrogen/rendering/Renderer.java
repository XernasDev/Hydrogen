package dev.xernas.hydrogen.rendering;

import dev.xernas.hydrogen.HydrogenException;
import dev.xernas.hydrogen.ecs.Actor;
import dev.xernas.hydrogen.ecs.Scene;
import dev.xernas.photon.api.model.Model;
import dev.xernas.photon.api.texture.ITexture;
import dev.xernas.photon.exceptions.PhotonException;

public interface Renderer {

    void render(Scene scene) throws PhotonException;

    void loadScene(Scene scene) throws PhotonException;

    void loadActor(Actor actor) throws PhotonException;

    void unloadScene() throws PhotonException;

    void unloadActor(Actor actor) throws PhotonException;

    default void reloadActor(Actor actor) throws PhotonException {
        unloadActor(actor);
        loadActor(actor);
    }

    default void changeScene(Scene scene) throws PhotonException {
        unloadScene();
        loadScene(scene);
    }

}

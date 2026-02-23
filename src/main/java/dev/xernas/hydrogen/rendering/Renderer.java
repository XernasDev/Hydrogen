package dev.xernas.hydrogen.rendering;

import dev.xernas.hydrogen.ecs.Scene;
import dev.xernas.photon.exceptions.PhotonException;

public interface Renderer {

    Frame render(Scene scene) throws PhotonException;

    void display(Frame frame) throws PhotonException;

    void loadScene(Scene scene) throws PhotonException;

}

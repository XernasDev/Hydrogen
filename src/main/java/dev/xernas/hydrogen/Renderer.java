package dev.xernas.hydrogen;

import dev.xernas.photon.exceptions.PhotonException;

import java.awt.*;

public interface Renderer {

    Frame render(Scene scene) throws PhotonException;

    void display(Frame frame) throws PhotonException;

    void loadScene(Scene scene) throws PhotonException;

}

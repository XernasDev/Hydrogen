package dev.xernas.hydrogen;

import dev.xernas.photon.api.texture.Texture;

import java.awt.*;

public interface Material {

    Color getBaseColor();

    Texture getTexture();

}

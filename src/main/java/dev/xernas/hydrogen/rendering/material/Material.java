package dev.xernas.hydrogen.rendering.material;

import dev.xernas.photon.api.texture.Texture;

import java.awt.*;

public interface Material {

    Color getBaseColor();

    Texture getTexture();

}

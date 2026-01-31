package dev.xernas.hydrogen;

import dev.xernas.photon.api.texture.Texture;

import java.awt.*;

public class ColorMaterial implements Material {

    private Color color;

    public ColorMaterial(Color color) {
        this.color = color;
    }

    @Override
    public Color getBaseColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public Texture getTexture() {
        return null;
    }
}

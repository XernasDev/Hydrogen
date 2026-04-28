package dev.xernas.hydrogen.ecs.ui;

import dev.xernas.hydrogen.asset.Asset;
import dev.xernas.hydrogen.ecs.Actor;
import dev.xernas.hydrogen.ecs.module.RenderingModule;
import dev.xernas.hydrogen.rendering.material.Material;
import dev.xernas.hydrogen.utils.ui.UnitHelper;
import dev.xernas.microscope.format.FontFormat;
import dev.xernas.photon.api.model.Model;
import dev.xernas.photon.api.texture.Texture;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntSupplier;

public class Text extends Actor {

    private static float pointer = 0;

    private final List<CharActor> charActors = new ArrayList<>();
    private final float size;
    private final Asset.FontAsset font;
    private final Color color;
    private final IntSupplier x;
    private final IntSupplier y;
    private String string;

    public Text(String string, IntSupplier x, IntSupplier y, float size, Asset.FontAsset font, Color color) {
        this.string = string;
        this.x = x;
        this.y = y;
        this.size = size;
        this.font = font;
        this.color = color;
        for (int i = 0; i < string.length(); i++) {
            char character = string.charAt(i);
            final float currentPointer = pointer;
            CharActor charActor = new CharActor(character, UnitHelper.add(x, () -> Math.round(currentPointer)), y, size, font, color);
            newChild(charActor);
            charActors.add(charActor);
        }

        UnitHelper.setContextSize(getWidth());

        pointer = 0;
    }

    public Text(String string, IntSupplier x, IntSupplier y, float size, Asset.FontAsset font) {
        this(string, x, y, size, font, null);
    }

    public Text(String string, int x, int y, float size, Asset.FontAsset font, Color color) {
        this(string, () -> x, () -> y, size, font, color);
    }

    public Text(String string, int x, int y, float size, Asset.FontAsset font) {
        this(string, x, y, size, font, null);
    }

    public int getX() {
        return x.getAsInt();
    }

    public int getY() {
        return y.getAsInt();
    }

    public int getWidth() {
        int widthSum = 0;
        for (CharActor charActor : charActors) widthSum += charActor.getWidth();
        return widthSum;
    }

    public int getHeight() {
        return charActors.getFirst().getHeight();
    }

    public String getString() {
        return string;
    }

    public void setString(String newString) {
        this.string = newString;
        destroyAllChildren();
        charActors.clear();
        for (int i = 0; i < newString.length(); i++) {
            char character = newString.charAt(i);
            final float currentPointer = pointer;
            CharActor charActor = new CharActor(character, UnitHelper.add(x, () -> Math.round(currentPointer)), y, size, font, color);
            instantiateChild(charActor);
            charActors.add(charActor);
        }

        pointer = 0;
    }

    public static class CharActor extends Actor {

        private int width;
        private int height;

        public CharActor(char character, IntSupplier x, IntSupplier y, float size, Asset.FontAsset font, Color color) {
            super(new UITransform(0, 0, 0, 0));

            switch (font.getType()) {
                case BITMAP -> {
                    Asset.BitmapFontAsset bitmapFont = (Asset.BitmapFontAsset) font;
                    FontFormat format = bitmapFont.getFormat();
                    FontFormat.Glyph glyph = format.getGlyph(character);
                    Model model = getCharModel(glyph, format.getScaleW(), format.getScaleH());
                    model.usePerspective(false);
                    model.flipV();
                    Texture texture = bitmapFont.getTexture().setNearestFiltering(true);
                    Color finalColor = color != null ? color : Color.BLACK;
                    RenderingModule rm = new RenderingModule("text", model, new Material() {
                        @Override
                        public Color getBaseColor() {
                            return finalColor;
                        }

                        @Override
                        public Texture getTexture() {
                            return texture;
                        }
                    });
                    newModules(rm);

                    UITransform transform = (UITransform) getTransform();
                    float xPos = ((float) glyph.xOffset() / (float) format.getSize()) * size;
                    float yPos = ((float) glyph.yOffset() / (float) format.getSize()) * size;
                    transform.setX(UnitHelper.add(x, () -> Math.round(xPos)));
                    transform.setY(UnitHelper.add(y, () -> Math.round(yPos)));

                    this.width = Math.round((float) glyph.width() / format.getSize() * size);
                    this.height = Math.round((float) glyph.height() / format.getSize() * size);
                    transform.setWidth(width);
                    transform.setHeight(height);

                    pointer += ((float) glyph.xAdvance() / format.getSize()) * size;
                }
            }
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        private Model getCharModel(FontFormat.Glyph glyph, int atlasW, int atlasH) {
            float halfSize = 0.5f;
            float u0 = (float) glyph.x() / atlasW;
            float v0 = (float) glyph.y() / atlasH;

            float u1 = (float) (glyph.x() + glyph.width()) / atlasW;
            float v1 = (float) (glyph.y() + glyph.height()) / atlasH;

            Model.Vertex[] vertices = {
                    new Model.Vertex(-halfSize, -halfSize, 0.0f, u0, v1, 0.0f, 0.0f, 1.0f),
                    new Model.Vertex(halfSize, -halfSize, 0.0f, u1, v1, 0.0f, 0.0f, 1.0f),
                    new Model.Vertex(halfSize, halfSize, 0.0f, u1, v0, 0.0f, 0.0f, 1.0f),
                    new Model.Vertex(-halfSize, halfSize, 0.0f, u0, v0, 0.0f, 0.0f, 1.0f)
            };
            int[] indices = {
                    0, 1, 2,
                    2, 3, 0
            };

            return new Model("Char-" + glyph.x() + "-" + glyph.y() + "-" + glyph.width() + "-" + glyph.height() + "-" + atlasW + "-" + atlasH, vertices, indices);
        }
    }

}

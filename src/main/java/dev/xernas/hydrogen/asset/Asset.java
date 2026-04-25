package dev.xernas.hydrogen.asset;

import dev.xernas.photon.api.shader.Shader;
import dev.xernas.photon.api.texture.Texture;

public class Asset {

    private final String path;
    private final String name;
    private final AssetManager owner;

    public Asset(String path, String name, AssetManager owner) {
        this.path = path;
        this.name = name;
        this.owner = owner;
    }

    public String getPath() {
        return path;
    }

    public String getName() {
        String[] split = name.split("\\.");
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < split.length; i++) sb.append(split[i]);
        return sb.toString();
    }

    public AssetManager getOwner() {
        return owner;
    }

    public static class ShaderAsset extends Asset {

        private final Shader shader;

        public ShaderAsset(String path, String name, AssetManager owner, Shader shader) {
            super(path, name, owner);
            this.shader = shader;
        }

        public Shader getShader() {
            return shader;
        }
    }

    public static class TextureAsset extends Asset {

        private final Texture texture;

        public TextureAsset(String path, String name, AssetManager owner, Texture texture) {
            super(path, name, owner);
            this.texture = texture;
        }

        public Texture getTexture() {
            return texture;
        }
    }

}

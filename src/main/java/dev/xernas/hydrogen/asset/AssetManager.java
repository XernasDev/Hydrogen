package dev.xernas.hydrogen.asset;

import dev.xernas.atom.file.FileUtils;
import dev.xernas.hydrogen.Application;
import dev.xernas.hydrogen.HydrogenException;
import dev.xernas.microscope.MicroscopeException;
import dev.xernas.microscope.format.FontFormat;
import dev.xernas.microscope.helper.PathHelper;
import dev.xernas.microscope.reader.FontReader;
import dev.xernas.microscope.reader.ImageReader;
import dev.xernas.photon.api.shader.Shader;
import dev.xernas.photon.api.texture.Texture;
import dev.xernas.photon.utils.ShaderResource;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.*;

public class AssetManager {

    private static final Map<String, Asset> loadedAssets = new HashMap<>();

    private final ClassLoader classLoader;
    private final String shadersDirectory;
    private final String texturesDirectory;
    private final String fontsDirectory;

    public AssetManager(ClassLoader classLoader, String shadersDirectory, String texturesDirectory, String fontsDirectory) {
        this.classLoader = classLoader;
        this.shadersDirectory = shadersDirectory;
        this.texturesDirectory = texturesDirectory;
        this.fontsDirectory = fontsDirectory;
    }

    public static <T extends Asset> T getAssetByName(String name) throws HydrogenException {
        Asset asset = searchForAssetWithName(name.toLowerCase());
        if (asset == null) throw new HydrogenException("No asset found with name: " + name);
        try {
            return (T) asset;
        } catch (ClassCastException e) {
            throw new HydrogenException("Asset with name " + name + " is not of the expected type.");
        }
    }

    private static Asset searchForAssetWithName(String name) {
        Asset asset = loadedAssets.get(name);
        if (asset != null) return asset;
        Asset shaderAsset = loadedAssets.get("shader." + name);
        if (shaderAsset != null) return shaderAsset;
        Asset textureAsset = loadedAssets.get("texture." + name);
        if (textureAsset != null) return textureAsset;
        Asset fontAsset = loadedAssets.get("font." + name);
        if (fontAsset != null) return fontAsset;
        return null;
    }

    private static void loadAsset(Asset asset) {
        loadedAssets.put(asset.getRawName().toLowerCase(), asset);
    }

    private Path getFilePath(String resourcePath, boolean isDirectory) throws HydrogenException {
        try {
            return PathHelper.getResourcePath(classLoader, resourcePath);
        } catch (FileNotFoundException fe) {
            if (!isDirectory) throw new HydrogenException("File not found: " + resourcePath);
            else return null;
        } catch (URISyntaxException ue) {
            throw new HydrogenException("Invalid file path: " + resourcePath);
        }
    }

    private String readPathString(Path path) throws HydrogenException {
        try {
            return PathHelper.getStringOf(path);
        } catch (IOException ie) {
            throw new HydrogenException("Failed to read file: " + path);
        }
    }

    private InputStream getPathInputStream(Path path) throws HydrogenException {
        try {
            return PathHelper.getStream(path);
        } catch (IOException ie) {
            throw new HydrogenException("Failed to read file: " + path);
        }
    }

    private ShaderResource loadShaderResource(String path) throws HydrogenException {
        Path resourcePath = getFilePath(shadersDirectory + "/" + path, false);
        String shaderCode = readPathString(resourcePath);

        return new ShaderResource(Objects.requireNonNull(resourcePath).getFileName().toString(), shaderCode);
    }

    private void loadFont(Path path) throws HydrogenException {
        String fontJson = readPathString(path);
        JSONTokener tokener = new JSONTokener(fontJson);
        JSONObject root = new JSONObject(tokener);
        String fontName = root.getString("name");
        if (isAssetLoaded("font." + fontName.toLowerCase())) throw new HydrogenException("Font with name " + fontName + " already loaded.");
        String assetName = "font." + fontName;
        Asset.FontAsset.FontType fontType = Asset.FontAsset.FontType.valueOf(root.getString("type").toUpperCase());

        Asset.FontAsset fontAsset = null;
        switch (fontType) {
            case BITMAP -> {
                String fntPath = root.getString("fnt_info");
                String texPath = root.getString("fnt_tex");
                Path bitmapFontFilePath = getFilePath(fontsDirectory + "/" + fntPath, false);
                Path bitmapFontTexturePath = getFilePath(fontsDirectory + "/" + texPath, false);

                FontFormat format = readBitmapFont(bitmapFontFilePath);
                Texture texture = readTexture(bitmapFontTexturePath);
                fontAsset = new Asset.BitmapFontAsset(path.toString(), assetName, this, format, texture);
            }
        }

        if (fontAsset == null) throw new HydrogenException("Couldn't find a compatible font type");

        loadAsset(fontAsset);
    }

    public void loadFonts() throws HydrogenException {
        Path fontsDirPath = getFilePath(fontsDirectory, true);
        if (fontsDirPath == null) return;
        try {
            Set<Path> fontFiles = PathHelper.list(fontsDirPath);
            for (Path fontFile : fontFiles) {
                String ext = FileUtils.getFileExtension(fontFile.getFileName().toString());
                if (!ext.equalsIgnoreCase("json")) continue;
                loadFont(fontFile);
            }
        } catch (IOException e) {
            throw new HydrogenException("Failed to list font files in directory: " + texturesDirectory, e);
        }
    }

    private FontFormat readBitmapFont(Path fntPath) throws HydrogenException {
        InputStream fntStream = getPathInputStream(fntPath);
        try {
            FontReader reader = new FontReader(fntStream);
            return reader.getFormat();
        } catch (IOException | MicroscopeException e) {
            throw new HydrogenException("Failed to read the bitmap font", e);
        }
    }

    private Texture readTexture(Path path) throws HydrogenException {
        InputStream textureInput = getPathInputStream(path);
        try {
            ImageReader reader = new ImageReader(textureInput);
            return new Texture(reader.getWidth(), reader.getHeight(), reader.getData());
        } catch (IOException e) {
            throw new HydrogenException("Failed to read the texture file", e);
        }
    }

    private void loadTexture(Path path) throws HydrogenException {
        Texture texture = readTexture(path);
        String assetName = "texture." + path.getFileName().toString().toLowerCase();
        if (isAssetLoaded(assetName)) throw new HydrogenException("Texture with name " + path.getFileName().toString().toLowerCase() + " is already loaded");
        Asset.TextureAsset asset = new Asset.TextureAsset(path.toString(), assetName, this, texture);
        loadAsset(asset);
    }

    public void loadTextures() throws HydrogenException {
        Path texturesDirPath = getFilePath(texturesDirectory, true);
        if (texturesDirPath == null) return;
        try {
            Set<Path> textureFiles = PathHelper.list(texturesDirPath);
            for (Path textureFile : textureFiles) {
                String ext = FileUtils.getFileExtension(textureFile.getFileName().toString());
                if (!ext.equalsIgnoreCase("png") && !ext.equalsIgnoreCase("jpg") && !ext.equalsIgnoreCase("jpeg")) continue;
                loadTexture(textureFile);
            }
        } catch (IOException e) {
            throw new HydrogenException("Failed to list texture files in directory: " + texturesDirectory, e);
        }
    }

    private void loadShader(Path path) throws HydrogenException {
        String shaderJson = readPathString(path);
        JSONTokener tokener = new JSONTokener(shaderJson);
        JSONObject root = new JSONObject(tokener);
        String shaderName = root.getString("name");
        if (isAssetLoaded("shader." + shaderName)) throw new HydrogenException("Shader with name " + shaderName + " already loaded.");
        JSONObject vertex = root.getJSONObject("vertex");
        JSONObject fragment = root.getJSONObject("fragment");
        boolean vertexFromHydro = vertex.getBoolean("fromHydrogen");
        boolean fragmentFromHydro = fragment.getBoolean("fromHydrogen");
        ShaderResource vertexResource = vertexFromHydro ? Application.getHydrogenAssetManager().loadShaderResource(vertex.getString("path")) : loadShaderResource(vertex.getString("path"));
        ShaderResource fragmentResource = fragmentFromHydro ? Application.getHydrogenAssetManager().loadShaderResource(fragment.getString("path")) : loadShaderResource(fragment.getString("path"));
        Shader shader = new Shader(shaderName, vertexResource, fragmentResource);
        String assetName = "shader." + shaderName;
        Asset.ShaderAsset shaderAsset = new Asset.ShaderAsset(path.toString(), assetName, this, shader);
        loadAsset(shaderAsset);
    }

    public void loadShaders() throws HydrogenException {
        Path shadersDirPath = getFilePath(shadersDirectory, true);
        if (shadersDirPath == null) return;
        try {
            Set<Path> shaderFiles = PathHelper.list(shadersDirPath);
            for (Path shaderFile : shaderFiles) {
                if (!FileUtils.getFileExtension(shaderFile.getFileName().toString()).equalsIgnoreCase("json")) continue;
                loadShader(shaderFile);
            }
        } catch (IOException e) {
            throw new HydrogenException("Failed to list shader files in directory: " + shadersDirectory, e);
        }
    }

    public boolean isAssetLoaded(String name) {
        Asset asset = searchForAssetWithName(name);
        return asset != null && asset.getOwner() == this;
    }

}

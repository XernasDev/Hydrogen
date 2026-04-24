package dev.xernas.hydrogen.asset;

import dev.xernas.atom.file.FileUtils;
import dev.xernas.hydrogen.Application;
import dev.xernas.hydrogen.HydrogenException;
import dev.xernas.microscope.helper.PathHelper;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AssetManager {

    private static final Map<String, Asset> loadedAssets = new HashMap<>();

    private final ClassLoader classLoader;
    private final String shadersDirectory;
    private final String texturesDirectory;

    public AssetManager(ClassLoader classLoader, String shadersDirectory, String texturesDirectory) {
        this.classLoader = classLoader;
        this.shadersDirectory = shadersDirectory;
        this.texturesDirectory = texturesDirectory;
    }

    public static <T extends Asset> T getAssetByName(String name) throws HydrogenException {
        Asset asset = loadedAssets.get(name);
        if (asset == null) throw new HydrogenException("No asset found with name: " + name);
        try {
            return (T) asset;
        } catch (ClassCastException e) {
            throw new HydrogenException("Asset with name " + name + " is not of the expected type.");
        }
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

        return new ShaderResource(resourcePath.getFileName().toString(), shaderCode);
    }

    private void loadTexture(Path path) throws HydrogenException {
        InputStream textureInput = getPathInputStream(path);
        try {
            ImageReader reader = new ImageReader(textureInput);
            Texture texture = new Texture(reader.getWidth(), reader.getHeight(), reader.getData());
            Asset.TextureAsset asset = new Asset.TextureAsset(path.toString(), path.getFileName().toString(), this, texture);
            loadedAssets.put(asset.getName(), asset);
        } catch (IOException e) {
            throw new HydrogenException("Failed to read the texture file", e);
        }
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
        if (isAssetLoaded(shaderName)) throw new HydrogenException("Shader with name " + shaderName + " already loaded.");
        JSONObject vertex = root.getJSONObject("vertex");
        JSONObject fragment = root.getJSONObject("fragment");
        boolean vertexFromHydro = vertex.getBoolean("fromHydrogen");
        boolean fragmentFromHydro = fragment.getBoolean("fromHydrogen");
        ShaderResource vertexResource = vertexFromHydro ? Application.getHydrogenAssetManager().loadShaderResource(vertex.getString("path")) : loadShaderResource(vertex.getString("path"));
        ShaderResource fragmentResource = fragmentFromHydro ? Application.getHydrogenAssetManager().loadShaderResource(fragment.getString("path")) : loadShaderResource(fragment.getString("path"));
        Shader shader = new Shader(shaderName, vertexResource, fragmentResource);
        Asset.ShaderAsset shaderAsset = new Asset.ShaderAsset(path.toString(), shaderName, this, shader);
        loadedAssets.put(shaderName, shaderAsset);
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

    public boolean isAssetLoaded(String name) throws HydrogenException {
        Asset asset = loadedAssets.get(name);
        return asset != null && asset.getOwner() == this;
    }

}

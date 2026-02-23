package dev.xernas.hydrogen.rendering;

import dev.xernas.hydrogen.*;
import dev.xernas.hydrogen.asset.Asset;
import dev.xernas.hydrogen.asset.AssetManager;
import dev.xernas.hydrogen.ecs.Actor;
import dev.xernas.hydrogen.ecs.module.RenderingModule;
import dev.xernas.hydrogen.ecs.Scene;
import dev.xernas.hydrogen.rendering.material.Material;
import dev.xernas.photon.api.IRenderer;
import dev.xernas.photon.api.Transform;
import dev.xernas.photon.api.framebuffer.IFramebuffer;
import dev.xernas.photon.api.model.IMesh;
import dev.xernas.photon.api.model.Model;
import dev.xernas.photon.api.shader.IShader;
import dev.xernas.photon.api.shader.Shader;
import dev.xernas.photon.api.texture.ITexture;
import dev.xernas.photon.api.window.Window;
import dev.xernas.photon.exceptions.PhotonException;
import dev.xernas.photon.utils.MatrixUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultRenderer implements Renderer {

    private final IRenderer<IFramebuffer, IShader, IMesh, ITexture> renderer;
    private final Window window;

    private final List<Scene> loadedScenes;
    private final Map<RenderingModule, RenderingData> loadedModules;

    public DefaultRenderer(IRenderer<IFramebuffer, IShader, IMesh, ITexture> renderer, Window window) {
        this.renderer = renderer;
        this.window = window;
        this.loadedScenes = new ArrayList<>();
        this.loadedModules = new HashMap<>();
    }

    @Override
    public Frame render(Scene scene) throws PhotonException {
        if (isSceneNotLoaded(scene)) throw new HydrogenException("Scene " + scene.getName() + " not loaded");
        renderer.clear(AppConstants.APP_COLOR);
        Map<Actor, RenderingData> renderingData = new HashMap<>();
        for (Actor renderableActor : scene.getActorsWithModule(RenderingModule.class)) {
            RenderingModule renderingModule = renderableActor.getModule(RenderingModule.class);
            RenderingData data = loadedModules.get(renderingModule);
            if (data != null) renderingData.put(renderableActor, data);
            else throw new HydrogenException("Rendering module not loaded for actor " + renderableActor.getName());
        }
        for (Map.Entry<Actor, RenderingData> entry : renderingData.entrySet()) {
            Actor actor = entry.getKey();
            RenderingData data = entry.getValue();
            renderer.render(data.getShader(), data.getMesh(), ((mesh, shader) -> {
                // Vertex
                shader.setUniform("u_modelMatrix", MatrixUtils.createTransformationMatrix(actor.getTransform()));
                if (scene.is3D()) {
                    shader.setUniform("u_viewProjectionMatrix",
                            MatrixUtils.createProjectionMatrix(window, 80, 0.1f, 1000f)
                                    .mul(MatrixUtils.createViewMatrix((Transform.CameraTransform) scene.getCameraActor().getTransform())));
                }
                else {
                    shader.setUniform("u_viewProjectionMatrix",
                            MatrixUtils.createOrthoMatrix(window)
                                    .mul(MatrixUtils.create2DViewMatrix((Transform.CameraTransform) scene.getCameraActor().getTransform())));
                }
                shader.setUniform("u_cameraWorldPos", scene.getCameraActor().getTransform().getPosition());

                // Fragment
                shader.setUniform("u_color", data.getMaterial().getBaseColor());
                if (data.getTexture() != null) {
                    shader.setUniform("u_useTexture", true);
                    shader.useTexture("u_sampler", data.getTexture(), 0);
                }
                else shader.setUniform("u_useTexture", false);
            }));
        }

        return new Frame(null);
    }

    @Override
    public void display(Frame frame) throws PhotonException {
//        AssetManager.ShaderAsset shaderAsset = AssetManager.getAssetByName("hydroScreen");
//        IShader shader = renderer.loadShader(shaderAsset.getShader());
//        IMesh mesh = renderer.loadMesh(Models.createQuad());
//        renderer.render(shader, mesh, (m, s) -> {
//            s.useTexture("u_ScreenTexture", frame.getTexture(), 0);
//        });
    }

    @Override
    public void loadScene(Scene scene) throws PhotonException {
        for (Actor renderableActor : scene.getActorsWithModule(RenderingModule.class)) {
            RenderingModule renderingModule = renderableActor.getModule(RenderingModule.class);
            if (isModuleLoaded(renderingModule)) continue;

            Asset.ShaderAsset shaderAsset = AssetManager.getAssetByName(renderingModule.shader());
            if (shaderAsset == null) throw new HydrogenException("Shader not found: " + renderingModule.shader());
            Shader shader = shaderAsset.getShader();
            Model model = renderingModule.model();
            if (model.is3D()) scene.set3D(true);
            Material material = renderingModule.material();

            RenderingData data = new RenderingData(shader, model, material, renderer);
            loadedModules.put(renderingModule, data);
        }
        if (isSceneNotLoaded(scene)) loadedScenes.add(scene);
    }

    public boolean isSceneNotLoaded(Scene scene) {
        if (loadedScenes.contains(scene)) {
            for (Actor renderableActor : scene.getActorsWithModule(RenderingModule.class)) {
                RenderingModule renderingModule = renderableActor.getModule(RenderingModule.class);
                if (!isModuleLoaded(renderingModule)) {
                    return true;
                }
            }
        }
        else return true;
        return false;
    }

    private boolean isModuleLoaded(RenderingModule module) {
        return loadedModules.containsKey(module);
    }

}

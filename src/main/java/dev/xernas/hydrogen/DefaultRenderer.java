package dev.xernas.hydrogen;

import dev.xernas.photon.api.IRenderer;
import dev.xernas.photon.api.framebuffer.IFramebuffer;
import dev.xernas.photon.api.model.IMesh;
import dev.xernas.photon.api.model.Model;
import dev.xernas.photon.api.shader.IShader;
import dev.xernas.photon.api.shader.Shader;
import dev.xernas.photon.api.texture.ITexture;
import dev.xernas.photon.exceptions.PhotonException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultRenderer implements Renderer {

    private final IRenderer<IFramebuffer, IShader, IMesh, ITexture> renderer;

    private final List<Scene> loadedScenes;
    private final Map<RenderingModule, RenderingData> loadedModules;

    public DefaultRenderer(IRenderer<IFramebuffer, IShader, IMesh, ITexture> renderer) {
        this.renderer = renderer;
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
                //TODO: Apply uniforms like transform, texture, etc.
            }));
        }

        return null;
    }

    @Override
    public void display(Frame frame) throws PhotonException {
//        IShader shader = renderer.loadShader(AssetManager.getShaderByName("hydroScreen"));
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

            Shader shader = AssetManager.getShaderByName(renderingModule.shader());
            Model model = renderingModule.model();
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

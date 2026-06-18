package dev.xernas.hydrogen.ecs;

import dev.xernas.hydrogen.Application;
import dev.xernas.hydrogen.HydrogenException;
import dev.xernas.hydrogen.ecs.module.GlobalModule;
import dev.xernas.hydrogen.ecs.module.Module;
import dev.xernas.hydrogen.rendering.Renderer;
import dev.xernas.hydrogen.rendering.RenderingData;
import dev.xernas.photon.api.IRenderer;
import dev.xernas.photon.api.Transform;
import dev.xernas.photon.api.framebuffer.IFramebuffer;
import dev.xernas.photon.api.model.IMesh;
import dev.xernas.photon.api.shader.IShader;
import dev.xernas.photon.api.texture.ITexture;
import dev.xernas.photon.api.window.Window;
import dev.xernas.photon.api.window.input.Input;

import java.util.*;

public class Actor {

    private final String name;
    private final Transform transform;
    private final Map<Class<? extends Module>, Module> modules;
    private final Map<String, Actor> children = new HashMap<>();

    private final List<GlobalModule> childrenGlobalModules = new ArrayList<>();

    private Scene scene = null;
    private Actor parent = null;


    public Actor(Module... modules) {
        this(new Transform(), modules);
    }

    public Actor(Transform transform, Module... modules) {
        this(null, transform, modules);
    }

    public Actor(String name, Module... modules) {
        this(name, new Transform(), modules);
    }

    public Actor(String name, Transform transform, Module... modules) {
        this.name = name == null ? UUID.randomUUID().toString() : name;
        this.transform = transform;
        this.modules = new HashMap<>();
        for (Module module : modules) {
            this.modules.put(module.getClass(), module);
        }
    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public void newModules(Module... modules) {
        for (Module module : modules) this.modules.put(module.getClass(), module);
    }

    public Actor newChild(Actor actor) {
        this.children.put(actor.getName(), actor);
        actor.setParent(this);
        if (actor.hasModule(GlobalModule.class)) childrenGlobalModules.add(actor.getModule(GlobalModule.class));
        childrenGlobalModules.addAll(actor.getChildrenGlobalModules());
        return this;
    }

    public void newChildren(Actor... actors) {
        for (Actor actor : actors) newChild(actor);
    }

    public void instantiateChild(Actor actor) throws HydrogenException {
        newChild(actor);
        scene.instantiate(actor);
    }

    public void destroyAllChildren() {
        children.forEach((string, actor) -> {
            scene.destroyActor(actor);
            actor.setParent(null);
            if (actor.hasModule(GlobalModule.class)) childrenGlobalModules.remove(actor.getModule(GlobalModule.class));
        });
        children.clear();
    }

    public List<GlobalModule> getChildrenGlobalModules() {
        return childrenGlobalModules;
    }

    public Actor getParent() {
        return parent;
    }

    public void setParent(Actor parent) {
        this.parent = parent;
    }

    public boolean isChild() {
        return parent != null;
    }

    public String getName() {
        return name;
    }

    public Transform getTransform() {
        return transform;
    }

    public Actor hasCamera() {
        if (transform instanceof Transform.CameraTransform) return this;
        for (Actor child : children.values()) {
            Actor camera = child.hasCamera();
            if (camera != null) return camera;
        }
        return null;
    }

    public void start(Application app, Window window, Renderer renderer) throws HydrogenException {
        for (Module module : modules.values()) module.enable(app, this, window, renderer);
        for (Actor child : children.values()) child.start(app, window, renderer);
    }

    public void update() throws HydrogenException {
        for (Module module : new ArrayList<>(modules.values())) if (module.isActive()) module.onUpdate();
        for (Actor child : new ArrayList<>(children.values())) child.update();
    }

    public void input() throws HydrogenException {
        for (Module module : new ArrayList<>(modules.values())) if (module.isActive()) module.onInput();
        for (Actor child : new ArrayList<>(children.values())) child.input();
    }

    public void render(RenderingData data, IRenderer<IFramebuffer, IShader, IMesh, ITexture> renderer) {
        modules.values().forEach(module -> module.onRender(data, renderer));
        children.values().forEach(child -> child.render(data, renderer));
    }

    public void stop() {
        children.values().forEach(Actor::stop);
        modules.values().forEach(Module::onStop);
        modules.clear();
    }

    public Actor getChild(String name) {
        return children.get(name);
    }

    public Actor findFirstChildWithModule(Class<? extends Module> moduleClass) {
        List<Actor> childrenWithModule = getChildrenWithModule(moduleClass);
        if (!childrenWithModule.isEmpty()) return childrenWithModule.getFirst();
        return null;
    }

    public List<Actor> getChildrenWithModule(Class<? extends Module> moduleClass) {
        List<Actor> moduleChildren = new ArrayList<>(children.values().stream()
                .filter(actor -> actor.hasModule(moduleClass))
                .toList());
        children.values().forEach(child -> moduleChildren.addAll(child.getChildrenWithModule(moduleClass)));
        return moduleChildren;
    }

    public Collection<Module> getModules() {
        return modules.values();
    }

    public <T extends Module> T getModule(Class<T> clazz) {
        for (Module module : modules.values()) {
            if (clazz.isAssignableFrom(module.getClass())) {
                return clazz.cast(module);
            }
        }
        return null;
    }

    public boolean hasModule(Class<? extends Module> moduleClass) {
        for (Module module : modules.values()) {
            if (moduleClass.isAssignableFrom(module.getClass())) {
                return true;
            }
        }
        return false;
    }
}

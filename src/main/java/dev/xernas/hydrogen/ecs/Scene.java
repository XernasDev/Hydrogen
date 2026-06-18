package dev.xernas.hydrogen.ecs;

import dev.xernas.hydrogen.Application;
import dev.xernas.hydrogen.HydrogenException;
import dev.xernas.hydrogen.ecs.module.GlobalModule;
import dev.xernas.hydrogen.ecs.module.Module;
import dev.xernas.hydrogen.rendering.Renderer;
import dev.xernas.photon.api.Transform;
import dev.xernas.photon.api.window.Window;
import dev.xernas.photon.api.window.input.Input;
import dev.xernas.photon.exceptions.PhotonException;

import java.util.*;

public class Scene {

    private final String name;
    private final List<Actor> actors = new ArrayList<>();
    private final Map<String, Actor> actorNames = new HashMap<>();

    private final List<GlobalModule> globalModules = new ArrayList<>();

    private Application app;
    private Window window;
    private Renderer renderer;

    private Actor cameraActor;

    public Scene(String name, Actor... actors) {
        this.name = name;
        for (Actor actor : actors) newActor(actor);
    }

    public void load(Application app, Window window, Renderer renderer) throws PhotonException {
        cameraActor = findCameraActor();
        if (cameraActor == null) throw new HydrogenException("Scene " + name + " must have at least one camera actor to be loaded.");
        renderer.loadScene(this);
        for (Actor actor : actors) actor.start(app, window, renderer);

        this.app = app;
        this.window = window;
        this.renderer = renderer;
    }

    public void update() throws HydrogenException {
        for (Actor actor : new ArrayList<>(actors)) actor.update();
    }

    public void input() throws HydrogenException {
        for (Actor actor : new ArrayList<>(actors)) actor.input();
    }

    public void stop() throws PhotonException {
        actors.forEach(Actor::stop);
        renderer.unloadScene();
    }

    public Scene newActor(Actor actor) {
        actors.add(actor);
        actorNames.put(actor.getName(), actor);
        actor.setScene(this);
        if (actor.hasModule(GlobalModule.class)) globalModules.add(actor.getModule(GlobalModule.class));
        globalModules.addAll(actor.getChildrenGlobalModules());
        return this;
    }

    public void instantiate(Actor actor) throws HydrogenException {
        if (isNotRendererLoaded()) return;
        if (!actor.isChild()) newActor(actor);
        try {
            renderer.loadActor(actor);
        } catch (PhotonException e) {
            e.printStackTrace(); // It'll do it for now but i have to do better error handling next
        }
        actor.start(app, window, renderer);
    }

    public void destroyActor(Actor actor) {
        if (isNotRendererLoaded()) return;
        if (!actor.isChild()) if (actor.hasModule(GlobalModule.class)) globalModules.remove(actor.getModule(GlobalModule.class));
        actor.stop();
        try {
            renderer.unloadActor(actor);
        } catch (PhotonException e) {
            e.printStackTrace(); // It'll do it for now but i have to do better error handling next
        }
        if (!actor.isChild()) {
            actors.remove(actor);
            actorNames.remove(actor.getName());
        }
    }

    public String getName() {
        return name;
    }

    public Actor getActor(String name) {
        return actorNames.get(name);
    }

    public boolean hasActor(Actor actor) {
        return actors.contains(actor);
    }

    public List<Actor> getActors() {
        return actors;
    }

    // Do NOT execute this every frame !
    public List<Actor> getActorsWithModule(Class<? extends Module> moduleClass) {
        List<Actor> moduleActors = new ArrayList<>(actors.stream()
                .filter(actor -> actor.hasModule(moduleClass))
                .toList());
        actors.forEach(actor -> moduleActors.addAll(actor.getChildrenWithModule(moduleClass)));
        return moduleActors;
    }

    public List<GlobalModule> getGlobalModules() {
        return globalModules;
    }

    private Actor findCameraActor() {
        for (Actor actor : actors) return actor.hasCamera();
        return null;
    }

    public Actor getCameraActor() {
        return cameraActor;
    }

    private boolean isNotRendererLoaded() {
        return renderer == null;
    }
}

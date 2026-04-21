package dev.xernas.hydrogen.ecs;

import dev.xernas.hydrogen.HydrogenException;
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

    private Window window;
    private Renderer renderer;

    private boolean is3D;

    public Scene(String name, Actor... actors) {
        this.name = name;
        for (Actor actor : actors) {
            this.actors.add(actor);
            actorNames.put(actor.getName(), actor);
        }
    }

    public void load(Window window, Renderer renderer) throws PhotonException {
        if (!hasCameraActor()) throw new HydrogenException("Scene " + name + " must have at least one camera actor to be loaded.");
        renderer.loadScene(this);
        actors.forEach(actor -> actor.start(window, renderer));

        this.window = window;
        this.renderer = renderer;
    }

    public void update() {
        new ArrayList<>(actors).forEach(Actor::update);
    }

    public void input(Input input) {
        new ArrayList<>(actors).forEach(actor -> actor.input(input));
    }

    public void stop() throws PhotonException {
        actors.forEach(Actor::stop);
        renderer.unloadScene();
    }

    public Scene newActor(Actor actor) {
        actors.add(actor);
        actorNames.put(actor.getName(), actor);
        return this;
    }

    public void instantiate(Actor actor) {
        if (isNotRendererLoaded()) return;
        newActor(actor);
        try {
            renderer.loadActor(actor);
        } catch (PhotonException e) {
            e.printStackTrace(); // It'll do it for now but i have to do better error handling next
        }
        actor.start(window, renderer);
    }

    public void destroyActor(Actor actor) {
        if (isNotRendererLoaded()) return;
        actor.stop();
        try {
            renderer.unloadActor(actor);
        } catch (PhotonException e) {
            e.printStackTrace(); // It'll do it for now but i have to do better error handling next
        }
        actors.remove(actor);
        actorNames.remove(actor.getName());
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

    public List<Actor> getActorsWithModule(Class<? extends Module> moduleClass) {
        return actors.stream()
                     .filter(actor -> actor.hasModule(moduleClass))
                     .toList();
    }

    public Actor getCameraActor() {
        Optional<Actor> cameraActor = actors.stream()
                .filter(actor -> actor.getTransform() instanceof Transform.CameraTransform)
                .findAny();
        return cameraActor.orElse(null);
    }

    public boolean hasCameraActor() {
        return actors.stream()
                .anyMatch(actor -> actor.getTransform() instanceof Transform.CameraTransform);
    }

    private boolean isNotRendererLoaded() {
        return renderer == null;
    }

    public boolean is3D() {
        return is3D;
    }

    public void set3D(boolean is3D) {
        this.is3D = is3D;
    }
}

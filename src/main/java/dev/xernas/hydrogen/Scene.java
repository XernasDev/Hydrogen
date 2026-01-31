package dev.xernas.hydrogen;

import java.util.ArrayList;
import java.util.List;

public class Scene {

    private final String name;
    private final List<Actor> actors = new ArrayList<>();

    public Scene(String name, Actor... actors) {
        this.name = name;
        this.actors.addAll(List.of(actors));
    }

    public Scene addActor(Actor actor) {
        actors.add(actor);
        return this;
    }

    public String getName() {
        return name;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public List<Actor> getActorsWithModule(Class<? extends Module> moduleClass) {
        return actors.stream()
                     .filter(actor -> actor.hasModule(moduleClass))
                     .toList();
    }
}

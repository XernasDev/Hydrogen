package dev.xernas.hydrogen.ecs.ui;

import dev.xernas.hydrogen.ecs.Actor;
import dev.xernas.hydrogen.ecs.module.Module;
import dev.xernas.hydrogen.ecs.module.RenderingModule;
import dev.xernas.hydrogen.rendering.material.Material;
import dev.xernas.photon.api.model.Model;
import dev.xernas.photon.utils.Models;

public class UIComponent extends Actor {

    public UIComponent(String name, UITransform transform, Material material, Module... modules) {
        super(name, transform);

        Model noPerspectiveQuad = Models.createQuad();
        noPerspectiveQuad.usePerspective(false);

        RenderingModule renderingModule = new RenderingModule("ui", noPerspectiveQuad, material);
        newModules(renderingModule);

        newModules(modules);
    }

    public UIComponent(UITransform transform, Material material, Module... modules) {
        super(transform);

        Model noPerspectiveQuad = Models.createQuad();
        noPerspectiveQuad.usePerspective(false);

        RenderingModule renderingModule = new RenderingModule("ui", noPerspectiveQuad, material);
        newModules(renderingModule);

        newModules(modules);
    }

}

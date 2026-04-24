package dev.xernas.hydrogen.ecs.actor;

import dev.xernas.hydrogen.ecs.Actor;
import dev.xernas.hydrogen.ecs.UITransform;
import dev.xernas.hydrogen.ecs.module.Module;
import dev.xernas.hydrogen.ecs.module.RenderingModule;
import dev.xernas.hydrogen.rendering.material.Material;
import dev.xernas.photon.api.model.Model;
import dev.xernas.photon.utils.Models;

public class UIComponent extends Actor {

    public UIComponent(UITransform transform, Material material, Module... modules) {
        super(transform);

        Model noPerspectiveQuad = Models.createQuad();
        noPerspectiveQuad.usePerspective(false);

        RenderingModule renderingModule = new RenderingModule("ui", noPerspectiveQuad, material);
        newModules(renderingModule);

        newModules(modules);
    }

}

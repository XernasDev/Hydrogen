package dev.xernas.hydrogen.ecs.ui;

import dev.xernas.hydrogen.ecs.Actor;
import dev.xernas.hydrogen.ecs.module.Module;
import dev.xernas.hydrogen.ecs.module.RenderingModule;
import dev.xernas.hydrogen.rendering.material.Material;
import dev.xernas.photon.api.model.Model;
import dev.xernas.photon.utils.Models;

public class UIComponent extends Actor {

    public UIComponent(String name, UITransform transform, Model model, Material material, Module... modules) {
        super(name, transform, modules);

        model.usePerspective(false);

        RenderingModule renderingModule = new RenderingModule("ui", model, material);
        newModules(renderingModule);
    }

    public UIComponent(String name, UITransform transform, Material material, Module... modules) {
        this(name, transform, Models.createQuad(), material, modules);
    }

    public UIComponent(UITransform transform, Model model, Material material, Module... modules) {
        this(null, transform, model, material, modules);
    }

    public UIComponent(UITransform transform, Material material, Module... modules) {
        this(null, transform, material, modules);
    }

}

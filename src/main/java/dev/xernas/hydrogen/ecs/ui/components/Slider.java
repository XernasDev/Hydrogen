package dev.xernas.hydrogen.ecs.ui.components;

import dev.xernas.hydrogen.ecs.ui.UIComponent;
import dev.xernas.hydrogen.ecs.ui.UITransform;
import dev.xernas.hydrogen.ecs.ui.modules.Bar;
import dev.xernas.hydrogen.ecs.ui.modules.SliderKnob;
import dev.xernas.hydrogen.rendering.material.Material;
import dev.xernas.photon.api.model.Model;
import dev.xernas.photon.api.window.cursor.CursorShape;

public class Slider extends UIComponent {

    private final Bar bar;
    private final SliderKnob knob;

    private CursorShape onHoverCursor;

    public Slider(String name, UITransform transform, int knobSize, Material barMaterial, int barRounding, Model knobModel, Material knobMaterial, Material backMaterial) {
        super(name, transform.setZIndex(0.5f), barMaterial, barRounding, "progress");

        bar = new Bar();
        newModules(bar);

        this.knob = new SliderKnob() {
            @Override
            public CursorShape getCursor() {
                return getOnHoverCursor();
            }

            @Override
            public void onRelease(Bar bar) {
                Slider.this.onRelease(bar);
            }
        };

        UIComponent knob = new UIComponent(
                "SliderKnob",
                new UITransform(
                        0,
                        0,
                        knobSize,
                        knobSize
                ).setZIndex(1f),
                knobModel,
                knobMaterial,
                this.knob
        );

        UIComponent backgroundBar = new UIComponent(
                "BackgroundBar",
                new UITransform(
                        transform.getRawX(),
                        transform.getRawY(),
                        transform::getWidth,
                        transform::getHeight
                ).setZIndex(0f),
                backMaterial,
                barRounding
        );

        newChildren(knob, backgroundBar);
    }

    public Slider(UITransform transform, int knobSize, Material barMaterial, int barRounding, Model knobModel, Material knobMaterial, Material backMaterial) {
        this(null, transform, knobSize, barMaterial, barRounding, knobModel, knobMaterial, backMaterial);
    }

    public Slider(String name, UITransform transform, int knobSize, Material barMaterial, int barRounding, Material knobMaterial, Material backMaterial) {
        super(name, transform.setZIndex(0.5f), barMaterial, barRounding, "progress");

        bar = new Bar();
        newModules(bar);

        this.knob = new SliderKnob() {
            @Override
            public CursorShape getCursor() {
                return getOnHoverCursor();
            }

            @Override
            public void onRelease(Bar bar) {
                Slider.this.onRelease(bar);
            }
        };

        UIComponent knob = new UIComponent(
                "SliderKnob",
                new UITransform(
                        0,
                        0,
                        knobSize,
                        knobSize
                ).setZIndex(1f),
                knobMaterial,
                transform.getWidth() / 2,
                this.knob
        );

        UIComponent backgroundBar = new UIComponent(
                "BackgroundBar",
                new UITransform(
                        transform.getRawX(),
                        transform.getRawY(),
                        transform::getWidth,
                        transform::getHeight
                ).setZIndex(0f),
                backMaterial,
                barRounding
        );

        newChildren(knob, backgroundBar);
    }

    public Slider(UITransform transform, int knobSize, Material barMaterial, int barRounding, Material knobMaterial, Material backMaterial) {
        this(null, transform, knobSize, barMaterial, barRounding, knobMaterial, backMaterial);
    }

    private CursorShape getOnHoverCursor() {
        return onHoverCursor;
    }

    public Slider setCursorOnHover(CursorShape onHoverCursor) {
        this.onHoverCursor = onHoverCursor;
        return this;
    }

    public void onRelease(Bar bar) {

    }

    public float getProgress() {
        return bar.getProgress();
    }

}

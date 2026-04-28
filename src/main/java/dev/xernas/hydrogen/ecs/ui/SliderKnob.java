package dev.xernas.hydrogen.ecs.ui;

import dev.xernas.hydrogen.Application;
import dev.xernas.hydrogen.ecs.Actor;
import dev.xernas.hydrogen.rendering.Renderer;
import dev.xernas.hydrogen.utils.ui.UnitHelper;
import dev.xernas.photon.api.window.Window;
import dev.xernas.photon.api.window.input.Input;
import dev.xernas.photon.api.window.input.Key;

public abstract class SliderKnob extends MouseBox {

    private Window window;
    private UITransform transform;
    private Bar bar;

    private boolean isPressing;
    private int mouseStart;
    private int knobStart;

    @Override
    public void onStart(Application app, Actor actor, Window window, Renderer renderer) {
        super.onStart(app, actor, window, renderer);
        if (!actor.isChild() || !actor.getParent().hasModule(Bar.class)) throw new IllegalStateException("A slider knob has to be a child of an actor that has the Bar.class module");
        this.window = window;
        this.bar = actor.getParent().getModule(Bar.class);
        this.transform = getUITransform();
        transform.setX(UnitHelper.sub(bar.getUITransform().getRawX(), () -> transform.getWidth() / 2));
        transform.setY(UnitHelper.sub(bar.getUITransform().getRawY(), () -> (transform.getHeight() - bar.getUITransform().getHeight()) / 2));
    }

    @Override
    public void onUpdate() {
        int minX = bar.getUITransform().getX() - transform.getWidth() / 2;
        int maxX = bar.getUITransform().getX() + bar.getMaxSize() - transform.getWidth() / 2;
        if (transform.getX() < minX) transform.setX(minX);
        if (transform.getX() > maxX) transform.setX(maxX);

        int range = maxX - minX;
        int currentX = transform.getX() - minX;
        float percent = (float) currentX / range;
        bar.setProgress(percent);
    }

    @Override
    public void onInput(Input input) {
        super.onInput(input);
        if (input.hasReleased(Key.MOUSE_LEFT)) {
            if (isPressing) onRelease(bar);
            isPressing = false;
        }
        if (isPressing) transform.setX(knobStart + (int) (input.getMouse().getX() - mouseStart));
    }

    @Override
    public void whileInside(Input input) {
        // TODO window.setCursorShape(CursorShape.ARROW);
        if (input.hasPressed(Key.MOUSE_LEFT)) {
            isPressing = true;
            mouseStart = (int) input.getMouse().getX();
            knobStart = transform.getX();
        }
    }

    @Override
    public void onMouseEnter() {

    }

    @Override
    public void onMouseLeave() {

    }

    public abstract void onRelease(Bar bar);
}

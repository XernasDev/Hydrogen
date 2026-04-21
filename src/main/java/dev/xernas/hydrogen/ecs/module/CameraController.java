package dev.xernas.hydrogen.ecs.module;

import dev.xernas.atom.math.MathUtils;
import dev.xernas.hydrogen.Application;
import dev.xernas.hydrogen.ecs.Actor;
import dev.xernas.hydrogen.rendering.Renderer;
import dev.xernas.photon.api.Transform;
import dev.xernas.photon.api.window.Window;
import dev.xernas.photon.api.window.input.Input;
import dev.xernas.photon.api.window.input.Key;
import org.joml.Vector3f;

public class CameraController extends Module {

    private Transform.CameraTransform cameraTransform;

    private Vector3f direction = new Vector3f();
    private Vector3f rotation = new Vector3f();

    private final float speed;

    public CameraController() {
        this.speed = 0.01f;
    }

    public CameraController(float speed) {
        this.speed = speed * 0.01f;
    }


    @Override
    public void onStart(Actor parent, Window window, Renderer renderer) {
        if (!(parent.getTransform() instanceof Transform.CameraTransform)) throw new IllegalArgumentException("CameraController module can only be added to actors with a CameraTransform.");
        cameraTransform = (Transform.CameraTransform) parent.getTransform();
    }

    @Override
    public void onUpdate() {
        cameraTransform.move(direction);
        cameraTransform.rotate(rotation);
        // Clamp the camera rotation
        Vector3f rotation = cameraTransform.getRotation();
        rotation.x = MathUtils.clamp(rotation.x, -90, 90);
        cameraTransform.setRotation(rotation);
    }

    @Override
    public void onInput(Input input) {
        direction = new Vector3f();
        rotation = new Vector3f();
        if (input.isPressing(Key.KEY_Z)) {
            direction.add(new Vector3f(0, 0, -speed));
        }
        if (input.isPressing(Key.KEY_S)) {
            direction.add(new Vector3f(0, 0, speed));
        }
        if (input.isPressing(Key.KEY_Q)) {
            direction.add(new Vector3f(-speed, 0, 0));
        }
        if (input.isPressing(Key.KEY_D)) {
            direction.add(new Vector3f(speed, 0, 0));
        }
        if (input.isPressing(Key.KEY_SPACE)) {
            direction.add(new Vector3f(0, speed, 0));
        }
        if (input.isPressing(Key.KEY_LEFT_SHIFT)) {
            direction.add(new Vector3f(0, -speed, 0));
        }
        if (input.isPressing(Key.KEY_ARROW_UP)) {
            rotation.add(new Vector3f(-0.1f, 0, 0));
        }
        if (input.isPressing(Key.KEY_ARROW_DOWN)) {
            rotation.add(new Vector3f(0.1f, 0, 0));
        }
        if (input.isPressing(Key.KEY_ARROW_LEFT)) {
            rotation.add(new Vector3f(0, -0.1f, 0));
        }
        if (input.isPressing(Key.KEY_ARROW_RIGHT)) {
            rotation.add(new Vector3f(0, 0.1f, 0));
        }
    }

}

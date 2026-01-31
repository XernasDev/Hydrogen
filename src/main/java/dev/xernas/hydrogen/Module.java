package dev.xernas.hydrogen;

public interface Module {

    void onStart();

    void onUpdate();

    default void onRender() {

    }

    void onStop();

}

package oliwiastrzelec.multiscalemodeling.model;

import lombok.Getter;

public enum Shape {
    CIRCLE("Circle"),
    SQUARE("Square");

    @Getter
    private final String shape;

    Shape(String shape) {
        this.shape = shape;
    }
}

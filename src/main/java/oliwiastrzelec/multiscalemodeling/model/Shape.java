package oliwiastrzelec.multiscalemodeling.model;

import lombok.Getter;

public enum Shape {
    SQUARE("Square"),
    CIRCLE("Circle");

    @Getter
    private final String shape;

    Shape(String shape) {
        this.shape = shape;
    }
}

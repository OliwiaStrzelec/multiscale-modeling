package oliwiastrzelec.multiscalemodeling.model;

import lombok.Getter;

public enum Mechanism {

    CELULAR_AUTOMATA("Celular automata"),
    MONTE_CARLO("Monte Carlo");

    Mechanism(String mechanism) {
        this.mechanism = mechanism;
    }

    @Getter
    private String mechanism;
}

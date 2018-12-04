package oliwiastrzelec.multiscalemodeling.model;

import lombok.Getter;

public enum EnergyDistribution {

    HOMOGENOUS("Homogenous"),
    HETEROGENOUS("Heterogenous");

    @Getter
    private String energy;

    EnergyDistribution(String energy) {
        this.energy = energy;
    }
}

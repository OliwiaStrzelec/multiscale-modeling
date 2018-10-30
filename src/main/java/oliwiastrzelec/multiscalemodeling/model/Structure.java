package oliwiastrzelec.multiscalemodeling.model;

import lombok.Getter;

public enum Structure {
    SUBSTRUCTURE("Substructure"),
    DUAL_PHASE("Dual phase");

    @Getter
    private String structure;

    Structure(String structure) {
        this.structure = structure;
    }
}

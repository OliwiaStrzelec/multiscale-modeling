package oliwiastrzelec.multiscalemodeling.model;

import lombok.Getter;

public enum Place {
        BORDERS("On grains borders"),
        RANDOM("Randomly");

        @Getter
        private final String place;

        Place(String place) {
            this.place = place;
        }
}

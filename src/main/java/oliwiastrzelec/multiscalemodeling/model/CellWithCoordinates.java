package oliwiastrzelec.multiscalemodeling.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CellWithCoordinates extends Cell {
    int x, y;
    public CellWithCoordinates(Cell c, int x, int y) {
        super(c.getId());
        this.setState(c.getState());
        this.setRgb(c.getRgb());
        this.x = x;
        this.y = y;
    }
}

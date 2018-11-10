package oliwiastrzelec.multiscalemodeling.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Arrays;

@Getter
@Setter
@ToString
public class Cell implements Comparable<Cell> {

    private int id;
    private int[] rgb = new int[3];
    private State state;

    public Cell(int id) {
        this.id = id;
        this.state = State.GRAIN;
        if(id == 0){
            this.setRgb(new int[]{255, 255, 255});
        }
    }

    public Cell(State state) {
        this.state = state;
        if (state.equals(State.INCLUSION)) {
            this.rgb[0] = 0;
            this.rgb[1] = 0;
            this.rgb[2] = 0;
            this.setId(-1);
        }
        if (state.equals(State.BORDER)) {
            this.rgb[0] = 30;
            this.rgb[1] = 30;
            this.rgb[2] = 30;
            this.setId(-2);
        }
        if (state.equals(State.PHASE)) {
            this.rgb[0] = 0;
            this.rgb[1] = 150;
            this.rgb[2] = 0;
            this.setId(-3);
        }
        if (state.equals(State.INSIDE_BORDER)) {
            this.rgb[0] = 255;
            this.rgb[1] = 255;
            this.rgb[2] = 255;
            this.setId(-4);
        }
    }

    public boolean isTheSameAs(Cell c) {
        if (!(this.getId() == c.getId())) {
            return false;
        }
        if (!(this.getState().equals(c.getState()))) {
            return false;
        }
        if (!(this.rgb[0] == c.getRgb()[0] && this.rgb[1] == c.getRgb()[1] && this.rgb[2] == c.getRgb()[2])) {
            return false;
        }
        return true;
    }


    public enum State {
        INCLUSION("inclusion"),
        GRAIN("grain"),
        PHASE("phase"),
        BORDER("border"),
        INSIDE_BORDER("inside_border");

        @Getter
        private String state;

        State(String state) {
            this.state = state;
        }
    }

    @Override
    public int compareTo(Cell c) {
        return Integer.compare(this.id, c.id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cell cell = (Cell) o;

        if (id != cell.getId()) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + Arrays.hashCode(rgb);
        return result;
    }
}

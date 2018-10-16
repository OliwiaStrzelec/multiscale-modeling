package oliwiastrzelec.multiscalemodeling.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Arrays;
import java.util.Comparator;

@Getter
@Setter
@ToString
public class Cell implements Comparable<Cell> {

    private int id;
    private int[] rgb = new int[3];

    public Cell(int id) {
        this.id = id;
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

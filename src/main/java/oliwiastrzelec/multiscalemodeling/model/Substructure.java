package oliwiastrzelec.multiscalemodeling.model;

public class Substructure {
    static void removeGrainFromArray(Cell c, Cell[][] array) {
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                if (array[i][j].isTheSameAs(c)) {
                    array[i][j] = new Cell(0);
                }
            }
        }
    }

    static void addStructureToGrains(Structure structure, Cell[][] array) {
        Cell c;
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                if ((c = array[i][j]).getId() > 0) {
                    if (structure.equals(Structure.DUAL_PHASE)) {
                        array[i][j] = new Cell(Cell.State.PHASE);
                    } else {
                        c.setId(-3);
                        array[i][j] = c;
                    }
                }
            }
        }
    }
}

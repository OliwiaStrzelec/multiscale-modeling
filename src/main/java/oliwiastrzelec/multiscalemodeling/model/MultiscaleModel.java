package oliwiastrzelec.multiscalemodeling.model;

import lombok.Getter;
import lombok.Setter;

import java.util.*;

import static oliwiastrzelec.multiscalemodeling.model.MultiscaleModelHelper.cloneArray;
import static oliwiastrzelec.multiscalemodeling.model.MultiscaleModelHelper.isGrainsBorder;

@Getter
@Setter
public class MultiscaleModel {

    public static MultiscaleModel instance;

    private final int sizeX = 200;

    private final int sizeY = 200;

    private Cell[][] array = MultiscaleModelHelper.generateEmptyArray(sizeX, sizeY);

    private boolean grainsGenerated = false;

    private int numberOfGrains = 0;

    private boolean arrayFilled = false;

    private boolean probabilityAdded = false;

    private boolean boundariesAdded = false;

    private boolean growingAfterBoundaries = false;

    private boolean grainsRemoved = false;

    private int probability = 90;

    private Structure structure = Structure.SUBSTRUCTURE;

    private boolean structureChoosen = false;

    private MultiscaleModel() {
    }

    public static MultiscaleModel getInstance() {
        if (instance == null) {
            instance = new MultiscaleModel();
        }
        return instance;
    }

    public void generateRandomGrains(int numberOfNucleons) {
        setNumberOfGrains(numberOfNucleons);
        if (!probabilityAdded) {
            setProbabilityAdded(true);
        }
        List<Cell> cells = MultiscaleModelHelper.generateRandomCells(numberOfNucleons);
        int x;
        int y;
        int i = numberOfNucleons - 1;
        Cell c;
        // for(int i = numberOfNucleons - 1; i >= 0; i--){
        while (!cells.isEmpty()) {
            x = (int) (Math.floor(Math.random() * (sizeX - 2)) + 1);
            y = (int) (Math.floor(Math.random() * (sizeY - 2)) + 1);
            if ((c = array[x][y]).getId() == 0 && !c.getState().equals(Cell.State.INCLUSION) && !c.getState().equals(Cell.State.PHASE) && !c.getState().equals(Cell.State.INSIDE_BORDER)) {
                array[x][y] = cells.remove(i--);
            }
        }
        setGrainsGenerated(true);
    }

    public void growGrainsLoop(int numberOfIterations) {
        int k = 0;
        if (numberOfIterations >= 50 && !growingAfterBoundaries) {
            while (MultiscaleModelHelper.countEmptyCells(array) != 0 && k < 100) {
                growGrains();
                k++;
            }
            setArrayFilled(true);
            return;
        }
        for (int i = 0; i < numberOfIterations; i++) {
            growGrains();
        }
        if (MultiscaleModelHelper.countEmptyCells(array) == 0) {
            setArrayFilled(true);
        }
    }

    public void stopGrowing() {
        setArrayFilled(true);
        //setGrowingAfterBoundaries(false);
    }

    public void growGrains() {
        int k = 0;
        Cell[][] previousArray = array;
        Cell[][] currentArray = MultiscaleModelHelper.generateEmptyArray(sizeX, sizeY);
        Cell c;
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                if ((c = previousArray[i][j]).getId() == 0) {
                    MooreNeighbourhood.fillMooreNeighbourFirstRule(i, j, previousArray, currentArray, probability);
                } else {
                    currentArray[i][j].setId(c.getId());
                    currentArray[i][j].setRgb(c.getRgb());
                    currentArray[i][j].setState(c.getState());
                }
            }
        }
        array = currentArray;
    }

    public void chooseStructure(Structure structure, int numberOfGrainsToStay) {
        this.structure = structure;
        this.structureChoosen = true;
        if (numberOfGrainsToStay >= numberOfGrains) {
            return;
        }
        int x, y;
        Cell c;
        int numberOfGrainsToRemove = numberOfGrains - numberOfGrainsToStay - 1;
        while (numberOfGrainsToRemove >= 0) {
            x = (int) (Math.floor(Math.random() * (sizeX - 2)) + 1);
            y = (int) (Math.floor(Math.random() * (sizeY - 2)) + 1);
            if ((c = array[x][y]).getId() > 0) {
                Substructure.removeGrainFromArray(c, array);
                numberOfGrainsToRemove--;
            }
        }
        Substructure.addStructureToGrains(structure, array);
        setArrayFilled(false);
        setGrainsGenerated(false);
    }

    public void clear() {
        setArray(MultiscaleModelHelper.generateEmptyArray(sizeX, sizeY));
        setGrainsGenerated(false);
        setArrayFilled(false);
        setProbabilityAdded(false);
        setStructure(Structure.SUBSTRUCTURE);
        setStructureChoosen(false);
        setBoundariesAdded(false);
        setGrowingAfterBoundaries(false);
    }

    public void generateInclusions(int numberOfInclusions, float sizeOfInclusions, Shape shapeOfInclusions) {
        if (shapeOfInclusions.equals(Shape.CIRCLE)) {
            addCircleInclusions(numberOfInclusions, sizeOfInclusions, array);
        } else {
            addSquareInclusions(numberOfInclusions, sizeOfInclusions);
        }
    }

    private void addCircleInclusions(int numberOfInclusions, float sizeOfInclusions, Cell[][] array) {
        if (arrayFilled) {
            Inclusions.addCircleInclusionsForFilledArray(numberOfInclusions, (int) sizeOfInclusions, array);
        }
        if (!grainsGenerated && !arrayFilled) {
            Inclusions.addCircleInclusionsForEmptyArray(numberOfInclusions, (int) sizeOfInclusions, array);
        }
    }

    private void addSquareInclusions(int numberOfInclusions, float sizeOfInclusions) {
        int a = (int) (sizeOfInclusions / Math.sqrt(2));
        if (arrayFilled) {
            Inclusions.addSquareInclusionsForFilledArray(numberOfInclusions, a, array);
        }
        if (!grainsGenerated && !arrayFilled) {
            Inclusions.addSquareInclusionsForEmptyArray(numberOfInclusions, a, array);
        }
    }

    private void removeIfNotGrainsBorder(int x, int y) {
        if (!arrayFilled && !grainsGenerated) {
            return;
        }
        Cell c = array[x][y];
        if (c.getState().equals(Cell.State.INSIDE_BORDER)) {
            array[x][y] = new Cell(Cell.State.INSIDE_BORDER);
            return;
        }
        if (c.getState().equals(Cell.State.BORDER)) {
            array[x][y] = new Cell(Cell.State.INCLUSION);
            return;
        }
        c = new Cell(0);
        c.setRgb(new int[]{255, 255, 255});
        array[x][y] = c;
    }

    private void fillIfGrainsBorder(int x, int y, int id, Cell[][] borderCells, int boundarySize) {
        if (!arrayFilled && !grainsGenerated) {
            return;
        }
        for (int i = x - 1; i <= x + 1; i++) {
            if (i < 0 || i >= sizeX) {
                continue;
            }
            if (array[i][y].getId() != id) {
                borderCells[x][y] = new Cell(Cell.State.BORDER);
                fillBorderXDimension(x, y, borderCells, boundarySize);
            }
        }
        for (int i = y - 1; i <= y + 1; i++) {
            if (i < 0 || i >= sizeY) {
                continue;
            }
            if (array[x][i].getId() != id) {
                borderCells[x][y] = new Cell(Cell.State.BORDER);
                fillBorderYDimension(x, y, borderCells, boundarySize);
            }
        }
    }

    private void fillBorderXDimension(int x, int y, Cell[][] cells, int boundarySize) {
        for (int i = x - (boundarySize - 1); i <= x + (boundarySize - 1); i++) {
            if (i < 0 || i >= sizeX) {
                continue;
            }
            cells[i][y] = new Cell(Cell.State.BORDER);
        }
    }

    private void fillBorderYDimension(int x, int y, Cell[][] cells, int boundarySize) {
        for (int i = y - (boundarySize - 1); i <= y + (boundarySize - 1); i++) {
            if (i < 0 || i >= sizeY) {
                continue;
            }
            cells[x][i] = new Cell(Cell.State.BORDER);
        }
    }

    public void addBoundaries(int boundarySize, int numberOfGrains) {
        if (!arrayFilled && !grainsGenerated) {
            return;
        }
        if (numberOfGrains == 0) {
            array = addGrainsBorders(boundarySize);
        } else {
            array = addBordersToGrains(boundarySize, numberOfGrains, array);
        }
        setBoundariesAdded(true);
    }

    public Cell[][] addBordersToGrains(int boundarySize, int numberOfGrains, Cell[][] array) {
        Cell[][] cellBorders = MultiscaleModelHelper.cloneArray(array);
        int x, y;
        Cell c;
        while (numberOfGrains > 0) {
            x = (int) (Math.floor(Math.random() * (array.length - 2)) + 1);
            y = (int) (Math.floor(Math.random() * (array[0].length - 2)) + 1);
            if ((c = array[x][y]).getId() > 0) {
                addBorderToGrain(c, cellBorders, boundarySize);
                numberOfGrains--;
            }
        }
        return cellBorders;
    }

    private void addBorderToGrain(Cell c, Cell[][] cellBorders, int boundarySize) {
        List<CellWithCoordinates> cells = allCells(c.getId(), cellBorders);
        cells.forEach(cell -> {
            fillIfGrainsBorder(cell.getX(), cell.getY(), c.getId(), cellBorders, boundarySize);
        });
        cells.forEach(cell -> {
            changeStateIfNotGrainsBorder(cell.getX(), cell.getY(), cellBorders);
        });
    }

    private void changeStateIfNotGrainsBorder(int x, int y, Cell[][] cellBorders) {
        Cell c;
        if (!((c = cellBorders[x][y]).getState().equals(Cell.State.BORDER))) {
            c.setState(Cell.State.INSIDE_BORDER);
            cellBorders[x][y] = c;
        }
    }

    private List<CellWithCoordinates> allCells(int id, Cell[][] cellBorders) {
        List<CellWithCoordinates> cells = new ArrayList<>();
        Cell c;
        for (int i = 0; i < cellBorders.length; i++) {
            for (int j = 0; j < cellBorders[0].length; j++) {
                if ((c = cellBorders[i][j]).getId() == id) {
                    cells.add(new CellWithCoordinates(c, i, j));
                }
            }
        }
        return cells;
    }

    public void removeGrains() {
        removeGrainsLeaveBorders();
        setGrainsGenerated(false);
        setBoundariesAdded(false);
        setGrowingAfterBoundaries(true);
        setArrayFilled(false);
    }

    private void removeGrainsLeaveBorders() {
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                removeIfNotGrainsBorder(i, j);
            }
        }
    }

    private Cell[][] addGrainsBordersToEmptyArray() {
        Cell[][] borderCells = MultiscaleModelHelper.generateEmptyArray(sizeX, sizeY);
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                fillIfGrainsBorder(i, j, array[i][j].getId(), borderCells, 1);
            }
        }
        return borderCells;
    }

    private Cell[][] addGrainsBorders(int boundarySize) {
        Cell[][] borderCells = cloneArray();
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                fillIfGrainsBorder(i, j, array[i][j].getId(), borderCells, boundarySize);
            }
        }
        return borderCells;
    }

    private Cell[][] cloneArray() {
        return MultiscaleModelHelper.cloneArray(array);
    }

}
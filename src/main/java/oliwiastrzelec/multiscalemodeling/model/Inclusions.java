package oliwiastrzelec.multiscalemodeling.model;

public class Inclusions {
    static void addCircleInclusionsForEmptyArray(int numberOfInclusions, int r, Cell[][] array) {
        int x, y;
        for (int i = 0; i < numberOfInclusions; i++) {
            x = getX(r, array.length);
            y = getY(r, array[0].length);
            while (array[x][y].getState().equals(Cell.State.INCLUSION)) {
                x = getX(r, array.length);
                y = getY(r, array[0].length);
            }
            fillCircle(r, x, y, array);
        }
    }

    static void addCircleInclusionsForFilledArray(int numberOfInclusions, int r, Cell[][] array) {
        int x;
        int y;
        for (int i = 0; i < numberOfInclusions; i++) {
            x = getX(r, array.length);
            y = getY(r, array[0].length);
            while (array[x][y].getState().equals(Cell.State.INCLUSION) || array[x][y].getState().equals(Cell.State.PHASE) || !MultiscaleModelHelper.isGrainsBorder(x, y, array[x][y].getId(), array)) {
                x = getX(r, array.length);
                y = getY(r, array[0].length);
            }
            fillCircle(r, x, y, array);
        }
    }

    static void addSquareInclusionsForFilledArray(int numberOfInclusions, int a, Cell[][] array) {
        int x;
        int y;
        for (int i = 0; i < numberOfInclusions; i++) {
            x = getX(a, array.length);
            y = getY(a, array[0].length);
            while (array[x][y].getState().equals(Cell.State.INCLUSION) || array[x][y].getState().equals(Cell.State.PHASE) || !MultiscaleModelHelper.isGrainsBorder(x, y, array[x][y].getId(), array)) {
                x = getX(a, array.length);
                y = getY(a, array[0].length);
            }
            fillSquare(a, x, y, array);
        }
    }

    static void addSquareInclusionsForEmptyArray(int numberOfInclusions, int a, Cell[][] array) {
        int x, y;
        for (int i = 0; i < numberOfInclusions; i++) {
            x = getX(a, array.length);
            y = getY(a, array[0].length);
            while (array[x][y].getState().equals(Cell.State.INCLUSION)) {
                x = getX(a, array.length);
                y = getY(a, array[0].length);
            }
            fillSquare((int) a, x, y, array);
        }
    }

    private static void fillSquare(int a, int x, int y, Cell[][] array) {
        for (int i = x; i <= x + a && i < array.length; i++) {
            for (int j = y; j <= y + a && j < array[0].length; j++) {
                array[i][j] = new Cell(Cell.State.INCLUSION);
            }
        }
    }

    private static void fillCircle(int r, int x, int y, Cell[][] array) {
        double pi = Math.PI;
        int a, b;
        for (double i = 0; i <= 360; i += 0.01) {
            for (int j = r; j >= 0; j--) {
                a = (int) (j * Math.cos(i * pi / 2)) + x;
                b = (int) (j * Math.sin(i * pi / 2)) + y;
                if (a < 0 || a >= array.length || b < 0 || b > array[0].length) {
                    continue;
                }
                array[a][b] = new Cell(Cell.State.INCLUSION);
            }
        }
    }

    private static int getY(int a, int sizeY) {
        return (int) (Math.floor(Math.random() * (sizeY - 2 - a)) + 1);
    }

    private static int getX(int a, int sizeX) {
        return (int) (Math.floor(Math.random() * (sizeX - 2 - a)) + 1);
    }

}

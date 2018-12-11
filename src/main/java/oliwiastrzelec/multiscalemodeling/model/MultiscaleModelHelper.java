package oliwiastrzelec.multiscalemodeling.model;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MultiscaleModelHelper {
    public static String getDataFile(Cell[][] array, int sizeX, int sizeY) {
        StringBuilder sb = new StringBuilder();
        sb.append("x\ty\tid\tr\tg\tb\tstate\n");
        Cell cell;
        for (int i = 0; i < sizeX; i++) {
            for (int j = 0; j < sizeY; j++) {
                cell = array[i][j];
                sb.append(i + "\t" + j + "\t" + cell.getId() + "\t" + cell.getRgb()[0] + "\t" + cell.getRgb()[1] + "\t" + cell.getRgb()[2] + "\t" + cell.getState() + "\n");
            }
        }
        return sb.toString();
    }

    public static void saveBitmap(final BufferedImage bufferedImage, final String path) {
        try {
            RenderedImage rendImage = bufferedImage;
            ImageIO.write(rendImage, "bmp", new File(path));
            //ImageIO.write(rendImage, "PNG", new File(path));
            //ImageIO.write(rendImage, "jpeg", new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage getBitmap(Cell[][] array) {
        final BufferedImage bufferedImage = new BufferedImage(array.length, array[0].length, BufferedImage.TYPE_INT_RGB);
        Color color;
        for (int x = 0; x < array.length; x++) {
            for (int y = 0; y < array[0].length; y++) {
                color = new Color(array[x][y].getRgb()[0], array[x][y].getRgb()[1], array[x][y].getRgb()[2]);
                bufferedImage.setRGB(x, y, color.getRGB());
            }
        }
        File file = new File("src/main/resources/static/bitmap.bmp");
        saveBitmap(bufferedImage, file.getAbsolutePath());
        return bufferedImage;
    }

    public static void printArray(Cell[][] array) {
        List<Cell[]> list = new ArrayList(Arrays.asList(array));
        list.forEach(cells -> System.out.println(Arrays.asList(cells)));
    }

    public static int[] generateRandomColor() {
        int[] color = new int[3];
//        color[0] = (int) (Math.floor(Math.random() * 50) + 150);
//        color[1] = (int) (Math.floor(Math.random() * 50) + 150);
        color[2] = 0;//(int) (Math.floor(Math.random() * 255) + 1);
        color[0] = (int) (Math.floor(Math.random() * 220) + 36);
        color[1] = (int) (Math.floor(Math.random() * 220) + 36);
        return color;
    }

    public static int[] generateRandomRecrystalizedColor() {
        int[] color = new int[3];
        color[0] = 0;
        color[1] = (int) (Math.floor(Math.random() * 220) + 36);
        color[2] = (int) (Math.floor(Math.random() * 220) + 36);
        return color;
    }

    private static Cell generateRandomCell(int x, int y) {
        Cell cell = new Cell(Integer.valueOf(String.valueOf(x) + String.valueOf(y)));
        cell.setRgb(generateRandomColor());
        return cell;
    }

    public static int countEmptyCells(Cell[][] array) {
        int count = 0;
        for (Cell[] cells : array) {
            for (Cell cell : cells) {
                if (cell.getId() == 0) {
                    count++;
                }
            }
        }
        return count;
    }

    static List<Cell> generateRandomCells(int numberOfNucleons) {
        List<Cell> cells = new ArrayList<>();
        for (int i = 1; i <= numberOfNucleons; i++) {
            Cell c = new Cell(i);
            c.setRgb(generateRandomColor());
            cells.add(c);
        }
        return cells;
    }

    static Cell[][] generateEmptyArray(int sizeX, int sizeY) {
        Cell[][] cells = new Cell[sizeX][sizeY];
        for (int i = 0; i < sizeY; i++) {
            for (int j = 0; j < sizeY; j++) {
                Cell c = new Cell(0);
                c.setRgb(new int[]{255, 255, 255});
                cells[i][j] = c;
            }
        }
        return cells;
    }

    protected static Cell[][] cloneArray(Cell[][] array) {
        Cell[][] clone = new Cell[array.length][array[0].length];
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[0].length; j++) {
                clone[i][j] = array[i][j];
            }
        }
        return clone;
    }

    static boolean isGrainsBorder(int x, int y, int id, Cell[][] array) {
        for (int i = x; i <= x + 2; i++) {
            if (i < 0 || i >= array.length) {
                continue;
            }
            if (array[i][y].getId() != id) {
                return true;
            }
        }
        for (int i = y; i <= y + 2; i++) {
            if (i < 0 || i >= array[0].length) {
                continue;
            }
            if (array[x][i].getId() != id) {
                return false;
            }
        }
        return false;
    }

    public static List<Cell> generateRandomRecrystalizedNucleons(int numberOfNucleons) {
        List<Cell> cells = new ArrayList<>();
        for (int i = 1; i <= numberOfNucleons; i++) {
            Cell c = new Cell(Cell.State.RECRYSTALIZED);
            cells.add(c);
        }
        return cells;
    }
}

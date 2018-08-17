package StoreByDirectedGraph;

import org.omg.PortableInterceptor.SYSTEM_EXCEPTION;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Line2D;

public class Drawer {

    private int height;
    private int width;
    private CellNode[] currentRow;
    private int gridSize;
    private final int offset;
    private final int linewidth;
    private int colorOffset;
    private int greenNum;

    public Drawer(int height, int width, CellNode[] initialRow) {
        this.height = height;
        this.width = width;
        this.currentRow = initialRow;
        colorOffset = 255 / (width * 2) - 1;
        greenNum = 100;
        //Automatically set the gridSize to reach a best fit.
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;
        gridSize = Math.min(screenHeight / (height + 1), screenWidth / (width + 1));
        //You can adjust linewidth to be thicker or thinner.
        linewidth = gridSize / 10;
        //Offset has to be this value
        offset = gridSize / 4;
    }

    public void displayGraph() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                paintGraph();
            }
        });
    }

    private void paintGraph() {
        JFrame f = new JFrame("The weaving pattern");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.add(new JComponent() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setStroke(new BasicStroke(linewidth));
                for (int row = height - 1; row >= 0; row--) {
                    // If row % 2 = 1, it means that the current row is wrapping around.
                    if (row % 2 != 0) {
                        // Deal with wrap around
                        Line2D[] leftLines = generateWrapLine(row, currentRow[width - 1].getTurningStatus()[0], 0);
                        Line2D[] rightLines = generateWrapLine(row, currentRow[width - 1].getTurningStatus()[1], 1);
                        switch (currentRow[width - 1].getCrossingStatus()) {
                            case RightTop:
                                setColor(g2, currentRow[width - 1].getThreads()[0]);
                                g2.draw(leftLines[0]);
                                g2.draw(leftLines[1]);
                                setColor(g2, currentRow[width - 1].getThreads()[1]);
                                g2.draw(rightLines[0]);
                                g2.draw(rightLines[1]);
                                break;
                            default:
                                if (rightLines != null) {
                                    setColor(g2, currentRow[width - 1].getThreads()[1]);
                                    if (rightLines[0] != null) {
                                        g2.draw(rightLines[0]);
                                    }
                                    if (rightLines[1] != null) {
                                        g2.draw(rightLines[1]);
                                    }
                                }
                                if (leftLines != null) {
                                    setColor(g2, currentRow[width - 1].getThreads()[0]);
                                    if (leftLines[0] != null) {
                                        g2.draw(leftLines[0]);
                                    }
                                    if (leftLines[1] != null) {
                                        g2.draw(leftLines[1]);
                                    }
                                }
                        }
                        currentRow[width - 1] = currentRow[width - 1].getNeighbors()[0];
                    }
                    // Deal with other cells
                    // If row % 2 == 1, we will not reach the last cell
                    for (int col = 0; col < width - row % 2; col++) {
                        Line2D left = generateUnwrapLine(row, col, currentRow[col].getTurningStatus()[0], 0);
                        Line2D right = generateUnwrapLine(row, col, currentRow[col].getTurningStatus()[1], 1);
                        switch (currentRow[col].getCrossingStatus()) {
                            case RightTop:
                                setColor(g2, currentRow[col].getThreads()[0]);
                                g2.draw(left);
                                setColor(g2, currentRow[col].getThreads()[1]);
                                g2.draw(right);
                                break;
                            default:
                                if (right != null) {
                                    setColor(g2, currentRow[col].getThreads()[1]);
                                    g2.draw(right);
                                }
                                if (left != null) {
                                    setColor(g2, currentRow[col].getThreads()[0]);
                                    g2.draw(left);
                                }
                        }
                        // Check if the current row is at the bottom of grid. If not, update current row
                        // If current row is wrapping around, set the left neighbor to be next cell
                        // Else, set right neighbor to be the next.
                        if (row != 0) {
                            currentRow[col] = currentRow[col].getNeighbors()[(row + 1) % 2];
                        }
                    }
                }
            }
        });
        f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        f.setVisible(true);
    }

    private void setColor(Graphics2D g2, int threadNum) {
        if (threadNum < 0) {
            return;
        }
        g2.setColor(new Color(255 - threadNum * colorOffset, (threadNum % 2 + 1) * greenNum, threadNum * colorOffset));
    }

    /**
     * The method generates a line2D object of a thread based on the information given.
     * It takes care of the thread that are not wrapping around.
     *
     * @param row    The row of the thread we are going to draw
     * @param col    The column of the thread we are going to draw
     * @param status The turning status of the thread.
     * @param side   If side = 0, the thread start at left bottom half of the cell.
     *               If side = 1, the thread start at right bottom half of the cell
     * @return Line2D The line2D object of the target thread.
     */
    private Line2D generateUnwrapLine(int row, int col, TurningStatus status, int side) {
        int wrap = row % 2;
        // Since java sets the top left corner to be origin, we modify the row to fit the coordinates.
        row = height - 1 - row;
        // Notice that if row % 2 != 0, we know that the current row is wraping around.
        // So we add 0.5 * gridSize to the x coordinates in that case.
        switch (status) {
            case Slanted:
                if (side == 0) {
                    return new Line2D.Double((col + wrap * 0.5) * gridSize + offset, (row + 1) * gridSize,
                            (col + 1 + wrap * 0.5) * gridSize - offset, row * gridSize);
                } else {
                    return new Line2D.Double((col + wrap * 0.5) * gridSize + offset, row * gridSize,
                            (col + 1 + wrap * 0.5) * gridSize - offset, (row + 1) * gridSize);
                }
            case Upright:
                if (side == 0) {
                    return new Line2D.Double((col + wrap * 0.5) * gridSize + offset, row * gridSize,
                            (col + wrap * 0.5) * gridSize + offset, (row + 1) * gridSize);
                } else {
                    return new Line2D.Double((col + 1 + wrap * 0.5) * gridSize - offset, (row + 1) * gridSize,
                            (col + 1 + wrap * 0.5) * gridSize - offset, row * gridSize);
                }
            case No:
                return null;
        }
        return null;
    }

    /**
     * The method generates a line2D array of a thread based on the information given.
     * It takes care of the thread that are at the edge of the grid and is wrapping around.
     * Notice that for the upright threads, we do not need to use all spots of the array.
     *
     * @param row    The row of the thread we are going to draw
     * @param status The turning status of the thread.
     * @param side   If side = 0, the thread start at left bottom half of the cell.
     *               If side = 1, the thread start at right bottom half of the cell
     * @return Line2D The line2D array of the target thread.
     */
    private Line2D[] generateWrapLine(int row, TurningStatus status, int side) {
        // Since java sets the top left corner to be origin, we modify the row to fit the coordinates.
        row = height - 1 - row;
        Line2D[] lines = new Line2D[2];
        switch (status) {
            case Slanted:
                if (side == 0) {
                    lines[0] = new Line2D.Double(0, (row + 0.5) * gridSize,
                            offset, row * gridSize);
                    lines[1] = new Line2D.Double(width * gridSize, (row + 0.5) * gridSize,
                            width * gridSize - offset, (row + 1) * gridSize);
                } else {
                    lines[0] = new Line2D.Double(0, (row + 0.5) * gridSize,
                            offset, (row + 1) * gridSize);
                    lines[1] = new Line2D.Double(width * gridSize, (row + 0.5) * gridSize,
                            width * gridSize - offset, row * gridSize);
                }
                break;
            case Upright:
                if (side == 0) {
                    lines[0] = new Line2D.Double(width * gridSize - offset, row * gridSize,
                            width * gridSize - offset, (row + 1) * gridSize);
                } else {
                    lines[0] = new Line2D.Double(offset, row * gridSize,
                            offset, (row + 1) * gridSize);
                }
                break;
            case No:
                return lines;
        }
        return lines;
    }
}

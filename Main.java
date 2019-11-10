/**********************************
 * Minesweeper on Java
 * Author: ImFuryPro
 * Version: 1.0
 * Description: Сапер на Java. Сделано чисто по фану и для того, чтобы вспомнить Java.
 *              Сделано на платформе JavaFX.
 **********************************/

package sample;

import javafx.application.Application;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;

public class Main extends Application {

    private static int gridW = 4;
    private static int gridH = 4;
    private static int cellSize = 50;
    private static int numMines = 1;

    private static int[][] mines;
    private static boolean[][] flags;
    private static boolean[][] revealed;
    private static boolean firstClick = true;
    private static boolean isGame = true;

    private static JFrame frame = new JFrame(); //создает фрейм
    private static JButton[][] grid;

    private static int freeCells = 0;
    private static int freeMines = 0;
    private static String gameState = "";

    private static int rnd(int max) {
        return (int) (Math.random() * ++max);
    }

    private static void setup() {
        mines = new int[gridW][gridH];
        flags = new boolean[gridW][gridH];
        revealed = new boolean[gridW][gridH];

        for (int x = 0; x < gridW; x++) {
            for (int y = 0; y < gridH; y++) {
                mines[x][y] = 0;
                flags[x][y] = false;
                revealed[x][y] = false;
            }
        }
    }

    private static void placeMines() {
        int i = 0;
        int rndW = gridW - 1;
        int rndH = gridH - 1;
        while (i < numMines) {
            int x = rnd(rndW);
            int y = rnd(rndH);
            if (mines[x][y] == 1) continue;
            mines[x][y] = 1;
            i++;
        }
    }

    private static void clearMines() {
        for (int x = 0; x < gridW; x++) {
            for (int y = 0; y < gridH; y++) {
                mines[x][y] = 0;
            }
        }
    }

    private static boolean outBounds(int x, int y) {
        return x < 0 || y < 0 || x >= gridW || y >= gridH;
    }

    private static int calcNear(int x, int y) {
        if (outBounds(x, y)) return 0;
        int i = 0;
        for (int oX = -1; oX <= 1; oX++) {
            for (int oY = -1; oY <= 1; oY++) {
                if (outBounds(oX + x, oY + y)) continue;
                i += mines[oX + x][oY + y];
            }
        }

        return i;
    }

    private static void reveal(int x, int y) {
        if (outBounds(x, y)) return;
        if (revealed[x][y]) return;
        revealed[x][y] = true;
        if (calcNear(x, y) > 0) {
            freeCells -= 1;
            return;
        } else {
            freeCells -= 1;
        }

        reveal(x - 1, y - 1);
        reveal(x - 1, y + 1);
        reveal(x + 1, y - 1);
        reveal(x + 1, y + 1);

        reveal(x - 1, y);
        reveal(x + 1, y);
        reveal(x, y - 1);
        reveal(x, y + 1);
    }

    private static void setButtons(JFrame frame, JButton[][] grid, int x, int y) {
        grid[x][y] = new JButton() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(cellSize, cellSize);
            }
        };

        grid[x][y].addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (isGame) {
                    if (grid[x][y].isEnabled()) {
                        System.out.println("Click");
                        if (evt.getButton() == MouseEvent.BUTTON3) {
                            flags[x][y] = !flags[x][y];
                            if (flags[x][y]) {
                                grid[x][y].setText("F");
                                freeMines--;
                            } else {
                                grid[x][y].setText("");
                                freeMines++;
                            }
                            frame.setTitle("Minesweeper | Mines left: " + freeMines + " " + gameState);
                        } else {
                            if (!flags[x][y]) {
                                if (firstClick) {
                                    firstClick = false;
                                    clearMines();
                                    placeMines();
                                }

                                if (mines[x][y] != 0) {
                                    System.out.println("Dang!");
                                    gameOver();
                                } else {
                                    reveal(x, y);
                                    draw();
                                    gameWin();
                                }
                            }
                        }
                    }
                }
            }
        });

        frame.add(grid[x][y]);
        freeCells++;
    }

    private static void gameOver() {
        isGame = false;
        gameState = "Game Lost!";

        for (int x = 0; x < gridW; x++) {
            for (int y = 0; y < gridH; y++) {
                if (mines[x][y] == 1) {
                    grid[x][y].setText("M");
                }
            }
        }
        frame.setTitle("Minesweeper | Mines left: " + freeMines + " " + gameState);
    }

    private static void gameWin() {
        if (freeCells == 0) {
            isGame = false;
            gameState = "Game Won!";

            frame.setTitle("Minesweeper | Mines left: " + freeMines + " " + gameState);
        }
    }

    private static void draw() {
        for (int x = 0; x < gridW; x++) {
            for (int y = 0; y < gridH; y++) {

                int near = calcNear(x, y);

                if (near > 0 && revealed[x][y]) {
                    if (mines[x][y] == 1) {
                        grid[x][y].setText("M");
                    } else {
                        if (near == 1 || near == 2 || near == 3 || near == 4 || near == 5) {
                            grid[x][y].setEnabled(false);
                            grid[x][y].setText(String.valueOf(near));
                        }
                    }
                } else if (near <= 0 && revealed[x][y]) {
                    grid[x][y].setEnabled(false);
                    grid[x][y].setText("");
                }
            }
        }
        System.out.println(freeMines);
    }

    private static void setting(int width, int length) {
        setup();

        freeCells = freeCells - numMines;
        freeMines = numMines;

        frame.setTitle("Minesweeper | Mines left: " + freeMines + " " + gameState);
        frame.setLayout(new GridLayout(width, length));
        frame.setResizable(false);
        grid = new JButton[width][length];

        for (int y = 0; y < length; y++) {
            for (int x = 0; x < width; x++) {
                setButtons(frame, grid, x, y);
            }
        }

        frame.setBackground(Color.PINK);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack(); //устанавливает соответствующий размер фрейма
        frame.setVisible(true); //делает фрейм видимым
    }

    @Override
    public void start(Stage stage) throws Exception {
        setting(gridW, gridH);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class AnimatedMinesweeper extends JFrame {
    private final int SIZE = 12, MINES = 20;
    private JButton[][] buttons = new JButton[SIZE][SIZE];
    private boolean[][] mines = new boolean[SIZE][SIZE];
    private boolean[][] revealed = new boolean[SIZE][SIZE];
    private boolean firstClick = true;

    public AnimatedMinesweeper() {
        setTitle("Minesweeper Classic");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridLayout(SIZE, SIZE));
        
        setupBoard();
        
        setSize(500, 500);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void setupBoard() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                JButton b = new JButton();
                b.setFocusPainted(false);
                b.setFont(new Font("Arial", Font.BOLD, 18));
                b.setBackground(new Color(189, 189, 189)); // Classic Grey
                b.setBorder(BorderFactory.createBevelBorder(0)); // 3D effect
                
                int row = r, col = c;
                b.addMouseListener(new MouseAdapter() {
                    public void mousePressed(MouseEvent e) {
                        if (SwingUtilities.isRightMouseButton(e)) toggleFlag(b);
                        else handleLeftClick(row, col);
                    }
                });
                buttons[r][c] = b;
                add(b);
            }
        }
    }

    private void handleLeftClick(int r, int c) {
        if (firstClick) {
            generateMines(r, c);
            firstClick = false;
        }
        reveal(r, c);
    }

    private void generateMines(int startR, int startC) {
        Random rand = new Random();
        int count = 0;
        while (count < MINES) {
            int r = rand.nextInt(SIZE), col = rand.nextInt(SIZE);
            if (!mines[r][col] && (Math.abs(r - startR) > 1 || Math.abs(col - startC) > 1)) {
                mines[r][col] = true;
                count++;
            }
        }
    }

    private void reveal(int r, int c) {
        if (r < 0 || r >= SIZE || c < 0 || c >= SIZE || revealed[r][c]) return;

        revealed[r][c] = true;
        JButton b = buttons[r][c];
        b.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // Flat look when revealed

        if (mines[r][c]) {
            b.setBackground(Color.RED);
            b.setText("💣");
            revealAllMines();
            JOptionPane.showMessageDialog(this, "Game Over!");
        } else {
            int mCount = countMines(r, c);
            b.setBackground(new Color(224, 224, 224));
            if (mCount > 0) {
                b.setText(String.valueOf(mCount));
                b.setForeground(getNumberColor(mCount));
            } else {
                // Animation-like delay for flood fill
                Timer timer = new Timer(30, e -> revealNeighbors(r, c));
                timer.setRepeats(false);
                timer.start();
            }
        }
    }

    private void toggleFlag(JButton b) {
        if (b.getText().equals("🚩")) b.setText("");
        else if (b.getText().equals("")) b.setText("🚩");
    }

    private int countMines(int r, int c) {
        int count = 0;
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                int nr = r + i, nc = c + j;
                if (nr >= 0 && nr < SIZE && nc >= 0 && nc < SIZE && mines[nr][nc]) count++;
            }
        }
        return count;
    }

    private void revealNeighbors(int r, int c) {
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) reveal(r + i, c + j);
        }
    }

    private void revealAllMines() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) if (mines[r][c]) buttons[r][c].setText("💣");
        }
    }

    private Color getNumberColor(int n) {
        return switch (n) {
            case 1 -> Color.BLUE;
            case 2 -> new Color(0, 128, 0);
            case 3 -> Color.RED;
            case 4 -> new Color(0, 0, 128);
            default -> new Color(128, 0, 0);
        };
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(AnimatedMinesweeper::new);
    }
}

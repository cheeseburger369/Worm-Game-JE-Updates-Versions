import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.io.*;

public class TheWormGame extends JFrame implements ActionListener, KeyListener {
    // Game settings
    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;
    private static final int GRID_SIZE = 20;
    private static final int COLS = WIDTH / GRID_SIZE;
    private static final int ROWS = HEIGHT / GRID_SIZE;
    private static final int GAME_SPEED = 150; // milliseconds between moves
    
    // Colors
    private static final Color BLACK = Color.BLACK;
    private static final Color GREEN = Color.GREEN;
    private static final Color RED = Color.RED;
    private static final Color WHITE = Color.WHITE;
    private static final Color YELLOW = Color.YELLOW;
    
    // Game objects
    private List<Point> worm;
    private Point apple;
    private Point direction;
    private int score;
    private int highscore;
    private boolean gameRunning;
    private boolean gameOver;
    private boolean showMenu;
    
    // UI
    private javax.swing.Timer gameTimer;
    private GamePanel gamePanel;
    
    public TheWormGame() {
        loadHighscore();
        showMenu = true;
        gameRunning = false;
        gameOver = false;
        setupUI();
        gameTimer = new javax.swing.Timer(GAME_SPEED, this);
        gameTimer.start();
    }
    
    private void initGame() {
        worm = new ArrayList<>();
        worm.add(new Point(5, 5));
        worm.add(new Point(4, 5));
        worm.add(new Point(3, 5));
        
        direction = new Point(1, 0); // moving right
        spawnApple();
        score = 0;
        showMenu = false;
        gameRunning = true;
        gameOver = false;
    }
    
    private void setupUI() {
        setTitle("The Worm Game - Java Edition 1.0");
        setSize(WIDTH, HEIGHT);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLocationRelativeTo(null);
        
        gamePanel = new GamePanel();
        add(gamePanel);
        addKeyListener(this);
        setFocusable(true);
        setVisible(true);
    }
    
    private void spawnApple() {
        Random rand = new Random();
        do {
            apple = new Point(rand.nextInt(COLS), rand.nextInt(ROWS));
        } while (worm.contains(apple));
    }
    
    private void loadHighscore() {
        try {
            File file = new File("highscore.txt");
            if (file.exists()) {
                Scanner scanner = new Scanner(file);
                if (scanner.hasNextInt()) {
                    highscore = scanner.nextInt();
                }
                scanner.close();
            }
        } catch (Exception e) {
            highscore = 0;
        }
    }
    
    private void saveHighscore() {
        try {
            if (score > highscore) {
                highscore = score;
                PrintWriter writer = new PrintWriter(new FileWriter("highscore.txt"));
                writer.println(score);
                writer.close();
            }
        } catch (Exception e) {
            // Ignore errors
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameRunning) {
            moveWorm();
            checkCollisions();
            checkApple();
        }
        gamePanel.repaint();
    }
    
    private void moveWorm() {
        Point head = new Point(worm.get(0));
        head.x += direction.x;
        head.y += direction.y;
        
        worm.add(0, head);
        worm.remove(worm.size() - 1);
    }
    
    private void checkCollisions() {
        Point head = worm.get(0);
        
        // Check wall collision
        if (head.x < 0 || head.x >= COLS || head.y < 0 || head.y >= ROWS) {
            gameOver();
            return;
        }
        
        // Check self collision
        for (int i = 1; i < worm.size(); i++) {
            if (head.equals(worm.get(i))) {
                gameOver();
                return;
            }
        }
    }
    
    private void checkApple() {
        if (worm.get(0).equals(apple)) {
            score++;
            worm.add(new Point(worm.get(worm.size() - 1))); // grow worm
            spawnApple();
        }
    }
    
    private void gameOver() {
        gameRunning = false;
        gameOver = true;
        saveHighscore();
    }
    
    private void restart() {
        initGame();
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        if (showMenu) {
            if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                initGame();
            }
            return;
        }
        
        if (!gameRunning && gameOver) {
            if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                restart();
            }
            return;
        }
        
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                if (direction.y != 1) direction = new Point(0, -1);
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                if (direction.y != -1) direction = new Point(0, 1);
                break;
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                if (direction.x != 1) direction = new Point(-1, 0);
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                if (direction.x != -1) direction = new Point(1, 0);
                break;
        }
    }
    
    @Override
    public void keyReleased(KeyEvent e) {}
    
    @Override
    public void keyTyped(KeyEvent e) {}
    
    private class GamePanel extends JPanel {
        public GamePanel() {
            setBackground(BLACK);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            // Black background
            g.setColor(BLACK);
            g.fillRect(0, 0, getWidth(), getHeight());
            
            if (showMenu) {
                // Start menu - centered using actual component size
                int componentWidth = getWidth();
                int componentHeight = getHeight();
                
                g.setColor(RED);
                g.setFont(new Font("Arial", Font.BOLD, 48));
                FontMetrics fm = g.getFontMetrics();
                String title = "THE WORM. JAVA EDITION";
                int titleX = (componentWidth - fm.stringWidth(title)) / 2;
                int titleY = (componentHeight / 2) - 30;
                g.drawString(title, titleX, titleY);
                
                g.setColor(WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 24));
                fm = g.getFontMetrics();
                String subtitle = "press ENTER to start the game";
                int subtitleX = (componentWidth - fm.stringWidth(subtitle)) / 2;
                int subtitleY = (componentHeight / 2) + 40;
                g.drawString(subtitle, subtitleX, subtitleY);
                
            } else if (gameRunning) {
                // Draw worm
                g.setColor(GREEN);
                for (Point segment : worm) {
                    g.fillRect(segment.x * GRID_SIZE, segment.y * GRID_SIZE, GRID_SIZE, GRID_SIZE);
                }
                
                // Draw apple
                g.setColor(RED);
                g.fillRect(apple.x * GRID_SIZE, apple.y * GRID_SIZE, GRID_SIZE, GRID_SIZE);
                
                // Draw score
                g.setColor(WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 16));
                g.drawString("Score: " + score, 10, 25);
                g.drawString("High Score: " + highscore, 10, 45);
                
            } else if (gameOver) {
                // Game over screen - centered using actual component size
                int componentWidth = getWidth();
                int componentHeight = getHeight();
                
                g.setColor(WHITE);
                g.setFont(new Font("Arial", Font.BOLD, 36));
                FontMetrics fm = g.getFontMetrics();
                String gameOverText = "GAME OVER";
                int gameOverX = (componentWidth - fm.stringWidth(gameOverText)) / 2;
                int gameOverY = (componentHeight / 2) - 80;
                g.drawString(gameOverText, gameOverX, gameOverY);
                
                g.setFont(new Font("Arial", Font.BOLD, 24));
                fm = g.getFontMetrics();
                String scoreText = "Final Score: " + score;
                int scoreX = (componentWidth - fm.stringWidth(scoreText)) / 2;
                int scoreY = (componentHeight / 2) - 20;
                g.drawString(scoreText, scoreX, scoreY);
                
                String highScoreText = "High Score: " + highscore;
                int highScoreX = (componentWidth - fm.stringWidth(highScoreText)) / 2;
                int highScoreY = (componentHeight / 2) + 20;
                g.drawString(highScoreText, highScoreX, highScoreY);
                
                g.setFont(new Font("Arial", Font.BOLD, 18));
                fm = g.getFontMetrics();
                String restartText = "Press SPACE to play again";
                int restartX = (componentWidth - fm.stringWidth(restartText)) / 2;
                int restartY = (componentHeight / 2) + 80;
                g.drawString(restartText, restartX, restartY);
            }
            
            // Draw border using actual component size
            g.setColor(YELLOW);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setStroke(new BasicStroke(3));
            g2d.drawRect(1, 1, getWidth() - 3, getHeight() - 3);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TheWormGame());
    }
}
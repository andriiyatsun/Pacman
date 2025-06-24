import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;

public class GameWindow extends JPanel implements ActionListener {

    private Dimension d; // The dimension of the game window
    private final Font smallFont = new Font("Arial", Font.BOLD, 14); // Font used for drawing score
    private boolean inGame = false; // Indicates if the game is currently running
    private boolean dying = false; // Indicates if the player is dying

    private final int BLOCK_SIZE = 24; // Size of each block in the maze
    private final int N_BLOCKS = 15; // Number of blocks in one row/column
    private final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE; // Total screen size
    private final int MAX_GHOSTS = 12; // Maximum number of ghosts in the game
    private int lastSpeedIncreaseScore = 0; // Last score at which ghost speed increased
    private final int MAX_SCORE = 194; // Maximum score to win

    private int N_GHOSTS = 6; // Number of ghosts currently in the game
    private int lives, score; // Player's lives and score
    private int[] dx, dy; // Directions for movement
    private int[] ghost_x, ghost_y, ghost_dx, ghost_dy, ghostSpeed; // Ghost position and speed

    private Image heart, ghost; // Images for heart and ghost
    private Image up, down, left, right; // Images for Pac-Man directions

    private int pacman_x, pacman_y, pacmand_x, pacmand_y; // Pac-Man's current position and direction
    private int req_dx, req_dy; // Requested movement direction

    private final short levelData[] = {
            19, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 18, 22,
            17, 16, 16, 16, 16, 24, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            25, 24, 24, 24, 28, 0, 17, 16, 16, 16, 16, 16, 16, 16, 20,
            0,  0,  0,  0,  0,  0, 17, 16, 16, 16, 16, 16, 16, 16, 20,
            19, 18, 18, 18, 18, 18, 16, 16, 16, 16, 24, 24, 24, 24, 20,
            17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,
            17, 16, 16, 16, 16, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,
            17, 16, 16, 16, 24, 16, 16, 16, 16, 20, 0,  0,  0,   0, 21,
            17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 18, 18, 18, 18, 20,
            17, 24, 24, 28, 0, 25, 24, 24, 16, 16, 16, 16, 16, 16, 20,
            21, 0,  0,  0,  0,  0,  0,   0, 17, 16, 16, 16, 16, 16, 20,
            17, 18, 18, 22, 0, 19, 18, 18, 16, 16, 16, 16, 16, 16, 20,
            17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            17, 16, 16, 20, 0, 17, 16, 16, 16, 16, 16, 16, 16, 16, 20,
            25, 24, 24, 24, 26, 24, 24, 24, 24, 24, 24, 24, 24, 24, 28
    };

    private final int[] validSpeeds = {1, 2, 3, 4, 6, 8}; // Valid ghost speeds


    private int currentSpeed = 3; // Current speed of the ghosts
    private short[] screenData; // Data representing the maze
    private Timer timer; // Timer for game refresh


    public GameWindow() {
        loadImages(); // Loads images for characters and objects
        initVariables(); // Initializes game variables
        addKeyListener(new TAdapter()); // Adds key listener for player input
        setFocusable(true); // Makes the panel focusable to accept key events
        initGame(); // Initializes the game state
    }

    /**
     * Method that load game images
     */
    private void loadImages() {
        down = new ImageIcon("resources/images/game/down.gif").getImage();
        up = new ImageIcon("resources/images/game/up.gif").getImage();
        left = new ImageIcon("resources/images/game/left.gif").getImage();
        right = new ImageIcon("resources/images/game/right.gif").getImage();
        ghost = new ImageIcon("resources/images/game/ghost.gif").getImage();
        heart = new ImageIcon("resources/images/game/heart.png").getImage();

    }

    /**
     * Method that initialize starting game variables
     */
    private void initVariables() {
        screenData = new short[N_BLOCKS * N_BLOCKS];
        d = new Dimension(400, 400);
        ghost_x = new int[MAX_GHOSTS];
        ghost_dx = new int[MAX_GHOSTS];
        ghost_y = new int[MAX_GHOSTS];
        ghost_dy = new int[MAX_GHOSTS];
        ghostSpeed = new int[MAX_GHOSTS];
        dx = new int[4];
        dy = new int[4];

        timer = new Timer(40, this);
        timer.start();
    }

    private void playGame(Graphics2D g2d) {
        if (dying) {
            death();
        } else {
            movePacman();
            drawPacman(g2d);
            moveGhosts(g2d);
            checkMaze();
        }

        if (score >= MAX_SCORE) {
            inGame = false;
            showVictoryMessage();
            MenuWindow menu = new MenuWindow();
            setVisible(false);
        }
    }

    /**
     * Method that show intro message
     */
    private void showIntroScreen(Graphics2D g2d) {
        String start = "Press SPACE to start";
        g2d.setColor(Color.white);
        g2d.drawString(start, (SCREEN_SIZE)/4, 150);
    }

    /**
     * Method that draw gamescore
     */
    private void drawScore(Graphics2D g) {
        g.setFont(smallFont);
        g.setColor(new Color(255, 255, 255));
        String s = "Score: " + score;
        g.drawString(s, SCREEN_SIZE / 2 + 96, SCREEN_SIZE + 16);
        for (int i = 0; i < lives; i++) {
            g.drawImage(heart, i * 28 + 8, SCREEN_SIZE + 1, this);
        }
    }

    /**
     * Method that check game field and
     */
    private void checkMaze() {
        int i = 0;
        boolean finished = true;

        while (i < N_BLOCKS * N_BLOCKS && finished) {
            if ((screenData[i]) != 0) {
                finished = false;
            }
            i++;
        }

        if (finished) {

            if (N_GHOSTS < MAX_GHOSTS) {
                N_GHOSTS++;
            }
            initLevel();
        }
    }


    /**
     * Method that decrease live amount, and close window if life is equals zero.
     */
    private void death() {
        lives--;
        if (lives == 0) {
            inGame = false;
            showGameOverMessage();
            MenuWindow menu = new MenuWindow();
            setVisible(false);
        } else {
            continueLevel();
        }
    }

    private void moveGhosts(Graphics2D g2d) {

        int pos;
        int count;

        for (int i = 0; i < N_GHOSTS; i++) {
            if (ghost_x[i] % BLOCK_SIZE == 0 && ghost_y[i] % BLOCK_SIZE == 0) {
                pos = ghost_x[i] / BLOCK_SIZE + N_BLOCKS * (int) (ghost_y[i] / BLOCK_SIZE);

                count = 0;

                if ((screenData[pos] & 1) == 0 && ghost_dx[i] != 1) {
                    dx[count] = -1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 2) == 0 && ghost_dy[i] != 1) {
                    dx[count] = 0;
                    dy[count] = -1;
                    count++;
                }

                if ((screenData[pos] & 4) == 0 && ghost_dx[i] != -1) {
                    dx[count] = 1;
                    dy[count] = 0;
                    count++;
                }

                if ((screenData[pos] & 8) == 0 && ghost_dy[i] != -1) {
                    dx[count] = 0;
                    dy[count] = 1;
                    count++;
                }

                if (count == 0) {

                    if ((screenData[pos] & 15) == 15) {
                        ghost_dx[i] = 0;
                        ghost_dy[i] = 0;
                    } else {
                        ghost_dx[i] = -ghost_dx[i];
                        ghost_dy[i] = -ghost_dy[i];
                    }

                } else {

                    count = (int) (Math.random() * count);

                    if (count > 3) {
                        count = 3;
                    }

                    ghost_dx[i] = dx[count];
                    ghost_dy[i] = dy[count];
                }

            }

            ghost_x[i] = ghost_x[i] + (ghost_dx[i] * ghostSpeed[i]);
            ghost_y[i] = ghost_y[i] + (ghost_dy[i] * ghostSpeed[i]);
            drawGhost(g2d, ghost_x[i] + 1, ghost_y[i] + 1);

            if (pacman_x > (ghost_x[i] - 12) && pacman_x < (ghost_x[i] + 12)
                    && pacman_y > (ghost_y[i] - 12) && pacman_y < (ghost_y[i] + 12)
                    && inGame) {
                dying = true;
            }
        }
    }

    private void drawGhost(Graphics2D g2d, int x, int y) {
        g2d.drawImage(ghost, x, y, this);
    }

    private void movePacman() {
        int pos;
        short ch;

        if (pacman_x % BLOCK_SIZE == 0 && pacman_y % BLOCK_SIZE == 0) {
            pos = pacman_x / BLOCK_SIZE + N_BLOCKS * (int) (pacman_y / BLOCK_SIZE);
            ch = screenData[pos];

            if ((ch & 16) != 0) {
                screenData[pos] = (short) (ch & 15);
                score++;

                checkSpeedIncrease();
            }

            if (req_dx != 0 || req_dy != 0) {
                if (!((req_dx == -1 && req_dy == 0 && (ch & 1) != 0)
                        || (req_dx == 1 && req_dy == 0 && (ch & 4) != 0)
                        || (req_dx == 0 && req_dy == -1 && (ch & 2) != 0)
                        || (req_dx == 0 && req_dy == 1 && (ch & 8) != 0))) {
                    pacmand_x = req_dx;
                    pacmand_y = req_dy;
                }
            }

            // Check for standstill
            if ((pacmand_x == -1 && pacmand_y == 0 && (ch & 1) != 0)
                    || (pacmand_x == 1 && pacmand_y == 0 && (ch & 4) != 0)
                    || (pacmand_x == 0 && pacmand_y == -1 && (ch & 2) != 0)
                    || (pacmand_x == 0 && pacmand_y == 1 && (ch & 8) != 0)) {
                pacmand_x = 0;
                pacmand_y = 0;
            }
        }
        int PACMAN_SPEED = 4;
        pacman_x = pacman_x + PACMAN_SPEED * pacmand_x;
        pacman_y = pacman_y + PACMAN_SPEED * pacmand_y;
    }

    private void checkSpeedIncrease() {
        if (score / 100 > lastSpeedIncreaseScore / 100) {
            for (int i = 0; i < N_GHOSTS; i++) {
                // Знаходимо поточний індекс швидкості
                int speedIndex = 0;
                for (int j = 0; j < validSpeeds.length; j++) {
                    if (validSpeeds[j] == ghostSpeed[i]) {
                        speedIndex = j;
                        break;
                    }
                }

                // Збільшуємо індекс на 1, але не більше максимального
                speedIndex = Math.min(speedIndex + 1, validSpeeds.length - 1);
                ghostSpeed[i] = validSpeeds[speedIndex];
            }

            lastSpeedIncreaseScore = score;
            System.out.println("Speed increased! Score: " + score);
        }
    }

    private void drawPacman(Graphics2D g2d) {

        if (req_dx == -1) {
            g2d.drawImage(left, pacman_x + 1, pacman_y + 1, this);
        } else if (req_dx == 1) {
            g2d.drawImage(right, pacman_x + 1, pacman_y + 1, this);
        } else if (req_dy == -1) {
            g2d.drawImage(up, pacman_x + 1, pacman_y + 1, this);
        } else {
            g2d.drawImage(down, pacman_x + 1, pacman_y + 1, this);
        }
    }

    /**
     * Method that draw maze
     */
    /**
     * Method that draw maze
     */
    private void drawMaze(Graphics2D g2d) {
        short i = 0;
        int x, y;

        for (y = 0; y < SCREEN_SIZE; y += BLOCK_SIZE) {
            for (x = 0; x < SCREEN_SIZE; x += BLOCK_SIZE) {

                g2d.setColor(new Color(255, 255, 255));
                g2d.setStroke(new BasicStroke(4));

                if ((levelData[i] == 0)) {
                    g2d.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE); // Draw empty block
                }

                if ((screenData[i] & 1) != 0) {
                    g2d.drawLine(x, y, x, y + BLOCK_SIZE - 1); // Draw left wall
                }

                if ((screenData[i] & 2) != 0) {
                    g2d.drawLine(x, y, x + BLOCK_SIZE - 1, y); // Draw top wall
                }

                if ((screenData[i] & 4) != 0) {
                    g2d.drawLine(x + BLOCK_SIZE - 1, y, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1); // Draw right wall
                }

                if ((screenData[i] & 8) != 0) {
                    g2d.drawLine(x, y + BLOCK_SIZE - 1, x + BLOCK_SIZE - 1,
                            y + BLOCK_SIZE - 1); // Draw bottom wall
                }

                if ((screenData[i] & 16) != 0) {
                    g2d.setColor(new Color(255,255,255));
                    g2d.fillOval(x + 10, y + 10, 6, 6); // Draw dot
                }

                i++;
            }
        }
    }

    /**
     * Method that initialize game
     */
    private void initGame() {
        lives = 3; // Set starting lives
        score = 0; // Set starting score
        initLevel(); // Initialize the level
        N_GHOSTS = 6; // Set number of ghosts
        currentSpeed = 3; // Set current speed
    }

    private void initLevel() {
        int i;
        for (i = 0; i < N_BLOCKS * N_BLOCKS; i++) {
            screenData[i] = levelData[i]; // Copy level layout data
        }

        continueLevel(); // Continue to the next level
    }

    private void continueLevel() {
        int dx = 1;
        int random;

        for (int i = 0; i < N_GHOSTS; i++) {
            ghost_y[i] = 4 * BLOCK_SIZE; // Start position for ghosts
            ghost_x[i] = 4 * BLOCK_SIZE;
            ghost_dy[i] = 0;
            ghost_dx[i] = dx;
            dx = -dx;
            random = (int) (Math.random() * (currentSpeed + 1)); // Randomize ghost speed

            if (random > currentSpeed) {
                random = currentSpeed;
            }

            ghostSpeed[i] = validSpeeds[random]; // Set ghost speed
        }

        pacman_x = 7 * BLOCK_SIZE;  // Start position for Pac-Man
        pacman_y = 11 * BLOCK_SIZE;
        pacmand_x = 0; // Reset Pac-Man's movement direction
        pacmand_y = 0;
        req_dx = 0; // Reset requested direction
        req_dy = 0;
        dying = false; // Reset dying state
    }

    /**
     * Mehtod that do starting game visualization
     *
     * @param g the <code>Graphics</code> object to protect
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(Color.black);
        g2d.fillRect(0, 0, d.width, d.height); // Fill background with black color

        drawMaze(g2d); // Draw the maze
        drawScore(g2d); // Draw the score

        if (inGame) {
            playGame(g2d); // Play the game if inGame is true
        } else {
            showIntroScreen(g2d); // Show intro screen if not inGame
        }

        Toolkit.getDefaultToolkit().sync();
        g2d.dispose(); // Clean up after painting
    }

    /**
     * Method that process keypad buttons pressed
     */
    class TAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {

            int key = e.getKeyCode();

            if (inGame) {
                if (key == KeyEvent.VK_LEFT) {
                    req_dx = -1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_RIGHT) {
                    req_dx = 1;
                    req_dy = 0;
                } else if (key == KeyEvent.VK_UP) {
                    req_dx = 0;
                    req_dy = -1;
                } else if (key == KeyEvent.VK_DOWN) {
                    req_dx = 0;
                    req_dy = 1;
                } else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()) {
                    inGame = false;
                }
            } else {
                if (key == KeyEvent.VK_SPACE) {
                    inGame = true;
                    initGame();
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        repaint();
    }

    private void showVictoryMessage() {
        JOptionPane.showMessageDialog(this, "You win! All dots collected.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showGameOverMessage() {
        JOptionPane.showMessageDialog(this, "Game Over! You lost all lives.", "Game Over", JOptionPane.INFORMATION_MESSAGE);
    }
}
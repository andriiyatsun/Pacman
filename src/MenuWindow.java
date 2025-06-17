import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MenuWindow extends JFrame {

    private static final int WINDOW_HEIGHT = 420;
    private static final int WINDOW_WIDTH = 380;

    private static final int LABEL_NAME_WIDTH = 300;
    private static final int LABEL_NAME_HEIGHT = 100;

    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 60;

    private static final int BUTTON_FONT_SIZE = 14;

    private static final int LABEL_NAME_FONT_SIZE = 30;

    private static final int BUTTON_VERTICAL_MARGIN = 15;

    public MenuWindow() {
        /* Set window parameters */
        ImageIcon icon = new ImageIcon("resources/images/logo.png");
        setIconImage(icon.getImage());

        this.setTitle("PAC-MAN");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        this.setLocationRelativeTo(null);
        this.setResizable(false);

        /* Creating container with BoxLayout for vertical alignment */
        JPanel panel = new JPanel();
        panel.setBackground(new Color(10, 144, 45));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        /* Creating buttons */
        JLabel game_name = createLabel("\"PAC-MAN\"");
        JButton button_play = createButton("PLAY");
        JButton button_scores = createButton("SCORES");
        JButton button_exit = createButton("EXIT");


        /* Adding Action Listener to each button */
        button_play.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Play button clicked");
                new Pacman();
                setVisible(false);
            }
        });

        button_scores.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Scores button clicked");
            }
        });

        button_exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {System.exit(0);}
        });

        /* Adding vertical glue to center buttons vertically */
        panel.add(Box.createVerticalGlue());

        /* Adding buttons to the panel with space between them */
        panel.add(game_name);
        panel.add(Box.createRigidArea(new Dimension(0, BUTTON_VERTICAL_MARGIN)));
        panel.add(button_play);
        panel.add(Box.createRigidArea(new Dimension(0, BUTTON_VERTICAL_MARGIN)));
        ///game scores
//        panel.add(button_scores);
//        panel.add(Box.createRigidArea(new Dimension(0, BUTTON_VERTICAL_MARGIN)));
        panel.add(button_exit);

        /* Adding vertical glue to center buttons vertically */
        panel.add(Box.createVerticalGlue());

        /* Adding panel to window */
        this.add(panel);

        /* Show window */
        this.setVisible(true);
    }

    public static void main(String[] args) {
        new MenuWindow();  // Creating and adding window
    }

    private JButton createButton(String text){
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, BUTTON_FONT_SIZE));

        button.setBackground(Color.BLACK);  // Колір фону
        button.setForeground(Color.yellow);

        button.setAlignmentX(Component.CENTER_ALIGNMENT);

        Dimension buttonSize = new Dimension(BUTTON_WIDTH, BUTTON_HEIGHT);
        button.setPreferredSize(buttonSize);
        button.setMinimumSize(buttonSize);
        button.setMaximumSize(buttonSize);

        return button;
    }

    private JLabel createLabel(String text){
        JLabel label = new JLabel(text, JLabel.CENTER);
        label.setFont(new Font("Arial", Font.BOLD, LABEL_NAME_FONT_SIZE));
        label.setForeground(Color.yellow);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        Dimension labelSize = new Dimension(LABEL_NAME_WIDTH, LABEL_NAME_HEIGHT);
        label.setPreferredSize(labelSize);
        label.setMinimumSize(labelSize);
        label.setMaximumSize(labelSize);

        return label;
    }
}
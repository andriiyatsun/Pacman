import javax.swing.JFrame;

public class Frog extends JFrame{

    public Frog() {
        add(new GameWindow());

        this.setVisible(true);
        this.setTitle("Frog Adventures");
        this.setSize(380,425);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }
}
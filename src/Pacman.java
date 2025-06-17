import javax.swing.JFrame;

public class Pacman extends JFrame{

    public Pacman() {
        add(new GameWindow());

        this.setVisible(true);
        this.setTitle("PAC-MAN");
        this.setSize(380,425);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);
    }
}
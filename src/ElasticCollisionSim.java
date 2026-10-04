import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class ElasticCollisionSim extends JPanel implements ActionListener {
    private double mA, mB;
    private double vA = 0, vB;
    private double posA = 60;
    private double posB = 300;
    private final int blockSize = 40;
    private final int wallX = 20;

    private int collisionCount = 0;
    private javax.swing.Timer timer;

    // List of active collision effects
    private List<CollisionEffect> effects = new ArrayList<>();

    public ElasticCollisionSim(double mA, double mB) {
        this.mA = mA;
        this.mB = mB;
        this.vB = -10.0;
        this.vA = 0.0;

        timer = new javax.swing.Timer(16, this);
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int width = getWidth();
        int height = getHeight();

        // background
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

        int screenCenter = width / 2;
        double offset = posB - screenCenter;

        g.setColor(Color.LIGHT_GRAY);
        int spacing = 50;
        int startX = -((int) offset % spacing);
        for (int x = startX; x < width; x += spacing) {
            g.drawLine(x, 0, x, height);
            int worldCoord = (int) (x + offset);
            g.drawString(Integer.toString(worldCoord), x + 2, height - 10);
        }

        g.setColor(Color.BLACK);
        g.fillRect((int) (wallX - offset), 50, 10, 100);

        g.setColor(Color.BLUE);
        g.fillRect((int) (posA - offset), 80, blockSize, blockSize);

        g.setColor(Color.RED);
        g.fillRect(screenCenter, 80, blockSize, blockSize);

        g.setColor(Color.BLACK);
        g.drawString("Collisions: " + collisionCount, 20, 20);
        g.drawString("mA=" + (long) mA + "  mB=" + (long) mB, 140, 20);

        for (Iterator<CollisionEffect> it = effects.iterator(); it.hasNext();) {
            CollisionEffect fx = it.next();
            if (fx.isAlive()) {
                fx.draw(g, offset);
            } else {
                it.remove();
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        posA += vA * 0.1;
        posB += vB * 0.1;

        // --- A hits wall ---
        if (posA <= wallX + 10) {
            collisionCount++;
            vA = -vA;
            posA = wallX + 11;

            Toolkit.getDefaultToolkit().beep();
            effects.add(new CollisionEffect(posA, 80 + blockSize / 2.0));
        }

        if (posB <= posA + blockSize) {
            collisionCount++;

            double newVA = ((mA - mB) / (mA + mB)) * vA + ((2 * mB) / (mA + mB)) * vB;
            double newVB = ((2 * mA) / (mA + mB)) * vA + ((mB - mA) / (mA + mB)) * vB;

            vA = newVA;
            vB = newVB;

            posB = posA + blockSize + 1;

            Toolkit.getDefaultToolkit().beep();
            effects.add(new CollisionEffect(posB, 80 + blockSize / 2.0));
        }

        repaint();
    }

    private static class CollisionEffect {
        double x, y;
        int life = 20;

        CollisionEffect(double x, double y) {
            this.x = x;
            this.y = y;
        }

        void draw(Graphics g, double offset) {
            int size = (21 - life) * 3;
            int alpha = (int) (255 * (life / 20.0));
            g.setColor(new Color(255, 200, 0, alpha));
            g.fillOval((int) (x - offset - size / 2), (int) (y - size / 2), size, size);
            life--;
        }

        boolean isAlive() {
            return life > 0;
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter integer power for Block A (mass = 100^pA): ");
        int pA = sc.nextInt();
        System.out.print("Enter integer power for Block B (mass = 100^pB): ");
        int pB = sc.nextInt();
        sc.close();

        double mA = Math.pow(100, pA);
        double mB = Math.pow(100, pB);

        JFrame frame = new JFrame("Elastic Collision Simulation (With Effects)");
        ElasticCollisionSim sim = new ElasticCollisionSim(mA, mB);

        frame.add(sim);
        frame.setSize(800, 250);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.toFront();
        frame.requestFocus();
    }
}

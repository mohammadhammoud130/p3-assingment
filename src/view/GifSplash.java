package view;

import javax.swing.*;
import java.awt.*;

public class GifSplash {

    public static void showIntro(JFrame frame) {
        // Load and scale the GIF to 1920x1080
        ImageIcon originalGif = new ImageIcon("assets/intro.gif");
        Image scaledImage = originalGif.getImage().getScaledInstance(1920, 1080, Image.SCALE_DEFAULT);
        ImageIcon scaledGif = new ImageIcon(scaledImage);

        JLabel gifLabel = new JLabel(scaledGif);
        gifLabel.setHorizontalAlignment(JLabel.CENTER);
        gifLabel.setVerticalAlignment(JLabel.CENTER);

        // Set panel background to match GIF (optional)
        JPanel splashPanel = new JPanel(new BorderLayout());
        splashPanel.setBackground(Color.BLACK); // or match your GIF background color
        splashPanel.add(gifLabel, BorderLayout.CENTER);

        frame.setContentPane(splashPanel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Transition to login after delay
        Timer timer = new Timer(8000, e -> Login.login(frame));
        timer.setRepeats(false);
        timer.start();
    }
}

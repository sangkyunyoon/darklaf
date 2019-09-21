import com.weis.darklaf.LafManager;

import javax.swing.*;
import java.awt.*;

public final class SplitPaneDemo {

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> {
            LafManager.loadLaf(LafManager.Theme.Dark);
            final var frame = new JFrame();
            frame.setSize(500, 500);
            var splitPane = new JSplitPane() {
            };
            splitPane.setLeftComponent(new JPanel() {{
                setBackground(Color.RED);
            }});
            splitPane.setRightComponent(new JPanel() {{
                setBackground(Color.BLUE);
            }});
            splitPane.putClientProperty("JSplitPane.style", "line");
            splitPane.setOneTouchExpandable(true);
            frame.setContentPane(new JPanel(new BorderLayout()) {{
                add(splitPane, BorderLayout.CENTER);
            }});
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }
}

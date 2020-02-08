/*
 * MIT License
 *
 * Copyright (c) 2020 Jannis Weis
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package demo.tabFrame;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.components.SelectableTreeNode;
import com.github.weisj.darklaf.components.alignment.Alignment;
import com.github.weisj.darklaf.components.border.DarkBorders;
import com.github.weisj.darklaf.components.tabframe.JTabFrame;
import com.github.weisj.darklaf.components.tabframe.TabbedPopup;
import com.github.weisj.darklaf.components.text.NonWrappingTextPane;
import com.github.weisj.darklaf.components.text.NumberedTextComponent;
import com.github.weisj.darklaf.components.text.NumberingPane;
import com.github.weisj.darklaf.icons.IconLoader;
import com.github.weisj.darklaf.util.StringUtil;
import demo.DemoResources;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

public class TabFrameDemo {

    public static void main(final String[] args) {
        //Todo Rework Demo
        SwingUtilities.invokeLater(() -> {
            LafManager.install();

            final JFrame frame = new JFrame();
            Icon folderIcon = IconLoader.get().getUIAwareIcon("files/folder.svg", 19, 19);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            JTabFrame tabFrame = new JTabFrame();
            for (Alignment o : Alignment.values()) {
                if (o != Alignment.CENTER) {
                    for (int i = 0; i < 2; i++) {
                        JPanel pcc = new JPanel();
                        pcc.setOpaque(true);
                        pcc.add(new JLabel(o.toString() + "_" + i + " Popup"));
                        tabFrame.addTab(pcc, o.toString() + "_" + i, folderIcon, o);
                    }
                }
            }
            TabbedPopup tabbedPopup = new TabbedPopup("Tabbed Popup:");
            tabFrame.setTabAt(tabbedPopup, "NORTH (Tabbed Pane Tab)", null, Alignment.NORTH, 0);
            for (int i = 0; i < 5; i++) {
                JPanel panel = new JPanel();
                JLabel label = new JLabel("Tab Number " + i);
                panel.add(label);
                tabbedPopup.getTabbedPane().addTab("Tab " + i, panel);
            }
            tabFrame.setComponentAt(new JScrollPane(createTree()), Alignment.NORTH_WEST, 0);
            /* Activate for custom tab demo.
            tabFrame.setUserTabComponentAt(new JLabel("NORTH (custom tab)") {{
                setBorder(new EmptyBorder(0, 5, 0, 5));
                setOpaque(false);
                setForeground(Color.RED);
                setFont(new Font(Font.SERIF, Font.ITALIC, 12));
            }}, Alignment.NORTH, 1);
             */
            tabFrame.setAcceleratorAt(1, Alignment.NORTH_WEST, 0);

            JPanel contentPane = new JPanel(new BorderLayout());
            JPanel topPanel = new JPanel(new GridBagLayout());
            topPanel.add(new JButton("I do nothing!"), null);
            topPanel.setBorder(DarkBorders.createLineBorder(0, 0, 1, 0));

            contentPane.add(topPanel, BorderLayout.NORTH);
            contentPane.add(tabFrame, BorderLayout.CENTER);

            frame.setContentPane(contentPane);
            tabFrame.setContent(createTextArea());

            frame.pack();
            frame.setSize(1000, 500);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    @NotNull
    protected static JTree createTree() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("States");
        DefaultMutableTreeNode parent1 = new DefaultMutableTreeNode("Andhra Pradesh");
        DefaultMutableTreeNode child = new DefaultMutableTreeNode("Vijayawada");
        DefaultMutableTreeNode child1 = new SelectableTreeNode("This node can be selected", true);
        DefaultMutableTreeNode parent2 = new DefaultMutableTreeNode("Telangana");
        DefaultMutableTreeNode child2 = new DefaultMutableTreeNode("Hyderabad");

        // Adding child nodes to parent
        parent1.add(child);
        parent1.add(child1);
        parent2.add(child2);

        // Adding parent nodes to root
        root.add(parent1);
        root.add(parent2);

        // Adding root to JTree
        JTree tree = new JTree(root);
        tree.setEditable(true);
//        tree.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        return tree;
    }

    @NotNull
    private static Component createTextArea() {
        NumberedTextComponent numberPane = new NumberedTextComponent(new NonWrappingTextPane() {{
            setText(StringUtil.repeat(DemoResources.LOREM_IPSUM, 10));
            setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        }});
        NumberingPane numbering = numberPane.getNumberingPane();
        Icon icon = IconLoader.get().getIcon("navigation/arrowRight.svg");
        try {
            numbering.addIconAtLine(5, icon);
            numbering.addIconAtLine(10, icon);
            numbering.addIconAtLine(15, icon);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        return numberPane;
    }
}
import com.weis.darklaf.LafManager;
import com.weis.darklaf.components.TextFieldHistory;
import com.weis.darklaf.icons.IconLoader;
import org.jdesktop.swingx.JXStatusBar;

import javax.swing.*;
import java.awt.*;

/**
 * @author Jannis Weis
 * @since 2018
 */
public final class UIDemo {

    private static final int SIZE = 10;

    public static void main(final String[] args) {
        System.setProperty("org.apache.batik.warn_destination", "false");
        SwingUtilities.invokeLater(
                () -> {
                    LafManager.loadLaf(LafManager.Theme.Dark);
                    JFrame.setDefaultLookAndFeelDecorated(true);

                    JFrame frame = new JFrame("UIDemo");
                    Icon folderIcon = IconLoader.get().getUIAwareIcon("files/folder.svg");

                    var panel = new JPanel(new GridLayout(2, 5));
                    var content = new JPanel(new BorderLayout());
                    content.add(panel, BorderLayout.CENTER);
                    var statusBar = new JXStatusBar();
                    statusBar.add(new JLabel("test1"));
                    statusBar.add(new JLabel("test2"));
                    statusBar.add(new JLabel("test3"));
                    content.add(statusBar, BorderLayout.SOUTH);

                    var defaultButton = new JButton("default") {{
                        setDefaultCapable(true);
                    }};

                    panel.add(new JPanel() {{
                        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                        add(new JCheckBox("checkBox"));
                        add(new JRadioButton("radioButton"));
                        add(new JPanel(new FlowLayout(FlowLayout.LEFT)) {{
                            add(new JButton("IconButton", folderIcon) {{
                                setRolloverEnabled(true);
                                putClientProperty("JButton.variant", "shadow");
                            }});
                            add(new JButton(folderIcon) {{
                                setRolloverEnabled(true);
                                putClientProperty("JButton.buttonType", "square");
                                putClientProperty("JButton.variant", "shadow");
                            }});
                            add(new JButton(folderIcon) {{
                                setRolloverEnabled(true);
                                putClientProperty("JButton.buttonType", "square");
                                putClientProperty("JButton.thin", Boolean.TRUE);
                                putClientProperty("JButton.forceRoundCorner", Boolean.TRUE);
                                putClientProperty("JButton.variant", "shadow");
                            }});
                            add(new JButton(folderIcon) {{
                                putClientProperty("JButton.variant", "onlyLabel");
                            }});
                        }});
                        add(new JToggleButton("toggle") {{
                            putClientProperty("ToggleButton.variant", "slider");
                            setEnabled(false);
                            setSelected(true);
                        }});
                    }});
                    panel.add(new JPanel() {{
                        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                        add(new JComboBox<String>() {{
                            addItem("Editable ComboBox");
                            for (int i = 0; i < 20; i++) {
                                addItem("item " + i);
                            }
                            setEditable(true);
                        }});
                        add(new JComboBox<String>() {{
                            addItem("Uneditable ComboBox");
                            for (int i = 0; i < 20; i++) {
                                addItem("item " + i);
                            }
                            setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                        }});
                        add(new JComboBox<String>() {{
                            addItem("DisabledComboBox");
                            setEnabled(false);
                        }});
                        add(new JSpinner());
                        add(new JSpinner() {{
                            setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                        }});
                        add(new JSpinner() {{
                            setEnabled(false);
                        }});
                    }});
                    panel.add(new JPanel() {{
                        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                        add(new JTextField("TextField"));
                        add(new JTextField("TextField") {{
                            setEnabled(false);
                        }});
                        add(new JTextField("TextField") {{
                            putClientProperty("JTextField.alternativeArc", Boolean.TRUE);
                        }});
                        add(new JTextField("SearchField") {{
                            putClientProperty("JTextField.variant", "search");
                        }});
                        add(new JTextField("SearchFieldWithHistory") {{
                            putClientProperty("JTextField.variant", "search");
                            putClientProperty("JTextField.Search.FindPopup",
                                              new TextFieldHistory(this, 50, 100));
                        }});
                        add(new JPasswordField("Password"));
                        add(new JPasswordField("VeryStrongPassword") {{
                            putClientProperty("JTextField.alternativeArc", Boolean.TRUE);
                            putClientProperty("PasswordField.view", Boolean.TRUE);
                        }});
                    }});
                    panel.add(new JPanel() {{
                        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                        add(new JButton("enabled") {{
                        }});
                        add(new JButton("disabled") {{
                            setEnabled(false);
                        }});
                        add(defaultButton);
                        add(new JToggleButton("toggle") {{
                            putClientProperty("ToggleButton.variant", "slider");
                        }});
                        add(new JButton("square") {{
                            putClientProperty("JButton.buttonType", "square");
                        }});
                    }});
                    panel.add(new JPanel());
                    panel.add(new JPanel());
                    panel.add(new JPanel() {{
                        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                        add(new JSlider());
                        add(new JSlider() {{
                            setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                        }});
                        add(new JSlider() {{
                            setInverted(true);
                        }});
                        add(new JSlider() {{
                            setInverted(true);
                            setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                        }});
                        add(new JSlider() {{
                            putClientProperty("Slider.variant", "volume");
                            putClientProperty("Slider.instantScrollEnabled", Boolean.TRUE);
                        }});
                        add(new JSlider() {{
                            putClientProperty("Slider.variant", "volume");
                            putClientProperty("Slider.instantScrollEnabled", Boolean.TRUE);
                            putClientProperty("Slider.volume.showIcon", Boolean.TRUE);
                        }});
                        add(new JSlider() {{
                            setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                            putClientProperty("Slider.variant", "volume");
                            putClientProperty("Slider.instantScrollEnabled", Boolean.TRUE);
                            putClientProperty("Slider.volume.showIcon", Boolean.TRUE);
                        }});
                    }});
                    panel.add(new JPanel() {{
                        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                        add(new JSlider() {{
                            setSnapToTicks(true);
                            setPaintTicks(true);
                            setMajorTickSpacing(20);
                            setMinorTickSpacing(5);
                            setPaintLabels(true);
                        }});
                        add(new JSlider() {{
                            setSnapToTicks(true);
                            setPaintTicks(true);
                            setMajorTickSpacing(20);
                            setMinorTickSpacing(5);
                            setPaintLabels(true);
                            setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                        }});
                        add(new JSlider() {{
                            setSnapToTicks(true);
                            setPaintTicks(true);
                            setMajorTickSpacing(20);
                            setMinorTickSpacing(5);
                            setPaintLabels(true);
                            setInverted(true);
                        }});
                        add(new JSlider() {{
                            setSnapToTicks(true);
                            setPaintTicks(true);
                            setMajorTickSpacing(20);
                            setMinorTickSpacing(5);
                            setPaintLabels(true);
                            setInverted(true);
                            setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                        }});
                    }});
                    panel.add(new JPanel() {{
                        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
                        add(new JSlider() {{
                            setSnapToTicks(true);
                            setPaintTicks(true);
                            setMajorTickSpacing(20);
                            setMinorTickSpacing(5);
                            setPaintLabels(true);
                            putClientProperty("Slider.variant", "volume");
                            putClientProperty("Slider.instantScrollEnabled", Boolean.TRUE);
                        }});
                        add(new JSlider() {{
                            setSnapToTicks(true);
                            setPaintTicks(true);
                            setMajorTickSpacing(20);
                            setMinorTickSpacing(5);
                            setPaintLabels(true);
                            putClientProperty("Slider.variant", "volume");
                            putClientProperty("Slider.instantScrollEnabled", Boolean.TRUE);
                            putClientProperty("Slider.volume.showIcon", Boolean.TRUE);
                        }});
                        add(new JSlider() {{
                            setSnapToTicks(true);
                            setPaintTicks(true);
                            setMajorTickSpacing(20);
                            setMinorTickSpacing(5);
                            setPaintLabels(true);
                            setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                            putClientProperty("Slider.variant", "volume");
                            putClientProperty("Slider.instantScrollEnabled", Boolean.TRUE);
                            putClientProperty("Slider.volume.showIcon", Boolean.TRUE);
                        }});
                    }});
                    panel.add(new JPanel() {{
                        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
                        add(new JSlider() {{
                            setOrientation(VERTICAL);
                        }});
                        add(new JSlider() {{
                            setOrientation(VERTICAL);
                            setInverted(true);
                        }});
                        add(new JSlider() {{
                            setOrientation(VERTICAL);
                            putClientProperty("Slider.variant", "volume");
                            putClientProperty("Slider.instantScrollEnabled", Boolean.TRUE);
                        }});
                        add(new JSlider() {{
                            setOrientation(VERTICAL);
                            putClientProperty("Slider.variant", "volume");
                            putClientProperty("Slider.instantScrollEnabled", Boolean.TRUE);
                            putClientProperty("Slider.volume.showIcon", Boolean.TRUE);
                        }});
                        add(new JSlider() {{
                            setOrientation(VERTICAL);
                            setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                            putClientProperty("Slider.variant", "volume");
                            putClientProperty("Slider.instantScrollEnabled", Boolean.TRUE);
                            putClientProperty("Slider.volume.showIcon", Boolean.TRUE);
                        }});
                    }});
                    panel.add(new JPanel() {{
                        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
                        add(new JSlider() {{
                            setOrientation(VERTICAL);
                            setSnapToTicks(true);
                            setPaintTicks(true);
                            setMajorTickSpacing(20);
                            setMinorTickSpacing(5);
                            setPaintLabels(true);
                        }});
                        add(new JSlider() {{
                            setOrientation(VERTICAL);
                            setSnapToTicks(true);
                            setPaintTicks(true);
                            setMajorTickSpacing(20);
                            setMinorTickSpacing(5);
                            setPaintLabels(true);
                            setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                        }});
                        add(new JSlider() {{
                            setOrientation(VERTICAL);
                            setSnapToTicks(true);
                            setPaintTicks(true);
                            setMajorTickSpacing(20);
                            setMinorTickSpacing(5);
                            setPaintLabels(true);
                            setInverted(true);
                        }});
                        add(new JSlider() {{
                            setOrientation(VERTICAL);
                            setSnapToTicks(true);
                            setPaintTicks(true);
                            setMajorTickSpacing(20);
                            setMinorTickSpacing(5);
                            setPaintLabels(true);
                            setInverted(true);
                            setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                        }});
                    }});
                    panel.add(new JPanel() {{
                        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
                        add(new JSlider() {{
                            setOrientation(VERTICAL);
                            setSnapToTicks(true);
                            setPaintTicks(true);
                            setMajorTickSpacing(20);
                            setMinorTickSpacing(5);
                            setPaintLabels(true);
                            putClientProperty("Slider.variant", "volume");
                            putClientProperty("Slider.instantScrollEnabled", Boolean.TRUE);
                        }});
                        add(new JSlider() {{
                            setOrientation(VERTICAL);
                            setSnapToTicks(true);
                            setPaintTicks(true);
                            setMajorTickSpacing(20);
                            setMinorTickSpacing(5);
                            setPaintLabels(true);
                            putClientProperty("Slider.variant", "volume");
                            putClientProperty("Slider.instantScrollEnabled", Boolean.TRUE);
                            putClientProperty("Slider.volume.showIcon", Boolean.TRUE);
                        }});
                        add(new JSlider() {{
                            setOrientation(VERTICAL);
                            setSnapToTicks(true);
                            setPaintTicks(true);
                            setMajorTickSpacing(20);
                            setMinorTickSpacing(5);
                            setPaintLabels(true);
                            setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                            putClientProperty("Slider.variant", "volume");
                            putClientProperty("Slider.instantScrollEnabled", Boolean.TRUE);
                            putClientProperty("Slider.volume.showIcon", Boolean.TRUE);
                        }});
                    }});

                    frame.setContentPane(content);

                    var menuBar = new JMenuBar();
                    var menu = new JMenu("test");
                    menu.add(new JMenuItem("item"));
                    menu.addSeparator();
                    menu.add(new JRadioButtonMenuItem("radioButton"));
                    menu.add(new JCheckBoxMenuItem("checkBox"));
                    menuBar.add(menu);
                    frame.setJMenuBar(menuBar);

                    frame.setMinimumSize(new Dimension(100, 100));
                    frame.getRootPane().setDefaultButton(defaultButton);
                    frame.pack();
                    frame.setLocationRelativeTo(null);
                    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                    frame.setVisible(true);
                });
    }
}
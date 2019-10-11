package com.weis.darklaf.ui.toolbar;

import com.weis.darklaf.decorators.MouseResponder;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @author Jannis Weis
 */
public class DarkToolBarUI extends DarkToolBarUIBridge {

    private static final Robot robot = createRobot();

    private final DropPreviewPanel previewPanel = new DropPreviewPanel();
    private Dimension verticalDim = new Dimension(0, 0);
    private Dimension horizontalDim = new Dimension(0, 0);
    private Timer timer = new Timer(5, e -> dragTo());

    @NotNull
    @Contract(value = "_ -> new", pure = true)
    public static ComponentUI createUI(final JComponent c) {
        return new DarkToolBarUI();
    }

    @Nullable
    private static Robot createRobot() {
        try {
            return new Robot();
        } catch (AWTException e) {
            return null;
        }
    }

    @Override
    public void installUI(final JComponent c) {
        super.installUI(c);
        previewPanel.setToolBar(toolBar);
        dragWindow = createDragWindow(toolBar);
        floatingToolBar = createFloatingWindow(toolBar);
    }

    @Override
    protected void installListeners() {
        super.installListeners();
    }

    @Override
    protected void uninstallListeners() {
        super.uninstallListeners();
    }

    @Override
    protected DragWindow createDragWindow(final JToolBar toolbar) {
        Window frame = null;
        if (toolBar != null) {
            Container p = toolbar.getParent();
            while (p != null && !(p instanceof Window)) {
                p = p.getParent();
            }
            if (p != null) {
                frame = (Window) p;
            }
        }
        if (floatingToolBar instanceof Window) { frame = (Window) floatingToolBar; }
        return new DarkDragWindow(frame);
    }

    protected void setBorderToNonRollover(final Component c) {
    }

    @Override
    public void setFloating(final boolean b, final Point p) {
        if (toolBar.isFloatable()) {
            boolean visible = false;

            Window ancestor = SwingUtilities.getWindowAncestor(toolBar);
            if (ancestor != null) {
                visible = ancestor.isVisible();
            }

            if (dragWindow != null) {
                stopDrag();
            }

            floating = b;

            if (b) {
                constraintBeforeFloating = calculateConstraint();
                if (propertyListener != null) {
                    UIManager.addPropertyChangeListener(propertyListener);
                }

                floatingToolBar.getContentPane().add(toolBar, BorderLayout.CENTER);
                if (floatingToolBar instanceof Window) {
                    ((Window) floatingToolBar).pack();
                    ((Window) floatingToolBar).setLocation(floatingX, floatingY);
                    if (visible) {
                        ((Window) floatingToolBar).setVisible(true);
                    } else {
                        if (ancestor != null) {
                            ancestor.addWindowListener(new WindowAdapter() {
                                public void windowOpened(final WindowEvent e) {
                                    ((Window) floatingToolBar).setVisible(true);
                                }
                            });
                        }
                    }
                }
            } else {
                if (floatingToolBar instanceof Window) {
                    ((Window) floatingToolBar).setVisible(false);
                }
                floatingToolBar.getContentPane().remove(toolBar);

                String constraint = getDockingConstraint(dockingSource, p);
                if (constraint == null) {
                    constraint = BorderLayout.NORTH;
                }
                setOrientation(mapConstraintToOrientation(constraint));
                dockingSource.add(toolBar, constraint);
                updateDockingSource();
            }
        }
    }

    @Override
    protected boolean isBlocked(final Component comp, final Object constraint) {
        if (comp instanceof Container) {
            Container cont = (Container) comp;
            LayoutManager lm = cont.getLayout();
            if (lm instanceof BorderLayout) {
                BorderLayout blm = (BorderLayout) lm;
                Component c = blm.getLayoutComponent(cont, constraint);
                return (c != null && c != toolBar && c != previewPanel);
            }
        }
        return false;
    }

    protected String getDockingConstraint(final Component c, final Point p) {
        if (p == null) return constraintBeforeFloating;
        if (c.contains(p)) {
            //North
            if (p.y < horizontalDim.height && !isBlocked(c, BorderLayout.NORTH)) {
                return BorderLayout.NORTH;
            }
            //South
            if (p.y >= c.getHeight() - horizontalDim.height && !isBlocked(c, BorderLayout.SOUTH)) {
                return BorderLayout.SOUTH;
            }
            // East
            if (p.x >= c.getWidth() - verticalDim.width && !isBlocked(c, BorderLayout.EAST)) {
                return BorderLayout.EAST;
            }
            // West
            if (p.x < verticalDim.width && !isBlocked(c, BorderLayout.WEST)) {
                return BorderLayout.WEST;
            }
        }
        return null;
    }

    @Override
    protected void dragTo() {
        if (toolBar.isFloatable()) {
            Point offset = dragWindow.getOffset();
            Point global = MouseInfo.getPointerInfo().getLocation();
            Point dragPoint = new Point(global.x - offset.x, global.y - offset.y);
            ensureDockingSource();

            Point dockingPosition = dockingSource.getLocationOnScreen();
            Point comparisonPoint = new Point(global.x - dockingPosition.x, global.y - dockingPosition.y);

            if (canDock(dockingSource, comparisonPoint)) {
                String constraint = getDockingConstraint(dockingSource, comparisonPoint);
                setOrientation(mapConstraintToOrientation(constraint));
                dockingSource.add(previewPanel, constraint);
            } else {
                setOrientation(mapConstraintToOrientation(constraintBeforeFloating));
                dockingSource.remove(previewPanel);
            }
            updateDockingSource();

            dragWindow.setLocation(dragPoint.x, dragPoint.y);
            startDrag();
        }
    }

    private void ensureDockingSource() {
        if (dockingSource == null) {
            dockingSource = toolBar.getParent();
        }
    }

    protected void updateDockingSource() {
        dockingSource.invalidate();
        Container dockingSourceParent = dockingSource.getParent();
        if (dockingSourceParent != null) { dockingSourceParent.validate(); }
        dockingSource.repaint();
    }

    protected void startDrag() {
        if (!dragWindow.isVisible()) {
            dragWindow.getContentPane().add(toolBar);
            updateDockingSource();
            dragWindow.setVisible(true);
            //Is needed to intercept ongoing drag.
            SwingUtilities.invokeLater(() -> robot.mouseRelease(MouseEvent.BUTTON1_DOWN_MASK));

            var oldOrientation = toolBar.getOrientation();
            toolBar.setOrientation(SwingConstants.VERTICAL);
            verticalDim = toolBar.getPreferredSize();
            toolBar.setOrientation(SwingConstants.HORIZONTAL);
            horizontalDim = toolBar.getPreferredSize();
            toolBar.setOrientation(oldOrientation);
            timer.start();
        }
    }

    @Override
    protected void floatAt() {
        if (toolBar.isFloatable()) {
            try {
                Point offset = dragWindow.getOffset();
                Point global = MouseInfo.getPointerInfo().getLocation();
                setFloatingLocation(global.x - offset.x, global.y - offset.y);

                if (dockingSource != null) {
                    Point dockingPosition = dockingSource.getLocationOnScreen();
                    Point comparisonPoint = new Point(global.x - dockingPosition.x,
                                                      global.y - dockingPosition.y);
                    if (canDock(dockingSource, comparisonPoint)) {
                        setFloating(false, comparisonPoint);
                    } else {
                        setFloating(true, null);
                    }
                } else {
                    setFloating(true, null);
                }
                dockingSource.remove(previewPanel);
            } catch (IllegalComponentStateException ignored) {
            }
        }
    }

    protected void stopDrag() {
        dragWindow.setVisible(false);
        timer.stop();
    }

    @Override
    protected void paintDragWindow(@NotNull final Graphics g) {
        g.setColor(dragWindow.getBackground());
        int w = dragWindow.getWidth();
        int h = dragWindow.getHeight();
        g.fillRect(0, 0, w, h);
        g.setColor(dragWindow.getBorderColor());
        g.fillRect(0, 0, w, 1);
        g.fillRect(0, 0, 1, h);
        g.fillRect(w - 1, 0, 1, h);
        g.fillRect(0, h - 1, w, 1);
    }

    public void paint(@NotNull final Graphics g, @NotNull final JComponent c) {
        g.setColor(UIManager.getColor("ToolBar.background"));
        g.fillRect(0, 0, c.getWidth(), c.getHeight());
    }

    protected class DarkDragWindow extends DragWindow {

        protected DarkDragWindow(final Window w) {
            super(w);
            setLayout(new BorderLayout());
            setBackground(toolBar.getBackground());
            var glassPane = new JPanel();
            glassPane.setOpaque(false);
            glassPane.addMouseListener(new MouseResponder(e -> {
                e.consume();
                if (e.getID() == MouseEvent.MOUSE_RELEASED) {
                    floatAt();
                }
            }));
            setGlassPane(glassPane);
            glassPane.setVisible(true);
        }

        @Override
        public void setOrientation(final int o) {
            super.setOrientation(o);
            var size = toolBar.getPreferredSize();
            size.width += 2;
            size.height += 2;
            setSize(size);
            doLayout();
        }

        @Override
        public Point getOffset() {
            return new Point(getWidth() / 2, getHeight() / 2);
        }
    }
}
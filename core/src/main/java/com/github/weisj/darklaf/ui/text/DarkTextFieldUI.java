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
 *
 */
package com.github.weisj.darklaf.ui.text;

import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.JTextComponent;

import com.github.weisj.darklaf.graphics.GraphicsContext;
import com.github.weisj.darklaf.listener.MouseClickListener;
import com.github.weisj.darklaf.listener.MouseMovementListener;
import com.github.weisj.darklaf.listener.PopupMenuAdapter;
import com.github.weisj.darklaf.ui.text.bridge.DarkTextFieldUIBridge;
import com.github.weisj.darklaf.util.DarkUIUtil;
import com.github.weisj.darklaf.util.PropertyUtil;

/**
 * @author Jannis Weis
 */
public class DarkTextFieldUI extends DarkTextFieldUIBridge implements PropertyChangeListener, MouseClickListener {

    protected static final String KEY_PREFIX = "JTextField.";
    public static final String KEY_VARIANT = KEY_PREFIX + "variant";
    public static final String KEY_SHOW_CLEAR = KEY_PREFIX + "showClear";
    public static final String KEY_FIND_POPUP = KEY_PREFIX + "Search.FindPopup";
    public static final String VARIANT_SEARCH = "search";

    protected Icon clear;
    protected Icon clearHover;
    protected Icon search;
    protected Icon searchDisabled;
    protected Icon searchWithHistory;
    protected Icon searchWithHistoryDisabled;
    protected int arcSize;
    protected int searchArcSize;
    protected Color background;
    protected Color inactiveBackground;
    private long lastSearchEvent;
    protected Insets padding;

    private final PopupMenuListener searchPopupListener = new PopupMenuAdapter() {
        @Override
        public void popupMenuWillBecomeInvisible(final PopupMenuEvent e) {
            lastSearchEvent = System.currentTimeMillis();
        }
    };
    private boolean clearHovered;
    private final MouseMotionListener mouseMotionListener = (MouseMovementListener) e -> updateCursor(e.getPoint());
    private final KeyListener keyListener = new KeyAdapter() {
        @Override
        public void keyTyped(final KeyEvent e) {
            SwingUtilities.invokeLater(() -> {
                Point p = MouseInfo.getPointerInfo().getLocation();
                SwingUtilities.convertPointFromScreen(p, getComponent());
                updateCursor(p);
            });
        }
    };

    public static ComponentUI createUI(final JComponent c) {
        return new DarkTextFieldUI();
    }

    protected Icon getRightIcon(final JTextComponent c) {
        return getClearIcon(clearHovered || !c.isEditable() || !c.isEnabled());
    }

    protected Icon getClearIcon(final boolean clearHovered) {
        return clearHovered ? clearHover : clear;
    }

    protected Icon getLeftIcon(final JTextComponent c) {
        return getSearchIcon(c);
    }

    protected Icon getSearchIcon(final JTextComponent c) {
        boolean enabled = c.isEnabled();
        return isSearchFieldWithHistoryPopup(c)
                ? enabled ? searchWithHistory : searchWithHistoryDisabled
                : enabled ? search : searchDisabled;
    }

    @Override
    public Dimension getPreferredSize(final JComponent c) {
        return addIconSizes(addPadding(c, super.getPreferredSize(c)));
    }

    @Override
    public Dimension getMinimumSize(final JComponent c) {
        return addIconSizes(addPadding(c, super.getMinimumSize(c)));
    }

    protected Dimension addIconSizes(final Dimension dim) {
        JTextComponent comp = getComponent();
        boolean ltr = comp.getComponentOrientation().isLeftToRight();
        int leftPad = ltr ? padding.left : padding.right;
        int rightPad = ltr ? padding.right : padding.left;
        if (doPaintLeftIcon(comp)) {
            Icon left = getLeftIcon(comp);
            dim.width += left.getIconWidth() + leftPad;
            dim.height = Math.max(dim.height, left.getIconHeight());
        }
        if (doPaintRightIcon(comp)) {
            Icon right = getRightIcon(comp);
            dim.width += right.getIconWidth() + rightPad;
            dim.height = Math.max(dim.height, right.getIconHeight());
        }
        return dim;
    }

    protected Dimension addPadding(final JComponent c, final Dimension dim) {
        if (!isInCell(c)) {
            dim.width += padding.left + padding.right;
            dim.height += padding.top + padding.bottom;
        }
        return dim;
    }

    @Override
    protected Rectangle getVisibleEditorRect() {
        Rectangle rect = super.getVisibleEditorRect();
        adjustTextRect(getComponent(), rect);
        return rect;
    }

    protected void adjustTextRect(final JTextComponent c, final Rectangle r) {
        boolean ltr = c.getComponentOrientation().isLeftToRight();
        if (!isInCell(c) && getBorder(c) != null) DarkUIUtil.applyInsets(r, padding);
        if (doPaintLeftIcon(c)) {
            int w = getLeftIcon(c).getIconWidth() + padding.left;
            if (ltr) r.x += w;
            r.width -= w;
        }
        if (doPaintRightIcon(c)) {
            int w = getRightIcon(c).getIconWidth() + padding.right;
            if (!ltr) r.x += w;
            r.width -= w;
        }
    }

    public static boolean isOver(final Point p, final Icon icon, final Point e) {
        return new Rectangle(p.x, p.y,
                             icon.getIconWidth(), icon.getIconHeight()).contains(e);
    }

    protected void updateCursor(final Point p) {
        ClickAction action = getActionUnder(p);
        boolean oldClear = clearHovered;
        clearHovered = action == ClickAction.RIGHT_ACTION;
        if (oldClear != clearHovered) {
            editor.repaint();
        }
        Rectangle textRect = getVisibleEditorRect();
        if (textRect.contains(p)) {
            getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.TEXT_CURSOR));
        } else {
            Cursor cursor = action == ClickAction.NONE
                    ? Cursor.getDefaultCursor()
                    : Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
            getComponent().setCursor(cursor);
        }
    }

    protected ClickAction getActionUnder(final Point p) {
        JTextComponent c = getComponent();
        if (!c.isEnabled()) return ClickAction.NONE;
        if (isOver(getRightIconCoord(), getRightIcon(c), p) && doPaintRightIcon(c)) {
            return ClickAction.RIGHT_ACTION;
        }
        if (isOver(getLeftIconCoord(), getLeftIcon(c), p) && doPaintLeftIcon(c)) {
            return ClickAction.LEFT_ACTION;
        }
        return ClickAction.NONE;
    }

    private static JPopupMenu getSearchPopup(final JComponent c) {
        return PropertyUtil.getObject(c, KEY_FIND_POPUP, JPopupMenu.class);
    }

    public static boolean isSearchFieldWithHistoryPopup(final JTextComponent c) {
        return isSearchField(c) && getSearchPopup(c) != null;
    }

    public static boolean isSearchField(final Component c) {
        return PropertyUtil.isPropertyEqual(c, KEY_VARIANT, VARIANT_SEARCH);
    }

    public static boolean showClearIcon(final JComponent c) {
        return PropertyUtil.getBooleanProperty(c, KEY_SHOW_CLEAR);
    }

    @Override
    protected void paintSafely(final Graphics g) {
        GraphicsContext context = new GraphicsContext(g);
        super.paintSafely(g);
        context.restore();
        paintIcons(g);
    }

    protected void paintIcons(final Graphics g) {
        JTextComponent c = getComponent();
        if (doPaintLeftIcon(c)) paintLeftIcon(g);
        if (doPaintRightIcon(c)) paintRightIcon(g);
    }

    protected boolean doPaintLeftIcon(final JTextComponent c) {
        return isSearchField(c);
    }

    protected boolean doPaintRightIcon(final JTextComponent c) {
        return c.isEditable() && c.getText().length() > 0 && showClearIcon(c);
    }

    protected void paintRightIcon(final Graphics g) {
        Point p = getRightIconCoord();
        getRightIcon(editor).paintIcon(null, g, p.x, p.y);
    }

    protected void paintLeftIcon(final Graphics g) {
        Point p = getLeftIconCoord();
        getLeftIcon(editor).paintIcon(null, g, p.x, p.y);
    }

    @Override
    public Rectangle getDrawingRect(final JTextComponent c) {
        return DarkUIUtil.applyInsets(new Rectangle(0, 0, c.getWidth(), c.getHeight()), c.getInsets());
    }

    @Override
    protected int getArcSize(final JComponent c) {
        return DarkTextFieldUI.isSearchField(c) ? searchArcSize : arcSize;
    }

    protected Point getLeftIconCoord() {
        Rectangle r = getDrawingRect(getComponent());
        int w = getLeftIcon(getComponent()).getIconWidth();
        int left = getComponent().getInsets().left + padding.left;
        return DarkUIUtil.adjustForOrientation(new Point(r.x + left, r.y + (r.height - w) / 2),
                                               w, editor);
    }

    protected Point getRightIconCoord() {
        Rectangle r = getDrawingRect(getComponent());
        int w = getRightIcon(getComponent()).getIconWidth();
        int right = getComponent().getInsets().right + padding.right;
        return DarkUIUtil.adjustForOrientation(new Point(r.x + r.width - w - right, r.y + (r.height - w) / 2),
                                               w, editor);
    }

    protected void showSearchPopup() {
        if (lastSearchEvent == 0 || (System.currentTimeMillis() - lastSearchEvent) > 250) {
            JPopupMenu menu = getSearchPopup(getComponent());
            if (menu != null) {
                menu.show(getComponent(), getLeftIconCoord().x, getComponent().getHeight());
            }
        }
    }

    @Override
    protected DarkCaret.CaretStyle getDefaultCaretStyle() {
        return DarkCaret.CaretStyle.VERTICAL_LINE_STYLE;
    }

    @Override
    protected void installDefaults() {
        super.installDefaults();
        arcSize = UIManager.getInt("TextField.arc");
        searchArcSize = UIManager.getInt("TextField.searchArc");
        background = UIManager.getColor("TextField.background");
        inactiveBackground = UIManager.getColor("TextField.disabledBackground");
        clearHover = UIManager.getIcon("TextField.search.clearHover.icon");
        clear = UIManager.getIcon("TextField.search.clear.icon");
        searchWithHistory = UIManager.getIcon("TextField.search.searchWithHistory.icon");
        searchWithHistoryDisabled = UIManager.getIcon("TextField.search.searchWithHistory.disabled.icon");
        search = UIManager.getIcon("TextField.search.search.icon");
        searchDisabled = UIManager.getIcon("TextField.search.search.disabled.icon");
        padding = UIManager.getInsets("TextField.insets");
        if (padding == null) padding = new Insets(0, 0, 0, 0);

    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        super.propertyChange(evt);
        String key = evt.getPropertyName();
        if (KEY_FIND_POPUP.equals(key)) {
            Object oldVal = evt.getOldValue();
            Object newVal = evt.getNewValue();
            if (oldVal instanceof JPopupMenu) {
                ((JPopupMenu) oldVal).removePopupMenuListener(searchPopupListener);
            }
            if (newVal instanceof JPopupMenu) {
                ((JPopupMenu) newVal).addPopupMenuListener(searchPopupListener);
            }
        } else if (KEY_VARIANT.equals(key)) {
            editor.putClientProperty(KEY_SHOW_CLEAR, isSearchField(editor));
            layoutChanged();
        } else if (KEY_SHOW_CLEAR.equals(key)) {
            layoutChanged();
        }
    }

    protected void layoutChanged() {
        Component parent = editor.getParent();
        if (parent instanceof JComponent) {
            parent.doLayout();
        }
        editor.repaint();
    }

    @Override
    protected void installListeners() {
        super.installListeners();
        JTextComponent c = getComponent();
        c.addMouseListener(this);
        c.addMouseMotionListener(mouseMotionListener);
        c.addKeyListener(keyListener);
    }

    @Override
    protected void uninstallListeners() {
        JTextComponent c = getComponent();
        c.removeMouseListener(this);
        c.removeMouseMotionListener(mouseMotionListener);
        c.removeKeyListener(keyListener);
    }

    @Override
    public void mouseClicked(final MouseEvent e) {
        ClickAction actionUnder = getActionUnder(e.getPoint());
        if (actionUnder == ClickAction.RIGHT_ACTION) {
            rightActionClicked();
        } else if (actionUnder == ClickAction.LEFT_ACTION) {
            leftActionClicked();
        }
    }

    protected void leftActionClicked() {
        showSearchPopup();
    }

    protected void rightActionClicked() {
        getComponent().setText("");
    }

    protected enum ClickAction {
        RIGHT_ACTION,
        LEFT_ACTION,
        NONE
    }
}

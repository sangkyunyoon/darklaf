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
package com.github.weisj.darklaf.ui.scrollpane;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;

import javax.swing.*;
import javax.swing.plaf.ComponentUI;

import com.github.weisj.darklaf.graphics.GraphicsContext;
import com.github.weisj.darklaf.graphics.GraphicsUtil;

public class DarkMacScrollBarUI extends DarkScrollBarUI {

    public static ComponentUI createUI(final JComponent c) {
        return new DarkMacScrollBarUI();
    }

    @Override
    protected void paintTrack(final Graphics g, final JComponent c, final Rectangle bounds) {}

    @Override
    protected void paintMaxiThumb(final Graphics2D g, final Rectangle rect) {
        GraphicsContext context = GraphicsUtil.setupStrokePainting(g);
        g.setComposite(COMPOSITE.derive(thumbAlpha));
        boolean horizontal = scrollbar.getOrientation() == JScrollBar.HORIZONTAL;
        int ins = 2;
        int arc = horizontal ? (rect.height - 2 * ins) : (rect.width - 2 * ins);
        RoundRectangle2D roundRect = new RoundRectangle2D.Float();
        roundRect.setRoundRect(rect.x + ins, rect.y + ins,
                               rect.width - 2 * ins, rect.height - 2 * ins, arc, arc);
        g.setColor(getThumbColor());
        g.fill(roundRect);
        g.setColor(getThumbBorderColor());
        g.draw(roundRect);
        context.restore();
    }
}

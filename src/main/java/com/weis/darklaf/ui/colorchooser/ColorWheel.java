package com.weis.darklaf.ui.colorchooser;


import com.weis.darklaf.util.ColorUtil;
import com.weis.darklaf.util.GraphicsContext;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

public class ColorWheel extends JComponent {
    private static final int BORDER_SIZE = 5;
    private final List<ColorListener> myListeners = new ArrayList<>();
    private float myBrightness = 1f;
    private float myHue = 1f;
    private float mySaturation = 0f;
    private Image myImage;
    private Rectangle myWheel;
    private boolean myShouldInvalidate = true;
    private Color myColor;
    private int myOpacity;
    private boolean pressedInside;

    public ColorWheel() {
        setOpaque(true);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent e) {
                myShouldInvalidate = true;
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(final MouseEvent e) {
                if (!pressedInside) return;
                final int x = e.getX();
                final int y = e.getY();
                int mx = myWheel.x + myWheel.width / 2;
                int my = myWheel.y + myWheel.height / 2;
                double s;
                double h;
                s = Math.sqrt((x - mx) * (x - mx) + (y - my) * (y - my)) / (myWheel.height / 2.0);
                h = -Math.atan2(y - my, x - mx) / (2 * Math.PI);
                if (h < 0) h += 1.0;
                if (s > 1) s = 1.0;

                setHSBValue((float) h, (float) s, myBrightness, myOpacity);
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent e) {
                final int x = e.getX();
                final int y = e.getY();
                int mx = myWheel.x + myWheel.width / 2;
                int my = myWheel.y + myWheel.height / 2;
                double s;
                double h;
                s = Math.sqrt((x - mx) * (x - mx) + (y - my) * (y - my)) / (myWheel.height / 2.0);
                h = -Math.atan2(y - my, x - mx) / (2 * Math.PI);
                if (h < 0) h += 1.0;
                if (s <= 1) {
                    pressedInside = true;
                    setHSBValue((float) h, (float) s, myBrightness, myOpacity);
                } else {
                    pressedInside = false;
                }
            }

            @Override
            public void mouseReleased(final MouseEvent e) {
                pressedInside = false;
            }
        });
    }

    private void setHSBValue(final float h, final float s, final float b, final int opacity) {
        Color rgb = new Color(Color.HSBtoRGB(h, s, b));
        setColor(ColorUtil.toAlpha(rgb, opacity), this, h, s, b);
    }

    public void setColor(@NotNull final Color color, final Object source,
                         final float h, final float s, final float b) {
        myColor = color;
        myHue = h;
        mySaturation = s;
        myBrightness = b;
        myOpacity = color.getAlpha();

        fireColorChanged(source);

        repaint();
    }

    public void addListener(final ColorListener listener) {
        myListeners.add(listener);
    }

    private void fireColorChanged(final Object source) {
        for (ColorListener listener : myListeners) {
            listener.colorChanged(myColor, source);
        }
    }

    public void setBrightness(final float brightness) {
        if (brightness != myBrightness) {
            myImage = null;
            setHSBValue(myHue, mySaturation, brightness, myOpacity);
        }
    }

    public void setOpacity(final int opacity) {
        if (opacity != myOpacity) {
            setHSBValue(myHue, mySaturation, myBrightness, opacity);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return getMinimumSize();
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(300, 300);
    }

    @Override
    protected void paintComponent(final Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        final Dimension size = getSize();
        int _size = Math.min(size.width, size.height);
        _size = Math.min(_size, 600);

        if (myImage != null && myShouldInvalidate) {
            if (myImage.getWidth(null) != _size) {
                myImage = null;
            }
        }

        myShouldInvalidate = false;

        if (myImage == null) {
            myImage = createImage(new ColorWheelImageProducer(
                    _size - BORDER_SIZE * 2, _size - BORDER_SIZE * 2, myBrightness));
            myWheel = new Rectangle(BORDER_SIZE, BORDER_SIZE, _size - BORDER_SIZE * 2,
                                    _size - BORDER_SIZE * 2);
        }

        g2d.setColor(UIManager.getColor("Panel.background"));
        g2d.fillRect(0, 0, getWidth(), getHeight());


        var config = new GraphicsContext(g);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, ((float) myOpacity) / 255f));
        g2d.drawImage(myImage, myWheel.x, myWheel.y, null);
        config.restore();

        int mx = myWheel.x + myWheel.width / 2;
        int my = myWheel.y + myWheel.height / 2;
        int arcw = (int) (myWheel.width * mySaturation / 2);
        int arch = (int) (myWheel.height * mySaturation / 2);
        double th = myHue * 2 * Math.PI;
        final int x = (int) (mx + arcw * Math.cos(th));
        final int y = (int) (my - arch * Math.sin(th));

        g2d.setColor(UIManager.getColor("ColorChooser.colorWheelDropBackgroundColor"));
        g2d.fillRect(x - 2, y - 2, 4, 4);
        g2d.setColor(UIManager.getColor("ColorChooser.colorWheelDropBorderColor"));
        g2d.drawRect(x - 2, y - 2, 4, 4);
    }

    public void dropImage() {
        myImage = null;
    }
}

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
package com.github.weisj.darklaf.graphics;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;

import javax.swing.*;
import javax.swing.plaf.basic.BasicHTML;
import javax.swing.text.View;

import sun.swing.SwingUtilities2;

import com.github.weisj.darklaf.util.DarkUIUtil;
import com.github.weisj.darklaf.util.PropertyUtil;
import com.github.weisj.darklaf.util.Scale;
import com.github.weisj.darklaf.util.SystemInfo;

public class PaintUtil {

    public static final Color TRANSPARENT_COLOR = new Color(0x0, true);
    private static AlphaComposite glowComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
    private static AlphaComposite dropComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f);
    private static AlphaComposite shadowComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f);
    private static Color errorGlow;
    private static Color errorFocusGlow;
    private static Color focusGlow;
    private static Color focusInactiveGlow;
    private static Color warningGlow;

    private static final RoundRectangle2D roundRect = new RoundRectangle2D.Double();

    public static void setGlowOpacity(final float alpha) {
        glowComposite = glowComposite.derive(alpha);
    }

    public static void setShadowOpacity(final float alpha) {
        shadowComposite = shadowComposite.derive(alpha);
    }

    public static void setDropOpacity(final float alpha) {
        dropComposite = dropComposite.derive(alpha);
    }

    public static AlphaComposite getDropComposite() {
        return dropComposite;
    }

    public static AlphaComposite getShadowComposite() {
        return shadowComposite;
    }

    public static AlphaComposite getGlowComposite() {
        return glowComposite;
    }

    public static void setErrorGlow(final Color errorGlow) {
        PaintUtil.errorGlow = errorGlow;
    }

    public static void setErrorFocusGlow(final Color errorFocusGlow) {
        PaintUtil.errorFocusGlow = errorFocusGlow;
    }

    public static void setFocusGlow(final Color focusGlow) {
        PaintUtil.focusGlow = focusGlow;
    }

    public static void setFocusInactiveGlow(final Color focusInactiveGlow) {
        PaintUtil.focusInactiveGlow = focusInactiveGlow;
    }

    public static void setWarningGlow(final Color warningGlow) {
        PaintUtil.warningGlow = warningGlow;
    }

    public static Color getErrorGlow() {
        return errorGlow;
    }

    public static Color getErrorFocusGlow() {
        return errorFocusGlow;
    }

    public static Color getFocusGlow() {
        return focusGlow;
    }

    public static Color getFocusInactiveGlow() {
        return focusInactiveGlow;
    }

    public static Color getWarningGlow() {
        return warningGlow;
    }

    private static void doPaint(final Graphics2D g, final float width, final float height, final float arc,
                                final float bw, final boolean inside) {
        GraphicsContext context = GraphicsUtil.setupStrokePainting(g);
        Shape outerRect;
        Shape innerRect;
        if (Scale.equalWithError(arc, 0)) {
            outerRect = new Rectangle2D.Float(0, 0, width, height);
            innerRect = new Rectangle2D.Float(bw, bw, width - 2 * bw, height - 2 * bw);
        } else {
            float outerArc = inside ? arc : arc + bw;
            float innerArc = inside ? arc - bw : arc;
            outerRect = new RoundRectangle2D.Float(0, 0, width, height, outerArc, outerArc);
            innerRect = new RoundRectangle2D.Float(bw, bw, width - 2 * bw, height - 2 * bw, innerArc, innerArc);
        }
        Path2D path = new Path2D.Float(Path2D.WIND_EVEN_ODD);
        path.append(outerRect, false);
        path.append(innerRect, false);
        g.fill(path);
        context.restore();
    }

    public static void paintFocusBorder(final Graphics2D g, final int width, final int height, final float arc,
                                        final float bw) {
        paintFocusBorder(g, width, height, arc, bw, true);
    }

    public static void paintFocusBorder(final Graphics2D g, final int width, final int height, final float arc,
                                        final float bw, final boolean active) {
        GraphicsContext config = new GraphicsContext(g);
        g.setComposite(PaintUtil.glowComposite);
        paintOutlineBorder(g, width, height, arc, bw, active, Outline.focus);
        config.restore();
    }

    public static void paintOutlineBorder(final Graphics2D g, final int width, final int height, final float arc,
                                          final float bw, final boolean hasFocus, final Outline type) {
        paintOutlineBorder(g, width, height, arc, bw, hasFocus, type, true);
    }

    public static void paintOutlineBorder(final Graphics2D g, final int width, final int height, final float arc,
                                          final float bw, final boolean hasFocus, final Outline type,
                                          final boolean withLineBorder) {
        type.setGraphicsColor(g, hasFocus);
        doPaint(g, width, height, arc, withLineBorder ? bw + getStrokeWidth(g) : bw, false);
    }

    public static void fillFocusRect(final Graphics2D g, final int x, final int y,
                                     final int width, final int height) {
        fillFocusRect(g, x, y, width, height, true);
    }

    public static void fillFocusRect(final Graphics2D g, final int x, final int y,
                                     final int width, final int height, final boolean active) {
        GraphicsContext config = new GraphicsContext(g);
        g.setComposite(PaintUtil.glowComposite);
        Outline.focus.setGraphicsColor(g, active);
        g.fillRect(x, y, width, height);
        config.restore();
    }

    public static void paintFocusOval(final Graphics2D g, final int x, final int y,
                                      final int width, final int height, final int bw) {
        paintFocusOval(g, (float) x, (float) y, (float) width, (float) height, (float) bw);
    }

    public static void paintFocusOval(final Graphics2D g, final float x, final float y,
                                      final float width, final float height, final float bw) {
        paintFocusOval(g, x, y, width, height, true, bw);
    }

    public static void paintFocusOval(final Graphics2D g, final float x, final float y,
                                      final float width, final float height, final boolean active, final float bw) {
        GraphicsContext config = new GraphicsContext(g);
        g.setComposite(PaintUtil.glowComposite);
        Outline.focus.setGraphicsColor(g, active);

        Path2D shape = new Path2D.Float(Path2D.WIND_EVEN_ODD);
        shape.append(new Ellipse2D.Float(x - bw, y - bw, width + bw * 2, height + bw * 2), false);
        shape.append(new Ellipse2D.Float(x, y, width, height), false);
        g.fill(shape);
        config.restore();
    }

    public static float getStrokeWidth(final Graphics2D g) {
        Stroke stroke = g.getStroke();
        return stroke instanceof BasicStroke ? ((BasicStroke) stroke).getLineWidth() : 1f;
    }

    public static void paintLineBorder(final Graphics2D g, final float x, final float y,
                                       final float width, final float height, final int arc) {
        float lw = getStrokeWidth(g);
        g.translate(x, y);
        doPaint(g, width, height, arc, lw, true);
        g.translate(-x, -y);
    }

    public static void fillRoundRect(final Graphics2D g, final float x, final float y,
                                     final float width, final float height, final int arc) {
        fillRoundRect(g, x, y, width, height, arc, true);
    }

    public static void fillRoundRect(final Graphics2D g, final float x, final float y,
                                     final float width, final float height, final int arc,
                                     final boolean adjustForBorder) {
        GraphicsContext context = GraphicsUtil.setupStrokePainting(g);
        int stroke = adjustForBorder ? (int) getStrokeWidth(g) : 0;
        float lw = Scale.equalWithError(Scale.getScaleX(g), 1f) ? stroke : stroke / 2f;
        float arcSize = arc;

        arcSize -= stroke;
        g.translate(lw, lw);
        roundRect.setRoundRect(x, y, width - 2 * lw, height - 2 * lw, arcSize, arcSize);
        g.fill(roundRect);
        g.translate(-lw, -lw);
        context.restore();
    }

    public static void drawRect(final Graphics g, final Rectangle rect, final int thickness) {
        drawRect(g, rect.x, rect.y, rect.width, rect.height, thickness);
    }

    public static void drawRect(final Graphics g, final Rectangle rect) {
        drawRect(g, rect, 1);
    }

    public static void drawRect(final Graphics g, final int x, final int y,
                                final int width, final int height, final int thickness) {
        g.fillRect(x, y, width, thickness);
        g.fillRect(x, y + thickness, thickness, height - 2 * thickness);
        g.fillRect(x + width - thickness, y + thickness, thickness, height - 2 * thickness);
        g.fillRect(x, y + height - thickness, width, thickness);
    }

    public static <T extends JComponent> void drawString(final Graphics g, final T c,
                                                         final String text, final Rectangle textRect) {
        drawString(g, c, text, textRect, SwingUtilities2.getFontMetrics(c, g));
    }

    public static <T extends JComponent> void drawString(final Graphics g, final T c, final View view,
                                                         final String text, final Rectangle textRect,
                                                         final FontMetrics fm) {
        drawStringImpl(g, c, view, text, textRect, c.getFont(), fm, -1);
    }

    public static <T extends JComponent> void drawString(final Graphics g, final T c,
                                                         final String text, final Rectangle textRect,
                                                         final FontMetrics fm) {
        drawStringImpl(g, c, null, text, textRect, c.getFont(), fm, -1);
    }

    public static <T extends JComponent> void drawStringUnderlineCharAt(final Graphics g, final T c,
                                                                        final String text, final int mnemIndex,
                                                                        final Rectangle textRect) {
        drawStringUnderlineCharAt(g, c, text, mnemIndex, textRect, c.getFont());
    }

    public static <T extends JComponent> void drawStringUnderlineCharAt(final Graphics g, final T c,
                                                                        final String text, final int mnemIndex,
                                                                        final Rectangle textRect,
                                                                        final Font f) {
        drawStringUnderlineCharAt(g, c, text, mnemIndex, textRect, f, SwingUtilities2.getFontMetrics(c, g));
    }

    public static <T extends JComponent> void drawStringUnderlineCharAt(final Graphics g, final T c, final View view,
                                                                        final String text, final int mnemIndex,
                                                                        final Rectangle textRect,
                                                                        final Font font,
                                                                        final FontMetrics fm) {
        drawStringImpl(g, c, view, text, textRect, font, fm, mnemIndex);
    }

    public static <T extends JComponent> void drawStringUnderlineCharAt(final Graphics g, final T c,
                                                                        final String text, final int mnemIndex,
                                                                        final Rectangle textRect,
                                                                        final Font font,
                                                                        final FontMetrics fm) {
        drawStringImpl(g, c, null, text, textRect, font, fm, mnemIndex);
    }

    public static <T extends JComponent> void drawStringImpl(final Graphics g, final T c,
                                                             final View view,
                                                             final String text, final Rectangle textRect,
                                                             final Font font, final FontMetrics fm,
                                                             final int mnemIndex) {
        drawStringImpl(g, c, view, text, textRect, font, fm, mnemIndex, c.getBackground());
    }

    public static <T extends JComponent> void drawStringImpl(final Graphics g, final T c,
                                                             final View view,
                                                             final String text, final Rectangle textRect,
                                                             final Font font, final FontMetrics fm,
                                                             final int mnemIndex,
                                                             final Color bg) {
        if (text != null && !text.equals("")) {
            GraphicsContext context = GraphicsUtil.setupAntialiasing(g);

            int asc = fm.getAscent();
            int x = textRect.x;
            int y = textRect.y;

            Graphics2D drawingGraphics = (Graphics2D) g;
            BufferedImage img = null;
            Window window = DarkUIUtil.getWindow(c);

            /*
             * If the parent window is non-opaque on Windows no sub-pixel AA is supported.
             * In this case we paint the text to an offscreen image with opaque background and paste
             * it draw it back to the original graphics object.
             *
             * See https://bugs.openjdk.java.net/browse/JDK-8215980?attachmentOrder=desc
             */
            boolean imgGraphics = SystemInfo.isWindows
                                  && window != null && window.getBackground().getAlpha() < 255;
            if (imgGraphics) {
                double scaleX = Scale.getScaleX((Graphics2D) g);
                double scaleY = Scale.getScaleX((Graphics2D) g);
                img = ImageUtil.createCompatibleImage((int) Math.round(scaleX * textRect.width),
                                                      (int) Math.round(scaleY * textRect.height));
                drawingGraphics = (Graphics2D) img.getGraphics();
                drawingGraphics.setColor(bg);
                drawingGraphics.fillRect(0, 0, img.getWidth(), img.getHeight());
                drawingGraphics.setColor(g.getColor());
                textRect.setLocation(0, 0);
                drawingGraphics.scale(scaleX, scaleY);
            } else {
                drawingGraphics.clipRect(textRect.x, textRect.y, textRect.width, textRect.height);
            }

            drawingGraphics.setFont(font);

            View v = view != null ? view : PropertyUtil.getObject(c, BasicHTML.propertyKey, View.class);
            if (v != null) {
                v.paint(drawingGraphics, textRect);
            } else {
                textRect.y += asc;
                if (mnemIndex >= 0) {
                    SwingUtilities2.drawStringUnderlineCharAt(c, drawingGraphics, text,
                                                              mnemIndex, textRect.x, textRect.y);
                } else {
                    SwingUtilities2.drawString(c, drawingGraphics, text, textRect.x, textRect.y);
                }
            }
            if (img != null) {
                drawingGraphics.dispose();
                g.drawImage(img, x, y, textRect.width, textRect.height, null);
            }
            context.restore();
        }
    }

    public static void fillRect(final Graphics g, final Rectangle r) {
        fillRect(g, r.x, r.y, r.width, r.height);
    }

    public static void fillRect(final Graphics g, final int x, final int y, final int w, final int h) {
        g.fillRect(x, y, w, h);
    }

    public enum Outline {
        error {
            @Override
            public void setGraphicsColor(final Graphics2D g, final boolean focused) {
                if (focused) {
                    g.setColor(getErrorFocusGlow());
                } else {
                    g.setColor(getErrorGlow());
                }
            }
        },

        warning {
            @Override
            public void setGraphicsColor(final Graphics2D g, final boolean focused) {
                g.setColor(getWarningGlow());
            }
        },

        defaultButton {
            @Override
            public void setGraphicsColor(final Graphics2D g, final boolean focused) {
                if (focused) {
                    g.setColor(getFocusGlow());
                }
            }
        },

        focus {
            @Override
            public void setGraphicsColor(final Graphics2D g, final boolean active) {
                if (active) {
                    g.setColor(getFocusGlow());
                } else {
                    g.setColor(getFocusInactiveGlow());
                }
            }
        };

        public abstract void setGraphicsColor(Graphics2D g, boolean focused);
    }
}

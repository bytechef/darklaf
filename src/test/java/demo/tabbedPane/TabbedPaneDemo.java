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
package demo.tabbedPane;

import com.github.weisj.darklaf.LafManager;
import com.github.weisj.darklaf.components.ClosableTabbedPane;
import com.github.weisj.darklaf.util.StringUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.UIResource;
import java.awt.*;
import java.awt.event.ItemEvent;

public class TabbedPaneDemo extends JFrame {

    public static void main(final String[] args) {
        SwingUtilities.invokeLater(() -> {
            LafManager.install();
            final JFrame frame = new JFrame();
            int tabCount = 1;
            frame.setSize(500 * tabCount, 500);
            JPanel c = new JPanel(new GridLayout(1, 2));
            for (int j = 0; j < tabCount; j++) {
                JPanel p = new JPanel(new BorderLayout());
                final ClosableTabbedPane tabbedPane = new ClosableTabbedPane();
                tabbedPane.setName("TabPane " + j);

                for (int i = 0; i < 4; i++) {
                    JTextPane editor = new JTextPane();
                    editor.setText(StringUtil.repeat("TabPaneDemo TabPane-" + j + "\n", i + 1));
                    tabbedPane.addTab("Tab (" + i + "," + j + ")", editor);
                }
                tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
                tabbedPane.putClientProperty("JTabbedPane.showNewTabButton", Boolean.TRUE);
                tabbedPane.putClientProperty("JTabbedPane.dndEnabled", Boolean.TRUE);
                tabbedPane.putClientProperty("JTabbedPane.trailingComponent", new Label("Trailing"));
                tabbedPane.putClientProperty("JTabbedPane.leadingComponent", new Label("Leading"));
                tabbedPane.putClientProperty("JTabbedPane.northComponent", new Label("North"));
                tabbedPane.putClientProperty("JTabbedPane.eastComponent", new Label("East"));
                tabbedPane.putClientProperty("JTabbedPane.southComponent", new Label("South"));
                tabbedPane.putClientProperty("JTabbedPane.westComponent", new Label("West"));

                tabbedPane.setSelectedIndex(-1);

                p.add(tabbedPane, BorderLayout.CENTER);
                p.add(new JPanel() {{
                    setLayout(new FlowLayout(FlowLayout.LEFT));
                    add(new JComboBox<String>() {{
                        addItem("TOP");
                        addItem("BOTTOM");
                        addItem("LEFT");
                        addItem("RIGHT");
                        setEditable(false);
                        addItemListener(e -> {
                            if (e.getStateChange() == ItemEvent.SELECTED) {
                                if ("TOP".equals(e.getItem())) {
                                    tabbedPane.setTabPlacement(JTabbedPane.TOP);
                                } else if ("BOTTOM".equals(e.getItem())) {
                                    tabbedPane.setTabPlacement(JTabbedPane.BOTTOM);
                                } else if ("LEFT".equals(e.getItem())) {
                                    tabbedPane.setTabPlacement(JTabbedPane.LEFT);
                                } else if ("RIGHT".equals(e.getItem())) {
                                    tabbedPane.setTabPlacement(JTabbedPane.RIGHT);
                                }
                                tabbedPane.requestFocus();
                            }
                        });
                    }});
                    add(new JCheckBox("Scroll-layout enabled") {{
                        addActionListener(e -> {
                            if (tabbedPane.getTabLayoutPolicy() == JTabbedPane.WRAP_TAB_LAYOUT) {
                                tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
                            } else {
                                tabbedPane.setTabLayoutPolicy(JTabbedPane.WRAP_TAB_LAYOUT);
                            }
                        });
                        setSelected(true);
                    }});
                    add(new JCheckBox("Left To Right") {{
                        addActionListener(e -> {
                            if (tabbedPane.getComponentOrientation().isLeftToRight()) {
                                tabbedPane.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
                            } else {
                                tabbedPane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
                            }
                        });
                        setSelected(true);
                    }});
                    add(new JCheckBox("DnD enabled") {{
                        addActionListener(e -> {
                            if (tabbedPane.getDropTarget().isActive()) {
                                tabbedPane.putClientProperty("JTabbedPane.dndEnabled", Boolean.FALSE);
                            } else {
                                tabbedPane.putClientProperty("JTabbedPane.dndEnabled", Boolean.TRUE);
                            }
                        });
                        setSelected(true);
                    }});
                }}, BorderLayout.SOUTH);
                c.add(p);
            }
            frame.setContentPane(c);
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
            frame.repaint();
        });
    }

    private static class Label extends JLabel implements UIResource {
        private Label(final String title) {
            super(title);
            setBorder(new EmptyBorder(5, 5, 5, 5));
        }
    }
}

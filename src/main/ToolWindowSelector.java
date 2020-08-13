package main;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.*;
import javax.swing.event.*;

public class ToolWindowSelector extends JPanel {
    private static final long serialVersionUID = 1L;
    private int _width, _center;
    private JSlider _width_slider, _center_slider;
    private JLabel _width_label, _center_label;

    public ToolWindowSelector() {
        JLabel win_set_tilte = new JLabel("Window Range Selector");

        int range_max = 100;
        _width = 50;
        _center = 50;

        _width_label = new JLabel("Width:" + _width);
        _center_label = new JLabel("Center:" + _center);

        _width_slider = new JSlider(0, range_max, _width);
        _width_slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (source.getValueIsAdjusting()) {
                    _width = (int) source.getValue();
                    _width_label.setText("Width:" + _width);
                    System.out.println("_width_slider stateChanged: " + _width);
                }
            }
        });

        _center_slider = new JSlider(0, range_max, _center);
        _center_slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (source.getValueIsAdjusting()) {
                    _center = (int) source.getValue();
                    _center_label.setText("Center:" + _center);
                    System.out.println("_center_slider stateChanged: " + _center);
                }
            }
        });

        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weighty = 0.3;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(2, 2, 2, 2); // ************** */

        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 0;
        this.add(win_set_tilte, c);

        c.gridwidth = 1;
        c.weightx = 0.01;
        c.gridx = 0;
        c.gridy = 1;
        this.add(_width_label, c);

        c.gridx = 0;
        c.gridy = 2;
        this.add(_center_label, c);

        c.weightx = 0.99;
        c.gridx = 1;
        c.gridy = 1;
        this.add(_width_slider, c);

        c.gridx = 1;
        c.gridy = 2;
        this.add(_center_slider, c);

    }
}
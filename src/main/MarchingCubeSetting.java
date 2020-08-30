package main;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MarchingCubeSetting extends JPanel {
    private static final long serialVersionUID = 1L;
    // private Segment _rg_seg;
    private JSlider _size_slider;
    private JLabel _size_label;
    private JButton _update_Btn;
    // private static JLabel _postion_label = null;

    public MarchingCubeSetting(Viewport3d v3d) {
        int range_max = 10;
        JLabel win_set_tilte = new JLabel("Marching Setting");

        _size_label = new JLabel("Cube Size: " + String.format("%d", 3));
        _size_slider = new JSlider(1, range_max, 3);
        _size_slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (source.getValueIsAdjusting()) {
                    int size = source.getValue();
                    _size_label.setText("Size: " + String.format("%d", size));
                    v3d.setMCsize(size);
                    v3d.update_view();
                    // System.out.println("_varianz_slider stateChanged: " + _varianz);
                }
            }
        });

        _update_Btn = new JButton("Update: MC");
        _update_Btn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                v3d.toggleMarchingCube();
                v3d.update_view();
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

        c.gridwidth = 2;
        c.gridx = 1;
        c.gridy = 0;
        this.add(_update_Btn, c);

        c.gridwidth = 1;
        c.weightx = 0.01;
        c.gridx = 0;
        c.gridy = 1;
        this.add(_size_label, c);

        c.weightx = 0.99;
        c.gridx = 1;
        c.gridy = 1;
        this.add(_size_slider, c);

    }

}
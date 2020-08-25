package main;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.*;
import javax.swing.event.*;

public class Tool3dSetting extends JPanel {
    private static final long serialVersionUID = 1L;
    // private Segment _rg_seg;
    private JSlider _distance_slider;
    private JLabel _distance_label;
    // private static JLabel _postion_label = null;

    public Tool3dSetting(Viewport3d v3d) {
        int range_max = 500;
        float dis_range = 0.01f;
        JLabel win_set_tilte = new JLabel("3D Setting");

        _distance_label = new JLabel("Distance: " + String.format("%.4f", v3d.getPointCloudDis()));
        _distance_slider = new JSlider(0, range_max, (int) (v3d.getPointCloudDis() * range_max / dis_range));
        _distance_slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (source.getValueIsAdjusting()) {
                    float distance = (float) source.getValue() / (float) range_max * dis_range;
                    _distance_label.setText("Distance: " + String.format("%.3f", distance));
                    v3d.setPointCloudDis(distance);
                    v3d.update_view();
                    // System.out.println("_varianz_slider stateChanged: " + _varianz);
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
        this.add(_distance_label, c);

        c.weightx = 0.99;
        c.gridx = 1;
        c.gridy = 1;
        this.add(_distance_slider, c);

    }

}
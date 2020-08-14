package main;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import javax.swing.*;
import javax.swing.event.*;
import javax.vecmath.Point3i;

public class ToolRGSelector extends JPanel {
    private static final long serialVersionUID = 1L;
    private Segment _rg_seg;
    private JSlider _varianz_slider;
    private JLabel _varianz_label, _seed_label, _postion_abel;

    public ToolRGSelector(Viewport2d v2d, Segment seg) {

        JLabel win_set_tilte = new JLabel("Region Grow Selector");
        final ImageStack slices = ImageStack.getInstance();

        // final String RG_SEG_NAME = new String("Region Grow Segment");

        _rg_seg = seg;

        int range_max = 100;

        _varianz_label = new JLabel("Varianz: " + String.format("%.2f", v2d.RG_Varianz));
        _seed_label = new JLabel("Seed:");
        _postion_abel = new JLabel("(xx,yy,zz)");

        _varianz_slider = new JSlider(0, range_max, (int) (v2d.RG_Varianz * 100));
        _varianz_slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                if (source.getValueIsAdjusting()) {
                    float varianz = (float) source.getValue() / (float) range_max;
                    _varianz_label.setText("Varianz: " + String.format("%.2f", varianz));
                    v2d.RG_Varianz = varianz;

                    _rg_seg.create_region_grow_seg(v2d.RG_Seed, varianz, slices);
                    v2d.update_view();
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
        this.add(_seed_label, c);

        c.gridx = 0;
        c.gridy = 2;
        this.add(_varianz_label, c);

        c.weightx = 0.99;
        c.gridx = 1;
        c.gridy = 1;
        this.add(_postion_abel, c);

        c.gridx = 1;
        c.gridy = 2;
        this.add(_varianz_slider, c);

    }
}
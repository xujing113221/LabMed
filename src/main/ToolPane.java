package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.border.LineBorder;

/**
 * Container class for all tools in the lower section of the main window.
 *
 * @author Karl-Ingo Friese
 */
public class ToolPane extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTabbedPane _tabbedPane;

	/**
	 * Default Constructor. Creates an empty ToolPane with no active panel.
	 *
	 */
	public ToolPane() {
		this.setPreferredSize(new Dimension(800, 200));
		setBorder(new LineBorder(Color.black, 1));
		setLayout(new BorderLayout(0, 0));
		_tabbedPane = new JTabbedPane();
		this.add(_tabbedPane, BorderLayout.CENTER);
		this.validate();
	}

	/**
	 * Shows a new tool in the ToolPane.
	 * 
	 * @param panel the new panel to show
	 */
	public void showTool(String tabname, JPanel panel) {
		int index = _tabbedPane.indexOfTab(tabname);
		if (index == -1) {
			_tabbedPane.addTab(tabname, panel);
			_tabbedPane.setSelectedComponent(panel);
		} else {
			_tabbedPane.setSelectedIndex(index);
		}

		this.repaint();
	}
}

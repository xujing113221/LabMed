package main;

import java.awt.event.*;
import javax.swing.*;

import misc.DiFileFilter;

import java.io.*;

/**
 * This class represents the main menu of YaDiV (lab version).
 * 
 * @author Karl-Ingo Friese
 */
public class MenuBar extends JMenuBar {
	private static final long serialVersionUID = 1L;

	private JMenu _menuFile;
	private JMenu _menu2d;
	private JMenu _menu3d;
	private JMenu _menuTools;
	private JMenu _menuWinSet;
	private JMenu _menuRGSeg;
	private JMenuItem _no_entries2d;
	private JMenuItem _no_entries3d;
	private JMenuItem _tools_show_seg;
	private InfoWindow _info_frame;
	private ToolPane _tools;

	private Viewport2d _v2d;
	private Viewport3d _v3d;
	private MainWindow _win;

	/**
	 * Constructor. Needs many references, since the MenuBar has to trigger a lot of
	 * functions.
	 * 
	 * @param slices the global image stack reference
	 * @param v2d    the Viewport2d reference
	 * @param v3d    the Viewport3d reference
	 * @param tools  the ToolPane reference
	 */
	public MenuBar(Viewport2d v2d, Viewport3d v3d, ToolPane tools) {
		JMenuItem item;

		_v2d = v2d;
		_v3d = v3d;
		_tools = tools;

		_menuFile = new JMenu("File");
		_menu2d = new JMenu("2D View");
		_menu3d = new JMenu("3D View");
		_menuTools = new JMenu("Tools");
		_menuWinSet = new JMenu("Window Setting");
		_menuRGSeg = new JMenu("RG Segement");
		_info_frame = null;

		// -------------------------------------------------------------------------------------

		item = new JMenuItem(new String("Load"), 'L');
		item.addActionListener(loadListener);
		_menuFile.add(item);

		item = new JMenuItem(new String("Save"));
		item.addActionListener(saveListener);
		item.setEnabled(false);
		_menuFile.add(item);

		item = new JMenuItem(new String("Save as ..."));
		item.addActionListener(saveAsListener);
		item.setEnabled(false);
		_menuFile.add(item);

		item = new JMenuItem(new String("Quit"), 'Q');
		item.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				System.exit(0);
			}
		});
		_menuFile.add(item);

		// -------------------------------------------------------------------------------------

		item = new JMenuItem(new String("DICOM Info"));
		item.addActionListener(showInfoListener);
		_menu2d.add(item);

		_menu2d.addSeparator();
		item = new JCheckBoxMenuItem(new String("Show original data"), true);
		item.addActionListener(toggleBGListener2d);
		_menu2d.add(item);

		JRadioButtonMenuItem rbMenuItem;
		ButtonGroup group = new ButtonGroup();

		rbMenuItem = new JRadioButtonMenuItem("Transversal");
		rbMenuItem.addActionListener(setViewModeListener);
		group.add(rbMenuItem);
		_menu2d.add(rbMenuItem);
		rbMenuItem.setSelected(true);

		rbMenuItem = new JRadioButtonMenuItem("Sagittal");
		rbMenuItem.addActionListener(setViewModeListener);
		group.add(rbMenuItem);
		_menu2d.add(rbMenuItem);

		rbMenuItem = new JRadioButtonMenuItem("Frontal");
		rbMenuItem.addActionListener(setViewModeListener);
		group.add(rbMenuItem);
		_menu2d.add(rbMenuItem);

		_menu2d.addSeparator();

		_no_entries2d = new JMenuItem(new String("no segmentations yet"));
		_no_entries2d.setEnabled(false);
		_menu2d.add(_no_entries2d);

		// -------------------------------------------------------------------------------------

		item = new JMenuItem(new String("3d Item 1"));
		// item.addActionListener(...);
		item = new JCheckBoxMenuItem(new String("Show original Data"), false);
		item.addActionListener(toggleBGListener3d);
		_menu3d.add(item);

		item = new JCheckBoxMenuItem(new String("Show Ortho Slice"), false);
		item.addActionListener(toggleOrthoSliceListener3d);
		_menu3d.add(item);

		_menu3d.addSeparator();

		_no_entries3d = new JMenuItem(new String("no segmentations yet"));
		_no_entries3d.setEnabled(false);
		_menu3d.add(_no_entries3d);

		// -------------------------------------------------------------------------------------

		item = new JMenuItem(new String("Neue Segmentierung"));
		item.addActionListener(newSegmentListener);
		_menuTools.add(item);

		_tools_show_seg = new JMenuItem(new String("Show Segemet Tool"));
		_tools_show_seg.addActionListener(showSegementListener);
		_tools_show_seg.setEnabled(false);
		_menuTools.add(_tools_show_seg);

		// -------------------------------------------------------------------------------------

		item = new JMenuItem(new String("Show Setting Tool"));
		item.addActionListener(windowSettingListener);
		_menuWinSet.add(item);

		// -------------------------------------------------------------------------------------

		item = new JMenuItem(new String("Show Region Grow"));
		item.addActionListener(RG_SegmentListener);
		_menuRGSeg.add(item);

		// -------------------------------------------------------------------------------------
		add(_menuFile);
		add(_menu2d);
		add(_menu3d);
		add(_menuTools);
		add(_menuWinSet);
		add(_menuRGSeg);

		// -------------------------------------------------------------------------------------
		_tools.showTool("3D Setting", new Tool3dSetting(_v3d));
		_tools.showTool("Marching Setting", new MarchingCubeSetting(_v3d));
		_tools.showTool("Windows Setting", new ToolWindowSelector(_v2d, _v3d));
	}

	/**
	 * This function is called when someone chooses to load DICOM series.
	 */
	ActionListener loadListener = new ActionListener() {
		public void actionPerformed(ActionEvent event) {
			openDialog(false);
		}
	};

	/**
	 * This function is called when someone chooses to save the current project.
	 */
	ActionListener saveListener = new ActionListener() {
		public void actionPerformed(ActionEvent event) {
			// saveFile(...);
		}
	};

	/**
	 * This function is called when someone chooses to save the current project
	 * under a new name.
	 */
	ActionListener saveAsListener = new ActionListener() {
		public void actionPerformed(ActionEvent event) {
			openDialog(true);
		}
	};

	/**
	 * Opens a file chooser dialog with a DICOM file filter and directory.
	 * 
	 * @param save true if the dialog should be a save file dialog, false if not
	 */
	private void openDialog(boolean save) {
		int returnVal;
		File file;
		JFileChooser chooser;
		String default_dir = new String("/Users/xujing/Documents/Deutschland/Labor/LabMed/");

		if (new File(default_dir).exists()) {
			chooser = new JFileChooser(default_dir);
		} else {
			chooser = new JFileChooser();
		}

		DiFileFilter filter = new DiFileFilter();
		filter.setDescription("Dicom Image Files");
		chooser.setFileFilter(filter);

		if (save) {
			returnVal = chooser.showSaveDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = chooser.getSelectedFile();
				if (!file.canWrite()) {
					System.out.println("could not read!");
					return;
				}
				// saveFile(file);
			}
		} else {
			returnVal = chooser.showOpenDialog(null);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				file = chooser.getSelectedFile();
				if (!file.canRead()) {
					System.out.println("could not read!");
					return;
				}
				System.out.println(file.getParent());
				LabMed.get_is().initFromDirectory(file.getParent());
			}
		}

	}

	/**
	 * This function is called when someone wants to see the dicome file info
	 * window.
	 */
	ActionListener showInfoListener = new ActionListener() {
		public void actionPerformed(ActionEvent event) {
			if (LabMed.get_is().getNumberOfImages() == 0) {
				JOptionPane.showMessageDialog(null, "Fehler: Keine DICOM Datei geöffnet", "Inane error",
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			if (_info_frame == null) {
				_info_frame = new InfoWindow();
				LabMed.get_is().addObserver(_info_frame);
			}

			if (!_info_frame.isVisible()) {
				_info_frame.setVisible(true);
			}

			_info_frame.showInfo(_v2d.currentFile());

		}
	};

	/**
	 * Actionlistener for changing the 2d viewmode.
	 */
	ActionListener setViewModeListener = new ActionListener() {
		public void actionPerformed(ActionEvent event) {
			String name = event.getActionCommand();
			if (name.equals("Transversal")) {
				_v2d.setViewMode(0);
				_v3d.setViewMode(0);
			} else if (name.equals("Sagittal")) {
				_v2d.setViewMode(1);
				_v3d.setViewMode(1);
			} else if (name.equals("Frontal")) {
				_v2d.setViewMode(2);
				_v3d.setViewMode(2);
			}
		}
	};

	/**
	 * ActionListener for toggling the 2d background image.
	 */
	ActionListener toggleBGListener2d = new ActionListener() {
		public void actionPerformed(ActionEvent event) {
			_v2d.toggleBG();
		}
	};

	/**
	 * ActionListener for toggling the 3d background image.
	 */
	ActionListener toggleBGListener3d = new ActionListener() {
		public void actionPerformed(ActionEvent event) {
			// _v3d.toggleBG();
			_v3d.toggleShowOriginalData();
			_v3d.update_view();
		}
	};

	ActionListener toggleOrthoSliceListener3d = new ActionListener() {
		public void actionPerformed(ActionEvent event) {
			// _v3d.toggleBG();
			_v3d.toggleOrthoSlice();
			_v3d.update_view();
		}
	};

	/**
	 * ActionListener for toggling a segmentation in the 2d viewport.
	 */
	ActionListener toggleSegListener2d = new ActionListener() {
		public void actionPerformed(ActionEvent event) {
			String name = event.getActionCommand();
			_v2d.toggleSeg(LabMed.get_is().getSegment(name));
		}
	};

	/**
	 * ActionListener for toggling a segmentation in the 3d viewport.
	 */
	ActionListener toggleSegListener3d = new ActionListener() {
		public void actionPerformed(ActionEvent event) {
			String name = event.getActionCommand();
			_v3d.toggleSeg(LabMed.get_is().getSegment(name));
		}
	};

	/**
	 * ActionListener for adding a new segmentation to the global image stack.
	 */
	ActionListener newSegmentListener = new ActionListener() {
		public void actionPerformed(ActionEvent event) {
			ImageStack is = LabMed.get_is();
			if (is.getNumberOfImages() == 0) {
				JOptionPane.showMessageDialog(_win, "Segmentierung ohne geöffneten DICOM Datensatz nicht möglich.",
						"Inane error", JOptionPane.ERROR_MESSAGE);
			} else if (is.getSegmentNumber() == 3) {
				JOptionPane.showMessageDialog(_win,
						"In der Laborversion werden nicht mehr als drei Segmentierungen benötigt.", "Inane error",
						JOptionPane.ERROR_MESSAGE);
			} else {
				String name = JOptionPane.showInputDialog(_win, "Name der Segmentierung");
				if (name != null) {
					_no_entries2d.setVisible(false);
					_no_entries3d.setVisible(false);
					Segment seg = is.createSegment(name);
					_v2d.toggleSeg(seg);
					JMenuItem item = new JCheckBoxMenuItem(name, true);
					item.addActionListener(toggleSegListener2d);
					_menu2d.add(item);
					item = new JCheckBoxMenuItem(name, false);
					item.addActionListener(toggleSegListener3d);
					_menu3d.add(item);
					// _tools.showTool(new ToolRangeSelector(seg));
					_tools.showTool("Segments", new ToolRangeSelector(_v2d, seg));
					_tools_show_seg.setEnabled(true);
				}
			}
		}
	};

	ActionListener showSegementListener = new ActionListener() {
		public void actionPerformed(ActionEvent event) {
			String seg_name = _v2d._slices.getSegNames().getElementAt(0);
			Segment seg = _v2d._slices.getSegment(seg_name);
			_tools.showTool("Segments", new ToolRangeSelector(_v2d, seg));
		}
	};

	ActionListener windowSettingListener = new ActionListener() {
		public void actionPerformed(ActionEvent event) {
			ImageStack is = LabMed.get_is();
			if (is.getNumberOfImages() == 0) {
				JOptionPane.showMessageDialog(_win, "WindowSelector ohne geöffneten DICOM Datensatz nicht möglich.",
						"Inane error", JOptionPane.ERROR_MESSAGE);
			} else {
				_tools.showTool("Windows Setting", new ToolWindowSelector(_v2d, _v3d));
			}
		}
	};

	ActionListener RG_SegmentListener = new ActionListener() {
		public void actionPerformed(ActionEvent event) {
			ImageStack is = LabMed.get_is();
			if (is.getNumberOfImages() == 0) {
				JOptionPane.showMessageDialog(_win, "Segmentierung ohne geöffneten DICOM Datensatz nicht möglich.",
						"Inane error", JOptionPane.ERROR_MESSAGE);
			} else {
				String rg_name = new String("Region Grow Segment");
				Segment seg;

				if (!is.getSegNames().contains(rg_name)) {
					seg = is.createSegment(rg_name);
					_no_entries2d.setVisible(false);
					_no_entries3d.setVisible(false);
					JMenuItem item = new JCheckBoxMenuItem(rg_name, true);
					item.addActionListener(toggleSegListener2d);
					_menu2d.add(item);
					item = new JCheckBoxMenuItem(rg_name, false);
					item.addActionListener(toggleSegListener3d);
					_menu3d.add(item);
					_v2d.toggleSeg(seg);
				} else
					seg = is.getSegment(rg_name);
				_tools.showTool("RG-Seg-Setting", new ToolRGSelector(_v2d, seg));
			}

		}
	};
}

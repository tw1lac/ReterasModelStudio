package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.model.Material;
import com.hiveworkshop.rms.ui.icons.RMSIcons;
import com.hiveworkshop.rms.util.IterableListModel;

import javax.swing.*;

public class GeosetEditPanels {
	public static final ImageIcon orangeIcon = RMSIcons.orangeIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/BlankOrange_small.png"));
	public static final ImageIcon greenIcon = RMSIcons.greenIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/Blank_small.png"));
	public static final ImageIcon geoIcon = RMSIcons.geoIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/geo_small.png"));
	//	JTabbedPane geosetTabs = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);
	ImportPanel importPanel;
	private ModelHolderThing mht;

	public GeosetEditPanels(ModelHolderThing mht, ImportPanel importPanel) {
		this.mht = mht;
		this.importPanel = importPanel;
	}

	private static void importAllGeos(JTabbedPane geosetTabs, boolean b) {
		for (int i = 0; i < geosetTabs.getTabCount(); i++) {
			final GeosetPanel geoPanel = (GeosetPanel) geosetTabs.getComponentAt(i);
			geoPanel.setSelected(b);
		}
	}

	public JPanel makeGeosetPanel() {
		JPanel geosetsPanel = new JPanel();
//		addTab("Geosets", geoIcon, geosetsPanel, "Controls which geosets will be imported.");

		final IterableListModel<Material> materials = new IterableListModel<>();
		materials.addAll(mht.currentModel.getMaterials());
		materials.addAll(mht.importModel.getMaterials());

		// A list of all materials available for use during this import, in
		// the form of a IterableListModel

		final MaterialListCellRenderer materialsRenderer = new MaterialListCellRenderer(mht.currentModel);
		// All material lists will know which materials come from the
		// out-of-model source (imported model)

		// Build the geosetTabs list of GeosetPanels
		for (int i = 0; i < mht.currentModel.getGeosets().size(); i++) {
			final GeosetPanel geoPanel = new GeosetPanel(false, mht.currentModel, i, materials, materialsRenderer, importPanel);

			mht.geosetTabs.addTab(mht.currentModel.getName() + " " + (i + 1), greenIcon, geoPanel, "Click to modify material data for this geoset.");
		}
		for (int i = 0; i < mht.importModel.getGeosets().size(); i++) {
			final GeosetPanel geoPanel = new GeosetPanel(true, mht.importModel, i, materials, materialsRenderer, importPanel);

			mht.geosetTabs.addTab(mht.importModel.getName() + " " + (i + 1), orangeIcon, geoPanel, "Click to modify importing and material data for this geoset.");
		}

		JButton importAllGeos = new JButton("Import All");
		importAllGeos.addActionListener(e -> importAllGeos(mht.geosetTabs, true));
		geosetsPanel.add(importAllGeos);

		JButton uncheckAllGeos = new JButton("Leave All");
		uncheckAllGeos.addActionListener(e -> importAllGeos(mht.geosetTabs, false));
		geosetsPanel.add(uncheckAllGeos);

		setLayout(geosetsPanel, importAllGeos, uncheckAllGeos);

		return geosetsPanel;
	}

	private void setLayout(JPanel geosetsPanel, JButton importAllGeos, JButton uncheckAllGeos) {
		final GroupLayout geosetLayout = new GroupLayout(geosetsPanel);
		geosetLayout.setHorizontalGroup(geosetLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addGroup(geosetLayout.createSequentialGroup()
						.addComponent(importAllGeos).addGap(8)
						.addComponent(uncheckAllGeos))
				.addComponent(mht.geosetTabs));
		geosetLayout.setVerticalGroup(geosetLayout.createSequentialGroup()
				.addGroup(geosetLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(importAllGeos)
						.addComponent(uncheckAllGeos)).addGap(8)
				.addComponent(mht.geosetTabs));
		geosetsPanel.setLayout(geosetLayout);
	}
}

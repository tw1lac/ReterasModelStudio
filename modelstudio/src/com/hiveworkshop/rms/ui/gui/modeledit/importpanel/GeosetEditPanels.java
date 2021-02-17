package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.model.EditableModel;
import com.hiveworkshop.rms.editor.model.Material;
import com.hiveworkshop.rms.ui.icons.RMSIcons;

import javax.swing.*;

public class GeosetEditPanels {
	public static final ImageIcon orangeIcon = RMSIcons.orangeIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/BlankOrange_small.png"));
	public static final ImageIcon greenIcon = RMSIcons.greenIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/Blank_small.png"));
	public static final ImageIcon geoIcon = RMSIcons.geoIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/geo_small.png"));
	JTabbedPane geosetTabs;// = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);
//	JTabbedPane geosetTabs = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);

	public GeosetEditPanels(JTabbedPane geosetTabs) {
		this.geosetTabs = geosetTabs;
	}

	private static void importAllGeos(JTabbedPane geosetTabs, boolean b) {
		for (int i = 0; i < geosetTabs.getTabCount(); i++) {
			final GeosetPanel geoPanel = (GeosetPanel) geosetTabs.getComponentAt(i);
			geoPanel.setSelected(b);
		}
	}

	public JPanel makeGeosetPanel(EditableModel currentModel, EditableModel importedModel) {
		JPanel geosetsPanel = new JPanel();
//		addTab("Geosets", geoIcon, geosetsPanel, "Controls which geosets will be imported.");

		final DefaultListModel<Material> materials = new DefaultListModel<>();
		materials.addAll(currentModel.getMaterials());
		materials.addAll(importedModel.getMaterials());
//		for (int i = 0; i < currentModel.getMaterials().size(); i++) {
//			materials.addElement(currentModel.getMaterials().get(i));
//		}
//		for (int i = 0; i < importedModel.getMaterials().size(); i++) {
//			materials.addElement(importedModel.getMaterials().get(i));
//		}
		// A list of all materials available for use during this import, in
		// the form of a DefaultListModel

		final MaterialListCellRenderer materialsRenderer = new MaterialListCellRenderer(currentModel);
		// All material lists will know which materials come from the
		// out-of-model source (imported model)

		// Build the geosetTabs list of GeosetPanels
		for (int i = 0; i < currentModel.getGeosets().size(); i++) {
			final GeosetPanel geoPanel = new GeosetPanel(false, currentModel, i, materials, materialsRenderer);

			geosetTabs.addTab(currentModel.getName() + " " + (i + 1), greenIcon, geoPanel, "Click to modify material data for this geoset.");
		}
		for (int i = 0; i < importedModel.getGeosets().size(); i++) {
			final GeosetPanel geoPanel = new GeosetPanel(true, importedModel, i, materials, materialsRenderer);

			geosetTabs.addTab(importedModel.getName() + " " + (i + 1), orangeIcon, geoPanel, "Click to modify importing and material data for this geoset.");
		}

		JButton importAllGeos = new JButton("Import All");
		importAllGeos.addActionListener(e -> importAllGeos(geosetTabs, true));
		geosetsPanel.add(importAllGeos);

		JButton uncheckAllGeos = new JButton("Leave All");
		uncheckAllGeos.addActionListener(e -> importAllGeos(geosetTabs, false));
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
				.addComponent(geosetTabs));
		geosetLayout.setVerticalGroup(geosetLayout.createSequentialGroup()
				.addGroup(geosetLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(importAllGeos)
						.addComponent(uncheckAllGeos)).addGap(8)
				.addComponent(geosetTabs));
		geosetsPanel.setLayout(geosetLayout);
	}
}

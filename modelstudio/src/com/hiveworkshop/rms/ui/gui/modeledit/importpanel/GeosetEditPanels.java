package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.model.Geoset;
import com.hiveworkshop.rms.editor.model.Material;
import com.hiveworkshop.rms.ui.icons.RMSIcons;
import com.hiveworkshop.rms.util.IterableListModel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class GeosetEditPanels {
	public static final ImageIcon orangeIcon = RMSIcons.orangeIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/BlankOrange_small.png"));
	public static final ImageIcon greenIcon = RMSIcons.greenIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/Blank_small.png"));
	public static final ImageIcon geoIcon = RMSIcons.geoIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/geo_small.png"));
	//	JTabbedPane geosetTabs = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);
	ImportPanel importPanel;
	GeosetPanel currGeoPanel;
	CardLayout geoCardLayout;
	JPanel geoPanelCards;
	private ModelHolderThing mht;

	public GeosetEditPanels(ModelHolderThing mht, ImportPanel importPanel) {
		this.mht = mht;
		this.importPanel = importPanel;
	}

	public JPanel makeGeosetPanel() {
//		JPanel geosetsPanel = new JPanel(new MigLayout("gap 0, fill", "[grow]8[grow]", "[]8[grow]"));
		JPanel geosetsPanel = new JPanel(new MigLayout("gap 0, fill", "[grow]", "[]8[grow]"));

		final IterableListModel<Material> materials = new IterableListModel<>();
		materials.addAll(mht.currentModel.getMaterials());
		materials.addAll(mht.importModel.getMaterials());

		// A list of all materials available for use during this import, in the form of a IterableListModel

		final MaterialListCellRenderer materialsRenderer = new MaterialListCellRenderer(mht.currentModel);
		// All material lists will know which materials come from the out-of-model source (imported model)

		// Build the geosetTabs list of GeosetPanels
		List<GeosetShell> currGeosets = new ArrayList<>();
		for (Geoset geoset : mht.currentModel.getGeosets()) {
			currGeosets.add(new GeosetShell(geoset, mht.currentModel, false));
		}
		List<GeosetShell> impGeosets = new ArrayList<>();
		for (Geoset geoset : mht.importModel.getGeosets()) {
			impGeosets.add(new GeosetShell(geoset, mht.importModel, false));
		}
		mht.geoShells.addAll(currGeosets);
		mht.geoShells.addAll(impGeosets);

		JPanel bigPanel = new JPanel(new MigLayout("gap 0, fill", "[5%:30%:30%][100%:70%:70%]"));
		JScrollPane geoScrollPane = new JScrollPane(mht.geoTabs);
		bigPanel.add(geoScrollPane, "growy");
//		bigPanel.add(mht.geoTabs);

//		final GeosetPanel geoPanel = new GeosetPanel(false, mht.currentModel, materials, materialsRenderer, importPanel);
//		bigPanel.add(geoPanel);
		currGeoPanel = new GeosetPanel(false, mht.currentModel, materials, materialsRenderer, importPanel);
		bigPanel.add(currGeoPanel, "growy");

		mht.geoTabs.addListSelectionListener(e -> setGeoset());

//		for (int i = 0; i < mht.currentModel.getGeosets().size(); i++) {
////			final GeosetPanel geoPanel = new GeosetPanel(false, mht.currentModel, i, materials, materialsRenderer, importPanel);
//
//			mht.geosetTabs.addTab(mht.currentModel.getName() + " " + (i + 1), greenIcon, null, "Click to modify material data for this geoset.");
//			System.out.println("tab component: " + mht.geosetTabs.getTabComponentAt(i));
//			System.out.println("tab component class: " + mht.geosetTabs.getTabComponentAt(i).getClass());
////			mht.geosetTabs.addTab(mht.currentModel.getName() + " " + (i + 1), greenIcon, geoPanel, "Click to modify material data for this geoset.");
//		}
//		for (int i = 0; i < mht.importModel.getGeosets().size(); i++) {
////			final GeosetPanel geoPanel = new GeosetPanel(true, mht.importModel, i, materials, materialsRenderer, importPanel);
//
////			mht.geosetTabs.addTab(mht.importModel.getName() + " " + (i + 1), orangeIcon, geoPanel, "Click to modify importing and material data for this geoset.");
//			mht.geosetTabs.addTab(mht.importModel.getName() + " " + (i + 1), orangeIcon, null, "Click to modify importing and material data for this geoset.");
//			System.out.println("tab component: " + mht.geosetTabs.getTabComponentAt(i));
//			System.out.println("component : " + mht.geosetTabs.getComponentAt(i));
//		}

		JPanel topPanel = getTopPanel();

		geosetsPanel.add(topPanel, "spanx, align center, wrap");
		geosetsPanel.add(bigPanel, "growx, growy");

		return geosetsPanel;
	}

	private JPanel getTopPanel() {
		JPanel topPanel = new JPanel(new MigLayout("gap 0", "[]8[]"));

		JButton importAllGeos = new JButton("Import All");
		importAllGeos.addActionListener(e -> importAllGeos(true));
		topPanel.add(importAllGeos);

		JButton uncheckAllGeos = new JButton("Leave All");
		uncheckAllGeos.addActionListener(e -> importAllGeos(false));
		topPanel.add(uncheckAllGeos);
		return topPanel;
	}

	public void setGeoset() {
		currGeoPanel.setCurrentGeosetShell(mht.geoTabs.getSelectedValue());
	}

//	private static void importAllGeos(JTabbedPane geosetTabs, boolean b) {
//		for (int i = 0; i < geosetTabs.getTabCount(); i++) {
//			final GeosetPanel geoPanel = (GeosetPanel) geosetTabs.getComponentAt(i);
//			geoPanel.setSelected(b);
//		}
//	}

	private void importAllGeos(boolean b) {
		for (GeosetShell geosetShell : mht.geoShells) {
			geosetShell.setDoImport(b);
		}
	}
//	public JPanel makeGeosetPanel() {
//		JPanel geosetsPanel = new JPanel(new MigLayout("gap 0", "[grow]8[grow]", "[]8[grow]"));
////		addTab("Geosets", geoIcon, geosetsPanel, "Controls which geosets will be imported.");
//
//		final IterableListModel<Material> materials = new IterableListModel<>();
//		materials.addAll(mht.currentModel.getMaterials());
//		materials.addAll(mht.importModel.getMaterials());
//
//		// A list of all materials available for use during this import, in
//		// the form of a IterableListModel
//
//		final MaterialListCellRenderer materialsRenderer = new MaterialListCellRenderer(mht.currentModel);
//		// All material lists will know which materials come from the
//		// out-of-model source (imported model)
//
//		// Build the geosetTabs list of GeosetPanels
//		for (int i = 0; i < mht.currentModel.getGeosets().size(); i++) {
//			final GeosetPanel geoPanel = new GeosetPanel(false, mht.currentModel, i, materials, materialsRenderer, importPanel);
//
//			mht.geosetTabs.addTab(mht.currentModel.getName() + " " + (i + 1), greenIcon, geoPanel, "Click to modify material data for this geoset.");
//		}
//		for (int i = 0; i < mht.importModel.getGeosets().size(); i++) {
//			final GeosetPanel geoPanel = new GeosetPanel(true, mht.importModel, i, materials, materialsRenderer, importPanel);
//
//			mht.geosetTabs.addTab(mht.importModel.getName() + " " + (i + 1), orangeIcon, geoPanel, "Click to modify importing and material data for this geoset.");
//		}
//
//		JButton importAllGeos = new JButton("Import All");
//		importAllGeos.addActionListener(e -> importAllGeos(mht.geosetTabs, true));
//		geosetsPanel.add(importAllGeos, "cell 0 0, align right");
//
//		JButton uncheckAllGeos = new JButton("Leave All");
//		uncheckAllGeos.addActionListener(e -> importAllGeos(mht.geosetTabs, false));
//		geosetsPanel.add(uncheckAllGeos, "cell 1 0, align left");
//
//		geosetsPanel.add(mht.geosetTabs, "cell 0 1, spanx 2, spany, growx, growy");
//
//		return geosetsPanel;
//	}

}

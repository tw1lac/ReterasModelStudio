package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.model.EditableModel;
import com.hiveworkshop.rms.editor.model.Geoset;
import com.hiveworkshop.rms.editor.model.Material;
import com.hiveworkshop.rms.util.IterableListModel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

class GeosetPanel extends JPanel implements ChangeListener {
	// Geoset/Skin panel for controlling materials and geosets
	IterableListModel<Material> materials;
	JList<Material> materialList;
	JScrollPane materialListPane;
	JCheckBox doImport;
	JLabel geoTitle;
	JLabel materialText;
	EditableModel model;
	Geoset geoset;
	int index;
	boolean isImported;
	MaterialListCellRenderer renderer;
	ImportPanel impPanel;

	GeosetShell currentGeosetShell;

	public GeosetPanel(final boolean imported, // Is this Geoset an imported one, or an original?
	                   final EditableModel model, final int geoIndex, // which geoset is this for? (starts with 0)
	                   final IterableListModel<Material> materials, final MaterialListCellRenderer renderer, ImportPanel importPanel) {
		impPanel = importPanel;
		this.materials = materials;
		this.model = model;
		this.renderer = renderer;
		setLayout(new MigLayout("gap 0"));

		index = geoIndex;
		geoset = model.getGeoset(geoIndex);
		isImported = imported;

		geoTitle = new JLabel(model.getName() + " " + (index + 1));
		geoTitle.setFont(new Font("Arial", Font.BOLD, 26));
		add(geoTitle, "align center, wrap");

		doImport = new JCheckBox("Import this Geoset");
		doImport.setSelected(true);
		if (imported) {
			doImport.addChangeListener(this);
		} else {
			doImport.setEnabled(false);
		}
		add(doImport, "left, wrap");

		// Header for materials list
		materialText = new JLabel("Material:");
		add(materialText, "left, wrap");

		materialList = new JList<>(materials);
		materialList.setCellRenderer(renderer);
		materialList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		materialList.setSelectedValue(geoset.getMaterial(), true);

		materialListPane = new JScrollPane(materialList);
		add(materialListPane, "grow");
	}

	public GeosetPanel(final boolean imported, // Is this Geoset an imported one, or an original?
	                   final EditableModel model, // which geoset is this for? (starts with 0)
	                   final IterableListModel<Material> materials, final MaterialListCellRenderer renderer, ImportPanel importPanel) {
		impPanel = importPanel;
		this.renderer = renderer;
		setLayout(new MigLayout("gap 0"));
		if (model.getGeosets() != null && model.getGeosets().size() > 0) {
			geoset = model.getGeoset(0);
		}

		geoTitle = new JLabel("Geoset Title");
		geoTitle.setFont(new Font("Arial", Font.BOLD, 26));
		add(geoTitle, "align center, wrap");

		doImport = new JCheckBox("Import this Geoset");
		doImport.setSelected(true);
		doImport.addActionListener(e -> setDoImport());
		add(doImport, "left, wrap");
//		if (imported) {
//			doImport.addChangeListener(this);
//		} else {
//			doImport.setEnabled(false);
//		}

		// Header for materials list
		materialText = new JLabel("Material:");
		add(materialText, "left, wrap");

		materialList = new JList<>(materials);
		materialList.setCellRenderer(renderer);
		materialList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
//		materialList.setSelectedValue(geoset.getMaterial(), true);
		materialList.setSelectedIndex(0);
		materialList.addListSelectionListener(e -> setGeoShellMaterial());

		materialListPane = new JScrollPane(materialList);
		add(materialListPane, "grow");
	}

	public GeosetPanel setCurrentGeosetShell(GeosetShell geosetShell) {
		this.currentGeosetShell = geosetShell;
		setGeoTitle();
		materialList.setSelectedValue(currentGeosetShell.getMaterial(), true);
		doImport.setEnabled(currentGeosetShell.isImported);
		return this;
	}

	private void setGeoTitle() {
		if (currentGeosetShell != null) {
			geoTitle.setText(currentGeosetShell.toString());
		}
	}

	public void setDoImport() {
		currentGeosetShell.setDoImport(doImport.isSelected());
	}

	public void setGeoShellMaterial() {
		if (currentGeosetShell != null) {
			currentGeosetShell.setMaterial(materialList.getSelectedValue());
		}
	}

	@Override
	public void paintComponent(final Graphics g) {
//		renderer.setMaterial(geoset.getMaterial());
		renderer.setMaterial(materialList.getSelectedValue());
		super.paintComponent(g);
	}

	public void setSelected(final boolean flag) {
		if (isImported) {
			doImport.setSelected(flag);
		}
	}

	public static void informGeosetVisibility(JTabbedPane geosetAnimTabs, final Geoset g, final boolean flag) {
		for (int i = 0; i < geosetAnimTabs.getTabCount(); i++) {
			final BoneAttachmentPanel geoPanel = (BoneAttachmentPanel) geosetAnimTabs.getComponentAt(i);
			if (geoPanel.geoset == g) {
				geosetAnimTabs.setEnabledAt(i, flag);
			}
		}
	}

	public void setMaterials(EditableModel currentModel, EditableModel importedModel) {
		geoset.setMaterial(getSelectedMaterial());
		if (doImport.isSelected() && (model == importedModel)) {
			currentModel.add(geoset);
			if (geoset.getGeosetAnim() != null) {
				currentModel.add(geoset.getGeosetAnim());
			}
		}
	}

	@Override
	public void stateChanged(final ChangeEvent e) {
		materialText.setEnabled(doImport.isSelected());
		materialList.setEnabled(doImport.isSelected());
		materialListPane.setEnabled(doImport.isSelected());

		informGeosetVisibility(impPanel.mht.geosetAnimTabs, geoset, doImport.isSelected());
	}

	public Material getSelectedMaterial() {
		return materialList.getSelectedValue();
	}
}

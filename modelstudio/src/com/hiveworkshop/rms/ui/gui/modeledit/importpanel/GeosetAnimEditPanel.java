package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.model.EditableModel;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelViewManager;
import com.hiveworkshop.rms.ui.icons.RMSIcons;

import javax.swing.*;

public class GeosetAnimEditPanel {
	public static final ImageIcon greenIcon = RMSIcons.greenIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/Blank_small.png"));
	public static final ImageIcon orangeIcon = RMSIcons.orangeIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/BlankOrange_small.png"));
//	JPanel geosetAnimPanel = new JPanel();
//	JTabbedPane geosetAnimTabs = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);
//
//	ImportPanel importPanel;
//
//	JCheckBox displayParents = new JCheckBox("Display parent names");
//	JButton allMatrOriginal = new JButton("Reset all Matrices");
//	JButton allMatrSameName = new JButton("Set all to available, original names");

	JPanel geosetAnimPanel = new JPanel();
	JTabbedPane geosetAnimTabs = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);
	ImportPanel importPanel;
	JCheckBox displayParents = new JCheckBox("Display parent names");
	JButton allMatrOriginal = new JButton("Reset all Matrices");
	JButton allMatrSameName = new JButton("Set all to available, original names");

	public GeosetAnimEditPanel(JPanel geosetAnimPanel, JTabbedPane geosetAnimTabs, ImportPanel importPanel,
	                           JButton allMatrOriginal, JButton allMatrSameName) {
		this.geosetAnimPanel = geosetAnimPanel;
		this.geosetAnimTabs = geosetAnimTabs;
		this.importPanel = importPanel;
//		this.displayParents = displayParents;
		this.allMatrOriginal = allMatrOriginal;
		this.allMatrSameName = allMatrSameName;
	}

	private static void updateAnimTabs(ImportPanel importPanel) {
		((AnimPanel) importPanel.animTabs.getSelectedComponent()).updateSelectionPicks();
		importPanel.mht.getFutureBoneList();
		importPanel.mht.getFutureBoneListExtended(false);
		importPanel.visibilityList();
		// ((BoneAttachmentPane)geosetAnimTabs.getSelectedComponent()).refreshLists();
		importPanel.repaint();
	}

	private static void allMatrOriginal(JTabbedPane geosetAnimTabs) {
		for (int i = 0; i < geosetAnimTabs.getTabCount(); i++) {
			if (geosetAnimTabs.isEnabledAt(i)) {
				final BoneAttachmentPanel bap = (BoneAttachmentPanel) geosetAnimTabs.getComponentAt(i);
				bap.resetMatrices();
			}
		}
	}

	private static void allMatrSameName(JTabbedPane geosetAnimTabs) {
		for (int i = 0; i < geosetAnimTabs.getTabCount(); i++) {
			if (geosetAnimTabs.isEnabledAt(i)) {
				final BoneAttachmentPanel bap = (BoneAttachmentPanel) geosetAnimTabs.getComponentAt(i);
				bap.setMatricesToSimilarNames();
			}
		}
	}

	public void makeGeosetAnimPanel(EditableModel currentModel, EditableModel importedModel) {

		final ModelViewManager currentModelManager = new ModelViewManager(currentModel);
		final ModelViewManager importedModelManager = new ModelViewManager(importedModel);
		final ParentToggleRenderer ptr = makeMatricesPanle(currentModelManager, importedModelManager);
		for (int i = 0; i < currentModel.getGeosets().size(); i++) {
			final BoneAttachmentPanel geoPanel = new BoneAttachmentPanel(currentModel, currentModel.getGeoset(i), ptr, importPanel);
			String tip = "Click to modify animation data for Geoset " + i + " from " + currentModel.getName() + ".";
			geosetAnimTabs.addTab(currentModel.getName() + " " + (i + 1), greenIcon, geoPanel, tip);
		}
		for (int i = 0; i < importedModel.getGeosets().size(); i++) {
			final BoneAttachmentPanel geoPanel = new BoneAttachmentPanel(importedModel, importedModel.getGeoset(i), ptr, importPanel);
			String tip = "Click to modify animation data for Geoset " + i + " from " + importedModel.getName() + ".";
			geosetAnimTabs.addTab(importedModel.getName() + " " + (i + 1), orangeIcon, geoPanel, tip);
		}
		geosetAnimTabs.addChangeListener(e -> updateAnimTabs(importPanel));

		setLayout();
	}

	public ParentToggleRenderer makeMatricesPanle(ModelViewManager currentModelManager, ModelViewManager importedModelManager) {
//		addTab("Matrices", greenIcon, geosetAnimPanel, "Controls which bones geosets are attached to.");
//		addTab("Skin", orangeIcon, new JPanel(), "Edit SKIN chunk");

		final ParentToggleRenderer ptr = new ParentToggleRenderer(displayParents, currentModelManager, importedModelManager);

		displayParents.addChangeListener(e -> updateAnimTabs(importPanel));

		allMatrOriginal.addActionListener(e -> allMatrOriginal(geosetAnimTabs));
		allMatrSameName.addActionListener(e -> allMatrSameName(geosetAnimTabs));
		return ptr;
	}

	private void setLayout() {
		geosetAnimPanel.add(geosetAnimTabs);
		final GroupLayout gaLayout = new GroupLayout(geosetAnimPanel);
		gaLayout.setVerticalGroup(gaLayout.createSequentialGroup()
				.addComponent(displayParents)
				.addComponent(allMatrOriginal)
				.addComponent(allMatrSameName)
				.addComponent(geosetAnimTabs));
		gaLayout.setHorizontalGroup(gaLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(displayParents)
				.addComponent(allMatrOriginal)
				.addComponent(allMatrSameName)
				.addComponent(geosetAnimTabs));
		geosetAnimPanel.setLayout(gaLayout);
	}
}

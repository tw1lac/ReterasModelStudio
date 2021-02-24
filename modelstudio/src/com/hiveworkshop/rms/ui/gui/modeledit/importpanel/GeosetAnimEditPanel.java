package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.wrapper.v2.ModelViewManager;
import com.hiveworkshop.rms.ui.icons.RMSIcons;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class GeosetAnimEditPanel {
	public static final ImageIcon greenIcon = RMSIcons.greenIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/Blank_small.png"));
	public static final ImageIcon orangeIcon = RMSIcons.orangeIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/BlankOrange_small.png"));


	ImportPanel importPanel;
	JCheckBox displayParents = new JCheckBox("Display parent names");
	JButton allMatrOriginal;
	JButton allMatrSameName;
	private ModelHolderThing mht;

	public GeosetAnimEditPanel(ModelHolderThing mht, ImportPanel importPanel,
	                           JButton allMatrOriginal, JButton allMatrSameName) {
		this.mht = mht;
		this.importPanel = importPanel;
//		this.displayParents = displayParents;
		this.allMatrOriginal = allMatrOriginal;
		this.allMatrSameName = allMatrSameName;
	}

	public JPanel makeGeosetAnimPanel() {
		JPanel geosetAnimPanel = new JPanel(new MigLayout("gap 0, fill", "[grow]", "[][grow]"));

		final ModelViewManager currentModelManager = new ModelViewManager(mht.currentModel);
		final ModelViewManager importedModelManager = new ModelViewManager(mht.importModel);
		final ParentToggleRenderer ptr = makeMatricesPanle(currentModelManager, importedModelManager);
		for (int i = 0; i < mht.currentModel.getGeosets().size(); i++) {
			final BoneAttachmentPanel geoPanel = new BoneAttachmentPanel(mht.currentModel, mht.currentModel.getGeoset(i), ptr, importPanel);
			String tip = "Click to modify animation data for Geoset " + i + " from " + mht.currentModel.getName() + ".";
			mht.geosetAnimTabs.addTab(mht.currentModel.getName() + " " + (i + 1), greenIcon, geoPanel, tip);
		}
		for (int i = 0; i < mht.importModel.getGeosets().size(); i++) {
			final BoneAttachmentPanel geoPanel = new BoneAttachmentPanel(mht.importModel, mht.importModel.getGeoset(i), ptr, importPanel);
			String tip = "Click to modify animation data for Geoset " + i + " from " + mht.importModel.getName() + ".";
			mht.geosetAnimTabs.addTab(mht.importModel.getName() + " " + (i + 1), orangeIcon, geoPanel, tip);
		}
		mht.geosetAnimTabs.addChangeListener(e -> updateAnimTabs(importPanel));

		JPanel topPanel = new JPanel(new MigLayout("gap 0", "[align center]"));
		topPanel.add(displayParents, "wrap");
		topPanel.add(allMatrOriginal, "wrap");
		topPanel.add(allMatrSameName, "wrap");
		geosetAnimPanel.add(topPanel, "align center, wrap");
		geosetAnimPanel.add(mht.geosetAnimTabs, "growx, growy");

		return geosetAnimPanel;
	}

	private void updateAnimTabs(ImportPanel importPanel) {
		//should this edit animShells or GeosetAnimShells..?
//		((AnimPanel) mht.animTabs.getSelectedComponent()).updateSelectionPicks();
		((AnimPanel) mht.geosetAnimTabs.getSelectedComponent()).updateSelectionPicks();
		mht.getFutureBoneList();
		mht.getFutureBoneListExtended(false);
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

	public ParentToggleRenderer makeMatricesPanle(ModelViewManager currentModelManager, ModelViewManager importedModelManager) {
//		addTab("Matrices", greenIcon, geosetAnimPanel, "Controls which bones geosets are attached to.");
//		addTab("Skin", orangeIcon, new JPanel(), "Edit SKIN chunk");

		final ParentToggleRenderer ptr = new ParentToggleRenderer(displayParents, currentModelManager, importedModelManager);

		displayParents.addChangeListener(e -> updateAnimTabs(importPanel));

		allMatrOriginal.addActionListener(e -> allMatrOriginal(mht.geosetAnimTabs));
		allMatrSameName.addActionListener(e -> allMatrSameName(mht.geosetAnimTabs));
		return ptr;
	}

	private void setLayout(JPanel geosetAnimPanel) {
		geosetAnimPanel.add(mht.geosetAnimTabs);
		final GroupLayout gaLayout = new GroupLayout(geosetAnimPanel);
		gaLayout.setVerticalGroup(gaLayout.createSequentialGroup()
				.addComponent(displayParents)
				.addComponent(allMatrOriginal)
				.addComponent(allMatrSameName)
				.addComponent(mht.geosetAnimTabs));
		gaLayout.setHorizontalGroup(gaLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(displayParents)
				.addComponent(allMatrOriginal)
				.addComponent(allMatrSameName)
				.addComponent(mht.geosetAnimTabs));
		geosetAnimPanel.setLayout(gaLayout);
	}
}

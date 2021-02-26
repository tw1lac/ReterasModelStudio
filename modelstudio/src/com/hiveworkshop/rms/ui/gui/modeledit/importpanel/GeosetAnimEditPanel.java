package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.model.Bone;
import com.hiveworkshop.rms.editor.model.EditableModel;
import com.hiveworkshop.rms.editor.model.Geoset;
import com.hiveworkshop.rms.editor.model.Matrix;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelViewManager;
import com.hiveworkshop.rms.ui.gui.modeledit.BoneShell;
import com.hiveworkshop.rms.ui.gui.modeledit.MatrixShell;
import com.hiveworkshop.rms.ui.icons.RMSIcons;
import com.hiveworkshop.rms.util.IterableListModel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.ArrayList;

public class GeosetAnimEditPanel {
	public static final ImageIcon greenIcon = RMSIcons.greenIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/Blank_small.png"));
	public static final ImageIcon orangeIcon = RMSIcons.orangeIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/BlankOrange_small.png"));


	ImportPanel importPanel;
	JCheckBox displayParents = new JCheckBox("Display parent names");
	private ModelHolderThing mht;

	JButton allMatrOriginal = new JButton("Reset all Matrices");
	JButton allMatrSameName = new JButton("Set all to available, original names");

	BoneAttachmentPanel currBAP;

	public GeosetAnimEditPanel(ModelHolderThing mht, ImportPanel importPanel) {
		this.mht = mht;
		this.importPanel = importPanel;
	}

//	public JPanel makeGeosetAnimPanel() {
//		JPanel geosetAnimPanel = new JPanel(new MigLayout("gap 0, fill", "[grow]", "[][grow]"));
//
//		final ModelViewManager currentModelManager = new ModelViewManager(mht.currentModel);
//		final ModelViewManager importedModelManager = new ModelViewManager(mht.importModel);
//
//		final ParentToggleRenderer ptr = makeMatricesPanel(currentModelManager, importedModelManager);
//		for (int i = 0; i < mht.currentModel.getGeosets().size(); i++) {
//			final BoneAttachmentPanel geoPanel = new BoneAttachmentPanel(mht, mht.currentModel, mht.currentModel.getGeoset(i), ptr);
//			String tip = "Click to modify animation data for Geoset " + i + " from " + mht.currentModel.getName() + ".";
//			mht.geosetAnimTabs.addTab(mht.currentModel.getName() + " " + (i + 1), greenIcon, geoPanel, tip);
//		}
//
//		for (int i = 0; i < mht.importModel.getGeosets().size(); i++) {
//			final BoneAttachmentPanel geoPanel = new BoneAttachmentPanel(mht, mht.importModel, mht.importModel.getGeoset(i), ptr);
//			String tip = "Click to modify animation data for Geoset " + i + " from " + mht.importModel.getName() + ".";
//			mht.geosetAnimTabs.addTab(mht.importModel.getName() + " " + (i + 1), orangeIcon, geoPanel, tip);
//		}
//		mht.geosetAnimTabs.addChangeListener(e -> updateAnimTabs(importPanel));
//
//		JPanel topPanel = getTopPanel();
//
//		geosetAnimPanel.add(topPanel, "align center, wrap");
//		geosetAnimPanel.add(mht.geosetAnimTabs, "growx, growy");
//
//		return geosetAnimPanel;
//	}

	public JPanel makeGeosetAnimPanel1() {
		JPanel geosetAnimPanel = new JPanel(new MigLayout("gap 0, fill", "[grow]", "[][grow]"));

		final ModelViewManager currentModelManager = new ModelViewManager(mht.receivingModel);
		final ModelViewManager importedModelManager = new ModelViewManager(mht.donatingModel);

		final ParentToggleRenderer ptr = makeMatricesPanel(currentModelManager, importedModelManager);
//		for (int i = 0; i < mht.currentModel.getGeosets().size(); i++) {
//			final BoneAttachmentPanel geoPanel = new BoneAttachmentPanel(mht, mht.currentModel, mht.currentModel.getGeoset(i), ptr);
//			String tip = "Click to modify animation data for Geoset " + i + " from " + mht.currentModel.getName() + ".";
//			mht.geosetAnimTabs.addTab(mht.currentModel.getName() + " " + (i + 1), greenIcon, geoPanel, tip);
//		}
		JPanel bigPanel = new JPanel(new MigLayout("gap 0, fill", "[][grow]", "[grow]"));
		JScrollPane bAPScrollPane = new JScrollPane(mht.bAPTabs);
		bigPanel.add(bAPScrollPane, "growy");

		IterableListModel<BoneShell> boneShellList = new IterableListModel<>();
		boneShellList.addAll(mht.existingBones);
		System.out.println("existing bones: " + mht.existingBones.size());
//		List<BoneShell> boneShellList = new ArrayList<>();
//		boneShellList.addAll(mht.oldBoneShellList);
//		boneShellList.addAll(mht.newBonesShellList);

		currBAP = new BoneAttachmentPanel(mht, mht.receivingModel, ptr);

		createBoneAttShells(boneShellList, mht.receivingModel, false);

		createBoneAttShells(boneShellList, mht.donatingModel, true);

		bigPanel.add(currBAP, "growx, growy");
		mht.bAPTabs.addListSelectionListener(e -> updateAnimTabs());
//		mht.geosetAnimTabs.addChangeListener(e -> updateAnimTabs(importPanel));

		geosetAnimPanel.add(getTopPanel(), "align center, wrap");
		geosetAnimPanel.add(bigPanel, "growx, growy");
//		geosetAnimPanel.add(mht.geosetAnimTabs, "growx, growy");

		return geosetAnimPanel;
	}

	public void createBoneAttShells(IterableListModel<BoneShell> boneShellList, EditableModel model, boolean b) {
		for (Geoset geoset : model.getGeosets()) {
			IterableListModel<MatrixShell> matrixShells = new IterableListModel<>();
			for (Matrix matrix : geoset.getMatrix()) {
				ArrayList<BoneShell> orgBones = new ArrayList<>();
				for (Bone bone : matrix.getBones()) {
					orgBones.add(mht.boneToShell.get(bone));
				}
				matrixShells.addElement(new MatrixShell(matrix, orgBones));
			}

			BoneAttachmentShell boneAttachmentShell = new BoneAttachmentShell(geoset, model, b, matrixShells);
			boneAttachmentShell.setBones(new IterableListModel<>(boneShellList)).fixBones();
			mht.geoBAPShells.addElement(boneAttachmentShell);
		}
	}

	private JPanel getTopPanel() {
		JPanel topPanel = new JPanel(new MigLayout("gap 0", "[align center]"));
		topPanel.add(displayParents, "wrap");
		topPanel.add(allMatrOriginal, "wrap");
		topPanel.add(allMatrSameName, "wrap");
		return topPanel;
	}

	private void updateAnimTabs(ImportPanel importPanel) {
		//should this edit animShells or GeosetAnimShells..?
//		((AnimPanel) mht.animTabs.getSelectedComponent()).updateSelectionPicks();
//		((AnimPanel) mht.geosetAnimTabs.getSelectedComponent()).updateSelectionPicks();
		mht.getFutureBoneList();
		mht.getFutureBoneListExtended(false);
		importPanel.visibilityList();
		// ((BoneAttachmentPane)geosetAnimTabs.getSelectedComponent()).refreshLists();
		importPanel.repaint();
	}

	private void updateAnimTabs() {
		//should this edit animShells or GeosetAnimShells..?
//		((AnimPanel) mht.animTabs.getSelectedComponent()).updateSelectionPicks();
//		((AnimPanel) mht.geosetAnimTabs.getSelectedComponent()).updateSelectionPicks();
		currBAP.setCurrentBAP(mht.bAPTabs.getSelectedValue());
		mht.getFutureBoneList();
		mht.getFutureBoneListExtended(false);
		importPanel.visibilityList();
		// ((BoneAttachmentPane)geosetAnimTabs.getSelectedComponent()).refreshLists();
		importPanel.repaint();
	}

	private void allMatrOriginal() {
		for (BoneAttachmentShell bas : mht.geoBAPShells) {
			if (bas.isDoImport()) {
				for (MatrixShell ms : bas.getOldBoneRefs()) {
					ms.resetMatrix();
				}
			}
		}
	}

	private void allMatrSameName() {
		for (BoneAttachmentShell bas : mht.geoBAPShells) {
			if (bas.isDoImport()) {
				for (MatrixShell ms : bas.getOldBoneRefs()) {
					ms.clearNewBones();
					final Matrix m = ms.getMatrix();
					// For look to find similarly named stuff and add it
					for (final BoneShell bs : mht.futureBoneList) {
						for (final Bone b : m.getBones()) {
							final String mName = b.getName();
							if (bs.getBone().getName().equals(mName)) {
								ms.addNewBone(bs);
							}
						}
					}
				}
			}
		}
	}
//
//	private static void allMatrOriginal(JTabbedPane geosetAnimTabs) {
//		for (int i = 0; i < geosetAnimTabs.getTabCount(); i++) {
//			if (geosetAnimTabs.isEnabledAt(i)) {
//				final BoneAttachmentPanel bap = (BoneAttachmentPanel) geosetAnimTabs.getComponentAt(i);
//				bap.resetMatrices();
//			}
//		}
//	}
//
//	private static void allMatrSameName(JTabbedPane geosetAnimTabs) {
//		for (int i = 0; i < geosetAnimTabs.getTabCount(); i++) {
//			if (geosetAnimTabs.isEnabledAt(i)) {
//				final BoneAttachmentPanel bap = (BoneAttachmentPanel) geosetAnimTabs.getComponentAt(i);
//				bap.setMatricesToSimilarNames();
//			}
//		}
//	}

	public ParentToggleRenderer makeMatricesPanel(ModelViewManager currentModelManager, ModelViewManager importedModelManager) {
//		addTab("Matrices", greenIcon, geosetAnimPanel, "Controls which bones geosets are attached to.");
//		addTab("Skin", orangeIcon, new JPanel(), "Edit SKIN chunk");

		final ParentToggleRenderer ptr = new ParentToggleRenderer(displayParents, currentModelManager, importedModelManager);

		displayParents.addChangeListener(e -> updateAnimTabs(importPanel));

		allMatrOriginal.addActionListener(e -> allMatrOriginal());
		allMatrSameName.addActionListener(e -> allMatrSameName());
		return ptr;
	}
}

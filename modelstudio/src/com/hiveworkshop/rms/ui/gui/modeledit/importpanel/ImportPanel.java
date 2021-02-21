package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.model.*;
import com.hiveworkshop.rms.editor.model.animflag.AnimFlag;
import com.hiveworkshop.rms.editor.model.animflag.FloatAnimFlag;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelViewManager;
import com.hiveworkshop.rms.ui.application.edit.ModelStructureChangeListener;
import com.hiveworkshop.rms.ui.gui.modeledit.BoneShell;
import com.hiveworkshop.rms.ui.icons.RMSIcons;
import com.hiveworkshop.rms.ui.util.ExceptionPopup;
import com.hiveworkshop.rms.util.IterableListModel;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The panel to handle the import function.
 *
 * Eric Theller 6/11/2012
 */
public class ImportPanel extends JTabbedPane implements ChangeListener {
	public static final ImageIcon animIcon = RMSIcons.animIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/anim_small.png"));
	public static final ImageIcon boneIcon = RMSIcons.boneIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/Bone_small.png"));
	public static final ImageIcon geoIcon = RMSIcons.geoIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/geo_small.png"));
	public static final ImageIcon objIcon = RMSIcons.objIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/Obj_small.png"));
	public static final ImageIcon greenIcon = RMSIcons.greenIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/Blank_small.png"));
	public static final ImageIcon redIcon = RMSIcons.redIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/BlankRed_small.png"));
	public static final ImageIcon orangeIcon = RMSIcons.orangeIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/BlankOrange_small.png"));
	public static final ImageIcon cyanIcon = RMSIcons.cyanIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/BlankCyan_small.png"));
	public static final ImageIcon redXIcon = RMSIcons.redXIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/redX.png"));
	public static final ImageIcon greenArrowIcon = RMSIcons.greenArrowIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/greenArrow.png"));
	public static final ImageIcon moveUpIcon = RMSIcons.moveUpIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/moveUp.png"));
	public static final ImageIcon moveDownIcon = RMSIcons.moveDownIcon;// new ImageIcon(ImportPanel.class.getClassLoader().getResource("ImageBin/moveDown.png"));

	JFrame frame;

	// Geosets
	JTabbedPane geosetTabs = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);


	public final BoneShellListCellRenderer boneShellRenderer;
	JCheckBox clearExistingAnims = new JCheckBox("Clear pre-existing animations");
	// Animation
	JTabbedPane animTabs = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);

	// Bones
//	IterableListModel<BonePanel> bonePanels = new IterableListModel<>();
//	JList<BonePanel> boneTabs = new JList<>(bonePanels);

	// Matrices
	JPanel geosetAnimPanel = new JPanel();
	JTabbedPane geosetAnimTabs = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);

	JButton allMatrOriginal = new JButton("Reset all Matrices");
	JButton allMatrSameName = new JButton("Set all to available, original names");
	IterableListModel<AnimShell> existingAnims = new IterableListModel<>();
	// Objects
	IterableListModel<ObjectPanel> objectPanels = new IterableListModel<>();
	JList<ObjectPanel> objectTabs = new JList<>(objectPanels);

	// Visibility
	JList<VisibilityPanel> visTabs = new JList<>();


	IterableListModel<VisibilityPanel> visibilityPanels = new IterableListModel<>();
	ArrayList<VisibilityPanel> allVisShellPanes = new ArrayList<>();

	ArrayList<Object> visSourcesNew;

	ModelHolderThing mht;

	private ModelStructureChangeListener callback;
	boolean importSuccess = false;
	boolean importStarted = false;
	boolean importEnded = false;

	public ImportPanel(final EditableModel a, final EditableModel b) {
		this(a, b, true);
	}


	public ImportPanel(final EditableModel currentModel1, final EditableModel importedModel1, final boolean visibleOnStart) {
		super();
		mht = new ModelHolderThing(currentModel1, importedModel1);
		if (mht.currentModel.getName().equals(mht.importModel.getName())) {
			mht.importModel.setFileRef(new File(mht.importModel.getFile().getParent() + "/" + mht.importModel.getName() + " (Imported)" + ".mdl"));
			frame = new JFrame("Importing " + mht.currentModel.getName() + " into itself");
		} else {
			frame = new JFrame("Importing " + mht.importModel.getName() + " into " + mht.currentModel.getName());
		}
		mht.currentModel.doSavePreps();
		try {
			frame.setIconImage(RMSIcons.MDLIcon.getImage());
		} catch (final Exception e) {
			JOptionPane.showMessageDialog(null, "Error: Image files were not found! Due to bad programming, this might break the program!");
		}
//		this.currentModel = currentModel;
//		this.importedModel = importedModel;
		final ModelViewManager currentModelManager = new ModelViewManager(mht.currentModel);
		final ModelViewManager importedModelManager = new ModelViewManager(mht.importModel);

		// Geoset Panel
		GeosetEditPanels geosetEditPanels = new GeosetEditPanels(geosetTabs, this);
		addTab("Geosets", geoIcon, geosetEditPanels.makeGeosetPanel(mht.currentModel, mht.importModel), "Controls which geosets will be imported.");
		System.out.println("Geosets");

		// Animation Panel
		AnimEditPanel animEditPanel = new AnimEditPanel(animTabs, existingAnims, clearExistingAnims);
		addTab("Animation", animIcon, animEditPanel.makeAnimationPanel(mht.currentModel, mht.importModel), "Controls which animations will be imported.");
		System.out.println("Animation");

		// Bone Panel
		boneShellRenderer = new BoneShellListCellRenderer(currentModelManager, importedModelManager);
		final BonePanelListCellRenderer bonePanelRenderer = new BonePanelListCellRenderer(currentModelManager, importedModelManager);

		BonesEditPanel bonesEditPanel = new BonesEditPanel(mht, objectPanels, boneShellRenderer, geosetAnimTabs, this);

		addTab("Bones", boneIcon, bonesEditPanel.makeBonePanel(bonePanelRenderer), "Controls which bones will be imported.");

		System.out.println("Bones");

		// Matrices Panel + Build the geosetAnimTabs list of GeosetPanels
		GeosetAnimEditPanel geosetAnimEditPanel = new GeosetAnimEditPanel(geosetAnimPanel, geosetAnimTabs, this, allMatrOriginal, allMatrSameName);
		geosetAnimEditPanel.makeGeosetAnimPanel(mht.currentModel, mht.importModel);

		addTab("Matrices", greenIcon, geosetAnimPanel, "Controls which bones geosets are attached to.");

		// Objects Panel
		ObjectsEditPanel objectsEditPanel = new ObjectsEditPanel(this, objectPanels, objectTabs);


		addTab("Objects", objIcon, objectsEditPanel.makeObjecsPanel(mht.importModel), "Controls which objects are imported.");

//		makeObjecsPanel(importedModel);

		// Visibility Panel
		VisibilityEditPanel visibilityEditPanel = new VisibilityEditPanel(
				visTabs, mht.currentModel, mht.importModel,
				visibilityPanels, allVisShellPanes, allMatrOriginal,
				allMatrSameName, visSourcesNew,
				objectPanels, objectTabs, geosetTabs);
		addTab("Visibility", orangeIcon, visibilityEditPanel.makeVisPanel(mht.currentModel), "Controls the visibility of portions of the model.");

		// Listen all
		addChangeListener(e -> updateAnimTabs(this));

		JPanel footerPanel = getFooterPanel();

		setPreferredSize(new Dimension(1024, 780));

		final JPanel containerPanel = new JPanel(new MigLayout("gap 0, fill", "[grow]", "[grow]"));
		containerPanel.add(this, "wrap");
		containerPanel.add(footerPanel);
		frame.setContentPane(containerPanel);

		frame.setBounds(0, 0, 1024, 780);
		frame.setLocationRelativeTo(null);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				cancelImport(ImportPanel.this);
			}
		});
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		frame.setVisible(visibleOnStart);
		frame.pack();
	}


	private static void uncheckAllAnims(JTabbedPane animTabs, boolean b) {
		for (int i = 0; i < animTabs.getTabCount(); i++) {
			final AnimPanel aniPanel = (AnimPanel) animTabs.getComponentAt(i);
			aniPanel.setSelected(b);
		}
	}

	private static void selSimButton(ArrayList<VisibilityPanel> allVisShellPanes) {
		for (final VisibilityPanel vPanel : allVisShellPanes) {
			vPanel.selectSimilarOptions();
		}
	}

	private static void cancelImport(ImportPanel importPanel) {
		final Object[] options = {"Yes", "No"};
		final int n = JOptionPane.showOptionDialog(importPanel.frame, "Really cancel this import?", "Confirmation",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
		if (n == 0) {
			importPanel.frame.setVisible(false);
			importPanel.frame = null;
		}
	}

	/**
	 * public void addAnimPicks(Object [] whichAnims, AnimPanel source) { for( int i
	 * = 0; i < animTabs.getTabCount(); i++ ) { AnimPanel aniPanel =
	 * (AnimPanel)animTabs.getComponentAt(i); if( aniPanel != source ) { for( Object
	 * o: whichAnims ) { if( !aniPanel.existingAnims.contains(o) ) {
	 * aniPanel.listenSelection = false; aniPanel.existingAnims.addElement(o);
	 * aniPanel.listenSelection = true; //
	 * System.out.println(animTabs.getTitleAt(i)+" gained "
	 * +((Animation)o).getName()); } } aniPanel.reorderToModel(existingAnims); } } }
	 * public void removeAnimPicks(Object [] whichAnims, AnimPanel source) { for(
	 * int i = 0; i < animTabs.getTabCount(); i++ ) { AnimPanel aniPanel =
	 * (AnimPanel)animTabs.getComponentAt(i); if( aniPanel != source ) { for( Object
	 * o: whichAnims ) { aniPanel.listenSelection = false;
	 * aniPanel.existingAnims.removeElement(o); aniPanel.listenSelection = true; //
	 * System.out.println(animTabs.getTitleAt(i)+" lost "
	 * +((Animation)o).getName()); } } } } public void reorderAnimPicks(AnimPanel
	 * source) { for( int i = 0; i < animTabs.getTabCount(); i++ ) { AnimPanel
	 * aniPanel = (AnimPanel)animTabs.getComponentAt(i); if( aniPanel != source ) {
	 * aniPanel.reorderToModel(existingAnims); } } } public void addBonePicks(Object
	 * [] whichBones, BonePanel source) { for( int i = 0; i < bonePanels.size(); i++
	 * ) { BonePanel bonePanel = bonePanels.get(i); if( bonePanel != source ) { for(
	 * Object o: whichBones ) { if( !bonePanel.existingBones.contains(o) ) {
	 * bonePanel.listenSelection = false; bonePanel.existingBones.addElement(o);
	 * bonePanel.listenSelection = true; //
	 * System.out.println(animTabs.getTitleAt(i)+" gained "
	 * +((Animation)o).getName()); } } bonePanel.reorderToModel(existingBones); } }
	 * } public void removeBonePicks(Object [] whichBones, BonePanel source) { for(
	 * int i = 0; i < bonePanels.size(); i++ ) { BonePanel bonePanel =
	 * bonePanels.get(i); if( bonePanel != source ) { for( Object o: whichBones ) {
	 * bonePanel.listenSelection = false; bonePanel.existingBones.removeElement(o);
	 * bonePanel.listenSelection = true; //
	 * System.out.println(animTabs.getTitleAt(i)+" lost "
	 * +((Animation)o).getName()); } } } } public void reorderBonePicks(BonePanel
	 * source) { for( int i = 0; i < bonePanels.size(); i++ ) { BonePanel bonePanel
	 * = bonePanels.get(i); if( bonePanel != source ) {
	 * bonePanel.reorderToModel(existingBones); } } }
	 **/

	public static VisibilityPanel visPaneFromObject(ArrayList<VisibilityPanel> allVisShellPanes, final Named o) {
		for (final VisibilityPanel vp : allVisShellPanes) {
			if (vp.sourceShell.source == o) {
				return vp;
			}
		}
		return null;
	}


	/**
	 * Provides a Runnable to the ImportPanel that will be run after the import has
	 * ended successfully.
	 */
	public void setCallback(final ModelStructureChangeListener callback) {
		this.callback = callback;
	}


	private static void updateAnimTabs(ImportPanel importPanel) {
		((AnimPanel) importPanel.animTabs.getSelectedComponent()).updateSelectionPicks();
		importPanel.mht.getFutureBoneList();
		importPanel.mht.getFutureBoneListExtended(false);
		importPanel.visibilityList();
		// ((BoneAttachmentPane)geosetAnimTabs.getSelectedComponent()).refreshLists();
		importPanel.repaint();
	}

	private static void clearSelectedBones(IterableListModel<BoneShell> existingBones, ModelHolderThing modelHolderThing) {
		for (int i = 0; i < existingBones.size(); i++) {
			final BoneShell bs = existingBones.get(i);
			if (bs.importBone != null && modelHolderThing.shouldImportBone(bs.importBone)) {
				bs.bone.copyMotionFrom(bs.importBone);
			}
		}
	}

	private static void importAllGeos(JTabbedPane geosetTabs, boolean b) {
		for (int i = 0; i < geosetTabs.getTabCount(); i++) {
			final GeosetPanel geoPanel = (GeosetPanel) geosetTabs.getComponentAt(i);
			geoPanel.setSelected(b);
		}
	}

	@Override
	public void stateChanged(final ChangeEvent e) {
		updateAnimTabs(this);
	}

	private static void importAllBones(IterableListModel<BoneShell> boneShells, int selectionIndex) {
		for (final BoneShell boneShell : boneShells) {
			boneShell.importStatus = selectionIndex;
		}
	}

	private static void importAllObjs(IterableListModel<ObjectPanel> objectPanels, boolean b) {
		for (final ObjectPanel objectPanel : objectPanels) {
			objectPanel.doImport.setSelected(b);
		}
	}

	private static void applyImport(ImportPanel importPanel) {
		importPanel.doImport();
		importPanel.frame.setVisible(false);
	}

	private JPanel getFooterPanel() {
		JPanel footerPanel = new JPanel(new MigLayout("gap 0", "[grow, left]8[grow, right]"));

		JButton okayButton = new JButton("Finish");
		okayButton.addActionListener(e -> applyImport(this));
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> cancelImport(this));

		footerPanel.add(okayButton);
		footerPanel.add(cancelButton);
		return footerPanel;
	}

	public IterableListModel<VisibilityPanel> visibilityList() {
		final Object selection = visTabs.getSelectedValue();
		visibilityPanels.clear();
		for (int i = 0; i < geosetTabs.getTabCount(); i++) {
			final GeosetPanel gp = (GeosetPanel) geosetTabs.getComponentAt(i);
			for (final Layer l : gp.getSelectedMaterial().getLayers()) {
				final VisibilityPanel vs = visPaneFromObject(allVisShellPanes, l);
				if (!visibilityPanels.contains(vs) && (vs != null)) {
					visibilityPanels.addElement(vs);
				}
			}
		}
		for (int i = 0; i < geosetTabs.getTabCount(); i++) {
			final GeosetPanel gp = (GeosetPanel) geosetTabs.getComponentAt(i);
			if (gp.doImport.isSelected()) {
				final Geoset ga = gp.geoset;
				final VisibilityPanel vs = visPaneFromObject(allVisShellPanes, ga);
				if (!visibilityPanels.contains(vs) && (vs != null)) {
					visibilityPanels.addElement(vs);
				}
			}
		}
		// The current's

		final EditableModel model = mht.currentModel;
		for (IdObject idObject : model.getIdObjects()) {
			if (!(idObject instanceof Bone) && !(idObject instanceof EventObject) && !(idObject instanceof CollisionShape)) {
				final VisibilityPanel vs = visPaneFromObject(allVisShellPanes, idObject);
				if (!visibilityPanels.contains(vs) && (vs != null)) {
					visibilityPanels.addElement(vs);
				}
			}
		}
		for (final Named x : model.getLights()) {
			final VisibilityPanel vs = visPaneFromObject(allVisShellPanes, x);
			if (!visibilityPanels.contains(vs) && (vs != null)) {
				visibilityPanels.addElement(vs);
			}
		}
		for (final Named x : model.getAttachments()) {
			final VisibilityPanel vs = visPaneFromObject(allVisShellPanes, x);
			if (!visibilityPanels.contains(vs) && (vs != null)) {
				visibilityPanels.addElement(vs);
			}
		}
		for (final Named x : model.getParticleEmitters()) {
			final VisibilityPanel vs = visPaneFromObject(allVisShellPanes, x);
			if (!visibilityPanels.contains(vs) && (vs != null)) {
				visibilityPanels.addElement(vs);
			}
		}
		for (final Named x : model.getParticleEmitter2s()) {
			final VisibilityPanel vs = visPaneFromObject(allVisShellPanes, x);
			if (!visibilityPanels.contains(vs) && (vs != null)) {
				visibilityPanels.addElement(vs);
			}
		}
		for (final Named x : model.getPopcornEmitters()) {
			final VisibilityPanel vs = visPaneFromObject(allVisShellPanes, x);
			if (!visibilityPanels.contains(vs) && (vs != null)) {
				visibilityPanels.addElement(vs);
			}
		}
		for (final Named x : model.getPopcornEmitters()) {
			final VisibilityPanel vs = visPaneFromObject(allVisShellPanes, x);
			if (!visibilityPanels.contains(vs) && (vs != null)) {
				visibilityPanels.addElement(vs);
			}
		}

		for (final ObjectPanel op : objectPanels) {
			if (op.doImport.isSelected() && (op.object != null))
			// we don't touch camera "object" panels (which aren't idobjects)
			{
				final VisibilityPanel vs = visPaneFromObject(allVisShellPanes, op.object);
				if (!visibilityPanels.contains(vs) && (vs != null)) {
					visibilityPanels.addElement(vs);
				}
			}
		}
		visTabs.setSelectedValue(selection, true);
		return visibilityPanels;
	}

	private void getSingelAnimation(Animation pickedAnim) {
		uncheckAllAnims(animTabs, false);
		for (int i = 0; i < animTabs.getTabCount(); i++) {
			final AnimPanel aniPanel = (AnimPanel) animTabs.getComponentAt(i);
			if (aniPanel.anim.getName().equals(pickedAnim.getName())) {
				aniPanel.doImport.setSelected(true);
			}
		}
		clearExistingAnims.doClick();// turn it back off
	}

	public void doImport() {
		importStarted = true;
		try {
			// AFTER WRITING THREE THOUSAND LINES OF INTERFACE, FINALLLLLLLLLLLLYYYYYYYYY
			// The engine for actually performing the model to model import.

			if (mht.currentModel == mht.importModel) {
				JOptionPane.showMessageDialog(null, "The program has confused itself.");
			}

			for (int i = 0; i < geosetTabs.getTabCount(); i++) {
				final GeosetPanel gp = (GeosetPanel) geosetTabs.getComponentAt(i);
				gp.setMaterials(mht.currentModel, mht.importModel);
			}
			// note to self: remember to scale event objects with time

			final List<AnimFlag<?>> newImpFlags = new ArrayList<>();
			final java.util.List<AnimFlag<?>> impFlags = mht.importModel.getAllAnimFlags();
			for (final AnimFlag<?> af : impFlags) {
				if (!af.hasGlobalSeq()) {
					newImpFlags.add(AnimFlag.buildEmptyFrom(af));
				} else {
					newImpFlags.add(AnimFlag.createFromAnimFlag(af));
				}
			}

			final List<EventObject> newImpEventObjs = new ArrayList<>();
			final List<EventObject> impEventObjs = mht.importModel.getEvents();
			for (final Object e : impEventObjs) {
				newImpEventObjs.add(EventObject.buildEmptyFrom((EventObject) e));
			}

			if (clearExistingAnims.isSelected()) {
				clearCurrModelAnims();
			}


			final List<Animation> newAnims = new ArrayList<>();
			for (int i = 0; i < animTabs.getTabCount(); i++) {
				final AnimPanel aniPanel = (AnimPanel) animTabs.getComponentAt(i);
				if (aniPanel.doImport.isSelected()) {
					aniPanel.doImportSelectedAnims(mht.currentModel, mht.importModel, newAnims, impFlags, impEventObjs, newImpFlags, newImpEventObjs);
				}
			}

			if (!clearExistingAnims.isSelected()) {
				clearSelectedCurrModelAnims(newAnims, impFlags, impEventObjs, newImpFlags, newImpEventObjs, mht.clearExistingBones.isSelected());
			}
			// Now, rebuild the old animflags with the new
			for (final AnimFlag<?> af : impFlags) {
				af.setValuesTo(newImpFlags.get(impFlags.indexOf(af)));
			}
			for (final Object e : impEventObjs) {
				((EventObject) e).setValuesTo(newImpEventObjs.get(impEventObjs.indexOf(e)));
			}

			if (mht.clearExistingBones.isSelected()) {
				for (final IdObject o : mht.currentModel.sortedIdObjects(Bone.class)) {
					mht.currentModel.remove(o);
				}
				for (final IdObject o : mht.currentModel.sortedIdObjects(Helper.class)) {
					mht.currentModel.remove(o);
				}
			}

			final List<IdObject> objectsAdded = new ArrayList<>();

			for (BoneShell boneShell : mht.boneShells) {
				if (boneShell.importStatus == 0) {
					mht.currentModel.add(boneShell.bone);
					objectsAdded.add(boneShell.bone);
					boneShell.bone.setParent(boneShell.newParent);
				} else {
					boneShell.bone.setParent(null);
				}
			}

//			for (BonePanel bonePanel : mht.bonePanels) {
//				bonePanel.getSelectedBones(objectsAdded, mht.currentModel);
//			}
			if (!mht.clearExistingBones.isSelected()) {
				clearSelectedBones(mht.existingBones, mht);
			}

//			applyMatrices();
			Bone dummyBone = null;
			for (int i = 0; i < geosetAnimTabs.getTabCount(); i++) {
				if (geosetAnimTabs.isEnabledAt(i)) {
					final BoneAttachmentPanel bap = (BoneAttachmentPanel) geosetAnimTabs.getComponentAt(i);
					dummyBone = bap.attachBones(dummyBone);
//				dummyBone = attachBones(dummyBone, bap);
				}
			}


			mht.currentModel.updateObjectIds();
			for (final Geoset g : mht.currentModel.getGeosets()) {
				g.applyMatricesToVertices(mht.currentModel);
			}

			// Objects!
			final List<Camera> camerasAdded = new ArrayList<>();
			for (final ObjectPanel objectPanel : objectPanels) {
//				addSelectedObjects(objectPanel, objectsAdded, camerasAdded);
				objectPanel.addSelectedObjects(objectsAdded, camerasAdded, mht.currentModel);
			}

			final List<Animation> oldAnims = new ArrayList<>(mht.currentModel.getAnims());
			final List<FloatAnimFlag> finalVisFlags = new ArrayList<>();
			for (int i = 0; i < visibilityPanels.size(); i++) {
				final VisibilityPanel vPanel = visibilityPanels.get(i);
				vPanel.addSelectedVisFlags(oldAnims, newAnims, clearExistingAnims.isSelected(), finalVisFlags, mht.currentModel, mht.importModel);
//				addSelectedVisFlags(vPanel, oldAnims, newAnims, clearAnims, finalVisFlags);
			}

			for (int i = 0; i < visibilityPanels.size(); i++) {
				final VisibilityPanel vPanel = visibilityPanels.get(i);
				final VisibilitySource temp = ((VisibilitySource) vPanel.sourceShell.source);
				final AnimFlag<?> visFlag = finalVisFlags.get(i);// might be null
				if (visFlag.size() > 0) {
					temp.setVisibilityFlag(visFlag);
				} else {
					temp.setVisibilityFlag(null);
				}
			}

			importSuccess = true;

			// TODO This is broken now, should fix it
			// Tell program to set visibility after import
			// MDLDisplay display = MainFrame.panel.displayFor(currentModel);
			// if( display != null )
			// {
			// display.setBeenSaved(false); // we edited the model
			// for( int i = 0; i < geosetTabs.getTabCount(); i++ )
			// {
			// GeosetPanel gp = (GeosetPanel)geosetTabs.getComponentAt(i);
			// if( gp.doImport.isSelected() && gp.model == importedModel )
			// {
			// display.makeGeosetEditable(gp.geoset, true);
			// display.makeGeosetVisible(gp.geoset, true);
			// }
			// }
			// MainFrame.panel.geoControl.repaint();
			// MainFrame.panel.geoControl.setMDLDisplay(display);
			// display.reloadTextures();//.mpanel.perspArea.reloadTextures();//addGeosets(newGeosets);
			// }
			final List<Geoset> geosetsAdded = new ArrayList<>();
			for (int i = 0; i < geosetTabs.getTabCount(); i++) {
				final GeosetPanel gp = (GeosetPanel) geosetTabs.getComponentAt(i);
				if (gp.doImport.isSelected() && (gp.model == mht.importModel)) {
					geosetsAdded.add(gp.geoset);
				}
			}
			if (callback != null) {
				callback.geosetsAdded(geosetsAdded);
				callback.nodesAdded(objectsAdded);
				callback.camerasAdded(camerasAdded);
			}
			for (final AnimFlag<?> flag : mht.currentModel.getAllAnimFlags()) {
				flag.sort();
			}
		} catch (final Exception e) {
			e.printStackTrace();
			ExceptionPopup.display(e);
		}

		importEnded = true;
	}

	private void clearCurrModelAnims() {
		final List<AnimFlag<?>> curFlags = mht.currentModel.getAllAnimFlags();
		final List<EventObject> curEventObjs = mht.currentModel.getEvents();
		for (final Animation anim : mht.currentModel.getAnims()) {
			anim.clearData(curFlags, curEventObjs);
		}
		mht.currentModel.clearAnimations();
	}

	private void clearSelectedCurrModelAnims(List<Animation> newAnims, List<AnimFlag<?>> impFlags, List<EventObject> impEventObjs, List<AnimFlag<?>> newImpFlags, List<EventObject> newImpEventObjs, boolean clearBones) {
		for (AnimShell animShell : existingAnims) {

			if (animShell.importAnim != null) {
				animShell.importAnim.copyToInterval(animShell.anim.getStart(), animShell.anim.getEnd(), impFlags, impEventObjs, newImpFlags, newImpEventObjs);

				final Animation tempAnim = new Animation("temp", animShell.anim.getStart(), animShell.anim.getEnd());
				newAnims.add(tempAnim);

				if (!clearBones) {

					for (BoneShell bs : mht.existingBones) {
						if (bs.importBone != null) {
							if (mht.boneToPanel.get(bs.importBone).importTypeBox.getSelectedIndex() == 1) {
								System.out.println(
										"Attempting to clear animation for " + bs.bone.getName() + " values "
												+ animShell.anim.getStart() + ", " + animShell.anim.getEnd());
								bs.bone.clearAnimation(animShell.anim);
							}
						}
					}
				}
			}
		}
	}

	// *********************Simple Import Functions****************
	public void animTransfer(final boolean singleAnimation, final Animation pickedAnim, final boolean show) {
		importAllGeos(geosetTabs, false);
		importAllBones(mht.boneShells, 1);
		clearExistingAnims.doClick();
		importAllObjs(objectPanels, false);
		visibilityList();
		selSimButton(allVisShellPanes);

		if (singleAnimation) {
			getSingelAnimation(pickedAnim);
		}

		VisibilityShell corpseShell = null;
		// Try assuming it's a unit with a corpse; they'll tend to be that way

		// Iterate through new visibility sources, find a geoset with gutz material
		for (int i = 0; (i < visSourcesNew.size()) && (corpseShell == null); i++) {
			if (visSourcesNew.get(i) instanceof VisibilityShell) {
				VisibilityShell vs = (VisibilityShell) visSourcesNew.get(i);
				if (vs.source instanceof Geoset) {
					final Geoset g = (Geoset) vs.source;
					if ((g.getGeosetAnim() != null) && g.getMaterial().firstLayer().firstTexture().getPath().equalsIgnoreCase("textures\\gutz.blp")) {
						corpseShell = vs;
					}
				}
			}
		}
		if (corpseShell != null) {
			for (VisibilityPanel vp : visibilityPanels) {
				VisibilityShell vs = vp.sourceShell;
				if (vs.source instanceof Geoset) {
					final Geoset g = (Geoset) vp.sourceShell.source;
					if ((g.getGeosetAnim() != null) && g.getMaterial().firstLayer().firstTexture().getPath().equalsIgnoreCase("textures\\gutz.blp")) {
						vp.newSourcesBox.setSelectedItem(corpseShell);
					}
				}
			}
		}

		if (!show) {
			applyImport(this);
		}

	}

	// *********************Simple Import Functions****************
	public void animTransferPartTwo(final boolean singleAnimation, final Animation pickedAnim,
	                                final Animation visFromAnim, final boolean show) {
		// This should be an import from self
		importAllGeos(geosetTabs, false);
		uncheckAllAnims(animTabs, false);
		importAllBones(mht.boneShells, 2);
		importAllObjs(objectPanels, false);
		visibilityList();
		selSimButton(allVisShellPanes);

		if (singleAnimation) {
//			transfereSingleAnimation(pickedAnim, visFromAnim);
			for (int i = 0; i < animTabs.getTabCount(); i++) {
				final AnimPanel aniPanel = (AnimPanel) animTabs.getComponentAt(i);
				aniPanel.transferSingleAnimation(pickedAnim, visFromAnim);
			}
		} else {
			JOptionPane.showMessageDialog(null, "Bug in anim transfer: attempted unnecessary 2-part transfer");
		}
		for (int i = 0; i < visibilityPanels.getSize(); i++) {
			final VisibilityPanel vp = visibilityPanels.get(i);
			vp.favorOld.doClick();
		}

		if (!show) {
			applyImport(this);
		}
	}

	public boolean importSuccessful() {
		return importSuccess;
	}

//	private static void importAllBones(IterableListModel<BonePanel> bonePanels, int selsctionIndex) {
//		for (final BonePanel bonePanel : bonePanels) {
//			bonePanel.setSelectedIndex(selsctionIndex);
//		}
//	}

	public boolean importStarted() {
		return importStarted;
	}

	public boolean importEnded() {
		return importEnded;
	}

	public JFrame getParentFrame() {
		return frame;
	}
}


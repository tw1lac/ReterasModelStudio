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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
//	JTabbedPane geosetTabs = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);


	public final BoneShellListCellRenderer boneShellRenderer;
//	JCheckBox clearExistingAnims = new JCheckBox("Clear pre-existing animations");
	// Animation
//	JTabbedPane animTabs = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);

	// Bones
//	IterableListModel<BonePanel> bonePanels = new IterableListModel<>();
//	JList<BonePanel> boneTabs = new JList<>(bonePanels);

	// Matrices
//	JPanel geosetAnimPanel = new JPanel();
//	JTabbedPane geosetAnimTabs = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);

	JButton allMatrOriginal = new JButton("Reset all Matrices");
	JButton allMatrSameName = new JButton("Set all to available, original names");
//	IterableListModel<AnimShell> existingAnims = new IterableListModel<>();
	// Objects
//	IterableListModel<ObjectPanel> objectPanels = new IterableListModel<>();
//	JList<ObjectPanel> objectTabs = new JList<>(objectPanels);

	// Visibility
//	JList<VisibilityPanel> visTabs = new JList<>();


//	IterableListModel<VisibilityPanel> visibilityPanels = new IterableListModel<>();
//	ArrayList<VisibilityPanel> allVisShellPanes = new ArrayList<>();

//	ArrayList<Object> visSourcesNew;

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
		GeosetEditPanels geosetEditPanels = new GeosetEditPanels(mht, this);
		addTab("Geosets", geoIcon, geosetEditPanels.makeGeosetPanel(), "Controls which geosets will be imported.");
		System.out.println("Geosets");

		// Animation Panel
		AnimEditPanel animEditPanel = new AnimEditPanel(mht);
		addTab("Animation", animIcon, animEditPanel.makeAnimationPanel(), "Controls which animations will be imported.");
		System.out.println("Animation");

		// Bone Panel
		boneShellRenderer = new BoneShellListCellRenderer(currentModelManager, importedModelManager);
		final BonePanelListCellRenderer bonePanelRenderer = new BonePanelListCellRenderer(currentModelManager, importedModelManager);

		BonesEditPanel bonesEditPanel = new BonesEditPanel(mht, boneShellRenderer, this);

		addTab("Bones", boneIcon, bonesEditPanel.makeBonePanel(bonePanelRenderer), "Controls which bones will be imported.");

		System.out.println("Bones");

		// Matrices Panel + Build the geosetAnimTabs list of GeosetPanels
		GeosetAnimEditPanel geosetAnimEditPanel = new GeosetAnimEditPanel(mht, this, allMatrOriginal, allMatrSameName);

		addTab("Matrices", greenIcon, geosetAnimEditPanel.makeGeosetAnimPanel(), "Controls which bones geosets are attached to.");

		// Objects Panel
		ObjectsEditPanel objectsEditPanel = new ObjectsEditPanel(mht, this);


		addTab("Objects", objIcon, objectsEditPanel.makeObjectsPanel(), "Controls which objects are imported.");

//		makeObjecsPanel(importedModel);

		// Visibility Panel
		VisibilityEditPanel visibilityEditPanel = new VisibilityEditPanel(mht, allMatrOriginal, allMatrSameName);
		addTab("Visibility", orangeIcon, visibilityEditPanel.makeVisPanel(), "Controls the visibility of portions of the model.");

		// Listen all
//		addChangeListener(e -> updateAnimTabs(this));

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


	private static void uncheckAllAnims(IterableListModel<AnimShell> aniShells, boolean b) {
		for (AnimShell animShell : aniShells) {
			animShell.setDoImport(false);
		}
//		for (int i = 0; i < animTabs.getTabCount(); i++) {
//			final AnimPanel aniPanel = (AnimPanel) animTabs.getComponentAt(i);
//			aniPanel.setSelected(b);
//		}
	}

//	private static void selSimButton(ArrayList<VisibilityPanel> allVisShellPanes) {
//		for (final VisibilityPanel vPanel : allVisShellPanes) {
//			vPanel.selectSimilarOptions();
//		}
//	}

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

	public static VisibilityPanel visPaneFromObject(ArrayList<VisibilityPanel> allVisShellPanes, final VisibilitySource o) {
		for (final VisibilityPanel vp : allVisShellPanes) {
			if (vp.sourceShell.source == o) {
				return vp;
			}
		}
		return null;
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

	private static void deleteFlagAnimations(List<Animation> anims, FloatAnimFlag flag) {
		for (final Animation a : anims) {
			if (flag != null) {
				if (!flag.hasGlobalSeq()) {
					flag.deleteAnim(a);
				}
			}
		}
	}


	/**
	 * Provides a Runnable to the ImportPanel that will be run after the import has
	 * ended successfully.
	 */
	public void setCallback(final ModelStructureChangeListener callback) {
		this.callback = callback;
	}


//	private static void updateAnimTabs(ImportPanel importPanel) {
//		((AnimPanel) importPanel.mht.animTabs.getSelectedComponent()).updateSelectionPicks();
//		importPanel.mht.getFutureBoneList();
//		importPanel.mht.getFutureBoneListExtended(false);
//		importPanel.visibilityList();
//		// ((BoneAttachmentPane)geosetAnimTabs.getSelectedComponent()).refreshLists();
//		importPanel.repaint();
//	}

	private static void clearSelectedBones(IterableListModel<BoneShell> existingBones, ModelHolderThing modelHolderThing) {
		for (int i = 0; i < existingBones.size(); i++) {
			final BoneShell bs = existingBones.get(i);
			if (bs.getImportBone() != null && bs.getShouldImportBone()) {
				bs.getBone().copyMotionFrom(bs.getImportBone());
			}
		}
	}

	private static void importAllGeos(JTabbedPane geosetTabs, boolean b) {
		for (int i = 0; i < geosetTabs.getTabCount(); i++) {
			final GeosetPanel geoPanel = (GeosetPanel) geosetTabs.getComponentAt(i);
			geoPanel.setSelected(b);
		}
	}

	private static void importAllGeos(IterableListModel<GeosetShell> geoShells, boolean b) {
		for (GeosetShell geosetShell : geoShells) {
			geosetShell.setDoImport(b);
		}
	}

	@Override
	public void stateChanged(final ChangeEvent e) {
//		updateAnimTabs(this);
	}

	private static void importAllBones(IterableListModel<BoneShell> boneShells, int selectionIndex) {
		for (final BoneShell boneShell : boneShells) {
			boneShell.setImportStatus(selectionIndex);
		}
	}

	private static void importAllObjs(IterableListModel<ObjectShell> objectShells, boolean b) {
//		for (final ObjectPanel objectPanel : objectPanels) {
//			objectPanel.doImport.setSelected(b);
//		}
		for (final ObjectShell objectShell : objectShells) {
			objectShell.setShouldImport(b);
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

	private static FloatAnimFlag getVisAnimFlag(List<Animation> anims, boolean tans, VisibilityShell source) {
		FloatAnimFlag flag = null;
		if (source != null) {
			if (source.getSource() == null && source.getModel() == null) {
				if (source.isNeverVisible()) {
					flag = new FloatAnimFlag("temp");
					for (final Animation a : anims) {
						if (tans) {
							flag.addEntry(a.getStart(), (float) 0, (float) 0, (float) 0);
						} else {
							flag.addEntry(a.getStart(), (float) 0);
						}
					}
				}
			} else {
				flag = (FloatAnimFlag) source.getVisSource().getVisibilityFlag();
			}
		}
		return flag;
	}

	private void selSimButton(ArrayList<VisibilityShell> allVisShell) {
		Map<String, VisibilityShell> oldSourceMap = new HashMap<>();
		Map<String, VisibilityShell> newSourceMap = new HashMap<>();
		for (final VisibilityShell visibilityShell : mht.visSourcesNew) {
			newSourceMap.put(visibilityShell.getSource().getName(), visibilityShell);
		}
		for (final VisibilityShell visibilityShell : mht.visSourcesOld) {
			oldSourceMap.put(visibilityShell.getSource().getName(), visibilityShell);
		}
		for (final VisibilityShell visibilityShell : allVisShell) {
			String name = visibilityShell.getSource().getName();
			if (oldSourceMap.containsKey(name)) {
				visibilityShell.setOldVisSource(oldSourceMap.get(name));
			}
			if (newSourceMap.containsKey(name)) {
				visibilityShell.setNewVisSource(newSourceMap.get(name));
			}
		}
	}

	private void getSingelAnimation(Animation pickedAnim) {
		uncheckAllAnims(mht.aniShells, false);
//		for (int i = 0; i < mht.animTabs.getTabCount(); i++) {
//			final AnimPanel aniPanel = (AnimPanel) mht.animTabs.getComponentAt(i);
//			if (aniPanel.anim.getName().equals(pickedAnim.getName())) {
//				aniPanel.doImport.setSelected(true);
//			}
//		}
		for (AnimShell animShell : mht.aniShells) {
			if (animShell.getAnim().getName().equals(pickedAnim.getName())) {
				animShell.setDoImport(true);
			}
		}
//		mht.clearExistingAnims.doClick();// turn it back off
	}

	public IterableListModel<VisibilityShell> visibilityList() {
//		final VisibilityShell selection = mht.visTabs.getSelectedValue();
//		mht.visibilityShells.clear();
//		for (int i = 0; i < mht.geosetTabs.getTabCount(); i++) {
//			final GeosetPanel gp = (GeosetPanel) mht.geosetTabs.getComponentAt(i);
//			for (final Layer x : gp.getSelectedMaterial().getLayers()) {
//				getAndAddVisShell(x);
//			}
//		}
//		for (int i = 0; i < mht.geosetTabs.getTabCount(); i++) {
//			final GeosetPanel gp = (GeosetPanel) mht.geosetTabs.getComponentAt(i);
//			if (gp.doImport.isSelected()) {
//				final Geoset x = gp.geoset;
//				getAndAddVisShell(x);
//			}
//		}
		final VisibilityShell selection = mht.visTabs.getSelectedValue();
		mht.visibilityShells.clear();
		for (GeosetShell geosetShell : mht.geoShells) {
			for (final Layer x : geosetShell.getMaterial().getLayers()) {
				getAndAddVisShell(x);
			}
		}
		for (GeosetShell geosetShell : mht.geoShells) {
			if (geosetShell.isDoImport()) {
				final Geoset x = geosetShell.getGeoset();
				getAndAddVisShell(x);
			}
		}
		// The current's

		final EditableModel model = mht.currentModel;
		for (IdObject x : model.getIdObjects()) {
			if (!(x instanceof Bone) && !(x instanceof EventObject) && !(x instanceof CollisionShape)) {
				getAndAddVisShell(x);
			}
		}
		for (final VisibilitySource x : model.getLights()) {
			getAndAddVisShell(x);
		}
		for (final VisibilitySource x : model.getAttachments()) {
			getAndAddVisShell(x);
		}
		for (final VisibilitySource x : model.getParticleEmitters()) {
			getAndAddVisShell(x);
		}
		for (final VisibilitySource x : model.getParticleEmitter2s()) {
			getAndAddVisShell(x);
		}
		for (final VisibilitySource x : model.getPopcornEmitters()) {
			getAndAddVisShell(x);
		}
		for (final VisibilitySource x : model.getPopcornEmitters()) {
			getAndAddVisShell(x);
		}

		for (final ObjectShell op : mht.objectShells) {
			if (op.getShouldImport() && (op.getIdObject() != null))
			// we don't touch camera "object" panels (which aren't idobjects)
			{
				getAndAddVisShell(op.getIdObject());
			}
		}
		mht.visTabs.setSelectedValue(selection, true);
		return mht.visibilityShells;
	}

	public void getAndAddVisShell(VisibilitySource x) {
		VisibilityShell vs = mht.allVisShellsMap.get(x);
		if (!mht.visibilityShells.contains(vs) && (vs != null)) {
			mht.visibilityShells.addElement(vs);
		}
	}

	public void doImport() {
		importStarted = true;
		try {
			// AFTER WRITING THREE THOUSAND LINES OF INTERFACE, FINALLLLLLLLLLLLYYYYYYYYY
			// The engine for actually performing the model to model import.

			if (mht.currentModel == mht.importModel) {
				JOptionPane.showMessageDialog(null, "The program has confused itself.");
			}

//			for (int i = 0; i < mht.geosetTabs.getTabCount(); i++) {
//				final GeosetPanel gp = (GeosetPanel) mht.geosetTabs.getComponentAt(i);
//				gp.setMaterials(mht.currentModel, mht.importModel);
//			}

			for (GeosetShell geosetShell : mht.geoShells) {
				geosetShell.getGeoset().setMaterial(geosetShell.getMaterial());
				if (geosetShell.isDoImport() && (geosetShell.isImported)) {
					mht.currentModel.add(geosetShell.getGeoset());
					if (geosetShell.getGeoset().getGeosetAnim() != null) {
						mht.currentModel.add(geosetShell.getGeoset().getGeosetAnim());
					}
				}

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

			if (mht.clearExistingAnims.isSelected()) {
				clearCurrModelAnims();
			}


			final List<Animation> newAnims = new ArrayList<>();
//			for (int i = 0; i < mht.animTabs.getTabCount(); i++) {
//				final AnimPanel aniPanel = (AnimPanel) mht.animTabs.getComponentAt(i);
//				if (aniPanel.doImport.isSelected()) {
//					aniPanel.doImportSelectedAnims(mht.currentModel, mht.importModel, newAnims, impFlags, impEventObjs, newImpFlags, newImpEventObjs);
//				}
//			}
			for (AnimShell animShell : mht.aniShells) {
				if (animShell.isDoImport()) {
					mht.doImportSelectedAnims(animShell, mht.currentModel, mht.importModel, newAnims, impFlags, impEventObjs, newImpFlags, newImpEventObjs);
				}
			}

			if (!mht.clearExistingAnims.isSelected()) {
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
				if (boneShell.getImportStatus() == 0) {
					mht.currentModel.add(boneShell.getBone());
					objectsAdded.add(boneShell.getBone());
					boneShell.getBone().setParent(boneShell.getNewParent());
				} else {
					boneShell.getBone().setParent(null);
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
			for (int i = 0; i < mht.geosetAnimTabs.getTabCount(); i++) {
				if (mht.geosetAnimTabs.isEnabledAt(i)) {
					final BoneAttachmentPanel bap = (BoneAttachmentPanel) mht.geosetAnimTabs.getComponentAt(i);
					dummyBone = bap.attachBones(dummyBone);
//				dummyBone = attachBones(dummyBone, bap);
				}
			}


			mht.currentModel.updateObjectIds();
			for (final Geoset g : mht.currentModel.getGeosets()) {
				g.applyMatricesToVertices(mht.currentModel);
			}

			// Objects!
//			final List<Camera> camerasAdded = new ArrayList<>();
//			for (final ObjectPanel objectPanel : mht.objectPanels) {
////				addSelectedObjects(objectPanel, objectsAdded, camerasAdded);
//				objectPanel.addSelectedObjects(objectsAdded, camerasAdded, mht.currentModel);
//			}
			final List<Camera> camerasAdded = new ArrayList<>();
			for (final ObjectShell os : mht.objectShells) {
//				addSelectedObjects(objectPanel, objectsAdded, camerasAdded);
//				mht.addSelectedObjects(objectPanel, objectsAdded, camerasAdded, mht.currentModel);
				if (os.getShouldImport()) {
					if (os.getIdObject() != null) {
						final BoneShell mbs = os.getParent();
						if (mbs != null) {
							os.getIdObject().setParent(mbs.getBone());
						} else {
							os.getIdObject().setParent(null);
						} // later make a name field?
						mht.currentModel.add(os.getIdObject());
						objectsAdded.add(os.getIdObject());
					} else if (os.getCamera() != null) {
						mht.currentModel.add(os.getCamera());
						camerasAdded.add(os.getCamera());
					}
				} else {
					if (os.getIdObject() != null) {
						os.getIdObject().setParent(null); // Fix cross-model referencing issue (force clean parent node's list of children)
					}
				}
			}

//			final List<Animation> oldAnims = new ArrayList<>(mht.currentModel.getAnims());
//			final List<FloatAnimFlag> finalVisFlags = new ArrayList<>();
//			for (int i = 0; i < mht.visibilityPanels.size(); i++) {
//				final VisibilityPanel vPanel = mht.visibilityPanels.get(i);
//				vPanel.addSelectedVisFlags(oldAnims, newAnims, mht.clearExistingAnims.isSelected(), finalVisFlags, mht.currentModel, mht.importModel);
////				addSelectedVisFlags(vPanel, oldAnims, newAnims, clearAnims, finalVisFlags);
//			}
//
//			for (int i = 0; i < mht.visibilityPanels.size(); i++) {
//				final VisibilityPanel vPanel = mht.visibilityPanels.get(i);
//				final VisibilitySource temp = ((VisibilitySource) vPanel.sourceShell.source);
//				final AnimFlag<?> visFlag = finalVisFlags.get(i);// might be null
//				if (visFlag.size() > 0) {
//					temp.setVisibilityFlag(visFlag);
//				} else {
//					temp.setVisibilityFlag(null);
//				}
//			}

			final List<Animation> oldAnims = new ArrayList<>(mht.currentModel.getAnims());
			final List<FloatAnimFlag> finalVisFlags = new ArrayList<>();
			for (int i = 0; i < mht.visibilityShells.size(); i++) {
				final VisibilityShell vPanel = mht.visibilityShells.get(i);
				addSelectedVisFlags(vPanel, oldAnims, newAnims, mht.clearExistingAnims.isSelected(), finalVisFlags, mht.currentModel, mht.importModel);
//				addSelectedVisFlags(vPanel, oldAnims, newAnims, clearAnims, finalVisFlags);
			}

			for (int i = 0; i < mht.visibilityShells.size(); i++) {
				final VisibilityShell vPanel = mht.visibilityShells.get(i);
				final VisibilitySource temp = ((VisibilitySource) vPanel.source);
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
//			for (int i = 0; i < mht.geosetTabs.getTabCount(); i++) {
//				final GeosetPanel gp = (GeosetPanel) mht.geosetTabs.getComponentAt(i);
//				if (gp.doImport.isSelected() && (gp.model == mht.importModel)) {
//					geosetsAdded.add(gp.geoset);
//				}
//			}
			for (GeosetShell geosetShell : mht.geoShells) {
				if (geosetShell.isDoImport() && geosetShell.isImported) {
					geosetsAdded.add(geosetShell.getGeoset());
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

	public void addSelectedVisFlags(VisibilityShell vs, List<Animation> oldAnims, java.util.List<Animation> newAnims, boolean clearAnims, List<FloatAnimFlag> finalVisFlags, EditableModel currentModel, EditableModel importedModel) {
		final VisibilitySource temp = vs.visibilitySource;
		final AnimFlag<?> visFlag = temp.getVisibilityFlag();// might be null
		final FloatAnimFlag newVisFlag;
		boolean tans = false;
		if (visFlag != null) {
			newVisFlag = (FloatAnimFlag) AnimFlag.buildEmptyFrom(visFlag);
			tans = visFlag.tans();
		} else {
			newVisFlag = new FloatAnimFlag(temp.visFlagName());
		}
		// newVisFlag = new AnimFlag(temp.visFlagName());
		final VisibilityShell oldSource = vs.getOldVisSource();
		FloatAnimFlag flagOld = getVisAnimFlag(oldAnims, tans, oldSource);
		final VisibilityShell newSource = vs.getNewVisSource();
		FloatAnimFlag flagNew = getVisAnimFlag(newAnims, tans, newSource);
		if ((vs.getFavorOld() && vs.model == currentModel && !clearAnims) || (!vs.getFavorOld() && vs.model == importedModel)) {
			// this is an element favoring existing animations over imported
			deleteFlagAnimations(oldAnims, flagNew);
			// All entries for visibility are deleted from imported sources during existing animation times
		} else {
			// this is an element not favoring existing over imported
			deleteFlagAnimations(newAnims, flagOld);
			// All entries for visibility are deleted from original-based sources during imported animation times
		}
		if (flagOld != null) {
			newVisFlag.copyFrom(flagOld);
		}
		if (flagNew != null) {
			newVisFlag.copyFrom(flagNew);
		}
		finalVisFlags.add(newVisFlag);
	}

	public void addSelectedObjects(ObjectShell os, List<IdObject> objectsAdded, List<Camera> camerasAdded, EditableModel model) {
		if (os.getShouldImport()) {
			if (os.getIdObject() != null) {
				final BoneShell mbs = os.getParent();
				if (mbs != null) {
					os.getIdObject().setParent(mbs.getBone());
				} else {
					os.getIdObject().setParent(null);
				}
				// later make a name field?
				model.add(os.getIdObject());
				objectsAdded.add(os.getIdObject());
			} else if (os.getCamera() != null) {
				model.add(os.getCamera());
				camerasAdded.add(os.getCamera());
			}
		} else {
			if (os.getIdObject() != null) {
				os.getIdObject().setParent(null);
				// Fix cross-model referencing issue (force clean parent node's list of children)
			}
		}
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
		for (AnimShell animShell : mht.existingAnims) {

			if (animShell.importAnim != null) {
				animShell.importAnim.copyToInterval(animShell.anim.getStart(), animShell.anim.getEnd(), impFlags, impEventObjs, newImpFlags, newImpEventObjs);

				final Animation tempAnim = new Animation("temp", animShell.anim.getStart(), animShell.anim.getEnd());
				newAnims.add(tempAnim);

				if (!clearBones) {

					for (BoneShell bs : mht.existingBones) {
						if (bs.getImportBone() != null) {
							if (mht.boneToPanel.get(bs.getImportBone()).importTypeBox.getSelectedIndex() == 1) {
								System.out.println(
										"Attempting to clear animation for " + bs.getBone().getName() + " values "
												+ animShell.anim.getStart() + ", " + animShell.anim.getEnd());
								bs.getBone().clearAnimation(animShell.anim);
							}
						}
					}
				}
			}
		}
	}

	// *********************Simple Import Functions****************
	public void animTransfer(final boolean singleAnimation, final Animation pickedAnim, final boolean show) {
//		importAllGeos(mht.geosetTabs, false);
		importAllGeos(mht.geoShells, false);
		importAllBones(mht.boneShells, 1);
		mht.clearExistingAnims.doClick();
//		importAllObjs(mht.objectPanels, false);
		importAllObjs(mht.objectShells, false);
		visibilityList();
		selSimButton(mht.allVisShells);

		if (singleAnimation) {
			getSingelAnimation(pickedAnim);
		}

		VisibilityShell corpseShell = null;
		// Try assuming it's a unit with a corpse; they'll tend to be that way

		// Iterate through new visibility sources, find a geoset with gutz material
		for (int i = 0; (i < mht.visSourcesNew.size()) && (corpseShell == null); i++) {
			if (mht.visSourcesNew.get(i) instanceof VisibilityShell) {
				VisibilityShell vs = (VisibilityShell) mht.visSourcesNew.get(i);
				if (vs.source instanceof Geoset) {
					final Geoset g = (Geoset) vs.source;
					if ((g.getGeosetAnim() != null) && g.getMaterial().firstLayer().firstTexture().getPath().equalsIgnoreCase("textures\\gutz.blp")) {
						corpseShell = vs;
					}
				}
			}
		}
//		if (corpseShell != null) {
//			for (VisibilityPanel vp : mht.visibilityPanels) {
//				VisibilityShell vs = vp.sourceShell;
//				if (vs.source instanceof Geoset) {
//					final Geoset g = (Geoset) vp.sourceShell.source;
//					if ((g.getGeosetAnim() != null) && g.getMaterial().firstLayer().firstTexture().getPath().equalsIgnoreCase("textures\\gutz.blp")) {
//						vp.newSourcesBox.setSelectedItem(corpseShell);
//					}
//				}
//			}
//		}
		if (corpseShell != null) {
			for (VisibilityShell vs : mht.visibilityShells) {
				if (vs.source instanceof Geoset) {
					final Geoset g = (Geoset) vs.source;
					if ((g.getGeosetAnim() != null) && g.getMaterial().firstLayer().firstTexture().getPath().equalsIgnoreCase("textures\\gutz.blp")) {
						vs.setNewVisSource(corpseShell);
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
//		importAllGeos(mht.geosetTabs, false);
		importAllGeos(mht.geoShells, false);
		uncheckAllAnims(mht.aniShells, false);
		importAllBones(mht.boneShells, 2);
//		importAllObjs(mht.objectPanels, false);
		importAllObjs(mht.objectShells, false);
		visibilityList();
		selSimButton(mht.allVisShells);

		if (singleAnimation) {
//			transfereSingleAnimation(pickedAnim, visFromAnim);
			for (AnimShell animShell : mht.aniShells) {
				mht.transferSingleAnimation(animShell, pickedAnim, visFromAnim);
			}
//			for (int i = 0; i < mht.animTabs.getTabCount(); i++) {
//				final AnimPanel aniPanel = (AnimPanel) mht.animTabs.getComponentAt(i);
//				aniPanel.transferSingleAnimation(pickedAnim, visFromAnim);
//			}
		} else {
			JOptionPane.showMessageDialog(null, "Bug in anim transfer: attempted unnecessary 2-part transfer");
		}
//		for (int i = 0; i < mht.visibilityPanels.getSize(); i++) {
//			final VisibilityPanel vp = mht.visibilityPanels.get(i);
//			vp.favorOld.doClick();
//		}
		for (VisibilityShell vp : mht.visibilityShells) {
			vp.setFavorOld(false);
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


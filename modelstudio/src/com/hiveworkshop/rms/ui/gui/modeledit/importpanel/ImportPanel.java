package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.model.EventObject;
import com.hiveworkshop.rms.editor.model.*;
import com.hiveworkshop.rms.editor.model.animflag.AnimFlag;
import com.hiveworkshop.rms.editor.model.animflag.FloatAnimFlag;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelViewManager;
import com.hiveworkshop.rms.ui.application.edit.ModelStructureChangeListener;
import com.hiveworkshop.rms.ui.gui.modeledit.BoneShell;
import com.hiveworkshop.rms.ui.icons.RMSIcons;
import com.hiveworkshop.rms.ui.util.ExceptionPopup;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.List;
import java.util.*;

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

	EditableModel currentModel;
	EditableModel importedModel;

	// Geosets
	JTabbedPane geosetTabs = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);

	//	Vector<BonePanel> bbp = new Vector<>();
//	List<BonePanel> bonePanels = new ArrayList<>();
	private final Map<Bone, BonePanel> boneToPanel = new HashMap<>();
	JTabbedPane animTabs = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);
	// Animation
//	JCheckBox clearExistingAnims;
	JCheckBox clearExistingAnims = new JCheckBox("Clear pre-existing animations");

	// Bones
//	JPanel bonesPanel = new JPanel();
//	DefaultListModel<AnimShell> existingAnims;
	DefaultListModel<AnimShell> existingAnims = new DefaultListModel<>();
	JCheckBox clearExistingBones = new JCheckBox("Clear pre-existing bones and helpers");
	//	DefaultListModel<BonePanel> bonePanels = new DefaultListModel<>();
	Vector<BonePanel> bonePanels = new Vector<>();
	JList<BonePanel> boneTabs = new JList<>(bonePanels);
	//	DefaultListModel<BoneShell> existingBones;
	DefaultListModel<BoneShell> existingBones = new DefaultListModel<>();

	// Matrices
	JPanel geosetAnimPanel = new JPanel();
	JTabbedPane geosetAnimTabs = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);

	DefaultListModel<BoneShell> futureBoneList = new DefaultListModel<>();
	List<BoneShell> oldBones;
	List<BoneShell> newBones;

	JButton allMatrOriginal = new JButton("Reset all Matrices");
	JButton allMatrSameName = new JButton("Set all to available, original names");

	private final Set<BoneShell> futureBoneListExQuickLookupSet = new HashSet<>();
	private final BoneShellListCellRenderer boneShellRenderer;

	// Objects
//	DefaultListModel<ObjectPanel> objectPanels = new DefaultListModel<>();
	Vector<ObjectPanel> objectPanels = new Vector<>();
	//	List<ObjectPanel> objectPanels = new ArrayList<>();
//	JList<ObjectPanel> objectTabs = new JList<ObjectPanel>((Vector<? extends ObjectPanel>) objectPanels);
	JList<ObjectPanel> objectTabs = new JList<>(objectPanels);

	// Visibility
	JList<VisibilityPanel> visTabs = new JList<>();

	boolean importSuccess = false;
	boolean importStarted = false;
	boolean importEnded = false;

	private ModelStructureChangeListener callback;

	//	DefaultListModel<VisibilityPanel> visComponents;
	DefaultListModel<VisibilityPanel> visComponents = new DefaultListModel<>();
	ArrayList<VisibilityPanel> allVisShellPanes = new ArrayList<>();
	DefaultListModel<BoneShell> futureBoneListEx = new DefaultListModel<>();
	List<DefaultListModel<BoneShell>> futureBoneListExFixableItems = new ArrayList<>();
	ArrayList<BoneShell> oldHelpers;
	ArrayList<BoneShell> newHelpers;
	ArrayList<Object> visSourcesNew;

	public ImportPanel(final EditableModel a, final EditableModel b) {
		this(a, b, true);
	}


	public ImportPanel(final EditableModel currentModel, final EditableModel importedModel, final boolean visibleOnStart) {
		super();
		if (currentModel.getName().equals(importedModel.getName())) {
			importedModel.setFileRef(new File(importedModel.getFile().getParent() + "/" + importedModel.getName() + " (Imported)" + ".mdl"));
			frame = new JFrame("Importing " + currentModel.getName() + " into itself");
		} else {
			frame = new JFrame("Importing " + importedModel.getName() + " into " + currentModel.getName());
		}
		currentModel.doSavePreps();
		try {
			frame.setIconImage(RMSIcons.MDLIcon.getImage());
		} catch (final Exception e) {
			JOptionPane.showMessageDialog(null, "Error: Image files were not found! Due to bad programming, this might break the program!");
		}
		this.currentModel = currentModel;
		this.importedModel = importedModel;
		final ModelViewManager currentModelManager = new ModelViewManager(currentModel);
		final ModelViewManager importedModelManager = new ModelViewManager(importedModel);

		// Geoset Panel
		GeosetEditPanels geosetEditPanels = new GeosetEditPanels(geosetTabs);
		addTab("Geosets", geoIcon, geosetEditPanels.makeGeosetPanel(currentModel, importedModel), "Controls which geosets will be imported.");
//		makeGeosetPanel(currentModel, importedModel);
		System.out.println("Geosets");

		// Animation Panel
		AnimEditPanel animEditPanel = new AnimEditPanel(animTabs,
				existingAnims,
				clearExistingAnims);
		addTab("Animation", animIcon, animEditPanel.makeAnimationPanel(currentModel, importedModel), "Controls which animations will be imported.");
//		makeAnimationPanel(currentModel, importedModel);
		System.out.println("Animation");

		// Bone Panel
		boneShellRenderer = new BoneShellListCellRenderer(currentModelManager, importedModelManager);
		final BonePanelListCellRenderer bonePanelRenderer = new BonePanelListCellRenderer(currentModelManager, importedModelManager);

		BonesEditPanel bonesEditPanel = new BonesEditPanel(existingBones,
				clearExistingBones,
				boneShellRenderer,
				geosetAnimTabs,
				this, boneToPanel);
//		bonesPanel = bonesEditPanel.makeBonePanel(currentModel, importedModel, bonePanelRenderer);
		addTab("Bones", boneIcon, bonesEditPanel.makeBonePanel(currentModel, importedModel, bonePanelRenderer), "Controls which bones will be imported.");
//		makeBonePanel(currentModel, importedModel, bonePanelRenderer);
		System.out.println("Bones");

		// Matrices Panel + Build the geosetAnimTabs list of GeosetPanels
		GeosetAnimEditPanel geosetAnimEditPanel = new GeosetAnimEditPanel(geosetAnimPanel,
				geosetAnimTabs,
				this,
				allMatrOriginal,
				allMatrSameName);
		geosetAnimEditPanel.makeGeosetAnimPanel(currentModel, importedModel);
		addTab("Matrices", greenIcon, geosetAnimPanel, "Controls which bones geosets are attached to.");
//		final ParentToggleRenderer ptr = makeMatricesPanle(currentModelManager, importedModelManager);
//		MakeGeosetAnimPanel(currentModel, importedModel, ptr);

		// Objects Panel
//		ObjectsEditPanel objectsEditPanel = new ObjectsEditPanel(clearExistingBones, oldHelpers, newHelpers, currentModel, importedModel, this,
//				objectPanels, boneToPanel, objectTabs, bonesPanel,
//				futureBoneListExQuickLookupSet,
//				futureBoneListEx,
//				futureBoneListExFixableItems);
		ObjectsEditPanel objectsEditPanel = new ObjectsEditPanel(
				clearExistingBones,
				oldHelpers,
				newHelpers,
				currentModel,
				importedModel,
				this,
				objectPanels,
				boneToPanel,
				objectTabs,
				futureBoneListExQuickLookupSet,
				futureBoneListEx,
				futureBoneListExFixableItems);
//		ObjectsEditPanel objectsEditPanel = new ObjectsEditPanel(
//				clearExistingBones,
//				oldHelpers,
//				newHelpers,
//				currentModel,
//				importedModel,
//				this,
//				objectPanels,
//				boneToPanel,
//				objectTabs,
//				futureBoneListExQuickLookupSet,
//				futureBoneListEx,
//				futureBoneListExFixableItems);
		addTab("Objects", objIcon, objectsEditPanel.makeObjecsPanel(importedModel), "Controls which objects are imported.");
//		makeObjecsPanel(importedModel);

		// Visibility Panel
		VisibilityEditPanel visibilityEditPanel = new VisibilityEditPanel(
				visTabs,
				currentModel,
				importedModel,
				visComponents,
				allVisShellPanes,
				allMatrOriginal,
				allMatrSameName,
				futureBoneListEx,
				futureBoneListExFixableItems,
				oldHelpers,
				newHelpers,
				visSourcesNew,
				objectPanels,
				objectTabs,
				geosetTabs);
		addTab("Visibility", orangeIcon, visibilityEditPanel.makeVisPanel(currentModel), "Controls the visibility of portions of the model.");
//		makeVisPanel(currentModel);

		// Listen all
		addChangeListener(e -> updateAnimTabs(this));

		footerFinalPanel();

		this.setMaximumSize(new Dimension(1920, 1080));
		frame.setBounds(0, 0, 1024, 780);
		frame.setMaximumSize(new Dimension(1920, 1080));
		frame.setLocationRelativeTo(null);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(final WindowEvent e) {
				cancelImport(ImportPanel.this);
			}
		});
		frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		// frame.pack();
		frame.setVisible(visibleOnStart);
	}


	private static void importAllGeos(JTabbedPane geosetTabs, boolean b) {
		for (int i = 0; i < geosetTabs.getTabCount(); i++) {
			final GeosetPanel geoPanel = (GeosetPanel) geosetTabs.getComponentAt(i);
			geoPanel.setSelected(b);
		}
	}

	private static void uncheckAllAnims(JTabbedPane animTabs, boolean b) {
		for (int i = 0; i < animTabs.getTabCount(); i++) {
			final AnimPanel aniPanel = (AnimPanel) animTabs.getComponentAt(i);
			aniPanel.setSelected(b);
		}
	}

	private static void importAllBones(Vector<BonePanel> bonePanels, int selsctionIndex) {
		for (final BonePanel bonePanel : bonePanels) {
			bonePanel.setSelectedIndex(selsctionIndex);
		}
	}

	private static void importAllObjs(Vector<ObjectPanel> objectPanels, boolean b) {
		for (final ObjectPanel objectPanel : objectPanels) {
			objectPanel.doImport.setSelected(b);
		}
	}

	private static void selSimButton(ArrayList<VisibilityPanel> allVisShellPanes) {
		for (final VisibilityPanel vPanel : allVisShellPanes) {
			vPanel.selectSimilarOptions();
		}
	}

	private static void applyImport(ImportPanel importPanel) {
		importPanel.doImport();
		importPanel.frame.setVisible(false);
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
	public static void informGeosetVisibility(JTabbedPane geosetAnimTabs, final Geoset g, final boolean flag) {
		for (int i = 0; i < geosetAnimTabs.getTabCount(); i++) {
			final BoneAttachmentPanel geoPanel = (BoneAttachmentPanel) geosetAnimTabs.getComponentAt(i);
			if (geoPanel.geoset == g) {
				geosetAnimTabs.setEnabledAt(i, flag);
			}
		}
	}

	public static VisibilityPanel visPaneFromObject(ArrayList<VisibilityPanel> allVisShellPanes, final Object o) {
		for (final VisibilityPanel vp : allVisShellPanes) {
			if (vp.sourceShell.source == o) {
				return vp;
			}
		}
		return null;
	}

	public static VisibilityPanel visPaneFromObject(ArrayList<VisibilityPanel> allVisShellPanes, final Named o) {
		for (final VisibilityPanel vp : allVisShellPanes) {
			if (vp.sourceShell.source == o) {
				return vp;
			}
		}
		return null;
	}

	public static void setSelectedItem(JList<BonePanel> boneTabs, final String what) {
		final Object[] selected = boneTabs.getSelectedValuesList().toArray();
		for (Object o : selected) {
			final BonePanel temp = (BonePanel) o;
			temp.setSelectedValue(what);
		}
	}

	/**
	 * The method run when the user pushes the "Set Parent for All" button in the
	 * MultiBone panel.
	 */
	public static void setParentMultiBones(ImportPanel importPanel) {
		final JList<BoneShell> list = new JList<>(importPanel.getFutureBoneListExtended(true));
		list.setCellRenderer(importPanel.boneShellRenderer);
		final int x = JOptionPane.showConfirmDialog(importPanel, new JScrollPane(list), "Set Parent for All Selected Bones",
				JOptionPane.OK_CANCEL_OPTION);
		if (x == JOptionPane.OK_OPTION) {
			final Object[] selected = importPanel.boneTabs.getSelectedValuesList().toArray();
			for (Object o : selected) {
				final BonePanel temp = (BonePanel) o;
				temp.setParent(list.getSelectedValue());
			}
		}
	}

	public static void setObjGroupSelected(JList<ObjectPanel> objectTabs, final boolean flag) {
		final Object[] selected = objectTabs.getSelectedValuesList().toArray();
		for (Object o : selected) {
			final ObjectPanel temp = (ObjectPanel) o;
			temp.doImport.setSelected(flag);
		}
	}

	public static void setVisGroupSelected(JList<VisibilityPanel> visTabs, final boolean flag) {
		final Object[] selected = visTabs.getSelectedValuesList().toArray();
		for (Object o : selected) {
			final VisibilityPanel temp = (VisibilityPanel) o;
			temp.favorOld.setSelected(flag);
		}
	}

	public static void setVisGroupItemOld(JList<VisibilityPanel> visTabs, final Object o) {
		final Object[] selected = visTabs.getSelectedValuesList().toArray();
		for (Object value : selected) {
			final VisibilityPanel temp = (VisibilityPanel) value;
			temp.oldSourcesBox.setSelectedItem(o);
		}
	}

	/**
	 * Provides a Runnable to the ImportPanel that will be run after the import has
	 * ended successfully.
	 */
	public void setCallback(final ModelStructureChangeListener callback) {
		this.callback = callback;
	}

	public static void setVisGroupItemNew(JList<VisibilityPanel> visTabs, final Object o) {
		final Object[] selected = visTabs.getSelectedValuesList().toArray();
		for (Object value : selected) {
			final VisibilityPanel temp = (VisibilityPanel) value;
			temp.newSourcesBox.setSelectedItem(o);
		}
	}

	private static void updateAnimTabs(ImportPanel importPanel) {
		((AnimPanel) importPanel.animTabs.getSelectedComponent()).updateSelectionPicks();
		importPanel.getFutureBoneList();
		importPanel.getFutureBoneListExtended(false);
		importPanel.visibilityList();
		// ((BoneAttachmentPane)geosetAnimTabs.getSelectedComponent()).refreshLists();
		importPanel.repaint();
	}

	private void footerFinalPanel() {
		JButton okayButton = new JButton("Finish");
		okayButton.addActionListener(e -> applyImport(this));
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(e -> cancelImport(this));

		final JPanel finalPanel = new JPanel();
		final GroupLayout layout = new GroupLayout(finalPanel);
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(this)
				.addGroup(layout.createSequentialGroup()
						.addComponent(cancelButton)
						.addComponent(okayButton)));
		layout.setVerticalGroup(layout.createSequentialGroup()
				.addComponent(this).addGap(8)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addComponent(cancelButton)
						.addComponent(okayButton)));
		finalPanel.setLayout(layout);

		// Later add a Yes/No confirmation of "do you wish to cancel this
		// import?" when you close the window.
		frame.setContentPane(finalPanel);
	}

	public DefaultListModel<BoneShell> getFutureBoneList() {
		if (oldBones == null) {
			oldBones = new ArrayList<>();
			newBones = new ArrayList<>();
//			final List<Bone> oldBonesRefs = currentModel.sortedIdObjects(Bone.class);
			final List<Bone> oldBonesRefs = currentModel.getBones();
			for (final Bone b : oldBonesRefs) {
				final BoneShell bs = new BoneShell(b);
				bs.modelName = currentModel.getName();
				oldBones.add(bs);
			}
//			final List<Bone> newBonesRefs = importedModel.sortedIdObjects(Bone.class);
			final List<Bone> newBonesRefs = importedModel.getBones();
			for (final Bone b : newBonesRefs) {
				final BoneShell bs = new BoneShell(b);
				bs.modelName = importedModel.getName();
				bs.panel = boneToPanel.get(b);
				newBones.add(bs);
			}
		}
		if (!clearExistingBones.isSelected()) {
			for (final BoneShell b : oldBones) {
				if (!futureBoneList.contains(b)) {
					futureBoneList.addElement(b);
				}
			}
		} else {
			for (final BoneShell b : oldBones) {
				if (futureBoneList.contains(b)) {
					futureBoneList.removeElement(b);
				}
			}
		}
		for (final BoneShell b : newBones) {
			if (b.panel.importTypeBox.getSelectedItem() == BonePanel.IMPORT) {
				if (!futureBoneList.contains(b)) {
					futureBoneList.addElement(b);
				}
			} else {
				if (futureBoneList.contains(b)) {
					futureBoneList.removeElement(b);
				}
			}
		}
		return futureBoneList;
	}

	public DefaultListModel<BoneShell> getFutureBoneListExtended(final boolean newSnapshot) {
		long totalAddTime = 0;
		long addCount = 0;
		long totalRemoveTime = 0;
		long removeCount = 0;
		if (oldHelpers == null) {
			oldHelpers = new ArrayList<>();
			newHelpers = new ArrayList<>();

//			List<? extends Bone> oldHelpersRefs = currentModel.sortedIdObjects(Bone.class);
			List<? extends Bone> oldHelpersRefs = currentModel.getBones();
			for (final Bone b : oldHelpersRefs) {
				final BoneShell bs = new BoneShell(b);
				bs.modelName = currentModel.getName();
				bs.showClass = true;
				oldHelpers.add(bs);
			}
//			oldHelpersRefs = currentModel.sortedIdObjects(Helper.class);
			oldHelpersRefs = currentModel.getHelpers();
			for (final Bone b : oldHelpersRefs) {
				final BoneShell bs = new BoneShell(b);
				bs.modelName = currentModel.getName();
				bs.showClass = true;
				oldHelpers.add(bs);
			}

//			List<? extends Bone> newHelpersRefs = importedModel.sortedIdObjects(Bone.class);
			List<? extends Bone> newHelpersRefs = importedModel.getBones();
			for (final Bone b : newHelpersRefs) {
				final BoneShell bs = new BoneShell(b);
				bs.modelName = importedModel.getName();
				bs.showClass = true;
				bs.panel = boneToPanel.get(b);
				newHelpers.add(bs);
			}
//			newHelpersRefs = importedModel.sortedIdObjects(Helper.class);
			newHelpersRefs = importedModel.getHelpers();
			for (final Bone b : newHelpersRefs) {
				final BoneShell bs = new BoneShell(b);
				bs.modelName = importedModel.getName();
				bs.showClass = true;
				bs.panel = boneToPanel.get(b);
				newHelpers.add(bs);
			}
		}
		if (!clearExistingBones.isSelected()) {
			for (final BoneShell b : oldHelpers) {
				if (!futureBoneListExQuickLookupSet.contains(b)) {
					final long startTime = System.nanoTime();
					futureBoneListEx.addElement(b);
					final long endTime = System.nanoTime();
					totalAddTime += (endTime - startTime);
					addCount++;
					futureBoneListExQuickLookupSet.add(b);
				}
			}
		} else {
			for (final BoneShell b : oldHelpers) {
				if (futureBoneListExQuickLookupSet.remove(b)) {
					final long startTime = System.nanoTime();
					futureBoneListEx.removeElement(b);
					final long endTime = System.nanoTime();
					totalRemoveTime += (endTime - startTime);
					removeCount++;
				}
			}
		}
		for (final BoneShell b : newHelpers) {
			b.panel = boneToPanel.get(b.bone);
			if (b.panel != null) {
				if (b.panel.importTypeBox.getSelectedItem() == BonePanel.IMPORT) {
					if (!futureBoneListExQuickLookupSet.contains(b)) {
						final long startTime = System.nanoTime();
						futureBoneListEx.addElement(b);
						final long endTime = System.nanoTime();
						totalAddTime += (endTime - startTime);
						addCount++;
						futureBoneListExQuickLookupSet.add(b);
					}
				} else {
					if (futureBoneListExQuickLookupSet.remove(b)) {
						final long startTime = System.nanoTime();
						futureBoneListEx.removeElement(b);
						final long endTime = System.nanoTime();
						totalRemoveTime += (endTime - startTime);
						removeCount++;
					}
				}
			}
		}
		if (addCount != 0) {
			System.out.println("average add time: " + (totalAddTime / addCount));
			System.out.println("add count: " + addCount);
		}
		if (removeCount != 0) {
			System.out.println("average remove time: " + (totalRemoveTime / removeCount));
			System.out.println("remove count: " + removeCount);
		}

		final DefaultListModel<BoneShell> listModelToReturn;
		if (newSnapshot || futureBoneListExFixableItems.isEmpty()) {
			final DefaultListModel<BoneShell> futureBoneListReplica = new DefaultListModel<>();
			futureBoneListExFixableItems.add(futureBoneListReplica);
			listModelToReturn = futureBoneListReplica;
		} else {
			listModelToReturn = futureBoneListExFixableItems.get(0);
		}
		// We CANT call clear, we have to preserve
		// the parent list
		for (final DefaultListModel<BoneShell> model : futureBoneListExFixableItems) {
			// clean things that should not be there
			for (int i = model.getSize() - 1; i >= 0; i--) {
				final BoneShell previousElement = model.get(i);
				if (!futureBoneListExQuickLookupSet.contains(previousElement)) {
					model.remove(i);
				}
			}
			// add back things who should be there
			for (int i = 0; i < futureBoneListEx.getSize(); i++) {
				final BoneShell elementAt = futureBoneListEx.getElementAt(i);
				if (!model.contains(elementAt)) {
					model.addElement(elementAt);
				}
			}
		}
//		for(DefaultListModel<BoneShell> model: futureBoneListExFixableItems) {
//			model.clear();
//			for(int i = 0; i < futureBoneListEx.getSize(); i++) {
//				model.addElement(futureBoneListEx.getElementAt(i));
//			}
//		}
		return listModelToReturn;
//		return futureBoneListEx;
	}

//	public DefaultListModel<VisibilityPanel> visibilityList() {
//		final Object selection = visTabs.getSelectedValue();
//		visComponents.clear();
//		for (int i = 0; i < geosetTabs.getTabCount(); i++) {
//			final GeosetPanel gp = (GeosetPanel) geosetTabs.getComponentAt(i);
//			for (final Layer l : gp.getSelectedMaterial().getLayers()) {
//				final VisibilityPanel vs = visPaneFromObject(allVisShellPanes, l);
//				if (!visComponents.contains(vs) && (vs != null)) {
//					visComponents.addElement(vs);
//				}
//			}
//		}
//		for (int i = 0; i < geosetTabs.getTabCount(); i++) {
//			final GeosetPanel gp = (GeosetPanel) geosetTabs.getComponentAt(i);
//			if (gp.doImport.isSelected()) {
//				final Geoset ga = gp.geoset;
//				final VisibilityPanel vs = visPaneFromObject(allVisShellPanes, ga);
//				if (!visComponents.contains(vs) && (vs != null)) {
//					visComponents.addElement(vs);
//				}
//			}
//		}
//		// The current's
//		final EditableModel model = currentModel;
//		for (final Named l : model.sortedIdObjects(Light.class)) {
//			final VisibilityPanel vs = visPaneFromObject(allVisShellPanes, l);
//			if (!visComponents.contains(vs) && (vs != null)) {
//				visComponents.addElement(vs);
//			}
//		}
//		for (final Named a : model.sortedIdObjects(Attachment.class)) {
//			final VisibilityPanel vs = visPaneFromObject(allVisShellPanes, a);
//			if (!visComponents.contains(vs) && (vs != null)) {
//				visComponents.addElement(vs);
//			}
//		}
//		for (final Named x : model.sortedIdObjects(ParticleEmitter.class)) {
//			final VisibilityPanel vs = visPaneFromObject(allVisShellPanes, x);
//			if (!visComponents.contains(vs) && (vs != null)) {
//				visComponents.addElement(vs);
//			}
//		}
//		for (final Named x : model.sortedIdObjects(ParticleEmitter2.class)) {
//			final VisibilityPanel vs = visPaneFromObject(allVisShellPanes, x);
//			if (!visComponents.contains(vs) && (vs != null)) {
//				visComponents.addElement(vs);
//			}
//		}
//		for (final Named x : model.sortedIdObjects(RibbonEmitter.class)) {
//			final VisibilityPanel vs = visPaneFromObject(allVisShellPanes, x);
//			if (!visComponents.contains(vs) && (vs != null)) {
//				visComponents.addElement(vs);
//			}
//		}
//		for (final Named x : model.sortedIdObjects(ParticleEmitterPopcorn.class)) {
//			final VisibilityPanel vs = visPaneFromObject(allVisShellPanes, x);
//			if (!visComponents.contains(vs) && (vs != null)) {
//				visComponents.addElement(vs);
//			}
//		}
//
//		for (int i = 0; i < objectPanels.size(); i++) {
//			final ObjectPanel op = objectPanels.get(i);
//			if (op.doImport.isSelected() && (op.object != null))
//			// we don't touch camera "object" panels (which aren't idobjects)
//			{
//				final VisibilityPanel vs = visPaneFromObject(allVisShellPanes, op.object);
//				if (!visComponents.contains(vs) && (vs != null)) {
//					visComponents.addElement(vs);
//				}
//			}
//		}
//		visTabs.setSelectedValue(selection, true);
//		return visComponents;
//	}

	public DefaultListModel<VisibilityPanel> visibilityList() {
		final Object selection = visTabs.getSelectedValue();
		visComponents.clear();
		for (int i = 0; i < geosetTabs.getTabCount(); i++) {
			final GeosetPanel gp = (GeosetPanel) geosetTabs.getComponentAt(i);
			for (final Layer l : gp.getSelectedMaterial().getLayers()) {
				final VisibilityPanel vs = visPaneFromObject(allVisShellPanes, l);
				if (!visComponents.contains(vs) && (vs != null)) {
					visComponents.addElement(vs);
				}
			}
		}
		for (int i = 0; i < geosetTabs.getTabCount(); i++) {
			final GeosetPanel gp = (GeosetPanel) geosetTabs.getComponentAt(i);
			if (gp.doImport.isSelected()) {
				final Geoset ga = gp.geoset;
				final VisibilityPanel vs = visPaneFromObject(allVisShellPanes, ga);
				if (!visComponents.contains(vs) && (vs != null)) {
					visComponents.addElement(vs);
				}
			}
		}
		// The current's

		final EditableModel model = currentModel;
		for (IdObject idObject : model.getIdObjects()) {
			if (!(idObject instanceof Bone) && !(idObject instanceof EventObject) && !(idObject instanceof CollisionShape)) {
				final VisibilityPanel vs = visPaneFromObject(allVisShellPanes, idObject);
				if (!visComponents.contains(vs) && (vs != null)) {
					visComponents.addElement(vs);
				}
			}
		}
		for (final Named x : model.sortedIdObjects(Light.class)) {
			final VisibilityPanel vs = visPaneFromObject(allVisShellPanes, x);
			if (!visComponents.contains(vs) && (vs != null)) {
				visComponents.addElement(vs);
			}
		}
		for (final Named x : model.sortedIdObjects(Attachment.class)) {
			final VisibilityPanel vs = visPaneFromObject(allVisShellPanes, x);
			if (!visComponents.contains(vs) && (vs != null)) {
				visComponents.addElement(vs);
			}
		}
		for (final Named x : model.sortedIdObjects(ParticleEmitter.class)) {
			final VisibilityPanel vs = visPaneFromObject(allVisShellPanes, x);
			if (!visComponents.contains(vs) && (vs != null)) {
				visComponents.addElement(vs);
			}
		}
		for (final Named x : model.sortedIdObjects(ParticleEmitter2.class)) {
			final VisibilityPanel vs = visPaneFromObject(allVisShellPanes, x);
			if (!visComponents.contains(vs) && (vs != null)) {
				visComponents.addElement(vs);
			}
		}
		for (final Named x : model.sortedIdObjects(RibbonEmitter.class)) {
			final VisibilityPanel vs = visPaneFromObject(allVisShellPanes, x);
			if (!visComponents.contains(vs) && (vs != null)) {
				visComponents.addElement(vs);
			}
		}
		for (final Named x : model.sortedIdObjects(ParticleEmitterPopcorn.class)) {
			final VisibilityPanel vs = visPaneFromObject(allVisShellPanes, x);
			if (!visComponents.contains(vs) && (vs != null)) {
				visComponents.addElement(vs);
			}
		}

		for (final ObjectPanel op : objectPanels) {
			if (op.doImport.isSelected() && (op.object != null))
			// we don't touch camera "object" panels (which aren't idobjects)
			{
				final VisibilityPanel vs = visPaneFromObject(allVisShellPanes, op.object);
				if (!visComponents.contains(vs) && (vs != null)) {
					visComponents.addElement(vs);
				}
			}
		}
		visTabs.setSelectedValue(selection, true);
		return visComponents;
	}

	@Override
	public void stateChanged(final ChangeEvent e) {
		updateAnimTabs(this);
	}

	public void doImport() {
		importStarted = true;
		try {
			// AFTER WRITING THREE THOUSAND LINES OF INTERFACE, FINALLLLLLLLLLLLYYYYYYYYY
			// The engine for actually performing the model to model import.

			if (currentModel == importedModel) {
				JOptionPane.showMessageDialog(null, "The program has confused itself.");
			}

			for (int i = 0; i < geosetTabs.getTabCount(); i++) {
				final GeosetPanel gp = (GeosetPanel) geosetTabs.getComponentAt(i);
				gp.setMaterials(currentModel, importedModel);
//				gp.geoset.setMaterial(gp.getSelectedMaterial());
//				if (gp.doImport.isSelected() && (gp.model == importedModel)) {
//					currentModel.add(gp.geoset);
//					if (gp.geoset.getGeosetAnim() != null) {
//						currentModel.add(gp.geoset.getGeosetAnim());
//					}
//				}
			}
			// note to self: remember to scale event objects with time

			final List<AnimFlag<?>> newImpFlags = new ArrayList<>();
			final java.util.List<AnimFlag<?>> impFlags = importedModel.getAllAnimFlags();
			for (final AnimFlag<?> af : impFlags) {
				if (!af.hasGlobalSeq()) {
					newImpFlags.add(AnimFlag.buildEmptyFrom(af));
				} else {
					newImpFlags.add(AnimFlag.createFromAnimFlag(af));
				}
			}

			final List<EventObject> newImpEventObjs = new ArrayList<>();
//			final List<EventObject> impEventObjs = importedModel.sortedIdObjects(EventObject.class);
			final List<EventObject> impEventObjs = importedModel.getEvents();
			for (final Object e : impEventObjs) {
				newImpEventObjs.add(EventObject.buildEmptyFrom((EventObject) e));
			}
			final boolean clearAnims = clearExistingAnims.isSelected();
			if (clearAnims) {
				final java.util.List<AnimFlag<?>> curFlags = currentModel.getAllAnimFlags();
				final List<EventObject> curEventObjs = currentModel.getEvents();
//				final List<EventObject> curEventObjs = currentModel.sortedIdObjects(EventObject.class);
				for (final Animation anim : currentModel.getAnims()) {
					anim.clearData(curFlags, curEventObjs);
				}
				currentModel.getAnims().clear();
			}


			final List<Animation> newAnims = new ArrayList<>();
			for (int i = 0; i < animTabs.getTabCount(); i++) {
				final AnimPanel aniPanel = (AnimPanel) animTabs.getComponentAt(i);
				if (aniPanel.doImport.isSelected()) {
					aniPanel.doImportSelectedAnims(currentModel, importedModel, newAnims, impFlags, impEventObjs, newImpFlags, newImpEventObjs);
//					doImportSelectedAnims(newAnims, impFlags, impEventObjs, newImpFlags, newImpEventObjs, aniPanel);
				}
			}

			final boolean clearBones = clearExistingBones.isSelected();
			if (!clearAnims) {
				doClearAnims(newAnims, impFlags, impEventObjs, newImpFlags, newImpEventObjs, clearBones);
			}
			// Now, rebuild the old animflags with the new
			for (final AnimFlag<?> af : impFlags) {
				af.setValuesTo(newImpFlags.get(impFlags.indexOf(af)));
			}
			for (final Object e : impEventObjs) {
				((EventObject) e).setValuesTo(newImpEventObjs.get(impEventObjs.indexOf(e)));
			}

			if (clearBones) {
				for (final IdObject o : currentModel.sortedIdObjects(Bone.class)) {
					currentModel.remove(o);
				}
				for (final IdObject o : currentModel.sortedIdObjects(Helper.class)) {
					currentModel.remove(o);
				}
			}

			final List<IdObject> objectsAdded = new ArrayList<>();

			for (BonePanel bonePanel : bonePanels) {
				bonePanel.getSelectedBones(objectsAdded, currentModel);
			}
			if (!clearBones) {
				for (int i = 0; i < existingBones.size(); i++) {
					final BoneShell bs = existingBones.get(i);
					if (bs.importBone != null) {
						if (boneToPanel.get(bs.importBone).importTypeBox.getSelectedIndex() == 1) {
							bs.bone.copyMotionFrom(bs.importBone);
						}
					}
				}
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


			currentModel.updateObjectIds();
			for (final Geoset g : currentModel.getGeosets()) {
				g.applyMatricesToVertices(currentModel);
			}

			// Objects!
			final List<Camera> camerasAdded = new ArrayList<>();
			for (final ObjectPanel objectPanel : objectPanels) {
//				addSelectedObjects(objectPanel, objectsAdded, camerasAdded);
				objectPanel.addSelectedObjects(objectsAdded, camerasAdded, currentModel);
			}

			final List<Animation> oldAnims = new ArrayList<>(currentModel.getAnims());
			final List<FloatAnimFlag> finalVisFlags = new ArrayList<>();
			for (int i = 0; i < visComponents.size(); i++) {
				final VisibilityPanel vPanel = visComponents.get(i);
				vPanel.addSelectedVisFlags(oldAnims, newAnims, clearAnims, finalVisFlags, currentModel, importedModel);
//				addSelectedVisFlags(vPanel, oldAnims, newAnims, clearAnims, finalVisFlags);
			}

			for (int i = 0; i < visComponents.size(); i++) {
				final VisibilityPanel vPanel = visComponents.get(i);
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
				if (gp.doImport.isSelected() && (gp.model == importedModel)) {
					geosetsAdded.add(gp.geoset);
				}
			}
			if (callback != null) {
				callback.geosetsAdded(geosetsAdded);
				callback.nodesAdded(objectsAdded);
				callback.camerasAdded(camerasAdded);
			}
			for (final AnimFlag<?> flag : currentModel.getAllAnimFlags()) {
				flag.sort();
			}
		} catch (final Exception e) {
			e.printStackTrace();
			ExceptionPopup.display(e);
		}

		importEnded = true;
	}

//	private void addSelectedVisFlags(VisibilityPanel vPanel, List<Animation> oldAnims, List<Animation> newAnims, boolean clearAnims, List<FloatAnimFlag> finalVisFlags) {
//		final VisibilitySource temp = ((VisibilitySource) vPanel.sourceShell.source);
//		final AnimFlag<?> visFlag = temp.getVisibilityFlag();// might be null
//		final FloatAnimFlag newVisFlag;
//		boolean tans = false;
//		if (visFlag != null) {
//			newVisFlag = (FloatAnimFlag) AnimFlag.buildEmptyFrom(visFlag);
//			tans = visFlag.tans();
//		} else {
//			newVisFlag = new FloatAnimFlag(temp.visFlagName());
//		}
//		// newVisFlag = new AnimFlag(temp.visFlagName());
//		final Object oldSource = vPanel.oldSourcesBox.getSelectedItem();
//		FloatAnimFlag flagOld = getVisAnimFlag(oldAnims, tans, oldSource);
//		final Object newSource = vPanel.newSourcesBox.getSelectedItem();
//		FloatAnimFlag flagNew = getVisAnimFlag(newAnims, tans, newSource);
//		if ((vPanel.favorOld.isSelected() && vPanel.sourceShell.model == currentModel && !clearAnims)
//				|| (!vPanel.favorOld.isSelected() && vPanel.sourceShell.model == importedModel)) {
//			// this is an element favoring existing animations over imported
//			deleteFlagAnimations(oldAnims, flagNew);
//			// All entries for visibility are deleted from imported sources during existing animation times
//		} else {
//			// this is an element not favoring existing over imported
//			deleteFlagAnimations(newAnims, flagOld);
//			// All entries for visibility are deleted from original-based sources during imported animation times
//		}
//		if (flagOld != null) {
//			newVisFlag.copyFrom(flagOld);
//		}
//		if (flagNew != null) {
//			newVisFlag.copyFrom(flagNew);
//		}
//		finalVisFlags.add(newVisFlag);
//	}

//	private static void deleteFlagAnimations(List<Animation> anims, FloatAnimFlag flag) {
//		for (final Animation a : anims) {
//			if (flag != null) {
//				if (!flag.hasGlobalSeq()) {
//					flag.deleteAnim(a);
//				}
//			}
//		}
//	}

//	private static FloatAnimFlag getVisAnimFlag(List<Animation> anims, boolean tans, Object source) {
//		FloatAnimFlag flag = null;
//		if (source != null){
//			if (source.getClass() == String.class) {
//				if (source == VisibilityPanel.NOTVISIBLE) {
//					flag = new FloatAnimFlag("temp");
//					for (final Animation a : anims) {
//						if (tans) {
//							flag.addEntry(a.getStart(), (float) 0, (float) 0, (float) 0);
//						} else {
//							flag.addEntry(a.getStart(), (float) 0);
//						}
//					}
//				}
//			} else {
//				flag = (FloatAnimFlag) ((VisibilitySource) ((VisibilityShell) source).source).getVisibilityFlag();
//			}
//		}
//		return flag;
//	}

//	private void addSelectedObjects(ObjectPanel objectPanel, List<IdObject> objectsAdded, List<Camera> camerasAdded) {
//		if (objectPanel.doImport.isSelected()) {
//			if (objectPanel.object != null) {
//				final BoneShell mbs = objectPanel.parentsList.getSelectedValue();
//				if (mbs != null) {
//					objectPanel.object.setParent(mbs.bone);
//				} else {
//					objectPanel.object.setParent(null);
//				}
//				// objectPanel.object.setName(importedModel.getName()+" "+objectPanel.object.getName());
//				// later make a name field?
//				currentModel.add(objectPanel.object);
//				objectsAdded.add(objectPanel.object);
//			} else if (objectPanel.camera != null) {
//				// objectPanel.camera.setName(importedModel.getName()+" "+objectPanel.camera.getName());
//				currentModel.add(objectPanel.camera);
//				camerasAdded.add(objectPanel.camera);
//			}
//		} else {
//			if (objectPanel.object != null) {
//				objectPanel.object.setParent(null);
//				// Fix cross-model referencing issue (force clean parent node's list of children)
//			}
//		}
//	}
//
//	private void applyMatrices() {
//		// DefaultListModel<BoneShell> bones = getFutureBoneList();
//		Bone dummyBone = null;
//		for (int i = 0; i < geosetAnimTabs.getTabCount(); i++) {
//			if (geosetAnimTabs.isEnabledAt(i)) {
//				final BoneAttachmentPanel bap = (BoneAttachmentPanel) geosetAnimTabs.getComponentAt(i);
//				dummyBone = bap.attachBones(dummyBone);
////				dummyBone = attachBones(dummyBone, bap);
//			}
//		}
//	}
//
//	private Bone attachBones(Bone dummyBone, BoneAttachmentPanel bap) {
//		for (int l = 0; l < bap.oldBoneRefs.size(); l++) {
//			final MatrixShell ms = bap.oldBoneRefs.get(l);
//			ms.matrix.getBones().clear();
//			for (final BoneShell bs : ms.newBones) {
//				if (currentModel.contains(bs.bone)) {
//					if (bs.bone.getClass() == Helper.class) {
//						JOptionPane.showMessageDialog(null,
//								"Error: Holy fo shizzle my grizzle! A geoset is trying to attach to a helper, not a bone!");
//					}
//					ms.matrix.add(bs.bone);
//				} else {
//					System.out.println("Boneshaving " + bs.bone.getName() + " out of use");
//				}
//			}
//			if (ms.matrix.size() == 0) {
//				JOptionPane.showMessageDialog(null,
//						"Error: A matrix was functionally destroyed while importing, and may take the program with it!");
//			}
//			if (ms.matrix.getBones().size() < 1) {
//				if (dummyBone == null) {
//					dummyBone = new Bone();
//					dummyBone.setName("Bone_MatrixEaterDummy" + (int) (Math.random() * 2000000000));
//					dummyBone.setPivotPoint(new Vec3(0, 0, 0));
//					if (!currentModel.contains(dummyBone)) {
//						currentModel.add(dummyBone);
//					}
//					JOptionPane.showMessageDialog(null,
//							"Warning: You left some matrices empty. This was detected, and a dummy bone at { 0, 0, 0 } has been generated for them named "
//									+ dummyBone.getName()
//									+ "\nMultiple geosets may be attached to this bone, and the error will only be reported once for your convenience.");
//				}
//				if (!ms.matrix.getBones().contains(dummyBone)) {
//					ms.matrix.getBones().add(dummyBone);
//				}
//			}
//			// ms.matrix.bones = ms.newBones;
//		}
//		return dummyBone;
//	}
//
//	private void getSelectedBones(List<IdObject> objectsAdded, int i) {
//		final BonePanel bonePanel = bonePanels.get(i);
//		final Bone b = bonePanel.bone;
//		final int type = bonePanel.importTypeBox.getSelectedIndex();
//		// b.setName(b.getName()+" "+importedModel.getName());
//		// bonePanel.boneList.getSelectedValuesList();
//
//		// we will go through all bone shells for this
//		// Fix cross-model referencing issue (force clean parent node's list of children)
//		switch (type) {
//			case 0 -> {
//				currentModel.add(b);
//				objectsAdded.add(b);
//				final BoneShell mbs = bonePanel.futureBonesList.getSelectedValue();
//				if (mbs != null) {
//					b.setParent((mbs).bone);
//				} else {
//					b.setParent(null);
//				}
//			}
//			case 1, 2 -> b.setParent(null);
//		}
//	}
//
//	private void doImportSelectedAnims(List<Animation> newAnims, List<AnimFlag<?>> impFlags, List<EventObject> impEventObjs, List<AnimFlag<?>> newImpFlags, List<EventObject> newImpEventObjs, AnimPanel aniPanel) {
//		final int type = aniPanel.importTypeBox.getSelectedIndex();
//		final int animTrackEnd = currentModel.animTrackEnd();
//		if (aniPanel.inReverse.isSelected()) {
//			// reverse the animation
//			aniPanel.anim.reverse(impFlags, impEventObjs);
//		}
//		switch (type) {
//			case 0:
//				aniPanel.anim.copyToInterval(animTrackEnd + 300, animTrackEnd + aniPanel.anim.length() + 300, impFlags, impEventObjs, newImpFlags, newImpEventObjs);
//				aniPanel.anim.setInterval(animTrackEnd + 300, animTrackEnd + aniPanel.anim.length() + 300);
//				currentModel.add(aniPanel.anim);
//				newAnims.add(aniPanel.anim);
//				break;
//			case 1:
//				aniPanel.anim.copyToInterval(animTrackEnd + 300, animTrackEnd + aniPanel.anim.length() + 300, impFlags, impEventObjs, newImpFlags, newImpEventObjs);
//				aniPanel.anim.setInterval(animTrackEnd + 300, animTrackEnd + aniPanel.anim.length() + 300);
//				aniPanel.anim.setName(aniPanel.newNameEntry.getText());
//				currentModel.add(aniPanel.anim);
//				newAnims.add(aniPanel.anim);
//				break;
//			case 2:
//				// List<AnimShell> targets = aniPane.animList.getSelectedValuesList();
//				// aniPanel.anim.setInterval(animTrackEnd+300,animTrackEnd + aniPanel.anim.length() + 300, impFlags,
//				// impEventObjs, newImpFlags, newImpEventObjs);
//				// handled by animShells
//				break;
//			case 3:
//				importedModel.buildGlobSeqFrom(aniPanel.anim, impFlags);
//				break;
//		}
//	}

	private void doClearAnims(List<Animation> newAnims, List<AnimFlag<?>> impFlags, List<EventObject> impEventObjs, List<AnimFlag<?>> newImpFlags, List<EventObject> newImpEventObjs, boolean clearBones) {
		for (int i = 0; i < existingAnims.size(); i++) {
			final AnimShell animShell = existingAnims.get(i);
			if (animShell.importAnim != null) {
				animShell.importAnim.copyToInterval(animShell.anim.getStart(), animShell.anim.getEnd(), impFlags, impEventObjs, newImpFlags, newImpEventObjs);
				final Animation tempAnim = new Animation("temp", animShell.anim.getStart(), animShell.anim.getEnd());
				newAnims.add(tempAnim);
				if (!clearBones) {
					for (int p = 0; p < existingBones.size(); p++) {
						final BoneShell bs = existingBones.get(p);
						if (bs.importBone != null) {
							if (boneToPanel.get(bs.importBone).importTypeBox.getSelectedIndex() == 1) {
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

	public boolean importSuccessful() {
		return importSuccess;
	}

	public boolean importStarted() {
		return importStarted;
	}

	public boolean importEnded() {
		return importEnded;
	}

	public JFrame getParentFrame() {
		return frame;
	}

	// *********************Simple Import Functions****************
	public void animTransfer(final boolean singleAnimation, final Animation pickedAnim, final Animation visFromAnim,
	                         final boolean show) {
		importAllGeos(geosetTabs, false);
		importAllBones(bonePanels, 1);
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
			for (int i = 0; i < visComponents.getSize(); i++) {
				VisibilityPanel vp = visComponents.get(i);
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

	// *********************Simple Import Functions****************
	public void animTransferPartTwo(final boolean singleAnimation, final Animation pickedAnim,
	                                final Animation visFromAnim, final boolean show) {
		// This should be an import from self
		importAllGeos(geosetTabs, false);
		uncheckAllAnims(animTabs, false);
		importAllBones(bonePanels, 2);
		importAllObjs(objectPanels, false);
		visibilityList();
		selSimButton(allVisShellPanes);

		if (singleAnimation) {
//			transfereSingleAnimation(pickedAnim, visFromAnim);
			for (int i = 0; i < animTabs.getTabCount(); i++) {
				final AnimPanel aniPanel = (AnimPanel) animTabs.getComponentAt(i);
				aniPanel.transfereSingleAnimation(pickedAnim, visFromAnim);
			}
		} else {
			JOptionPane.showMessageDialog(null, "Bug in anim transfer: attempted unnecessary 2-part transfer");
		}
		for (int i = 0; i < visComponents.getSize(); i++) {
			final VisibilityPanel vp = visComponents.get(i);
			vp.favorOld.doClick();
		}

		if (!show) {
			applyImport(this);
		}
	}

//	private void transfereSingleAnimation(Animation pickedAnim, Animation visFromAnim) {
//		for (int i = 0; i < animTabs.getTabCount(); i++) {
//			final AnimPanel aniPanel = (AnimPanel) animTabs.getComponentAt(i);
//			aniPanel.transfereSingleAnimation(pickedAnim, visFromAnim);
////			if (aniPanel.anim.getName().equals(visFromAnim.getName())) {
////				aniPanel.doImport.doClick();
////				aniPanel.importTypeBox.setSelectedItem(AnimPanel.TIMESCALE);
////
////				for (int d = 0; d < existingAnims.getSize(); d++) {
////					final AnimShell shell = existingAnims.get(d);
////					if ((shell).anim.getName().equals(pickedAnim.getName())) {
////						aniPanel.animList.setSelectedValue(shell, true);
////						aniPanel.updateSelectionPicks();
////						break;
////					}
////				}
////			}
//		}
//	}
}


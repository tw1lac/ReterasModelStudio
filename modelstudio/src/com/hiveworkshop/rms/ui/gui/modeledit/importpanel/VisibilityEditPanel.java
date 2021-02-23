package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.model.*;
import com.hiveworkshop.rms.util.BiMap;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class VisibilityEditPanel {

	JButton allMatrOriginal;
	JButton allMatrSameName;
	ArrayList<VisibilityShell> allVisShells;
	BiMap<VisibilitySource, VisibilityShell> vsiSourceToVisShell = new BiMap<>();
	BiMap<VisibilitySource, VisibilityPanel> vsiSourceToVisPanel = new BiMap<>();
	VisibilityPanel currVisibilityPanel;
	//	ArrayList<Object> visSourcesOld;
	private ModelHolderThing mht;

	public VisibilityEditPanel(ModelHolderThing mht,
	                           JButton allMatrOriginal,
	                           JButton allMatrSameName
	) {
		this.mht = mht;
		this.allMatrOriginal = allMatrOriginal;
		this.allMatrSameName = allMatrSameName;
	}

	public JPanel makeVisPanel() {
		JPanel visPanel = new JPanel(new MigLayout("gap 0", ""));
		visPanel.setMaximumSize(new Dimension(500, 500));
		visPanel.setPreferredSize(new Dimension(500, 500));
		visPanel.setMaximumSize(new Dimension(500, 500));
//		addTab("Visibility", orangeIcon, visPanel, "Controls the visibility of portions of the model.");

		initVisibilityList();
		visibilityList();

		CardLayout visCardLayout = new CardLayout();
		visCardLayout.maximumLayoutSize(visPanel);
		JPanel visPanelCards = new JPanel(visCardLayout);
		visPanelCards.setMaximumSize(new Dimension(500, 500));

		final VisShellBoxCellRenderer visRenderer = new VisShellBoxCellRenderer();
		currVisibilityPanel = new VisibilityPanel(mht, visRenderer);
		visPanelCards.add(currVisibilityPanel, "single");
		for (final VisibilityShell vs : allVisShells) {

//			mht.allVisShellPanes.add(vp);
			mht.allVisShells.add(vs);
			mht.allVisShellsMap.put(vs.getVisSource(), vs);

//			visPanelCards.add(vp, vp.title.getText());
		}

		MultiVisibilityPanel multiVisPanel = new MultiVisibilityPanel(mht, visRenderer);
		visPanelCards.add(new JPanel(), "blank");
		visPanelCards.add(multiVisPanel, "multiple");
//		mht.visTabs.setModel(mht.visibilityPanels);
		mht.visTabs.setModel(mht.visibilityShells);
		mht.visTabs.setCellRenderer(new VisPaneListCellRenderer(mht.currentModel));
		mht.visTabs.addListSelectionListener(e -> visTabsValueChanged(multiVisPanel, visCardLayout, visPanelCards, mht.visTabs));
		mht.visTabs.setSelectedIndex(0);
//		visPanelCards.setBorder(BorderFactory.createLineBorder(Color.blue.darker()));

//		JButton allInvisButton = createButton("All Invisible in Exotic Anims", e -> allVisButton(mht.allVisShellPanes, mht.currentModel, VisibilityPanel.NOTVISIBLE), "Forces everything to be always invisibile in animations other than their own original animations.");
//		visPanel.add(allInvisButton);
//
//		JButton allVisButton = createButton("All Visible in Exotic Anims", e -> allVisButton(mht.allVisShellPanes, mht.currentModel, VisibilityPanel.VISIBLE), "Forces everything to be always visibile in animations other than their own original animations.");
//		visPanel.add(allVisButton);
//
//		JButton selSimButton = createButton("Select Similar Options", e -> selSimButton(mht.allVisShellPanes), "Similar components will be selected as visibility sources in exotic animations.");
//		visPanel.add(selSimButton);

		JButton allInvisButton = createButton("All Invisible in Exotic Anims", e -> allVisButton(mht.allVisShells, mht.currentModel, VisibilityPanel.NOTVISIBLE), "Forces everything to be always invisibile in animations other than their own original animations.");
		visPanel.add(allInvisButton);

		JButton allVisButton = createButton("All Visible in Exotic Anims", e -> allVisButton(mht.allVisShells, mht.currentModel, VisibilityPanel.VISIBLE), "Forces everything to be always visibile in animations other than their own original animations.");
		visPanel.add(allVisButton);

		JButton selSimButton = createButton("Select Similar Options", e -> selSimButton(mht.allVisShells), "Similar components will be selected as visibility sources in exotic animations.");
		visPanel.add(selSimButton);

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(mht.visTabs), visPanelCards);

		visPanel.add(allInvisButton, "wrap");
		visPanel.add(allVisButton, "wrap");
		visPanel.add(selSimButton, "wrap");
		visPanel.add(splitPane, "wrap");

		System.out.println("done visPanel");
		return visPanel;
	}

	public JButton createButton(String text, ActionListener actionListener, String toolTipText) {
		JButton selSimButton = new JButton(text);
		selSimButton.addActionListener(actionListener);
		selSimButton.setToolTipText(toolTipText);
		return selSimButton;
	}

	//	private static void visTabsValueChanged(MultiVisibilityPanel multiVisPanel, CardLayout visCardLayout, JPanel visPanelCards, JList<VisibilityPanel> visTabs) {
	private void visTabsValueChanged(MultiVisibilityPanel multiVisPanel, CardLayout visCardLayout, JPanel visPanelCards, JList<VisibilityShell> visTabs) {
//		List<VisibilityPanel> selectedValuesList = visTabs.getSelectedValuesList();
		List<VisibilityShell> selectedValuesList = visTabs.getSelectedValuesList();
		if (selectedValuesList.size() < 1) {
			visCardLayout.show(visPanelCards, "blank");
		} else if (selectedValuesList.size() == 1) {
//			visCardLayout.show(visPanelCards, visTabs.getSelectedValue().toString());
			visCardLayout.show(visPanelCards, "single");
			currVisibilityPanel.setCurrSourceShell(visTabs.getSelectedValue());
		} else {
			visCardLayout.show(visPanelCards, "multiple");

//			int tempIndexOld = selectedValuesList.get(0).oldSourcesBox.getSelectedIndex();
//			int tempIndexNew = selectedValuesList.get(0).newSourcesBox.getSelectedIndex();
//			boolean selectedt = selectedValuesList.get(0).favorOld.isSelected();
			VisibilityShell tempIndexOld = selectedValuesList.get(0).getOldVisSource();
			VisibilityShell tempIndexNew = selectedValuesList.get(0).getNewVisSource();
			boolean selectedt = selectedValuesList.get(0).getFavorOld();


			boolean dif = selectedValuesList.stream().anyMatch(visibilityShell -> visibilityShell.getFavorOld());
			if (!dif) {
				multiVisPanel.favorOld.setSelected(selectedt);
			}

			boolean difBoxOld = selectedValuesList.stream().anyMatch(visibilityShell -> visibilityShell.getOldVisSource() != tempIndexOld);
			if (difBoxOld) {
				multiVisPanel.setMultipleOld();
			} else {
				multiVisPanel.oldSourcesBox.setSelectedItem(tempIndexOld);
			}

			boolean difBoxNew = selectedValuesList.stream().anyMatch(visibilityShell -> visibilityShell.getNewVisSource() != tempIndexNew);
			if (difBoxNew) {
				multiVisPanel.setMultipleNew();
			} else {
				multiVisPanel.newSourcesBox.setSelectedItem(tempIndexNew);
			}
		}
	}

//	public VisibilityShell shellFromObject(ArrayList<VisibilityShell> allVisShells, final VisibilitySource o) {
//		for (final VisibilityShell vs : allVisShells) {
//			if (vs.source == o) {
//				return vs;
//			}
//		}
//		return null;
//	}

	public VisibilityPanel visPaneFromObject(ArrayList<VisibilityPanel> allVisShellPanes, final VisibilitySource o) {
		for (final VisibilityPanel vp : allVisShellPanes) {
//			return vsiSourceToVisShell.get(o);
			if (vp.sourceShell.source == o) {
				return vp;
			}
		}
		return null;
	}

//	private void allVisButton(ArrayList<VisibilityPanel> allVisShellPanes, EditableModel currentModel, String visible) {
//		for (final VisibilityPanel vPanel : allVisShellPanes) {
//			if (vPanel.sourceShell.model == currentModel) {
//				vPanel.newSourcesBox.setSelectedItem(visible);
//			} else {
//				vPanel.oldSourcesBox.setSelectedItem(visible);
//			}
//		}
//	}

	private void allVisButton(ArrayList<VisibilityShell> allVisShellPanes, EditableModel currentModel, String visible) {
		for (final VisibilityShell visibilityShell : allVisShellPanes) {
			if (visibilityShell.model == currentModel) {
				VisibilityPanel.newSourcesBox.setSelectedItem(visible);
			} else {
				VisibilityPanel.oldSourcesBox.setSelectedItem(visible);
			}
		}
	}

//	private void selSimButton(ArrayList<VisibilityPanel> allVisShellPanes) {
//		for (final VisibilityPanel vPanel : allVisShellPanes) {
//			VisibilityPanel.selectSimilarOptions(vPanel.sourceShell);
//		}
//	}

//	private void selSimButton(ArrayList<VisibilityShell> allVisShellPanes) {
//		for (final VisibilityShell visibilityShell : allVisShellPanes) {
//			VisibilityPanel.selectSimilarOptions(visibilityShell);
//		}
//	}

	private void selSimButton(ArrayList<VisibilityShell> allVisShellPanes) {
		for (final VisibilityShell visibilityShell : allVisShellPanes) {
//			VisibilityPanel.selectSimilarOptions(visibilityShell);
			if (VisibilityPanel.oldSourcesBox.getSelectedItem() instanceof VisibilityShell) {
				visibilityShell.setOldVisSource((VisibilityShell) VisibilityPanel.oldSourcesBox.getSelectedItem());
			}
			if (VisibilityPanel.newSourcesBox.getSelectedItem() instanceof VisibilityShell) {
				visibilityShell.setNewVisSource((VisibilityShell) VisibilityPanel.newSourcesBox.getSelectedItem());
			}
		}
//		for (final VisibilityShell visibilityShell : allVisShellPanes) {
//			//		for (mht)
//			final ListModel oldSources = oldSourcesBox.getModel();
//			for (int i = 0; i < oldSources.getSize(); i++) {
//				if (!(oldSources.getElementAt(i) instanceof String)) {
//					if (sourceShell.source.getName().equals(((VisibilityShell) oldSources.getElementAt(i)).source.getName())) {
//						System.out.println(sourceShell.source.getName());
//						oldSourcesBox.setSelectedItem(oldSources.getElementAt(i));
//					}
//				}
//			}
//			final ListModel newSources = newSourcesBox.getModel();
//			for (int i = 0; i < newSources.getSize(); i++) {
//				if (!(newSources.getElementAt(i) instanceof String)) {
//					if (sourceShell.source.getName().equals(((VisibilityShell) newSources.getElementAt(i)).source.getName())) {
//						System.out.println(sourceShell.source.getName());
//						newSourcesBox.setSelectedItem(newSources.getElementAt(i));
//					}
//				}
//			}
//		}
	}

	public void initVisibilityList() {
		mht.visSourcesOld = new ArrayList<>();
		mht.visSourcesNew = new ArrayList<>();
		allVisShells = new ArrayList<>();
//		BiMap<VisibilitySource, VisibilityShell> vsiSourceToVisShell = new BiMap<>();
		List<VisibilityShell> impAllVisShells = new ArrayList<>();
		List<VisibilityShell> currAllVisShells = new ArrayList<>();
		EditableModel model = mht.currentModel;
		final List<VisibilitySource> tempList = new ArrayList<>();
		for (final Material mat : model.getMaterials()) {
			for (final Layer x : mat.getLayers()) {
				createAndAddVisShell(model, tempList, x, currAllVisShells, vsiSourceToVisShell);
			}
		}
		for (final Geoset x : model.getGeosets()) {
			createAndAddVisShell(model, tempList, x, currAllVisShells, vsiSourceToVisShell);
		}
		for (final Light x : model.getLights()) {
			createAndAddVisShell(model, tempList, x, currAllVisShells, vsiSourceToVisShell);
		}
		for (final Attachment x : model.getAttachments()) {
			createAndAddVisShell(model, tempList, x, currAllVisShells, vsiSourceToVisShell);
		}
		for (final ParticleEmitter x : model.getParticleEmitters()) {
			createAndAddVisShell(model, tempList, x, currAllVisShells, vsiSourceToVisShell);
		}
		for (final ParticleEmitter2 x : model.getParticleEmitter2s()) {
			createAndAddVisShell(model, tempList, x, currAllVisShells, vsiSourceToVisShell);
		}
		for (final RibbonEmitter x : model.getRibbonEmitters()) {
			createAndAddVisShell(model, tempList, x, currAllVisShells, vsiSourceToVisShell);
		}
		for (final ParticleEmitterPopcorn x : model.getPopcornEmitters()) {
			createAndAddVisShell(model, tempList, x, currAllVisShells, vsiSourceToVisShell);
		}

		EditableModel impModel = mht.importModel;
		for (final Material mat : impModel.getMaterials()) {
			for (final Layer x : mat.getLayers()) {
				createAndAddVisShell(impModel, tempList, x, impAllVisShells, vsiSourceToVisShell);
			}
		}
		for (final Geoset x : impModel.getGeosets()) {
			createAndAddVisShell(impModel, tempList, x, impAllVisShells, vsiSourceToVisShell);
		}
		for (final Light x : impModel.getLights()) {
			createAndAddVisShell(impModel, tempList, x, impAllVisShells, vsiSourceToVisShell);
		}
		for (final Attachment x : impModel.getAttachments()) {
			createAndAddVisShell(impModel, tempList, x, impAllVisShells, vsiSourceToVisShell);
		}
		for (final ParticleEmitter x : impModel.getParticleEmitters()) {
			createAndAddVisShell(impModel, tempList, x, impAllVisShells, vsiSourceToVisShell);
		}
		for (final ParticleEmitter2 x : impModel.getParticleEmitter2s()) {
			createAndAddVisShell(impModel, tempList, x, impAllVisShells, vsiSourceToVisShell);
		}
		for (final RibbonEmitter x : impModel.getRibbonEmitters()) {
			createAndAddVisShell(impModel, tempList, x, impAllVisShells, vsiSourceToVisShell);
		}
		for (final ParticleEmitterPopcorn x : impModel.getPopcornEmitters()) {
			createAndAddVisShell(impModel, tempList, x, impAllVisShells, vsiSourceToVisShell);
		}

//		System.out.println("allVisShells:");
//		for (final VisibilityShell vs : allVisShells) {
//			System.out.println(vs.source.getName());
//		}

		System.out.println("new/old:");
		for (final VisibilitySource o : mht.currentModel.getAllVisibilitySources()) {
			if (o.getClass() != GeosetAnim.class) {
				mht.visSourcesOld.add(vsiSourceToVisShell.get(o));
				if (vsiSourceToVisShell.get(o) != null) {
					System.out.println(vsiSourceToVisShell.get(o).source.getName());
				}

//				visSourcesOld.add(shellFromObject(allVisShells, o));
//				System.out.println(shellFromObject(allVisShells, o).source.getName());
			} else {
				mht.visSourcesOld.add(vsiSourceToVisShell.get(((GeosetAnim) o).getGeoset()));

				if (vsiSourceToVisShell.get(o) != null) {
					System.out.println(vsiSourceToVisShell.get(((GeosetAnim) o).getGeoset()).source.getName());
				}

//				visSourcesOld.add(shellFromObject(allVisShells, ((GeosetAnim) o).getGeoset()));
//				System.out.println(shellFromObject(allVisShells, ((GeosetAnim) o).getGeoset()).source.getName());
			}
		}

//		mht.visSourcesOld.add(VisibilityPanel.NOTVISIBLE);
//		mht.visSourcesOld.add(VisibilityPanel.VISIBLE);
		mht.visSourcesOld.add(new VisibilityShell(true));
		mht.visSourcesOld.add(new VisibilityShell(false));


		for (final VisibilitySource o : mht.importModel.getAllVisibilitySources()) {
			if (o.getClass() != GeosetAnim.class) {
				mht.visSourcesNew.add(vsiSourceToVisShell.get(o));
				if (vsiSourceToVisShell.get(o) != null) {
					System.out.println(vsiSourceToVisShell.get(o).source.getName());
				}
//				mht.visSourcesNew.add(shellFromObject(allVisShells, o));
			} else {
				mht.visSourcesNew.add(vsiSourceToVisShell.get(((GeosetAnim) o).getGeoset()));

				if (vsiSourceToVisShell.get(o) != null) {
					System.out.println(vsiSourceToVisShell.get(((GeosetAnim) o).getGeoset()).source.getName());
				}
//				mht.visSourcesNew.add(shellFromObject(allVisShells, ((GeosetAnim) o).getGeoset()));
			}
		}
//		mht.visSourcesNew.add(VisibilityPanel.NOTVISIBLE);
//		mht.visSourcesNew.add(VisibilityPanel.VISIBLE);
		mht.visSourcesNew.add(new VisibilityShell(true));
		mht.visSourcesNew.add(new VisibilityShell(false));
//		visComponents = new DefaultListModel<>();
	}

	public void createAndAddVisShell(EditableModel model, List<VisibilitySource> tempList, VisibilitySource x, List<VisibilityShell> sepList, BiMap<VisibilitySource, VisibilityShell> vsiSourceToVisShell) {
		final VisibilityShell vs = new VisibilityShell(x, model);
		if (!allVisShells.contains(vs)) {
			allVisShells.add(vs);
			System.out.println("VS");
		}
		if (!vsiSourceToVisShell.containsKey(x)) {
			vsiSourceToVisShell.put(x, vs);
		}
		if (!sepList.contains(vs)) {
			sepList.add(vs);
		}
		if (!tempList.contains(x)) {
			tempList.add(x);
			System.out.println("TEMP");
//			allVisShells.add(vs);
		}
	}

//	public void initVisibilityList() {
//		visSourcesOld = new ArrayList<>();
//		visSourcesNew = new ArrayList<>();
//		allVisShells = new ArrayList<>();
//		EditableModel model = currentModel;
//		final List tempList = new ArrayList();
//		for (final Material mat : model.getMaterials()) {
//			for (final Layer lay : mat.getLayers()) {
//				final VisibilityShell vs = new VisibilityShell(lay, model);
//				if (!tempList.contains(lay)) {
//					tempList.add(lay);
//					allVisShells.add(vs);
//				}
//			}
//		}
//		for (final Geoset ga : model.getGeosets()) {
//			final VisibilityShell vs = new VisibilityShell(ga, model);
//			if (!tempList.contains(ga)) {
//				tempList.add(ga);
//				allVisShells.add(vs);
//			}
//		}
//		for (final Object l : model.sortedIdObjects(Light.class)) {
//			final VisibilityShell vs = new VisibilityShell((Named) l, model);
//			if (!tempList.contains(l)) {
//				tempList.add(l);
//				allVisShells.add(vs);
//			}
//		}
//		for (final Object a : model.sortedIdObjects(Attachment.class)) {
//			final VisibilityShell vs = new VisibilityShell((Named) a, model);
//			if (!tempList.contains(a)) {
//				tempList.add(a);
//				allVisShells.add(vs);
//			}
//		}
//		for (final Object x : model.sortedIdObjects(ParticleEmitter.class)) {
//			final VisibilityShell vs = new VisibilityShell((Named) x, model);
//			if (!tempList.contains(x)) {
//				tempList.add(x);
//				allVisShells.add(vs);
//			}
//		}
//		for (final Object x : model.sortedIdObjects(ParticleEmitter2.class)) {
//			final VisibilityShell vs = new VisibilityShell((Named) x, model);
//			if (!tempList.contains(x)) {
//				tempList.add(x);
//				allVisShells.add(vs);
//			}
//		}
//		for (final Object x : model.sortedIdObjects(RibbonEmitter.class)) {
//			final VisibilityShell vs = new VisibilityShell((Named) x, model);
//			if (!tempList.contains(x)) {
//				tempList.add(x);
//				allVisShells.add(vs);
//			}
//		}
//		for (final Object x : model.sortedIdObjects(ParticleEmitterPopcorn.class)) {
//			final VisibilityShell vs = new VisibilityShell((Named) x, model);
//			if (!tempList.contains(x)) {
//				tempList.add(x);
//				allVisShells.add(vs);
//			}
//		}
//		model = importedModel;
//		for (final Material mat : model.getMaterials()) {
//			for (final Layer lay : mat.getLayers()) {
//				final VisibilityShell vs = new VisibilityShell(lay, model);
//				if (!tempList.contains(lay)) {
//					tempList.add(lay);
//					allVisShells.add(vs);
//				}
//			}
//		}
//		for (final Geoset ga : model.getGeosets()) {
//			final VisibilityShell vs = new VisibilityShell(ga, model);
//			if (!tempList.contains(ga)) {
//				tempList.add(ga);
//				allVisShells.add(vs);
//			}
//		}
//		for (final Object l : model.sortedIdObjects(Light.class)) {
//			final VisibilityShell vs = new VisibilityShell((Named) l, model);
//			if (!tempList.contains(l)) {
//				tempList.add(l);
//				allVisShells.add(vs);
//			}
//		}
//		for (final Object a : model.sortedIdObjects(Attachment.class)) {
//			final VisibilityShell vs = new VisibilityShell((Named) a, model);
//			if (!tempList.contains(a)) {
//				tempList.add(a);
//				allVisShells.add(vs);
//			}
//		}
//		for (final Object x : model.sortedIdObjects(ParticleEmitter.class)) {
//			final VisibilityShell vs = new VisibilityShell((Named) x, model);
//			if (!tempList.contains(x)) {
//				tempList.add(x);
//				allVisShells.add(vs);
//			}
//		}
//		for (final Object x : model.sortedIdObjects(ParticleEmitter2.class)) {
//			final VisibilityShell vs = new VisibilityShell((Named) x, model);
//			if (!tempList.contains(x)) {
//				tempList.add(x);
//				allVisShells.add(vs);
//			}
//		}
//		for (final Object x : model.sortedIdObjects(RibbonEmitter.class)) {
//			final VisibilityShell vs = new VisibilityShell((Named) x, model);
//			if (!tempList.contains(x)) {
//				tempList.add(x);
//				allVisShells.add(vs);
//			}
//		}
//		for (final Object x : model.sortedIdObjects(ParticleEmitterPopcorn.class)) {
//			final VisibilityShell vs = new VisibilityShell((Named) x, model);
//			if (!tempList.contains(x)) {
//				tempList.add(x);
//				allVisShells.add(vs);
//			}
//		}
//
//		System.out.println("allVisShells:");
//		for (final VisibilityShell vs : allVisShells) {
//			System.out.println(vs.source.getName());
//		}
//
//		System.out.println("new/old:");
//		for (final Object o : currentModel.getAllVisibilitySources()) {
//			if (o.getClass() != GeosetAnim.class) {
//				visSourcesOld.add(shellFromObject(allVisShells, o));
//				System.out.println(shellFromObject(allVisShells, o).source.getName());
//			} else {
//				visSourcesOld.add(shellFromObject(allVisShells, ((GeosetAnim) o).getGeoset()));
//				System.out.println(shellFromObject(allVisShells, ((GeosetAnim) o).getGeoset()).source.getName());
//			}
//		}
//		visSourcesOld.add(VisibilityPanel.NOTVISIBLE);
//		visSourcesOld.add(VisibilityPanel.VISIBLE);
//		for (final Object o : importedModel.getAllVisibilitySources()) {
//			if (o.getClass() != GeosetAnim.class) {
//				visSourcesNew.add(shellFromObject(allVisShells, o));
//			} else {
//				visSourcesNew.add(shellFromObject(allVisShells, ((GeosetAnim) o).getGeoset()));
//			}
//		}
//		visSourcesNew.add(VisibilityPanel.NOTVISIBLE);
//		visSourcesNew.add(VisibilityPanel.VISIBLE);
////		visComponents = new DefaultListModel<>();
//	}

	public DefaultListModel<VisibilityShell> visibilityList() {
		System.out.println("visibilityList");
		final Object selection = mht.visTabs.getSelectedValue();
		mht.visibilityShells.clear();
		for (int i = 0; i < mht.geosetTabs.getTabCount(); i++) {
			final GeosetPanel gp = (GeosetPanel) mht.geosetTabs.getComponentAt(i);
			for (final Layer x : gp.getSelectedMaterial().getLayers()) {
				getAndAddVisShell(x);

			}
		}
		for (int i = 0; i < mht.geosetTabs.getTabCount(); i++) {
			final GeosetPanel gp = (GeosetPanel) mht.geosetTabs.getComponentAt(i);
			if (gp.doImport.isSelected()) {
				final Geoset ga = gp.geoset;
				getAndAddVisShell(ga);
			}
		}

		System.out.println("CurrModel");
		// The current's
		final EditableModel model = mht.currentModel;
		for (final Light x : model.getLights()) {
			getAndAddVisShell(x);
		}
		for (final Attachment x : model.getAttachments()) {
			getAndAddVisShell(x);
		}
		for (final ParticleEmitter x : model.getParticleEmitters()) {
			getAndAddVisShell(x);
		}
		for (final ParticleEmitter2 x : model.getParticleEmitter2s()) {
			getAndAddVisShell(x);
		}
		for (final RibbonEmitter x : model.getRibbonEmitters()) {
			getAndAddVisShell(x);
		}
		for (final ParticleEmitterPopcorn x : model.getPopcornEmitters()) {
			getAndAddVisShell(x);
		}

		for (final ObjectShell os : mht.objectShells) {
			// we don't touch camera "object" panels (which aren't idobjects)
			if (os.getShouldImport() && (os.getIdObject() != null)) {
				getAndAddVisShell(os.getIdObject());
			}
		}

		System.out.println("done visConp");
		mht.visTabs.setSelectedValue(selection, true);
		return mht.visibilityShells;
	}

	public void getAndAddVisShell(VisibilitySource x) {
		VisibilityShell vs = mht.allVisShellsMap.get(x);
		if (!mht.visibilityShells.contains(vs) && (vs != null)) {
			mht.visibilityShells.addElement(vs);
		}
	}

//	public DefaultListModel<VisibilityPanel> visibilityList() {
//		System.out.println("visibilityList");
//		final Object selection = mht.visTabs.getSelectedValue();
//		mht.visibilityPanels.clear();
//		mht.visibilityShells.clear();
//		for (int i = 0; i < mht.geosetTabs.getTabCount(); i++) {
//			final GeosetPanel gp = (GeosetPanel) mht.geosetTabs.getComponentAt(i);
//			for (final Layer x : gp.getSelectedMaterial().getLayers()) {
//				final VisibilityPanel vs = visPaneFromObject(mht.allVisShellPanes, x);
////				if (!vsiSourceToVisPanel.containsValue(vs) && (vs != null)) {
////					vsiSourceToVisPanel.put(x, vs);
////				}
//				if (!mht.visibilityPanels.contains(vs) && (vs != null)) {
//					mht.visibilityPanels.addElement(vs);
//				}
//				if (!mht.visibilityShells.contains(vs) && (vs != null)) {
//					mht.visibilityShells.addElement(vsiSourceToVisShell.get(x));
//				}
//
//			}
//		}
//		for (int i = 0; i < mht.geosetTabs.getTabCount(); i++) {
//			final GeosetPanel gp = (GeosetPanel) mht.geosetTabs.getComponentAt(i);
//			if (gp.doImport.isSelected()) {
//				final Geoset ga = gp.geoset;
//				final VisibilityPanel vs = visPaneFromObject(mht.allVisShellPanes, ga);
//				if (!mht.visibilityPanels.contains(vs) && (vs != null)) {
//					mht.visibilityPanels.addElement(vs);
//				}
//				if (!mht.visibilityPanels.contains(vs) && (vs != null)) {
//					mht.visibilityPanels.addElement(vs);
//				}
//			}
//		}
//
//		System.out.println("CurrModel");
//		// The current's
//		final EditableModel model = mht.currentModel;
//		for (final Light x : model.getLights()) {
//			final VisibilityPanel vs = visPaneFromObject(mht.allVisShellPanes, x);
//			if (!mht.visibilityPanels.contains(vs) && (vs != null)) {
//				mht.visibilityPanels.addElement(vs);
//			}
//			if (!mht.visibilityShells.contains(vs) && (vs != null)) {
//				mht.visibilityShells.addElement(vsiSourceToVisShell.get(x));
//			}
//		}
//		for (final Attachment x : model.getAttachments()) {
//			final VisibilityPanel vs = visPaneFromObject(mht.allVisShellPanes, x);
//			if (!mht.visibilityPanels.contains(vs) && (vs != null)) {
//				mht.visibilityPanels.addElement(vs);
//			}
//			if (!mht.visibilityShells.contains(vs) && (vs != null)) {
//				mht.visibilityShells.addElement(vsiSourceToVisShell.get(x));
//			}
//		}
//		for (final ParticleEmitter x : model.getParticleEmitters()) {
//			final VisibilityPanel vs = visPaneFromObject(mht.allVisShellPanes, x);
//			if (!mht.visibilityPanels.contains(vs) && (vs != null)) {
//				mht.visibilityPanels.addElement(vs);
//			}
//			if (!mht.visibilityShells.contains(vs) && (vs != null)) {
//				mht.visibilityShells.addElement(vsiSourceToVisShell.get(x));
//			}
//		}
//		for (final ParticleEmitter2 x : model.getParticleEmitter2s()) {
//			final VisibilityPanel vs = visPaneFromObject(mht.allVisShellPanes, x);
//			if (!mht.visibilityPanels.contains(vs) && (vs != null)) {
//				mht.visibilityPanels.addElement(vs);
//			}
//			if (!mht.visibilityShells.contains(vs) && (vs != null)) {
//				mht.visibilityShells.addElement(vsiSourceToVisShell.get(x));
//			}
//		}
//		for (final RibbonEmitter x : model.getRibbonEmitters()) {
//			final VisibilityPanel vs = visPaneFromObject(mht.allVisShellPanes, x);
//			if (!mht.visibilityPanels.contains(vs) && (vs != null)) {
//				mht.visibilityPanels.addElement(vs);
//			}
//			if (!mht.visibilityShells.contains(vs) && (vs != null)) {
//				mht.visibilityShells.addElement(vsiSourceToVisShell.get(x));
//			}
//		}
//		for (final ParticleEmitterPopcorn x : model.getPopcornEmitters()) {
//			final VisibilityPanel vs = visPaneFromObject(mht.allVisShellPanes, x);
//			if (!mht.visibilityPanels.contains(vs) && (vs != null)) {
//				mht.visibilityPanels.addElement(vs);
//			}
//			if (!mht.visibilityShells.contains(vs) && (vs != null)) {
//				mht.visibilityShells.addElement(vsiSourceToVisShell.get(x));
//			}
//		}
//
//		for (final ObjectShell os : mht.objectShells) {
//			if (os.getShouldImport() && (os.getIdObject() != null))
//			// we don't touch camera "object" panels (which aren't idobjects)
//			{
//				final VisibilityPanel vs = visPaneFromObject(mht.allVisShellPanes, os.getIdObject());
//				if (!mht.visibilityPanels.contains(vs) && (vs != null)) {
//					mht.visibilityPanels.addElement(vs);
//				}
//				if (!mht.visibilityShells.contains(vs) && (vs != null)) {
//					mht.visibilityShells.addElement(vsiSourceToVisShell.get(os.getIdObject()));
//				}
//			}
//		}
//
//		System.out.println("done visConp");
//		mht.visTabs.setSelectedValue(selection, true);
//		return mht.visibilityPanels;
//	}

//	public DefaultListModel<VisibilityPanel> visibilityList2() {
//		System.out.println("visibilityList");
//		final Object selection = mht.visTabs.getSelectedValue();
//		mht.visibilityPanels.clear();
//		for (int i = 0; i < mht.geosetTabs.getTabCount(); i++) {
//			final GeosetPanel gp = (GeosetPanel) mht.geosetTabs.getComponentAt(i);
//			for (final Layer l : gp.getSelectedMaterial().getLayers()) {
//				final VisibilityPanel vs = visPaneFromObject(mht.allVisShellPanes, l);
//				if (!mht.visibilityPanels.contains(vs) && (vs != null)) {
//					mht.visibilityPanels.addElement(vs);
//				}
//			}
//		}
//		for (int i = 0; i < mht.geosetTabs.getTabCount(); i++) {
//			final GeosetPanel gp = (GeosetPanel) mht.geosetTabs.getComponentAt(i);
//			if (gp.doImport.isSelected()) {
//				final Geoset ga = gp.geoset;
//				final VisibilityPanel vs = visPaneFromObject(mht.allVisShellPanes, ga);
//				if (!mht.visibilityPanels.contains(vs) && (vs != null)) {
//					mht.visibilityPanels.addElement(vs);
//				}
//			}
//		}
//
//		System.out.println("CurrModel");
//		// The current's
//		final EditableModel model = mht.currentModel;
//		for (final Light l : model.getLights()) {
//			final VisibilityPanel vs = visPaneFromObject(mht.allVisShellPanes, l);
//			if (!mht.visibilityPanels.contains(vs) && (vs != null)) {
//				mht.visibilityPanels.addElement(vs);
//			}
//		}
//		for (final Attachment a : model.getAttachments()) {
//			final VisibilityPanel vs = visPaneFromObject(mht.allVisShellPanes, a);
//			if (!mht.visibilityPanels.contains(vs) && (vs != null)) {
//				mht.visibilityPanels.addElement(vs);
//			}
//		}
//		for (final ParticleEmitter x : model.getParticleEmitters()) {
//			final VisibilityPanel vs = visPaneFromObject(mht.allVisShellPanes, x);
//			if (!mht.visibilityPanels.contains(vs) && (vs != null)) {
//				mht.visibilityPanels.addElement(vs);
//			}
//		}
//		for (final ParticleEmitter2 x : model.getParticleEmitter2s()) {
//			final VisibilityPanel vs = visPaneFromObject(mht.allVisShellPanes, x);
//			if (!mht.visibilityPanels.contains(vs) && (vs != null)) {
//				mht.visibilityPanels.addElement(vs);
//			}
//		}
//		for (final RibbonEmitter x : model.getRibbonEmitters()) {
//			final VisibilityPanel vs = visPaneFromObject(mht.allVisShellPanes, x);
//			if (!mht.visibilityPanels.contains(vs) && (vs != null)) {
//				mht.visibilityPanels.addElement(vs);
//			}
//		}
//		for (final ParticleEmitterPopcorn x : model.getPopcornEmitters()) {
//			final VisibilityPanel vs = visPaneFromObject(mht.allVisShellPanes, x);
//			if (!mht.visibilityPanels.contains(vs) && (vs != null)) {
//				mht.visibilityPanels.addElement(vs);
//			}
//		}
//
//		for (final ObjectShell os : mht.objectShells) {
//			if (os.getShouldImport() && (os.getIdObject() != null))
//			// we don't touch camera "object" panels (which aren't idobjects)
//			{
//				final VisibilityPanel vs = visPaneFromObject(mht.allVisShellPanes, os.getIdObject());
//				if (!mht.visibilityPanels.contains(vs) && (vs != null)) {
//					mht.visibilityPanels.addElement(vs);
//				}
//			}
//		}
//
//		System.out.println("done visConp");
//		mht.visTabs.setSelectedValue(selection, true);
//		return mht.visibilityPanels;
//	}
}

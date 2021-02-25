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

	ArrayList<VisibilityShell> allVisShells;
	BiMap<VisibilitySource, VisibilityShell> vsiSourceToVisShell = new BiMap<>();
	BiMap<VisibilitySource, VisibilityPanel> vsiSourceToVisPanel = new BiMap<>();
	VisibilityPanel currVisibilityPanel;
	MultiVisibilityPanel multiVisPanel;

	//	ArrayList<Object> visSourcesOld;
	private ModelHolderThing mht;

	public VisibilityEditPanel(ModelHolderThing mht) {
		this.mht = mht;
	}

	public JPanel makeVisPanel() {
		JPanel visPanel = new JPanel(new MigLayout("gap 0, fill", "", "[][grow]"));
//		visPanel.setPreferredSize(new Dimension(500, 500));
//		addTab("Visibility", orangeIcon, visPanel, "Controls the visibility of portions of the model.");

		JPanel topPanel = getTopPanel();

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

//		MultiVisibilityPanel multiVisPanel = new MultiVisibilityPanel(mht, visRenderer);
		multiVisPanel = new MultiVisibilityPanel(mht, visRenderer);
		visPanelCards.add(new JPanel(), "blank");
		visPanelCards.add(multiVisPanel, "multiple");
		mht.visTabs.setModel(mht.visibilityShells);
		mht.visTabs.setCellRenderer(new VisPaneListCellRenderer(mht.receivingModel));
		mht.visTabs.addListSelectionListener(e -> visTabsValueChanged(multiVisPanel, visCardLayout, visPanelCards, mht.visTabs));
		mht.visTabs.setSelectedIndex(0);
//		visPanelCards.setBorder(BorderFactory.createLineBorder(Color.blue.darker()));

		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(mht.visTabs), visPanelCards);

		visPanel.add(topPanel, "spanx, align center, wrap");
		visPanel.add(splitPane, "wrap");

		System.out.println("done visPanel");
		return visPanel;
	}

	private JPanel getTopPanel() {
		JPanel topPanel = new JPanel(new MigLayout("gap 0", "", "[]8[]8[]"));

		JButton allInvisButton = createButton("All Invisible in Exotic Anims", e -> allVisButton(mht.allVisShells, mht.receivingModel, VisibilityPanel.NOTVISIBLE), "Forces everything to be always invisibile in animations other than their own original animations.");
		topPanel.add(allInvisButton, "align center, wrap");

		JButton allVisButton = createButton("All Visible in Exotic Anims", e -> allVisButton(mht.allVisShells, mht.receivingModel, VisibilityPanel.VISIBLE), "Forces everything to be always visibile in animations other than their own original animations.");
		topPanel.add(allVisButton, "align center, wrap");

		JButton selSimButton = createButton("Select Similar Options", e -> selSimButton(mht.allVisShells), "Similar components will be selected as visibility sources in exotic animations.");
		topPanel.add(selSimButton, "align center, wrap");
		return topPanel;
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
			System.out.println("selected visTab: " + visTabs.getSelectedValue().toString());
			currVisibilityPanel.setCurrSourceShell(visTabs.getSelectedValue());
		} else {
			visCardLayout.show(visPanelCards, "multiple");

//			int tempIndexOld = selectedValuesList.get(0).oldSourcesBox.getSelectedIndex();
//			int tempIndexNew = selectedValuesList.get(0).newSourcesBox.getSelectedIndex();
//			boolean selectedt = selectedValuesList.get(0).favorOld.isSelected();
			VisibilityShell tempIndexOld = selectedValuesList.get(0).getOldVisSource();
			VisibilityShell tempIndexNew = selectedValuesList.get(0).getNewVisSource();
			boolean selectedt = selectedValuesList.get(0).getFavorOld();


			boolean dif = selectedValuesList.stream().anyMatch(VisibilityShell::getFavorOld);
			if (!dif) {
				multiVisPanel.favorOld.setSelected(selectedt);
			}

			boolean difBoxOld = selectedValuesList.stream().anyMatch(visibilityShell -> visibilityShell.getOldVisSource() != tempIndexOld);
			if (difBoxOld) {
				multiVisPanel.setMultipleOld();
			} else {
//				VisibilityPanel.oldSourcesBox.setSelectedItem(tempIndexOld);
				multiVisPanel.oldSourcesBox.setSelectedItem(tempIndexOld);
			}

			boolean difBoxNew = selectedValuesList.stream().anyMatch(visibilityShell -> visibilityShell.getNewVisSource() != tempIndexNew);
			if (difBoxNew) {
				multiVisPanel.setMultipleNew();
			} else {
//				VisibilityPanel.newSourcesBox.setSelectedItem(tempIndexNew);
				multiVisPanel.newSourcesBox.setSelectedItem(tempIndexNew);
			}
		}
	}

	private void allVisButton(ArrayList<VisibilityShell> allVisShellPanes, EditableModel model, String visible) {
		for (final VisibilityShell visibilityShell : allVisShellPanes) {
			if (visibilityShell.model == model) {
//				VisibilityPanel.newSourcesBox.setSelectedItem(visible);
				multiVisPanel.newSourcesBox.setSelectedItem(visible);
			} else {
//				VisibilityPanel.oldSourcesBox.setSelectedItem(visible);
				multiVisPanel.oldSourcesBox.setSelectedItem(visible);
			}
		}
	}

//	private void selSimButton(ArrayList<VisibilityShell> allVisShellPanes) {
//		for (final VisibilityShell visibilityShell : allVisShellPanes) {
////			VisibilityPanel.selectSimilarOptions(visibilityShell);
//			if (VisibilityPanel.oldSourcesBox.getSelectedItem() instanceof VisibilityShell) {
//				visibilityShell.setOldVisSource((VisibilityShell) VisibilityPanel.oldSourcesBox.getSelectedItem());
//			}
//			if (VisibilityPanel.newSourcesBox.getSelectedItem() instanceof VisibilityShell) {
//				visibilityShell.setNewVisSource((VisibilityShell) VisibilityPanel.newSourcesBox.getSelectedItem());
//			}
//		}
//	}

	private void selSimButton(ArrayList<VisibilityShell> allVisShellPanes) {
		for (final VisibilityShell visibilityShell : allVisShellPanes) {
//			VisibilityPanel.selectSimilarOptions(visibilityShell);
			if (multiVisPanel.oldSourcesBox.getSelectedItem() instanceof VisibilityShell) {
				visibilityShell.setOldVisSource((VisibilityShell) multiVisPanel.oldSourcesBox.getSelectedItem());
			}
			if (multiVisPanel.newSourcesBox.getSelectedItem() instanceof VisibilityShell) {
				visibilityShell.setNewVisSource((VisibilityShell) multiVisPanel.newSourcesBox.getSelectedItem());
			}
		}
	}

	public void initVisibilityList() {
		mht.recModelVisSources = new ArrayList<>();
		mht.donModelVisSources = new ArrayList<>();
		allVisShells = new ArrayList<>();
//		BiMap<VisibilitySource, VisibilityShell> vsiSourceToVisShell = new BiMap<>();
		List<VisibilityShell> donModelAllVisShells = new ArrayList<>();
		List<VisibilityShell> recModelAllVisShells = new ArrayList<>();

		EditableModel recModel = mht.receivingModel;
		final List<VisibilitySource> tempList = new ArrayList<>();
		for (final Material mat : recModel.getMaterials()) {
			for (final Layer x : mat.getLayers()) {
				createAndAddVisShell(recModel, tempList, x, recModelAllVisShells, vsiSourceToVisShell);
			}
		}
		for (final Geoset x : recModel.getGeosets()) {
			createAndAddVisShell(recModel, tempList, x, recModelAllVisShells, vsiSourceToVisShell);
		}
		for (final Light x : recModel.getLights()) {
			createAndAddVisShell(recModel, tempList, x, recModelAllVisShells, vsiSourceToVisShell);
		}
		for (final Attachment x : recModel.getAttachments()) {
			createAndAddVisShell(recModel, tempList, x, recModelAllVisShells, vsiSourceToVisShell);
		}
		for (final ParticleEmitter x : recModel.getParticleEmitters()) {
			createAndAddVisShell(recModel, tempList, x, recModelAllVisShells, vsiSourceToVisShell);
		}
		for (final ParticleEmitter2 x : recModel.getParticleEmitter2s()) {
			createAndAddVisShell(recModel, tempList, x, recModelAllVisShells, vsiSourceToVisShell);
		}
		for (final RibbonEmitter x : recModel.getRibbonEmitters()) {
			createAndAddVisShell(recModel, tempList, x, recModelAllVisShells, vsiSourceToVisShell);
		}
		for (final ParticleEmitterPopcorn x : recModel.getPopcornEmitters()) {
			createAndAddVisShell(recModel, tempList, x, recModelAllVisShells, vsiSourceToVisShell);
		}

		EditableModel donModel = mht.donatingModel;
		for (final Material mat : donModel.getMaterials()) {
			for (final Layer x : mat.getLayers()) {
				createAndAddVisShell(donModel, tempList, x, donModelAllVisShells, vsiSourceToVisShell);
			}
		}
		for (final Geoset x : donModel.getGeosets()) {
			createAndAddVisShell(donModel, tempList, x, donModelAllVisShells, vsiSourceToVisShell);
		}
		for (final Light x : donModel.getLights()) {
			createAndAddVisShell(donModel, tempList, x, donModelAllVisShells, vsiSourceToVisShell);
		}
		for (final Attachment x : donModel.getAttachments()) {
			createAndAddVisShell(donModel, tempList, x, donModelAllVisShells, vsiSourceToVisShell);
		}
		for (final ParticleEmitter x : donModel.getParticleEmitters()) {
			createAndAddVisShell(donModel, tempList, x, donModelAllVisShells, vsiSourceToVisShell);
		}
		for (final ParticleEmitter2 x : donModel.getParticleEmitter2s()) {
			createAndAddVisShell(donModel, tempList, x, donModelAllVisShells, vsiSourceToVisShell);
		}
		for (final RibbonEmitter x : donModel.getRibbonEmitters()) {
			createAndAddVisShell(donModel, tempList, x, donModelAllVisShells, vsiSourceToVisShell);
		}
		for (final ParticleEmitterPopcorn x : donModel.getPopcornEmitters()) {
			createAndAddVisShell(donModel, tempList, x, donModelAllVisShells, vsiSourceToVisShell);
		}


		System.out.println("new/old:");
		for (final VisibilitySource o : mht.receivingModel.getAllVisibilitySources()) {
			if (o.getClass() != GeosetAnim.class) {
				mht.recModelVisSources.add(vsiSourceToVisShell.get(o));
				if (vsiSourceToVisShell.get(o) != null) {
					System.out.println(vsiSourceToVisShell.get(o).source.getName());
				}

//				visSourcesOld.add(shellFromObject(allVisShells, o));
//				System.out.println(shellFromObject(allVisShells, o).source.getName());
			} else {
				mht.recModelVisSources.add(vsiSourceToVisShell.get(((GeosetAnim) o).getGeoset()));

				if (vsiSourceToVisShell.get(o) != null) {
					System.out.println(vsiSourceToVisShell.get(((GeosetAnim) o).getGeoset()).source.getName());
				}

//				visSourcesOld.add(shellFromObject(allVisShells, ((GeosetAnim) o).getGeoset()));
//				System.out.println(shellFromObject(allVisShells, ((GeosetAnim) o).getGeoset()).source.getName());
			}
		}

//		mht.visSourcesOld.add(VisibilityPanel.NOTVISIBLE);
//		mht.visSourcesOld.add(VisibilityPanel.VISIBLE);
//		mht.visSourcesOld.add(new VisibilityShell(true));
//		mht.visSourcesOld.add(new VisibilityShell(false));
		mht.recModelVisSources.add(0, new VisibilityShell(true));
		mht.recModelVisSources.add(1, new VisibilityShell(false));


		for (final VisibilitySource o : mht.donatingModel.getAllVisibilitySources()) {
			if (o.getClass() != GeosetAnim.class) {
				mht.donModelVisSources.add(vsiSourceToVisShell.get(o));
				if (vsiSourceToVisShell.get(o) != null) {
					System.out.println(vsiSourceToVisShell.get(o).source.getName());
				}
			} else {
				mht.donModelVisSources.add(vsiSourceToVisShell.get(((GeosetAnim) o).getGeoset()));

				if (vsiSourceToVisShell.get(o) != null) {
					System.out.println(vsiSourceToVisShell.get(((GeosetAnim) o).getGeoset()).source.getName());
				}
			}
		}
//		mht.visSourcesNew.add(VisibilityPanel.NOTVISIBLE);
//		mht.visSourcesNew.add(VisibilityPanel.VISIBLE);
		mht.donModelVisSources.add(0, new VisibilityShell(true));
		mht.donModelVisSources.add(1, new VisibilityShell(false));
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

	public DefaultListModel<VisibilityShell> visibilityList() {
		System.out.println("visibilityList");
		final Object selection = mht.visTabs.getSelectedValue();
		mht.visibilityShells.clear();
//		for (int i = 0; i < mht.geosetTabs.getTabCount(); i++) {
//			final GeosetPanel gp = (GeosetPanel) mht.geosetTabs.getComponentAt(i);
//			for (final Layer x : gp.getSelectedMaterial().getLayers()) {
//				getAndAddVisShell(x);
//
//			}
//		}
//		for (int i = 0; i < mht.geosetTabs.getTabCount(); i++) {
//			final GeosetPanel gp = (GeosetPanel) mht.geosetTabs.getComponentAt(i);
//			if (gp.doImport.isSelected()) {
//				final Geoset ga = gp.geoset;
//				getAndAddVisShell(ga);
//			}
//		}

		System.out.println("CurrModel");
		// The current's
		final EditableModel model = mht.receivingModel;
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
}

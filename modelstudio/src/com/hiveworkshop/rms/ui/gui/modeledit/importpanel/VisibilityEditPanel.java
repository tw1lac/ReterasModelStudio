package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.model.*;
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
	ArrayList<Object> visSourcesOld;
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
		for (final VisibilityShell vs : allVisShells) {
			final VisibilityPanel vp = new VisibilityPanel(vs, new DefaultComboBoxModel<>(visSourcesOld.toArray()), new DefaultComboBoxModel<>(mht.visSourcesNew.toArray()), visRenderer);

			mht.allVisShellPanes.add(vp);

			visPanelCards.add(vp, vp.title.getText());
		}

		MultiVisibilityPanel multiVisPanel = new MultiVisibilityPanel(new DefaultComboBoxModel<>(visSourcesOld.toArray()), new DefaultComboBoxModel<>(mht.visSourcesNew.toArray()), visRenderer);
		visPanelCards.add(new JPanel(), "blank");
		visPanelCards.add(multiVisPanel, "multiple");
		mht.visTabs.setModel(mht.visibilityPanels);
		mht.visTabs.setCellRenderer(new VisPaneListCellRenderer(mht.currentModel));
		mht.visTabs.addListSelectionListener(e -> visTabsValueChanged(multiVisPanel, visCardLayout, visPanelCards, mht.visTabs));
		mht.visTabs.setSelectedIndex(0);
//		visPanelCards.setBorder(BorderFactory.createLineBorder(Color.blue.darker()));

		JButton allInvisButton = createButton("All Invisible in Exotic Anims", e -> allVisButton(mht.allVisShellPanes, mht.currentModel, VisibilityPanel.NOTVISIBLE), "Forces everything to be always invisibile in animations other than their own original animations.");
		visPanel.add(allInvisButton);

		JButton allVisButton = createButton("All Visible in Exotic Anims", e -> allVisButton(mht.allVisShellPanes, mht.currentModel, VisibilityPanel.VISIBLE), "Forces everything to be always visibile in animations other than their own original animations.");
		visPanel.add(allVisButton);

		JButton selSimButton = createButton("Select Similar Options", e -> selSimButton(mht.allVisShellPanes), "Similar components will be selected as visibility sources in exotic animations.");
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

	private static void visTabsValueChanged(MultiVisibilityPanel multiVisPanel, CardLayout visCardLayout, JPanel visPanelCards, JList<VisibilityPanel> visTabs) {
		List<VisibilityPanel> selectedValuesList = visTabs.getSelectedValuesList();
		if (selectedValuesList.size() < 1) {
			visCardLayout.show(visPanelCards, "blank");
		} else if (selectedValuesList.size() == 1) {
			visCardLayout.show(visPanelCards, visTabs.getSelectedValue().title.getText());
		} else {
			visCardLayout.show(visPanelCards, "multiple");

			int tempIndexOld = selectedValuesList.get(0).oldSourcesBox.getSelectedIndex();
			int tempIndexNew = selectedValuesList.get(0).newSourcesBox.getSelectedIndex();
			boolean selectedt = selectedValuesList.get(0).favorOld.isSelected();


			boolean dif = selectedValuesList.stream().anyMatch(visibilityPanel -> visibilityPanel.favorOld.isSelected());
			if (!dif) {
				multiVisPanel.favorOld.setSelected(selectedt);
			}

			boolean difBoxOld = selectedValuesList.stream().anyMatch(visibilityPanel -> visibilityPanel.oldSourcesBox.getSelectedIndex() != tempIndexOld);
			if (difBoxOld) {
				multiVisPanel.setMultipleOld();
			} else {
				multiVisPanel.oldSourcesBox.setSelectedIndex(tempIndexOld);
			}

			boolean difBoxNew = selectedValuesList.stream().anyMatch(visibilityPanel -> visibilityPanel.newSourcesBox.getSelectedIndex() != tempIndexNew);
			if (difBoxNew) {
				multiVisPanel.setMultipleNew();
			} else {
				multiVisPanel.newSourcesBox.setSelectedIndex(tempIndexNew);
			}
		}
	}

	public static VisibilityShell shellFromObject(ArrayList<VisibilityShell> allVisShells, final Object o) {
		for (final VisibilityShell v : allVisShells) {
			if (v.source == o) {
				return v;
			}
		}
		return null;
	}

	public static VisibilityPanel visPaneFromObject(ArrayList<VisibilityPanel> allVisShellPanes, final Object o) {
		for (final VisibilityPanel vp : allVisShellPanes) {
			if (vp.sourceShell.source == o) {
				return vp;
			}
		}
		return null;
	}

	private static void allVisButton(ArrayList<VisibilityPanel> allVisShellPanes, EditableModel currentModel, String visible) {
		for (final VisibilityPanel vPanel : allVisShellPanes) {
			if (vPanel.sourceShell.model == currentModel) {
				vPanel.newSourcesBox.setSelectedItem(visible);
			} else {
				vPanel.oldSourcesBox.setSelectedItem(visible);
			}
		}
	}

	private static void selSimButton(ArrayList<VisibilityPanel> allVisShellPanes) {
		for (final VisibilityPanel vPanel : allVisShellPanes) {
			vPanel.selectSimilarOptions();
		}
	}

	public void initVisibilityList() {
		visSourcesOld = new ArrayList<>();
		mht.visSourcesNew = new ArrayList<>();
		allVisShells = new ArrayList<>();
		EditableModel model = mht.currentModel;
		final List tempList = new ArrayList();
		for (final Material mat : model.getMaterials()) {
			for (final Layer lay : mat.getLayers()) {
				final VisibilityShell vs = new VisibilityShell(lay, model);
				if (!tempList.contains(lay)) {
					tempList.add(lay);
					allVisShells.add(vs);
				}
			}
		}
		for (final Geoset ga : model.getGeosets()) {
			final VisibilityShell vs = new VisibilityShell(ga, model);
			if (!tempList.contains(ga)) {
				tempList.add(ga);
				allVisShells.add(vs);
			}
		}
		for (final Object l : model.getLights()) {
			final VisibilityShell vs = new VisibilityShell((Named) l, model);
			if (!tempList.contains(l)) {
				tempList.add(l);
				allVisShells.add(vs);
			}
		}
		for (final Object a : model.getAttachments()) {
			final VisibilityShell vs = new VisibilityShell((Named) a, model);
			if (!tempList.contains(a)) {
				tempList.add(a);
				allVisShells.add(vs);
			}
		}
		for (final Object x : model.getParticleEmitters()) {
			final VisibilityShell vs = new VisibilityShell((Named) x, model);
			if (!tempList.contains(x)) {
				tempList.add(x);
				allVisShells.add(vs);
			}
		}
		for (final Object x : model.getParticleEmitter2s()) {
			final VisibilityShell vs = new VisibilityShell((Named) x, model);
			if (!tempList.contains(x)) {
				tempList.add(x);
				allVisShells.add(vs);
			}
		}
		for (final Object x : model.getRibbonEmitters()) {
			final VisibilityShell vs = new VisibilityShell((Named) x, model);
			if (!tempList.contains(x)) {
				tempList.add(x);
				allVisShells.add(vs);
			}
		}
		for (final Object x : model.getPopcornEmitters()) {
			final VisibilityShell vs = new VisibilityShell((Named) x, model);
			if (!tempList.contains(x)) {
				tempList.add(x);
				allVisShells.add(vs);
			}
		}
		model = mht.importModel;
		for (final Material mat : model.getMaterials()) {
			for (final Layer lay : mat.getLayers()) {
				final VisibilityShell vs = new VisibilityShell(lay, model);
				if (!tempList.contains(lay)) {
					tempList.add(lay);
					allVisShells.add(vs);
				}
			}
		}
		for (final Geoset ga : model.getGeosets()) {
			final VisibilityShell vs = new VisibilityShell(ga, model);
			if (!tempList.contains(ga)) {
				tempList.add(ga);
				allVisShells.add(vs);
			}
		}
		for (final Object l : model.getLights()) {
			final VisibilityShell vs = new VisibilityShell((Named) l, model);
			if (!tempList.contains(l)) {
				tempList.add(l);
				allVisShells.add(vs);
			}
		}
		for (final Object a : model.getAttachments()) {
			final VisibilityShell vs = new VisibilityShell((Named) a, model);
			if (!tempList.contains(a)) {
				tempList.add(a);
				allVisShells.add(vs);
			}
		}
		for (final Object x : model.getParticleEmitters()) {
			final VisibilityShell vs = new VisibilityShell((Named) x, model);
			if (!tempList.contains(x)) {
				tempList.add(x);
				allVisShells.add(vs);
			}
		}
		for (final Object x : model.getParticleEmitter2s()) {
			final VisibilityShell vs = new VisibilityShell((Named) x, model);
			if (!tempList.contains(x)) {
				tempList.add(x);
				allVisShells.add(vs);
			}
		}
		for (final Object x : model.getRibbonEmitters()) {
			final VisibilityShell vs = new VisibilityShell((Named) x, model);
			if (!tempList.contains(x)) {
				tempList.add(x);
				allVisShells.add(vs);
			}
		}
		for (final Object x : model.getPopcornEmitters()) {
			final VisibilityShell vs = new VisibilityShell((Named) x, model);
			if (!tempList.contains(x)) {
				tempList.add(x);
				allVisShells.add(vs);
			}
		}

		System.out.println("allVisShells:");
		for (final VisibilityShell vs : allVisShells) {
			System.out.println(vs.source.getName());
		}

		System.out.println("new/old:");
		for (final Object o : mht.currentModel.getAllVisibilitySources()) {
			if (o.getClass() != GeosetAnim.class) {
				visSourcesOld.add(shellFromObject(allVisShells, o));
				System.out.println(shellFromObject(allVisShells, o).source.getName());
			} else {
				visSourcesOld.add(shellFromObject(allVisShells, ((GeosetAnim) o).getGeoset()));
				System.out.println(shellFromObject(allVisShells, ((GeosetAnim) o).getGeoset()).source.getName());
			}
		}
		visSourcesOld.add(VisibilityPanel.NOTVISIBLE);
		visSourcesOld.add(VisibilityPanel.VISIBLE);
		for (final Object o : mht.importModel.getAllVisibilitySources()) {
			if (o.getClass() != GeosetAnim.class) {
				mht.visSourcesNew.add(shellFromObject(allVisShells, o));
			} else {
				mht.visSourcesNew.add(shellFromObject(allVisShells, ((GeosetAnim) o).getGeoset()));
			}
		}
		mht.visSourcesNew.add(VisibilityPanel.NOTVISIBLE);
		mht.visSourcesNew.add(VisibilityPanel.VISIBLE);
//		visComponents = new DefaultListModel<>();
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

	public DefaultListModel<VisibilityPanel> visibilityList() {
		System.out.println("visibilityList");
		final Object selection = mht.visTabs.getSelectedValue();
		mht.visibilityPanels.clear();
		for (int i = 0; i < mht.geosetTabs.getTabCount(); i++) {
			final GeosetPanel gp = (GeosetPanel) mht.geosetTabs.getComponentAt(i);
			for (final Layer l : gp.getSelectedMaterial().getLayers()) {
				final VisibilityPanel vs = visPaneFromObject(mht.allVisShellPanes, l);
				if (!mht.visibilityPanels.contains(vs) && (vs != null)) {
					mht.visibilityPanels.addElement(vs);
				}
			}
		}
		for (int i = 0; i < mht.geosetTabs.getTabCount(); i++) {
			final GeosetPanel gp = (GeosetPanel) mht.geosetTabs.getComponentAt(i);
			if (gp.doImport.isSelected()) {
				final Geoset ga = gp.geoset;
				final VisibilityPanel vs = visPaneFromObject(mht.allVisShellPanes, ga);
				if (!mht.visibilityPanels.contains(vs) && (vs != null)) {
					mht.visibilityPanels.addElement(vs);
				}
			}
		}

		System.out.println("CurrModel");
		// The current's
		final EditableModel model = mht.currentModel;
		for (final Object l : model.sortedIdObjects(Light.class)) {
			final VisibilityPanel vs = visPaneFromObject(mht.allVisShellPanes, l);
			if (!mht.visibilityPanels.contains(vs) && (vs != null)) {
				mht.visibilityPanels.addElement(vs);
			}
		}
		for (final Object a : model.sortedIdObjects(Attachment.class)) {
			final VisibilityPanel vs = visPaneFromObject(mht.allVisShellPanes, a);
			if (!mht.visibilityPanels.contains(vs) && (vs != null)) {
				mht.visibilityPanels.addElement(vs);
			}
		}
		for (final Object x : model.sortedIdObjects(ParticleEmitter.class)) {
			final VisibilityPanel vs = visPaneFromObject(mht.allVisShellPanes, x);
			if (!mht.visibilityPanels.contains(vs) && (vs != null)) {
				mht.visibilityPanels.addElement(vs);
			}
		}
		for (final Object x : model.sortedIdObjects(ParticleEmitter2.class)) {
			final VisibilityPanel vs = visPaneFromObject(mht.allVisShellPanes, x);
			if (!mht.visibilityPanels.contains(vs) && (vs != null)) {
				mht.visibilityPanels.addElement(vs);
			}
		}
		for (final Object x : model.sortedIdObjects(RibbonEmitter.class)) {
			final VisibilityPanel vs = visPaneFromObject(mht.allVisShellPanes, x);
			if (!mht.visibilityPanels.contains(vs) && (vs != null)) {
				mht.visibilityPanels.addElement(vs);
			}
		}
		for (final Object x : model.sortedIdObjects(ParticleEmitterPopcorn.class)) {
			final VisibilityPanel vs = visPaneFromObject(mht.allVisShellPanes, x);
			if (!mht.visibilityPanels.contains(vs) && (vs != null)) {
				mht.visibilityPanels.addElement(vs);
			}
		}

		for (final ObjectShell os : mht.objectShells) {
			if (os.getShouldImport() && (os.getIdObject() != null))
			// we don't touch camera "object" panels (which aren't idobjects)
			{
				final VisibilityPanel vs = visPaneFromObject(mht.allVisShellPanes, os.getIdObject());
				if (!mht.visibilityPanels.contains(vs) && (vs != null)) {
					mht.visibilityPanels.addElement(vs);
				}
			}
		}

		System.out.println("done visConp");
		mht.visTabs.setSelectedValue(selection, true);
		return mht.visibilityPanels;
	}
}

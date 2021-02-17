package com.hiveworkshop.rms.ui.gui.modeledit.importpanel;

import com.hiveworkshop.rms.editor.model.*;
import com.hiveworkshop.rms.ui.gui.modeledit.BoneShell;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class VisibilityEditPanel {
//	JList<VisibilityPanel> visTabs = new JList<>();
//	EditableModel currentModel;
//	EditableModel importedModel;
//
//	DefaultListModel<VisibilityPanel> visComponents;
//	ArrayList<VisibilityPanel> allVisShellPanes = new ArrayList<>();
//	JButton allMatrOriginal = new JButton("Reset all Matrices");
//	JButton allMatrSameName = new JButton("Set all to available, original names");
//	DefaultListModel<BoneShell> futureBoneListEx = new DefaultListModel<>();
//	List<DefaultListModel<BoneShell>> futureBoneListExFixableItems = new ArrayList<>();
//	ArrayList<BoneShell> oldHelpers;
//	ArrayList<BoneShell> newHelpers;
//	ArrayList<VisibilityShell> allVisShells;
//	ArrayList<Object> visSourcesOld;
//	ArrayList<Object> visSourcesNew;
//	DefaultListModel<ObjectPanel> objectPanels = new DefaultListModel<>();
//	JList<ObjectPanel> objectTabs = new JList<>(objectPanels);
//	JTabbedPane geosetTabs = new JTabbedPane(JTabbedPane.LEFT, JTabbedPane.SCROLL_TAB_LAYOUT);

	JList<VisibilityPanel> visTabs;
	EditableModel currentModel;
	EditableModel importedModel;

	DefaultListModel<VisibilityPanel> visComponents;
	ArrayList<VisibilityPanel> allVisShellPanes;
	JButton allMatrOriginal;
	JButton allMatrSameName;
	DefaultListModel<BoneShell> futureBoneListEx;
	List<DefaultListModel<BoneShell>> futureBoneListExFixableItems;
	ArrayList<BoneShell> oldHelpers;
	ArrayList<BoneShell> newHelpers;
	ArrayList<VisibilityShell> allVisShells;
	//	ArrayList<VisibilityShell> allVisShells;
	ArrayList<Object> visSourcesOld;
	ArrayList<Object> visSourcesNew;
	Vector<ObjectPanel> objectPanels;
	JList<ObjectPanel> objectTabs;
	JTabbedPane geosetTabs;

	public VisibilityEditPanel(JList<VisibilityPanel> visTabs,
	                           EditableModel currentModel,
	                           EditableModel importedModel,
	                           DefaultListModel<VisibilityPanel> visComponents,
	                           ArrayList<VisibilityPanel> allVisShellPanes,
	                           JButton allMatrOriginal,
	                           JButton allMatrSameName,
	                           DefaultListModel<BoneShell> futureBoneListEx,
	                           List<DefaultListModel<BoneShell>> futureBoneListExFixableItems,
	                           ArrayList<BoneShell> oldHelpers,
	                           ArrayList<BoneShell> newHelpers,
	                           ArrayList<Object> visSourcesNew,
	                           Vector<ObjectPanel> objectPanels,
	                           JList<ObjectPanel> objectTabs,
	                           JTabbedPane geosetTabs
	) {

		this.visTabs = visTabs;
		this.currentModel = currentModel;
		this.importedModel = importedModel;
		this.visComponents = visComponents;
		this.allVisShellPanes = allVisShellPanes;
		this.allMatrOriginal = allMatrOriginal;
		this.allMatrSameName = allMatrSameName;
		this.futureBoneListEx = futureBoneListEx;
		this.futureBoneListExFixableItems = futureBoneListExFixableItems;
		this.oldHelpers = oldHelpers;
		this.newHelpers = newHelpers;
//		this.allVisShells = allVisShells;
//		this.visSourcesOld = visSourcesOld;
		this.visSourcesNew = visSourcesNew;
		this.objectPanels = objectPanels;
		this.objectTabs = objectTabs;
		this.geosetTabs = geosetTabs;
	}

	private static void visTabsValueChanged(MultiVisibilityPanel multiVisPanel, CardLayout visCardLayout, JPanel visPanelCards, JList<VisibilityPanel> visTabs) {
		if (visTabs.getSelectedValuesList().toArray().length < 1) {
			visCardLayout.show(visPanelCards, "blank");
		} else if (visTabs.getSelectedValuesList().toArray().length == 1) {
			visCardLayout.show(visPanelCards, visTabs.getSelectedValue().title.getText());
		} else if (visTabs.getSelectedValuesList().toArray().length > 1) {
			visCardLayout.show(visPanelCards, "multiple");
			final Object[] selected = visTabs.getSelectedValuesList().toArray();

			boolean dif = false;
			boolean set = false;
			boolean selectedt = false;

			boolean difBoxOld = false;
			boolean difBoxNew = false;
			int tempIndexOld = -99;
			int tempIndexNew = -99;

			for (int i = 0; (i < selected.length) && !dif; i++) {
				final VisibilityPanel temp = (VisibilityPanel) selected[i];
				if (!set) {
					set = true;
					selectedt = temp.favorOld.isSelected();
				} else if (selectedt != temp.favorOld.isSelected()) {
					dif = true;
				}

				if (tempIndexOld == -99) {
					tempIndexOld = temp.oldSourcesBox.getSelectedIndex();
				}
				if (tempIndexOld != temp.oldSourcesBox.getSelectedIndex()) {
					difBoxOld = true;
				}

				if (tempIndexNew == -99) {
					tempIndexNew = temp.newSourcesBox.getSelectedIndex();
				}
				if (tempIndexNew != temp.newSourcesBox.getSelectedIndex()) {
					difBoxNew = true;
				}
			}
			if (!dif) {
				multiVisPanel.favorOld.setSelected(selectedt);
			}
			if (difBoxOld) {
				multiVisPanel.setMultipleOld();
			} else {
				multiVisPanel.oldSourcesBox.setSelectedIndex(tempIndexOld);
			}
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

	public JPanel makeVisPanel(EditableModel currentModel) {
		JPanel visPanel = new JPanel();
		JSplitPane splitPane;
//		addTab("Visibility", orangeIcon, visPanel, "Controls the visibility of portions of the model.");

		initVisibilityList();
		visibilityList();

		CardLayout visCardLayout = new CardLayout();
		JPanel visPanelCards = new JPanel(visCardLayout);

		final VisShellBoxCellRenderer visRenderer = new VisShellBoxCellRenderer();
		for (final VisibilityShell vs : allVisShells) {
			final VisibilityPanel vp = new VisibilityPanel(vs, new DefaultComboBoxModel<>(visSourcesOld.toArray()), new DefaultComboBoxModel<>(visSourcesNew.toArray()), visRenderer);

			allVisShellPanes.add(vp);

			visPanelCards.add(vp, vp.title.getText());
		}

		MultiVisibilityPanel multiVisPanel = new MultiVisibilityPanel(new DefaultComboBoxModel<>(visSourcesOld.toArray()), new DefaultComboBoxModel<>(visSourcesNew.toArray()), visRenderer);
		visPanelCards.add(new JPanel(), "blank");
		visPanelCards.add(multiVisPanel, "multiple");
		visTabs.setModel(visComponents);
		visTabs.setCellRenderer(new VisPaneListCellRenderer(currentModel));
		visTabs.addListSelectionListener(e -> visTabsValueChanged(multiVisPanel, visCardLayout, visPanelCards, visTabs));
		visTabs.setSelectedIndex(0);
		visPanelCards.setBorder(BorderFactory.createLineBorder(Color.blue.darker()));

		JButton allInvisButton = new JButton("All Invisible in Exotic Anims");
		allInvisButton.addActionListener(e -> allVisButton(allVisShellPanes, currentModel, VisibilityPanel.NOTVISIBLE));
		allInvisButton.setToolTipText("Forces everything to be always invisibile in animations other than their own original animations.");
		visPanel.add(allInvisButton);

		JButton allVisButton = new JButton("All Visible in Exotic Anims");
		allVisButton.addActionListener(e -> allVisButton(allVisShellPanes, currentModel, VisibilityPanel.VISIBLE));
		allVisButton.setToolTipText("Forces everything to be always visibile in animations other than their own original animations.");
		visPanel.add(allVisButton);

		JButton selSimButton = new JButton("Select Similar Options");
		selSimButton.addActionListener(e -> selSimButton(allVisShellPanes));
		selSimButton.setToolTipText("Similar components will be selected as visibility sources in exotic animations.");
		visPanel.add(selSimButton);

		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JScrollPane(visTabs), visPanelCards);

		setLayout(visPanel, splitPane, allInvisButton, allVisButton, selSimButton);

		return visPanel;
	}

	private void setLayout(JPanel visPanel, JSplitPane splitPane, JButton allInvisButton, JButton allVisButton, JButton selSimButton) {
		final GroupLayout visLayout = new GroupLayout(visPanel);
		visLayout.setHorizontalGroup(visLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(allInvisButton)// .addGap(8)
				.addComponent(allVisButton)
				.addComponent(selSimButton)
				.addComponent(splitPane));
		visLayout.setVerticalGroup(visLayout.createSequentialGroup()
				.addComponent(allInvisButton).addGap(8)
				.addComponent(allVisButton).addGap(8)
				.addComponent(selSimButton).addGap(8)
				.addComponent(splitPane));
		visPanel.setLayout(visLayout);
	}

	public void initVisibilityList() {
		visSourcesOld = new ArrayList<>();
		visSourcesNew = new ArrayList<>();
		allVisShells = new ArrayList<>();
		EditableModel model = currentModel;
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
		for (final Object l : model.sortedIdObjects(Light.class)) {
			final VisibilityShell vs = new VisibilityShell((Named) l, model);
			if (!tempList.contains(l)) {
				tempList.add(l);
				allVisShells.add(vs);
			}
		}
		for (final Object a : model.sortedIdObjects(Attachment.class)) {
			final VisibilityShell vs = new VisibilityShell((Named) a, model);
			if (!tempList.contains(a)) {
				tempList.add(a);
				allVisShells.add(vs);
			}
		}
		for (final Object x : model.sortedIdObjects(ParticleEmitter.class)) {
			final VisibilityShell vs = new VisibilityShell((Named) x, model);
			if (!tempList.contains(x)) {
				tempList.add(x);
				allVisShells.add(vs);
			}
		}
		for (final Object x : model.sortedIdObjects(ParticleEmitter2.class)) {
			final VisibilityShell vs = new VisibilityShell((Named) x, model);
			if (!tempList.contains(x)) {
				tempList.add(x);
				allVisShells.add(vs);
			}
		}
		for (final Object x : model.sortedIdObjects(RibbonEmitter.class)) {
			final VisibilityShell vs = new VisibilityShell((Named) x, model);
			if (!tempList.contains(x)) {
				tempList.add(x);
				allVisShells.add(vs);
			}
		}
		for (final Object x : model.sortedIdObjects(ParticleEmitterPopcorn.class)) {
			final VisibilityShell vs = new VisibilityShell((Named) x, model);
			if (!tempList.contains(x)) {
				tempList.add(x);
				allVisShells.add(vs);
			}
		}
		model = importedModel;
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
		for (final Object l : model.sortedIdObjects(Light.class)) {
			final VisibilityShell vs = new VisibilityShell((Named) l, model);
			if (!tempList.contains(l)) {
				tempList.add(l);
				allVisShells.add(vs);
			}
		}
		for (final Object a : model.sortedIdObjects(Attachment.class)) {
			final VisibilityShell vs = new VisibilityShell((Named) a, model);
			if (!tempList.contains(a)) {
				tempList.add(a);
				allVisShells.add(vs);
			}
		}
		for (final Object x : model.sortedIdObjects(ParticleEmitter.class)) {
			final VisibilityShell vs = new VisibilityShell((Named) x, model);
			if (!tempList.contains(x)) {
				tempList.add(x);
				allVisShells.add(vs);
			}
		}
		for (final Object x : model.sortedIdObjects(ParticleEmitter2.class)) {
			final VisibilityShell vs = new VisibilityShell((Named) x, model);
			if (!tempList.contains(x)) {
				tempList.add(x);
				allVisShells.add(vs);
			}
		}
		for (final Object x : model.sortedIdObjects(RibbonEmitter.class)) {
			final VisibilityShell vs = new VisibilityShell((Named) x, model);
			if (!tempList.contains(x)) {
				tempList.add(x);
				allVisShells.add(vs);
			}
		}
		for (final Object x : model.sortedIdObjects(ParticleEmitterPopcorn.class)) {
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
		for (final Object o : currentModel.getAllVisibilitySources()) {
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
		for (final Object o : importedModel.getAllVisibilitySources()) {
			if (o.getClass() != GeosetAnim.class) {
				visSourcesNew.add(shellFromObject(allVisShells, o));
			} else {
				visSourcesNew.add(shellFromObject(allVisShells, ((GeosetAnim) o).getGeoset()));
			}
		}
		visSourcesNew.add(VisibilityPanel.NOTVISIBLE);
		visSourcesNew.add(VisibilityPanel.VISIBLE);
//		visComponents = new DefaultListModel<>();
	}

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
		for (final Object l : model.sortedIdObjects(Light.class)) {
			final VisibilityPanel vs = visPaneFromObject(allVisShellPanes, l);
			if (!visComponents.contains(vs) && (vs != null)) {
				visComponents.addElement(vs);
			}
		}
		for (final Object a : model.sortedIdObjects(Attachment.class)) {
			final VisibilityPanel vs = visPaneFromObject(allVisShellPanes, a);
			if (!visComponents.contains(vs) && (vs != null)) {
				visComponents.addElement(vs);
			}
		}
		for (final Object x : model.sortedIdObjects(ParticleEmitter.class)) {
			final VisibilityPanel vs = visPaneFromObject(allVisShellPanes, x);
			if (!visComponents.contains(vs) && (vs != null)) {
				visComponents.addElement(vs);
			}
		}
		for (final Object x : model.sortedIdObjects(ParticleEmitter2.class)) {
			final VisibilityPanel vs = visPaneFromObject(allVisShellPanes, x);
			if (!visComponents.contains(vs) && (vs != null)) {
				visComponents.addElement(vs);
			}
		}
		for (final Object x : model.sortedIdObjects(RibbonEmitter.class)) {
			final VisibilityPanel vs = visPaneFromObject(allVisShellPanes, x);
			if (!visComponents.contains(vs) && (vs != null)) {
				visComponents.addElement(vs);
			}
		}
		for (final Object x : model.sortedIdObjects(ParticleEmitterPopcorn.class)) {
			final VisibilityPanel vs = visPaneFromObject(allVisShellPanes, x);
			if (!visComponents.contains(vs) && (vs != null)) {
				visComponents.addElement(vs);
			}
		}

		for (int i = 0; i < objectPanels.size(); i++) {
			final ObjectPanel op = objectPanels.get(i);
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
}

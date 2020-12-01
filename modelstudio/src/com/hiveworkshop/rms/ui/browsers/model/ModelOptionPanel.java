package com.hiveworkshop.rms.ui.browsers.model;

import com.hiveworkshop.rms.editor.model.EditableModel;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelView;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelViewManager;
import com.hiveworkshop.rms.filesystem.GameDataFileSystem;
import com.hiveworkshop.rms.parsers.mdlx.MdlxModel;
import com.hiveworkshop.rms.parsers.mdlx.util.MdxUtils;
import com.hiveworkshop.rms.parsers.slk.DataTable;
import com.hiveworkshop.rms.parsers.slk.Element;
import com.hiveworkshop.rms.ui.application.viewer.AnimationViewer;
import com.hiveworkshop.rms.ui.browsers.jworldedit.WEString;
import com.hiveworkshop.rms.ui.preferences.ProgramPreferences;
import com.hiveworkshop.rms.ui.util.ExceptionPopup;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.*;

public class ModelOptionPanel extends JPanel {

	static class Model {
		String cachedIcon;
		String displayName;
		String filepath;

		@Override
		public String toString() {
			return displayName;
		}
	}

	static class ModelGroup {
		String name;
		List<Model> models = new ArrayList<>();

		public ModelGroup(final String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	static class ModelComparator implements Comparator<Model> {
		@Override
		public int compare(final Model o1, final Model o2) {
			return o1.displayName.compareToIgnoreCase(o2.displayName);
		}

	}

	static class NamedList<E> extends ArrayList<E> {
		String name;
		String cachedIconPath = null; // might be present

		public NamedList(final String name) {
			this.name = name;
		}

		public void setCachedIconPath(final String cachedIconPath) {
			this.cachedIconPath = cachedIconPath;
		}

		public String getCachedIconPath() {
			return cachedIconPath;
		}
	}

	static List<ModelGroup> groups = new ArrayList<>();

	static DataTable unitData = null;
	static DataTable itemData = null;
	static DataTable buffData = null;
	static DataTable destData = null;
	static DataTable doodData = null;
	static DataTable spawnData = null;
	static DataTable ginterData = null;

	static boolean preloaded;

	public static void dropCache() {
		preloaded = false;
	}

	static void preload() {
		if (preloaded) {
			return;
		} else {
			preloaded = true;
			// 11 ModelGroups:
			// - Units
			// - Units - Missiles
			// - Units - Special
			// - Items
			// - Abilities
			// - Buffs
			// - Destructibles
			// - Doodads
			// - Spawned Effects
			// - Game Interface
		}
		groups.clear();
		unitData = DataTable.get();
		itemData = DataTable.getItems();
		buffData = DataTable.getBuffs();
		destData = DataTable.getDestructables();
		doodData = DataTable.getDoodads();
		spawnData = DataTable.getSpawns();
		ginterData = DataTable.getGinters();

		// WESTRING_OE_TYPECAT_UNIT=Units
		// WESTRING_OE_TYPECAT_UNIT_MSSL=Units - Missiles
		// WESTRING_OE_TYPECAT_UNIT_SPEC=Units - Special
		// WESTRING_OE_TYPECAT_ITEM=Items
		// WESTRING_OE_TYPECAT_ABIL=Abilities
		// WESTRING_OE_TYPECAT_BUFF=Buffs
		// WESTRING_OE_TYPECAT_UPGR=Upgrades
		// WESTRING_OE_TYPECAT_DEST=Destructibles
		// WESTRING_OE_TYPECAT_DOOD=Doodads
		// WESTRING_OE_TYPECAT_SPWN=Spawned Effects
		// WESTRING_OE_TYPECAT_SKIN=Game Interface
		// WESTRING_OE_TYPECAT_XTRA=Extra

		// Preload "Units" modelGroup
		final Map<String, NamedList<String>> unitsModelData = new HashMap<>();
		final Map<String, NamedList<String>> unitsMissileData = new HashMap<>();
		final Map<String, NamedList<String>> unitsSpecialData = new HashMap<>();
		final Map<String, NamedList<String>> abilityModelData = new HashMap<>();
		final Map<String, NamedList<String>> buffModelData = new HashMap<>();
		final Map<String, NamedList<String>> itemsModelData = new HashMap<>();
		final Map<String, NamedList<String>> destModelData = new HashMap<>();
		final Map<String, NamedList<String>> doodModelData = new HashMap<>();
		final Map<String, NamedList<String>> spawnModelData = new HashMap<>();
		final Map<String, NamedList<String>> ginterModelData = new HashMap<>();

		// List<Unit> sortedUnitData = new ArrayList<Unit>();
		for (String str : unitData.keySet()) {
			str = str.toUpperCase();
			if (str.startsWith("R")) {
			} else if (str.startsWith("A") || str.startsWith("S")) {
				// ability
				final Element unit = unitData.get(str);
				String filepath = unit.getField("Areaeffectart");
				if (filepath.length() > 0) {
					if (filepath.contains(",")) {
						filepath = filepath.split(",")[0];
					}
					NamedList<String> unitList = abilityModelData.get(filepath.toLowerCase());
					if (unitList == null) {
						unitList = new NamedList<>(filepath);
						unitList.setCachedIconPath(unit.getIconPath());
						abilityModelData.put(filepath.toLowerCase(), unitList);
					}
					unitList.add(unit.getName() + " " + WEString.getString("WESTRING_OE_TYPECAT_SUFFIX_AREAEFFECT"));
				}
				getUnitByField(abilityModelData, unit, "areaeffectart", filepath, "WESTRING_OE_TYPECAT_SUFFIX_AREAEFFECT");
				getUnitByField(abilityModelData, unit, "AreaEffectart", filepath, "WESTRING_OE_TYPECAT_SUFFIX_AREAEFFECT");
				getUnitByField(abilityModelData, unit, "AreaEffectArt", filepath, "WESTRING_OE_TYPECAT_SUFFIX_AREAEFFECT");
				getUnitByField(abilityModelData, unit, "AreaeffectArt", filepath.toLowerCase(), "WESTRING_OE_TYPECAT_SUFFIX_AREAEFFECT");

				getUnitByField(abilityModelData, unit, "CasterArt", filepath, "WESTRING_OE_TYPECAT_SUFFIX_CASTER");
				getUnitByField(abilityModelData, unit, "Casterart", filepath, "WESTRING_OE_TYPECAT_SUFFIX_CASTER");
				getUnitByField(abilityModelData, unit, "casterart", filepath, "WESTRING_OE_TYPECAT_SUFFIX_CASTER");

				getUnitByField(abilityModelData, unit, "EffectArt", filepath, "WESTRING_OE_TYPECAT_SUFFIX_EFFECT");
				getUnitByField(abilityModelData, unit, "Effectart", filepath, "WESTRING_OE_TYPECAT_SUFFIX_EFFECT");
				getUnitByField(abilityModelData, unit, "effectart", filepath, "WESTRING_OE_TYPECAT_SUFFIX_EFFECT");

				getUnitByField(abilityModelData, unit, "Missileart", filepath, "WESTRING_OE_TYPECAT_SUFFIX_MISSILE");
				getUnitByField(abilityModelData, unit, "missileart", filepath, "WESTRING_OE_TYPECAT_SUFFIX_MISSILE");
				getUnitByField(abilityModelData, unit, "MissileArt", filepath, "WESTRING_OE_TYPECAT_SUFFIX_MISSILE");

				getUnitByField(abilityModelData, unit, "SpecialArt", filepath, "WESTRING_OE_TYPECAT_SUFFIX_SPECIAL");
				getUnitByField(abilityModelData, unit, "Specialart", filepath, "WESTRING_OE_TYPECAT_SUFFIX_SPECIAL");
				getUnitByField(abilityModelData, unit, "specialart", filepath, "WESTRING_OE_TYPECAT_SUFFIX_SPECIAL");

				getUnitByField(abilityModelData, unit, "TargetArt", filepath, "WESTRING_OE_TYPECAT_SUFFIX_TARGET");
				getUnitByField(abilityModelData, unit, "Targetart", filepath, "WESTRING_OE_TYPECAT_SUFFIX_TARGET");
				getUnitByField(abilityModelData, unit, "targetart", filepath, "WESTRING_OE_TYPECAT_SUFFIX_TARGET");
			} else if (str.startsWith("B") || str.startsWith("X") || str.startsWith("A")) {
				// BUFF
			} else {
				// UNIT
				final Element unit = unitData.get(str);
				String filepath = unit.getField("file");

				filepath = unit.getField("file");
				if (filepath.length() > 0) {
					NamedList<String> unitList = unitsModelData.get(filepath.toLowerCase());
					if (unitList == null) {
						unitList = new NamedList<>(filepath);
						unitList.setCachedIconPath(unit.getIconPath());
						unitsModelData.put(filepath.toLowerCase(), unitList);
					}
					unitList.add(unit.getName());
				}

				filepath = unit.getField("Missileart");
				if (filepath.length() > 0) {
					if (filepath.contains(",")) {
						final String[] filepaths = filepath.split(",");
						for (final String fp : filepaths) {
							NamedList<String> unitList = unitsMissileData.get(fp.toLowerCase());
							if (unitList == null) {
								unitList = new NamedList<>(filepath);
								unitList.setCachedIconPath(unit.getIconPath());
								unitsMissileData.put(fp.toLowerCase(), unitList);
							}
							unitList.add(unit.getName());
						}
					} else {
						NamedList<String> unitList = unitsMissileData.get(filepath.toLowerCase());
						if (unitList == null) {
							unitList = new NamedList<>(filepath);
							unitList.setCachedIconPath(unit.getIconPath());
							unitsMissileData.put(filepath.toLowerCase(), unitList);
						}
						unitList.add(unit.getName());
					}
				}

				getUnitByField2(unitsSpecialData, unit, "Specialart", "WESTRING_OE_TYPECAT_SUFFIX_SPECIAL");

				getUnitByField2((Map<String, NamedList<String>>) unitsSpecialData, unit, "Targetart", "WESTRING_OE_TYPECAT_SUFFIX_TARGET");
			}
		}

		for (final String str : buffData.keySet()) {

			final Element unit = buffData.get(str);
			String filepath = unit.getField("EffectArt");


			filepath = unit.getField("EffectArt");
			if (filepath.length() > 0) {
				if (filepath.contains(",")) {
					filepath = filepath.split(",")[0];
				}
				NamedList<String> unitList = buffModelData.get(filepath.toLowerCase());
				if (unitList == null) {
					unitList = new NamedList<>(filepath);
					unitList.setCachedIconPath(unit.getIconPath());
					buffModelData.put(filepath.toLowerCase(), unitList);
				}
				unitList.add(unit.getName() + " " + WEString.getString("WESTRING_OE_TYPECAT_SUFFIX_EFFECT"));
			}

			getUnitByField(buffModelData, unit, "Effectart", filepath, "WESTRING_OE_TYPECAT_SUFFIX_EFFECT");
			getUnitByField(buffModelData, unit, "effectart", filepath, "WESTRING_OE_TYPECAT_SUFFIX_EFFECT");

			getUnitByField(buffModelData, unit, "Missileart", filepath, "WESTRING_OE_TYPECAT_SUFFIX_MISSILE");
			getUnitByField(buffModelData, unit, "MissileArt", filepath, "WESTRING_OE_TYPECAT_SUFFIX_MISSILE");
			getUnitByField(buffModelData, unit, "missileart", filepath, "WESTRING_OE_TYPECAT_SUFFIX_MISSILE");

			getUnitByField((Map<String, NamedList<String>>) buffModelData, unit, "SpecialArt", filepath, "WESTRING_OE_TYPECAT_SUFFIX_SPECIAL");

			getUnitByField(buffModelData, unit, "Specialart", filepath, "WESTRING_OE_TYPECAT_SUFFIX_SPECIAL");
			getUnitByField(buffModelData, unit, "specialart", filepath, "WESTRING_OE_TYPECAT_SUFFIX_SPECIAL");

			getUnitByField(buffModelData, unit, "TargetArt", filepath, "WESTRING_OE_TYPECAT_SUFFIX_TARGET");
			getUnitByField(buffModelData, unit, "Targetart", filepath, "WESTRING_OE_TYPECAT_SUFFIX_TARGET");
			getUnitByField(buffModelData, unit, "targetart", filepath, "WESTRING_OE_TYPECAT_SUFFIX_TARGET");
		}

		for (final String str : itemData.keySet()) {
			// ITEMS
			final Element unit = itemData.get(str);
			final String filepath = unit.getField("file");
			if (filepath.length() > 0) {
				NamedList<String> unitList = itemsModelData.get(filepath.toLowerCase());
				if (unitList == null) {
					unitList = new NamedList<>(filepath);
					unitList.setCachedIconPath(unit.getIconPath());
					itemsModelData.put(filepath.toLowerCase(), unitList);
				}
				unitList.add(unit.getName());
			}
		}

		for (final String str : destData.keySet()) {
			// ITEMS
			final Element unit = destData.get(str);
			final String filepath = unit.getField("file");
			if (filepath.length() > 0) {
				NamedList<String> unitList = destModelData.get(filepath.toLowerCase());
				if (unitList == null) {
					unitList = new NamedList<>(filepath);
					unitList.setCachedIconPath(unit.getIconPath());
					destModelData.put(filepath.toLowerCase(), unitList);
				}
				unitList.add(unit.getName() + " <Base>");

				final int numVar = unit.getFieldValue("numVar");
				if (numVar > 1) {
					for (int i = 0; i < numVar; i++) {

						final String filepath2 = filepath + i + ".mdl";
						if (filepath2.length() > 0) {
							NamedList<String> unitList2 = destModelData.get(filepath2.toLowerCase());
							if (unitList2 == null) {
								unitList2 = new NamedList<>(filepath2);
								unitList2.setCachedIconPath(unit.getIconPath());
								destModelData.put(filepath2.toLowerCase(), unitList2);
							}
							unitList2.add(unit.getName() + " <" + WEString.getString("WESTRING_PREVIEWER_VAR") + " "
									+ (i + 1) + ">");
						}
					}
				}
			}
		}

		for (final String str : doodData.keySet()) {
			// ITEMS
			final Element unit = doodData.get(str);
			final String filepath = unit.getField("file");
			if (filepath.length() > 0) {
				NamedList<String> unitList = doodModelData.get(filepath.toLowerCase());
				if (unitList == null) {
					unitList = new NamedList<>(filepath);
					unitList.setCachedIconPath(unit.getIconPath());
					doodModelData.put(filepath.toLowerCase(), unitList);
				}
				unitList.add(unit.getName() + " <Base>");

				final int numVar = unit.getFieldValue("numVar");
				if (numVar > 1) {
					for (int i = 0; i < numVar; i++) {

						final String filepath2 = filepath + i + ".mdl";
						if (filepath2.length() > 0) {
							NamedList<String> unitList2 = doodModelData.get(filepath2.toLowerCase());
							if (unitList2 == null) {
								unitList2 = new NamedList<>(filepath2);
								unitList2.setCachedIconPath(unit.getIconPath());
								doodModelData.put(filepath2.toLowerCase(), unitList2);
							}
							unitList2.add(unit.getName() + " <" + WEString.getString("WESTRING_PREVIEWER_VAR") + " "
									+ (i + 1) + ">");
						}
					}
				}
			}
		}

		for (final String str : spawnData.keySet()) {
			if (!str.equals("init")) {
				// ITEMS
				final Element unit = spawnData.get(str);
				String model = unit.getField("Model");
				if (model.equals("_")) {
					continue;
				}
				final String filepath = model;
				if (filepath.length() > 0) {
					NamedList<String> unitList = spawnModelData.get(filepath.toLowerCase());
					if (unitList == null) {
						unitList = new NamedList<>(filepath);
						unitList.setCachedIconPath(unit.getIconPath());
						spawnModelData.put(filepath.toLowerCase(), unitList);
					}
					if (model.contains("\\")) {
						model = model.substring(model.lastIndexOf("\\") + 1);
					}
					if (model.contains(".")) {
						model = model.substring(0, model.indexOf("."));
					}
					unitList.add(model);
				}
			}
		}

		for (final String str : ginterData.keySet()) {
			final Element race = ginterData.get(str);
			// System.err.println("Gintering unit " + str);
			for (final String fieldName : race.keySet()) {
				final String value = race.getField(fieldName);
				if (value.endsWith(".mdl")) {

					NamedList<String> unitList = ginterModelData.get(value.toLowerCase());
					if (unitList == null) {
						unitList = new NamedList<>(value);
						unitList.setCachedIconPath(race.getIconPath());
						ginterModelData.put(value.toLowerCase(), unitList);
					}
					unitList.add(fieldName + " (" + race.getUnitId() + ")");
				}
			}
		}

		final int lengthCap = 120;
		// Collections.sort(sortedUnitData, new UnitComparator2());
		// for( Unit unit: sortedUnitData ) {
		// }
		createNewModelGroup(unitsModelData, lengthCap, "WESTRING_OE_TYPECAT_UNIT");

		createNewModelGroup(unitsMissileData, lengthCap, "WESTRING_OE_TYPECAT_UNIT_MSSL");

		createNewModelGroup(unitsSpecialData, lengthCap, "WESTRING_OE_TYPECAT_UNIT_SPEC");

		createNewModelGroup((Map<String, NamedList<String>>) itemsModelData, lengthCap, "WESTRING_OE_TYPECAT_ITEM");

		createNewModelGroup(abilityModelData, lengthCap, "WESTRING_OE_TYPECAT_ABIL");

		createNewModelGroup(buffModelData, lengthCap, "WESTRING_OE_TYPECAT_BUFF");

		final ModelGroup destructibles = new ModelGroup(WEString.getString("WESTRING_OE_TYPECAT_DEST"));
		for (final String str : destModelData.keySet()) {
			final NamedList<String> unitList = destModelData.get(str);
			Collections.sort(unitList);
			StringBuilder nameOutput = new StringBuilder();
			for (final String unitName : unitList) {
				if (nameOutput.length() > 0) {
					nameOutput.append(", ");
				}
				if ((nameOutput.length() + unitName.length()) > lengthCap) {
					nameOutput.append("...");
					break;
				} else {
					nameOutput.append(unitName);
				}
			}
			final Model nextModel = new Model();
			nextModel.displayName = nameOutput.toString();
			nextModel.filepath = unitList.name;
			nextModel.cachedIcon = unitList.getCachedIconPath();
			destructibles.models.add(nextModel);
		}
		destructibles.models.sort(new ModelComparator());
		groups.add(destructibles);

		createNewModelGroup(doodModelData, lengthCap, "WESTRING_OE_TYPECAT_DOOD");

		createNewModelGroup(spawnModelData, lengthCap, "WESTRING_OE_TYPECAT_SPWN");

		createNewModelGroup(ginterModelData, lengthCap, "WESTRING_OE_TYPECAT_SKIN");

		final ModelGroup extra = new ModelGroup(WEString.getString("WESTRING_OE_TYPECAT_XTRA"));
		final DataTable worldEditData = new DataTable();
		try {
			worldEditData.readTXT(GameDataFileSystem.getDefault().getResourceAsStream("UI\\WorldEditData.txt"), true);
		} catch (final IOException e) {
			ExceptionPopup.display(e);
		}
		final Element extraModels = worldEditData.get("ExtraModels");
		int emId = 0;
		while (extraModels.getField(String.format("%2d", emId).replace(" ", "0")).length() > 0) {
			final String fieldName = String.format("%2d", emId).replace(" ", "0");
			final Model nextModel = new Model();
			nextModel.displayName = WEString.getString(extraModels.getField(fieldName, 2));
			nextModel.filepath = extraModels.getField(fieldName, 1);
			nextModel.cachedIcon = extraModels.getIconPath();
			extra.models.add(nextModel);

			emId++;
		}
		extra.models.sort(new ModelComparator());
		groups.add(extra);

		for (final Model model : extra.models) {
			System.out.println(model);// + ": \"" + model.filepath + "\"");
		}

		// new JFrame().setVisible(true);
	}

	private static void getUnitByField2(Map<String, NamedList<String>> unitsSpecialData, Element unit, String targetart, String westring_oe_typecat_suffix_target) {
		String filepath;
		filepath = unit.getField(targetart);
		if (filepath.length() > 0) {
			NamedList<String> unitList = unitsSpecialData.get(filepath.toLowerCase());
			if (unitList == null) {
				unitList = new NamedList<>(filepath);
				unitList.setCachedIconPath(unit.getIconPath());
				unitsSpecialData.put(filepath.toLowerCase(), unitList);
			}
			unitList.add(unit.getName() + " " + WEString.getString(westring_oe_typecat_suffix_target));
		}
	}

	private static void getUnitByField(Map<String, NamedList<String>> buffModelData, Element unit, String specialArt, String filepath3, String westring_oe_typecat_suffix_special) {
		String filepath = unit.getField(specialArt);
		if (filepath.length() > 0) {
			if (filepath.contains(",")) {
				filepath = filepath.split(",")[0];
			}
			NamedList<String> unitList = buffModelData.get(filepath3.toLowerCase());
			if (unitList == null) {
				unitList = new NamedList<>(filepath);
				unitList.setCachedIconPath(unit.getIconPath());
				buffModelData.put(filepath3.toLowerCase(), unitList);
			}
			unitList.add(unit.getName() + " " + WEString.getString(westring_oe_typecat_suffix_special));
		}
	}

	private static void createNewModelGroup(Map<String, NamedList<String>> modelDataMap, int lengthCap, String westringType) {
		final ModelGroup modelGroup = new ModelGroup(WEString.getString(westringType));
		for (final String str : modelDataMap.keySet()) {
			final NamedList<String> unitList = modelDataMap.get(str);
			StringBuilder nameOutput = new StringBuilder();
			for (final String unitName : unitList) {
				if (nameOutput.length() > 0) {
					nameOutput.append(", ");
				}
				if ((nameOutput.length() + unitName.length()) > lengthCap) {
					nameOutput.append("...");
					break;
				} else {
					nameOutput.append(unitName);
				}
			}
			final Model nextModel = new Model();
			nextModel.displayName = nameOutput.toString();
			nextModel.filepath = unitList.name;
			nextModel.cachedIcon = unitList.getCachedIconPath();
			modelGroup.models.add(nextModel);
		}
		modelGroup.models.sort(new ModelComparator());
		groups.add(modelGroup);
	}

	JComboBox<ModelGroup> groupBox;
	JComboBox<Model> modelBox;
	JTextField filePathField;
	String cachedIconPath;
	DefaultComboBoxModel<ModelGroup> groupsModel = new DefaultComboBoxModel<>();
	List<DefaultComboBoxModel<Model>> groupModels = new ArrayList<>();

	AnimationViewer viewer;

	final EditableModel blank = new EditableModel();
	final ModelView blankDisp = new ModelViewManager(blank);

	public ModelOptionPanel() {
		preload();

		for (final ModelGroup group : groups) {
			groupsModel.addElement(group);
			final DefaultComboBoxModel<Model> groupModel = new DefaultComboBoxModel<>();

			for (final Model model : group.models) {
				groupModel.addElement(model);
			}
			groupModels.add(groupModel);
		}
		groupBox = new JComboBox<>(groupsModel);
		modelBox = new JComboBox<>(groupModels.get(0));
		filePathField = new JTextField();
		filePathField.setMaximumSize(new Dimension(20000, 25));
		groupBox.addActionListener(e -> {
			modelBox.setModel(groupModels.get(groupBox.getSelectedIndex()));
			modelBox.setSelectedIndex(0);
		});
		modelBox.addActionListener(e -> {
			EditableModel toLoad = blank;
			ModelView modelDisp;
			try {
				String filepath = ((Model) modelBox.getSelectedItem()).filepath;
				filePathField.setText(filepath);
				cachedIconPath = ((Model) modelBox.getSelectedItem()).cachedIcon;
				if (filepath.endsWith(".mdl")) {
					filepath = filepath.replace(".mdl", ".mdx");
				} else if (!filepath.endsWith(".mdx")) {
					filepath = filepath.concat(".mdx");
				}
				final InputStream modelStream = GameDataFileSystem.getDefault().getResourceAsStream(filepath);
				final MdlxModel model = MdxUtils.loadMdlx(modelStream);
				toLoad = new EditableModel(model);
				modelDisp = new ModelViewManager(toLoad);
			} catch (final Exception exc) {
				exc.printStackTrace();
				// bad model!
				modelDisp = blankDisp;
			}

			viewer.setModel(modelDisp);
			viewer.setTitle(toLoad.getName());
		});
		filePathField.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void removeUpdate(final DocumentEvent e) {
				refresh();
			}

			@Override
			public void insertUpdate(final DocumentEvent e) {
				refresh();
			}

			@Override
			public void changedUpdate(final DocumentEvent e) {
				refresh();
			}

			void refresh() {
				EditableModel toLoad = blank;
				ModelView modelDisp;
				try {
					String filepath = filePathField.getText();
					if (filepath.endsWith(".mdl")) {
						filepath = filepath.replace(".mdl", ".mdx");
					} else if (!filepath.endsWith(".mdx")) {
						filepath = filepath.concat(".mdx");
					}
					final InputStream modelStream = GameDataFileSystem.getDefault().getResourceAsStream(filepath);
					final MdlxModel model = MdxUtils.loadMdlx(modelStream);
					toLoad = new EditableModel(model);
					modelDisp = new ModelViewManager(toLoad);
					cachedIconPath = null;
				} catch (final Exception exc) {
					exc.printStackTrace();
					// bad model!
					modelDisp = blankDisp;
				}

				viewer.setModel(modelDisp);
				viewer.setTitle(toLoad.getName());
			}
		});

		groupBox.setMaximumRowCount(11);
		modelBox.setMaximumRowCount(36);

		groupBox.setMaximumSize(new Dimension(140, 25));
		modelBox.setMaximumSize(new Dimension(10000, 25));

		// TODO program prefs not be null???
		// viewer = new PerspDisplayPanel("blank", blankDisp, null);
		viewer = new AnimationViewer(blankDisp, new ProgramPreferences(), false);
		modelBox.setSelectedIndex(0);

		add(groupBox);
		add(modelBox);

		final GroupLayout layout = new GroupLayout(this);

		layout.setHorizontalGroup(layout.createSequentialGroup().addGap(8).addComponent(viewer).addGap(8).addGroup(
				layout.createParallelGroup().addComponent(groupBox).addComponent(modelBox).addComponent(filePathField))
				.addGap(8));
		layout.setVerticalGroup(layout.createSequentialGroup().addGap(8)
				.addGroup(layout.createParallelGroup().addComponent(viewer).addGroup(layout.createSequentialGroup()
						.addComponent(groupBox).addGap(4).addComponent(modelBox).addGap(4).addComponent(filePathField)))
				.addGap(8));

		setLayout(layout);
	}

	public String getSelection() {
		return filePathField.getText();
		// if( modelBox.getSelectedItem() != null ) {
		// return ((Model)modelBox.getSelectedItem()).filepath;
		// } else {
		// return null;
		// }
	}

	public String getCachedIconPath() {
		return cachedIconPath;
	}

	public void setSelection(final String path) {
		if (path != null) {
			ItemFinder: for (final ModelGroup group : groups) {
				for (final Model model : group.models) {
					if (model.filepath.equals(path)) {
						groupBox.setSelectedItem(group);
						modelBox.setSelectedItem(model);
						cachedIconPath = model.cachedIcon;
						break ItemFinder;
					}
				}
			}
			filePathField.setText(path);
		} else {
			filePathField.setText("");
		}
	}

	// public static void main(String[] args) {
	// try {
	// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	// } catch (ClassNotFoundException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (InstantiationException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (IllegalAccessException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// } catch (UnsupportedLookAndFeelException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// preload();
	// JOptionPane.showMessageDialog(null, new ModelOptionPanel(), "Choose
	// Model", JOptionPane.PLAIN_MESSAGE);
	// }
}

package com.hiveworkshop.rms.ui.gui.modeledit;

import com.hiveworkshop.rms.filesystem.sources.DataSourceDescriptor;
import com.hiveworkshop.rms.ui.preferences.DataSourceChooserPanel;
import com.hiveworkshop.rms.ui.preferences.GUITheme;
import com.hiveworkshop.rms.ui.preferences.MouseButtonPreference;
import com.hiveworkshop.rms.ui.preferences.ProgramPreferences;
import com.hiveworkshop.rms.ui.util.ColorChooserIcon;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public final class ProgramPreferencesPanel extends JTabbedPane {
	private final ProgramPreferences programPreferences;
	private final DataSourceChooserPanel dataSourceChooserPanel;

	public ProgramPreferencesPanel(final ProgramPreferences programPreferences,
								   final List<DataSourceDescriptor> dataSources) {
		this.programPreferences = programPreferences;

		final JPanel generalPrefsPanel = new JPanel(new MigLayout());
		final JLabel viewModeLabel = new JLabel("3D View Mode");
		final JRadioButton wireframeViewMode = new JRadioButton("Wireframe");
		final JRadioButton solidViewMode = new JRadioButton("Solid");
		final JCheckBox grid2d = new JCheckBox();
		final JCheckBox useBoxesForNodes = new JCheckBox();
		final JCheckBox quickBrowse = new JCheckBox();
		final JCheckBox allowLoadingNonBlpTextures = new JCheckBox();
		if (programPreferences.show2dGrid()) {
			grid2d.setSelected(true);
		}
		if (programPreferences.getUseBoxesForPivotPoints()) {
			useBoxesForNodes.setSelected(true);
		}
		if (programPreferences.getQuickBrowse()) {
			quickBrowse.setSelected(true);
		}
		if (programPreferences.getAllowLoadingNonBlpTextures()) {
			allowLoadingNonBlpTextures.setSelected(true);
		}
		final ActionListener viewModeUpdater = e -> {
			programPreferences.setViewMode(wireframeViewMode.isSelected() ? 0 : 1);
			programPreferences.setShow2dGrid(grid2d.isSelected());
        };
		wireframeViewMode.setSelected(programPreferences.viewMode() == 0);
		wireframeViewMode.addActionListener(viewModeUpdater);
		solidViewMode.setSelected(programPreferences.viewMode() == 1);
		solidViewMode.addActionListener(viewModeUpdater);
		final ButtonGroup viewModes = new ButtonGroup();
		viewModes.add(wireframeViewMode);
		viewModes.add(solidViewMode);

		generalPrefsPanel.add(viewModeLabel, "cell 0 0");
		generalPrefsPanel.add(wireframeViewMode, "cell 0 1");
		generalPrefsPanel.add(solidViewMode, "cell 0 2");
		generalPrefsPanel.add(new JLabel("Show 2D Viewport Gridlines:"), "cell 0 3");
		generalPrefsPanel.add(grid2d, "cell 1 3");
		generalPrefsPanel.add(new JLabel("Use Boxes for Nodes:"), "cell 0 4");
		generalPrefsPanel.add(useBoxesForNodes, "cell 1 4");
		generalPrefsPanel.add(new JLabel("Quick Browse:"), "cell 0 5");
		quickBrowse.setToolTipText("When opening a new model, close old ones if they have not been modified.");
		generalPrefsPanel.add(quickBrowse, "cell 1 5");
		generalPrefsPanel.add(new JLabel("Allow Loading Non BLP Textures:"), "cell 0 6");
		allowLoadingNonBlpTextures.setToolTipText("Needed for opening PNGs with standard File Open");
		generalPrefsPanel.add(allowLoadingNonBlpTextures, "cell 1 6");
//		generalPrefsPanel.add(new JLabel("Render Particle Emitters:"), "cell 0 7");
		// final BoxLayout boxLayout = new BoxLayout(generalPrefsPanel,
		// BoxLayout.PAGE_AXIS);

		addTab("General", generalPrefsPanel);

		final JPanel modelEditorPanel = new JPanel();
		modelEditorPanel.setLayout(new MigLayout());
		grid2d.addActionListener(viewModeUpdater);
		quickBrowse.addActionListener(e -> programPreferences.setQuickBrowse(quickBrowse.isSelected()));
		allowLoadingNonBlpTextures.addActionListener(e -> programPreferences.setAllowLoadingNonBlpTextures(allowLoadingNonBlpTextures.isSelected()));
		useBoxesForNodes.addActionListener(e -> programPreferences.setUseBoxesForPivotPoints(useBoxesForNodes.isSelected()));
		final ColorChooserIcon backgroundColorIcon = new ColorChooserIcon(programPreferences.getBackgroundColor(),
				programPreferences::setBackgroundColor);
		final ColorChooserIcon perspectiveBackgroundColorIcon = new ColorChooserIcon(
				programPreferences.getPerspectiveBackgroundColor(), programPreferences::setPerspectiveBackgroundColor);
		final ColorChooserIcon vertexColorIcon = new ColorChooserIcon(programPreferences.getVertexColor(),
				programPreferences::setVertexColor);
		final ColorChooserIcon triangleColorIcon = new ColorChooserIcon(programPreferences.getTriangleColor(),
				programPreferences::setTriangleColor);
		final ColorChooserIcon visibleUneditableColorIcon = new ColorChooserIcon(
				programPreferences.getVisibleUneditableColor(), programPreferences::setVisibleUneditableColor);
		final ColorChooserIcon selectColorIcon = new ColorChooserIcon(programPreferences.getSelectColor(),
				programPreferences::setSelectColor);
		final ColorChooserIcon triangleHighlightColorIcon = new ColorChooserIcon(
				programPreferences.getHighlighTriangleColor(), programPreferences::setHighlighTriangleColor);
		final ColorChooserIcon vertexHighlightColorIcon = new ColorChooserIcon(
				programPreferences.getHighlighVertexColor(), programPreferences::setHighlighVertexColor);
		final ColorChooserIcon animtedBoneSelectedColorIcon = new ColorChooserIcon(
				programPreferences.getAnimatedBoneSelectedColor(), programPreferences::setAnimatedBoneSelectedColor);
		final ColorChooserIcon animtedBoneUnselectedColorIcon = new ColorChooserIcon(
				programPreferences.getAnimatedBoneUnselectedColor(), programPreferences::setAnimatedBoneUnselectedColor);
		final ColorChooserIcon animtedBoneSelectedUpstreamColorIcon = new ColorChooserIcon(
				programPreferences.getAnimatedBoneSelectedUpstreamColor(), programPreferences::setAnimatedBoneSelectedUpstreamColor);
		final ColorChooserIcon pivotPointColorIcon = new ColorChooserIcon(programPreferences.getPivotPointsColor(),
				programPreferences::setPivotPointsColor);
		final ColorChooserIcon pivotPointSelectedColorIcon = new ColorChooserIcon(
				programPreferences.getPivotPointsSelectedColor(), programPreferences::setPivotPointsSelectedColor);
		final ColorChooserIcon buttonColorB1Icon = new ColorChooserIcon(programPreferences.getActiveBColor1(),
				programPreferences::setActiveBColor1);
		final ColorChooserIcon buttonColorB2Icon = new ColorChooserIcon(programPreferences.getActiveBColor2(),
				programPreferences::setActiveBColor2);
		final ColorChooserIcon buttonColor1Icon = new ColorChooserIcon(programPreferences.getActiveColor1(),
				programPreferences::setActiveColor1);
		final ColorChooserIcon buttonColor2Icon = new ColorChooserIcon(programPreferences.getActiveColor2(),
				programPreferences::setActiveColor2);
		final ColorChooserIcon buttonColorR1Icon = new ColorChooserIcon(programPreferences.getActiveRColor1(),
				programPreferences::setActiveRColor1);
		final ColorChooserIcon buttonColorR2Icon = new ColorChooserIcon(programPreferences.getActiveRColor2(),
				programPreferences::setActiveRColor2);

		final JComboBox<GUITheme> themeCheckBox = new JComboBox<>(GUITheme.values());
		themeCheckBox.setSelectedItem(programPreferences.getTheme());
		themeCheckBox.addActionListener(new ActionListener() {
			boolean hasWarned = false;

			@Override
			public void actionPerformed(final ActionEvent e) {
				programPreferences.setTheme((GUITheme) themeCheckBox.getSelectedItem());
				if (!hasWarned) {
					hasWarned = true;
					JOptionPane.showMessageDialog(ProgramPreferencesPanel.this,
							"Some settings may not take effect until you restart the application.", "Warning",
							JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		int row = 0;
		row = getRowAddUgg(modelEditorPanel, backgroundColorIcon, row, "Background Color:");
		row = getRowAddUgg(modelEditorPanel, vertexColorIcon, row, "Vertex Color:");
		row = getRowAddUgg(modelEditorPanel, triangleColorIcon, row, "Triangle Color:");
		row = getRowAddUgg(modelEditorPanel, selectColorIcon, row, "Select Color:");
		row = getRowAddUgg(modelEditorPanel, triangleHighlightColorIcon, row, "Triangle Highlight Color:");
		row = getRowAddUgg(modelEditorPanel, vertexHighlightColorIcon, row, "Vertex Highlight Color:");
		row = getRowAddUgg(modelEditorPanel, perspectiveBackgroundColorIcon, row, "Perspective Background Color:");
		row = getRowAddUgg(modelEditorPanel, visibleUneditableColorIcon, row, "Visible Uneditable Mesh Color:");
		row = getRowAddUgg(modelEditorPanel, animtedBoneUnselectedColorIcon, row, "Animation Editor Bone Color:");
		row = getRowAddUgg(modelEditorPanel, animtedBoneSelectedColorIcon, row, "Animation Editor Selected Bone Color:");
		row = getRowAddUgg(modelEditorPanel, animtedBoneSelectedUpstreamColorIcon, row, "Animation Editor Selected Upstream Color:");
		row = getRowAddUgg(modelEditorPanel, pivotPointColorIcon, row, "Pivot Point Color:");
		row = getRowAddUgg(modelEditorPanel, pivotPointSelectedColorIcon, row, "Pivot Point Selected Color:");
		row = getRowAddUgg(modelEditorPanel, buttonColorB1Icon, row, "Button B Color 1:");
		row = getRowAddUgg(modelEditorPanel, buttonColorB2Icon, row, "Button B Color 2:");
		row = getRowAddUgg(modelEditorPanel, buttonColor1Icon, row, "Button Color 1:");
		row = getRowAddUgg(modelEditorPanel, buttonColor2Icon, row, "Button Color 2:");
		row = getRowAddUgg(modelEditorPanel, buttonColorR1Icon, row, "Button R Color 1:");
		row = getRowAddUgg(modelEditorPanel, buttonColorR2Icon, row, "Button R Color 2:");
		modelEditorPanel.add(new JLabel("Window Borders (Theme):"), "cell 0 " + row);
		modelEditorPanel.add(themeCheckBox, "cell 1 " + row);

		addTab("Colors/Theme", new JScrollPane(modelEditorPanel));

		final JPanel hotkeysPanel = new JPanel();
		hotkeysPanel.setLayout(new MigLayout());
		row = 0;
		final JComboBox<MouseButtonPreference> cameraSpinBox = new JComboBox<>(MouseButtonPreference.values());
		cameraSpinBox.setSelectedItem(programPreferences.getThreeDCameraSpinButton());
		final JComboBox<MouseButtonPreference> cameraPanBox = new JComboBox<>(MouseButtonPreference.values());
		cameraPanBox.setSelectedItem(programPreferences.getThreeDCameraPanButton());
		cameraSpinBox.addActionListener(e -> programPreferences.setThreeDCameraSpinButton((MouseButtonPreference) cameraSpinBox.getSelectedItem()));
		cameraPanBox.addActionListener(e -> programPreferences.setThreeDCameraPanButton((MouseButtonPreference) cameraPanBox.getSelectedItem()));
		hotkeysPanel.add(new JLabel("3D Camera Spin"), "cell 0 " + row);
		hotkeysPanel.add(cameraSpinBox, "cell 1 " + row);
		row++;
		hotkeysPanel.add(new JLabel("3D Camera Pan"), "cell 0 " + row);
		hotkeysPanel.add(cameraPanBox, "cell 1 " + row);
		row++;
		addTab("Hotkeys", hotkeysPanel);

		dataSourceChooserPanel = new DataSourceChooserPanel(dataSources);
		addTab("Warcraft Data", dataSourceChooserPanel);
	}

	public int getRowAddUgg(JPanel modelEditorPanel, ColorChooserIcon backgroundColorIcon, int row, String s) {
		modelEditorPanel.add(new JLabel(s), "cell 0 " + row);
		modelEditorPanel.add(backgroundColorIcon, "cell 1 " + row);
		row++;
		return row;
	}

	public List<DataSourceDescriptor> getDataSources() {
		return dataSourceChooserPanel.getDataSourceDescriptors();
	}
}

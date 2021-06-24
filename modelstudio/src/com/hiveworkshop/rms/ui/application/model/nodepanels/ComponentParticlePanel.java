package com.hiveworkshop.rms.ui.application.model.nodepanels;

import com.hiveworkshop.rms.editor.model.ParticleEmitter;
import com.hiveworkshop.rms.editor.model.animflag.FloatAnimFlag;
import com.hiveworkshop.rms.parsers.mdlx.mdl.MdlUtils;
import com.hiveworkshop.rms.ui.application.edit.ModelStructureChangeListener;
import com.hiveworkshop.rms.ui.application.model.editors.ComponentEditorTextField;
import com.hiveworkshop.rms.ui.application.model.editors.FloatValuePanel;
import com.hiveworkshop.rms.ui.gui.modeledit.ModelHandler;

public class ComponentParticlePanel extends ComponentIdObjectPanel<ParticleEmitter> {
	private final ComponentEditorTextField pathField;

	private final FloatValuePanel longitudePanel;
	private final FloatValuePanel latitudePanel;
	private final FloatValuePanel speedPanel;
	private final FloatValuePanel gravityPanel;
	private final FloatValuePanel emissionPanel;
	private final FloatValuePanel visibilityPanel;

	public ComponentParticlePanel(ModelHandler modelHandler, ModelStructureChangeListener changeListener) {
		super(modelHandler, changeListener);

		pathField = new ComponentEditorTextField(24);
		pathField.addEditingStoppedListener(this::texturePathField);
		add(pathField, "wrap");

		longitudePanel = new FloatValuePanel(modelHandler, "Longitude", modelHandler.getUndoManager(), changeListener);
		latitudePanel = new FloatValuePanel(modelHandler, "Latitude", modelHandler.getUndoManager(), changeListener);
		speedPanel = new FloatValuePanel(modelHandler, "Speed", modelHandler.getUndoManager(), changeListener);
		gravityPanel = new FloatValuePanel(modelHandler, "Gravity", modelHandler.getUndoManager(), changeListener);
		emissionPanel = new FloatValuePanel(modelHandler, "EmissionRate", modelHandler.getUndoManager(), changeListener);
		visibilityPanel = new FloatValuePanel(modelHandler, "Visibility", modelHandler.getUndoManager(), changeListener);
		add(longitudePanel, "spanx, growx, wrap");
		add(latitudePanel, "spanx, growx, wrap");
		add(speedPanel, "spanx, growx, wrap");
		add(gravityPanel, "spanx, growx, wrap");
		add(emissionPanel, "spanx, growx, wrap");
		add(visibilityPanel, "spanx, growx, wrap");
	}

	@Override
	public void updatePanels() {
		pathField.reloadNewValue(idObject.getPath());
		longitudePanel.reloadNewValue((float) idObject.getLongitude(), (FloatAnimFlag) idObject.find(MdlUtils.TOKEN_LONGITUDE), idObject, MdlUtils.TOKEN_LONGITUDE, idObject::setLongitude);
		latitudePanel.reloadNewValue((float) idObject.getLatitude(), (FloatAnimFlag) idObject.find(MdlUtils.TOKEN_LATITUDE), idObject, MdlUtils.TOKEN_LATITUDE, idObject::setLatitude);
		speedPanel.reloadNewValue((float) idObject.getInitVelocity(), (FloatAnimFlag) idObject.find(MdlUtils.TOKEN_SPEED), idObject, MdlUtils.TOKEN_SPEED, idObject::setInitVelocity);
		gravityPanel.reloadNewValue((float) idObject.getGravity(), (FloatAnimFlag) idObject.find(MdlUtils.TOKEN_GRAVITY), idObject, MdlUtils.TOKEN_GRAVITY, idObject::setGravity);
		emissionPanel.reloadNewValue((float) idObject.getEmissionRate(), (FloatAnimFlag) idObject.find(MdlUtils.TOKEN_EMISSION_RATE), idObject, MdlUtils.TOKEN_EMISSION_RATE, idObject::setEmissionRate);
		visibilityPanel.reloadNewValue(1f, (FloatAnimFlag) idObject.find(MdlUtils.TOKEN_EMISSION_RATE), idObject, MdlUtils.TOKEN_EMISSION_RATE, null);
	}

	private void texturePathField() {
		idObject.setPath(pathField.getText());
	}
}
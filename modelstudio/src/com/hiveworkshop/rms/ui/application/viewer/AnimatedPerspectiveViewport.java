package com.hiveworkshop.rms.ui.application.viewer;

import com.hiveworkshop.rms.editor.render3d.RenderModel;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelView;
import com.hiveworkshop.rms.ui.preferences.ProgramPreferences;
import org.lwjgl.LWJGLException;

//public class AnimatedPerspectiveViewport extends BetterAWTGLCanvas implements MouseListener, ActionListener, MouseWheelListener, AnimatedRenderEnvironment, RenderResourceAllocator {
public class AnimatedPerspectiveViewport extends UggViewport {

	public AnimatedPerspectiveViewport(final ModelView modelView, RenderModel renderModel, final ProgramPreferences programPreferences, UggRenderEnv renderEnvironment, final boolean loadDefaultCamera) throws LWJGLException {
		super(modelView, renderModel, programPreferences, renderEnvironment, loadDefaultCamera);
		this.renderModel.refreshFromEditor(timeEnvironment, inverseCameraRotationQuat, inverseCameraRotationYSpin, inverseCameraRotationZSpin, this);
	}
}
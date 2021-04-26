package com.hiveworkshop.rms.ui.application.viewer.perspective;

import com.hiveworkshop.rms.editor.render3d.RenderModel;
import com.hiveworkshop.rms.editor.wrapper.v2.ModelView;
import com.hiveworkshop.rms.ui.application.viewer.ComPerspRenderEnv;
import com.hiveworkshop.rms.ui.application.viewer.ComPerspViewport;
import com.hiveworkshop.rms.ui.preferences.ProgramPreferences;

public class PerspectiveViewport extends ComPerspViewport {

	public PerspectiveViewport(ModelView modelView, RenderModel renderModel, ProgramPreferences programPreferences, ComPerspRenderEnv renderEnvironment) {
		super(modelView, renderModel, programPreferences, renderEnvironment, false);
	}
}
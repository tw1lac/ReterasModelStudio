package com.hiveworkshop.rms.ui.application.tools.shadereditors;

import com.hiveworkshop.rms.ui.application.viewer.ObjectRenderers.ShaderManager;
import com.hiveworkshop.rms.ui.application.viewer.twiTestRenderMaster.BufferFiller;
import com.hiveworkshop.rms.ui.gui.modeledit.ModelHandler;
import com.hiveworkshop.rms.util.FramePopup;
import com.hiveworkshop.rms.util.ScreenInfo;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class MeshShaderEditPanel extends ShaderEditPanel {

	boolean isHD;

	public MeshShaderEditPanel(ShaderManager shaderManager, boolean isHD) {
		super(shaderManager, getShaderStrings(isHD));
		this.isHD = isHD;
	}
	public MeshShaderEditPanel(List<ShaderManager> shaderManagers, boolean isHD) {
		super(shaderManagers, getShaderStrings(isHD));
		this.isHD = isHD;
	}
	public MeshShaderEditPanel(ModelHandler modelHandler) {
		super(modelHandler.getPreviewRenderModel().getBufferFiller().getShaderManager(), getShaderStrings(modelHandler.getPreviewRenderModel().getBufferFiller().isHD()));
		this.isHD = modelHandler.getPreviewRenderModel().getBufferFiller().isHD();
	}

	protected void createCustomShader(){
		for(ShaderManager shaderManager : shaderManagers){
			shaderManager.createCustomShader(shaderTrackers[0].getCurrShader(), shaderTrackers[1].getCurrShader(), isHD);
		}
	}
	protected void removeCustomShader(){
		for(ShaderManager shaderManager : shaderManagers){
			shaderManager.removeCustomShader();
		}
	}
//	protected void createCustomShader(){
//		shaderManager.createCustomShader(shaderTrackers[0].getCurrShader(), shaderTrackers[1].getCurrShader(), isHD);
//	}
//	protected void removeCustomShader(){
//		shaderManager.removeCustomShader();
//	}

	private static String[] getShaderStrings(boolean isHD){
		if(isHD){
			return new String[]{"HDDiffuseVertColor.vert", "HDDiffuseVertColor.frag"};
		} else {
			return new String[]{"simpleDiffuse.vert", "simpleDiffuse.frag"};
		}
	}

	public static void show(JComponent parent, BufferFiller bufferFiller) {
		MeshShaderEditPanel shaderEditPanel = new MeshShaderEditPanel(bufferFiller.getShaderManager(), bufferFiller.isHD());
//		shaderEditPanel.setSize(1600, 900);
		shaderEditPanel.setPreferredSize(ScreenInfo.getSmallWindow());
		FramePopup.show(shaderEditPanel, parent, "Shader Editor");

	}
	public static void show(JComponent parent, ModelHandler modelHandler) {
//		BufferFiller bufferFiller = modelHandler.getRenderModel().getBufferFiller();
		BufferFiller bufferFiller = modelHandler.getPreviewRenderModel().getBufferFiller();
		List<ShaderManager> managers = new ArrayList<>();
		managers.add(bufferFiller.getShaderManager());
		managers.add(modelHandler.getRenderModel().getBufferFiller().getShaderManager());
//		MeshShaderEditPanel shaderEditPanel = new MeshShaderEditPanel(bufferFiller.getShaderManager(), bufferFiller.isHD());
		MeshShaderEditPanel shaderEditPanel = new MeshShaderEditPanel(managers, bufferFiller.isHD());
//		shaderEditPanel.setSize(1600, 900);
		shaderEditPanel.setPreferredSize(ScreenInfo.getSmallWindow());
		FramePopup.show(shaderEditPanel, parent, "Shader Editor");

	}

}

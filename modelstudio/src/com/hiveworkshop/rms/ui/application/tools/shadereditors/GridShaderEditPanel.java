package com.hiveworkshop.rms.ui.application.tools.shadereditors;

import com.hiveworkshop.rms.ui.application.viewer.ObjectRenderers.ShaderManager;
import com.hiveworkshop.rms.ui.application.viewer.twiTestRenderMaster.BufferFiller;
import com.hiveworkshop.rms.ui.gui.modeledit.ModelHandler;
import com.hiveworkshop.rms.util.FramePopup;
import com.hiveworkshop.rms.util.ScreenInfo;
import net.miginfocom.swing.MigLayout;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class GridShaderEditPanel extends ShaderEditPanel {

	public GridShaderEditPanel(ShaderManager shaderManager) {
//		super(shaderManager, "Grid.vert", "Grid.glsl", "Grid.frag");
		super(shaderManager, "Grid.vert", "Grid.frag");
	}
	public GridShaderEditPanel(List<ShaderManager> shaderManagers) {
//		super(shaderManager, "Grid.vert", "Grid.glsl", "Grid.frag");
		super(shaderManagers, "Grid.vert", "Grid.frag");
	}
	public GridShaderEditPanel(ModelHandler modelHandler) {
		super(modelHandler.getRenderModel().getBufferFiller().getShaderManager(), "Grid.vert", "Grid.frag");
//		super(shaderManager, "Grid.vert", "Grid.glsl", "Grid.frag");
	}

	protected void createCustomShader(){
		for(ShaderManager shaderManager : shaderManagers){
//		    shaderManager.createCustomGridShader(shaderTrackers[0].getCurrShader(), shaderTrackers[2].getCurrShader(), shaderTrackers[1].getCurrShader());
			shaderManager.createCustomGridShader(shaderTrackers[0].getCurrShader(), shaderTrackers[1].getCurrShader(), null);
		}

	}
	protected void removeCustomShader(){
		for(ShaderManager shaderManager : shaderManagers){
			shaderManager.removeCustomGridShader();
		}
	}
//	protected void createCustomShader(){
////		shaderManager.createCustomGridShader(shaderTrackers[0].getCurrShader(), shaderTrackers[2].getCurrShader(), shaderTrackers[1].getCurrShader());
//		shaderManager.createCustomGridShader(shaderTrackers[0].getCurrShader(), shaderTrackers[1].getCurrShader(), null);
//
//	}
//	protected void removeCustomShader(){
//		shaderManager.removeCustomGridShader();
//	}

	public JPanel getRSEditorPanel(RSyntaxTextArea editorPane, JPanel buttonPanel){
		RTextScrollPane scrollPane = new RTextScrollPane(editorPane);
		JPanel mainPanel = new JPanel(new MigLayout("fill, gap 0, ins 0", "", "[grow][]"));
		mainPanel.add(scrollPane, "spanx, growx, growy, wrap");


		mainPanel.add(buttonPanel, "");
		return mainPanel;
	}

	public static void show(JComponent parent, BufferFiller bufferFiller) {
		GridShaderEditPanel shaderEditPanel = new GridShaderEditPanel(bufferFiller.getShaderManager());
//		shaderEditPanel.setSize(1600, 900);
		shaderEditPanel.setPreferredSize(ScreenInfo.getSmallWindow());
		FramePopup.show(shaderEditPanel, parent, "Grid Shader Editor");

	}
	public static void show(JComponent parent, ModelHandler modelHandler) {
		BufferFiller bufferFiller = modelHandler.getRenderModel().getBufferFiller();

		List<ShaderManager> managers = new ArrayList<>();
		managers.add(bufferFiller.getShaderManager());
		managers.add(modelHandler.getRenderModel().getBufferFiller().getShaderManager());

		GridShaderEditPanel shaderEditPanel = new GridShaderEditPanel(managers);
//		shaderEditPanel.setSize(1600, 900);
		shaderEditPanel.setPreferredSize(ScreenInfo.getSmallWindow());
		FramePopup.show(shaderEditPanel, parent, "Grid Shader Editor");

	}

}

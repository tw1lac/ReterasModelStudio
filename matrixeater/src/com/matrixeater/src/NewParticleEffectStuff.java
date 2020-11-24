package com.matrixeater.src;

import com.hiveworkshop.wc3.mdl.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class NewParticleEffectStuff {
    static JMenu addParticle;
    AddParticlePanel particlePanel;
    public NewParticleEffectStuff(JMenu menu, MainPanel mainPanel){
        addParticle = menu;
        fetchIncludedParticles(mainPanel);
        particlePanel = new AddParticlePanel();
    }


    void fetchIncludedParticles(final MainPanel mainPanel) {
        final File stockFolder = new File("matrixeater/stock/particles");
        final File[] stockFiles = stockFolder.listFiles((dir, name) -> name.endsWith(".mdx"));
        for (final File file : stockFiles) {
            parseStockFiles(mainPanel, file);
        }
    }

    private void parseStockFiles(MainPanel mainPanel, File file) {
        final String basicName = file.getName().split("\\.")[0];
        final File pngImage = new File(file.getParent() + File.separatorChar + basicName + ".png");
        if (pngImage.exists()) {
            try {
                makeParticleButtons(mainPanel, file, basicName, pngImage);
            } catch (final IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private void makeParticleButtons(MainPanel mainPanel, File file, String basicName, File pngImage) throws IOException {
        final Image image = ImageIO.read(pngImage);
        final JMenuItem particleItem = new JMenuItem(basicName,
                new ImageIcon(image.getScaledInstance(28, 28, Image.SCALE_DEFAULT)));
        particleItem.addActionListener(e -> particlePanel.addParticleEmitterButtonResponse(mainPanel, file, basicName, image));
        addParticle.add(particleItem);
    }

    static void addChosenParticles(ParticleEmitter2 particle, Bone nullBone, JComboBox<IdObject> parent, JTextField nameField, double[] coordinates, AnimFlag visFlag, MainPanel mainPanel, Color[] colors) {
        particle.setPivotPoint(new Vertex(coordinates[0],coordinates[1],coordinates[2]));

        for (int i = 0; i < colors.length; i++) {
            particle.setSegmentColor(i, new Vertex(colors[i].getBlue() / 255.00, colors[i].getGreen() / 255.00, colors[i].getRed() / 255.00));
        }
        final IdObject parentChoice = parent.getItemAt(parent.getSelectedIndex());
        if (parentChoice == nullBone) {
            particle.setParent(null);
        } else {
            particle.setParent(parentChoice);
        }


        particle.setVisibilityFlag(visFlag);
        particle.setName(nameField.getText());
        mainPanel.currentMDL().add(particle);
        mainPanel.modelStructureChangeListener.nodesAdded(Collections.<IdObject>singletonList(particle));
    }

}

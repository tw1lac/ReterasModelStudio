package com.matrixeater.src;

import com.badlogic.gdx.backends.lwjgl.LwjglNativesLoader;
import com.hiveworkshop.wc3.gui.BLPHandler;
import com.hiveworkshop.wc3.gui.ExceptionPopup;
import com.hiveworkshop.wc3.gui.ProgramPreferences;
import com.hiveworkshop.wc3.gui.datachooser.DataSourceChooserPanel;
import com.hiveworkshop.wc3.gui.datachooser.DataSourceDescriptor;
import com.hiveworkshop.wc3.mdl.EditableModel;
import com.hiveworkshop.wc3.mpq.MpqCodebase;
import com.hiveworkshop.wc3.resources.Resources;
import com.hiveworkshop.wc3.resources.WEString;
import com.hiveworkshop.wc3.units.DataTable;
import com.hiveworkshop.wc3.units.ModelOptionPanel;
import com.hiveworkshop.wc3.units.UnitOptionPanel;
import com.hiveworkshop.wc3.user.SaveProfile;
import com.owens.oobjloader.builder.Build;
import com.owens.oobjloader.parser.Parse;
import net.infonode.gui.laf.InfoNodeLookAndFeel;
import net.infonode.gui.laf.InfoNodeLookAndFeelTheme;
import net.infonode.gui.laf.InfoNodeLookAndFeelThemes;

import javax.swing.*;
import javax.swing.plaf.ColorUIResource;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Write a description of class MainFrame here.
 *
 * @author (your name)
 * @version (a version number or a date)
 */
public class MainFrame extends JFrame {
    public static final Image MAIN_PROGRAM_ICON = new ImageIcon(MainFrame.class.getResource("ImageBin/retera.jpg"))
            .getImage();
    static MainFrame frame;
    static MainPanel panel;
    static JMenuBar menuBar;

    public static MainPanel getPanel() {
        return panel;
    }

    public static void main(final String[] args) {
        final boolean hasArgs = args.length >= 1;
        final List<String> startupModelPaths = new ArrayList<>();
        if (hasArgs) {
            if ((args.length > 1) && args[0].equals("-convert")) {
                final String path = args[1];
                final EditableModel model = EditableModel.read(new File(path));
                if (path.toLowerCase().endsWith(".mdx")) {
                    model.printTo(new File(path.substring(0, path.lastIndexOf('.')) + ".mdl"));
                } else if (path.toLowerCase().endsWith(".mdl")) {
                    model.printTo(new File(path.substring(0, path.lastIndexOf('.')) + ".mdx"));
                } else {
                    // Unfortunately obj convert does popups right now
                    final Build builder = new Build();
                    try {
                        final Parse obj = new Parse(builder, path);
                        final EditableModel mdl = builder.createMDL();
                    } catch (final IOException e) {
                        ExceptionPopup.display(e);
                        e.printStackTrace();
                    }
                }
            } else {
                if (args[0].endsWith(".mdx") || args[0].endsWith(".mdl") || args[0].endsWith(".blp")
                        || args[0].endsWith(".dds") || args[0].endsWith(".obj")) {
                    startupModelPaths.addAll(Arrays.asList(args));
                }
            }
        }
        final boolean dataPromptForced = hasArgs && args[0].equals("-forcedataprompt");
        try {
            LwjglNativesLoader.load();
            final ProgramPreferences preferences = SaveProfile.get().getPreferences();


            setTheme(preferences);

            SwingUtilities.invokeLater(() -> {
                try {
                    final List<DataSourceDescriptor> dataSources = SaveProfile.get().getDataSources();
                    if ((dataSources == null) || dataPromptForced) {
                        final DataSourceChooserPanel dataSourceChooserPanel = new DataSourceChooserPanel(
                                dataSources);
//							JF
                        final JFrame jFrame = new JFrame("Retera Model Studio: Setup");
//							jFrame.setContentPane(dataSourceChooserPanel);
                        jFrame.setUndecorated(true);
                        jFrame.pack();
                        jFrame.setSize(0, 0);
                        jFrame.setLocationRelativeTo(null);
                        jFrame.setIconImage(MAIN_PROGRAM_ICON);
                        jFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                        jFrame.setVisible(true);
                        try {
                            if (JOptionPane.showConfirmDialog(jFrame, dataSourceChooserPanel,
                                    "Retera Model Studio: Setup", JOptionPane.OK_CANCEL_OPTION,
                                    JOptionPane.PLAIN_MESSAGE) != JOptionPane.OK_OPTION) {
                                return;
                            }
                        } finally {
                            jFrame.setVisible(false);
                        }
                        SaveProfile.get().setDataSources(dataSourceChooserPanel.getDataSourceDescriptors());
                        SaveProfile.save();
                        MpqCodebase.get().refresh(SaveProfile.get().getDataSources());
                        // cache priority order...
                        UnitOptionPanel.dropRaceCache();
                        DataTable.dropCache();
                        ModelOptionPanel.dropCache();
                        WEString.dropCache();
                        Resources.dropCache();
                        BLPHandler.get().dropCache();
                    }

                    JPopupMenu.setDefaultLightWeightPopupEnabled(false);
                    frame = new MainFrame("Retera Model Studio v0.04.2020.08.09 Nightly Build");
                    panel.init();
                    if (!startupModelPaths.isEmpty()) {
                        for (final String path : startupModelPaths) {
                            FileUtils.openFile(panel, new File(path));
                        }
                    }
                } catch (final Throwable th) {
                    th.printStackTrace();
                    ExceptionPopup.display(th);
                    if (!dataPromptForced) {
                        new Thread(() -> main(new String[]{"-forcedataprompt"})).start();
                    } else {
                        JOptionPane.showMessageDialog(null,
                                "Retera Model Studio startup sequence has failed for two attempts. The program will now exit.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                        System.exit(-1);
                    }
                }
            });
        } catch (final Throwable th) {
            th.printStackTrace();
            SwingUtilities.invokeLater(() -> ExceptionPopup.display(th));
            if (!dataPromptForced) {
                main(new String[]{"-forcedataprompt"});
            } else {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null,
                            "Retera Model Studio startup sequence has failed for two attempts. The program will now exit.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    System.exit(-1);
                });
            }
        }
    }

    private static void setTheme(ProgramPreferences preferences) {
        switch (preferences.getTheme()) {
            case DARK:
                EditorDisplayManager.setupLookAndFeel();
                break;
            case HIFI:
                EditorDisplayManager.setupLookAndFeel("HiFi");
                break;
            case ACRYL:
                EditorDisplayManager.setupLookAndFeel("Acryl");
                break;
            case ALUMINIUM:
                EditorDisplayManager.setupLookAndFeel("Aluminium");
                break;
            case FOREST_GREEN:
                try {
                    final InfoNodeLookAndFeelTheme theme = new InfoNodeLookAndFeelTheme("Retera Studio",
                            new Color(44, 46, 20), new Color(116, 126, 36), new Color(44, 46, 20),
                            new Color(220, 202, 132), new Color(116, 126, 36), new Color(220, 202, 132));
                    theme.setShadingFactor(-0.8);
                    theme.setDesktopColor(new Color(60, 82, 44));

                    UIManager.setLookAndFeel(new InfoNodeLookAndFeel(theme));
                } catch (final UnsupportedLookAndFeelException e) {
                    e.printStackTrace();
                }
                break;
            case WINDOWS:
                try {
                    UIManager.put("desktop", new ColorUIResource(Color.WHITE));
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    System.out.println(UIManager.getLookAndFeel());
                } catch (final UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    // handle exception
                }
                break;
            case JAVA_DEFAULT:
//				UIManager.getLookAndFeel().initialize();
//				UIManager.getLookAndFeel().getDefaults().put("TabbedPane.background", Color.GREEN);
//				UIManager.getLookAndFeel().getDefaults().put("InternalFrame.activeTitleBackground", Color.GREEN);
//				UIManager.getLookAndFeel().getDefaults().put("InternalFrame.activeTitleForeground", Color.GREEN);
//				UIManager.getLookAndFeel().getDefaults().put("InternalFrame.inactiveTitleBackground", Color.GREEN);
//				UIManager.getLookAndFeel().getDefaults().put("InternalFrame.inactiveTitleForeground", Color.GREEN);
//				UIManager.getLookAndFeel().getDefaults().put("Button.select", Color.GREEN);
//				UIManager.getLookAndFeel().getDefaults().put("Button.disabledText", Color.GREEN);
//				UIManager.getLookAndFeel().getDefaults().put("ScrollBar.background", Color.GREEN);
//				UIManager.getLookAndFeel().getDefaults().put("ScrollBar.shadow", Color.GREEN);
                break;
            case SOFT_GRAY:
                trySetTheme(InfoNodeLookAndFeelThemes.getSoftGrayTheme());

                break;
            case BLUE_ICE:
                trySetTheme(InfoNodeLookAndFeelThemes.getBlueIceTheme());

                break;
            case DARK_BLUE_GREEN:
                trySetTheme(InfoNodeLookAndFeelThemes
                        .getDarkBlueGreenTheme());

                break;
            case GRAY:
                trySetTheme(InfoNodeLookAndFeelThemes.getGrayTheme());

                break;
            case WINDOWS_CLASSIC:
                try {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
                } catch (final Exception exc) {
                    try {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    } catch (final ClassNotFoundException | UnsupportedLookAndFeelException | InstantiationException | IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private static void trySetTheme(InfoNodeLookAndFeelTheme theme) {
        try {
            UIManager.setLookAndFeel(new InfoNodeLookAndFeel(theme));
        } catch (final Exception exc) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (final ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
                e.printStackTrace();
            }
        }
    }

    public MainFrame(final String title) {
        super(title);
        // setDefaultCloseOperation(EXIT_ON_CLOSE);

        setBounds(0, 0, 1000, 650);
        panel = new MainPanel();
        setContentPane(panel);
        menuBar = MenuBar.createMenuBar(panel);
        setJMenuBar(menuBar);// MainFrame.class.getResource("ImageBin/DDChicken2.png")
        setIconImage(MAIN_PROGRAM_ICON);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent e) {
                if (panel.closeAll()) {
                    System.exit(0);
                }
            }
        });
        setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
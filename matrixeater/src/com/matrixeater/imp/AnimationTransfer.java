package com.matrixeater.imp;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.hiveworkshop.wc3.gui.ExceptionPopup;
import com.hiveworkshop.wc3.gui.modeledit.ImportPanel;
import com.hiveworkshop.wc3.mdl.Animation;
import com.hiveworkshop.wc3.mdl.EditableModel;
import com.hiveworkshop.wc3.user.SaveProfile;
import com.matrixeater.src.MainFrame;
import com.matrixeater.src.MainPanel;

public class AnimationTransfer extends JPanel implements ActionListener {
	final JLabel baseFileLabel;
	final JLabel animFileLabel;
	final JLabel outFileLabel;
	final JLabel transSingleLabel;
	final JLabel pickAnimLabel;
	final JLabel visFromLabel;
	final JTextField baseFileInput;
	final JTextField animFileInput;
	final JTextField outFileInput;
	final JCheckBox transferSingleAnimation;
	JCheckBox useCurrentModel;
	final JButton baseBrowse;
	final JButton animBrowse;
	final JButton outBrowse;
	final JButton transfer;
	final JButton done;
	final JButton goAdvanced;
	final JComboBox<Animation> pickAnimBox;
	final JComboBox<Animation> visFromBox;
	DefaultComboBoxModel<Animation> baseAnims;
	DefaultComboBoxModel<Animation> animAnims;

	final JFileChooser fc = new JFileChooser();

	EditableModel sourceFile;
	EditableModel animFile;
	private final JFrame parentFrame;

	public AnimationTransfer(final JFrame parentFrame) {
		this.parentFrame = parentFrame;
		final MainPanel panel = MainFrame.getPanel();
		final EditableModel current;// ;
		if (panel != null && (current = panel.currentMDL()) != null && current.getFile() != null) {
			fc.setCurrentDirectory(current.getFile().getParentFile());
		} else if (SaveProfile.get().getPath() != null) {
			fc.setCurrentDirectory(new File(SaveProfile.get().getPath()));
		}

		baseFileLabel = new JLabel("Base file:");
		baseFileInput = new JTextField("");
		baseFileInput.setMinimumSize(new Dimension(200, 18));
		baseBrowse = new JButton("...");
		final Dimension dim = new Dimension(28, 18);
		baseBrowse.setMaximumSize(dim);
		baseBrowse.setMinimumSize(dim);
		baseBrowse.setPreferredSize(dim);
		baseBrowse.addActionListener(this);

		animFileLabel = new JLabel("Animation file:");
		animFileInput = new JTextField("");
		animFileInput.setMinimumSize(new Dimension(200, 18));
		animBrowse = new JButton("...");
		animBrowse.setMaximumSize(dim);
		animBrowse.setMinimumSize(dim);
		animBrowse.setPreferredSize(dim);
		animBrowse.addActionListener(this);

		outFileLabel = new JLabel("Output file:");
		outFileInput = new JTextField("");
		outFileInput.setMinimumSize(new Dimension(200, 18));
		outBrowse = new JButton("...");
		outBrowse.setMaximumSize(dim);
		outBrowse.setMinimumSize(dim);
		outBrowse.setPreferredSize(dim);
		outBrowse.addActionListener(this);

		transferSingleAnimation = new JCheckBox("", false);
		transferSingleAnimation.addActionListener(this);
		transSingleLabel = new JLabel("Transfer single animation:");

		pickAnimLabel = new JLabel("Animation to transfer:");
		pickAnimBox = new JComboBox<>();
		pickAnimBox.setEnabled(false);

		visFromLabel = new JLabel("Get visibility from:");
		visFromBox = new JComboBox<>();
		visFromBox.setEnabled(false);

		transfer = new JButton("Transfer");
		transfer.setMnemonic(KeyEvent.VK_T);
		transfer.setMinimumSize(new Dimension(200, 35));
		transfer.addActionListener(this);

		done = new JButton("Done");
		done.setMnemonic(KeyEvent.VK_D);
		done.setMinimumSize(new Dimension(80, 35));
		done.addActionListener(this);

		goAdvanced = new JButton("Go Advanced");
		goAdvanced.setMnemonic(KeyEvent.VK_G);
		goAdvanced.addActionListener(this);
		goAdvanced.setToolTipText(
				"Opens the traditional MatrixEater Import window responsible for this Simple Import, so that you can micro-manage particular settings before finishing the operation.");

		final GroupLayout layout = new GroupLayout(this);
		layout.setHorizontalGroup(
				layout.createSequentialGroup().addGap(12)
						.addGroup(
								layout.createParallelGroup(GroupLayout.Alignment.CENTER)
										.addGroup(layout.createParallelGroup().addGroup(layout.createSequentialGroup()
												.addGroup(layout.createParallelGroup().addComponent(baseFileLabel)
														.addComponent(animFileLabel).addComponent(outFileLabel))
												.addGap(16)
												.addGroup(layout.createParallelGroup().addComponent(baseFileInput)
														.addComponent(animFileInput).addComponent(outFileInput))
												.addGap(16)
												.addGroup(layout.createParallelGroup().addComponent(baseBrowse)
														.addComponent(animBrowse).addComponent(outBrowse)))
												.addGroup(layout.createSequentialGroup().addComponent(transSingleLabel)
														.addComponent(transferSingleAnimation)))
										.addGroup(layout.createSequentialGroup().addGap(48)
												.addGroup(layout.createParallelGroup().addComponent(pickAnimLabel)
														.addComponent(visFromLabel))
												.addGap(16)
												.addGroup(layout.createParallelGroup().addComponent(pickAnimBox)
														.addComponent(visFromBox)))
										.addGroup(layout.createSequentialGroup().addComponent(transfer)
												.addComponent(done))
										.addComponent(goAdvanced))
						.addGap(12));
		layout.setVerticalGroup(layout.createSequentialGroup().addGap(12)
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(baseFileLabel)
								.addComponent(baseFileInput).addComponent(baseBrowse))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(animFileLabel)
								.addComponent(animFileInput).addComponent(animBrowse))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(outFileLabel)
								.addComponent(outFileInput).addComponent(outBrowse))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(transSingleLabel).addComponent(transferSingleAnimation))
						.addGap(8)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(pickAnimLabel)
								.addComponent(pickAnimBox))
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER).addComponent(visFromLabel)
								.addComponent(visFromBox))
						.addGap(24).addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
								.addComponent(transfer).addComponent(done))
						.addGap(12).addComponent(goAdvanced)

				).addGap(12));
		setLayout(layout);
	}

	public void refreshModels() {
		if (baseFileInput.getText().length() > 0) {
			if (sourceFile == null || sourceFile.getFile() == null
					|| !baseFileInput.getText().equals(sourceFile.getFile().getPath())) {
				sourceFile = EditableModel.read(new File(baseFileInput.getText()));
			}
		}
		if (animFileInput.getText().length() > 0) {
			if (animFile == null || animFile.getFile() == null
					|| !animFileInput.getText().equals(animFile.getFile().getPath())) {
				animFile = EditableModel.read(new File(animFileInput.getText()));
			}
		}
	}

	public void forceRefreshModels() {
		// if( (sourceFile == null && !baseFileInput.getText().equals("")) ||
		// !baseFileInput.getText().equals(sourceFile.getFile().getPath()) ) {
		sourceFile = EditableModel.read(new File(baseFileInput.getText()));
		// JOptionPane.showMessageDialog(null,"Reloaded base model");
		// }
		// if( (animFile == null && !animFileInput.getText().equals("")) ||
		// !animFileInput.getText().equals(animFile.getFile().getPath()) ) {
		animFile = EditableModel.read(new File(animFileInput.getText()));
		// JOptionPane.showMessageDialog(null,"Reloaded anim model");
		// }
		updateBoxes();
	}

	// public void refreshSource() {
	// sourceFile = MDL.read(new File(baseFileInput.getText()));
	// }
	//
	// public void refreshAnim() {
	// animFile = MDL.read(new File(animFileInput.getText()));
	// }

	public void updateBoxes() {
		if (animFile != null) {
			final DefaultComboBoxModel<Animation> model = new DefaultComboBoxModel<>();

			for (int i = 0; i < animFile.getAnimsSize(); i++) {
				final Animation anim = animFile.getAnim(i);
				model.addElement(anim);
			}
			final ComboBoxModel<Animation> oldModel = pickAnimBox.getModel();
			boolean equalModels = oldModel.getSize() > 0;
			for (int i = 0; i < oldModel.getSize() && i < model.getSize() && equalModels; i++) {
				if (oldModel.getElementAt(i) != model.getElementAt(i)) {
					equalModels = false;
				}
			}
			if (!equalModels) {
				pickAnimBox.setModel(model);
			}
		}
		if (sourceFile != null) {
			final DefaultComboBoxModel<Animation> model = new DefaultComboBoxModel<>();

			for (int i = 0; i < sourceFile.getAnimsSize(); i++) {
				final Animation anim = sourceFile.getAnim(i);
				model.addElement(anim);
			}
			final ComboBoxModel<Animation> oldModel = visFromBox.getModel();
			boolean equalModels = oldModel.getSize() > 0;
			for (int i = 0; i < oldModel.getSize() && i < model.getSize() && equalModels; i++) {
				if (oldModel.getElementAt(i) != model.getElementAt(i)) {
					equalModels = false;
				}
			}
			if (!equalModels) {
				visFromBox.setModel(model);
			}
		}
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (e.getSource() == baseBrowse) {
			fc.setDialogTitle("Open");
			final int returnValue = fc.showOpenDialog(this);

			if (returnValue == JFileChooser.APPROVE_OPTION) {
				String filepath = fc.getSelectedFile().getPath();
				if (!filepath.toLowerCase().endsWith(".mdl") && !filepath.toLowerCase().endsWith(".mdx")) {
					filepath += ".mdl";
				}
				baseFileInput.setText(filepath);
				refreshModels();
				updateBoxes();
			}
		} else if (e.getSource() == animBrowse) {
			fc.setDialogTitle("Open");
			final int returnValue = fc.showOpenDialog(this);

			if (returnValue == JFileChooser.APPROVE_OPTION) {
				String filepath = fc.getSelectedFile().getPath();
				if (!filepath.toLowerCase().endsWith(".mdl") && !filepath.toLowerCase().endsWith(".mdx")) {
					filepath += ".mdl";
				}
				animFileInput.setText(filepath);
				refreshModels();
				updateBoxes();
			}
		} else if (e.getSource() == outBrowse) {
			fc.setDialogTitle("Save");
			final int returnValue = fc.showSaveDialog(this);

			if (returnValue == JFileChooser.APPROVE_OPTION) {
				String filepath = fc.getSelectedFile().getPath();
				if (!filepath.toLowerCase().endsWith(".mdl") && !filepath.toLowerCase().endsWith(".mdx")) {
					filepath += ".mdl";
				}
				outFileInput.setText(filepath);
			}
		} else if (e.getSource() == transferSingleAnimation) {
			updateBoxes();
			pickAnimBox.setEnabled(transferSingleAnimation.isSelected());
			visFromBox.setEnabled(transferSingleAnimation.isSelected());
		} else if (e.getSource() == transfer) {
			// refreshModels();
			//
			// if( !transferSingleAnimation.isSelected() ) {
			// ImportPanel host = new ImportPanel(sourceFile,animFile,false);
			// host.animTransfer(transferSingleAnimation.isSelected(),
			// pickAnimBox.getItemAt(pickAnimBox.getSelectedIndex()),
			// visFromBox.getItemAt(visFromBox.getSelectedIndex()),false);
			//
			// sourceFile.printTo(new File(outFileInput.getText()));
			// JOptionPane.showMessageDialog(null, "Animation transfer done!");
			//
			// forceRefreshModels();
			// }
			// else
			// {
			// Thread watcher = new Thread(new Runnable() {
			// public void run()
			// {
			// final ImportPanel importPanel = new
			// ImportPanel(sourceFile,animFile,false);
			// importPanel.animTransfer(transferSingleAnimation.isSelected(),
			// pickAnimBox.getItemAt(pickAnimBox.getSelectedIndex()),
			// visFromBox.getItemAt(visFromBox.getSelectedIndex()),false);
			//
			//
			// ImportPanel importPanel2 = new
			// ImportPanel(sourceFile,MDL.read(sourceFile.getFile()),false);
			// importPanel2.animTransferPartTwo(transferSingleAnimation.isSelected(),
			// pickAnimBox.getItemAt(pickAnimBox.getSelectedIndex()),
			// visFromBox.getItemAt(visFromBox.getSelectedIndex()),false);
			//
			// sourceFile.printTo(new File(outFileInput.getText()));
			// JOptionPane.showMessageDialog(null, "Animation transfer done!");
			//
			// forceRefreshModels();
			//
			// }
			// });
			// watcher.start();
			// }
			doTransfer(false);
		} else if (e.getSource() == goAdvanced) {
			doTransfer(true);
		} else if (e.getSource() == done) {
			parentFrame.setVisible(false);
			parentFrame.dispose();
		}
	}

	public void doTransfer(final boolean show) {
		final EditableModel sourceFile = EditableModel.read(new File(baseFileInput.getText()));
		final EditableModel animFile = EditableModel.read(new File(animFileInput.getText()));

		if (!transferSingleAnimation.isSelected()) {
			final ImportPanel importPanel = new ImportPanel(sourceFile, animFile, show);
			new Thread(() -> {
				importPanel.animTransfer(transferSingleAnimation.isSelected(),
						pickAnimBox.getItemAt(pickAnimBox.getSelectedIndex()),
						visFromBox.getItemAt(visFromBox.getSelectedIndex()), show);
				while (importPanel.getParentFrame().isVisible()
						&& (!importPanel.importStarted() || importPanel.importEnded())) {
					// JOptionPane.showMessageDialog(null, "check 1!");
					try {
						Thread.sleep(1);
					} catch (final Exception e) {
						ExceptionPopup.display("MatrixEater detected error with Java's wait function", e);
					}
				}
				// if( !importPanel.getParentFrame().isVisible() &&
				// !importPanel.importEnded() )
				// JOptionPane.showMessageDialog(null,"bad voodoo
				// "+importPanel.importSuccessful());
				// else
				// JOptionPane.showMessageDialog(null,"good voodoo
				// "+importPanel.importSuccessful());
				// if( importPanel.importSuccessful() )
				// {
				// newModel.saveFile();
				// loadFile(newModel.getFile());
				// }

				if (importPanel.importStarted()) {
					while (!importPanel.importEnded()) {
						// JOptionPane.showMessageDialog(null, "check 2!");
						try {
							Thread.sleep(1);
						} catch (final Exception e) {
							ExceptionPopup.display("MatrixEater detected error with Java's wait function", e);
						}
					}

					// JOptionPane.showMessageDialog(null, "Animation
					// transfer 99% done!");

					if (importPanel.importSuccessful()) {
						String filepath = outFileInput.getText();
						if (!filepath.toLowerCase().endsWith(".mdl") && !filepath.toLowerCase().endsWith(".mdx")) {
							filepath += ".mdl";
						}
						sourceFile.printTo(new File(filepath));
						JOptionPane.showMessageDialog(null, "Animation transfer done!");
					}
				}

				// forceRefreshModels();
			}).start();
		} else {
			final Thread watcher = new Thread(() -> {
				final ImportPanel importPanel = new ImportPanel(sourceFile, animFile, show);
				importPanel.animTransfer(transferSingleAnimation.isSelected(),
						pickAnimBox.getItemAt(pickAnimBox.getSelectedIndex()),
						visFromBox.getItemAt(visFromBox.getSelectedIndex()), show);

				// while(importPanel.getParentFrame().isVisible() &&
				// (!importPanel.importStarted() ||
				// importPanel.importEnded()) )
				while (importPanel.getParentFrame().isVisible()
						&& (!importPanel.importStarted() || importPanel.importEnded())) {
					// JOptionPane.showMessageDialog(null, "check 1!");
					try {
						Thread.sleep(1);
					} catch (final Exception e) {
						ExceptionPopup.display("MatrixEater detected error with Java's wait function", e);
					}
				}
				// if( !importPanel.getParentFrame().isVisible() &&
				// !importPanel.importEnded() )
				// JOptionPane.showMessageDialog(null,"bad voodoo
				// "+importPanel.importSuccessful());
				// else
				// JOptionPane.showMessageDialog(null,"good voodoo
				// "+importPanel.importSuccessful());
				// if( importPanel.importSuccessful() )
				// {
				// newModel.saveFile();
				// loadFile(newModel.getFile());
				// }

				if (importPanel.importStarted()) {
					while (!importPanel.importEnded()) {
						// JOptionPane.showMessageDialog(null, "check 2!");
						try {
							Thread.sleep(1);
						} catch (final Exception e) {
							ExceptionPopup.display("MatrixEater detected error with Java's wait function", e);
						}
					}

					// JOptionPane.showMessageDialog(null, "Animation
					// transfer 99% done!");

					if (importPanel.importSuccessful()) {
						final ImportPanel importPanel2 = new ImportPanel(sourceFile, EditableModel.read(sourceFile.getFile()),
								show);
						importPanel2.animTransferPartTwo(transferSingleAnimation.isSelected(),
								pickAnimBox.getItemAt(pickAnimBox.getSelectedIndex()),
								visFromBox.getItemAt(visFromBox.getSelectedIndex()), show);

						while (importPanel2.getParentFrame().isVisible()
								&& (!importPanel2.importStarted() || importPanel2.importEnded())) {
							// JOptionPane.showMessageDialog(null, "check
							// 1!");
							try {
								Thread.sleep(1);
							} catch (final Exception e) {
								ExceptionPopup.display("MatrixEater detected error with Java's wait function", e);
							}
						}
						// if( !importPanel.getParentFrame().isVisible() &&
						// !importPanel.importEnded() )
						// JOptionPane.showMessageDialog(null,"bad voodoo
						// "+importPanel.importSuccessful());
						// else
						// JOptionPane.showMessageDialog(null,"good voodoo
						// "+importPanel.importSuccessful());
						// if( importPanel.importSuccessful() )
						// {
						// newModel.saveFile();
						// loadFile(newModel.getFile());
						// }

						if (importPanel2.importStarted()) {
							while (!importPanel2.importEnded()) {
								// JOptionPane.showMessageDialog(null,
								// "check 2!");
								try {
									Thread.sleep(1);
								} catch (final Exception e) {
									ExceptionPopup.display("MatrixEater detected error with Java's wait function",
											e);
								}
							}

							// JOptionPane.showMessageDialog(null,
							// "Animation transfer 99% done!");

							if (importPanel2.importSuccessful()) {
								JOptionPane.showMessageDialog(null, "Animation transfer done!");
								sourceFile.printTo(new File(outFileInput.getText()));

								// forceRefreshModels();
							}
						}
					}
				}
			});
			watcher.start();
		}
	}

	public static void main(final String[] args) {
		try {
			// Set cross-platform Java L&F (also called "Metal")
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (final UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException | ClassNotFoundException e) {
			// handle exception
		}

		final JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setIconImage((new ImageIcon(MainFrame.class.getResource("ImageBin/Anim.png"))).getImage());
		final AnimationTransfer transfer = new AnimationTransfer(frame);
		frame.setContentPane(transfer);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
}

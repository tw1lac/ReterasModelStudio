package com.matrixeater.hacks;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public final class BLPNulltexGenWar3 {
	private static final List<String> failed = new ArrayList<>();
	private static final List<String> passed = new ArrayList<>();
	private static File root;
	private static File checkFolder;
	private static File nulltex;
	private static File nullgenRoot;
	private static int count = 0;

	public static void main(final String[] args) {
		if (args.length != 1) {
			System.err.println("Usage: <mutableModelsDirectory>");
			System.exit(-1);
		}
		final File target = new File(args[0]);
		root = target;
		File texRoot = new File(target.getParent() + "/ns" + "textures");
		File compressRoot = new File(target.getParent() + "/ns" + "texturesCompressed");
		checkFolder = new File(target.getParent() + "/wc3nosound-bk");
		nulltex = new File(target.getParent() + "/scratch/nulltex.blp");
		nullgenRoot = new File(target.getParent() + "/nullgenTextures");
		operate(target);
		System.out.println("Passed: " + passed.size());
		System.out.println("Failed: " + failed.size());
		System.out.println("Fail list:");
		for (final String failedName : failed) {
			System.out.println(failedName);
		}
	}

	private static void operate(final File target) {
		if (target.isDirectory()) {
			for (final File file : target.listFiles()) {
				operate(file);
			}
		} else {
			if (target.getName().toLowerCase().endsWith(".blp")) {
				count++;
				if (count % 300 == 0) {
					System.out.println(count);
				}
				try {
					final String relativePath = target.getAbsolutePath().substring(root.getAbsolutePath().length());
					// final File textureTarget = new File(texRoot +
					// relativePath);
					// final File compressTarget = new File(compressRoot +
					// relativePath);
					final File checkTarget = new File(checkFolder + relativePath);
					final File nullgenTarget = new File(nullgenRoot + relativePath);
					// textureTarget.getParentFile().mkdirs();
					// compressTarget.getParentFile().mkdirs();
					// Files.copy(target.toPath(), textureTarget.toPath(),
					// StandardCopyOption.REPLACE_EXISTING);
					// // final BufferedImage blpImage =
					// // BlpFile.read(textureTarget);
					// final boolean generateMipMaps =
					// relativePath.toLowerCase().contains("\\units")
					// || relativePath.toLowerCase().contains("\\abilities")
					// || relativePath.toLowerCase().contains("\\buildings")
					// || relativePath.toLowerCase().contains("\\textures")
					// || relativePath.toLowerCase().contains("\\environment")
					// || relativePath.toLowerCase().contains("\\doodads")
					// || relativePath.toLowerCase().contains("\\sharedmodels")
					// ||
					// relativePath.toLowerCase().contains("\\objects\\inventoryitems")
					// ||
					// relativePath.toLowerCase().contains("\\replaceabletextures\\splats")
					// ||
					// relativePath.toLowerCase().startsWith("\\ReplaceableTextures\\Splats");//
					// ReplaceableTextures\Splats
					// // BlpFile.writePalettedBLP(blpImage, compressTarget,
					// // blpImage.getColorModel().hasAlpha(),
					// // generateMipMaps, false);
					// BLPHandler.get().compressBLPHopefullyALot(textureTarget,
					// compressTarget, generateMipMaps);
					if (!checkTarget.exists()) {
						// System.out.println(relativePath);
						nullgenTarget.getParentFile().mkdirs();
						Files.copy(nulltex.toPath(), nullgenTarget.toPath(), StandardCopyOption.REPLACE_EXISTING);
					}
					passed.add(target.getPath());
				} catch (final Exception e) {
					failed.add(target.getPath());
				}
			}
		}
	}

}

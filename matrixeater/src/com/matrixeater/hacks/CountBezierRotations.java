package com.matrixeater.hacks;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.hiveworkshop.wc3.mdl.AnimFlag;
import com.hiveworkshop.wc3.mdl.EditableModel;
import com.hiveworkshop.wc3.mdl.v2.timelines.InterpolationType;

public class CountBezierRotations {
	private static final Map<String, Map<String, Integer>> bigTreeMap = new HashMap<>();

	public static void main(final String[] args) {
		traverse(new File("C:\\MPQBuild"));

		for (final String key : bigTreeMap.keySet()) {
			final Map<String, Integer> two = bigTreeMap.get(key);
			System.out.println(key);
			final Set<String> interpKeys = two.keySet();
			for (final String interpKey : interpKeys) {
				System.out.println("\t" + interpKey + ": " + two.get(interpKey));
			}
		}
	}

	public static void traverse(final File file) {
		if (file.isDirectory()) {
			for (final File subFile : file.listFiles()) {
				traverse(subFile);
			}
		} else {
			if (file.getName().toLowerCase().endsWith(".mdx")) {
				final EditableModel model = EditableModel.read(file);
				final List<AnimFlag> allAnimFlags = model.getAllAnimFlags();
				for (final AnimFlag flag : allAnimFlags) {
					final InterpolationType interpTypeAsEnum = flag.getInterpTypeAsEnum();
					final String flagName = flag.getName();
                    Map<String, Integer> interpMap = bigTreeMap.computeIfAbsent(flagName, k -> new HashMap<>());

                    Integer previousCount = interpMap.get(interpTypeAsEnum.name());
					if (previousCount == null) {
						previousCount = 1;
					} else {
						previousCount++;
					}
					interpMap.put(interpTypeAsEnum.name(), previousCount);
					if ((interpTypeAsEnum == InterpolationType.BEZIER) && flagName.equals("Rotation")) {
						System.out.println(file.getPath());
					}
				}
			}
		}
	}

}

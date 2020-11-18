package com.matrixeater.hacks;

import java.io.File;

import com.hiveworkshop.wc3.mpq.MpqCodebase;
import com.hiveworkshop.wc3.mpq.MpqCodebase.LoadedMPQ;

public class LemmeJustGrabAFile {
	public static void main(final String[] args) {
		final File file = new File("C:\\Users\\micro\\Downloads\\Goblin_Survival_1.3b3.w3x");
		final LoadedMPQ loadMPQ = MpqCodebase.get().loadMPQ(file.toPath());
		System.out.println(loadMPQ.has("war3map.j"));
		System.out.println(loadMPQ.has("scripts\\war3map.j"));
		loadMPQ.unload();
	}
}

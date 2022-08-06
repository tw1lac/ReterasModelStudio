package com.hiveworkshop.rms.util.fileviewers;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class TxtViewer extends FileViewer {

	protected ArrayList<String> getStrings(InputStream in) {
		ArrayList<String> htmlTableStrings = new ArrayList<>();


		htmlTableStrings.add("<div>");
		try (BufferedReader r = new BufferedReader(new InputStreamReader(in))) {
			r.lines().forEach(l -> {
//				htmlTableStrings.add(l + "<br>");
				htmlTableStrings.add("<p>" + l + "</p>");
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		htmlTableStrings.add("</div>");

		return htmlTableStrings;
	}
}

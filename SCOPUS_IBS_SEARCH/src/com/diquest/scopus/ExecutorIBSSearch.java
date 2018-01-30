package com.diquest.scopus;

import com.diquest.scopus.search.IBSSearch;

public class ExecutorIBSSearch {
	public static void main(String[] args) throws Exception {
		System.setProperty("EJIANA_HOME", "D:\\Project_WorkSpace\\2014\\SCOPUS_IBS_SEARCH");
		InputParameter param = new InputParameter(args);
		IBSSearch ibs = new IBSSearch(param);
		ibs.search();
	}

	public static class InputParameter {
		public String source;
		public String target;

		public InputParameter(String[] params) throws Exception {
			for (String s : params) {
				parseParameter(s);
			}
		}

		private void parseParameter(String s) throws Exception {
			String param = s.substring(3);
			if (s.startsWith("-sr")) {
				source = param;
			} else if (s.startsWith("-tg")) {
				target = param;
			}
		}
	}
}

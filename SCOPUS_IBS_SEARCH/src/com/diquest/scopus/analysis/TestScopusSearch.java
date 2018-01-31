/**
 * 
 */
package com.diquest.scopus.analysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import com.diquest.ir.client.command.CommandSearchRequest;
import com.diquest.ir.common.exception.IRException;
import com.diquest.ir.common.msg.protocol.Protocol;
import com.diquest.ir.common.msg.protocol.query.Query;
import com.diquest.ir.common.msg.protocol.query.QuerySet;
import com.diquest.ir.common.msg.protocol.query.SelectSet;
import com.diquest.ir.common.msg.protocol.query.WhereSet;
import com.diquest.ir.common.msg.protocol.result.Result;
import com.diquest.ir.common.msg.protocol.result.ResultSet;

/**
 * @author coreawin
 * @date 2014. 6. 17.
 * @Version 1.0
 */
public class TestScopusSearch {

	/**
	 * 2012년 상위 1% 저널의 issn 번호
	 */
	@Deprecated
	static String[] topjournal = new String[] { "00079235", "00284793", "00346861", "00092665", "14710056", "01406736", "00280836", "14710072",
			"07320582", "14761122", "10614036", "1474175X", "00018732", "14741733", "14741776", "10870156", "00928674", "1471003X", "17483387",
			"00368075", "00319333", "00987484", "00664154", "17494885", "00796700", "15292908", "15534006", "19345909", "14702045", "03060012",
			"15356108", "10788956", "14744422", "15435008", "15487091", "00664146", "00796425", "03701573", "17401526", "00316997", "14338351",
			"17554330", "03621642", "00014842", "14657392", "0147006X", "14733099", "10747613", "00664278", "17452473", "0140525X", "0732183X",
			"10810706", "1461023X", "17480132", "00664197", "08938512", "17561833", "10922172", "00670049", "15317331", "13646613", "08966273",
			"00332909", "17594758", "01695347", "01675729", "10972765", "00664308", "15491676", "10976256", "00097322", "19475454", "03601285",
			"17594774", "13594184", "0163769X", "09359648", "0002953X", "15504131", "00664219", "15525260", "1758678X", "10889051", "19411405", };

	static String[] topjournalid = new String[] { "28773", "15847", "23340", "16590", "20425", "16115", "21206", "20651", "20315", "12464", "18991",
			"17854", "85291", "21318", "5200152704", "23571", "23350", "26651", "5700165152", "18434", "144840", "17437", "16801", "29719", "18990",
			"15819", "17899", "24004", "26465", "18100156701", "12354", "20313", "29093", "16721", "22471", "22657", "6100153018", "15359", "16860",
			"12010", "20798", "26038", "26978", "12108", "17500155114", "4000151822", "29229", "21315", "19014", "21100212318", "14181", "28677",
			"27538", "11300153736", "120008", "28705", "19479", "26574", "26756", "19700173023", "146172", "19881", "120017", "19651", "21191",
			"25781", "29709", "12519", "26555", "27825", "18395", "22401", "14365", "21100199127", "17436", "26968", "19700174677", "21706", "17814",
			"12000154482", "17700156408", "23183", "22581", "28599", "28366", "21100198409", "130139", "14305", "19700188395", "17700156734", "19133",
			"17269", "17809", "12074", "17600155041", "14599", "16715", "17956", "16594", "14750", "24242", "29993", "21928", "17924", "27540",
			"24535", "17900156715", "13140", "24309", "26053", "21100196101", "18063", "13345", "26055", "12996", "24254", "28801", "18935", "13348",
			"14231", "??", "13531", "24721", "25820" };

	static Set<String> journalISSNSet = new HashSet<String>();
	static Set<String> journalIDSet = new HashSet<String>();
	static Map<String, String> journalIDAscjMap = Collections.emptyMap();
	static {
		try {
			SrcIDReader sidr = new SrcIDReader("/com/diquest/scopus/analysis/top1JournalIdList.txt");
			journalIDSet = sidr.readId();

			sidr = new SrcIDReader("/com/diquest/scopus/analysis/topJournalASJCInfo.txt");
			journalIDAscjMap = sidr.readSrcFirstASJC();

		} catch (Exception e) {
			for (String j : topjournal) {
				journalISSNSet.add(j);
			}
			for (String j : topjournalid) {
				journalIDSet.add(j);
			}
		}
	}

	static String[] titles = new String[] { "", "84880800169", "84880733249", "84867765569", "79960515455", "79952613531", "67749106310",
			"61649128006", "", "34248545460", "", };

	static BufferedReader br = null;

	private LinkedList<LinkedList<String>> init() {
		LinkedList<LinkedList<String>> gdatalist = new LinkedList<LinkedList<String>>();
		try {

			br = new BufferedReader(new FileReader(new File("f:\\Documents\\Private\\2015\\IBS\\eid.txt")));
			String line = null;
			LinkedList<String> datalist = new LinkedList<String>();
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.startsWith("==")) {
					gdatalist.add(new LinkedList<String>(datalist));
					datalist = new LinkedList<String>();
					continue;
				}
				datalist.add(line.trim());
			}
			if (datalist.size() > 0) {
				gdatalist.add(new LinkedList<String>(datalist));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// return new LinkedList<LinkedList<String>> ();
		return gdatalist;
	}

	public LinkedList<String> searchTitle() {
		return searchTitle(init());
	}

	public LinkedList<String> searchTitle(LinkedList<LinkedList<String>> gdatalist) {
		LinkedList<String> datalist = new LinkedList<String>();
		// LinkedHashMap<String, DOC> datas = new LinkedHashMap<String, DOC>();
		String adminIP = "203.250.207.72"; // Admin 서버 IP
		int adminPORT = 5555; // Admin 서버 PORT
		String collectionName = "SCOPUS_2016"; // 컬렉션명

		// 전송하기 위한 Query를 설정합니다.

		Query query = new Query();
		query.setSearch(true); // 검색 여부 설정
		query.setResultCutOffSize(2);
		query.setFaultless(true);
		query.setPrintQuery(true);
		query.setIgnoreBrokerTimeout(true);
		SelectSet[] selectSet = new SelectSet[] { new SelectSet("EID", (byte) (Protocol.SelectSet.NONE), 300),
				new SelectSet("AU_ID", (byte) (Protocol.SelectSet.NONE), 300), new SelectSet("CIT_COUNT", (byte) (Protocol.SelectSet.NONE), 300),
				new SelectSet("ISSN", (byte) (Protocol.SelectSet.NONE), 300), new SelectSet("SRCTITLE", Protocol.SelectSet.NONE, 300),
				new SelectSet("TITLE", Protocol.SelectSet.NONE, 300), new SelectSet("AUTHOR_NAME", Protocol.SelectSet.NONE, 300),
				new SelectSet("CR_NAME", Protocol.SelectSet.NONE, 300), new SelectSet("CR_EMAIL", Protocol.SelectSet.NONE, 300),
				new SelectSet("SORTYEAR", Protocol.SelectSet.NONE, 300), new SelectSet("SRCID", Protocol.SelectSet.NONE, 300),
				new SelectSet("ASJC", Protocol.SelectSet.NONE, 300), new SelectSet("DOCTYPE", Protocol.SelectSet.NONE, 300), };
		query.setSelect(selectSet);
		query.setSearchOption(Protocol.SearchOption.PHRASEEXACT | Protocol.SearchOption.CACHE);
		query.setFrom(collectionName);
		query.setResult(0, 10);

		for (LinkedList<String> dl : gdatalist) {
			LinkedList<DOC> datas = new LinkedList<DOC>();
			for (String title : dl) {
				// System.out.println("searching... " + title);
				boolean checkT = false;
				query.setWhere(null);
				title = title.replaceAll("-", " ");
				// System.out.println(title);
				WhereSet[] whereSet = new WhereSet[] { new WhereSet("IDX_TITLE_E", Protocol.WhereSet.OP_HASALL, title, 150),
						new WhereSet(Protocol.WhereSet.OP_OR), new WhereSet("IDX_EID", Protocol.WhereSet.OP_HASALL, title, 150), };
				query.setWhere(whereSet);
				QuerySet querySet = new QuerySet(1);
				querySet.addQuery(query);
				Result[] resultlist = null;
				CommandSearchRequest command = new CommandSearchRequest(adminIP, adminPORT);
				int returnCode;
				try {
					returnCode = command.request(querySet);
					if (returnCode >= 0) {
						ResultSet results = command.getResultSet();
						resultlist = results.getResultList();
					} else {
						resultlist = new Result[1];
						resultlist[0] = new Result();
					}
					Result result = null;
					for (int k = 0; resultlist != null && k < resultlist.length; k++) {
						result = resultlist[k];
						// 검색 결과 출력
						if (result.getRealSize() != 0) {
							if (result.getRealSize() > 1) {
								System.out.println(result.getRealSize() + " ========== WARN " + title);
								checkT = true;
							}
							DOC d = null;
							boolean checkMatch = false;
							for (int i = 0; i < result.getRealSize(); i++) {
								DOC doc = new DOC();
								int columnIdx = 0;
								doc.eid = new String(result.getResult(i, columnIdx++));
								doc.auid = new String(result.getResult(i, columnIdx++));
								doc.cit_count = new String(result.getResult(i, columnIdx++));
								doc.issn = new String(result.getResult(i, columnIdx++)).replaceAll("-", "");
								doc.srctitle = new String(result.getResult(i, columnIdx++));
								String ti = new String(result.getResult(i, columnIdx++));
								doc.authorname = new String(result.getResult(i, columnIdx++)).replaceAll("\n", "; ").replaceAll("\t", " ");
								doc.crname = new String(result.getResult(i, columnIdx++));
								doc.cremail = new String(result.getResult(i, columnIdx++));
								doc.sortyear = new String(result.getResult(i, columnIdx++));
								doc.srcid = new String(result.getResult(i, columnIdx++)).trim();
								doc.asjc = new String(result.getResult(i, columnIdx++)).trim().replaceAll("\n", " ");
								doc.doctype = new String(result.getResult(i, columnIdx++)).trim();
								doc.title = ti;
								doc.auid_cnt = String.valueOf(doc.auid.split("\n").length);
								doc.isTopJ = journalIDSet.contains(doc.srcid) ? "TRUE" : "FALSE";
								doc.srcFirstAsjc = journalIDAscjMap.get(doc.srcid) != null ? journalIDAscjMap.get(doc.srcid) : "";
								String bigAsjc = doc.srcFirstAsjc;
								if (bigAsjc.length() > 2) {
									bigAsjc = bigAsjc.substring(0, 2);
								}

								if (bigAsjc.length() > 0) {
									String[] asjcCode = doc.asjc.split(" ");
									for (String asjc : asjcCode) {
										if (asjc.startsWith(bigAsjc)) {
											doc.jornalAjsc += asjc + " ";
										}
									}
								}
								doc.jornalAjsc = doc.jornalAjsc.trim();

								// System.out.println("asjc : " + doc.asjc);
								// System.out.println("doctype : " +
								// doc.doctype);
								// doc.isTopJ =
								// journalISSNSet.contains(doc.issn) ? "TRUE" :
								// "FALSE";
								if (result.getRealSize() > 1) {
									if (ti.replaceAll("[,\\.\\-]", " ").equals(title.replaceAll("[,\\.\\-]", " "))) {
										// System.out.println("FIND title
										// equals");
										try {
											// System.out.println("PUT E \t" +
											// title);
											HCPCheck.checkHCP(doc);
											datas.add(doc);
											// datas.put(doc.eid, doc);
											checkMatch = true;
											d = null;
											break;
										} catch (SQLException e) {
											e.printStackTrace();
										}
									}
									d = doc;
								} else {
									try {
										// System.out.println("PUT \t" + title);
										HCPCheck.checkHCP(doc);
										HCPCheck.checkASJC(doc);

										datas.add(doc);
										// datas.put(doc.eid, doc);
										break;
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
							if (result.getRealSize() > 1 && checkMatch == false) {
								// System.out.println("PUT M\t" + title);
								if (d != null) {
									d.title = "M_ " + d.title;
								}
								datas.add(d != null ? d : new DOC());
								// datas.put("Multi Titles " +
								// System.nanoTime(),
								// d!=null?d:new DOC());
							}
						} else {
							// System.out.println("PUT N\t" + title);
							datas.add(new DOC());
							// datas.put("No Titles " + System.nanoTime(), new
							// DOC());ㅁ
						}
					}
				} catch (IRException e) {
					e.printStackTrace();
				}
			}

			// System.out.println("TITLE\tEID\tTC\tHCP\tHCP
			// %\t저자수\tissn\t상위저널\t저널명");
			System.out.println();
			// System.out.println("EID\t저자순위\t저자들\t교신저자이름\t교신저자이메일\tTC\tissn\tPY\t저널1%\t저널명\tHCP%\t저자수");
			System.out.println("JCR IF Top 1%\tASJC\t상위1%ASJC\teid\tissn\tcorr-author");
			for (DOC d : datas) {

				// DOC d = datas.get(k);
				// System.out.println(d.title + "\t" + d.eid + "\t" +
				// d.cit_count + "\t" + d.isHCP + "\t" + d.ranking + "\t"
				// + d.auid_cnt + "\t" + d.issn + "\t" + d.isTopJ + "\t" +
				// d.srctitle);
				String data = d.asjc + " \t" + d.eid + " \t" + d.issn + " \t" + d.crname + " \t" + d.cit_count + " \t" + d.srctitle + " \t"
						+ d.srcFirstAsjc + " \t" + d.jornalAjsc + " \t" + d.isTopJ + " \t" + d.hcpFirstAsjc + " \t" + d.hcpSubAsjc + " \t"
						+ d.isFirstHCP + " \t" + ((d.hcpFirstAsjc.trim().length() > 0 || d.hcpSubAsjc.trim().length() > 0) ? "TRUE" : "FALSE");
				datalist.add(data);
				// System.out.println(data);
				// System.out.println(d.eid + "\t \t" + d./*authorname + "\t" +
				// d.crname + "\t" + d.cremail + "\t" + d.cit_count
				// + "\t" + d.issn + "\t" + d.sortyear + "\t" + d.isTopJ + "\t"
				// + d.srctitle + "\t" + d.ranking + "\t"
				// + d.auid_cnt + "\t" + d.title);*/
			}
			// System.out.println(datas.size());
		}
		
		
		return datalist;
	}

	/**
	 * @author coreawin
	 * @date 2014. 6. 17.
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		TestScopusSearch searcher = new TestScopusSearch();
		ExcelUtil u = new ExcelUtil();

		File dir = new File("D:\\Project_Document\\2017\\KISTI\\IBS\\분석\\20170428\\2017.03.IBS\\본원 및 캠퍼스 연구단");
		File[] files = dir.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				if (name.endsWith(".xlsx")) {
					return true;
				}
				return false;
			}
		});
		searchTOP1Journal(searcher, u, files);

//		dir = new File("E:\\프로젝트\\2017\\KISTI\\ibs\\분석\\20170530\\본원 및 캠퍼스 연구단");
//		files = dir.listFiles(new FilenameFilter() {
//			@Override
//			public boolean accept(File dir, String name) {
//				if (name.endsWith(".xlsx")) {
//					return true;
//				}
//				return false;
//			}
//		});
//		searchTOP1Journal(searcher, u, files);

		// dir = new File("E:\\IBS_검색결과\\외부연구단\\");
		// files = dir.listFiles(new FilenameFilter() {
		//
		// @Override
		// public boolean accept(File dir, String name) {
		// if (name.endsWith(".xlsx")) {
		// return true;
		// }
		// return false;
		// }
		// });
		// searchTOP1Journal(searcher, u, files);

		// new TestScopusSearch().searchTitle();
	}

	private static void searchTOP1Journal(TestScopusSearch searcher, ExcelUtil u, File[] files) throws IOException {
		for (File f : files) {
			Map<Integer, LinkedList<String>> mapData = u.readExcel(f);
			Set<Integer> sets = mapData.keySet();
			for (Integer i : sets) {
				LinkedList<LinkedList<String>> param = new LinkedList<LinkedList<String>>();
				param.add(mapData.get(i));
				LinkedList<String> list = searcher.searchTitle(param);
				mapData.put(i, list);
			}
			u.writeExcel(f, mapData);
		}
	}

	/**
	 * top1projasc : 상위 1%에 포함된 문서라면, 해당 분류의 쓰레숄드를 기준으로 문헌이 다른 분류로 등록되어 있다면, 해당
	 * 분류로도 쓰레숄드로 HCP를 비교한다. 해당 쓰레숄드 값이 다른 분류의 상위 1% 쓰레숄드보다 크다면 이곳에 등록
	 * 2015-06-15
	 * 
	 * @author pc
	 * @date 2015. 6. 15.
	 */
	public class DOC {
		public String eid = "", auid = " ", cit_count = " ", issn = " ", srctitle = " ", auid_cnt = " ", title = " ", isHCP = " ", ranking = " ",
				isTopJ = " ", authorname = " ", crname = " ", cremail = " ", sortyear = " ", srcid = " ", asjc = " ", hcpasjc = " ", doctype = " ",
				srcFirstAsjc = " ", jornalAjsc = " ", hcpEtcAsjc = " ", hcpFirstAsjc = " ", isFirstHCP = " ", hcpSubAsjc = " ";

	}
}

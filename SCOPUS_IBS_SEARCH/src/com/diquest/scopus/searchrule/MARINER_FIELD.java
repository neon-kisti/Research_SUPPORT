/**
 * 
 */
package com.diquest.scopus.searchrule;

import java.util.HashSet;
import java.util.Set;

/**
 * 마리너 검색 필드 명 정의
 * 
 * @author neon
 * @date 2014. 5. 2.
 * @Version 1.0
 */
public class MARINER_FIELD {
	/**
	 * TS : title, abs, key를 합친title, abs, key를 합친것.것.<br>
	 * TK : title, key를 합친title, key를 합친것.것.<br>
	 * AUTHORNAME_P : Author Name 검색시  {저자명}* 검색을 지원하기 위한 검색 필드<br>
	 * ALL : eid, title, abs, key, author name, affiliation name, doi, srctitle 를 합친것.
	 * @author coreawin
	 * @date   2014. 6. 16.
	 * @Version 1.0
	 */
	public enum SCOPUS_MARINER_FIELD {
		EID, FEID, OEID, TITLE, ABS, AUTHKEY, INDEXTERMS, AFID("AF_ID"), AFIDG("AF_ID_G"),
		AFFIL, AFFILCOUNTRY, DAFFIL("D_AFFIL"), DAFFIL_E("D_AFFIL_E"), DAFFILCOUNTRY("D_AFFILCOUNTRY"),
		AUTHORNAME("AUTHOR_NAME"), AUTHFIRST, AUTHLASTNAME, AUTHINIT("AUTH_INITIALNAME"),
		AUTHINDEXNAME("AUTH_INDEXNAME"), AUTHEMAIL("AUTHOR_EMAIL"), AUID("AU_ID"), AUIDG("AU_ID_G"),
		DOI, ASJC, FIRSTASJC("FIRST_ASJC"), SRCTITLE, SRCTITLEABBREV("SRCTITLE_ABBREV"),
		SRCTYPE, SRCID, SRCCOUNTRY("SRC_COUNTRY"), PUBYEAR, SORTYEAR, REFEID("REF_EID"),
		REFCOUNT("REF_COUNT"), CITEID("CIT_EID"), CITCOUNT("CIT_COUNT"),
		DOCTYPE, CRCOUNTRY("CR_COUNTRY"), CRCITY("CR_CITY"), CREMAIL("CR_EMAIL"),
		CRORG("CR_ORG"), CRNAME("CR_NAME"), CRFIRST("CR_FIRST"), CRLAST("CR_LAST"), XML, XMLC("XML_C"),
		EISSN, ISSN, ISSNP, ISSUE, FIRSTPAGE, LASTPAGE, VOLUMN, IDTYPE, PII, PUI, CRAFID("CR_AFID"),
		CRDORG("CR_DORG"), CRDCOUNTRY("CR_DCOUNTRY"), CRINDEXNAME("CR_INDEXNAME"), KEY, KEY_E,
		ABS_E, TITLE_E, INDEXTERMS_E, AUTHKEY_E, AFFIL_E, SORTING, TS, ALL, GT_AGENCY, GT_ACRONYM, YEAR_AFFIL, YEAR_ASJC, YEAR_AUID, YEAR_CN, YEAR_FASJC, YEAR_AUKEY,
		ABS_EN, TITLE_EN, INDEXTERMS_EN, AUTHKEY_EN, 
		ABS_P, TITLE_P, INDEXTERMS_P, AUTHKEY_P, 
		AUTHORNAME_P, TK;
		private String value;

		private SCOPUS_MARINER_FIELD(String s) {
			this.value = s;
		}

		private SCOPUS_MARINER_FIELD() {
			this.value = this.name();
		}

		public String getValue() {
			return this.value;
		}

		public String getIndexField() {
			return "IDX_" + this.value;
		}

		public String getFilterField() {
			return "FILTER_" + this.value;
		}

		public String getSortField() {
			return "SORT_" + this.value;
		}
		
		public String getGroupField() {
			return "GROUP_" + this.value;
		}
		
		public String getStatField() {
			return "STAT_" + this.value;
		}
	}

	/**
	 * SCOPUS 화면에 출력될 GROUP_FIELD 항목.
	 */
	public static final SCOPUS_MARINER_FIELD[] _GROUP_FIELDS = new SCOPUS_MARINER_FIELD[] {
		SCOPUS_MARINER_FIELD.SORTYEAR,
	};
	
	
	/**
	 * SCOPUS 화면에 출력될 GROUP_FIELD 항목.
	 */
	public static final SCOPUS_MARINER_FIELD[] VIEW_STATITICS_INFO = new SCOPUS_MARINER_FIELD[] {

		SCOPUS_MARINER_FIELD.SORTYEAR,
		SCOPUS_MARINER_FIELD.FIRSTASJC,
		SCOPUS_MARINER_FIELD.AFFILCOUNTRY,
		SCOPUS_MARINER_FIELD.AFID,
		SCOPUS_MARINER_FIELD.DOCTYPE
	};
	
	public static final SCOPUS_MARINER_FIELD[] VIEW_TIME_GROUP_INFO = new SCOPUS_MARINER_FIELD[]{
		SCOPUS_MARINER_FIELD.YEAR_ASJC,
		SCOPUS_MARINER_FIELD.YEAR_FASJC,
		SCOPUS_MARINER_FIELD.YEAR_CN,
		SCOPUS_MARINER_FIELD.YEAR_AUID,
		SCOPUS_MARINER_FIELD.YEAR_AFFIL,
		SCOPUS_MARINER_FIELD.YEAR_AUKEY,
	};


	/**
	 * SCOPUS 화면에 출력될 사용자가 설정하는 항목.
	 */
	public static final SCOPUS_MARINER_FIELD[] SEARCH_GROUP_OPTIONS = new SCOPUS_MARINER_FIELD[] {
		SCOPUS_MARINER_FIELD.SORTYEAR,
		SCOPUS_MARINER_FIELD.FIRSTASJC,
		SCOPUS_MARINER_FIELD.AFFILCOUNTRY,
		SCOPUS_MARINER_FIELD.AFID,
		SCOPUS_MARINER_FIELD.SRCID,
		SCOPUS_MARINER_FIELD.SRCTYPE,
		SCOPUS_MARINER_FIELD.AUID,
		SCOPUS_MARINER_FIELD.AUTHKEY,
		SCOPUS_MARINER_FIELD.INDEXTERMS,
		SCOPUS_MARINER_FIELD.DOCTYPE,
		SCOPUS_MARINER_FIELD.DAFFIL,
		SCOPUS_MARINER_FIELD.GT_ACRONYM,
		SCOPUS_MARINER_FIELD.GT_AGENCY
	};
	
	
	/**
	 * SCOPUS 화면에 출력될 SELECT_FIELD 항목.
	 */
	public static final SCOPUS_MARINER_FIELD[] _SELECT_FIELDS = new SCOPUS_MARINER_FIELD[] {
		SCOPUS_MARINER_FIELD.AUTHORNAME,
		SCOPUS_MARINER_FIELD.TITLE,
		SCOPUS_MARINER_FIELD.DOCTYPE,
		SCOPUS_MARINER_FIELD.SORTYEAR,
		SCOPUS_MARINER_FIELD.EID,
		SCOPUS_MARINER_FIELD.CRNAME
	};
	
	
	/**
	 * SCOPUS 화면에 출력될 SELECT_FIELD 항목.
	 */
	public static final SCOPUS_MARINER_FIELD[] _SELECT_FIELD_VIEW_ADV_STATISTICS = new SCOPUS_MARINER_FIELD[] {
		SCOPUS_MARINER_FIELD.EID,
		SCOPUS_MARINER_FIELD.SORTYEAR,
		SCOPUS_MARINER_FIELD.FIRSTASJC,
		SCOPUS_MARINER_FIELD.AUTHORNAME,
		SCOPUS_MARINER_FIELD.DAFFIL,
		SCOPUS_MARINER_FIELD.DAFFILCOUNTRY
	};
	
	/**
	 * SCOPUS 화면에 출력될 ORDER_FIELD 항목.
	 */
	public static final SCOPUS_MARINER_FIELD[] _ORDER_FIELD = new SCOPUS_MARINER_FIELD[] {
		SCOPUS_MARINER_FIELD.SORTYEAR,
		SCOPUS_MARINER_FIELD.PUBYEAR,
		SCOPUS_MARINER_FIELD.CITCOUNT,
		SCOPUS_MARINER_FIELD.REFCOUNT,
	};
	
	/**
	 * SCOPUS 화면에 출력될 FILTER_FIELD 항목.
	 */
	public static final SCOPUS_MARINER_FIELD[] _FILTER_FIELD = new SCOPUS_MARINER_FIELD[] {
//		SCOPUS_MARINER_FIELD.ASJC,
//		SCOPUS_MARINER_FIELD.FIRSTASJC,
//		SCOPUS_MARINER_FIELD.PUBYEAR,
//		SCOPUS_MARINER_FIELD.SORTYEAR,
		SCOPUS_MARINER_FIELD.REFCOUNT,
		SCOPUS_MARINER_FIELD.CITCOUNT,
	};
	
	public static Set<SCOPUS_MARINER_FIELD> _filterSet = new HashSet<SCOPUS_MARINER_FIELD>();
	
	static{
		for(SCOPUS_MARINER_FIELD f : _FILTER_FIELD){
			_filterSet.add(f);
		}
	}
}
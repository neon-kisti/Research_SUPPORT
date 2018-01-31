/**
 * 
 */
package com.diquest.scopus.analysis;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.diquest.scopus.analysis.TestScopusSearch.DOC;

/**
 * @author coreawin
 * @date 2014. 6. 19.
 * @Version 1.0
 */
public class HCPCheck {
	static String[] eids = new String[] { "84887581653", "84861017679",

	};

	private static String builddate = "20160422";
	/**
	 * 
	 * DB 커넥션 가져온다.
	 * 
	 */
	static class ConnectionFactory {

		private static ConnectionFactory instance = null;

		Connection conn = null;

		private static final String URL = "jdbc:oracle:thin:@203.250.196.44:1551:KISTI5";
		private static final String USER = "scopus";
		private static final String PASS = "scopus+11";

		public static synchronized ConnectionFactory getInstance() {
			if (instance == null) {
				instance = new ConnectionFactory();
			}
			return instance;
		}

		private ConnectionFactory() {
		}

		public Connection getConnection() throws Exception {
			try {
				Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
				return conn = DriverManager.getConnection(URL, USER, PASS);
			} catch (Exception ex) {
				System.out.println("Error: " + ex.getMessage());
				throw ex;
			}
		}

		public void release(Connection conn) {
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}

		public void release(PreparedStatement pstmt, Connection conn) {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}

		public void release(ResultSet rs, PreparedStatement pstmt, Connection conn) {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
				}
			}
		}

		public void release(ResultSet rs, PreparedStatement pstmt) {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
				}
			}
		}

	}
	
	
	static {
		try {
			builddate = getHCPBuildInfo();
			System.out.println("HCP Build date " + builddate);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		;
	}

	public static String getHCPBuildInfo() throws SQLException {
		String query = "select distinct regdate from SCOPUS_2017_HCP_VIEW_1 order by regdate desc";
		String result = "20141001";
		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		ConnectionFactory factory = null;
		try {
			factory = ConnectionFactory.getInstance();
			conn = factory.getConnection();
			psmt = conn.prepareStatement(query);
			rs = psmt.executeQuery();
			while (rs.next()) {
				result = rs.getString(1);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new SQLException(e.getMessage());
		} finally {
			if (factory != null)
				factory.release(rs, psmt, conn);
		}
		return result;
	}

	public static void checkHCP() throws SQLException {
		String regdate = builddate;
		String query = "select eid, cit_count, (ranking*100),  ranking from SCOPUS_2017_HCP_MAIN  where eid = ? and regdate = ?";

		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		ConnectionFactory factory = null;
		try {
			factory = ConnectionFactory.getInstance();
			conn = factory.getConnection();
			for (String eid : eids) {
				psmt = conn.prepareStatement(query);
				psmt.setString(1, eid);
				psmt.setString(2, regdate);
				rs = psmt.executeQuery();
				while (rs.next()) {
					boolean isHCP = false;
					String _eid = rs.getString(1);
					int cit_count = rs.getInt(2);
					double _ranking = Double.parseDouble(rs.getString(3));
					if (_ranking < 1) {
						isHCP = true;
					}
					System.out.println(_eid + "\t" + cit_count + "\t" + _ranking + "\t" + isHCP);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new SQLException(e.getMessage());
		} finally {
			if (factory != null)
				factory.release(rs, psmt, conn);
		}
	}

	public static DOC checkHCP(DOC doc) throws SQLException {
		String regdate = builddate;
		System.out.println("HCP BUILD DATE : " + regdate);
		String query = "select eid, cit_count, (ranking*100),  ranking, asjc_code from SCOPUS_2017_HCP_MAIN  where eid = ? and regdate = ?";
		// System.out.println("HCP RegDate " + regdate);

		Connection conn = null;
		PreparedStatement psmt = null;
		ResultSet rs = null;
		ConnectionFactory factory = null;
		try {
			factory = ConnectionFactory.getInstance();
			conn = factory.getConnection();
			psmt = conn.prepareStatement(query);
			psmt.setString(1, doc.eid);
			psmt.setString(2, regdate);
			rs = psmt.executeQuery();
			while (rs.next()) {
				boolean isHCP = false;
				String _eid = rs.getString(1);
				int cit_count = rs.getInt(2);
				double _ranking = Double.parseDouble(rs.getString(3));
				if (_ranking < 1) {
					isHCP = true;
					doc.hcpFirstAsjc = rs.getString(5) + " ";
					doc.isFirstHCP = String.valueOf(isHCP).toUpperCase();
					System.out.println(doc.eid + " : " + doc.hcpFirstAsjc);
				}
				doc.cit_count = String.valueOf(cit_count);
				doc.ranking = String.valueOf(_ranking);
				// System.out.println(_eid + "\t" + cit_count + "\t" + _ranking
				// + "\t" + isHCP);
			}
			
			String isHCP = doc.isFirstHCP.trim();
			if("".equals(isHCP)){
				doc.isFirstHCP = "FALSE";
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new SQLException(e.getMessage());
		} finally {
			if (factory != null)
				factory.release(rs, psmt, conn);
		}
		return doc;
	}

	/**
	 * 특정년도의 특정 분류의 상위 1% 쓰레숄드값을 가져와서 입력한 쓰레숄드값과 비교한다. <br>
	 * 입력한 쓰레숄드 값이 상위 1%쓰레숄드값보다 크면 true를 리턴한다. <br>
	 * 
	 * @author pc
	 * @date 2015. 6. 15.
	 * @param asjc
	 * @param year
	 * @return
	 * @throws SQLException
	 */
	protected static boolean check1ProThreshold(Connection conn, String asjc, String year, String threshold) throws SQLException {
		String regdate = builddate;
		String query = "select threshold from SCOPUS_2017_HCP_VIEW_1 where asjc_code = ? and publication_year = ? and regdate = ?";

		PreparedStatement psmt = null;
		ResultSet rs = null;
		boolean result = false;
		try {
			psmt = conn.prepareStatement(query);
			psmt.setString(1, asjc);
			psmt.setString(2, year);
			psmt.setString(3, regdate);
			rs = psmt.executeQuery();
			while (rs.next()) {
				int _t = rs.getInt(1);
				if (Integer.parseInt(threshold) >= _t) {
					// System.out.println("HCP RegDate " + asjc +"\t" + year);
					result = true;
				}
				break;

			}
		} catch (SQLException e) {
			throw e;
		} finally {
			if (rs != null)
				rs.close();
			if (psmt != null)
				psmt.close();
		}
		return result;
	}

	/**
	 * 논문의 나머지 ASJC 분류에 대한 상위 1% 쓰레숄드 여부를 구한다.
	 * 
	 * @author pc
	 * @date 2015. 6. 15.
	 * @param doc
	 * @return
	 * @throws Exception 
	 */
	public static DOC checkASJC(DOC doc) throws Exception {
		// if ("true".equalsIgnoreCase(doc.isTopJ)) {
		// doc.hcpasjc = doc.asjc.replaceAll(" ", ",");
		// return doc;
		// }
		Connection conn = null;
		ConnectionFactory factory = null;
		try {
			factory = ConnectionFactory.getInstance();
			conn = factory.getConnection();
			String[] asjcs = doc.asjc.split(" ");
			String hasjc = "";
			String firstAsjc = doc.hcpFirstAsjc.trim();
			boolean firstIndex = true;
			for (String asjc : asjcs) {
				if(firstAsjc.equals(asjc.trim())){
					continue;
				}
				boolean is1Pro = check1ProThreshold(conn, asjc, doc.sortyear, doc.cit_count);
				if (is1Pro) {
					if (firstIndex) {
						hasjc = asjc;
						firstIndex = false;
					} else {
						hasjc = hasjc + "," + asjc;
					}
				}
			}
			doc.hcpSubAsjc = hasjc;
		} catch (Exception e) {
			throw e;
		} finally {
			if (conn != null)
				conn.close();
		}
		return doc;
	}

	/**
	 * @author coreawin
	 * @date 2014. 6. 19.
	 * @param args
	 * @throws SQLException
	 */
	public static void main(String[] args) throws SQLException {
		HCPCheck.checkHCP();

	}

}

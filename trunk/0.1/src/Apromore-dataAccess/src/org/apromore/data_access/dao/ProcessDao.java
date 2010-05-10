package org.apromore.data_access.dao;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;

import org.apromore.anf.AnnotationsType;
import org.apromore.cpf.CanonicalProcessType;
import org.apromore.data_access.commons.ConstantDB;
import org.apromore.data_access.exception.ExceptionDao;
import org.apromore.data_access.model_manager.ProcessSummariesType;
import org.apromore.data_access.model_manager.ProcessSummaryType;
import org.apromore.data_access.model_manager.VersionSummaryType;


public class ProcessDao extends BasicDao {

	public ProcessDao() throws Exception {
		super();
		// TODO Auto-generated constructor stub
	}

	private static ProcessDao instance;

	public static ProcessDao getInstance() throws ExceptionDao {
		if (instance == null) {
			try {
				instance = new ProcessDao();
			} catch (Exception e) {
				throw new ExceptionDao(
						"Error: not able to get instance for DAO: " + e.getMessage());
			}
		}
		return instance;
	}

	/**
	 * Get from the database summaries of all processes (IncomingMessages details and IncomingMessages version
	 * details)
	 * @return ProcessSummariesType
	 * @throws Exception
	 */
	public ProcessSummariesType getProcessSummaries() throws Exception {

		ProcessSummariesType processSummaries = new ProcessSummariesType();
		Connection conn = null;
		Statement stmtP = null;
		ResultSet rsP = null;
		String requeteP = null;
		Statement stmtV = null;
		ResultSet rsV = null;
		String requeteV = null;

		try {
			conn = this.getConnection();
			stmtP = conn.createStatement();

			requeteP = "SELECT " + ConstantDB.ATTR_PROCESSID + ", P."+ ConstantDB.ATTR_NAME + ", "
			+            "P." + ConstantDB.ATTR_DOMAIN + ", P." + ConstantDB.ATTR_ORIGINAL_TYPE 
			+           ", R." + ConstantDB.ATTR_RANKING
			+          ", V." + ConstantDB.ATTR_VERSION_NAME
			+ " FROM " + ConstantDB.TABLE_PROCESSES + " natural join " + ConstantDB.VIEW_PROCESS_RANKING + " R "
			+ "      join " + ConstantDB.TABLE_VERSIONS + " V using(" + ConstantDB.ATTR_PROCESSID + ") "
			+ " where (" + ConstantDB.ATTR_PROCESSID + ", V." + ConstantDB.ATTR_CREATION_DATE + ")"
			+ "in (select " + ConstantDB.ATTR_PROCESSID + " , max(" + ConstantDB.ATTR_CREATION_DATE + ") "
			+ "        from " + ConstantDB.TABLE_VERSIONS
			+ "        group by " + ConstantDB.ATTR_PROCESSID + ") "
			+ " order by " + ConstantDB.ATTR_PROCESSID ;

			rsP = stmtP.executeQuery(requeteP);
			while (rsP.next()) {
				int processId = rsP.getInt(1);
				ProcessSummaryType processSummary = new ProcessSummaryType();
				processSummaries.getProcessSummary().add(processSummary);
				processSummary.setId(processId);
				processSummary.setName(rsP.getString(2));
				processSummary.setDomain(rsP.getString(3));
				processSummary.setOriginalNativeType(rsP.getString(4));
				processSummary.setRanking(rsP.getInt(5));
				processSummary.setLastVersion(rsP.getString(6));

				stmtV = conn.createStatement();
				requeteV = " select " + ConstantDB.ATTR_VERSION_NAME + ", "
				+ ConstantDB.ATTR_CREATION_DATE + ",  "
				+ ConstantDB.ATTR_LAST_UPDATE + ",  "
				+ ConstantDB.ATTR_RANKING + " "
				+ " from " + ConstantDB.TABLE_VERSIONS 
				+ " where  " + ConstantDB.ATTR_PROCESSID + " = " + processId 
				+ " order by  " + ConstantDB.ATTR_CREATION_DATE ;
				rsV = stmtV.executeQuery(requeteV);
				while (rsV.next()){
					VersionSummaryType version = new VersionSummaryType();
					version.setName(rsV.getString(1));
					version.setCreationDate(rsV.getTimestamp(2));
					version.setLastUpdate(rsV.getTimestamp(3));
					version.setRanking(rsV.getInt(4));
					processSummary.getVersionSummaries().add(version);
				}
				rsV.close(); stmtV.close();	
			} 
		}
		catch (SQLException e) {
			throw new Exception("Error: ProcessDao " + e.getMessage());
		}
		finally {
			Release(conn, stmtP, rsP);
		}
		return processSummaries;
	}

	/**
	 * returns IncomingMessages summaries which match at one of the keywords 
	 * @param String of the form "a , b ; c ; (d,n)"
	 * @return ProcessSummariesType
	 */
	public ProcessSummariesType getProcessSummaries(String keywordssearch) throws Exception {

		/**
		 * built the search condition 
		 */

		String condition = "";
		if (keywordssearch != null && keywordssearch.compareTo("") != 0) {
			// map search expression into a list of terms
			// example: (yawl;protos),invoicing => [(,yawl,or,protos,),and,invoicing]
			Vector<String> expression = mapQuery(keywordssearch) ;

			condition = "and ( ";
			for (int i = 0; i<expression.size();i++){
				String current = expression.elementAt(i);
				if (current.compareTo(" and ")==0 || current.compareTo(" or ")==0 ||
						current.compareTo(" ) ")==0 || current.compareTo(" ( ")==0) {
					condition += current ;
				} else {
					condition +=  ConstantDB.ATTR_PROCESSID + " in (select " + ConstantDB.ATTR_PROCESSID 
					+    " from " + ConstantDB.VIEW_KEYWORDS 
					+    " where " + ConstantDB.ATTR_WORD + " like '%" + current + "%' )";
				}
			}
			condition += " )";
		} 

		// if keywordssearch empty thus condition = ""

		ProcessSummariesType processSummaries = new ProcessSummariesType();
		Connection conn = null;
		Statement stmtP = null;
		ResultSet rsP = null;
		String requeteP = null;
		Statement stmtV = null;
		ResultSet rsV = null;
		String requeteV = null;

		try {
			conn = this.getConnection();
			stmtP = conn.createStatement();

			requeteP = "SELECT " + ConstantDB.ATTR_PROCESSID + "," 
			+ ConstantDB.ATTR_NAME + ", "
			+             ConstantDB.ATTR_DOMAIN + "," 
			+ ConstantDB.ATTR_ORIGINAL_TYPE + ","
			+     " R." + ConstantDB.ATTR_RANKING  + ","
			+             ConstantDB.ATTR_VERSION_NAME
			+     " FROM " + ConstantDB.TABLE_PROCESSES + " P "
			+	"    join " + ConstantDB.TABLE_VERSIONS + " V using(" + ConstantDB.ATTR_PROCESSID + ") "
			+       "  join " + ConstantDB.VIEW_PROCESS_RANKING + " R using (" + ConstantDB.ATTR_PROCESSID + ")" 
			+   " where "
			+          "  (" + ConstantDB.ATTR_PROCESSID + ", V." + ConstantDB.ATTR_CREATION_DATE + ")"
			+				 "in (select " + ConstantDB.ATTR_PROCESSID + " , max(" + ConstantDB.ATTR_CREATION_DATE + ") "
			+			 "        from " + ConstantDB.TABLE_VERSIONS
			+			 "        group by " + ConstantDB.ATTR_PROCESSID + ") "
			+ condition 
			+ " order by " + ConstantDB.ATTR_PROCESSID ;

			rsP = stmtP.executeQuery(requeteP);
			while (rsP.next()) {
				int processId = rsP.getInt(1);
				ProcessSummaryType processSummary = new ProcessSummaryType();
				processSummaries.getProcessSummary().add(processSummary);
				processSummary.setId(processId);
				processSummary.setName(rsP.getString(2));
				processSummary.setDomain(rsP.getString(3));
				processSummary.setOriginalNativeType(rsP.getString(4));
				processSummary.setRanking(rsP.getInt(5));
				processSummary.setLastVersion(rsP.getString(6));

				stmtV = conn.createStatement();
				requeteV = " select " + ConstantDB.ATTR_VERSION_NAME + ", "
				+ ConstantDB.ATTR_CREATION_DATE + ",  "
				+ ConstantDB.ATTR_LAST_UPDATE + ",  "
				+ ConstantDB.ATTR_RANKING + " "
				+ " from " + ConstantDB.TABLE_VERSIONS 
				+ " where  " + ConstantDB.ATTR_PROCESSID + " = " + processId 
				+ " order by  " + ConstantDB.ATTR_CREATION_DATE ;

				rsV = stmtV.executeQuery(requeteV);
				while (rsV.next()){
					VersionSummaryType version = new VersionSummaryType();
					version.setName(rsV.getString(1));
					version.setCreationDate(rsV.getTimestamp(2));
					version.setLastUpdate(rsV.getTimestamp(3));
					version.setRanking(rsV.getInt(4));
					processSummary.getVersionSummaries().add(version);
				}
				rsV.close(); stmtV.close();	
			} 
		}
		catch (SQLException e) {
			throw new Exception("Error: ProcessDao " + e.getMessage());
		}
		finally {
			Release(conn, stmtP, rsP);
		}
		return processSummaries;
	}

	/**
	 * Interpretation of the query received by customer
	 * "," => and
	 * ";" => or
	 * "(" and ")" remain
	 * each term of the query is an element in the result
	 * a,b;(d,e) => [a, and, b, or, (, a, and, e, )]
	 * @param  String keywordssearch 
	 * @return Vector<String> : the SQL condition corresponding to keywordssearch
	 * @throws UnsupportedEncodingException 
	 */
	private Vector<String> mapQuery(String keywordssearch) throws UnsupportedEncodingException {
		Vector<String> res = new Vector<String>();
		String term = "" ;
		int state = 1;	// initial state in the recognition automaton
		String currentChar = "" ;
		for (int i=0; i<keywordssearch.length();i++) {
			currentChar = keywordssearch.substring(i, i+1);
			if (state==1) {
				if (currentChar.compareTo(",")==0) {
					// and
					res.add(" and ");
				} else {
					if (currentChar.compareTo(";")==0) {
						// or
						res.add(" or ");
					} else {
						if (currentChar.compareTo(")") ==0) {
							res.add(" ) ");
						} else {
							if (currentChar.compareTo("(") ==0) {
								res.add(" ( ");
							} else {
								if (currentChar.compareTo(" ") != 0) {
									// not an operator, not a space
									term = currentChar;
									state = 2;
								}	}	}	} }
			} else {
				if (state ==2) {
					if (currentChar.compareTo(",")==0) {
						// and
						res.add(term);
						res.add(" and ");
						state = 1;
					} else {
						if (currentChar.compareTo(";")==0) {
							// or
							res.add(term);
							res.add(" or ");
							state = 1;
						} else {
							if (currentChar.compareTo(")") ==0) {
								res.add(term);
								res.add(" ) ");
								state = 1;
							} else {
								if (currentChar.compareTo("(") ==0) {
									res.add(term);
									res.add(" ( ");
									state = 1;
								} else {
									if (currentChar.compareTo(" ") != 0) {
										// not an operator, not a space
										term += currentChar;
									}	else {
										state = 3;
									}	}	} } }
				} else {
					// state = 3
					if (currentChar.compareTo(",")==0) {
						// and
						res.add(term);
						res.add(" and ");
						state = 1;
					} else {
						if (currentChar.compareTo(";")==0) {
							// or
							res.add(term);
							res.add(" or ");
							state = 1;
						} else {
							if (currentChar.compareTo(")") ==0) {
								res.add(term);
								res.add(" ) ");
								state = 1;
							} else {
								if (currentChar.compareTo("(") ==0) {
									res.add(term);
									res.add(" ( ");
									state = 1;
								} else {
									if (currentChar.compareTo(" ") != 0) {
										// not an operator, not a space
										term += " " + currentChar;
										state = 2;
									}	}	} }
					}
				}
			}
		}
		if (state == 2 || state == 3) res.add(term);
		return res;
	}

	public void storeNativeCpf (String username, String processName, String domain, 
			String nativeType, String version, InputStream process_xml,
			CanonicalProcessType cpf, AnnotationsType anf) throws ExceptionDao, SQLException, IOException {

		
		
		Connection conn = null;
		Statement stmt0 = null;
		PreparedStatement
		stmt1 = null,
		stmt2 = null,
		stmt3 = null,
		stmt5 = null,
		stmt6 = null;
		ResultSet rs0 = null;
		try {
			if (version == null || version.compareTo("")==0) {
				version = "v0";
			}
			StringBuilder sb0 = new StringBuilder();
			String line ;
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(process_xml, "UTF-8"));
				while ((line = reader.readLine()) != null) {
					sb0.append(line).append("\n");
				}
			} finally {
				process_xml.close();
			}
			String process_string = sb0.toString();
			
			JAXBContext jcpf = JAXBContext.newInstance("org.apromore.cpf");
			Marshaller m = jcpf.createMarshaller();
			m.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			JAXBElement<CanonicalProcessType> rootcpf = new org.apromore.cpf.ObjectFactory().createCanonicalProcess(cpf);
			ByteArrayOutputStream cpf_xml = new ByteArrayOutputStream();
			m.marshal(rootcpf, cpf_xml);
			InputStream cpf_xml_is = new ByteArrayInputStream(cpf_xml.toByteArray());

			StringBuilder sb = new StringBuilder();

			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(cpf_xml_is, "UTF-8"));
				while ((line = reader.readLine()) != null) {
					sb.append(line).append("\n");
				}
			} finally {
				cpf_xml_is.close();
			}

			String cpf_string = sb.toString();
			
			JAXBContext janf = JAXBContext.newInstance("org.apromore.anf");
			m = jcpf.createMarshaller();
			m.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			JAXBElement<AnnotationsType> rootanf = new org.apromore.anf.ObjectFactory().createAnnotations(anf);
			ByteArrayOutputStream anf_xml = new ByteArrayOutputStream();
			m.marshal(rootcpf, anf_xml);
			InputStream anf_xml_is = new ByteArrayInputStream(anf_xml.toByteArray());
			
			StringBuilder sb1 = new StringBuilder();

			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(anf_xml_is, "UTF-8"));
				while ((line = reader.readLine()) != null) {
					sb.append(line).append("\n");
				}
			} finally {
				anf_xml_is.close();
			}

			String anf_string = sb1.toString();
			
			conn = this.getConnection();

			String query0 = " select " + ConstantDB.ATTR_USERID
			+ " from " + ConstantDB.TABLE_USERS
			+ " where " + ConstantDB.ATTR_USERNAME + " = '" + username + "'";
			stmt0 = conn.createStatement();
			rs0 = stmt0.executeQuery(query0);
			if (!rs0.next()) {
				throw new ExceptionDao ("User not found");
			}
			Integer userId = rs0.getInt(1);
			
			String query3 = " insert into " + ConstantDB.TABLE_CANONICALS
			+ "(" + ConstantDB.ATTR_CONTENT + ")"
			+ " values (?) ";

			stmt3 = conn.prepareStatement(query3, Statement.RETURN_GENERATED_KEYS);
			//stmt3.setAsciiStream(1, cpf_xml_is);
			stmt3.setString(1, cpf_string);

			int rs3 = stmt3.executeUpdate();
			ResultSet keys = stmt3.getGeneratedKeys() ;
			if (!keys.next()) {
				throw new ExceptionDao ("Error: cannot retrieve generated key.");
			}
			int cpfId = keys.getInt(1);
			keys.close();
			String query5 = " insert into " + ConstantDB.TABLE_ANNOTATION
			+ "(" + ConstantDB.ATTR_CONTENT + ")"
			+ " values (?) ";
			stmt5 = conn.prepareStatement(query5, Statement.RETURN_GENERATED_KEYS);

			//stmt5.setAsciiStream(1, anf_xml_is);
			stmt5.setString (1, anf_string);
			Integer rs5 = stmt5.executeUpdate();
			keys = stmt5.getGeneratedKeys() ;
			if (!keys.next()) {
				throw new ExceptionDao ("Error: cannot retrieve generated key.");
			}
			int anfId = keys.getInt(1);
			keys.close();

			String query6 = " insert into " + ConstantDB.TABLE_NATIVES
			+ "(" + ConstantDB.ATTR_CONTENT + ","
			+       ConstantDB.ATTR_NAT_TYPE + ","
			+       ConstantDB.ATTR_CANONICAL + ","
			+       ConstantDB.ATTR_ANNOTATION + ")"
			+ " values (?,?,?,?) ";
			stmt6 = conn.prepareStatement(query6, Statement.RETURN_GENERATED_KEYS);
			//stmt6.setAsciiStream(1, process_xml);
			stmt6.setString(1, process_string);
			stmt6.setString(2, nativeType);
			stmt6.setInt(3, cpfId);
			stmt6.setInt(4, anfId);
			Integer rs6 = stmt6.executeUpdate();
			keys = stmt6.getGeneratedKeys() ;
			if (!keys.next()) {
				throw new ExceptionDao ("Error: cannot retrieve generated key.");
			}
			int natId = keys.getInt(1);
			keys.close();

			String query1 = " insert into " + ConstantDB.TABLE_PROCESSES
			+ "(" + ConstantDB.ATTR_NAME + ","
			+       ConstantDB.ATTR_DOMAIN + ","
			+		ConstantDB.ATTR_OWNER + ","
			+		ConstantDB.ATTR_ORIGINAL_TYPE + ")"
			+ " values (?, ?, ?, ?) ";
			stmt1 = conn.prepareStatement(query1, Statement.RETURN_GENERATED_KEYS);
			stmt1.setString(1, processName);
			stmt1.setString(2, domain);
			stmt1.setInt(3, userId);
			stmt1.setString(4,nativeType);
			int rs1 = stmt1.executeUpdate();
			keys = stmt1.getGeneratedKeys() ;
			if (!keys.next()) {
				throw new ExceptionDao ("Error: cannot retrieve generated key.");
			}
			int processId = keys.getInt(1);
			keys.close();

			String query2 = " insert into " + ConstantDB.TABLE_VERSIONS
			+ "(" + ConstantDB.ATTR_PROCESSID + ","
			+     ConstantDB.ATTR_VERSION_NAME + ","
			+     ConstantDB.ATTR_CREATION_DATE + ","
			+     ConstantDB.ATTR_LAST_UPDATE + ","
			+     ConstantDB.ATTR_CANONICAL + ")"
			+ " values (?, ?, now(), now(), ?) ";
			stmt2 = conn.prepareStatement(query2);
			stmt2.setInt(1, processId);
			stmt2.setString(2, version);
			stmt2.setInt(3, cpfId);
			Integer rs2 = stmt2.executeUpdate();

			conn.commit();

		} catch (SQLException e) {
			e.printStackTrace();
			conn.rollback();
			throw new ExceptionDao ("SQL error ProcessDAO (storeNative): " + e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			conn.rollback();
			throw new ExceptionDao ("Error ProcessDAO (storeNative): " + e.getMessage());
		} finally {
			Release(conn, stmt0, rs0);
		}
	}

}

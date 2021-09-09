package common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

// connection ��ü�� �޼��� non-static�ε�, �̸� static���� ������ ���� �� transaction ó�� ����
// Resultset close �޼���, Statment close �޼��� �� non-static �̱⿡ static���� ����� �־����
// static ���� ���ǿ� ������ �־�� �ϹǷ� �� static�̾�� �Ѵ�.

// DB Connection�� ���� �ް� ��ȯ�ϴ� ���� �⺻���� ��ɵ��� ��Ƶ� Util Ŭ����

public class JDBCTemplate {

	// 1. Connection
	public static Connection getConnection() {
		// ���� ��ü�� return ���־�� ���Ŀ� ���� �� �ִ�.
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 2. ���� ��Ų ����̹��� �̿��ؼ� ����Ŭ�� �����Ѵ�.
		// getConnection(String url, Properties info)
		Properties pro = new Properties();
		pro.put("user", "scott");
		pro.put("password", "TIGER");
		Connection conn=null;
		try {
			conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe", pro);
			conn.setAutoCommit(false);//���� Ŀ�� ����
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;//Ŀ�ؼ� ��ü ��ȯ
	}
	//1-2. DB�� Connect �Ǿ����� ������ ����
	public static boolean isConnected(Connection conn) {
		boolean validConnection= true;
		try {
			if(conn== null || conn.isClosed()){
				validConnection=false;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return validConnection;
	}

	// 2. Close _Connection
	public static void Close(Connection conn) {
		try {
			if(conn!=null && !conn.isClosed())
			{
				conn.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// 3. Close_Statement
	public static void Close(Statement stmt) {
		if(stmt!=null){
			try {
				stmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	// 4. Close.ResultSet
	public static void Close(ResultSet rs) {
		if(rs!=null){
			try {
				rs.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	// 5. RollBack()
	public static void RollBack(Connection conn) {
		if(conn!=null){
			try {
				conn.rollback();
				System.out.println("[JDBCTemplate.Rollback ]: DB Successfully Rollbacked");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	// 6. Commit()
	public static void Commit(Connection conn) {
		if(conn!=null) {
			try {
				conn.commit();
				System.out.println("[JDBCTemplate.commit ]: DB Successfully Committed");
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
}

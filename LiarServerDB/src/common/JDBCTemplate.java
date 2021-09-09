package common;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

// connection 객체의 메서드 non-static인데, 이를 static으로 만들어야 여러 명 transaction 처리 가능
// Resultset close 메서드, Statment close 메서드 다 non-static 이기에 static으로 만들어 주어야함
// static 값을 세션에 가지고 있어야 하므로 꼭 static이어야 한다.

// DB Connection을 리턴 받고 반환하는 등의 기본적인 기능들을 모아둔 Util 클래스

public class JDBCTemplate {

	// 1. Connection
	public static Connection getConnection() {
		// 연결 객체는 return 해주어야 추후에 끊을 수 있다.
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 2. 참조 시킨 드라이버를 이용해서 오라클에 연결한다.
		// getConnection(String url, Properties info)
		Properties pro = new Properties();
		pro.put("user", "scott");
		pro.put("password", "TIGER");
		Connection conn=null;
		try {
			conn = DriverManager.getConnection("jdbc:oracle:thin:@127.0.0.1:1521:xe", pro);
			conn.setAutoCommit(false);//오토 커밋 방지
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return conn;//커넥션 객체 반환
	}
	//1-2. DB와 Connect 되었는지 유무를 리턴
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

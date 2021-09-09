package com.dao;

import com.vo.*;
import static common.JDBCTemplate.*;
import java.sql.*;
import java.util.*;

public class MyLiarDaoImple implements MyLiarDao{

	private Connection conn;
	public MyLiarDaoImple(Connection conn) {
		this.conn=conn;//Ŀ�ؼ� ������ Dao����
	}
	public void create_Liar_Table() {
		
		PreparedStatement stmt=null;
		try {
			stmt=conn.prepareStatement(create_Liar_Table);
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Close(stmt);
	}
	public void drop_Liar_Table() {
		PreparedStatement stmt=null;
		try {
			stmt=conn.prepareStatement(drop_Liar_Table);
			stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		Close(stmt);
	}
	public List<MyLiarVo> select_all_liar(){
		List<MyLiarVo> all= new ArrayList<>();
		MyLiarVo vo=null;
		Statement stmt=null;
		ResultSet rs=null;
		try {
			stmt=conn.createStatement();
			rs=stmt.executeQuery(select_all_liar);
			while(rs.next()) {
				vo=new MyLiarVo(rs.getString(1),rs.getString(2));
				all.add(vo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			Close(rs);
			Close(stmt);
		}
		return all;
	}
	public int my_Liar_insert(MyLiarVo vo) {
		int r=0;
		PreparedStatement pstm=null;
		try {
			pstm=conn.prepareStatement(my_Liar_insert);
			pstm.setString(1, vo.getUserID());
			pstm.setString(2, vo.getPassword());
			
			r=pstm.executeUpdate();
			if(r>0) {
				Commit(conn);//���������� commit
			}//���� �߻� �� catch�� �Ѿ
		} catch (SQLException e) {
			RollBack(conn);
			e.printStackTrace();
		}finally {
			Close(pstm);
		}
		return r;
	}
	public int my_Liar_delete(MyLiarVo vo) {
		int r=0;
		PreparedStatement pstm=null;
		try {
			pstm=conn.prepareStatement(my_Liar_delete);
			pstm.setString(1, vo.getUserID());
			
			r=pstm.executeUpdate();
			if(r>0) {
				Commit(conn);//���������� commit
			}//���� �߻� �� catch�� �Ѿ
		} catch (SQLException e) {
			RollBack(conn);
		}finally {
			Close(pstm);
		}//Ŀ�ؼ� �ݴ� ���� biz����
		
		return r;
	}
	public List<MyLiarVo> my_Liar_find(MyLiarVo vo){
		List<MyLiarVo> all= new ArrayList<>();
		PreparedStatement stmt=null;
		ResultSet rs=null;
		try {
			stmt=conn.prepareStatement(my_Liar_find);
			stmt.setString(1, vo.getUserID());
			rs=stmt.executeQuery();
			while(rs.next()) {
				vo=new MyLiarVo(rs.getString(1),rs.getString(2));
				all.add(vo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			Close(rs);
			Close(stmt);
		}
		return all;
	}
}

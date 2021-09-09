package com.biz;

import com.vo.MyLiarVo;
import com.dao.*;
import static common.JDBCTemplate.*;

import java.sql.Connection;
import java.util.*;
public class MyLiarBiz {

	public void create_Liar_Table(){
		Connection conn=getConnection();
		new MyLiarDaoImple(conn).create_Liar_Table();
		Close(conn);
	}
	public void drop_Liar_Table() {
		Connection conn=getConnection();
		new MyLiarDaoImple(conn).drop_Liar_Table();
		Close(conn);
	}
	public List<MyLiarVo> select_all_liar() {
		Connection con= getConnection();
		List<MyLiarVo> all=new MyLiarDaoImple(con).select_all_liar();
		Close(con);//무조건 닫아줄것 매번 열고 닫고
		return all;
	}
	public int my_Liar_insert(MyLiarVo vo) {
		Connection con= getConnection();
		int r=new MyLiarDaoImple(con).my_Liar_insert(vo);
		Close(con);
		return r;
	}
	public int my_Liar_delete(MyLiarVo vo) {
		Connection con= getConnection();
		int r=new MyLiarDaoImple(con).my_Liar_delete(vo);
		Close(con);
		return r;
	}
	public List<MyLiarVo> my_Liar_find(MyLiarVo vo) {
		Connection con= getConnection();
		List<MyLiarVo> all=new MyLiarDaoImple(con).my_Liar_find(vo);
		Close(con);//무조건 닫아줄것 매번 열고 닫고
		return all;
	}
}

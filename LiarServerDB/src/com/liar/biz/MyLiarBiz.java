package com.liar.biz;

import com.liar.dao.*;
import com.liar.vo.MyLiarVo;

import static common.JDBCTemplate.*;

import java.sql.Connection;
import java.util.*;
public class MyLiarBiz {

	public static int theSize;
	public static String message;
	private static MyLiarBiz biz=new MyLiarBiz();
	
	// DB에 유저 추가하는 logic
	public static void addUser(String id, String pw) {
		MyLiarVo vo=new MyLiarVo(id,pw);
		int r=biz.my_Liar_insert(vo);
		List<MyLiarVo> list=biz.select_all_liar();// 유저의 수와 새로 추가된 유저를 포함한 모든 유저들을 다른 이들에게도 알려야 하니
		int count = 0;
		String str = "";
		for(MyLiarVo li:list) {
			count++;
			String key = li.getUserID();
			str = str + key + "_";
		}
		theSize = count;
		message = str;
	}

	public static void deleteUser(String nickName) {
		MyLiarVo vo=new MyLiarVo(nickName,null);
		int r=biz.my_Liar_delete(vo);
	}

	public boolean findByUserId(String nickName) {
		MyLiarVo vo=new MyLiarVo(nickName,null);
		List<MyLiarVo> list=biz.my_Liar_find(vo);
		if(list.size()==0)
			return true;
		else
			return false;
		// 없는 경우 true, 있는 경우 false 준다.
	}
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

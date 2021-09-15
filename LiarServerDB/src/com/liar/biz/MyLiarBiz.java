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
	
	// DB�� ���� �߰��ϴ� logic
	public static void addUser(String id, String pw) {
		MyLiarVo vo=new MyLiarVo(id,pw);
		int r=biz.my_Liar_insert(vo);
		List<MyLiarVo> list=biz.select_all_liar();// ������ ���� ���� �߰��� ������ ������ ��� �������� �ٸ� �̵鿡�Ե� �˷��� �ϴ�
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
		// ���� ��� true, �ִ� ��� false �ش�.
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
		Close(con);//������ �ݾ��ٰ� �Ź� ���� �ݰ�
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
		Close(con);//������ �ݾ��ٰ� �Ź� ���� �ݰ�
		return all;
	}
}

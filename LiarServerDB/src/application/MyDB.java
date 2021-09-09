package application;

import static common.JDBCTemplate.*;
import com.biz.*;
import com.vo.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MyDB {

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

}

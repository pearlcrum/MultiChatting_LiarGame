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

}

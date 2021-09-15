package com.liar.dao;

public interface MyLiarDao {

	public static final String create_Liar_Table="create table My_Liar (user_ID varchar2(20) primary key ,user_Password varchar2(20) not null)";
	public static final String drop_Liar_Table="drop table My_liar";
	public static final String select_all_liar="select * from My_Liar";
	public static final String my_Liar_insert="insert into My_Liar values (?,?)";
	public static final String my_Liar_delete="delete from My_Liar where user_ID=?";
	public static final String my_Liar_find="select * from My_Liar where user_ID=?";
}

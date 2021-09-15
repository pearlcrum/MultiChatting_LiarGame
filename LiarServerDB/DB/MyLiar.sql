
create table My_Liar (user_ID varchar2(20) primary key ,user_Password varchar2(20) not null);

drop table My_Liar;

select * from My_Liar;

insert into My_Liar values (?,?);

delete from My_Liar where user_ID=?;

select * from My_Liar where user_ID=?;
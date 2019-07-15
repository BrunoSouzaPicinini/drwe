create table user_details
(
	user_id int auto_increment primary key,
	user_name varchar(255) null,
	first_name varchar(50) null,
	last_name varchar(50) null,
	gender varchar(10) null,
	password varchar(50) null,
	status tinyint(10) null
)


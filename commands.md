# 功能演示





#### register, list and quit 测试

```
#注册四个用户
java Client localhost 8000 register admin admin
quit
java Client localhost 8000 register ck ck
quit
java Client localhost 8000 register hzf hzf
quit
java Client localhost 8000 register user3 user3

list

quit

#注册错误
java Client localhost 8000 register admin 123456
```

#### login 测试

```
#密码错误
java Client localhost 8000 login admin abc
#用户错误
java Client localhost 8000 login adm admin

#登录成功
java Client localhost 8000 login admin admin
```

#### list and help 测试

```
list
help
```

#### add 功能测试

```
add hzf 2000-11-11T04:00 2000-11-11T06:00 ttt
list
add ck 2000-11-11T09:00 2000-11-11T11:00 cknb
list

#用户不存在
add abc 2000-11-11T04:00 2000-11-11T06:00 ttt

#自己添加自己
add admin 2000-11-11T09:00 2000-11-11T11:00 cknb

#时间格式错误
add hzf 2000-13-11T04:00 2000-11-11T06:00 ttt
add hzf 2000-11-32T04:00 2000-11-11T06:00 ttt
add hzf 2000-11-11T25:00 2000-11-11T06:00 ttt
add hzf 2000-11-11T04:61 2000-11-11T06:00 ttt

#开始时间晚于结束时间
add hzf 2000-11-11T08:00 2000-11-11T06:00 ttt

#用户没有空闲时间
list
add hzf 2000-11-11T05:00 2000-11-11T07:00 ttt
add ck 2000-11-11T06:00 2000-11-11T10:00 cknb
```

#### user4 login and add new meeting

```
新建一个客户端，注册使用新的用户user4登录

java Client localhost 8000 register user4 user4
list

#新增会议错误
add hzf 2000-11-11T05:00 2000-11-11T07:00 ttt
add admin 2000-11-11T05:00 2000-11-11T07:00 ttt

#新增hzf的会议
list
add hzf 2000-11-11T07:00 2000-11-11T09:00 ttt
list

#新增错误
add admin 2000-11-11T07:00 2000-11-11T10:00 ttt
add admin 2000-11-11T04:00 2000-11-11T06:00 ttt

#新增admin的会议
list
add admin 2000-11-11T13:00 2000-11-11T15:00 ttt
list

#新增相同时间的会议
list
add user3 2000-11-11T13:00 2000-11-11T15:00 ttt
list
```

#### query 测试

```
list
query 2000-11-11T02:00 2000-11-11T06:00

#query 会根据startTime排序
list
query 2000-11-11T01:00 2000-11-11T23:00

#query 不存在的会议
query 2000-11-11T16:00 2000-11-11T18:00
```

#### delete 测试

```
list

#delete 
delete <某个被user4创建的会议id>
list

#delete 错误
delete <某个不存在的id或某个不是user4创建的会议id>
list
```

### clear 清除该用户创建的所有会议

```
clear
list


# 开一个新的client，登录hzf，hzf没有创建任何会议，所以clear无效
java Client localhost 8000 login hzf hzf
list
clear
list


# 返回第一个client，让admin 使用clear指令
《切换回第一个client》
list
clear
list
quit

# 把其他两个clinet也quit
切回第二个client
quit
切回第三个client
quit
```

#### 清空数据库数据，避免与下一次测试冲突。

```
必须先关闭RMIServer进程，不然无法删除数据库数据

1.关闭RMIServer

2.打开sql连接

-- 检查会议是否清空
SELECT * FROM meeting;
-- 检查用户是否清空
SELECT * FROM user;

-- 删除所有用户
DELETE FROM user;
-- 检查用户是否清空
SELECT * FROM user;
```


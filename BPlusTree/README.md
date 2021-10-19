使用该数据库,首先应在Analyzer的refresh方法中进行表的创建
具体操作为
1.创建表信息数组:colMsg
2.指定索引列数组(目前最多支持两个联合索引)
3.在tableNames.txt中注册表名
4.在main 函数中执行refresh方法
注:refresh方法中有Person表示例



                Sql语句:
查询语句:与MySQL查询语句语法一致,注意每两个字段之间严格保持一个空格,前后勿留空格
插入语句:与MySql插入语句语法差异:values()中的插入字段之间用空格隔开,而不是","
删除语句:与MySQL删除语句语法一致

注:where后仅支持一级 or 和 and
如 where id = 1 and name = wmj
为最长支持


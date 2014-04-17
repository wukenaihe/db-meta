db-meta
=======

  Db-meta is a free database schema discovery and easily tool. Db-meta now can support mysql, sql server and oracle. It is easy to extend. Db-meta's output meta is object, so it is easy to use in your project. And our library is thread safety.

Example Usage
=======
You can download the demo and run it by yourself.[db-meta-demo](https://github.com/wukenaihe/db-meta-example)

Architecture
<h3>Meta data architecture</h3>

![metaclass](https://raw.githubusercontent.com/wukenaihe/db-meta/master/db-meta/src/main/resources/metaclass.jpg "metaclass")

<ul>
 <li><b>Schema:</b>According to the [sql-92](http://www.contrib.andrew.cmu.edu/~shadow/sql/sql1992.txt),Catalogs are named collections of schemas in an SQL-environment. Just like "database->catalogs->schema->table", but Oracle only support schema, Mysql only support catalogs, Sql server support all of them. So <b>Schema</b> here is "catalog.schema".</li>
 <li><b>Constraint:</b>Our Constraint only contain unique and check(Primary and Foreign are not in is)</li>
</ul>

<h3>soft Architecture</h3>

![frame](https://raw.githubusercontent.com/wukenaihe/db-meta/master/db-meta/src/main/resources/frame.jpg "frame")


<h3>API</h3>
  In order to improve performance, avoid to crawle useless meta ,we use schemalevel.
  
						              |min   | standard | max |
						--------------|------|----------|-----|
						JdbcDriverInfo| Yes  | Yes      | Yes |
						DatabaseInfo  | Yes  | Yes      | Yes |
						Table         | Yes  | Yes      | Yes |
						Column        | Yes  | Yes      | Yes |
						PrimaryKey    | Yes  | Yes      | Yes |
						Constraint    | Yes  | Yes      | Yes |
						View          | NO   | NO       | Yes |
						Index         | NO   | Yes      | Yes |
						ForeignKey    | NO   | Yes      | Yes |
						Privilege     | NO   | NO       | Yes |
						Trigger       | NO   | NO       | Yes |	


  MetaLoader interface is what you need. 
  
Method                                                      |Description
------------------------------------------------------------|-----------------------------------------------------------
Set<String> getTableNames()                                 |Get table names（current Schema）
Table getTable(String tableName)	                          |Get table（SchemaInfolevel.standard）
Table getTable(String tableName,SchemaInfoLevel schemaLevel)|Get table
Table getTable(String tableName,SchemaInfo schemaInfo)	    | 
Set<SchemaInfo> getSchemaInfos()	                          |Get current schema information
Schema getSchema()	                                        |Get current schema
Schema getSchema(SchemaInfo schemaInfo)	                    |Get schema ,according to the SchemaInfo
Set<String> getProcedureNames()	                            |Get the user's procedure names
Procedure getProcedure(String procedureName)	              |Get the procedure information,according to the name
Map<String,Procedure> getProcedures()	                      |Get the user's procedures
Set<String> getTriggerNames()	                              |Get the user's trigger names
Trigger getTrigger(String triggerName)	                    |Get the trigger information, according to the trigger name
Map<String, Trigger> getTriggers()	                        |Get the user's triggers
Set<String> getFunctionNames()	                            |Get the user's function names
Function getFunction(String name)	                          |Get the function information, according to the  name
Map<String, Function> getFunctions()	                      |Get the user's functions
Database getDatabase()	                                    |Get all the meta data of the database(Standard)
Database getDatabase(SchemaInfoLevel level)	                |Get all the meta data of the database
  


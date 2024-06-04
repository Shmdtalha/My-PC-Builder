My PC Builder

Softwares and Versions used:
SpringBoot 3.0.5
SpringBoot security: 6
Java Development Kit: 17
MS SQL Configuration Manager: 18
SQL Server :19
Visual Studio Code
Visual Studio Code extensions: Language Support for Java(TM) by Red Hat  v1.18.0

Note: This project has been tested on multiple computers with the following steps, therefore they should work. If you meet any difficulties, kindly email at l215775@lhr.nu.edu.pk.

You have to follow these steps to run our project

1) First of All you have to make a User Account in SQL Server Management Studio.
   For this go to theSecurity section, right Click on Login then Click on New Login.
   Make new user with Login Name "sms" And set Password "123456". It is neccessary to have this name and password.
   Then select SQL Server Authentication, go to Server Roles, check "sysadmin" And click on ok.
   At last connect SSMS with this login and password.

Important note:
a) Make sure that TCP/IP ports are enabled in Network Configuration in Microsoft SQL Configuration Management. (Refer: https://help.dugeo.com/m/Insight/l/438913-troubleshooting-enabling-tcp-ip-in-the-sql-server)

b) Make sure that SQL authentication login is allowed by right clicking on Server ->Properties -> Security. (Refer: https://www.dundas.com/support/learning/documentation/installation/how-to-enable-sql-server-authentication)



2) Use the backup 'mpcb_backup.bak' file to get data for the database. To do this, right click on Databases, press 'Restore Database...' and locate the file on your device to add it.

3) Open our project and write your server name in application.properties file to connect it to database. For example:

spring.datasource.url = jdbc:sqlserver://thefirst\\sqlexpress;databaseName=mpcb_db;encrypt=true;trustServerCertificate=true;

Here "thefirst\\sqlexpress" is our server name. You must replace it with your server name. (Note: the file reads '\\' as '\')

4) Now run project and write http://localhost:8080/ on your web browser.



5) Access:
Admins have access to all functionalities: Login with Username: 1@1.com, Password: 1
Customers have access to shopping and checking out functionalities: Login with Username: 3@3.com, Password: 3
Anonymous users have access to shopping functionalities: No need to login for that.

Management Portal can be accessed through the header or via http://localhost:8080/manager


Thank you!  
# MyPCBuilder
 SDA Project

Getting a laptop or a desktop is a very hectic process, especially in third world countries where used PCs have a higher demand than new ones. There are problems like: 

●	Researching about different computer components and comparing them in a single platform. Hard to choose between prices, specs, speeds etc.
●	Looking for those components. Some components are not available. Some are not upto the mark and are falsely advertised.

The PC parts catalog software will be a software-based application that is easy to navigate and use.  
It will simplify the component selection process, ensure compatibility, and provide users with a hassle-free buying experience.
It will include a wide range of PC components, including CPUs, GPUs, RAM, motherboards, storage devices, and power supplies, from different manufacturers. 
The software will allow users to select and configure their PC build, and it will ensure that all components are compatible with each other.
One of the primary features of the software will be the cost estimator, which will calculate the total cost of the selected components, including taxes and shipping costs. 
The cost estimator will also allow users to compare prices of different components.


List of 5 to 10 Main Features:

1.	A software that allows users to select pc components to build PCs
2.	Comparing different components; price, model, specs, colours
3.	Compatibility checking (Check the compatibility of selected components to avoid any issues during the assembly process)
4.	Other details displayed as well (warranties, Taxes?shipping fees?)
5.	RL links to direct people to component sites when they need more information
6.	Utility software links for each component ( Eg. Nvidia GeForce)
7.	Component recommendations: Suggest alternative components based on the user's budget or performance requirements
8.	Build sharing (Enable users to save their build configurations and share them with others for feedback)
9.	Cost estimator, which will calculate the total cost of the selected components, including taxes and shipping costs

#Configurations
My PC Builder

Softwares and Versions used:
SpringBoot 3.0.5
SpringBoot security: 6
Java Development Kit: 17
MS SQL Configuration Manager: 18
SQL Server :19
Visual Studio Code
Visual Studio Code extensions: Language Support for Java(TM) by Red Hat  v1.18.0

Note: This project has been tested on multiple computers with the following steps, therefore they should work. If you meet any difficulties, kindly email at shmdtalah@gmail.com.

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



# Login Access:
Admins have access to all functionalities: Login with Username: 1@1.com, Password: 1
Customers have access to shopping and checking out functionalities: Login with Username: 3@3.com, Password: 3
Anonymous users have access to shopping functionalities: No need to login for that.

Management Portal can be accessed through the header or via http://localhost:8080/manager


Thank you!  
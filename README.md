# Final Year Project

***

## TO RUN THE SYSTEM FROM THE START

1. Download and install XAMPP (take note of the installation folder) - https://www.apachefriends.org/download.html
2. Start the Apache and MySQL modules
3. Open your browser and type in localhost/phpmyadmin
4. On the left-hand side click new and create a database with a name "conferences" and a collation of utf8_general_ci
5. Select the created database and click Import
6. Choose the research_events.sql file from the code folder and click Go
 * All the tables should have been imported
7. Go to the XAMPP installation folder -> htdocs and add the codeigniter folder from the code folder
8. Run the Main.java located in the main package.
9. The percentage completion will start displaying in the console. 
 * Note that the speed of crawling depends on a website connection speed, so it may take longer at times.
10. After completion or during the program run go to http://localhost/codeigniter/index.php/conferences
11. Click any conference from the list to see the details or type in an acronym to find a conference

***

## TO CHECK THE WEBSITE WITHOUT RUNNING THE SYSTEM

1. Download and install XAMPP (take note of the installation folder) - https://www.apachefriends.org/download.html
2. Start the Apache and MySQL modules
3. Open your browser and type in localhost/phpmyadmin
4. On the left-hand side click new and create a database with a name "conferences" and a collation of utf8_general_ci
5. Select the created database and click Import
6. Choose the completed_research_events.sql file from the code folder and click Go
 * All the tables should have been imported
7. Go to the XAMPP installation folder -> htdocs and add the codeigniter folder from the code folder
8. Type in http://localhost/codeigniter/index.php/conferences into your browser and the conferences should display

***

## NOTES

* If you want to run tests, the Apache and MySQL modules need to be turned on because the system uses a database.
* www.icsoft.org may have long loading times, hence, the program won't extract anything (you may consider removing it from the configuration in the database)
* To change the configuration in the database, run the Configuration.java class and follow the instructions or use the phpmyadmin page

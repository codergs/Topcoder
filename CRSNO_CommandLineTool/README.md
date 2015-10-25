=================================================== <br/>
Programmer          : Codergs                       <br/>
Last Updated        : 13th September 2015           <br/>
Tool Name           : JAVA CRSNO Command Line Tool  <br/>
=================================================== <br/>

================================================== <br/>
 PROJECT INSTRUCTIONS AND GUIDELINES FROM TOPCODER <br/>
================================================== <br/>

CRSNO AFM tool is a Java command line tool that helps soybean breeders to count the CRSNO's trials and report by year and stage.

1. Ask user to supply a template file which contains all the CRSNO's that we wish to count. Column "MAT:GNA:CRSNO" has the CRSNO. Column headers are always in row 3. Refer to template.xlsx for example. There will be tens of thousands of rows.  Also ask user for an output filename.

2. Ask user to supply a folder which contains multiple experiment data files.  Process each file (print info to console which file is being processed).  Only look at the first tab of each file.  These files can have tens of thousands of rows as well.

2a. The files will have year at the beginning of the filename, like "2013 Experiment Entries NA CRSNO AFM tool.xlsx".

2b. We will look at 3 columns "EEXP:MAT:GNA:CRSNO" or "CRSNO", "EEXP:CPIFL" or "CPIFL" and "EEXP:EXP:STGCD" or "STGCD".  If any of these columns are missing, print an error to console and skip the file.  Column headers are always in row 3.

2c. Look at each row, if CRSNO does not appear in the template, do not count it.  If CPIFL is 1 or True, do not count it.  (Only count 0, False or empty CPIFL).

2d. For STGCD, possible values are 2, 2.5, 3, 4, 5, 6 or empty.  Count 2.5 as 2.  Count 5 and 6 in the same group called "5/6".

2e. After all the files are processed, we should have counts of entries for 5 stage codes 2, 3, 4, 5/6, NA for each year for each CRSNO.

3. Copy everything from template file into output file, and append some columns for the counts. Refer to output.xlsx as an example. Do not include a specific year/stage combination if there are no counts for it.  Order the columns properly.


=================<br/>
FOLDER STRUCTURE <br/>
=================<br/>


CRSNO_Tool/ <br/>
├── build   <br/>
│   ├── classes <br/>
│   │   └── com <br/>
│   │       └── topcoder <br/>
│   │           └── CRSNOCounter <br/>
│   │               ├── CRSNOTemplateReader.class <br/>
│   │               ├── Util.class <br/>
│   │               ├── Utilities.class <br/>
│   │               └── dataProcessingPerCRSNO.class <br/>
│   └── dist <br/>
│       └── CRSNOJavaTool.jar <br/>
├── build.xml <br/>
├── conf <br/>
│   ├── template1.xlsx <br/>
│   └── template2.xlsx <br/>
├── doc <br/>
│   └── README.txt <br/>
├── lib <br/>
│   ├── log4j-1.2.17.jar <br/>
│   ├── poi-3.11-20141221.jar <br/>
│   ├── poi-ooxml-3.11-20141221.jar <br/>
│   ├── poi-ooxml-schemas-3.11-20141221.jar <br/>
│   └── xmlbeans-2.6.0.jar <br/>
├── log <br/>
├── log4j.properties <br/>
├── out.xlsx <br/>
├── output <br/>
├── run.sh <br/>
├── src <br/>
│   └── main <br/>
│       └── java <br/>  
│           └── com <br/>
│               └── topcoder <br/>
│                   └── CRSNOCounter <br/>
│                       ├── CRSNOTemplateReader.java <br/>
│                       ├── Util.java <br/>
│                       ├── Utilities.java <br/>
│                       └── dataProcessingPerCRSNO.java <br/>
└── test_files <br/>
    ├── test1 <br/>
    │   ├── 2000 expfile.xlsx <br/>
    │   └── 2001 expfile.xlsx <br/>
    └── test2 <br/>
        ├── 2003 expfile.xlsx <br/>
        ├── 2004 expfile.xlsx <br/>
        └── 2005 expfile.xlsx <br/>


==================== <br/>
TOOL OVERVIEW        <br/>
==================== <br/>

1) The tool takes 3 arguments 
        1.1) Template file path with name (For e.g, conf/template.xlsx, where conf is a folder in the above shown hierarchy)
        1.2) Output File Name (For e.g, 2013OutputReport.xlsx )
        1.3) Experimental file path or directory (For e.g, test_files/testX, where X denotes some number. It is not a standard path,                                              and user can rovide any custom path)

2) The tool processess data, and created three level Map with CRSNO name as key of first-level Map, Year as key of second-level Map, and STGCD and CPIFL as keys in the thrid-level Map.

3) After Map is fully created, the changes are written to an output excel file, whose name is provided by the user as a second argument to the tool

4) The tool makes use of log4j for logging. The Log Level is set to INFO to start with. Users can change it as required. Logger uses a rotating appender which logs the files in maximum size of 10 MB across 8 instances before it starts rotating again

***************************** <br/>
VERY IMPORTANT NOTE: <br/>

All the constraints and settings described above in the "PROJECT INSTRUCTIONS AND GUIDELINES FROM TOPCODER" were striclty adhered. The Sample Template.xlsx file provided with the project was having the CRSNO tab on sheet 2. 

├── src <br/>
│   └── main <br/>
│       └── java <br/>
│           └── com <br/>
│               └── topcoder <br/>
│                   └── CRSNOCounter <br/>
│                       ├── Utilities.java <br/>

Under Utilities.java, the value of  TEMPLATETABSELECT = 2 tells us on which sheet is CRSNO tab. 

In the sample template.xlsx the CRSNO tab is on sheet 2. 

For the sake of DEMO tests, the CRSNO Tab on Sample Template.xlsx was shifted to 0 from 2 by setting  TEMPLATETABSELECT = 0 

In case if the user wants to run the test with the provided Samplte Template.xlsx, then please make  TEMPLATETABSELECT = 2
under Utiities.java file under the above shown hierarchy.

******************************

=================
INSTALLATION NOTE
=================

It is noted that the program need more than the default amount of memory. So in order to
provide the required -Xmx parameter, the script parent-counting.sh (for Unix-line systems)
and parent-counting.cmd (for Windows systems) has been provided. Note that the script
assumes the jar file is in the 'dist' directory. Use of the script in an installation
may need to update the 'dist' directory reference.

-Xms512m -Xmx2048m

====
DEMO
====


First provide executable permissions to run.sh by issuing command 

chmod +x run.sh

(sudo priviliges may be required according to the folder location where run.sh is downloaded along with the project files)



There are two test scenarios alreadey set up. In test 1, template1.xlsx is used to run with experimental files in folder test_files/test1. In test 2, template2.xlsx is used to run with experimental files in folder test_files/test2. 

The experimental files in test1 and test2 are custom made, and represents some of the possible ways in which the experimental files can be made. Interesting thing to note is that the headers row can be any row in first three. The CRSNO tool can adapat to changes. Also, one empty row in between CSNO names, is also handled by this tool. Please go through test files to have a feel on how to prepare the experimental files to use with this tool. 

===== <br/>
Test1 =====> Template1.xlsx is the Sample Template given along with the above instructions <br/>
===== <br/>

Run the run.sh script from to of this project directory.

Unix-like systems:

    ./run.sh


When prompted enter each of the three following input responses:

    conf/template1.xlsx             ("This is the path where template1.xlsx file resides or should reside")
    output1.xlsx                    ("This is the name of the output report file name. By default, the code will generate all the 
                                      output files under output folder this file under output folder")
    test_files/test1                ("This is the folder path for placing all the experimental files to run against the template2.xlsx)

The utility will run for a while then produce the following files:
- output1.xlsx under "output" folder for experimental files under "test_files/test1"


===== <br/>
Test2 =====> Template2.xlsx is a custom made Template with >70,000 rows.  <br/>
===== <br/>

Run the run.sh script from to of this project directory.

Unix-like systems:

    ./run.sh

When prompted enter each of the three following input responses:

    conf/template2.xlsx             ("This is the path where template2.xlsx file resides or should reside")
    output2.xlsx                    ("This is the name of the output report file name. By default, the code will generate all the 
                                      output files under output folder")
    test_files/test2                ("This is the folder path for placing all the experimental files to run against the template2.xlsx)

The utility will run for a while then produce the following files:
- output2.xlsx under "output folder" for experimental files under "test_files/test2"


========== <br/>
Custom Run <br/>
========== <br/>

1) Place the custom template.xlsx under folder conf/ . Also make sure that this template file matches the template1.xlsx (which is the same as the sample template file provided with this assignment on topcoder)

2) Optional: Create a folder name "CustomFolder" under output folder

3) Place all the experimental files under a new folder name "CustomRun" inside the test_files folder 

4) Set TEMPLATETABSELECT = 2 under Utilities.java from value 1

5) Run the run.sh script from to of this project directory.

Unix-like systems:

    ./run.sh

When prompted enter each of the three following input responses:

    conf/template.xlsx          ("This is the path where template.xlsx file resides or should reside")
    output.xlsx                 ("This is the name of the output report file name. By default, the code will generate all the output 
                                  files under output folder. If CustomFolder is given, then output file would be available under 
                                  output/CustomFolder/ path")
    test_files/CustomRun        ("This is the folder path for placing all the experimental files to run against the template.xlsx. 
                                   provided by the user)

The utility will run for a while then produce the following files:
- output.xlsx under "output/CustomFolder" or "output" folder for experimental files under "test_files/CustomRun"



//=====================================================//
//Programmer			: Codergs                      //
//Last Updated		    : 13th September 2015          //
//Tool Name 			: JAVA CRSNO Command Line Tool //
//=====================================================//


//**********************************************************************************************//
//							Entry Point: CRSNO Stage Counter per Year  		    				//
//**********************************************************************************************//


package com.topcoder.CRSNOCounter;

// for I/O
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;


import org.apache.log4j.Level;
// for logging
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


// for exception handling
import java.io.IOException;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public class Util {

	// creating a logger reference to Util.class
	static final Logger logger = Logger.getLogger(Util.class);
	
	// represent prompt message for crsno template file
	private static final String CRSNO_TEMPLATE_FILE_PROMPT_MESSAGE = "Please enter the filename of the template file:";
	
	// represent prompt message for output report file
	private static final String OUTPUT_REPORT_FILE_PROMPT_MESSAGE = "Please enter the filename of the output file:";
	
	// represent prompt message for experimental files directory path
	private static final String TEST_FILES_FOLDER_PROMPT_MESSAGE = "Please enter the folder path to the experient files:";
	
	public static void main(String[] args){
		// TODO Auto-generated method stub
		
		// Setting up log4j.porperties file
		PropertyConfigurator.configure("log4j.properties");
		logger.info("log4j.properties set");
		
		try {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

			logger.debug("Before reading the crsno Template file name");
			
			// read the CRSNO template file
			String crsnoTemplateFileName = null;
			while (crsnoTemplateFileName == null) {
				logger.info(CRSNO_TEMPLATE_FILE_PROMPT_MESSAGE);
				crsnoTemplateFileName = br.readLine();
				if (crsnoTemplateFileName != null
						&& crsnoTemplateFileName.trim().length() > 0 
						&& (crsnoTemplateFileName.toLowerCase().endsWith(".xls") || crsnoTemplateFileName.toLowerCase().endsWith(".xlsx"))) {
					crsnoTemplateFileName = crsnoTemplateFileName.trim();
				}
				else{
					logger.info("Invalid CRSNO template file name");
					throw new IOException("Invalid CRSNO template file name");
				}	
			} // while loop ends here
			
			logger.info("crsno template file name received successfully");

			logger.debug("Before reading file name for output report");
			
			// read the filename for output report
			String outputReportFileName = null;
			while (outputReportFileName == null) {
				logger.info(OUTPUT_REPORT_FILE_PROMPT_MESSAGE);
				outputReportFileName = br.readLine();
				if (outputReportFileName != null && outputReportFileName.trim().length() > 0
						&& (outputReportFileName.toLowerCase().endsWith(".xls") || outputReportFileName.toLowerCase().endsWith(".xlsx"))) {
					outputReportFileName = ("output/"+outputReportFileName).trim();
				}
				else{
					logger.debug("Invalid output report file name");
					throw new IOException("Invalid output report file name");
				}
			} // while loop ends here 

			logger.info("Output file name received successfully");

			logger.debug("Before reading directory path name for test files");
			
			// Get the directory name for the experimental files
			String testFileDir = null;
			while (testFileDir == null) {
				logger.info(TEST_FILES_FOLDER_PROMPT_MESSAGE);
				testFileDir = br.readLine();
				File outputReportDir = new File(testFileDir.trim());		
				if (testFileDir != null && testFileDir.trim().length() > 0 && outputReportDir.getAbsoluteFile().exists() && outputReportDir.getAbsoluteFile().isDirectory()) {
					testFileDir = testFileDir.trim();
				}
				else {
					logger.debug("Invalid directory path provided for experimental files");
					throw new FileNotFoundException("Invalid directory path provided for experimental files");
				}
			} // while loop ends here	
			
			logger.info("Directory path name received successfully");
			
			logger.debug("Before creating an object of class CRSNOTemplateReader");
			// creating an object of class CRSNOTemplateReader
			CRSNOTemplateReader crsnoTemplate= new CRSNOTemplateReader();
		
			// calling excelReader function to create map with CRSNO (from template file) as keys 
			logger.debug("Before calling function excelReader");
			crsnoTemplate.excelTemplateReader(crsnoTemplateFileName);//crsnoTemplateFileName);
			
			logger.debug("Before copying template file into the output report file");
			// copying template CRSNO tab into output report file
			Utilities.copyTemplateToOutputReportFile(crsnoTemplateFileName, outputReportFileName);
			
		
			logger.debug("Before reading the experimental files");
			
			// creating an object of class dataProcessingPerCRSNO
			dataProcessingPerCRSNO dao = new dataProcessingPerCRSNO();
			
			logger.info("Calling experimentalFilesReader function");
			
			dao.experimentalFilesReader(testFileDir, crsnoTemplate);
			
			logger.info("calling prepareYearColumnTreeSet function");
			
			//dao.OutputReportExcelWriter(outputReportFileName);
			dao.prepareYearColumnTreeSet();
			
			logger.info("calling excel writer function");
			dao.OutputReportExcelWriter(outputReportFileName);
			
			if(logger.getParent().getLevel() == Level.DEBUG){
				logger.debug("Print the CRSNO Map");
				crsnoTemplate.displayMap();
			}
			
		}// try block ends here
		catch (IOException e1) {
			e1.printStackTrace();
			System.exit(0);
		} catch (InvalidFormatException e2) {
			e2.printStackTrace();
			System.exit(0);
			
		}
		
	}// main ends here
}

//=====================================================//
//Programmer			: Codergs                      //
//Last Updated		    : 13th September 2015          //
//Tool Name 			: JAVA CRSNO Command Line Tool //
//=====================================================//


//**********************************************************************************************//
//								Common utility functions	    								//
//**********************************************************************************************//

package com.topcoder.CRSNOCounter;

// for I/O
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;



// for excel operation
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

public class Utilities {
	
	//  variable storing the row number at which the header names are listed
	public static final int SKIPROW = 3;
	
	// variable storing the spreadsheet tab to be read on template file and test files
	public static final int TEMPLATETABSELECT = 0;
	
	// variable storing the spreadsheet tab to be read on output file 
	public static final int EXPANDOUTPUTTABSELECT = 0;
	
	// variable storing the column to look for CRSNO
	public static final int COLUMN = 0;
	
	// function to open 
	public static Workbook retrunWorkbookReference (String fileName) throws IOException{
		
		Logger logger = Logger.getLogger("com.topcoder.CSRNOCounter.Utilities");
		
		// create Workbook instance holding reference to xlsx/xls file
		Workbook workbook = null;
		
		try{
			logger.debug("Opening an file input stram on file name");
			// creating FileInoutStream on crsnoTemplateFile 
			FileInputStream file = new FileInputStream(new File(fileName));
						  
			logger.debug("Checking the xlsx/xls xtension");
			
			if(fileName.toLowerCase().endsWith(".xlsx"))
				workbook = new XSSFWorkbook(file);
			else 
				if(fileName.toLowerCase().endsWith(".xls"))
					workbook = new HSSFWorkbook(file);
			logger.debug("Closing the file reference");
			
			// closing input file stream
			file.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.exit(0);	
		}
		
		// returning workbook reference
		return workbook;
	}// function ends here 
	

	// function to copy template file to output report file
	public static void copyTemplateToOutputReportFile(String templateFileName, String outputReportFileName) throws IOException, InvalidFormatException{
		
		try{
			
			Logger logger = Logger.getLogger("com.topcoder.CSRNOCounter.Utilities");
			// creating FileInoutStream on 
			FileInputStream file = new FileInputStream(templateFileName);
			logger.debug("Opening workbook reference on the template.xlsx file");
		    Workbook wb = WorkbookFactory.create(file);
		    
		    logger.debug("Before removing unwanted sheet from template file to write to outpt report file");
		    // remove all sheets expect the one name CRSNO
		    for(int i=wb.getNumberOfSheets()-1;i>=0;i--){
	            Sheet sheet =wb.getSheetAt(i);
	            if(!sheet.getSheetName().equals("CRSNO"))
	                wb.removeSheetAt(i);
	          }
		    logger.info("All sheets except CRSNO are deleted");
		    logger.debug("Before writing the output report file");
		    // Write the output to a file
		    FileOutputStream fileOut = new FileOutputStream(outputReportFileName);
		    
		    logger.info("Created "+outputReportFileName+" out of "+templateFileName);
		    
		    // writing to the output report file
		    wb.write(fileOut);
		    logger.info("Copy pasted template file to Output report file");
		    
		    // closing workbook and file instance
		    wb.close();
		    fileOut.close();
		}catch (IOException e1){
			e1.printStackTrace();
			System.exit(0);
		}
		catch (InvalidFormatException e2){
			e2.printStackTrace();
			System.exit(0);
		}
	} // function copyTemplateToOutputReportFile ends here
	
	// function to check if the test file names have the appropriate year name
	public static boolean isParsable(String input){
	    boolean parsable = true;
	    try{
	        Integer.parseInt(input);
	    }catch(NumberFormatException e){
	        parsable = false;
	    }
	    return parsable;
	} // function isParsable ends here
	
	
	public static Workbook writeWorkbookToFileName(String outputReportFileName, Workbook wb) throws IOException{
		FileOutputStream fileOut = new FileOutputStream(outputReportFileName);
		// writing to the output report file
		Utilities.displayMessage("Writing the workbook to "+outputReportFileName);
	    wb.write(fileOut);
	    
	    // closing Workbook and fileOut reference
	    fileOut.close();
	    return wb;
	}
	
	// function to print message
	public static void displayMessage(Object msg){
		System.out.println();
		System.out.println("****************************************************************************************************************");
		System.out.println("\t\t\t "+ msg);
		System.out.println("****************************************************************************************************************");
		System.out.println();
	} // function displayMessage ends here
} // Utilities Class ends here



//=====================================================//
//Programmer			: Codergs                      //
//Last Updated		    : 13th September 2015          //
//Tool Name 			: JAVA CRSNO Command Line Tool //
//=====================================================//


//**********************************************************************************************//
//							Reading CRSNO Template Excel and put into a three level Map		    //
//**********************************************************************************************//

package com.topcoder.CRSNOCounter;

// for I/O
import java.io.IOException;

// for collection framework data structures
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

// for logging
import org.apache.log4j.Logger;

// for excel operation
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;


public class CRSNOTemplateReader{
	
	// creating a logger reference to CRSNOTemplateReader.class
	private final Logger logger = Logger.getLogger(this.getClass());
	
	// CRSNO three level Map containing CPILF, year and STGCD values 
	private Map<String, HashMap<Integer, HashMap<String, String>>> crsnoMap = new HashMap<String,HashMap<Integer,HashMap<String, String>>>();

	// function to return reference to CRSNO Map
	public Map<String, HashMap<Integer, HashMap<String,String>>> getCrsnoMap(){
		return crsnoMap;
	}
	
	// Function to return an CRSNO Map content as ArrayList
	public ArrayList<ArrayList<String>> returnAsArrayOfArrayList(){
		
		// represents the innermost Map to store STGCD per stage 
		HashMap<String, String>stageMap;
				
		// represents the second level Map using years as the key and stageMap as value
		Map<Integer,HashMap<String, String>> yearMap;
		
		// data structure to capture row content  
		ArrayList<ArrayList<String>> group = new ArrayList<ArrayList<String>>();
		
		// sorted keys on CRSNO Map
		SortedSet<String> crsnoMapKeys = new TreeSet<String>(crsnoMap.keySet());
				
		for (String key1 : crsnoMapKeys){ //crsnoMap.keySet()){
			
			// inner level ArrayList
			ArrayList<String> rowList = new ArrayList<String>();
			
			// add CRSNo name to the ArrayList
			rowList.add(key1);
			yearMap = crsnoMap.get(key1);
					
			// sorted keys on year Map 
			SortedSet<Integer> yearMapKeys = new TreeSet<Integer>(yearMap.keySet());
					
			for(Integer key2 : yearMapKeys){
				// add year into the ArrayList
				rowList.add(String.valueOf(key2));
				stageMap = yearMap.get(key2);
				rowList.add(stageMap.get("CPILF"));
				rowList.add(stageMap.get("STGCD"));					
			} // for loop ends here	
			
			// add row to the group
			group.add(rowList);
			
		} // out 
		return group;
	}
	
	// function to print CRSNO Map 
	public void displayMap(){
		
		ArrayList<ArrayList<String>> group=returnAsArrayOfArrayList();
		ArrayList<String> row;
			
		// printing the ArrayList
		for (int i = 0; i < crsnoMap.size(); i++) {
			row = group.get(i);
			for (int j=0; j<row.size();j++){
				System.out.print(row.get(j) +"\t");
		}
		System.out.println();
		} // for loop ends here
		System.out.println();
		System.out.println("****************************************************************************************************************");
		System.out.println();
		System.out.println("****************************************************************************************************************");
	} // function displayMap ends here 
	
	
	// function to read CRSNOs form Template file into a Map 
	public void excelTemplateReader(String templateFileName)  throws IOException {
		
		try{				logger.debug("Opening a Workbook reference on template file name ");
				// get a workbook reference
				Workbook workbook = Utilities.retrunWorkbookReference(templateFileName);
				
				logger.info("Opened Workbook reference on "+templateFileName);
				
				logger.debug("Before getting header row from the template file");
		        // get the CRSNO tab sheet from the workbook
				Sheet spreadsheet = workbook.getSheetAt(Utilities.TEMPLATETABSELECT);
				
				logger.info("Reading all CRSNOs from CRSNO Template file");
				Row row = spreadsheet.getRow(Utilities.SKIPROW-1);
				Cell column = row.getCell(Utilities.COLUMN);
				
				int rowCounter = 0;

				logger.debug("Checking if row 3 column 0 has name MAT:GNA:CRSNO");
				
				// check if row 3 and column 0 has header MAT:GNA:CRSNO
	        	if(column.getCellType() == Cell.CELL_TYPE_STRING){
	        			if(column.getStringCellValue().equals("MAT:GNA:CRSNO")){
	        				
	        				// for each row, iterate over first column       		   
							Iterator<Row> rowIterator = spreadsheet.iterator();
	        		        
							// variable to check if there is an empty row by chance in Template file
							int flag; 
	        		        
							while(rowIterator.hasNext()){
	        		        	flag = 0;
	        		        	++rowCounter;	
	        		        	row = rowIterator.next();
	        		        	
	        		        	try{
	        		        		if(row.getCell(0).getCellType() == Cell.CELL_TYPE_BLANK){
	        		        			logger.debug("Entered a blank cell");
	        		        			if(rowIterator.hasNext()){
	        		        				logger.debug("Entere empty row check and has next line");
		        		        			logger.warn("Possible empty row in the template file");	
		        		        			rowCounter--;
		        		        			flag = 1;
		        		        		}			
	        		        		}
	        		        	}catch (Exception e){
	        		        		if(rowCounter < Utilities.SKIPROW){
	        		        			logger.warn("Possible null value in first column of the first three rows. Handling condition!!");
	        		        			flag = 1;
	        		        		}
	        		        		else {
	        		        			logger.debug("Last row line encountered");
	        		        			break;
	        		        		}	
	        		        	} // catch block ends here
	        		        	
	        		        	if(rowCounter > Utilities.SKIPROW && flag == 0){
	        		        		logger.debug("Entered main add key step");
		        	        		column = row.getCell(0);
		        	        		column.setCellType(Cell.CELL_TYPE_STRING);
		        		        	if(column.getCellType() == Cell.CELL_TYPE_STRING){
		        		        		crsnoMap.put(column.getStringCellValue(),new HashMap<Integer, HashMap<String, String>>());	
		        		        		logger.info("New key "+column.getStringCellValue()+" added to the Map");
		        		        	}  		    
	        		        	}
	        		        } // row iterator loop ends here
	        			} // if loop ends here
		        		else
		        			throw new IOException("Column header MAT:GNA:CRSNO not found. Terminating the run!!");
        		}
	        	else 
	        		throw new IOException("Column header MAT:GNA:CRSNO not found. Terminating the run!!");
				
	        	logger.info((rowCounter-Utilities.SKIPROW-1) + " CRSNOs read into the Map");
	        	logger.info("CRSNO Map sucessfully created");
		        workbook.close();
		} // try block ends here
		catch (IOException e1) {
			e1.printStackTrace();
			System.exit(0);
		}
	} // CRSNOTemplateReader function ends here
	
} // class ends here

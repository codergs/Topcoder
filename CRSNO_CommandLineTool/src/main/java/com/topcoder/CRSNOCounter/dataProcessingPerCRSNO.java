//=====================================================//
//Programmer			: Codergs	                   //
//Last Updated		    : 13th September 2015          //
//Tool Name 			: JAVA CRSNO Command Line Tool //
//=====================================================//


//**********************************************************************************************//
//			Reading CRSNO Experimental files  and updating into three level Map		    		//
//**********************************************************************************************//

package com.topcoder.CRSNOCounter;

//for I/O
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

//for collection framework data structures
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;




//for logging
import org.apache.log4j.Logger;

//for excel operation
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;



public class dataProcessingPerCRSNO {

	// represents the experimental processed Flag. To be set when there is a wrong file provided under test_files directory 
	private int processedFlag;
	
	// represents the column numbers in a row
	private int columnIndex[];
	
	// creating a logger reference to dataProcessingPerCRSNO.class
	private final Logger logger = Logger.getLogger(this.getClass());
	
	// represents the year for the experimental file read 
	private int year;
	
	// represents the number of columns in the template file
	private int columnCounter;
	
	// represents the union of all years for all CRSNO 
	private TreeSet<String> additionalYearColumnNames;
	
	// represents the innermost Map to store STGCD per stage 
	private HashMap<String, String>stageMap;
	
	// represents the second level Map using years as the key and stageMap as value
	private Map<Integer,HashMap<String, String>> yearMap;
	
	// represents the outermost Map using CRSNO name as mentioned in template file as keys and yearMap as value
	private Map<String, HashMap<Integer, HashMap<String, String>>> crsnoMap;
	
	// represents the column name that are to be considered during processing experimental files
	private String[] columnNames = {"EEXP:MAT:GNA:CRSNO","CRSNO","EEXP:CPIFL","CPIFL","EEXP:EXP:STGCD","STGCD"};
	
	// function to check if the experimental file has all the three column names present or not
	public boolean validyCheck(Row row, String fileName){
		
		int validCount = 3;
		int validityFlag = 0;
		columnCounter=0;
		
		columnIndex = new int[validCount];
		
		logger.info("Validity check of filename: "+fileName+ "for columns "
    			+ "EEXP:MAT:GNA:CRSNO or CRSNO, EEXP:CPIFL or CPIFL and EEXP:EXP:STGCD or STGCD ");
	
		// iterating over row to find all the column names and match with the above 6 possibilities
		Iterator<Cell> cellIterator = row.cellIterator();
        
        while (cellIterator.hasNext()) 
        {
            Cell column = cellIterator.next();
            // setting column type to String 
            column.setCellType(Cell.CELL_TYPE_STRING);
            logger.debug("Before validity check if loop");
            
            logger.debug("Adding "+column.getStringCellValue()+" to the columnName ArrayList");
            
            if(column.getStringCellValue().equals(columnNames[0]) || column.getStringCellValue().equals(columnNames[1])){
            	columnIndex[0] = columnCounter;
            	validityFlag ++;
            		
            }
            else if(column.getStringCellValue().equals(columnNames[2]) || column.getStringCellValue().equals(columnNames[3])){
            	columnIndex[1] = columnCounter;
            	validityFlag ++;
            }
            else if(column.getStringCellValue().equals(columnNames[4]) || column.getStringCellValue().equals(columnNames[5])){
            	columnIndex[2] = columnCounter;
            	validityFlag ++;
            		
            } // if loop ends here
            columnCounter++;  	 	
        } // while loop ends here
       
        // checking if all three column names are present 
        if(validityFlag == validCount){
        	return true;
        }
        else 
        	return false;
	}// validyCheck function ends here
	
	// function to get STGCD value from stageMap using a key 
	public String getSTGCDValue(String range) throws IOException{
		if(range.equals("N/A"))
			return "N/A";
		
		switch((int)Double.parseDouble(range)){
		
		case 2:	
		case 3:	
		case 4: return String.valueOf((int)Double.parseDouble(range)); 
		case 5:
		case 6: return "5/6" ;
		default: throw new IOException("Invalid STGCD value sent to the funciton from the CRSNO Map. Check the experimental file for invalid value under STGCD column");
		} // switch-case ends here 
	} // function getStageKey ends here
	
	// function to update values into CRSNO Map using the experimental files
	public void experimentalFilesReader(String experimentalFilesDirectoryPath, CRSNOTemplateReader obj) throws InvalidFormatException{
	
		logger.debug("before getting csrnoMap reference");
		crsnoMap = obj.getCrsnoMap();
		logger.info("CRSNO Map retrieved");
		
		try {
			logger.debug("Before creating file array pointing the exp files");
			// list experiment folder files and create file array to reference the files under it
			File folder = new File(experimentalFilesDirectoryPath);
			File[] expFiles = folder.listFiles();
			
			logger.debug("Before iterating over File array");
			//iterating over file array one by one
			for (File file : expFiles) {
				logger.debug("Checking if Filename is present in the File Array");
				if (file.isFile()) {
					logger.debug("File: "+file.getName()+" exists");
					logger.debug("Before checking the name of the "+file.getName()+" for the first occurance of \" \"");
					if(file.getName().indexOf(" ") != -1 && Utilities.isParsable(file.getName().substring(0,file.getName().indexOf(" ")))){  
						processedFlag = 0;
					    // parsing year form the file name
						year = Integer.parseInt(file.getName().substring(0,file.getName().indexOf(" ")));	
						
						Utilities.displayMessage("Trying to process Experimental File "+file.getName());		    		
						    
						logger.info("Reading file "+file.getName()+ " to check compatibility");
							    				
						// counter for rows
						int rowCounter = 0;
											  
						logger.debug("Checking the xlsx/xls xtension of "+file.getName());
								
						Workbook workbook = null;
						
						logger.debug("Opening an file input stream on file name: "+file.getName());
						
						logger.debug("Before creating a Workbook reference on "+file.getName());
						if(!experimentalFilesDirectoryPath.endsWith("/"))
							workbook = Utilities.retrunWorkbookReference(experimentalFilesDirectoryPath+"/"+file.getName());
						else 
							workbook = Utilities.retrunWorkbookReference(experimentalFilesDirectoryPath+file.getName());
						
						logger.debug("Before getting first tab on the excel file" +file.getName());
						// get the first tab sheet from the workbook
						Sheet spreadsheet = workbook.getSheetAt(Utilities.EXPANDOUTPUTTABSELECT);
								
						logger.debug("Before validy check function call for "+file.getName());
						// getting 3rd row from the excel file
								 
						if(validyCheck(spreadsheet.getRow(Utilities.SKIPROW-1),file.getName())){
							logger.info("Validy check pased for "+file.getName()+ ". All three column names are present.");
									
							// for each row, iterate over first column       		   
							Iterator<Row> rowIterator = spreadsheet.iterator();
									
							Row row;
							Cell column;		
							
							logger.info("Populating values for year, CPIFL and STGCD in CRSNO Map row by row");
			        		while(rowIterator.hasNext()){	
			        			 rowCounter++;	
				        		 row = rowIterator.next();
				        		 try{
					        			if(row.getCell(columnIndex[0])!= null ){
					        				column = row.getCell(columnIndex[0]);
					        		        column.setCellType(Cell.CELL_TYPE_STRING);
					        				// if loop in case if rows before header row are empty 
						        			if(row.getCell(columnIndex[0]).getStringCellValue().equals(columnNames[0])
						        					|| row.getCell(columnIndex[0]).getStringCellValue().equals(columnNames[1])){
						        				logger.debug("Matched CRSNO Name before skipping all three rows.");
						        				rowCounter = Utilities.SKIPROW;
						        			}
						        			
						        			// if loop coming into picture after skipping first three rows
						        			if(rowCounter > Utilities.SKIPROW){
						        				
						        				if(row.getCell(columnIndex[0]).getCellType() != Cell.CELL_TYPE_BLANK){
							        				//column = row.getCell(columnIndex[0]);
							        		        //column.setCellType(Cell.CELL_TYPE_STRING);
							        		        		
							        		        logger.debug("Before checking for CRSNO name existing in template file or not");
							        		        // check if CRSNO name is present in the template Map 
							        		        if(crsnoMap.containsKey(column.getStringCellValue())){
							        		        	processedFlag = 1;
							        		        	logger.debug("CSRNO name "+column.getStringCellValue()+" is present in CRSNO Template Map");
							        		        	logger.debug("Before getting the CPILF value");
									        	        column = row.getCell(columnIndex[1]);
									        	        logger.debug("Check if CPILF value is empty or not");
									        	        		
									        	        // Checking if CPIFL value per CRSNO name is empty or not 
									        	        if(column != null && column.getCellType()!=Cell.CELL_TYPE_BLANK){
									        	        	logger.debug("CRSNO value for "+row.getCell(columnIndex[0])+" is not empty");	
										        	        column.setCellType(Cell.CELL_TYPE_STRING);
										        	        logger.debug("Before checking if CPILF value matches the criteria or not");
										        	        		
										        	        // check CPILF column for valid values 
										        	        if(column.getStringCellValue().trim().equalsIgnoreCase("False") || column.getStringCellValue().trim().equals("0")){
										        	        		logger.info("Processing record "+row.getCell(columnIndex[0])+" on filename "+file.getName()+" into the CRSNO Map");
										        	      
										        	        		// retrieving second level yearMap
									        		        		yearMap = crsnoMap.get(row.getCell(columnIndex[0]).getStringCellValue());
									        		        		yearMap.put(year, new HashMap<String, String>());
									        		        			
									        		        		// retrieving third level stageMap
									        		        		stageMap = yearMap.get(year);
									        		        			
									        		        		// Populating data in stageMap
									        		        		stageMap.put("CPILF",column.getStringCellValue());
									        		        			
									        		        		// retrieving STGCD value
									        		        		column = row.getCell(columnIndex[2]);
									        		        		if(column == null){//column.getCellType() == Cell.CELL_TYPE_BLANK){
									        		        			logger.debug("Empty STGCD value found. Valid as per instructions!");
									        		        			stageMap.put("STGCD"," ");
									        		        		}
									        		        		else
									        		        		{
									        		        			logger.debug("Non-empty STGCD value found");
									        		        			column.setCellType(Cell.CELL_TYPE_STRING);
									        		        			stageMap.put("STGCD",getSTGCDValue(column.getStringCellValue()));
									        		        		}
										        	        			
										        	        } // if loop to check for valid CPILF values ends here
										        	        else
										        	        		logger.info("CPILF value check failed for "+row.getCell(columnIndex[0]));
									        	        } // if loop checking for empty CPILF value ends here
									        	        else {
									        	        		logger.debug("CRSNO value for "+row.getCell(columnIndex[0])+" is empty");
									        	        		logger.debug("EMPTY CPIFL value is valid for "+row.getCell(columnIndex[0]));
									        	        		logger.info("Processing record "+(rowCounter-(Utilities.SKIPROW+1))+" on filename "+file.getName()+" into the CRSNO Map");
										        	        			
									        	        		yearMap = crsnoMap.get(row.getCell(columnIndex[0]).getStringCellValue());
									        		        	yearMap.put(year, new HashMap<String, String>());
									        		        			
									        		        	// retrieving third level stageMap
									        		        	stageMap = yearMap.get(year);
									        		        		
									        		        	// Populating data in stageMap
								        		        		stageMap.put("CPILF"," ");
									        		        			
									        		        	// retrieving STGCD value
								        		        		column = row.getCell(columnIndex[2]);
								        		        		if(column != null && column.getCellType()!=Cell.CELL_TYPE_BLANK){
								        		        			logger.debug("Non-empty STGCD value found");
								        		        			column.setCellType(Cell.CELL_TYPE_STRING);        	
								        		        			stageMap.put("STGCD",getSTGCDValue(column.getStringCellValue()));
								        		        		}
								        		        		else
								        		        		{
								        		        			logger.debug("Empty STGCD value found. Valid as per instructions!");
								        		        			stageMap.put("STGCD"," ");
								        		        		}
										        	        			
									        	        } // else block ends here
									        	        		
									        	        		     		        		
							        		        } // if loop ends here for key check in CRSNO template map 
							        		        else
							        		        	logger.debug("CSRNO name "+column.getStringCellValue()+" is not a valid key in CRSNO Template Map. Moving to next CRSNO name");
							        		        	
							        		  } // if loop checking CRSNO name isn't missing in the column ends here
						        				else 
						        					logger.warn("Missing CRSNO name ");
						        			} // if loop for skipping columns from experimental file ends here 
					        			}// if loop for checking null value in CRSNO column ends here 
					        	}catch (Exception e){
					        		if(rowCounter < Utilities.SKIPROW){
	        		        			logger.warn("Possible null value in first column of the first three rows. Handling condition!!");
	        		        		}
	        		        		else {
	        		        			logger.warn("Last row line encountered");
	        		        			break;
	        		        		}
					        	} // catch block ends here
			        	   } // while loop ends here
			        		
			               Utilities.displayMessage("Experimental File "+file.getName()+" processed successfully");
					    } // validyCheck if loop ends here 
						else{
							logger.info("Validy check failed for "+file.getName()+ ". All three column names aren't present. Skipping file");
						}
						// closing reference to the workbook 
						workbook.close();
						if (processedFlag == 0)
							logger.warn("No CRSNO name read. Possibly a wrong file provided under test_files directory");
					} // if loop ends here
					else 
						logger.info("Invalid file name. Skipping the "+file.getName());
			    } // if loop ends here
			  } // for loop ends here
			  Utilities.displayMessage("All experimental files are processed "); 
			} // try block ends here
			catch (IOException e2){
				e2.printStackTrace();
			} //catch block ends here 	
	} // function experimentalFilesReader ends here
	
	// function to add new column names to output report filename
	public Workbook addAdditionalColumns(int columnCount, String outputReportFileName) throws IOException{
		
		logger.debug("Opening a Workbook reference on template file name ");

		// get a workbook reference
		Workbook workbook = Utilities.retrunWorkbookReference(outputReportFileName);
		
		logger.info("Opened Workbook reference on "+outputReportFileName);
		
		logger.debug("Before getting first tab on the excel file" +outputReportFileName);
		
		// get the first tab sheet from the workbook
		Sheet spreadsheet = workbook.getSheetAt(0);	
		columnCounter= spreadsheet.getRow(Utilities.SKIPROW-1).getLastCellNum()+1;
		
		Row row = spreadsheet.getRow(Utilities.SKIPROW-1);
		
		logger.debug("Creating ArrayList to store additional column names to iterate using index");
		ArrayList<String> columnNames = new ArrayList<String>();
		
		columnNames.addAll(additionalYearColumnNames);
		logger.info("Additional column names added to the ArrayList" );
		
		Cell column = null;
		logger.debug("Before adding the additonal year columns");
		
		logger.info("Adding additional columns to the "+outputReportFileName);
		for(int i=0; i< columnNames.size();i++){
			column = row.createCell(columnCounter+i);
			column.setCellValue(columnNames.get(i));
		}
		return workbook;
	} // function addAditionalCoumns ends here
	
	// function to write to the output report filename
	public void OutputReportExcelWriter(String outputReportFileName) throws IOException {
		
		// creating FileInoutStream on output report template
		try {
			
			// function to add new columns
			Workbook workbook = addAdditionalColumns(columnCounter, outputReportFileName);
			Sheet spreadsheet = workbook.getSheetAt(Utilities.EXPANDOUTPUTTABSELECT);
			
			Row row = spreadsheet.getRow(Utilities.SKIPROW-1);
			Cell column = row.getCell(Utilities.COLUMN);
			CellStyle cellStyle = workbook.createCellStyle();
			
			// for each row, iterate over first column       		   
			Iterator<Row> rowIterator = spreadsheet.iterator();
			
			int rowCounter=0;
			String crsnoName =null;
			while(rowIterator.hasNext()){
				if(rowCounter > Utilities.SKIPROW){
					column = row.getCell(Utilities.COLUMN);
        		    column.setCellType(Cell.CELL_TYPE_STRING);
        		    if(crsnoMap.get(column.getStringCellValue()) != null){
        		    	crsnoName = column.getStringCellValue();
        		    	yearMap = crsnoMap.get(column.getStringCellValue());
        		    	
        		    	// getting sorted keys from CRSNO Map
        		        SortedSet<Integer> yearMapKeys = new TreeSet<Integer>(yearMap.keySet());
        		        ArrayList<Integer> keys = new ArrayList<Integer>();
        		        keys.addAll(yearMapKeys);
        		        
        		        // check yearMap size
        		        if(yearMap.size()!=0){
	        		        logger.debug("Size of yearMap is "+yearMap.size());
	        		        logger.debug("Before iterating over keys in yearMap");
	        		        
	        		        // iterating over each key in stageMap
		        		    for(int i= 0; i<yearMap.size();i++){
		        		    	logger.info("Retrieving STGCD value per for CRSNO name "+crsnoName);
		        		    	stageMap = yearMap.get(keys.get(i));
		        		    	logger.debug("Before checnking if stageMap retrieved for CRSNO name "+crsnoName+" is empty ot not");
		        		        if(stageMap.get("STGCD") != null){
		        		        	column=row.getCell(columnCounter+getWriteColumn(keys.get(i),stageMap.get("STGCD")));
		        		        	if(column == null)
		        		        		column = row.createCell(columnCounter+getWriteColumn(keys.get(i),stageMap.get("STGCD")));
		        		        	column.setCellType(Cell.CELL_TYPE_STRING);
		        		        	column.setCellValue(stageMap.get("STGCD"));
		        		        	cellStyle.setAlignment(CellStyle.ALIGN_CENTER);
		        		        	column.setCellStyle(cellStyle);//(CellStyle.ALIGN_CENTER);
		        		        	logger.debug("Wriiten STGCD value "+ stageMap.get("STGCD")+ " to column "+ columnCounter+getWriteColumn(keys.get(i),stageMap.get("STGCD")));
		        		        } // stageMap if loop ends here 
		        		        else 
		        		        	logger.debug("Empty stageMap retrieved for CRSNO name "+crsnoName);
		        		    } // for loop ends here
		        		    logger.info("Done writing values for STGCD for CRSNO name "+crsnoName);
        		        } // yearMap if loop ends 		
        		    } // crsnoMap if loop ends here
				} // rowCounter if loop ends here
        		        	rowCounter++;	
        		        	row = rowIterator.next(); 	
			} // while loop ends here
        	
        	// Write the output to a file
    	    FileOutputStream fileOut = new FileOutputStream(outputReportFileName);
    	    
    	    logger.debug("Creating "+outputReportFileName);
    	    
    	    // writing to the output report file
    	    workbook.write(fileOut);
    	    
    	    logger.info("Ouput Report written to "+outputReportFileName);
    	    // closing workbook and file instance
    	    workbook.close();
    	    fileOut.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	} // function OutputReportExcelWriter ends here
	
	// function to find the union of all the years under CRSNO
	public void prepareYearColumnTreeSet(){
		
		String stageName;
		additionalYearColumnNames = new TreeSet<String>();
		SortedSet<String> crsnoMapKeys = new TreeSet<String>(crsnoMap.keySet());
		for (String key1 : crsnoMapKeys){	
			yearMap = crsnoMap.get(key1);
			if(!yearMap.isEmpty()){
				// sorted keys on year Map 
				SortedSet<Integer> yearMapKeys = new TreeSet<Integer>(yearMap.keySet());
				for(Integer key2 : yearMapKeys){
					if(yearMap.get(key2).get("STGCD").equals(" ")) 
						stageName= "N/A";
					else
						stageName=yearMap.get(key2).get("STGCD");
					logger.debug("Adding "+key2.toString()+" Stg "+stageName+" entries to TreeSet");
					additionalYearColumnNames.add(key2.toString()+" Stg "+stageName+" entries");
				} // for loop ends here
			} // end of if loop
		} // end of for loop
	} // end of prepareYearColumnTreeSet function
	
	// function to get the right column to write STGCD value a particular CRSNO and year combination 
	public int getWriteColumn(Integer year, String stgcd){
		logger.debug("Checking TreeSet additionalYearColumnNames for a possible key match for year "+year+ " and STGCD "+stgcd);
		String key = year.toString()+" Stg "+stgcd+" entries";
		return additionalYearColumnNames.contains(key)? additionalYearColumnNames.headSet(key).size(): -1;
	} // function getWriteColumn ends here
	
} // dataProcessingPerCRSNO class ends here
package tests;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import core.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestBase {
	
	private WebDriver driver;
	private ITestData testData;
	private ExtentReports extent;
	private ExtentTest test;


	@Parameters({"env","browser"})
	@BeforeSuite
	public void initSuite(String env,String browser) throws Exception {
		TestConfig.load(env);
		TestConfig.addProperty("browser",browser);
		TestConfig.addProperty("env",env);
		extent = new ExtentReports("./TestReport.html", true);
		File file = new File("./ScreenShots");
		if(!file.exists()){
			file.mkdir();
		}
	}

	@BeforeClass
	public void initDriver() {
		driver =  new DriverFactory().getDriver(TestConfig.getProperty("browser"));
	}

	@DataProvider
	public Object[][] getData(Method testCase) throws Exception {
		File testDataLocation = new File("src/test/resources/testdata");
		List<HashMap<String,String>> extractedData = null;
		String dataSource = TestConfig.getProperty("dataSource");
		String envName  =  TestConfig.getProperty("env").toUpperCase();

		// Setting the data source
		if(dataSource.equalsIgnoreCase("excel")){
			this.testData = new ExcelDataProvider(testDataLocation.getAbsolutePath()+"/TestData.xlsx",envName);
		}else if(dataSource.equalsIgnoreCase("json")){
			this.testData = new JSONDataProvider(testDataLocation+"/data."+envName+".json");
		}else{
			throw new Exception("Invalid data source specified : "+dataSource);
		}
		extractedData = this.testData.getAllData(testCase.getName());
		return this.createDataProvider(extractedData);
	}

	private Object[][] createDataProvider(Object dataSet){
		int rowNo = ((ArrayList)dataSet).size();
		Object[][] dataArray = new Object[rowNo][2];
		int dim = 0;
		for(int iRow=0;iRow<rowNo;iRow++) {
			dataArray[dim][0] = iRow+1;
			dataArray[dim][1] = ((ArrayList)dataSet).get(iRow);
			dim++;
		}
		return dataArray;
	}

	public WebDriver getDriver() {
		return driver;
	}
	
	@BeforeMethod
	public void launchApp() {
		driver.get(TestConfig.getProperty("appBaseURL"));
	}

	@BeforeMethod
	public void initTestReport(Method method){
		test = extent.startTest(method.getName(), "");
	}

	public ExtentTest report(){
		if(test!=null){
			return test;
		}
		return null;
	}

	@AfterMethod
	public void closeReport(){
		extent.endTest(test);
	}
	
	@AfterClass
	public void cleanUp() {
		if(driver!=null) {
			driver.quit();
		}
	}

	@AfterSuite
	public void clearReport(){
		extent.flush();
		extent.close();
	}

	public void takeSnapShot() throws Exception{
		WebDriver driver = getDriver();
	    //Convert web driver object to TakeScreenshot

		TakesScreenshot scrShot =((TakesScreenshot) driver);
		//Call getScreenshotAs method to create image file

		File SrcFile=scrShot.getScreenshotAs(OutputType.FILE);
		Path srcPath = SrcFile.toPath();
		//Move image file to new destination

		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
		LocalDateTime now = LocalDateTime.now();
		String date = dtf.format(now);
		date = date.replaceAll("/","").replaceAll(":","").replaceAll(" ","");

		File DestFile=new File("./ScreenShots/"+date+".png");
		Path destPath = DestFile.toPath();
		//Copy file at destination

		Files.copy(srcPath,destPath);

	}



}

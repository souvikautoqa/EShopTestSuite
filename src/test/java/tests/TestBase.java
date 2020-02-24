package tests;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import core.*;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestBase {
	
	private WebDriver driver;
	private ITestData testData;


	@Parameters({"env","browser"})
	@BeforeSuite
	public void initSuite(String env,String browser) throws Exception {
		TestConfig.load(env);
		TestConfig.addProperty("browser",browser);
		TestConfig.addProperty("env",env);
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
	
	@AfterClass
	public void cleanUp() {
		if(driver!=null) {
			driver.quit();
		}
	}
	

}

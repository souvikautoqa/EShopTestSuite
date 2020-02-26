package tests;

import com.relevantcodes.extentreports.LogStatus;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.MyntraHome;
import pages.MyntraLogin;

import java.util.HashMap;
import java.util.Map;

public class MyntraTest extends TestBase{
	
	MyntraHome home =null;
	MyntraLogin login = null;

	@Test(dataProvider = "getData")
	public void verifyMyntraLoginLogout(int itr, Map<String,String> data) throws Exception {
		try{
			home = new MyntraHome(getDriver());
			home.navigateToLoginPage();
			reporter().report(LogStatus.INFO,"Checking for navigation to Myntra", "Navigation to Mytra is successfull");
			login = new MyntraLogin(getDriver());
			login.performLogin(data.get("username"),data.get("password"));
			home = new MyntraHome(getDriver());
			reporter().report(LogStatus.PASS,"Checking for successfull login", "Login to Myntra is successfull", true);
			if(itr==1){
				home.performLogout();
			}
			if(itr==2){
				if(home.verifyLoginError("Account does not exist")){
					reporter().report(LogStatus.PASS,"Checking for Account doesnot exist error", "Account does not exist error displayed succesfully");
				}else{
					reporter().report(LogStatus.FAIL,"Checking for Account doesnot exist error", "Account does not exist error not displayed", true);

				}
			}
		}catch(Exception e){
			reporter().report(LogStatus.FAIL,"Exception Occurred", e.getMessage(), true);

		}

	}

}

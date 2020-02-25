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
			report().log(LogStatus.INFO,"Navigation to Login Page is successfull");
			takeSnapShot();
			login = new MyntraLogin(getDriver());
			login.performLogin(data.get("username"),data.get("password"));
			report().log(LogStatus.PASS,"Login to Myntra is successfull");
			home = new MyntraHome(getDriver());
			if(itr==1){
				home.performLogout();
			}
			if(itr==2){
				if(home.verifyLoginError("Account does not exist")){
					report().log(LogStatus.PASS,"Account does not exist error displayed succesfully");
				}else{
					report().log(LogStatus.FAIL,"Account does not exist error no displayed");
				}
			}
		}catch(Exception e){
			report().log(LogStatus.FAIL,e.getMessage());
		}

	}

}

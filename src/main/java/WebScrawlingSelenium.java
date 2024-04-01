package webcrawling;

import java.io.FileWriter;
import java.time.Duration;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class WebScrawlingSelenium {
	
	static WebDriver driver;

	final static String website1="https://www.realtor.ca/";
	final static String website2="https://www.zolo.ca/";
	final static String website3="https://www.royallepage.ca/en/searchgeo/homes/on/windsor?property_type=&house_type=&features=&listing_type=&lat=42.317438&lng=-83.035225&bypass=&address=Windsor&address_type=city&city_name=Windsor&prov_code=ON&display_type=gallery-view&da_id=&travel_time=&school_id=&boundary=true&search_str=Windsor%2C+ON%2C+CAN&id_search_str=Windsor%2C+ON%2C+CAN&school_search_str=&travel_time_min=30&travel_time_mode=drive&travel_time_congestion=&min_price=0&max_price=5000000%2B&min_leaseprice=0&max_leaseprice=5000%2B&beds=0&baths=0&transactionType=SALE&keyword=";

	public static void main(String [] args) throws Exception {
		try {
		setupChromeDriver();
		//      testCrawl();
		//      realtorCrawl();
		//		zoloCrawl();
		//		royallepageCrawl();
		}
		catch(Exception e) {
			if(e instanceof WebDriverException)
				System.out.println("Web driver issues! Check the driver.");
			if(e instanceof StaleElementReferenceException) //the most notorious one, daikirai
				System.out.println("Referenced web element is no longer present. Try again");
			if(e instanceof InterruptedException)
				System.out.println("Thread is interrupted. Try again");
			if(e instanceof ElementNotInteractableException)
				System.out.println("Web element is hidden or disabled or non-interactable. Try again");
			if(e instanceof TimeoutException)
				System.out.println("Operation took too long to load. Try again");
			if(e instanceof NoSuchElementException) //another bad BAD boy
				System.out.println("Unable to locate the web element. Try again");
		}

	}

	private static void writeToJSON(JSONArray arr, String file) {
		try(FileWriter f = new FileWriter(file)) {
			f.write(arr.toString());
			System.out.println("Data has been scrapped to " + file);
		}
		catch(Exception e) {
			System.out.println("Error writing data to json.");
		}
	}

	static void setupChromeDriver() {
		System.setProperty("webdriver.chrome.driver", "C:\\Users\\User\\Downloads\\chromedriver-win64\\chromedriver.exe");
		ChromeOptions options = new ChromeOptions();
		options.addArguments("--remote-allow-origins=*");
		driver = new ChromeDriver(options);
		driver.manage().window().maximize();
	}

	public static void testCrawl() throws InterruptedException {
		driver.get("http://www.google.com/");
		Thread.sleep(5000);  //let the user actually see something!

		WebElement searchBox = driver.findElement(By.name("q"));
		searchBox.sendKeys("ChromeDriver");
		searchBox.submit();

		Thread.sleep(5000);  //let the user actually see something!

		driver.quit();
	}

	public static String getFirstPageHandle(){
		Set<String> firstPageHandles = driver.getWindowHandles();
		String firstPageHandle = "";
		for (String handle: firstPageHandles) {
			firstPageHandle = handle;
			break;
		}
		return firstPageHandle;
	}

	public static void realtorCrawl() throws InterruptedException {
		driver.get(website1); //navigate to the website
		Thread.sleep(20000); //20s implicit wait for bot verification

		//dismiss the cookie option
		driver.findElement(By.id("TOUdismissBtn")).click();

		//click on the search icon
		driver.findElement(By.id("homeSearchBtn")).click();
		Thread.sleep(2000);

		//dynamic webpage wait for ~10s
		Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		JavascriptExecutor js = (JavascriptExecutor) driver;

		String firstPageHandle = getFirstPageHandle();

		ArrayList <String> cities = new ArrayList<>(Arrays.asList("Toronto, ON", "Windsor, ON", "Montreal, QC", "Edmonton, AB", "Vancouver, BC", "Halifax, NS"));

		//create the JSON array to store all the scraped data
		JSONArray rlt_arr= new JSONArray();

		//loop the listings for each cities
		for(String city:cities) {
			try {
				
				WebElement search = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("txtMapSearchInput")));

				//to get all the listings
				search.sendKeys(city);
				driver.findElement(By.id("btnMapSearch")).click();
				Thread.sleep(5000);
				driver.findElement(By.id("polygonOptInBtn")).click();
				Thread.sleep(5000);

				List <WebElement> realtor_listings= driver.findElements(By.className("cardCon"));
				System.out.println(realtor_listings.toString());

				int c=1;	//counter for keeping tracking of each listing
				for(WebElement rlt:realtor_listings) {
					//get the link of each card listing
					String link=rlt.findElement(By.cssSelector(".blockLink")).getAttribute("href");

					//open the link
					js.executeScript("window.open('"+link+"','_blank');");
					Thread.sleep(5000);

					//store all the window handles in a set to avoid repetition
					Set<String> windowHandles = driver.getWindowHandles();
					String[] handles = new String[windowHandles.size()];	//size=2 - mainPage and each listing page that opens

					//convert window handles to Array, start from the first listing (which is handle[1]), switch to the first listing tab
					windowHandles.toArray(handles);
					String handle = handles[1];
					driver.switchTo().window(handle);
					Thread.sleep(10000);

					//scrape all the data into a JSON object for every listing
					JSONObject rlt_obj= new JSONObject();
					rlt_obj.put(city+"_"+c, driver.findElement(By.id("mainCon")).getText());
					rlt_arr.put(rlt_obj);
					c++;

					//close the current tab and switch to the new next listing
					driver.close();
					driver.switchTo().window(firstPageHandle);
				}

				//go back to main listings page
				driver.switchTo().window(firstPageHandle);
				Thread.sleep(2000);

				//clear the text field for city
				driver.findElement(By.id("locationSearchFilterImg")).click();
			}
			catch(Exception e){
				System.out.println("Error scraping realtor.ca : " + e.toString());
			}
		}

		//write all the listings to the JSON file
		writeToJSON(rlt_arr, "realtor.json");

		//close the browser instance at the end
		driver.quit();
	}

	public static void zoloCrawl() throws InterruptedException {
		driver.get(website2); //navigate to the website
		Thread.sleep(10000); //10s implicit wait 

		//dynamic web-page wait for ~10s
		Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		ArrayList <String> cities = new ArrayList<>(Arrays.asList("Toronto, ON", "Windsor, ON", "Montreal, QC", "Edmonton, AB", "Vancouver, BC", "Halifax, NS"));

		//create the JSON array to store all the scraped data
		JSONArray zolo_arr= new JSONArray();

		//loop the listings for each cities
		for(String city:cities) {
			try {

				//clear the search field from previous value(s)
				driver.findElement(By.id("sarea")).clear();

				//dynamic wait until all the elements are visible on the web page
				WebElement search = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("sarea")));

				//search the listing
				search.sendKeys(city);
				//click on the search button
				driver.findElement(By.cssSelector(".xs-t05")).click();
				Thread.sleep(5000);

				//get all the listings
				List <WebElement> zolo_listings= driver.findElements(By.cssSelector(".xs-aspect-3-2"));
				System.out.println(zolo_listings.toString());

				int c=1;	//counter for keeping tracking of each listing
				for(int item=0; item<zolo_listings.size(); item++) {

					//find the listings again to avoid stale element reference exception
					zolo_listings= driver.findElements(By.cssSelector(".xs-aspect-3-2"));

					WebElement zolo = zolo_listings.get(item);

					//skip the hidden listings
					if(driver.findElement(By.cssSelector(".xs-mb1")).getText().equals("$XXX,XXX"))
						continue;

					//open the listing
					zolo.click();

					//click the link for the listing
					String link=zolo.getAttribute("href");
					driver.navigate().to(link);
					Thread.sleep(5000);

					//scrape all the data into a JSON object for every listing
					JSONObject zolo_obj= new JSONObject();
					zolo_obj.put(city+"_"+c, driver.findElement(By.className("main-column")).getText());
					zolo_arr.put(zolo_obj);
					c++;

					//go back to the previous tab before iterating to the next listing
					driver.navigate().back();
					//close the pop up - .xs-t1, .xs-z4
					driver.findElement(By.cssSelector(".xs-t1")).click();

					Thread.sleep(2000);
				}

				//go back to the previous tab before iterating to the next city
				driver.navigate().back();
				//close the pop up
				driver.findElement(By.cssSelector(".xs-r1")).click();
				Thread.sleep(2000);

			}
			catch(Exception e){
				System.out.println("Error scraping zolo.ca : " + e.toString());
			}
		}

		//write all the listings to the JSON file
		writeToJSON(zolo_arr, "zolo.json");

		//close the browser instance at the end
		driver.quit();
	}

	public static void royallepageCrawl() throws Exception {
		driver.get(website3); //navigate to the website
		Thread.sleep(5000); //10s implicit wait

		//clear the search field from previous value(s)
		driver.findElement(By.id("id_search_str")).clear();
		Thread.sleep(3000);

		//dynamic web-page wait for ~10s
		//		Wait<WebDriver> wait = new WebDriverWait(driver, Duration.ofSeconds(10));

		//		JavascriptExecutor js = (JavascriptExecutor) driver;

		ArrayList <String> cities = new ArrayList<>(Arrays.asList("Winnipeg, MB, CAN", "Windsor, ON, CAN", "Montreal, QC, CAN", "Toronto, ON, CAN", "Edmonton, AB, CAN", "Vancouver, BC, CAN", "Halifax, NS, CAN"));

		//create a JSON array to store all the scraped data
		JSONArray rp_arr= new JSONArray();


		//go through the listings for each cities
		for(String city:cities) {
			try {

				//search for the city
				driver.findElement(By.id("id_search_str")).sendKeys(city);
				Thread.sleep(3000);

				//click the search button
				driver.findElement(By.cssSelector(".search-bar__button .button")).click();
				Thread.sleep(5000);	//5s implicit wait for the elements to load

				//grab all the listings
				List <WebElement> royallepage_listings= driver.findElements(By.xpath("//*[@id=\"gallery-view2\"]/ul/li/div/div[3]/address[1]/a"));
				System.out.println(royallepage_listings.toString());

				int c=1;	//counter for keeping tracking of each listing

				for(int item=0; item<royallepage_listings.size(); item++) {

					//find the listings again to avoid stale element reference exception
					royallepage_listings= driver.findElements(By.xpath("//*[@id=\"gallery-view2\"]/ul/li/div/div[3]/address[1]/a"));

					WebElement rp = royallepage_listings.get(item);

					//checker for console - works :)
					//					System.out.println(rp.getText());

					//open the listing
					rp.click();
					Thread.sleep(5000);

					//create a JSON object to scrape all the data into
					JSONObject rp_obj= new JSONObject();
					rp_obj.put(city+"_"+c, driver.findElement(By.id("content")).getText());
					rp_arr.put(rp_obj);
					c++;

					//navigate to the main listings page before the next iteration of the listing
					driver.navigate().back();
					Thread.sleep(2000);

				}

				//navigate to the new main listings page to continue iterating through all the city's listings
				driver.navigate().back();				
				Thread.sleep(2000);

				//clear the search field before iterating over the next city
				driver.findElement(By.id("id_search_str")).clear();
				Thread.sleep(2000);

			}
			catch(Exception e){
				System.out.println("Error scraping royallepage.ca : " + e.toString());
			}
		}

		//write all the listings to the JSON file
		writeToJSON(rp_arr, "royalle.json");

		//close the browser instance at the end
		driver.quit();
	}
}

package com.MackarooProject;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import io.github.bonigarcia.wdm.WebDriverManager;

public class MockarooDataValidation {
	WebDriver driver;

	@BeforeClass
	public void setUp() {
		System.out.println("Setting up WebDriver in BeforeClass...");
		WebDriverManager.chromedriver().setup();
		driver = new ChromeDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.manage().window().fullscreen();
	}

	@Test(priority = 1)
	public void navigateToHomePageAndDoStuff() throws InterruptedException, IOException {
		driver.get("https://mockaroo.com/");
		assertEquals(driver.getTitle(),
				"Mockaroo - Random Data Generator and API Mocking Tool | JSON / CSV / SQL / Excel");
		String getMackaroo = driver.findElement(By.xpath("/html/body/div[1]/div/div[1]/a/div[1]")).getText();
		String getRealisticDG = driver.findElement(By.xpath("/html/body/div[1]/div/div[1]/a/div[2]")).getText();

		Assert.assertEquals(getMackaroo, "mockaroo");
		Assert.assertEquals(getRealisticDG, "realistic data generator");

		List<WebElement> checkboxes = driver.findElements(By.xpath("//a[contains(@class,'close')]"));
		for (WebElement wb : checkboxes) {
			System.out.println(wb.getText());
			wb.click();
		}

		List<WebElement> nameTypeOptions = driver
				.findElements(By.xpath("//div[contains(@class,'column column-header column')] "));
		for (WebElement wb1 : nameTypeOptions) {
			Assert.assertTrue(wb1.isDisplayed());
		}

		assertTrue(driver.findElement(By.xpath("//a[@class='btn btn-default add-column-btn add_nested_fields']"))
				.isEnabled());

		int rowsAct = Integer.parseInt(driver.findElement(By.xpath("//input[@id='num_rows']")).getAttribute("value"));
		assertEquals(rowsAct, 1000);

		Select formatDrop1 = new Select(driver.findElement(By.xpath("//select[@id='schema_file_format']")));
		assertEquals(formatDrop1.getFirstSelectedOption().getText(), "CSV");

		Select formatDrop2 = new Select(driver.findElement(By.xpath("//*[@id=\"schema_line_ending\"]")));
		assertEquals(formatDrop2.getFirstSelectedOption().getText(), "Unix (LF)");

		assertTrue(driver.findElement(By.id("schema_include_header")).isSelected());

		assertFalse(driver.findElement(By.id("schema_bom")).isSelected());

		// 12. Click on ‘Add another field’ and enter name “City”
		driver.findElement(By.xpath("//*[@id=\"schema_form\"]/div[2]/div[3]/div[2]/a")).click();
		driver.findElement(By.xpath("//div[@id='fields']/div[7]/div[2]/input")).sendKeys("City");

		// 13. Click on Choose type and assert that Choose a Type dialog box is
		// displayed
		driver.findElement(By.xpath("//*[@id=\"fields\"]/div[7]/div[3]/input[3]")).click();
		Thread.sleep(2000);
		assertTrue(driver.findElement(By.xpath("//h3[@class='modal-title'][.='Choose a Type']")).isDisplayed());

		// 14. Search for “city” and click on City on search results.
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[@id='type_search_field']")).sendKeys("City");
		driver.findElement(By.xpath("//div[@class='type-name'][.='City']")).click();

		// 15. Repeat steps 12-14 with field name and type “Country”
		Thread.sleep(2000);
		driver.findElement(By.xpath("//a[@class='btn btn-default add-column-btn add_nested_fields']")).click();
		driver.findElement(By.xpath("//div[@id='fields']/div[8]/div[2]/input")).clear();
		driver.findElement(By.xpath("//div[@id='fields']/div[8]/div[2]/input")).sendKeys("Country");

		driver.findElement(By.xpath("//div[@id=\"fields\"]/div[8]/div[3]/input[3]")).click();
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[@id='type_search_field']")).clear();
		Thread.sleep(2000);
		driver.findElement(By.xpath("//input[@id='type_search_field']")).sendKeys("Country");
		Thread.sleep(2000);
		driver.findElement(By.xpath("//div[@class='type-name'][.='Country']")).click();

		// 16. Click on Download Data.
		Thread.sleep(2000);
	}
	// driver.findElement(By.xpath("//button[@id='download']")).click();

	@Test(priority = 2)
	public void openFileAndDoStuff() throws IOException {

		// 17. Open the downloaded file using BufferedReader
		// 18. Assert that first row is matching with Field names that we selected.
		String fileName = "/Users/mustakilali/Downloads/MOCKAROO_DATA (2).csv";
		FileReader fr = new FileReader(fileName);
		BufferedReader br = new BufferedReader(fr);
		String expected = "City,Country";
		String actual = br.readLine();
		assertEquals(actual, expected);

		int count = 0;
		while (br.readLine() != null) {
			count++;
		}

		// 19. Assert that there are 1000 records
		assertTrue(count == 1000);

	

	}
	//20. From file add all Cities to Cities ArrayList
	@Test (priority = 3)
	public void cityList() throws IOException {
		String fileName = "/Users/mustakilali/Downloads/MOCKAROO_DATA (2).csv";
		FileReader fr = new FileReader(fileName);
		BufferedReader br = new BufferedReader(fr);
		List<String> cities = new ArrayList<>();
		String cityLine = "";
		br.readLine(); //Jumping City, to not add to list. 
		while ((cityLine = br.readLine()) != null) {
				int a = cityLine.indexOf(",");
				cityLine = cityLine.substring(0, a);
				cities.add(cityLine);
		}
		System.out.println("List of City List: " +cities.size());
		//24. From file add all Cities to citiesSet HashSet
		//25. Count how many unique cities are in Cities ArrayList and assert that it is matching with the count of citiesSet HashSet.
		Set<String> citiesSet = new HashSet<String>(cities);
		
		List<String> citiesCopy = new ArrayList<>(cities);
		
		
		for (Iterator<String> iter1 = cities.iterator(); iter1.hasNext(); ) {
			String ctry1 = iter1.next();
			boolean check = false;
			
			for (Iterator<String> iter2 = citiesCopy.iterator(); iter2.hasNext();) {
				String ctry2 = iter2.next();
				if (ctry2.equals(ctry1)) {
					if(!check) {
						check = true;
						continue;
					}
					iter2.remove();
				}
				
			}
		}
		
		assertEquals(citiesSet.size() , citiesCopy.size());
		System.out.println("Numbers of unique cities in citiesSet: " + citiesSet.size() + "------" + " Number of unique cities in ArrayList: " +citiesCopy.size());
		
		
		//22. Sort all cities and find the city with the longest name and shortest name
		 String longest = cities.get(0);	
		 String shortest = cities.get(0);
		 
		 for (String element1 : cities) {
			if(element1.length() > longest.length()) {
				longest = element1;
			}
		}
		 System.out.println("Longest City In List Is: " + longest);

		    for (String element2 : cities) {
		        if (element2.length() < shortest.length()) {
		            shortest = element2;
		        }
		    }
		
		    System.out.println("Shortest City In List Is: " + shortest);
		  
		
	}
	//21. Add all countries to Countries ArrayList
	@Test(priority = 4)
	public void countryList() throws IOException {
		String fileName = "/Users/mustakilali/Downloads/MOCKAROO_DATA (2).csv";
		FileReader fr = new FileReader(fileName);
		BufferedReader br = new BufferedReader(fr);
		List<String> country = new ArrayList<>();
		String countryLine = "";
		br.readLine(); //Jumping City, to not add to list. 
		while ((countryLine = br.readLine()) != null) {
				int a = countryLine.indexOf(",");
				countryLine = countryLine.substring(a+1);
				country.add(countryLine);
		}
		System.out.println("List of Country List: "+country.size());
		//26. Add all Countries to countrySet HashSet 
		//27. Count how many unique cities are in Countries ArrayList and assert that it is matching with the count of countrySet HashSet.
		Set<String> countrySet = new HashSet<String>(country);
		
		List<String> countryCopy = new ArrayList<>(country);
		
		for (Iterator<String> iter1 = country.iterator(); iter1.hasNext(); ) {
			String ctry1 = iter1.next();
			boolean check = false;
			
			for (Iterator<String> iter2 = countryCopy.iterator(); iter2.hasNext();) {
				String ctry2 = iter2.next();
				if (ctry2.equals(ctry1)) {
					if(!check) {
						check = true;
						continue;
					}
					iter2.remove();
				}
				
			}
		}
		
		assertEquals(countrySet.size() , countryCopy.size());
		System.out.println("Numbers of unique cities in citiesSet: " + countrySet.size() + "------" + " Number of unique cities in ArrayList: " +countryCopy.size());
		
		
		
		
		
		//23. In Countries ArrayList, find how many times each Country is mentioned. 
		Set<String> countryNum = new HashSet<String>(country);
		
		for (Iterator<String> iter = countryNum.iterator(); iter.hasNext(); ) {
			String ctryName = iter.next();
			int count = 0;
			for (int j = 0; j < country.size(); j++) {
				if(ctryName.equals(country.get(j))) {
					count++;
				}
			}
			System.out.println(ctryName + "-" + count);
		}
	}

}

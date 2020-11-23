package com.jaliansystems.javadriver.examples.swing.ut;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import net.sourceforge.marathon.javadriver.JavaDriver;
import net.sourceforge.marathon.javadriver.JavaProfile;
import net.sourceforge.marathon.javadriver.JavaProfile.LaunchMode;
import net.sourceforge.marathon.javadriver.JavaProfile.LaunchType;

public class LoginDialogTest {

	/*
	 * Changes: added some variables which are repeatable, set up them in @Before
	 * annotation, deleted unused imports, changed names of tests, added @DisplayName
	 * In build.gradle: changed dependencies.
	 * In gradle-wrapper.properties: changed version to: gradle-6.1.1-bin.zip
	 * 
	 */

	private LoginDialog login;
	private WebDriver driver;
	private WebDriverWait wait;
	private WebElement user, pass, loginBtn, cancelBtn, clearBtn;

	@Before
	public void setUp() throws Exception {
		login = new LoginDialog() {
			private static final long serialVersionUID = 1L;

			@Override
			protected void onSuccess() {
			}

			@Override
			protected void onCancel() {
			}
		};
		SwingUtilities.invokeLater(() -> login.setVisible(true));
		JavaProfile profile = new JavaProfile(LaunchMode.EMBEDDED);
		profile.setLaunchType(LaunchType.SWING_APPLICATION);
		driver = new JavaDriver(profile);
		wait = new WebDriverWait(driver, 10);
		user = driver.findElement(By.cssSelector("text-field"));
		pass = driver.findElement(By.cssSelector("password-field"));
		loginBtn = driver.findElement(By.cssSelector("button[text='Login']"));
		cancelBtn = driver.findElement(By.cssSelector("button[text='Cancel']"));
		clearBtn = driver.findElement(By.cssSelector("button[text='Clear']"));
	}

	@After
	public void tearDown() throws Exception {
		if (login != null)
			SwingUtilities.invokeAndWait(() -> login.dispose());
		if (driver != null)
			driver.quit();
	}

	@Test
	@DisplayName("Test successful login")
	public void testLoginSuccess() {
		user.sendKeys("bob");		
		pass.sendKeys("secret");		
		wait.until(ExpectedConditions.elementToBeClickable(loginBtn));
		loginBtn.click();
		assertTrue(login.isSucceeded());
		assertTrue(login.getSize() != null);
	}

	@Test
	@DisplayName("Test cancelled login")
	public void testLoginCancel() {
		user.sendKeys("bob");		
		pass.sendKeys("secret");
		cancelBtn.click();
		assertFalse(login.isSucceeded());
	}

	@Test
	@DisplayName("Test invalid login")
	public void testLoginInvalid() throws InterruptedException {
		user.sendKeys("bob");		
		pass.sendKeys("wrong");
		wait.until(ExpectedConditions.elementToBeClickable(loginBtn));
		loginBtn.click();
		driver.switchTo().window("Invalid Login");
		driver.findElement(By.cssSelector("button[text='OK']")).click();
		driver.switchTo().window("Login");
		assertEquals("", user.getText());
		assertEquals("", pass.getText());
	}
	
	// New added test for "clear" button added in UI in LoginDialog class
	@Test 
    @DisplayName("Test cleared login")		
    public void testLoginClear() throws InterruptedException {
        user.sendKeys("bob");
        pass.sendKeys("wrong");
        clearBtn.click(); 
        assertEquals("", user.getText());
        assertEquals("", pass.getText());
    }

	@Test
	@DisplayName("Test if all text components are associated with tooltip")
	public void testCheckTooltipText() {
		// Check that all the text components (like text fields, password
		// fields, text areas) are associated with a tooltip
		List<WebElement> textComponents = driver.findElements(By.className(JTextComponent.class.getName()));
		for (WebElement tc : textComponents) {
			assertNotEquals(null, tc.getAttribute("toolTipText"));
		}
	}
}

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class AccountWorker {

    public void runAccount(String user, String pass) {

        System.out.println("Starting account...");

        ChromeOptions options = new ChromeOptions();

        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        ChromeDriver driver = new ChromeDriver(options);

        try {
            driver.get("https://elem.cards/login/");

            driver.findElement(By.name("plogin")).sendKeys(user);
            driver.findElement(By.name("ppass")).sendKeys(pass);
            driver.findElement(By.cssSelector("input[type='submit']")).click();

            Thread.sleep(4000);

            System.out.println("Login success ✔");

            driver.get("https://elem.cards/guild/arena/");

            Thread.sleep(3000);

            System.out.println("Account finished ✔");

        } catch (Exception e) {
            System.out.println("Error in account:");
            e.printStackTrace();
        } finally {
            driver.quit();
        }
    }
}

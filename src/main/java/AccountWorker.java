import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.Queue;

public class AccountWorker implements Runnable {

    private Queue<Integer> accounts;
    private String name;

    public AccountWorker(String name, Queue<Integer> accounts) {
        this.name = name;
        this.accounts = accounts;
    }

    @Override
    public void run() {

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");

        WebDriver driver = new ChromeDriver(options);

        while (!accounts.isEmpty()) {

            int acc = accounts.poll();
            if (acc == 0) continue;

            try {
                String user = System.getenv("GAME_ID_" + acc);
                String pass = System.getenv("GAME_PASSWORD_" + acc);

                if (user == null || pass == null) {
                    System.out.println("Missing " + acc);
                    continue;
                }

                System.out.println(name + " preparing account " + acc);

                // LOGIN
                driver.get("https://elem.cards/login/");
                Thread.sleep(2000);

                driver.findElement(By.name("plogin")).sendKeys(user);
                driver.findElement(By.name("ppass")).sendKeys(pass);
                driver.findElement(By.cssSelector("input[type='submit']")).click();

                Thread.sleep(3000);

                driver.get("https://elem.cards/guild/arena/");
                Thread.sleep(1500);

                // WAIT FOR GLOBAL START SIGNAL 🔥
                while (!SharedSignal.START) {
                    Thread.sleep(10);
                }

                System.out.println(name + " STARTING account " + acc);

                var join = driver.findElements(
                        By.xpath("//a[contains(@href,'/guild/arena/join/')]")
                );

                if (!join.isEmpty()) {
                    driver.get(join.get(0).getAttribute("href"));
                    arena(driver);
                } else {
                    System.out.println("Arena not ready for " + acc);
                }

            } catch (Exception e) {
                System.out.println("Error account " + acc);
            }
        }

        driver.quit();
    }

    private void arena(WebDriver driver) throws Exception {

        int ticks = 0;

        while (ticks < 250) {

            boolean act = false;

            act |= click(driver, "a[href*='attack0']");
            act |= click(driver, "a[href*='attack1']");
            act |= click(driver, "a[href*='attack2']");

            if (!act) {
                Thread.sleep(1200);
                driver.navigate().refresh();

                if (driver.findElements(By.cssSelector("a[href*='attack']")).isEmpty()) {
                    break;
                }
            }

            Thread.sleep(300);
            ticks++;
        }
    }

    private boolean click(WebDriver driver, String css) {

        var el = driver.findElements(By.cssSelector(css));

        if (!el.isEmpty()) {
            try {
                el.get(0).click();
            } catch (Exception e) {
                ((JavascriptExecutor) driver)
                        .executeScript("arguments[0].click();", el.get(0));
            }
            return true;
        }
        return false;
    }
}

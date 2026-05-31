import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.List;

public class Main {

    private static WebDriver driver;

    public static void main(String[] args) {

        System.out.println("=== BOT STARTED (21–30) ===");

        ChromeOptions options = new ChromeOptions();

        // 🔥 Render stable flags
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);

        try {

            for (int i = 21; i <= 30; i++) {

                String user = System.getenv("GAME_ID_" + i);
                String pass = System.getenv("GAME_PASSWORD_" + i);

                if (user == null || pass == null) {
                    System.out.println("Skipping " + i);
                    continue;
                }

                System.out.println("\n▶ Account " + i);

                if (!login(user, pass)) {
                    System.out.println("Login failed " + i);
                    continue;
                }

                driver.get("https://elem.cards/guild/arena/");
                sleep(2000);

                List<WebElement> join =
                        driver.findElements(By.xpath("//a[contains(@href,'/guild/arena/join/')]"));

                if (join.isEmpty()) {
                    System.out.println("Arena not started for " + i);
                    continue;
                }

                driver.get(join.get(0).getAttribute("href"));
                sleep(2000);

                executeArena();

            }

        } catch (Exception e) {
            System.out.println("🔥 GLOBAL ERROR:");
            e.printStackTrace();
        } finally {
            if (driver != null) driver.quit();
        }

        System.out.println("=== DONE (21–30) ===");
    }

    // ---------------- LOGIN ----------------
    private static boolean login(String user, String pass) {
        try {
            driver.get("https://elem.cards/login/");
            sleep(2000);

            driver.findElement(By.name("plogin")).sendKeys(user);
            driver.findElement(By.name("ppass")).sendKeys(pass);
            driver.findElement(By.cssSelector("input[type='submit']")).click();

            sleep(4000);
            return true;

        } catch (Exception e) {
            return false;
        }
    }

    // ---------------- ARENA ----------------
    private static void executeArena() {

        int ticks = 0;

        while (ticks < 250) {

            boolean action = false;

            action |= click("a[href*='attack0']");
            action |= click("a[href*='attack1']");
            action |= click("a[href*='attack2']");

            if (!action) {
                sleep(1500);
                driver.navigate().refresh();

                if (driver.findElements(By.cssSelector("a[href*='attack']")).isEmpty()) {
                    System.out.println("Arena ended");
                    break;
                }
            }

            sleep(400);
            ticks++;
        }
    }

    // ---------------- CLICK SAFE ----------------
    private static boolean click(String css) {
        List<WebElement> el = driver.findElements(By.cssSelector(css));

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

    private static void sleep(int ms) {
        try { Thread.sleep(ms); } catch (Exception ignored) {}
    }
}

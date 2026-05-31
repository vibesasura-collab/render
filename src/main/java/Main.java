import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Main {

    public static void main(String[] args) throws Exception {

        System.out.println("=== PARALLEL BOT READY (21–30) ===");

        // ⏳ WAIT UNTIL EVENT TIME (optional)
        long startTime = System.currentTimeMillis() + 10000; // 10 sec demo wait

        System.out.println("Waiting for event trigger...");

        while (System.currentTimeMillis() < startTime) {
            Thread.sleep(500);
        }

        System.out.println("🔥 EVENT STARTED — launching all accounts");

        ExecutorService executor = Executors.newFixedThreadPool(10);

        List<Future<?>> tasks = new ArrayList<>();

        for (int i = 21; i <= 30; i++) {

            int accountId = i;

            tasks.add(executor.submit(() -> runAccount(accountId)));
        }

        for (Future<?> f : tasks) {
            f.get();
        }

        executor.shutdown();

        System.out.println("=== ALL ACCOUNTS FINISHED ===");
    }

    // ---------------- RUN EACH ACCOUNT ----------------
    private static void runAccount(int i) {

        ChromeOptions options = new ChromeOptions();

        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        WebDriver driver = new ChromeDriver(options);

        try {

            String user = System.getenv("GAME_ID_" + i);
            String pass = System.getenv("GAME_PASSWORD_" + i);

            if (user == null || pass == null) {
                System.out.println("Missing account " + i);
                return;
            }

            System.out.println("▶ Account " + i + " starting");

            // LOGIN
            driver.get("https://elem.cards/login/");
            Thread.sleep(2000);

            driver.findElement(By.name("plogin")).sendKeys(user);
            driver.findElement(By.name("ppass")).sendKeys(pass);
            driver.findElement(By.cssSelector("input[type='submit']")).click();

            Thread.sleep(4000);

            // ARENA
            driver.get("https://elem.cards/guild/arena/");
            Thread.sleep(2000);

            var join = driver.findElements(
                    By.xpath("//a[contains(@href,'/guild/arena/join/')]")
            );

            if (join.isEmpty()) {
                System.out.println("Arena not started for " + i);
                return;
            }

            driver.get(join.get(0).getAttribute("href"));

            executeArena(driver);

        } catch (Exception e) {
            System.out.println("Error account " + i);
        } finally {
            driver.quit();
        }
    }

    // ---------------- ARENA LOOP ----------------
    private static void executeArena(WebDriver driver) throws Exception {

        int ticks = 0;

        while (ticks < 250) {

            boolean action = false;

            action |= click(driver, "a[href*='attack0']");
            action |= click(driver, "a[href*='attack1']");
            action |= click(driver, "a[href*='attack2']");

            if (!action) {
                Thread.sleep(1500);
                driver.navigate().refresh();

                if (driver.findElements(By.cssSelector("a[href*='attack']")).isEmpty()) {
                    break;
                }
            }

            Thread.sleep(400);
            ticks++;
        }
    }

    private static boolean click(WebDriver driver, String css) {

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

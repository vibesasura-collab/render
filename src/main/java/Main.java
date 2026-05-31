public class Main {

    public static void main(String[] args) {

        System.out.println("=== BOT START ===");

        for (int i = 21; i <= 30; i++) {

            String user = System.getenv("GAME_ID_" + i);
            String pass = System.getenv("GAME_PASSWORD_" + i);

            if (user == null || pass == null) {
                System.out.println("Skip " + i);
                continue;
            }

            AccountWorker worker = new AccountWorker();
            worker.run(user, pass);
        }

        System.out.println("=== DONE ===");
    }
}

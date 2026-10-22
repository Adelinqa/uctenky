package cz.uctenka;

/** Vstupní bod programu. */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        System.out.println(AppInfo.load());
    }
}

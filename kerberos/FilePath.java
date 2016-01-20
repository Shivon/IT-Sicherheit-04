package kerberos;

public class FilePath {
    public static String getPath() {
        if (FilePath.isUnix()) {
            return "/home/marjan/projects/IT-Sicherheit-04/test.txt";
        } else {
            return ".\\IT-Sicherheit-04\\test.txt";
        }
    }

    private static boolean isUnix() {
        String os = System.getProperty("os.name");
        return (os.startsWith("Linux") || os.startsWith("Mac"));
    }
}

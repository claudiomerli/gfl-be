package it.xtreamdev.gflbe.util;

public class DocxUtils {
    public static String substituteDocxSpecialCharacters(String fileContent) {
        return fileContent
                .replace("&nbsp;", " ")
                .replace("&ograve;", "ò")
                .replace("&agrave;", "à")
                .replace("&eacute;", "é")
                .replace("&egrave;", "è")
                .replace("&ugrave;", "ù")
                .replace("&igrave;", "ì")
                .replace("&Egrave;", "È")
                .replace("&Eacute;", "É")
                .replace("&rsquo;", "’")
                .replace("&lsquo;", "‘");
    }
}

package Util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataUtil {
    public static String formatar(final Date data, final String mascara) {
        SimpleDateFormat formatD = new SimpleDateFormat(mascara);
        return data == null ? "" : formatD.format(data);
    }
}

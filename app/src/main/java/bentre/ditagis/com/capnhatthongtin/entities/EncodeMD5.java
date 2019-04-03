package bentre.ditagis.com.capnhatthongtin.entities;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by ThanLe on 23/10/2017.
 */

public class EncodeMD5 {

    public EncodeMD5() {
    }

    public String encode(String s) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        md.update(s.getBytes());

        byte byteData[] = md.digest();

        //convert the byte to hex format method 1
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
}

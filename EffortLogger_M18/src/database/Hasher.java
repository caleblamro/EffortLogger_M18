package database;

import org.mindrot.jbcrypt.BCrypt;

public class Hasher {
  
  public static String hashPassword(String password) {
    String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());
    return hashedPassword;
  }
  public static boolean matches(String plain_text_password, String hashed_password) {
	  return BCrypt.checkpw(plain_text_password, hashed_password);
  }
  
}

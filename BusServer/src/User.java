import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class User {
	int id;
	String username;
	String password;
	String name;
	String ccard_number;
	String ccard_val;
	String ccard_type;

	User(int id_number, String u, String p, String n, String cn, String cv, String ct){
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(p.getBytes());
			id = id_number;
			password = stringHexa(md.digest());
			username = u;
			name = n;
			ccard_number = cn;
			ccard_val = cv;
			ccard_type = ct;
		} catch (NoSuchAlgorithmException e) {
			System.out.println("User not created!");
		}

	}
	
	private static String stringHexa(byte[] bytes) {
		   StringBuilder s = new StringBuilder();
		   for (int i = 0; i < bytes.length; i++) {
		       int parteAlta = ((bytes[i] >> 4) & 0xf) << 4;
		       int parteBaixa = bytes[i] & 0xf;
		       if (parteAlta == 0) s.append('0');
		       s.append(Integer.toHexString(parteAlta | parteBaixa));
		   }
		   return s.toString();
	}

	public int login(String u, String p){

		System.out.println("password given:" + p + " password:" + password);

		if(u.equals(username))
			if(p.equals(password))
				return id;

		return -1;
	}



}

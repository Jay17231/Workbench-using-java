import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.json.JSONException;

import logging.EventLogger;

public class Authentication {
	String credentialsFile = "credentials.csv";
	String credential = "";

	public void register(String username, String password) throws IOException {
		EventLogger log2 = new EventLogger();
		credential = "";
		boolean isRegistered = false;
		File file = new File(credentialsFile);
		if (!file.exists()) {
			file.createNewFile();
		}
		BufferedReader bufferedReaderObject = new BufferedReader(new FileReader(credentialsFile));
		while ((credential = bufferedReaderObject.readLine()) != null) {
			String[] credentials = credential.split(",");
			if (credentials[0].toLowerCase().equals(username.toLowerCase())) {
				System.out.println("User Already Registered");
				isRegistered = true;
				log2.errorreglog(username);
				try {
					main(null);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				break;
			}
		}
		if (!isRegistered) {
			BufferedWriter bufferedWriterObject = new BufferedWriter(new FileWriter(credentialsFile, true));
			bufferedWriterObject.write(username + "," + password + "\n");
			bufferedWriterObject.close();
			System.out.println("User Registered successfully");
			log2.reglog(username);
			try {
				main(null);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void authenticate(String username, String password, String operation) throws IOException {
		EventLogger log2 = new EventLogger();
		Scanner scannerObject = new Scanner(System.in);
		Boolean isAuth = false;
		BufferedReader bufferedReaderObject = new BufferedReader(new FileReader(credentialsFile));
		while ((credential = bufferedReaderObject.readLine()) != null) {
			String[] credentials = credential.split(",");
			if (credentials != null) {
				if (credentials[0] != "" || credentials[1] != "") {
					if (credentials[0].toLowerCase().equals(username.toLowerCase())
							&& credentials[1].equals(password)) {
						System.out.println("Authenticated");
						isAuth = true;
						log2.authlog(username);
						if (operation.equals("queryInitialization")) {
							QueryInit qInit = new QueryInit();
							qInit.init(username);
						} else if (operation.equals("dumpcreation")) {
							DumpCreation dumpCreationObject = new DumpCreation();
							System.out.println("Enter the database Name");
							String DatabaseName = scannerObject.next();
							try {
								dumpCreationObject.CreateDump(DatabaseName);
							} catch (IOException e) {
								e.printStackTrace();
							} catch (JSONException e) {
								e.printStackTrace();
							}
						} else if (operation.equals("ERDGeneration")) {
							System.out.println("Enter the database Name");
							String DatabaseForERD = scannerObject.next();
							Process p = Runtime.getRuntime().exec("python3 ERDGeneration.py " + DatabaseForERD);
							System.out.println("Database ERD generated");
						}
					}
				}
			}
		}
		if (!isAuth) {
			System.out.println("Unable to Authenticate!");
			log2.errorauthlog(username);
			try {
				main(null);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) throws IOException, JSONException {
		Authentication testObject = new Authentication();
		DumpCreation dumpCreationObject = new DumpCreation();
		Scanner scannerObject = new Scanner(System.in);
		System.out.println(
				"Operations Available: 1. Registration 2. Authentication 3. DB Dump Creation 4. ERD Generation");
		System.out.println("Enter your choice ");
		int choice = scannerObject.nextInt();
		System.out.println("Enter the username");
		String username = scannerObject.next();
		System.out.println("Enter the password");
		String password = scannerObject.next();
		switch (choice) {
		case 1:
			testObject.register(username, password);
			break;
		case 2:
			testObject.authenticate(username, password, "queryInitialization");
			break;
		case 3:
			testObject.authenticate(username, password, "dumpcreation");
			break;
		case 4:
			testObject.authenticate(username, password, "ERDGeneration");
			break;
		default:
			System.out.println("invalid choice");
		}

//https://stackabuse.com/reading-and-writing-csvs-in-java/

	}
}

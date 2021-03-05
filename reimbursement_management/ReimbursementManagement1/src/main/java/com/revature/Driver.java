package com.revature;

import java.util.Scanner;

import javax.persistence.NoResultException;

import com.revature.model.Role;
import com.revature.repository.RoleRepositoryImpl;

public class Driver {
	
	public static void main(String[] args) {
		RoleRepositoryImpl roleRepository = new RoleRepositoryImpl();
		Role role = null;
		Scanner scanner = new Scanner(System.in);
		while(role == null) {
			System.out.println("Enter a valid role id number: ");
			String userInput = scanner.nextLine();
			try {
				int id = Integer.parseInt(userInput);
				role = roleRepository.findById(id);
				System.out.println(role);
			} catch(NumberFormatException e) {
				e.printStackTrace();
				System.out.println("Not a valid entry");
				continue;
			} catch(NullPointerException e) {
				e.printStackTrace();
				System.out.println("Not a valid entry");
				continue;
			} catch(NoResultException e) {
				e.printStackTrace();
				System.out.println("Not a valid entry");
				continue;
			}
		}
		System.out.println("Method terminating");
		scanner.close();
		
	}

}

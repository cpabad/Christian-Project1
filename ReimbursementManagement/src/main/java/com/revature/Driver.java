package com.revature;

import com.revature.service.UserService;

public class Driver {
	
	public static void main(String[] args) {
		new UserService().findById(1);
	}

}

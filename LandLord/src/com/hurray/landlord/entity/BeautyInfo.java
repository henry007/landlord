package com.hurray.landlord.entity;

public class BeautyInfo {
	private BeautyStatus status;
	private String name;
	
	public BeautyInfo(BeautyStatus status, String name) {
		this.status = status;
		this.name = name;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BeautyStatus getStatus() {
		return status;
	}

	public void setStatus(BeautyStatus status) {
		this.status = status;
	}
	
}

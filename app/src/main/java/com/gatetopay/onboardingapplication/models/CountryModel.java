package com.gatetopay.onboardingapplication.models;

import com.google.gson.annotations.SerializedName;

public class CountryModel{

	@SerializedName("image")
	private String image;

	@SerializedName("code")
	private String code;

	@SerializedName("nameAr")
	private String nameAr;

	@SerializedName("emoji")
	private String emoji;

	@SerializedName("dial_code")
	private String dialCode;

	@SerializedName("name")
	private String name;

	@SerializedName("unicode")
	private String unicode;

	public String getImage(){
		return image;
	}

	public String getCode(){
		return code;
	}

	public String getNameAr(){
		return nameAr;
	}

	public String getEmoji(){
		return emoji;
	}

	public String getDialCode(){
		return dialCode;
	}

	public String getName(){
		return name;
	}

	public String getUnicode(){
		return unicode;
	}
}
package com.amazon.asksdk.session;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Gab {
	public final String question;
	public final String answer;
	public Gab(String question, String answer) {
		this.question = question;
		this.answer = answer;
	}
	
	final static Pattern spaces = Pattern.compile("\\s+");
	public String questionPeriods() {
		return Arrays.stream(spaces.split(question)).collect(Collectors.joining(". "));
	}
	
}

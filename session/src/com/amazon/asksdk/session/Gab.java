package com.amazon.asksdk.session;

import com.amazon.speech.ui.SsmlOutputSpeech;

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
	public String questionPeriods(String speed){
		return "<prosody rate=\""+speed+"\">"+Arrays.stream(spaces.split(question)).collect(Collectors.joining(". "))+"</prosody>";
	}
	
	public String getEnrichedSpeech(int milisecondsBreakTime) {
		StringBuilder builder = new StringBuilder();
		builder.append("<emphasis level=\"reduced\">");
		String[] words = this.question.split(" ");
		for (String word : words) {
			builder.append(word);
			builder.append(String.format("<break time=\"%dms\"/>", milisecondsBreakTime));
		}
		builder.append("</emphasis>");

		return builder.toString();
	}
}

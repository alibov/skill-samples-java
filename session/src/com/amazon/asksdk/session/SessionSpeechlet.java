/**
    Copyright 2014-2015 Amazon.com, Inc. or its affiliates. All Rights Reserved.

    Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance with the License. A copy of the License is located at

        http://aws.amazon.com/apache2.0/

    or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions and limitations under the License.
 */
package com.amazon.asksdk.session;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.speech.slu.Intent;
import com.amazon.speech.slu.Slot;
import com.amazon.speech.speechlet.IntentRequest;
import com.amazon.speech.speechlet.LaunchRequest;
import com.amazon.speech.speechlet.Session;
import com.amazon.speech.speechlet.SessionEndedRequest;
import com.amazon.speech.speechlet.SessionStartedRequest;
import com.amazon.speech.speechlet.SpeechletV2;
import com.amazon.speech.speechlet.SpeechletResponse;
import com.amazon.speech.json.SpeechletRequestEnvelope;
import com.amazon.speech.ui.PlainTextOutputSpeech;
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazon.speech.ui.SsmlOutputSpeech;

/**
 * This sample shows how to create a simple speechlet for handling intent requests and managing
 * session interactions.
 */
public class SessionSpeechlet implements SpeechletV2 {
	

    private static final int REPEATMSECS = 10;




	public final Gab[] gabs = {new Gab("Ace Leap Lesson Height","A Sleepless Night"), 
    		new Gab("Ace Lie Soap Eye","A Slice of Pie"), 
    		new Gab("Ace Nose Dorm","A Snowstorm"), 
    		new Gab("Ace Pea Ding Tea Kit","A Speeding Ticket"), 
    		new Gab("Ache Hand He Eye Pull","A Candy Apple"), 
    		new Gab("Ache Hick Kin Tub Hut","A Kick in the Butt"), 
    		new Gab("Ache Hood Sin Sew Fume Her","A Good Sense of Humor"), 
    		new Gab("Agree Nap Hull","A Green Apple"), 
    		new Gab("Bat Tree Snot Ink Looted","Batteries not included"), 
    		new Gab("Backed Ooze Queer Won","Back to Square 1"),
    		new Gab("Buy Ass Fraction","Bias For Action"),
    		new Gab("Alex ash oh ping","Alexa Shopping"),
    		new Gab("custom err orb session","Customer obsession"),
    		new Gab("in cease ton thai ass stand darts ","Insist on highest standards"),
    		new Gab("own err ship","Ownership")};
    
    


    private static final Logger log = LoggerFactory.getLogger(SessionSpeechlet.class);

    private static final String GAB_INDEX = "GABINDEX";




    //private static final List<String> ANSWER_SLOTS = Arrays.asList("movieAnswer","answer","book","tvshow");

    @Override
    public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope) {
       // log.info("onSessionStarted requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
         //       requestEnvelope.getSession().getSessionId());
        // any initialization logic goes here
    }

    @Override
    public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> requestEnvelope) {
        //log.info("onLaunch requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
          //      requestEnvelope.getSession().getSessionId());
        return getWelcomeResponse(requestEnvelope.getSession());
    }

    @Override
    public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        IntentRequest request = requestEnvelope.getRequest();
        Session session = requestEnvelope.getSession();
        //log.info("onIntent requestId={}, sessionId={}", request.getRequestId(), session);

        // Get intent from the request object.
        Intent intent = request.getIntent();
        String intentName = (intent != null) ? intent.getName() : null;

        // Note: If the session is started with an intent, no welcome message will be rendered;
        // rather, the intent specific response will be returned.
        if ("AnswerIntent".equals(intentName)) {
            return handleAnswerIntent(intent, session);
        } else if ("DontKnowIntent".equals(intentName)) {
            return handleDontKnow(intent, session);
        } else if ("AMAZON.RepeatIntent".equals(intentName)) {
            return handleRepeat(intent, session);
        } else if ("AMAZON.StopIntent".equals(intentName)) {
        	return handleStop();
        } else if ("AMAZON.StartOverIntent".equals(intentName)) {
        	return getWelcomeResponse(session);
        }
        else if ("AMAZON.HelpIntent".equals(intentName)){
            return getHelpResponse(session);
        }
        else {
            String errorSpeech = intentName + " is unsupported.  Please try something else.";
            return getSpeechletResponse(errorSpeech, errorSpeech, true);
        }
    }

	private SpeechletResponse handleStop() {
		return getSpeechletResponse("Your Score is 0", null, false);
	}
    private SpeechletResponse getHelpResponse(Session session) {
        String speechText = 
                "I will ask you a mad gab puzzle, consisting of simple words. These words, when <w role=\"amazon:VBD\">read</w> out loud, make up a name of a movie, or an amazon leadership principle. \n" +
                "For example, for the puzzle: \"hay. reap. otter.\", the solution is: \"the movie Harry Potter\". \n" +
                "For the puzzle: \"Thud. Oven. Cheek. Ode.\", the solution is \"The Da-Vinci Code\" <break time=\"0.8s\"/> \n" +
                "You can say \"repeat\", \"go back\"";
        String repromptText = gabs[Integer.parseInt(session.getAttribute(GAB_INDEX).toString())].question;
        speechText += repromptText;
        return getSpeechletResponse(speechText, repromptText, true);
    }

    private SpeechletResponse handleRepeat(Intent intent, Session session) {
        String repromptText = gabs[Integer.parseInt(session.getAttribute(GAB_INDEX).toString())].getEnrichedSpeech(REPEATMSECS);
        String speechText = repromptText;
        return getSpeechletResponse(speechText, repromptText, true);
	}

	private SpeechletResponse handleDontKnow(Intent intent, Session session) {
        String repromptText = getGabText(session);
        String speechText = repromptText;
        return getSpeechletResponse(speechText, repromptText, true);
	}

	@Override
    public void onSessionEnded(SpeechletRequestEnvelope<SessionEndedRequest> requestEnvelope) {
        //log.info("onSessionEnded requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
          //      requestEnvelope.getSession().getSessionId());
    }
 
	public final Random r = new Random(306054743L);
	
    String getGabText(Session session){
		int gabIndex = r.nextInt(gabs.length);		
    	Gab gab  = gabs[gabIndex];
          session.setAttribute(GAB_INDEX, gabIndex);
        return "Here's your next puzzle:\n\r " + gab.questionPeriods() + "<break time=\"2s\"/>";
    	
    }
    
    /**
     * Creates and returns a {@code SpeechletResponse} with a welcome message.
     * @param session 
     *
     * @return SpeechletResponse spoken and visual welcome message
     */
    private SpeechletResponse getWelcomeResponse(Session session) {
        // Create the welcome message.
		
        String repromptText = getGabText(session);
        String speechText = "Welcome to Mad Gab, " +repromptText;
        return getSpeechletResponse(speechText, repromptText, true);
    }

    /**
     * Creates a {@code SpeechletResponse} for the intent and stores the extracted color in the
     * Session.
     *
     * @param intent
     *            intent for the request
     * @param dontknow 
     * @return SpeechletResponse spoken and visual response the given intent
     */
    private SpeechletResponse handleAnswerIntent(final Intent intent, final Session session) {
        // Get the slots from the intent.
        Map<String, Slot> slots = intent.getSlots();

        Slot answerSlot = slots.values().stream().filter(u-> u!= null && u.getValue() != null).findFirst().orElse(null);
        // Get the color slot from the list of slots.
        String speechText, repromptText;

        // Check for favorite color and create output to user.
        if (answerSlot != null) {
            // Store the user's favorite color in the Session and create response.
        	
            String answer = answerSlot.getValue();
            if("stop".equals(answer)) {
            	return handleStop();
            }
            
            if("help".equals(answer)) {
            	return getHelpResponse(session);
            }
            if(answer == null) {
            	answer = "nothing";
            }
            speechText = "";
            String correctAnswer = gabs[Integer.parseInt(session.getAttribute(GAB_INDEX).toString())].answer;
            String correctString = correctAnswer.replaceAll("//s+", "");
            String answerTrimmed = answer.replaceAll("//s+", "");
            
            
            if(minDistance(correctString,answerTrimmed) <=1) {
            	log.info("correct! received: " + answer+ " correct answer: " + correctAnswer);	
            	speechText += "Correct! ";
            } else {
            	log.info("wrong! received: " + answer+ ". correct answer: " + correctAnswer);
            	speechText += "Wrong! You said " +answer +". The answer was " + correctAnswer+". ";
            }
            
            
            repromptText = getGabText(session);
            
            speechText += repromptText;

        } else {
            speechText = "Try answering the puzzle";
            repromptText =
                    "Try answering the puzzle, you can skip this puzzle by saying skip";
        }

        return getSpeechletResponse(speechText, repromptText, true);
    }
    
    
    public static int minDistance(String word1, String word2) {
    	int len1 = word1.length();
    	int len2 = word2.length();
     
    	// len1+1, len2+1, because finally return dp[len1][len2]
    	int[][] dp = new int[len1 + 1][len2 + 1];
     
    	for (int i = 0; i <= len1; i++) {
    		dp[i][0] = i;
    	}
     
    	for (int j = 0; j <= len2; j++) {
    		dp[0][j] = j;
    	}
     
    	//iterate though, and check last char
    	for (int i = 0; i < len1; i++) {
    		char c1 = word1.charAt(i);
    		for (int j = 0; j < len2; j++) {
    			char c2 = word2.charAt(j);
     
    			//if last two chars equal
    			if (c1 == c2) {
    				//update dp value for +1 length
    				dp[i + 1][j + 1] = dp[i][j];
    			} else {
    				int replace = dp[i][j] + 1;
    				int insert = dp[i][j + 1] + 1;
    				int delete = dp[i + 1][j] + 1;
     
    				int min = replace > insert ? insert : replace;
    				min = delete > min ? min : delete;
    				dp[i + 1][j + 1] = min;
    			}
    		}
    	}
     
    	return dp[len1][len2];
    }


    /**
     * Returns a Speechlet response for a speech and reprompt text.
     */
    private SpeechletResponse getSpeechletResponse(String speechText, String repromptText,
            boolean isAskResponse) {
        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("Mad Gab");
        card.setContent(speechText.replaceAll("<[^>]*>", ""));

        // Create the plain text output.
        SsmlOutputSpeech speech = new SsmlOutputSpeech();
        speech.setSsml("<speak>" + speechText +"</speak>");

        if (isAskResponse) {
            // Create reprompt
        	SsmlOutputSpeech repromptSpeech = new SsmlOutputSpeech();
            repromptSpeech.setSsml("<speak>" + repromptText+"</speak>");
            Reprompt reprompt = new Reprompt();
            reprompt.setOutputSpeech(repromptSpeech);

            return SpeechletResponse.newAskResponse(speech, reprompt, card);

        } else {
            return SpeechletResponse.newTellResponse(speech, card);
        }
    }
    
}

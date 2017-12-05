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

/**
 * This sample shows how to create a simple speechlet for handling intent requests and managing
 * session interactions.
 */
public class SessionSpeechlet implements SpeechletV2 {
	

    public final Gab[] gabs = {new Gab("Ace Leap Lesson Height","A Sleepless Night"), new Gab("Ace Lie Soap Eye","A Slice of Pie"), new Gab("Ace Nose Dorm","A Snowstorm"), new Gab("Ace Pea Ding Tea Kit","A Speeding Ticket"), new Gab("Ache Hand He Eye Pull","A Candy Apple"), new Gab("Ache Hick Kin Tub Hut","A Kick in the Butt"), new Gab("Ache Hood Sin Sew Fume Her","A Good Sense of Humor"), new Gab("Agree Nap Hull","A Green Apple"), new Gab("Bat Tree Snot Ink Looted","Batteries not included"), new Gab("Backed Ooze Queer Won","Back to Square One")};

    private static final Logger log = LoggerFactory.getLogger(SessionSpeechlet.class);

    private static final String QUESTION_KEY = "QUESTION";
    private static final String ANSWER_KEY = "ANSWER";
    private static final List<String> ANSWER_SLOTS = Arrays.asList("movieAnswer","answer","book","tvshow");

    @Override
    public void onSessionStarted(SpeechletRequestEnvelope<SessionStartedRequest> requestEnvelope) {
        log.info("onSessionStarted requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
                requestEnvelope.getSession().getSessionId());
        // any initialization logic goes here
    }

    @Override
    public SpeechletResponse onLaunch(SpeechletRequestEnvelope<LaunchRequest> requestEnvelope) {
        log.info("onLaunch requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
                requestEnvelope.getSession().getSessionId());
        return getWelcomeResponse(requestEnvelope.getSession());
    }

    @Override
    public SpeechletResponse onIntent(SpeechletRequestEnvelope<IntentRequest> requestEnvelope) {
        IntentRequest request = requestEnvelope.getRequest();
        Session session = requestEnvelope.getSession();
        log.info("onIntent requestId={}, sessionId={}", request.getRequestId(), session);

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
        	return getSpeechletResponse("Your Score is 0", null, false);
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

    private SpeechletResponse getHelpResponse(Session session) {
        String speechText = "<speak>\n" +
                "I will ask you a mad gab puzzle, consisting of simple words. These words, when <w role=\"amazon:VBD\">read</w> out loud, make up a name of a movie, or an amazon leadership principle. \n" +
                "For example, for the puzzle: \"hay. reap. otter.\", the solution is: \"the movie Harry Potter\". \n" +
                "For the puzzle: \"Thud. Oven. Cheek. Ode.\", the solution is \"The Da-Vinci Code\" <break time=\"0.8s\"/> \n" +
                "You can say \"repeat\", \"go back\"</speak>\n";
        return getSpeechletResponse(speechText, speechText, true);
    }

    private SpeechletResponse handleRepeat(Intent intent, Session session) {
        String repromptText = session.getAttribute(QUESTION_KEY).toString();
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
        log.info("onSessionEnded requestId={}, sessionId={}", requestEnvelope.getRequest().getRequestId(),
                requestEnvelope.getSession().getSessionId());
    }
 
	public final Random r = new Random(306054743L);
	
    Gab getGab(){
		return gabs[r.nextInt(gabs.length)];
    }
    
    
	String getGabText(Session session){
    	Gab gab  = getGab();
    	  session.setAttribute(QUESTION_KEY, gab.questionPeriods());
          session.setAttribute(ANSWER_KEY, gab.answer);
        return "Here's your next puzzle:\n\r " + gab.questionPeriods();
    	
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
            speechText = "";
            if(answer != null && answer.replaceAll("//s+", "").equalsIgnoreCase(session.getAttribute(ANSWER_KEY).toString().replaceAll("//s+", ""))) {
            	speechText += "Correct! ";
            } else {
            	speechText += "Wrong! The answer was " + session.getAttribute(ANSWER_KEY).toString()+". ";
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


    /**
     * Returns a Speechlet response for a speech and reprompt text.
     */
    private SpeechletResponse getSpeechletResponse(String speechText, String repromptText,
            boolean isAskResponse) {
        // Create the Simple card content.
        SimpleCard card = new SimpleCard();
        card.setTitle("Session");
        card.setContent(speechText);

        // Create the plain text output.
        PlainTextOutputSpeech speech = new PlainTextOutputSpeech();
        speech.setText(speechText);

        if (isAskResponse) {
            // Create reprompt
            PlainTextOutputSpeech repromptSpeech = new PlainTextOutputSpeech();
            repromptSpeech.setText(repromptText);
            Reprompt reprompt = new Reprompt();
            reprompt.setOutputSpeech(repromptSpeech);

            return SpeechletResponse.newAskResponse(speech, reprompt, card);

        } else {
            return SpeechletResponse.newTellResponse(speech, card);
        }
    }
}

package com.amazon.asksdk.session;

import java.util.Map;
import java.util.Random;

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
import com.amazon.speech.ui.Reprompt;
import com.amazon.speech.ui.SimpleCard;
import com.amazon.speech.ui.SsmlOutputSpeech;

/**
 * This sample shows how to create a simple speechlet for handling intent requests and managing
 * session interactions.
 */
public class SessionSpeechlet implements SpeechletV2 {



	public final String[] speed = {"medium","fast","x-fast"};
	
	public final String[] success = {"huzzah", "all righty","bam","bada bing bada boom","bazinga","bingo","bravo","checkmate","eureka","hip hip hooray","hurrah","hurray","kaboom","ta da","whee","yay"};
	public final String[] failure = {"argh","duh","uh oh","blah","boo","darn","honk","no way","ouch"};
	public final String[] finish = {"mazel tov","well done","bravo"};

	
	//histor the block -> histablock

	///write a crew - > white echo

	//electric rises - > electric razors


	
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
			new Gab("in cease ton thai ass stand darts ","Insist on the highest standards"),
			new Gab("own err ship","Ownership")};




	private static final Logger log = LoggerFactory.getLogger(SessionSpeechlet.class);

	private static final String GAB_INDEX = "GABINDEX";
	private static final String SPEED_INDEX = "SPEEDINDEX";




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
			return handleRepeat(session);
		} else if ("AMAZON.StopIntent".equals(intentName)) {
			return handleStop(session);
		} else if ("AMAZON.StartOverIntent".equals(intentName)) {
			return getWelcomeResponse(session);
		} else if ("AMAZON.HelpIntent".equals(intentName)){
			return getHelpResponse(session);
		} else {
			String errorSpeech = intentName + " is unsupported.  Please try something else.";
			return getSpeechletResponse(errorSpeech, errorSpeech, true);
		}
	}

	private SpeechletResponse handleStop(Session session) {
		Map<String, Object> attributes = session.getAttributes();
		
		
		Integer played = (Integer) attributes.getOrDefault("Played", 0);
		Integer correct = (Integer) attributes.getOrDefault("Correct", 0);

		return getSpeechletResponse(String.format("%s! Your Score is %d out of %d", interject(finish[r.nextInt(finish.length)]), correct,played), null, false);
	}
	private SpeechletResponse getHelpResponse(Session session) {
		String speechText = 
				"I will ask you a mad gab puzzle, consisting of simple words. These words, when <w role=\"amazon:VBD\">read</w> out loud, make up a name of a movie, or an amazon leadership principle. \n" +
						"For example, for the puzzle: \"hay. reap. otter.\", the solution is: \"the movie Harry Potter\". \n" +
						"For the puzzle: \"Thud. Oven. Cheek. Ode.\", the solution is \"The Da-Vinci Code\" <break time=\"0.8s\"/> \n" +
						"You can say \"repeat\", \"go back\"";
		String repromptText = gabs[Integer.parseInt(session.getAttribute(GAB_INDEX).toString())].questionPeriods(speed[(int) session.getAttribute(SPEED_INDEX)],(int) session.getAttribute(SPEED_INDEX));
		speechText += repromptText;
		return getSpeechletResponse(speechText, repromptText, true);
	}

	private SpeechletResponse handleRepeat(Session session) {
		//String repromptText = gabs[Integer.parseInt(session.getAttribute(GAB_INDEX).toString())].getEnrichedSpeech(REPEATMSECS);
		String repromptText = gabs[Integer.parseInt(session.getAttribute(GAB_INDEX).toString())].questionPeriods(speed[(int) session.getAttribute(SPEED_INDEX)],(int) session.getAttribute(SPEED_INDEX));
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
		int gabIndex = (Integer)session.getAttribute(GAB_INDEX)+1;	
		Gab gab  = gabs[gabIndex];
		session.setAttribute(GAB_INDEX, gabIndex);
		session.setAttribute(SPEED_INDEX, 0);
		return "Here's your next puzzle:\n\r " + gab.questionPeriods(speed[(int) session.getAttribute(SPEED_INDEX)],(int) session.getAttribute(SPEED_INDEX)) + "<break time=\"2s\"/>";

	}

	/**
	 * Creates and returns a {@code SpeechletResponse} with a welcome message.
	 * @param session 
	 *
	 * @return SpeechletResponse spoken and visual welcome message
	 */
	private SpeechletResponse getWelcomeResponse(Session session) {
		// Create the welcome message.
		session.setAttribute("Played",0);
		session.setAttribute("Correct",0);
		session.setAttribute(GAB_INDEX,-1);

		String repromptText = getGabText(session);
		String speechText = "Welcome to Mad Gab, " +repromptText;
		return getSpeechletResponse(speechText, repromptText, true);
	}

	
	
	public static String interject(String in) {
		return "<say-as interpret-as=\"interjection\">"+in+"</say-as>";
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
				return handleStop(session);
			}

			if("help".equals(answer)) {
				return getHelpResponse(session);
			}

			if("faster".equals(answer)) {
				return getFasterResponse(session);
			}
			
			if("next".equals(answer)) {
				return handleDontKnow(intent, session);
			}
			

			if(answer == null) {
				answer = "nothing";
			}
			speechText = "";
			String correctAnswer = gabs[Integer.parseInt(session.getAttribute(GAB_INDEX).toString())].answer;
			String correctString = correctAnswer.replaceAll("\\s+", "");
			String answerTrimmed = answer.replaceAll("\\s+", "");

			session.setAttribute("Played",  (Integer)session.getAttribute("Played") + 1);

			if(answerTrimmed.equalsIgnoreCase(correctString) || answerTrimmed.equalsIgnoreCase(correctString.substring(1)) || correctString.equalsIgnoreCase(answerTrimmed.substring(1))) {
				session.setAttribute("Correct", (Integer)session.getAttribute("Correct") + 1);
				log.info("Correct! received: " + answer+ " correct answer: " + correctAnswer);	
				speechText += interject(success[r.nextInt(success.length)])+"! ";
			} else {
				log.info("wrong! received: " + answer+ ". correct answer: " + correctAnswer);
				log.info("answerTrimmed:"+ answerTrimmed);
				log.info("correctString:"+ correctString);
				log.info("correctString.substring(1):"+ correctString.substring(1));
				log.info("answerTrimmed.substring(1):"+ answerTrimmed.substring(1));
				speechText += interject(failure[r.nextInt(failure.length)])+"! You said " +answer +". The answer was " + correctAnswer+". ";
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


	private SpeechletResponse getFasterResponse(Session session) {
		int speed = (int) session.getAttribute(SPEED_INDEX);
		if(speed <2) {
			speed++;
		}
		session.setAttribute(SPEED_INDEX, speed );
		return handleRepeat(session);
	}

	/**
	 * Returns a Speechlet response for a speech and reprompt text.
	 */
	private SpeechletResponse getSpeechletResponse(String speechText, String repromptText,
			boolean isAskResponse) {
		// Create the Simple card content.
		SimpleCard card = new SimpleCard();
		card.setTitle("Mad Gab");
		card.setContent(speechText.replaceAll("<[^>]*>", " ").trim());

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

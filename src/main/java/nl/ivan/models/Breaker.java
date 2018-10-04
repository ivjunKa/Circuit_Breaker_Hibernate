package nl.ivan.models;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nl.ivan.interfaces.IErrorCollector;

public class Breaker {
	//Deprecated, use nl.ivan.persistence.Breaker as Breaker model
	private Method fallbackFunction;
	private HashMap<IErrorCollector, ErrorCollector> errCol = new HashMap<>();
	private enum States {
		OPEN, CLOSED, HALFOPEN
	};
	private States currentState = null;
	private String protectedFunctionName = null;
	private String prefix = null;
	private String status = null;
	
	public Breaker(Method fallbackFunction) {
		// might be interesting to have one fallback per one errorCollector
		this.fallbackFunction = fallbackFunction;
		//how to get state?
		currentState = States.CLOSED;		
	}
	public void runProtected(Method m, Object invokingObject) {
		protectedFunctionName = m.getName();
		prefix = m.getDeclaringClass().toString();
		
		switch (currentState) {
		case OPEN:
			System.out.println("Not executed!");
			try {
				fallbackFunction.invoke(invokingObject);
			} catch (IllegalAccessException e1) {
				e1.printStackTrace();
			} catch (IllegalArgumentException e1) {
				e1.printStackTrace();
			} catch (InvocationTargetException e1) {
				e1.printStackTrace();
			}
			return;
		case CLOSED:
			try {
				Object res = m.invoke(invokingObject);
				//test responce some error message that was previously declared in ErrorCollector
				Object res2 = new String("Whoops");
				for(int i=0; i<3; i++) {
					for(Entry<IErrorCollector, ErrorCollector> ec : errCol.entrySet()) {
						if(ec.getKey().errPatternMatcher(res2) && ec.getValue().checkIfThresholdIsReached()) {
							this.currentState = States.OPEN;
							System.out.println("Breaker is triggered!");
						}
					}
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
			}
			break;
		case HALFOPEN:
			break;
		}
	}
	public void populateBreakersMap(IErrorCollector actualLogic, ErrorCollector state) {
		errCol.put(actualLogic, state);
	}
	public String getCurrentState() {
		return currentState.name();
	}
	public void setCurrentState(States currentState) {
		this.currentState = currentState;
	}
	public String getProtectedFunctionName() {
		return protectedFunctionName;
	}
	public void setProtectedFunctionName(String protectedFunctionName) {
		this.protectedFunctionName = protectedFunctionName;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public boolean checkIfIGotSomePreviousState() {
		return false;
	}
}

package nl.ivan.persistence;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Observable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import nl.ivan.interfaces.IBreakerStateListener;
import nl.ivan.interfaces.IErrorCollector;
import nl.ivan.models.ErrorCollector;

public class Breaker extends Observable {
	private long id;
	private String state;
	private String functionWrapped;
	private String prefix;
	private String status;

	public static String testState = "CLOSED";

	private Method fallbackFunction;
	private HashMap<IErrorCollector, ErrorCollector> errCol = new HashMap<>();

	private IBreakerStateListener crudListener;

	private enum States {
		OPEN, CLOSED, HALFOPEN
	};

	private States currentState = States.CLOSED;

	public Breaker() {
	}

	// TODO implement status getter from the database
	public Breaker(Method fallbackFunction) {
		setState(currentState.name());
		// indicates that breaker is not commented out and using the same function for
		// runProtected method
		setStatus("checked_in");
	}

	public Breaker(String functionWrapped, String fallbackFunction, String prefix) {
		this.functionWrapped = functionWrapped;
		this.state = currentState.name();
		this.status = "checked_in";
		this.prefix = prefix;
	}
//Deprecated - use runProtectedFunction instead
//	public void runProtected(Method m, Object invokingObject) {
//		functionWrapped = m.getName();
//		prefix = m.getDeclaringClass().toString();
//		switch (currentState) {
//		case OPEN:
//			System.out.println("Not executed!");
//			try {
//				fallbackFunction.invoke(invokingObject);
//			} catch (IllegalAccessException e1) {
//				e1.printStackTrace();
//			} catch (IllegalArgumentException e1) {
//				e1.printStackTrace();
//			} catch (InvocationTargetException e1) {
//				e1.printStackTrace();
//			}
//			return;
//		case CLOSED:
//			try {
//				Object res = m.invoke(invokingObject);
//				// test responce some error message that was declared in ErrorCollector
//				Object res2 = new String("Whoops");
//				for (int i = 0; i < 3; i++) {
//					for (Entry<IErrorCollector, ErrorCollector> ec : errCol.entrySet()) {
//						if (ec.getKey().errPatternMatcher(res2) && ec.getValue().checkIfThresholdIsReached()) {
//							// each state change needs to be persisted
//							this.currentState = States.OPEN;
//							setState(currentState.name());
//						}
//					}
//				}
//			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
//				e.printStackTrace();
//			}
//			break;
//		case HALFOPEN:
//			break;
//		}
//	}

	public void runProtectedFunction(Object caller) {
		Method protectedFoo = null;
		Method fallbackFoo = null;
		try {
			protectedFoo = caller.getClass().getMethod(getFunctionWrapped());
		} catch (NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		switch (currentState) {
		case OPEN:
			System.out.println("Not executed!");
			return;
		case CLOSED:
			try {
				Object res = protectedFoo.invoke(caller);
				// test responce some error message that was declared in ErrorCollector
				Object res2 = new String("Whoops");
				for (int i = 0; i < 3; i++) {
					for (Entry<IErrorCollector, ErrorCollector> ec : errCol.entrySet()) {
						if (ec.getKey().errPatternMatcher(res2) && ec.getValue().checkIfThresholdIsReached()) {
							// each state change needs to be persisted
							this.currentState = States.OPEN;
							setState(currentState.name());
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

	// ----------------------------------------------------------------------
	// ----Getters and setters part------------------------------------------
	// ----------------------------------------------------------------------
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
		setChanged();
		notifyObservers();
	}

	public String getFunctionWrapped() {
		return functionWrapped;
	}

	public void setFunctionWrapped(String functionWrapped) {
		this.functionWrapped = functionWrapped;
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

}

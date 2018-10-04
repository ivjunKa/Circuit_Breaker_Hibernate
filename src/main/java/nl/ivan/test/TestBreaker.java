package nl.ivan.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import nl.ivan.interfaces.IErrorCollector;
//import nl.ivan.models.Breaker;
import nl.ivan.models.ErrorCollector;
import nl.ivan.persistence.Breaker_CRUD;
import nl.ivan.persistence.Breaker;
public class TestBreaker {

	public String someCall() {
		System.out.println("REST call");
		return "OK";
	}
	public String someOtherCall() {
		System.out.println("Other REST call");
		return "OK";
	}
	public String thirdCall() {
		System.out.println("Third REST call");
		return "OK";
	}
	public void fallbackFoo() {
		System.out.println("Fallback function");
	}

	public static void main(String[] args) {
		TestBreaker tb = new TestBreaker();
		Breaker_CRUD breaker_crud = new Breaker_CRUD();
		Breaker cb = new Breaker("someCall", "fallbackFoo", tb.getClass().getName().toString());
		breaker_crud.registerBreakerListener(cb);
		
		Breaker othercb = new Breaker("thirdCall", "fallbackFoo", tb.getClass().getName().toString());
		breaker_crud.registerBreakerListener(othercb);
		
		breaker_crud.cleanupBreakers();
		cb.runProtectedFunction(tb);
		othercb.runProtectedFunction(tb);
// Define and add ErrorCollector
//		IErrorCollector ec = (o)-> {
//		System.out.println("Checking for possible error scenario " + o);
//		return (o.toString().equals("Whoops")) ? true : false;
//	};
//	ErrorCollector ecS = new ErrorCollector(3);
//		cb.populateBreakersMap(ec,ecS);
		
		
		
//-------Deprecated----------
//		Breaker cb = null;
//		Breaker cb1 = null;		
//		try {
//			cb = new Breaker(tb.getClass().getMethod("fallbackFoo"));			
//			cb.populateBreakersMap(ec,ecS);
//			cb1 = new Breaker(tb.getClass().getMethod("fallbackFoo"));
//			
//		} catch (NoSuchMethodException | SecurityException e1) {
//			e1.printStackTrace();
//		}
//		try {
//			cb.runProtected(tb.getClass().getMethod("someCall"), tb);
//			cb1.runProtected(tb.getClass().getMethod("thirdCall"), tb);
//			
//		} catch (NoSuchMethodException | SecurityException e) {
//			e.printStackTrace();
//		}
//		//we need to runProtected at least once before insert to make sure that breaker receives 'functionwrapped' state
//		try {
//			breaker_crud.insert(cb);
//			breaker_crud.insert(cb1);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		//this cleans up non-active breakers (i.e. that were commented out) or updating 'functionwrapped' state
//
//		//		breaker_crud.registerBreakerListener(cb);
//		breaker_crud.cleanupBreakers();
	}
	

}

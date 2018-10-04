package nl.ivan.models;

import java.lang.reflect.Method;

import nl.ivan.interfaces.IErrorCollector;

public class ErrorCollector {
	private int errCounter;
	private int errThreshold;
	private boolean errFounded;
	public ErrorCollector(int errThreshold) {
		// TODO Auto-generated constructor stub
		this.errCounter = 0;
		this.errThreshold = errThreshold;
	}
	public boolean checkIfThresholdIsReached() {
		errCounter++;
		return (errCounter>=errThreshold) ? true : false;
	}
}

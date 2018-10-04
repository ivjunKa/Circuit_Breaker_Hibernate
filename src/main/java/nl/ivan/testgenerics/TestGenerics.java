package nl.ivan.testgenerics;
import java.util.ArrayList;
import java.util.List;

import nl.ivan.models.*;
public class TestGenerics {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<Money> l = new ArrayList<>();		
		Generics<Money> gen = new Generics<>();
//		Generics<Gold> g = new Generics<>();
		Money m = new Money();
		Gold g = new Gold();
		l.add(m);
//		l.add(g);
//		gen.setGoods(m);
//		gen.setGoods(g);
	}

}

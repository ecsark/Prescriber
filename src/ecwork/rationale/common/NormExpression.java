package ecwork.rationale.common;

import java.util.HashSet;
import java.util.Iterator;


public class NormExpression<E> {
	
	public HashSet<NormClause<E>> clauses;
	
	public NormExpression(){
		clauses = new HashSet<NormClause<E>>();		
	}
	
	public NormExpression(HashSet<NormClause<E>> p){
		this.clauses = p;
	}
	
	public boolean addClause(NormClause<E> npe){
		return this.clauses.add(npe);		
	}
	
	
	public void union(NormExpression<E> c2){
		if(c2!=null)
			this.clauses.addAll(c2.clauses);
		else
			warning("Unsafe union operation with an uninitialized NormExpression");
	}
	
	public boolean intersect(NormExpression<E> n2){		
		if(n2==null)
			return false;
		
		for(NormClause<E> p1 : clauses){
			for(NormClause<E> p2 : n2.clauses){
				NormClause<E> cp = new NormClause<E>();
				cp.terms.addAll(p1.terms);
				cp.terms.addAll(p2.terms);
				clauses.add(cp);
			}
			clauses.remove(p1);
		}
		return true;
	}
	
	//this ^ (~other)
	public void eliminate(NormExpression<E> other){
		if(other == null){
			warning("Unsafe eliminate operation with an uninitialized NormExpression");
			return;
		}
		for(NormClause<E> op : other.clauses){
			for(NormClause<E> tp :clauses){
				if(op.isSubset(tp))
					clauses.remove(tp);
			}
		}		
	}
	
	//to be override in subclasses
	public void atomDisplay(E e){
		System.out.print(e);
	}
	
	public void display(){
		System.out.print("{");
		for(Iterator<NormClause<E>> j = clauses.iterator();j.hasNext();){
			NormClause<E> p = j.next();
			System.out.print("{");			
			
			for(Iterator<E> i = p.terms.iterator();i.hasNext();){
				atomDisplay((E)i.next());
				if(i.hasNext())
					System.out.print(",");
			}
			System.out.print("}");
			if(j.hasNext())
				System.out.print(",");
		}
		System.out.println("}");
	}
	
	private void warning(String s){
		System.err.println("Warning: "+s);
	}
}

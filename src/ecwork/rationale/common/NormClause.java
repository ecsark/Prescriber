package ecwork.rationale.common;

import java.util.HashSet;

public class NormClause<E> {
	
	public HashSet<E> terms;
	
	public boolean addTerm(E e){
		return terms.add(e);
	}
	
	public NormClause(){
		terms = new HashSet<E>();
	}
	
	public NormClause(HashSet<E> e){
		terms = e;
	}
	

	public boolean isSubset(NormClause<E> other){
		if(this.terms.size()>other.terms.size())
			return false;
		
		for(E e : this.terms){
			if(!other.terms.contains(e))
				return false;
		}
		
		return true;
	}
	
	@SuppressWarnings("unchecked")
	@Override
    public boolean equals(Object o) {
		NormClause<E> otherPart = (NormClause<E>)o;
		
		if(otherPart.terms.size()!=this.terms.size())
			return false;
		
		for(E atom : otherPart.terms)
			if(!terms.contains(atom))
				return false;
			
        return true;
    }
}

package ecwork.rationale;

import ecwork.rationale.common.NormExpression;


public class Condition extends NormExpression<Reason>{
	@Override
	public void atomDisplay(Reason r){
		System.out.print(r.text+":"+r.value);
	}
	
}

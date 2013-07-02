package ecwork.rationale;

import ecwork.rationale.common.*;


public class DrugSelection extends NormExpression<Drug> {
	
	@Override
	public void atomDisplay(Drug d){
		System.out.print(d.name);
	}
}




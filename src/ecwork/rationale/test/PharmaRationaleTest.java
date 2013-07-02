package ecwork.rationale.test;

//import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import ecwork.rationale.*;

public class PharmaRationaleTest {

	PharmaRationale rationale;
	
	@Before
	public void setUp() throws Exception {
		File drugFile = new File("/home/ecsark/Projects/Prescriber/src/Drugs.xml");
		File reasonFile = new File("/home/ecsark/Projects/Prescriber/src/Pharmacology.xml");
		if(!(drugFile.exists()||reasonFile.exists())){
			System.err.println("Files not found!");
			return;
		}
			
		rationale = new PharmaRationale(reasonFile, drugFile);
	}

	@Test
	public void test() {
		Reason reason = new Reason("BloodPressure",-1);
		ConditionPart cp = new ConditionPart();
		cp.addTerm(reason);
		Condition c1 = new Condition();
		c1.addClause(cp);
		Condition condition = rationale.getPremises(c1);
		condition.display();
		DrugSelection drugSelection  = rationale.getDrugSelection(condition);
		drugSelection.display();
		
	}

}

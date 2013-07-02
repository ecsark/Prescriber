package ecwork.rationale;

import java.io.File;
import java.io.IOException;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import ecwork.rationale.common.NormClause;


public class PharmaRationale {
	
	private Document pharmacology;
	
	private NodeList conclusionNodes;
	
	private Document drugs;
	
	private NodeList drugActionNodes;
	
	public PharmaRationale(File reasonFile, File drugFile){
		
		DocumentBuilderFactory DBF = DocumentBuilderFactory.newInstance();
		DocumentBuilder BD;
		try {
			BD = DBF.newDocumentBuilder();
			pharmacology = BD.parse(reasonFile);
			conclusionNodes = pharmacology.getElementsByTagName("conclusion");
			drugs = BD.parse(drugFile);
			drugActionNodes = drugs.getElementsByTagName("action");
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private ArrayList<Drug> getDrugByAction(Reason reason){
		ArrayList<Drug> drugs = new ArrayList<Drug>();
		for(int i=0; i<drugActionNodes.getLength(); ++i){
			Node drugNode = drugActionNodes.item(i);
			if(Float.parseFloat(drugNode.getAttributes().getNamedItem("truth").getTextContent())*reason.value > 0){
				Drug d = new Drug(drugNode.getParentNode().getAttributes().getNamedItem("name").getTextContent());
				drugs.add(d);
			}	
		}
		return drugs;
	}
	
	
	public DrugSelection getDrugSelection(Condition condition){
		DrugSelection selection = new DrugSelection();
		for(NormClause<Reason> cp : condition.clauses){
			DrugSelection dSelection = null;
			for(Reason r : cp.terms){
				DrugSelection ds= new DrugSelection();
				ArrayList<Drug> drugs = getDrugByAction(r);
				for(Drug d : drugs){
					NormClause<Drug> dsp = new DrugSelectionPart();
					dsp.addTerm(d);
					ds.addClause(dsp);					
				}
				if(dSelection == null)
					dSelection = ds;
				else
					dSelection.intersect(ds);
				
				
			}
			selection.union(dSelection);
		}
		return selection;
	}
	
	
	/*public Condition getConclusions(Condition condition){
		
	}*/
	
	
 	private ArrayList<Node> getConclusionNodeListByName(NodeList nodeList, String name){
		ArrayList<Node> conclusionNodeList = new ArrayList<Node>();
		for(int i=0; i<nodeList.getLength(); ++i){
			if(nodeList.item(i).getTextContent().equals(name))
				conclusionNodeList.add(nodeList.item(i));
		}
		return conclusionNodeList;
	}
	
 	public Condition getPremises(Condition condition){
 		Condition premises = new Condition();
 		for(NormClause<Reason> np : condition.clauses){
 			Condition nCond = null;
 			for(Reason r : np.terms){
 				if(nCond == null)
 					nCond = getPremises(r);
 				else
 					nCond.intersect(getPremises(r));
 			}
 			premises.union(nCond);
 		}
 		return premises;
 	}
	
	public Condition getPremises(Reason reason){
		
		ArrayList<Node> conclusionNodeList = getConclusionNodeListByName(conclusionNodes, reason.text);
		
		String premiseName = "premise";
		
		Condition condition = new Condition();
		
		if(conclusionNodeList.size()==0){
			ConditionPart cp = new ConditionPart();
			cp.addTerm(reason);
			condition.addClause(cp);
			return condition;
		}		
		
		for(Node node : conclusionNodeList){
			
			String[] truthThen = node.getAttributes().getNamedItem("truth").getTextContent().split(",");
			ArrayList<Integer> positions = new ArrayList<Integer>();
			for(int i=0; i<truthThen.length;++i){
				if(Float.parseFloat(truthThen[i])*reason.value>0)
					positions.add(i);
			}
			
			Condition cond = null;
			for(int iter : positions){
				NodeList premisesNL = node.getParentNode().getChildNodes();
				
				for(int j=0; j<premisesNL.getLength(); ++j){
					Node prem = premisesNL.item(j);
					if(prem.getNodeName().equals(premiseName)){					
						float trt = Float.parseFloat(prem.getAttributes().getNamedItem("truth")
								.getTextContent().split(",")[iter]);
						float val = trt*Math.abs(reason.value);
						Reason r = new Reason(prem.getTextContent(),val);
						
						Condition nCondition = getPremises(r);
						
						if(cond == null) //an empty condition
							cond = nCondition;
						else							
							cond.intersect(nCondition);						
					}
				}
			}
			condition.union(cond);
		}
		
		return condition;		
	}
}

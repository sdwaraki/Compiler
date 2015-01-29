import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Stack;
import java.util.Vector;


public class Semantic 
{
	
	LinkedHashMap<String,Vector<SymbolTable>> symtab;
	
	Stack <String> s;
	String scope;
	
	public static final int integer = 0;
	public static final int bool = 1;
	
	public static final int op_plus = 0;
	public static final int op_minus = 1;
	public static final int op_mult = 2;
	public static final int op_div = 3;
	public static final int less_than = 4;
	public static final int assign = 5;
	public static final int greater_than = 6;
	public static final int not_equal = 7;
	public static final int equals =8;
		
	String cube[][][];
	
	Semantic()
	{
		cube = new String[9][2][2];
		initializeCube(cube);
		symtab = new LinkedHashMap<String,Vector<SymbolTable>>();
		s=new Stack<String>();
	}
	
		
	
	public void initializeCube(String [][][]c)
	{
		cube[op_plus][integer][integer] = "integer";
		cube[op_plus][integer][bool] = "X";
		cube[op_plus][bool][bool]="X";
		cube[op_plus][bool][integer]="X";
		
		cube[op_minus][integer][integer] = "integer";
		cube[op_minus][integer][bool] = "X";
		cube[op_minus][bool][bool]="X";
		cube[op_minus][bool][integer]="X";
		
		cube[op_mult][integer][integer] = "integer";
		cube[op_mult][integer][bool] = "X";
		cube[op_mult][bool][bool]="X";
		cube[op_mult][bool][integer]="X";
		
		cube[op_div][integer][integer] = "integer";
		cube[op_div][integer][bool] = "X";
		cube[op_div][bool][bool]="X";
		cube[op_div][bool][integer]="X";

		cube[less_than][integer][integer] = "bool";
		cube[less_than][integer][bool] = "X";
		cube[less_than][bool][bool]="X";
		cube[less_than][bool][integer]="X";
		
		cube[greater_than][integer][integer] = "bool";
		cube[greater_than][integer][bool] = "X";
		cube[greater_than][bool][bool]="X";
		cube[greater_than][bool][integer]="X";
		
		cube[assign][integer][integer] = "integer";
		cube[assign][integer][bool] = "X";
		cube[assign][bool][bool]="bool";
		cube[assign][bool][integer]="X";
		
		cube[not_equal][integer][integer] = "bool";
		cube[not_equal][integer][bool] = "X";
		cube[not_equal][bool][bool]="bool";
		cube[not_equal][bool][integer]="X";
		
		cube[equals][integer][integer] = "bool";
		cube[equals][integer][bool] = "X";
		cube[equals][bool][bool]="bool";
		cube[equals][bool][integer]="X";
		
			
	}
	
	public void insertSymbolTable(String type, String id, String scope)
	{
		SymbolTable s = new SymbolTable();
		s.setScope(scope);
		s.setType(type);
		int i=0;
		if(symtab.containsKey(id))
		{
			Vector<SymbolTable> temp = symtab.get(id);
			temp.add(s);
			symtab.put(id, temp);
		}
		else
		{
			Vector<SymbolTable> temp = new Vector<SymbolTable>();
			temp.add(s);
			symtab.put(id, temp);
		}
	}
	
	public void insertSymbolTable(String type,String id,String scope,int value)
	{
		SymbolTable s = new SymbolTable();
		s.setScope(scope);
		s.setType(type);
		s.setValue(value);
		Vector<SymbolTable> temp = new Vector<SymbolTable>();
		temp.add(s);
		symtab.put(id, temp);
	}
	
	
	public boolean searchSymbolTable(String id)
	{
		if(symtab.containsKey(id))
			return true;
		else
			return false;
	}
	
	public boolean isBoolean()
	{
		String temp = s.pop();
		if(temp.equals("bool"))
			return true;
		else
			return false;
	}
	
	public boolean isTypeMatching(String operator)
	{
		String temp1 = s.pop();
		String temp2 = s.pop();
		
		int temp1Number = returnTypeNumber(temp1);
		int temp2Number = returnTypeNumber(temp2);
		int opNumber = returnOperatorNumber(operator);
		
		if(temp1Number==-1||temp2Number==-1)
			return false;
		
		
		String result = cube[opNumber][temp1Number][temp2Number];
		
		if(result.equals("integer")||result.equals("bool"))
			return true;
		else
			return false;
	}
	
	
	public int returnTypeNumber(String t)
	{
		if(t.equals("integer"))
			return integer;
		if(t.equals("boolean"))
			return bool;
		else
			return -1;
	}
	
	public int returnOperatorNumber(String operator)
	{
		if(operator.equals("="))
			return assign;
		else if (operator.equals("!="))
			return not_equal;
		else if (operator.equals("+"))
			return op_plus;
		else if (operator.equals("-"))
			return op_minus;
		else if (operator.equals("/"))
			return op_div;
		else if (operator.equals(">"))
			return greater_than;
		else if(operator.equals("<"))
			return less_than;
		else if (operator.equals("*"))
			return op_mult;
		else if(operator.equals("=="))
			return equals;
		else
			return -1;
	}
	
	public void insertStack(String type)
	{
		s.push(type);
		return;
	}
	
	
		
}
	
	
	
	


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;


public class Parser 
{
		
	Vector<Token> token;
	int current;
	int errorline;
	boolean noerror = true;
	Writer cout;
	String type;
	String operator;
	Semantic o = new Semantic();
	int pc;
	static int label_counter=0;
	String x;
	String out="@ \n";
	int switch_value;
	String final_label_name;
	String switch_id;
	boolean def=false;
	
	public Parser(String y) throws IOException
	{
		
		x=y;
		cout = new OutputStreamWriter(new FileOutputStream(y));
		//cout.write("Error");
	}
	

	public boolean parse(Vector<Token> tokens) throws IOException
	{
		this.token = tokens;
		current=0;
		program();
		//System.out.println("The last token was " + token.get(current).getWord() + " at line number "+ token.get(current).getLine());
		//System.out.println("The actual last token is "+token.get(token.size()-1).getWord());
		if(current != tokens.size()-1)
		{
			current++;tokencount();
			if(!token.get(current).getWord().isEmpty())
			{
				noerror=false;
				//System.out.println("\nLine number "+ token.get(current).getLine() + "\t: Extra Characters");
				cout.write("\nLine number"+token.get(current).getLine()+"\t: Extra Characters");
			}
			
		}
		cout.close();
		File f = new File(x);
		
		
		return noerror;
	}
	
	
	public void program() throws IOException
	{
		var_section();
		int i;
		
		String str;
		String type;
		
		
		
		pc=1;
		body();
		out = out + "OPR 1,0\n";
		pc++;
		out = out + "OPR 0,0\n";
		pc++;
		Set<String> set = o.symtab.keySet();
		Iterator <String> itr = set.iterator();	
		while(itr.hasNext())
		{
			str = itr.next();
			type = o.symtab.get(str).get(0).getType();
			if(type.equals("boolean"))
			{
				cout.write(str+",boolean\n");
			}
			else if(type.equals("integer"))
			{
				cout.write(str+",integer\n");
			}
			else
			{
				cout.write(str + "," + o.symtab.get(str).get(0).getValue()+"\n");
			}
		}
		
		
		cout.write(out);
	}
	
	public void var_section() throws IOException
	{
		type();
		id_list();
		if(token.get(current).getWord().equals(";"))
		{
			current++;tokencount();
			if(token.get(current).getWord().equals("{"))
			{
				return;
			}
			else if(token.get(current).getToken().equals("KEYWORD"))
			{
				var_section();
			}
			else
			{
				noerror = false;
				errorline=token.get(current).getLine();
				//System.out.println("\nLine number "+ errorline + "\t: Expected  type");
				cout.write("\nLine number "+ errorline + "\t: Expected  type");
				while(!token.get(current).getWord().equals(";"))
				{
					current++;
				}
			}
		}
		else
		{
			noerror = false;
			errorline=token.get(current).getLine();
			//System.out.println("\nLine number "+ errorline + "\t: Expected ;");
			cout.write("\nLine number "+ errorline + "\t: Expected ;");
			//while(!token.get(current).getWord().equals("{"))
			//{
			//	current++;tokencount();
				
			//}
		}
	}
	
	public void type() throws IOException
	{
		if(token.get(current).getWord().equals("integer")||token.get(current).getWord().equals("boolean")||token.get(current).getWord().equals("string")||token.get(current).getWord().equals("char")||token.get(current).getWord().equals("float")||token.get(current).getWord().equals("string"))
		{
			if(token.get(current).getWord().equals("integer"))
				type="integer";
			
			if(token.get(current).getWord().equals("boolean"))
				type="boolean";
			
			current++;tokencount();
			if(token.get(current).getToken().equals("IDENTIFIER"))
			{
				return;
			}
			else
			{
				noerror = false;
				//System.out.println("\nLine number "+ token.get(current).getLine() + "\t: Expected Identifier");
				cout.write("\nLine number "+ token.get(current).getLine() + "\t: Expected Identifier");
			}
		}
		else
		{
			noerror = false;
			errorline = token.get(current).getLine();
			//System.out.println("\nLine number "+ errorline + "\t: Expected Type");
			cout.write("\nLine number "+ errorline + "\t: Expected Type");
			
		}
	}
	
	public void id_list() throws IOException
	{
		if(token.get(current).getToken().equals("IDENTIFIER"))
		{
			//----------For Semantic Analysis--------------
			String id = token.get(current).getWord();
			boolean isPresent = o.searchSymbolTable(id);
			if(isPresent == false)
				o.insertSymbolTable(type, id, "global");
			else
			{	//System.out.println("Semantic error. Multiple Declaration of Variable");
				noerror = false;
				errorline =token.get(current).getLine();
				cout.write("Line number "+errorline+" :Duplicated Variable " + token.get(current).getWord());
			}
				//--------------------------------------------
			current++;tokencount();
			if(token.get(current).getWord().equals(","))
			{
				current++;tokencount();
				id_list();
			}
			else if(token.get(current).getWord().equals(";"))
			{
				return;
			}
			else
			{
				noerror = false;
				errorline = token.get(current).getLine();
				//System.out.println("\nLine number "+ token.get(current).getLine() + "\t: Expected IDlist Seperator");
				cout.write("\nLine number "+ token.get(current).getLine() + "\t: Expected IDlist Seperator");
				while(!token.get(current).getWord().equals("{")&&!token.get(current).getWord().equals(";"))
				{
					current++;
				}
			}
		}
		else
		{
			noerror = false;
			//System.out.println("\nLine number "+ token.get(current).getLine() + "\t: Expected Identifier");
			cout.write("\nLine number "+ token.get(current).getLine() + "\t: Expected Identifier");
		}
	}
	
	public void body() throws IOException
	{
		if(token.get(current).getWord().equals("{"))
		{
			current++;tokencount();
			stmt_list();
			if(token.get(current).getWord().equals("}"))
			{
				return;
				
			}
			else
			{
				noerror = false;
				//System.out.println("\nLine number "+ token.get(current).getLine() + "\t: Expected Delimiter }");
				cout.write("\nLine number "+ token.get(current).getLine() + "\t: Expected Delimiter }");
			}
		}
		else
		{
			noerror = false;
			//System.out.println("\nLine number "+ token.get(current).getLine() + "\t: Expected {");
			cout.write("\nLine number "+ token.get(current).getLine() + "\t: Expected {");
			errorline = token.get(current).getLine();
			stmt_list();	
			if(token.get(current).getWord().equals("}"))
			{
				return;
			}
			else
			{
				noerror = false;
				//System.out.println("\nLine number "+ token.get(current).getLine() + "\t: Expected Delimiter }");
				cout.write("\nLine number "+ token.get(current).getLine() + "\t: Expected Delimiter }");
			}
		}
	}
	
	public void stmt_list() throws IOException
	{
		stmt();
		while(!token.get(current).getWord().equals("}"))
		{
			stmt();
		}
		if(def==true)
			o.insertSymbolTable("label", final_label_name, "global", pc);
	}
	
	public void stmt() throws IOException
	{

		
		if(token.get(current).getWord().equalsIgnoreCase("IF"))
		{
			ifstatement();
			current++;tokencount();
		}
		else if(token.get(current).getWord().equalsIgnoreCase("WHILE"))
		{
			whilestatement();
			current++;tokencount();
		}
		else if(token.get(current).getWord().equalsIgnoreCase("SWITCH"))
		{
			switchstatement();
			current++;tokencount();
			if(token.get(current).getWord().equalsIgnoreCase("}"))
			{
				//current++;tokencount();
				return;
			}
			else
			{
				noerror = false;
				//System.out.println("\nLine number "+ token.get(current).getLine() + "\t: Expected } for Switch");
				cout.write("\nLine number "+ token.get(current).getLine() + "\t: Expected } for Switch");
			}
		}
		else if(token.get(current).getWord().equalsIgnoreCase("PRINT"))
		{
			printstatement();
		}
		else if(token.get(current).getToken().equals("IDENTIFIER"))
		{
			assignment();
		}
		else
		{
			noerror = false;
			//System.out.println("\nLine number "+ token.get(current).getLine() + "\t: Expected KeyWord or ID");
			cout.write("\nLine number "+ token.get(current).getLine() + "\t: Expected KeyWord or ID");
			current++;tokencount();
		}
		
	}
	
	public void ifstatement() throws IOException
	{
		String label_name;
		/**pc = token.get(current).getLine();
		label_counter++;
		label_name = "#e"+label_counter; 
		o.insertSymbolTable("integer", label_name,"global",pc);**/
		current++;tokencount();
		condition();
		label_counter++;
		label_name = "#e"+label_counter;
		//cout.write("JMC "+label_name+",false\n");
		out=out+"JMC"+label_name+",false\n";
		pc++;
		String temp1=o.s.pop();
		String temp2 =o.s.pop();
		int temp1Number = o.returnTypeNumber(temp1);
		int temp2Number = o.returnTypeNumber(temp2);
		int operatorNumber = o.returnOperatorNumber(operator);
		String result = o.cube[operatorNumber][temp1Number][temp2Number];
		o.s.push(result);
		boolean isbool = o.isBoolean();
		if(isbool==false)
		{
			noerror=false;
			errorline=token.get(current).getLine();
			cout.write("\nLine Number "+(errorline)+": Boolean expression expected");
		}
			
		body();
		o.insertSymbolTable("label", label_name, "global", pc);
		pc++;
		//current++;tokencount();
	}
	
	public void condition() throws IOException
	{
		primary();
		relop();
		primary();
		int opNumber = calculateOperatorNumber(operator);
		//cout.write("OPR "+opNumber+",0\n");
		out=out+"OPR " +opNumber+", 0\n";
		pc++;
	}
	
	public void primary() throws IOException
	{
		if(token.get(current).getToken().equals("IDENTIFIER")||token.get(current).getToken().equals("INTEGER")||token.get(current).getWord().equals("true")||token.get(current).getWord().equals("false"))
		{
			if(token.get(current).getToken().equals("IDENTIFIER"))
			{
				String id = token.get(current).getWord();
				boolean isPresent = o.searchSymbolTable(id);
				if(isPresent == false)
				{
					//System.out.println("Semantic Error. Variable not Declared");
					errorline =token.get(current).getLine();
					cout.write("\n Line number "+errorline+":Variable "+id+" not found");
				}
				else
				{
					SymbolTable symbol = o.symtab.get(id).get(0);
					String p = symbol.getType();
					o.s.push(p);
					//cout.write("LOD "+id+",0\n");
					out=out+"LOD "+id+",0\n";
					pc++;
				}
					
			}
			
			if(token.get(current).getToken().equals("INTEGER"))
			{
				o.insertStack("integer");
				//cout.write("LIT "+token.get(current).getWord()+",0\n");
				out=out+"LIT "+token.get(current).getWord()+",0\n";
				pc++;
			}
			
			if(token.get(current).getWord().equals("true")||token.get(current).getWord().equals("false"))
			{
				o.insertStack("boolean");
				out=out+"LIT "+token.get(current).getWord()+",0\n";
				pc++;
			}
			
			
			current++;tokencount();
		}
		else
		{
			noerror = false;
			//System.out.println("\nLine number "+ token.get(current).getLine() + "\t: Expected Identifier or Number");
			cout.write("\nLine number "+ token.get(current).getLine() + "\t: Expected Identifier or Number");
		}
	}
	
	public void relop() throws IOException
	{
		if(token.get(current).getWord().equals("<")||token.get(current).getWord().equals(">"))
		{
			operator = token.get(current).getWord();
			current++;tokencount();
		}
		else if(token.get(current).getWord().equals("!"))
		{
			current++;tokencount();
			if(token.get(current).getWord().equals("="))
				operator = "!=";
			current++;tokencount();
		}
		else if(token.get(current).getWord().equals("="))
		{
			current++;tokencount();
			if(token.get(current).getWord().equals("="))
				operator = "==";
			current++;tokencount();
		}
		else
		{
			noerror = false;
			//System.out.println("\nLine number "+ token.get(current).getLine() + "\t: Expected Relational Operator");
			cout.write("\nLine number "+ token.get(current).getLine() + "\t: Expected Relational Operator");
		}
	}
	
	public void assignment() throws IOException
	{
		String var = token.get(current).getWord();
		int opNumber;
		if(token.get(current).getToken().equals("IDENTIFIER"))
		{
			//-----For Semantic Analysis-------------
			String id = token.get(current).getWord();
			boolean isPresent = o.searchSymbolTable(id);
			if(isPresent==false)
			{
				//System.out.println("Semantic Error. Undeclared ID");
				errorline = token.get(current).getLine();
				cout.write("\n Line number "+errorline+":Variable "+id+" not found");
			}
			else
			{
				SymbolTable symbol = o.symtab.get(id).get(0);
				String p = symbol.getType();
				o.s.push(p);
			}
			
			//---------------------------------------
			current++;tokencount();
			if(token.get(current).getWord().equals("="))
			{
				current++;tokencount();
				primary();
				if(token.get(current).getWord().equals(";"))
				{
					current++;tokencount();
					boolean isTypeCheck = o.isTypeMatching("=");
					if(isTypeCheck==false)
					{	
						//System.out.println("Type MisMatch");
						errorline = token.get(current).getLine();
						cout.write("\n Line Number "+errorline+": Type Mismatch");
					}
					//cout.write("STO "+var+",0 \n" );
					out=out+"STO "+var+",0 \n";
					pc++;
					return;
				}
				else if(token.get(current).getWord().equals("+")||token.get(current).getWord().equals("-")||token.get(current).getWord().equals("*")||token.get(current).getWord().equals("/"))
				{
					operator = token.get(current).getWord();
					opNumber=calculateOperatorNumber(operator);
					current++;tokencount();
					primary();
					//---For Semantic Analysis------------------------------------
					String temp1 = o.s.pop();
					String temp2 = o.s.pop();
					int temp1Number = o.returnTypeNumber(temp1);
					int temp2Number = o.returnTypeNumber(temp2);
					int operatorNumber = o.returnOperatorNumber(operator);
					String result = o.cube[operatorNumber][temp1Number][temp2Number];
					o.s.push(result);
					
					//----------------------------------------------------
					if(token.get(current).getWord().equals(";"))
					{
						
						boolean isTypeCheck = o.isTypeMatching("=");
						if(isTypeCheck==false)
						{
							noerror=false;
							errorline = token.get(current).getLine(); 
							
							//System.out.println("Type Mismatch "+errorline);
							cout.write("\nLine Number "+errorline+": Type Mismatch");
							
						}
						current++;tokencount();
						//cout.write("OPR "+opNumber+",0\n");
						//cout.write("STO "+id+",0\n");
						out=out+"OPR "+opNumber+",0\n";
						pc++;
						out=out+"STO "+id+",0\n";
						
						pc++;
						return;
					}
					else
					{
						noerror = false;
						errorline=token.get(current).getLine()-1;
						//System.out.println("\nLine number "+ errorline + "\t: Expected ;");
						cout.write("\nLine number "+ errorline + "\t: Expected ;");
					}
				}
				else
				{
					noerror = false;
					errorline=token.get(current).getLine()-1;
				//	System.out.println("\nLine number "+ errorline + "\t: Expected ; or  Operator");
					cout.write("\nLine number "+ errorline + "\t: Expected ; or  Operator");
				}
			}
			else
			{
				noerror = false;
				errorline= token.get(current).getLine();
				//System.out.println("\nLine number "+ errorline + "\t: Expected =");
				cout.write("\nLine number "+ errorline + "\t: Expected =");
				while(errorline==token.get(current).getLine()) 
				{
					current++;
					tokencount();
				}
				
			}
		}
		
		
		
	}
	
	
	public int calculateOperatorNumber(String op)
	{
		int n;
		
		if(op.equals("+"))
			return 2;
		else if (op.equals("-"))
			return 3;
		else if (op.equals("*"))
			return 4;
		else if (op.equals("/"))
			return 5;
		else if (op.equals("%"))
			return 6;
		else if (op.equals(">"))
			return 11;
		else if (op.equals("<"))
			return 12;
		else if (op.equals("!="))
			return 16;
		else if (op.equals("=="))
			return 15;
		else 
			return -1;
		
	}
	

	public void printstatement() throws IOException
	{
		if(token.get(current).getWord().equalsIgnoreCase("PRINT"))
		{
			current++;tokencount();
			if(token.get(current).getToken().equalsIgnoreCase("IDENTIFIER"))
			{
				String id = token.get(current).getWord();
				boolean isPresent = o.searchSymbolTable(id);
				if(isPresent==false)
				{
					//System.out.println("Semantic Error. Variable not declared");
					noerror = false;
					errorline = token.get(current).getLine();
					cout.write("\nLine Number "+errorline+" : Variable "+id+" not found");
				}
				else
				{
					//cout.write("LOD "+token.get(current).getWord()+",0\n");
					out=out+"LOD "+token.get(current).getWord()+",0\n";
					pc++;
					id=token.get(current).getWord();
					o.insertStack(o.symtab.get(id).get(0).getType());
					current++;tokencount();
				}
				if(token.get(current).getWord().equals(";"))
				{
					current++;
					tokencount();
					//cout.write("OPR 21,0\n");
					out=out+"OPR 21,0\n";
					pc++;
					return;
				}
				else
				{
					noerror = false;
					errorline=token.get(current).getLine();
					//System.out.println("\nLine number "+ errorline + "\t: Expected ;");
					cout.write("\nLine number "+ errorline + "\t: Expected ;");
					while(!token.get(current).getWord().equals(";")&&token.get(current).getLine()==errorline)
					{
						current++;tokencount();
					}
					current++;tokencount();
				}
			}
			else
			{
				noerror = false;
				errorline = token.get(current).getLine();
				//System.out.println("\nLine number "+ token.get(current).getLine() + "\t: Expected Identifier");
				cout.write("\nLine number "+ token.get(current).getLine() + "\t: Expected Identifier");
				while(token.get(current).getLine()==errorline)
				{
					current++;
					tokencount();
				}
			}
		}
		else
		{
			noerror = false;
			//System.out.println("\nLine number "+ token.get(current).getLine() + "\t: Expected Keyword");
			cout.write("\nLine number "+ token.get(current).getLine() + "\t: Expected Keyword");
			
		}
				
	}
	
	public void whilestatement() throws IOException
	{
		String first_label_name,second_label_name;
		if(token.get(current).getWord().equalsIgnoreCase("WHILE"))
		{
			//pc = token.get(current).getLine();
			label_counter++;
			first_label_name = "#e"+label_counter; 
			o.insertSymbolTable("label",first_label_name,"global",pc);
			current++;tokencount();
			condition();
			//cout.write("JMC "+label_name+",false\n");
			label_counter++;
			second_label_name="#e"+label_counter;
			out=out+"JMC "+second_label_name+",false\n";
			pc++;
			String temp1=o.s.pop();
			String temp2 =o.s.pop();
			int temp1Number = o.returnTypeNumber(temp1);
			int temp2Number = o.returnTypeNumber(temp2);
			int operatorNumber = o.returnOperatorNumber(operator);
			String result = o.cube[operatorNumber][temp1Number][temp2Number];
			o.s.push(result);
			boolean isbool = o.isBoolean();
			if(isbool==false)
			{
				noerror=false;
				errorline=token.get(current).getLine();
				cout.write("\nLine Number "+(errorline)+": Boolean expression expected");
			}
			body();
			//cout.write("JMP "+label_name+",0\n");
			out=out+"JMP "+first_label_name+",0\n";
			pc++;
			o.insertSymbolTable("label", second_label_name, "global", pc);
			//current++;tokencount();
		}
	}
	
	public void switchstatement() throws IOException
	{
		if(token.get(current).getWord().equalsIgnoreCase("SWITCH"))
		{
			current++;tokencount();
			if(token.get(current).getToken().equalsIgnoreCase("IDENTIFIER"))
			{
				//----For Semantic Analysis--------------------------------
				String id = token.get(current).getWord();
				switch_id = id;
				boolean isPresent = o.searchSymbolTable(id);
				if(isPresent==false)
				{
					//System.out.println("Semantic Error. Variable not declared");
					noerror = false;
					errorline = token.get(current).getLine();
					cout.write("\nLine number "+errorline+": Variable "+id+" not found");
				}		
				//---------------------------------------------------------
				switch_value = o.symtab.get(id).get(0).getValue();
				
				o.insertStack(o.symtab.get(id).get(0).getType());
				
				
				current++;tokencount();
				
				if(token.get(current).getWord().equals("{"))
				{
					current++;tokencount();
					case_list();
					//if(token.get(current).getWord().equals("}"))
					//{
					//	current++;tokencount();
						if(token.get(current).getWord().equalsIgnoreCase("DEFAULT"))
						{
							default_statement();
							current++;tokencount();
						}
						else if(token.get(current).getWord().equals("}"))
						{
							return;
						}
						else	
						{
							noerror = false;
							errorline = token.get(current).getLine();
							//System.out.println("\nLine number "+ token.get(current).getLine() + "\t: Expected Keyword or }");
							cout.write("\nLine number "+ token.get(current).getLine() + "\t: Expected Keyword or }");
							while(!token.get(current).getWord().equals("}"))
							{
								current++;
							}
						}
					/**}  I removed this earlier..
					else
					{
						noerror = false;
						System.out.println("\nLine number "+ token.get(current).getLine() + "\t: Expected Delimiter or Default");
					}**/
				}
				else
				{
					noerror = false;
					//System.out.println("\nLine number "+ token.get(current).getLine() + "\t: Expected {");
					cout.write("\nLine number "+ token.get(current).getLine() + "\t: Expected {");
				}
			}
			else
			{
				noerror = false;
				//System.out.println("\nLine number "+ token.get(current).getLine() + "\t: Expected Identifier");
				cout.write("\nLine number "+ token.get(current).getLine() + "\t: Expected Identifier");
				while(!token.get(current).getWord().equals("{"))
				{
					current++;
				}
				current++;
				case_list();
				if(token.get(current).getWord().equals("}"))
				{
					current++;tokencount();
					if(token.get(current).getWord().equalsIgnoreCase("DEFAULT"))
					{
						default_statement();
					}
					else if(token.get(current).getWord().equals("}"))
					{
						return;
					}
					else
					{
						noerror = false;
						errorline = token.get(current).getLine();
						//System.out.println("\nLine number "+ token.get(current).getLine() + "\t: Expected Keyword or }");
						cout.write("\nLine number "+ token.get(current).getLine() + "\t: Expected Keyword or }");
						while(!token.get(current).getWord().equals("}"))
						{
							current++;
						}
					}
				}
				else
				{
					noerror = false;
					//System.out.println("\nLine number "+ token.get(current).getLine() + "\t: Expected Delimiter or Default");
					cout.write("\nLine number "+ token.get(current).getLine() + "\t: Expected Delimiter or Default");
				}
				
			}
		}
	}
	
	public void case_list() throws IOException
	{
		case_statement();
		current++;
				if(!token.get(current).getWord().equals("}"))
				{
					if(token.get(current).getWord().equals("CASE"))
						case_list();
					else
					{
						return;
					}
				}			
		
	}
	
	public void case_statement() throws IOException
	{
		String case_type;
		String id,label_name;
		if(token.get(current).getWord().equalsIgnoreCase("CASE"))
		{
			current++;tokencount();
			if(token.get(current).getToken().equals("INTEGER"))
			{
				out=out+"LOD "+switch_id+",0\n";
				pc++;
				o.insertStack("integer");
				id=token.get(current).getWord();
				out=out+"LIT "+id+",0\n";
				pc++;
				operator="==";
				int opNumber = calculateOperatorNumber("==");
				out=out+"OPR " +opNumber+", 0\n";
				pc++;
				current++;tokencount();
				if(token.get(current).getWord().equals(":"))
				{
					label_counter++;
					label_name = "#e"+label_counter;
					//cout.write("JMC "+label_name+",false\n");
					
					out=out+"JMC "+label_name+",false\n";
					pc++;
					String temp1=o.s.pop();
					String temp2 =o.s.pop();
					int temp1Number = o.returnTypeNumber(temp1);
					int temp2Number = o.returnTypeNumber(temp2);
					int operatorNumber = o.returnOperatorNumber(operator);
					String result = o.cube[operatorNumber][temp1Number][temp2Number];
					o.s.push(result);
					boolean isbool = o.isBoolean();
					if(isbool==false)
					{
						noerror=false;
						errorline=token.get(current).getLine();
						cout.write("\nLine Number "+(errorline)+": Boolean expression expected");
					}
					current++;tokencount();
					body();
					pc++;
					o.insertSymbolTable("label", label_name, "global", pc);
					label_counter++;
					if(final_label_name==null)
						final_label_name="#e"+label_counter;
					out=out+"JMP "+final_label_name+",0\n";
					
					
					//---------------------This is where I stopped----------------------------------------------------------------
					
				}
				else
				{
					noerror = false;
					//System.out.println("\nLine number "+ token.get(current).getLine() + "\t: Expected :");
					cout.write("\nLine number "+ token.get(current).getLine() + "\t: Expected :");
					while(!token.get(current).getWord().equals("{"))
					{
						current++;
					}
					body();
				}
				
			}
			else
			{
				noerror = false;
				//System.out.println("\nLine number "+ token.get(current).getLine() + "\t: Expected Integer");
				cout.write("\nLine number "+ token.get(current).getLine() + "\t: Expected Integer");
				while(!token.get(current).getWord().equals("{"))
				{
					current++;
				}
				body();
			}
		}
		else
		{
			noerror = false;
			//System.out.println("\nLine number "+ token.get(current).getLine() + "\t: Expected Keyword Case");
			cout.write("\nLine number "+ token.get(current).getLine() + "\t: Expected Keyword Case");
		}
	}
	
	public void default_statement() throws IOException
	{
		if(token.get(current).getWord().equalsIgnoreCase("DEFAULT"))
		{
			def=true;
			current++;tokencount();
			if(token.get(current).getWord().equals(":"))
			{
				current++;tokencount();
				body();
				
				//current++;tokencount();
			}
			else
			{
				noerror = false;
				//System.out.println("\nLine number "+ token.get(current).getLine() + "\t: Expected :");
				cout.write("\nLine number "+ token.get(current).getLine() + "\t: Expected :");
				errorline = token.get(current).getLine();
				while(!token.get(current).getWord().equals("{") && errorline==token.get(current).getLine())
				{
					current++;tokencount();
				}
				if(token.get(current).getWord().equals("{"))
				{
					body();
				}
			}
		}
		else
		{
			noerror = false;
			//System.out.println("\nLine number "+ token.get(current).getLine() + "\t: Expected Keyword Default");
			cout.write("\nLine number "+ token.get(current).getLine() + "\t: Expected Keyword Default");
		}
	}
	
	public void tokencount() throws IOException
	{
		if(current>token.size()-1)
		{
			//System.out.println("\nLine number "+token.get(current-1).getLine()+"\t: Expected }");
			cout.write("\nLine number "+token.get(current-1).getLine()+"\t: Expected }");
			cout.close();
			System.exit(0);
		}
	}

}

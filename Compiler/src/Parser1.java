import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Vector;


public class Parser1 
{
		
	Vector<Token> token;
	int current;
	int errorline;
	boolean noerror = true;
	Writer cout;
	String type;
	String operator;
	Semantic o = new Semantic();
	boolean primary_error=false;
	
	public Parser1(String x) throws IOException
	{
	
		cout = new OutputStreamWriter(new FileOutputStream(x));
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
		return noerror;
	}
	
	
	public void program() throws IOException
	{
		var_section();
		body();
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
				cout.write("\nLine number "+errorline+" :Duplicated Variable " + token.get(current).getWord());
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
			//System.out.println("While ends at" + token.get(current).getLine()+"and the token is"+token.get(current).getToken());
		}
		else if(token.get(current).getWord().equalsIgnoreCase("SWITCH"))
		{
			switchstatement();
			current++;tokencount();
			if(token.get(current).getWord().equalsIgnoreCase("}"))
			{
				current++;tokencount();
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
		current++;tokencount();
		condition();
		String temp1=o.s.pop();
		String temp2=o.s.pop();
		
		int temp1Number = o.returnTypeNumber(temp1);
		int temp2Number = o.returnTypeNumber(temp2);
		int operatorNumber =  o.returnOperatorNumber(operator);
		String result = o.cube[operatorNumber][temp1Number][temp2Number];
		o.s.push(result);	
		boolean isbool = o.isBoolean();
		if(isbool==false)
		{
			noerror=false;
			errorline=token.get(current).getLine();
			cout.write("\nLine Number "+(errorline)+": Boolean Expression Expected");
		}
		
		body();
		//current++;tokencount();
	}
	
	public void condition() throws IOException
	{
		primary();
		relop();
		primary();
	}
	
	public void primary() throws IOException
	{
		if(token.get(current).getToken().equals("IDENTIFIER")||token.get(current).getToken().equals("INTEGER"))
		{
			if(token.get(current).getToken().equals("IDENTIFIER"))
			{
				String id = token.get(current).getWord();
				boolean isPresent = o.searchSymbolTable(id);
				if(isPresent == false)
				{
					//System.out.println("Semantic Error. Variable not Declared");
					noerror=false;
					errorline =token.get(current).getLine();
					cout.write("\n Line number "+errorline+":Variable "+id+" not found");
					primary_error=true;
				}
				else
				{
					SymbolTable symbol = o.symtab.get(id).get(0);
					String p = symbol.getType();
					o.s.push(p);
				}
					
			}
			
			if(token.get(current).getToken().equals("INTEGER"))
			{
				o.insertStack("integer");
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
		if(token.get(current).getWord().equals("<")||token.get(current).getWord().equals(">")||token.get(current).getWord().equals("!="))
		{
			operator = token.get(current).getWord();
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
		boolean isTypeCheck = false;
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
					
					if(primary_error==false)
					{	
						isTypeCheck = o.isTypeMatching("=");
						if(isTypeCheck==false)
						{	
						//System.out.println("Type MisMatch");
							
							errorline = token.get(current).getLine();
							cout.write("\n Line Number "+errorline+": Type Mismatch");
							
						}
						current++;tokencount();
						return;
					}
					
					current++;tokencount();
					
				}
				else if(token.get(current).getWord().equals("+")||token.get(current).getWord().equals("-")||token.get(current).getWord().equals("*")||token.get(current).getWord().equals("/"))
				{
					operator = token.get(current).getWord();
					current++;tokencount();
					primary();
					//---For Semantic Analysis----------------------------
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
						
						 isTypeCheck = o.isTypeMatching("=");
						if(isTypeCheck==false)
						{
							noerror=false;
							errorline = token.get(current).getLine(); 
							
							//System.out.println("Type Mismatch "+errorline);
							cout.write("\nLine Number "+errorline+": Type Mismatch");
							
						}
						current++;tokencount();
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
				current++;tokencount();
				if(token.get(current).getWord().equals(";"))
				{
					current++;
					tokencount();
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
		if(token.get(current).getWord().equalsIgnoreCase("WHILE"))
		{
			current++;tokencount();
			condition();
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
				current++;tokencount();
				boolean isPresent = o.searchSymbolTable(id);
				//System.out.println("Switch at line"+token.get(current).getLine());
				if(isPresent==false)
				{
					//System.out.println("Semantic Error. Variable not declared");
					noerror = false;
					errorline = token.get(current).getLine();
					cout.write("\nLine number "+errorline+": Variable "+id+" not found");
					cout.write("\nLine number "+errorline+": Incompatible types - Boolean to integer not allowed");
				}		
			
				if(isPresent==true)
				{
					if(!o.symtab.get(id).get(0).getType().equals("integer"))
					{	
						noerror = false;
						errorline = token.get(current).getLine();
						cout.write("\nLine number "+errorline+": Incompatible Types - Boolean to integer not allowed");
					}
				}
				
					

				
				//---------------------------------------------------------
				
				
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
		if(token.get(current).getWord().equalsIgnoreCase("CASE"))
		{
			current++;tokencount();
			if(token.get(current).getToken().equals("INTEGER"))
			{
				current++;tokencount();
				if(token.get(current).getWord().equals(":"))
				{
					current++;tokencount();
					body();
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
			current++;tokencount();
			if(token.get(current).getWord().equals(":"))
			{
				current++;tokencount();
				body();
				
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

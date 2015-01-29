import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer_1206223989 {
 
  public final static String[] keywords = {"IF", "ELSE", "WHILE", "SWITCH", "CASE", "RETURN", "integer", "float", "void", "char", "string", "boolean", "true", "false", "print","DEFAULT"};
  
  public static String lexer(String string) 
  {
    // 4. IMPLEMENT THE LEXICAL RULES HERE
	    String integer = "0|[1-9][0-9]*";
		String decimal = "(0|[1-9][0-9]*)\\.(([0-9]*[1-9][0-9]*)|([0-9]*[e][1-9][0-9]*))";
		String text = "\"(.*|\\.)\"";
		String oper = "\\+|-|\\*|/|&|\\||>|<|==|%|=";
		String delimiter = "\\:|\\;|\\{|\\}|\\(|\\)|\\[|\\]|,|=";
		String hex = "0x[(0-9)|(A-F)|(a-f)]+";
		String oct = "0[0-7]+";
		String binary = "0[bB][1|0]+";
		String ch = "^'(.|\\\\.)'$";
		String ID = "[a-zA-Z][a-zA-Z0-9_$]*|[_|$][a-zA-Z0-9]+";	  
		
		// 5. RETURN THE TOKEN FOR THE string received as parameter;
		
		Pattern p = Pattern.compile(integer);
		Matcher m = p.matcher(string);
		if(m.matches())
			return "INTEGER";
		
		p = Pattern.compile(decimal);
		m = p.matcher(string);
		if(m.matches())
			return "FLOAT";
		
		p = Pattern.compile(text);
		m = p.matcher(string);
		if(m.matches())
			return "STRING";
		
		p = Pattern.compile(oper);
		m = p.matcher(string);
		if(m.matches())
			return "OPERATOR";
						
		p = Pattern.compile(delimiter);
		m = p.matcher(string);
		if(m.matches())
			return "DELIMITER";
			
		
		p = Pattern.compile(hex);
		m = p.matcher(string);
		if(m.matches())
			return "HEXADECIMAL";
		
		p = Pattern.compile(oct);
		m = p.matcher(string);
		if(m.matches())
			return "OCTAL";
		
		p = Pattern.compile(binary);
		m = p.matcher(string);
		if(m.matches())
			return "BINARY";
		
		p = Pattern.compile(ch);
		m = p.matcher(string);
		if(m.matches())
			return "CHARACTER";
		
		int c=0;
		p = Pattern.compile(ID);
		m = p.matcher(string);
		if(m.matches())
		{
			for(int i=0;i<keywords.length;i++)
			{
				if(string.equals(keywords[i]))
					return "KEYWORD";
			}
			return "IDENTIFIER";
		}
		
     
    // 6. RETURN "ERROR" if the string is not a word
    // (each time hat you detect and ID, search it in the array "keywords". If it exist then it is a keyword, else it is an ID)
    return "ERROR";
  }

  public static boolean isDelimiter(char c) 
  {
     char [] delimiters = {';', ' ', '}','{', '[',']','(',')',',',':'};
     for (int x=0; x<delimiters.length; x++) {
      if (c == delimiters[x]) return true;      
     }
     return false;
  }
  
  public static boolean isOperator(char o) 
  {
     char [] operators = {'+', '-', '*','/', '%','<','>','=','!','&','|'};
     for (int x=0; x<operators.length; x++) {
      if (o == operators[x]) return true;      
     }
     return false;
  }

}

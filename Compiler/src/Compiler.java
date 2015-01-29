
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Vector;

public class Compiler {

  public static Vector<Token> tokens;  
  
  private static String[] split(String line) {
    String [] strings;
	 // 1. SPLIT THE LINE IN STRINGS (WORDS);
    String [] str=new String[100];
	int c=0;
	char []newline=line.toCharArray();
	String tmp = "";
	boolean flag = false;
	for(int i=0;i<line.length();i++)
	{
		System.out.println("Current Char is "+newline[i]);
		if(!Lexer_1206223989.isDelimiter(newline[i]) && !Lexer_1206223989.isOperator(newline[i]) && !Character.isWhitespace(newline[i]) && newline[i]!='\0')
		{
			if(newline[i]=='\"' && flag == false)					
			{
				//i=i+1;
				if(tmp!="")
				{	
					str[c++]=tmp;
					tmp="";
					tmp = tmp + newline[i];
					flag = true;
					continue;
				}
				tmp=tmp+newline[i];
				flag=true;
				continue;
			}
						
			if(newline[i]=='\"' && flag == true)
			{
					if(newline[i-1]!='\\')
					{
						tmp=tmp+newline[i];
						str[c++]=tmp;
						flag=false;
						tmp="";
						continue;
					}
			}
			
			if(newline[i]=='\'' && flag == false)					
			{
				//i=i+1;
						if(tmp!="")
						{
							str[c++]=tmp;
							tmp="";	
							tmp = tmp + newline[i];
							flag = true;
							continue;
						}
						tmp=tmp+newline[i];
						flag=true;
						continue;
			}
						
			if(newline[i]=='\'' && flag == true)
			{
				if(newline[i-1]=='\"')
				{
					str[c++]=tmp;
					tmp="";
					tmp=tmp+newline[i];
					continue;
				}
							
				if(newline[i-1]!='\\')
				{	
					tmp=tmp+newline[i];
					str[c++]=tmp;
					flag=false;
					tmp="";
					continue;
				}
				
				
			}
						
			
			
			tmp = tmp +newline[i];
		}
		
		else
		{
			/**if(newline[i]=='\"' && flag == true)
			{
					tmp=tmp+newline[i];
					str[c++]=tmp;
					flag=false;
					tmp="";
					continue;
				
			}
			**/
			
			if(newline[i]=='\0')
			{
				str[c++]=tmp;
				tmp="";
				continue;
			}
			
			
			if(Character.isWhitespace(newline[i])&& flag==true)
			{
				tmp=tmp+newline[i];
				continue;
			}
			
						
			if(!Character.isWhitespace(newline[i]))
			{
				if(flag==false)
				{
					if(tmp!="")
					{
						str[c++]=tmp;
						tmp="";
					}
						
					if(Lexer_1206223989.isOperator(newline[i]))
					{
						str[c++]=Character.toString(newline[i]);
					}
					if(Lexer_1206223989.isDelimiter(newline[i]))
					{
						str[c++]=Character.toString(newline[i]);
					}
				}	
				else
				{
					tmp=tmp+newline[i];
				}
			}
			else
			{	
				if(tmp!="")
				{
					str[c++]=tmp;
					tmp="";
					continue;
				}
			}
		}
	}
	
	if(tmp!="")
	str[c++]=tmp;
		
	// 2. INSERT EACH WORD IN THE ARRAY strings
	int count=0;
	for(int i=0;i<str.length;i++)
		if(str[i]!=null)
			count++;
	
	strings = new String[count];
    
	for(int i=0;i<str.length;i++)
	{
		if(str[i]!=null)
			strings[i]=str[i];
	
	}
	
	return strings;
  }
  
  public static void main(String[] args) throws FileNotFoundException, IOException 
  {
	boolean noerror = true;
	tokens = new Vector<Token>();  
    BufferedReader br = new BufferedReader(new FileReader(args[0]));
    //Writer out = new OutputStreamWriter(new FileOutputStream(args[1]));  
    int totalLexicalErrors = 0;
    int line_number=0;
    Token x;
    try {            
      String line = br.readLine();   
      while (line != null) 
      {
    	  if(line.isEmpty() || line.trim().equals("") || line.trim().equals("\n"))
    	  {
    		  line=br.readLine();
    		  line_number++;
    		  continue;
    	  }	  
    	  line_number++;
        String[] strings = split (line);
        for (String string : strings) {
          String token = Lexer_1206223989.lexer(string);
              	  x = new Token(string,token,line_number);
                  tokens.add(x);
          if (token.equals("ERROR")) {
            totalLexicalErrors++;
          }
        }
       // System.out.println(strings[0].length());
        line = br.readLine();  
      }        
    } finally {
      br.close();
    }   
    // 3. PRINT THE VECTOR<TOKENS> INTO THE OUTPUT FILE (use the defined stream out)
    
    //Writer cout = new OutputStreamWriter(new FileOutputStream(args[1]));
    for(int i=0;i<tokens.size();i++)
    {
    	Token abc = tokens.get(i);
    	//cout.write(abc.getToken());
    	System.out.print(abc.getToken());
    	//cout.write("\t");
    	//cout.write(abc.getWord());
    	System.out.println("\t"+abc.getWord()+"\t"+abc.getLine()+"\n");
    	//cout.write("\n");
    	
    }
    //cout.close();
    
    Parser p =new Parser(args[1]);
    //Parser1 p =new Parser1(args[1]);
    noerror = p.parse(tokens);
    
    
   /** if(noerror==true)
    {
    	//System.out.println("Build Successful");
    	Writer cout = new OutputStreamWriter(new FileOutputStream(args[1]));
    	cout.write("Build Succesful");
    	cout.close();
    }
  **/
  }
  
}

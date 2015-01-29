/**
public class StringSplit {

	public static void main(String args[]) 
	{
		// TODO Auto-generated method stub
		//String line = "hello;world cse340 asu 2013/05/31";
		//String line = "boolean $xx= ((((((((23WE + 44 - 3 / 2 % 45 <=17) > 0xfffff.34.45;";
		String line = "String s = \"Hello \"jurassic\" world\"; "; 
		//String line = "for(x=0;x<24;x++)";
		System.out.println("Here!");
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
					i=i+1;
					tmp = tmp + newline[i];
					flag = true;
					continue;
				}
				
				if(newline[i]=='\"' && flag == true)
				{
					if(newline[i+1]==';')
					{
						str[c++]=tmp;
						tmp="";
						flag=false;
						continue;
					}
					
					tmp=tmp+newline[i];
					continue;
				}
				
				if(newline[i]=='\0')
				{
					str[c++]=tmp;
					break;
				}
				
				tmp = tmp +newline[i];
			}
			
			else
			{
				if(Character.isWhitespace(newline[i])&& flag==true)
				{
					tmp=tmp+newline[i];
					continue;
				}
				
				
				
				if(!Character.isWhitespace(newline[i]))
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
		
		for(int i = 0; i<str.length;i++)
		{
			System.out.println(str[i]);
		}
		
		
	
	}

}
**/
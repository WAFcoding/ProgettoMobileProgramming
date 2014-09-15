/**
 * 
 */
package utility;

/**
 * @author "Pasquale Verlotta - pasquale.verlotta@gmail.com"
 *
 */
public class Utils {
	
	private static String[] badSymbols=  {"\"", "'", "//", " "};
	
	public static String replaceBadSymbols(String param){
		String toReturn= "";
		char[] paramChar= param.toCharArray();
		
		for(String s : badSymbols){
			if(param.contains(s)){
				int size= paramChar.length;
				int[] position= new int[size];
				int index= 0;
				//mi salvo le posizioni dei simboli cattivi
				for(int pos=0; pos<size; pos++){
					if(paramChar[pos] == s.charAt(0)){
						position[index]= pos;
						index++;
					}
				}
				//rimpiazzo i simboli
				for(int j : position){
					paramChar[j]= '_';
				}
			}
		}
		toReturn= String.valueOf(paramChar);
		return toReturn;
	}
}

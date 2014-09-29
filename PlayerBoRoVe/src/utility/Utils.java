/**
 * 
 */
package utility;

import java.util.ArrayList;

/**
 * @author "Pasquale Verlotta - pasquale.verlotta@gmail.com"
 *
 */
public class Utils {
	
	private static String[] badSymbols=  {"'", ",", "_", "-"};
	
	public static String replaceBadSymbols(String param){
		
		String toReturn= "";
		char[] paramChar= param.toCharArray();
		
		for(String s : badSymbols){
			if(param.contains(s)){
				int size= paramChar.length;
				ArrayList<Integer> position= new ArrayList<Integer>();
				int index= 0;
				//mi salvo le posizioni dei simboli cattivi
				for(int pos=0; pos<size; pos++){
					if(paramChar[pos] == s.charAt(0)){
						position.add(pos);
						index++;
					}
				}
				if(index > 0){
					//rimpiazzo i simboli
					for(int j : position){
						paramChar[j]= ' ';
					}
				}
			}
		}
		toReturn= String.valueOf(paramChar);
		return toReturn;
	}
}

package PlayerManager;

/**
 * Rappresenta l'oggetto durata, sia di un singolo brano che di una playlist
 * 
 * @author BoRoVe
 * @version 0.0, 18/07/2014
 */

public class Duration {

	private int hour, minute, seconds;
	
	public Duration(int hour, int minute, int seconds){
		
		this.setHour(hour);
		this.setSeconds(seconds);
		this.setMinute(minute);
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMinute() {
		return minute;
	}

	public void setMinute(int minute) {
		this.minute = minute;
	}

	public int getSeconds() {
		return seconds;
	}

	public void setSeconds(int seconds) {
		this.seconds = seconds;
	}
	
	/**
	 * Restituisce il valore della durata in una stringa formattata hh:mm:ss
	 */
	public String getDuration(){
		String tmp1= Integer.toString(getHour());
		String tmp2= Integer.toString(getMinute());
		String tmp3= Integer.toString(getSeconds());
		
		return tmp1+":"+tmp2+":"+tmp3;
	}
	
	/**
	 * Imposta i tre valori della durata
	 * 
	 * @param h int ore
	 * @param m int minuti
	 * @param s int secondi
	 */
	public void setDuration(int h, int m, int s){
		setHour(h);
		setMinute(m);
		setSeconds(s);
	}
	
	public void setDuration(Duration d){
		setHour(d.getHour());
		setMinute(d.getMinute());
		setSeconds(d.getSeconds());
	}
	
	/***
	 * somma la durata passata alla durata locale
	 * @param d
	 */
	public void sum(Duration d) {
		int tmp_h= d.getHour();
		int tmp_m= d.getMinute();
		int tmp_s= d.getSeconds();
		
		int sum_s= seconds + tmp_s;
		int carry_s= 0;
		if(sum_s >= 60) {
			carry_s= 1;
			seconds= sum_s - 60;
		}else
		{
			seconds=sum_s;
		}
		
		int sum_m= minute + carry_s;
		int carry_m= 0;
		if(sum_m >= 60){
			carry_m= 1;
			minute= tmp_m;
		}
		else{
			sum_m+= tmp_m;
			if(sum_m >= 60){
				carry_m= 1;
				minute= sum_m - 60;
			}
			else
			{
				minute=sum_m;
			}
		}
		
		int sum_h= hour + carry_m + tmp_h;
		hour= sum_h;
	}
	
	/**
	 * confronta la durata passata con la locale
	 * @param d
	 * @return true se quella passata è più grande
	 */
	public boolean isSmallerOf(Duration d){
		
		//controllo se ha l'ora piu' grande
		if(d.getHour() < this.getHour()){
			return false;
		}
		else if(d.getHour()== this.getHour()){
			if(d.getMinute() < this.getMinute()){
				return false;
			}
			else if(d.getMinute() == this.getMinute()){
				if(d.getSeconds() < this.getSeconds()){
					return false;
				}
			}
		}
		
		
		return true;
	}
}

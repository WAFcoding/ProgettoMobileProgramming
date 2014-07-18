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
}

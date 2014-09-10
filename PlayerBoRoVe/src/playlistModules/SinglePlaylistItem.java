/**
 * 
 */
package playlistModules;

/**
 * Questa classe rappresenta il singolo elemento della scroll view e il singolo elemento della copertina
 * e' composto da una Stringa che indica il nome e da un'immagine
 * @author "Pasquale Verlotta - pasquale.verlotta@gmail.com"
 *
 */
public class SinglePlaylistItem {
	
	private String title;
	private String image_path;
	
	
	
	public SinglePlaylistItem(String p_title, String p_image_path){
		setTitle(p_title);
		setImagePath(p_image_path);
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getImagePath() {
		return image_path;
	}

	public void setImagePath(String image_path) {
		this.image_path = image_path;
	}

}
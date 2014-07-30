package it.borove.playerborove;

import PlayerManager.PlayerController;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
/*
 * ScrollView
 * LinearLayout(vertical)
 * TableLayout
 * TableRow
 * 
 * 
 * 
 */
public class LibraryActivity extends Activity {
	private TableLayout tableLayout;
	private TableRow tableRow;
	private TextView text;
	private LinearLayout lin;
	
	private PlayerController controller;
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 * 
	 * Aggiungere una activity intermedia con bottoni: 
	 * "tutti i brani" (e ti indirizza all'activity library)
	 * "genere, voto, artista, album" ti indirizza all' activity con l'elenco ordinato(stile playlist descritto su drive)
	 * 
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_library);
		
		controller = new PlayerController(this.getApplicationContext());
		
		
		/* Qui il controllore popola la libreria con i brani che trova nel database
		 * il controllore apre una connessione al database
		 * effettua una read dei brani
		 * per ogni brano leggi le informazioni del singolo brano(che sono state parsate dal singolo oggetto Track
		 * aggiungi una tableRow(riga) con le info lette del singolo brano(titolo, artista,...)
		 * 
		 */
		
		
		/* aggiunge una nuova riga nella tabella
		 * 
			tableLayout	= (TableLayout)findViewById(R.id.tableLayout1);
			tableRow = new TableRow(this);
			tableRow.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
			text = new TextView(this);
			text.setText("Ciao");
			text.setTextColor(Color.RED);
			tableRow.addView(text);
			tableLayout.addView(tableRow);	
		*/
		controller.connectToDB();
		
		String title = controller.getCurrentPlayingTrack().getTitle();
		String album = controller.getCurrentPlayingTrack().getAlbum();
		String author = controller.getCurrentPlayingTrack().getSinger();
		String length = controller.getCurrentPlayingTrack().getDuration().toString();
		String kind = controller.getCurrentPlayingTrack().getKind();
		
		tableLayout	= (TableLayout)findViewById(R.id.tableLayout2);
		tableRow = new TableRow(this);
		tableRow.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		
		TextView text = new TextView(this);
		setTrackRow(title, album, author,length, kind);
	
		
	}
	
	public void setTrackRow(String title, String album, String author, String length, String kind){
		text.setText(title);
		tableRow.addView(text);
		text.setText(album);
		tableRow.addView(text);
		text.setText(author);
		tableRow.addView(text);
		text.setText(length);
		tableRow.addView(text);
		text.setText(kind);
		tableRow.addView(text);
		tableLayout.addView(tableRow);
	}
	
	public void readDB(){
		controller.connectToDB();
		
	}
}

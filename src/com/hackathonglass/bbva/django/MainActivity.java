package com.hackathonglass.bbva.django;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.glass.app.Card;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;

public class MainActivity extends Activity {
	
	 private List<Card> mCards;
	 private CardScrollView mCardScrollView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		createCards();

        mCardScrollView = new CardScrollView(this);
        ExampleCardScrollAdapter adapter = new ExampleCardScrollAdapter();
        mCardScrollView.setAdapter(adapter);
        mCardScrollView.activate();
        setContentView(mCardScrollView);
	}
	
	  private void createCards() {
	        mCards = new ArrayList<Card>();

	        Card card;

	        card = new Card(this);
	        card.setText("This card has a footer.");
	        card.setFootnote("I'm the footer!");
	        mCards.add(card);

	        card = new Card(this);
	        card.setText("This card has a puppy background image.");
	        card.setFootnote("How can you resist?");
	        card.setImageLayout(Card.ImageLayout.FULL);
//	        card.addImage(R.drawable.puppy_bg);
	        mCards.add(card);

	        card = new Card(this);
	        card.setText("This card has a mosaic of puppies.");
	        card.setFootnote("Aren't they precious?");
	        card.setImageLayout(Card.ImageLayout.LEFT);
//	        card.addImage(R.drawable.puppy_small_1);
//	        card.addImage(R.drawable.puppy_small_2);
//	        card.addImage(R.drawable.puppy_small_3);
	        mCards.add(card);
	    }

	    private class ExampleCardScrollAdapter extends CardScrollAdapter {

	        @Override
	        public int findIdPosition(Object id) {
	            return -1;
	        }

	        @Override
	        public int findItemPosition(Object item) {
	            return mCards.indexOf(item);
	        }

	        @Override
	        public int getCount() {
	            return mCards.size();
	        }

	        @Override
	        public Object getItem(int position) {
	            return mCards.get(position);
	        }

	        @Override
	        public View getView(int position, View convertView, ViewGroup parent) {
	            return mCards.get(position).toView();
	        }
	    }
	
	/**
	 * Captura el Tap para mostrar el menu
	 */
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
          if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
              openOptionsMenu();
              return true;
          }
          return false;
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// No hace nada
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		// No hace nada, para salir hacer swipe hacia abajo
	}
}

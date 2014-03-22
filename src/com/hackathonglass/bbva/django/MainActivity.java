package com.hackathonglass.bbva.django;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.android.glass.app.Card;
import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.timeline.TimelineManager;
import com.google.android.glass.widget.CardScrollAdapter;
import com.google.android.glass.widget.CardScrollView;
import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class MainActivity extends Activity {
	
	 private List<Card> mCards;
	 private List<Flat> mFlats;
	 private CardScrollView mCardScrollView;
	 private String URL = "http://aicu.eui.upm.es/slavy/bbva.py?s=Madrid";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        publishCard(this);
        getFlats();
	}
	
	private void getFlats(){
		Ion.with(this, URL)
		.as(new TypeToken<List<Flat>>(){}) //Tipado
		.setCallback(new FutureCallback<List<Flat>>() {
			@Override
			public void onCompleted(Exception e, List<Flat> arg1) {
				if(e != null);//Si la excepcion no es null, es que algo ha pasao...
				else{
			        mCardScrollView = new CardScrollView(MainActivity.this);
			        setContentView(mCardScrollView);
					unpublishCard(MainActivity.this);
					createCards(arg1);
			        ExampleCardScrollAdapter adapter = new ExampleCardScrollAdapter();
			        mCardScrollView.setAdapter(adapter);
			        mCardScrollView.activate();
				}
			}
		});
	}
	
	private void getLocation(String param1,final String param2){
		Ion.with(this, "http://nominatim.openstreetmap.org/search?q=" + param1.replace(" ", "+") + "+" + param2.replace(" ", "+") + "&format=json")
		.as(new TypeToken<List<Place>>(){})
		.setCallback(new FutureCallback<List<Place>>() {
			@Override
			public void onCompleted(Exception arg0, List<Place> arg1) {
				if(arg0 != null){
					Intent navIntent = new Intent(Intent.ACTION_VIEW,
					        Uri.parse("google.navigation:ll=" + arg1.get(0).getLat() + "," + arg1.get(0).getLon() + "&title=" + param2));
					startActivity(navIntent);
				}
			}
		});
	}

	private void createCards(List<Flat> flats) {
		mCards = new ArrayList<Card>();
		for(Flat f:flats){
			Card card;
			card = new Card(this);
			card.setText(f.getAddress() + ", " + f.getPrice());
			card.setFootnote(f.getMetros() + ", habitaciones " + f.getRooms());
//			card.addImage(Uri.parse(f.getPicture()));
//			card.setImageLayout(Card.ImageLayout.LEFT);
//			 card.addImage(R.drawable.puppy_small_1);
//			 card.addImage(R.drawable.puppy_small_2);
//			 card.addImage(R.drawable.puppy_small_3);
			mCards.add(card);
		}
	}
	
	private LiveCard mLiveCard;

	private void publishCard(Context context) {
	    if (mLiveCard == null) {
	        TimelineManager tm = TimelineManager.from(context);
	        mLiveCard = tm.createLiveCard("card");
	        Intent intent = new Intent(context, MainActivity.class);
	        mLiveCard.setAction(PendingIntent.getActivity(context, 0, intent, 0));
	        mLiveCard.setViews(new RemoteViews(context.getPackageName(), R.layout.loading));
	        mLiveCard.publish(LiveCard.PublishMode.REVEAL);
	    } else {
	        return;
	    }
	}

	private void unpublishCard(Context context) {
	    if (mLiveCard != null) {
	        mLiveCard.unpublish();
	        mLiveCard = null;
	    }
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
	
	private class FlatCardScrollAdapter extends CardScrollAdapter {
		private List<Flat> flats;
		
		public FlatCardScrollAdapter(List <Flat> flats){
			this.flats = flats;
		}

		@Override
		public int findIdPosition(Object id) {
			return -1;
		}

		@Override
		public int findItemPosition(Object item) {
			return flats.indexOf(item);
		}

		@Override
		public int getCount() {
			return flats.size();
		}

		@Override
		public Object getItem(int position) { 
			return flats.get(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View root = getLayoutInflater().inflate(R.layout.place, null);
			ImageView image = (ImageView) root.findViewById(R.id.placeImage);
			Flat flat = flats.get(position);
			Ion.with(image).load(flat.getPicture());
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

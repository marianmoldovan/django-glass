package com.hackathonglass.bbva.django;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.TextView;

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
	 private FlatCardScrollAdapter superAdapter;
	 private String placeCity;
	 private String URL = "http://aicu.eui.upm.es/slavy/bbva.py?s=";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//        publishCard(this);
		setContentView(R.layout.loading);
        placeCity = "Madrid";
        if(getIntent().getExtras() != null)
        	placeCity = getIntent().getExtras().getString("city");
        if(placeCity == null) placeCity = "Madrid";
        getFlats();
	}
	
	private void getFlats(){
		Ion.with(this, URL + placeCity)
		.as(new TypeToken<List<Flat>>(){}) //Tipado
		.setCallback(new FutureCallback<List<Flat>>() {
			@Override
			public void onCompleted(Exception e, List<Flat> arg1) {
				if(e != null);//Si la excepcion no es null, es que algo ha pasao...
				else{
//					unpublishCardpiashfidoàhfs(MainActivity.this);
			        mCardScrollView = new CardScrollView(MainActivity.this);	
			        unpublishCard(MainActivity.this);
			        setContentView(mCardScrollView);
					createCards(arg1);
					superAdapter = new FlatCardScrollAdapter(arg1);
					mCardScrollView.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
							superAdapter.setLastPosition(position);
							openOptionsMenu();
						}
					});
			        mCardScrollView.setAdapter(superAdapter);
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
				if(arg1 != null){
					try {
						Intent navIntent = new Intent(Intent.ACTION_VIEW,Uri.parse("google.navigation:ll=" + arg1.get(0).getLat() + "," + arg1.get(0).getLon() + "&title=" + param2));
						startActivity(navIntent);
					} catch (Exception e) {
						e.printStackTrace();
					}
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

	
	private class FlatCardScrollAdapter extends CardScrollAdapter {
		private List<Flat> flats;
		private int lastPosition;
		
		public FlatCardScrollAdapter(List <Flat> flats){
			this.flats = flats;
		}
		
		public Flat getActualFlat(){
			return flats.get(lastPosition);
		}
		
		public void setLastPosition(int position){
			lastPosition = position;
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
			Ion.with(MainActivity.this, flat.getPicture())
				.withBitmap()
				.intoImageView(image);
			
			TextView address = (TextView) root.findViewById(R.id.placeAddress);
			address.setText( placeCity + ", " +flat.getAddress());
			TextView price = (TextView) root.findViewById(R.id.placePrice);
			price.setText(flat.getPrice());
			TextView data = (TextView) root.findViewById(R.id.placeData);
			data.setText(flat.getMetros() + ", " + flat.getRooms());
			return root;
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()){
			case R.id.action_go: {
				getLocation(placeCity, superAdapter.getActualFlat().getAddress());
				return true;
			}
			case R.id.action_share: {
				Intent shareIntent = ShareCompat.IntentBuilder.from(this)
						.setText(superAdapter.getActualFlat().getUrl()).setSubject("Apartment").getIntent()
						.setPackage("com.android.email");
//				Intent intent = new Intent();
//				intent.setAction(Intent.ACTION_SEND);
//				intent.putExtra(Intent.EXTRA_TEXT, superAdapter.getActualFlat().getUrl());
				try {
					startActivity(shareIntent);
				} catch (Exception e) {
					e.printStackTrace();
				}
//				Intent intent = new Intent();
//				intent.setAction(Intent.ACTION_SEND);
//				intent.putExtra(Intent.EXTRA_TEXT, superAdapter.getActualFlat().getUrl());
//				startActivity(intent);
				return true;
			}
			case R.id.action_more:{
				openWebPage(superAdapter.getActualFlat().getUrl());
				return true;
			}
		}
		return true;
	}
	
	public void openWebPage(String url) {
	    Uri webpage = Uri.parse(url);
	    Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
	    if (intent.resolveActivity(getPackageManager()) != null) {
	        startActivity(intent);
	    }
	}

	@Override
	public void onOptionsMenuClosed(Menu menu) {
		// No hace nada, para salir hacer swipe hacia abajo
	}
}

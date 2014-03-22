package com.hackathonglass.bbva.django;

import java.util.ArrayList;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.speech.RecognizerIntent;
import android.util.Log;

import com.google.android.glass.app.Card;
import com.google.android.glass.timeline.TimelineManager;

public class DjangoService extends Service {
	public DjangoService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		 ArrayList<String> voiceResults = intent.getExtras().getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
		 if(voiceResults.size() > 0){
			 Intent start = new Intent(this, MainActivity.class);
			 start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			 start.putExtra("city", voiceResults.get(0));
			 startActivity(start);
		 }
		 else {
			 Intent start = new Intent(this, MainActivity.class);
			 start.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			 start.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			 start.putExtra("city", "Madrid");
			 startActivity(start);
		 }
		 //		publishStaticCard();
		return super.onStartCommand(intent, flags, startId);
	}

	private void publishStaticCard() {
		Log.wtf("GLASS","DJANGO");
		Card card1 = new Card(this);
		card1.setText("Un momento");
		card1.setFootnote("Buscando viviendas...");
		card1.setImageLayout(Card.ImageLayout.LEFT);
		card1.addImage(R.drawable.bbvalogo);
		TimelineManager.from(this).insert(card1);
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				startActivity(new Intent(DjangoService.this, MainActivity.class));
			}
		}, 2000);
	}
}

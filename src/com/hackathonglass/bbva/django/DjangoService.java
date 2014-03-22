package com.hackathonglass.bbva.django;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.google.android.glass.app.Card;
import com.google.android.glass.timeline.TimelineManager;

public class DjangoService extends Service {
	public DjangoService() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		publishStaticCard();
		return START_STICKY;

	}

	private void publishStaticCard() {
		Card card1 = new Card(this);
		card1.setText("Hello Glass");
		card1.setFootnote("This is my first Card!");
		TimelineManager.from(this).insert(card1);
	}
}

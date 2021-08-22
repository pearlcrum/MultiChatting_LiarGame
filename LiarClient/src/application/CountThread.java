package application;

import javafx.application.Platform;
import javafx.scene.control.Label;

public class CountThread extends Thread {
	
	private Label m;
	private Label s;
	private long sec=60;
	boolean bool=true;
	public CountThread(Label m, Label s, long sec) {
		this.m = m;
		this.s = s;
		this.sec = sec;
	}
	public long getSec() {
		return sec;
	}
	public void setSec(long sec) {
		this.sec = sec;
	}
	public void stopThread() {
		bool=false;
	}
	@Override
	public void run() {
		while(bool) {
			try {
				Thread.sleep(1000);
				sec--;
				if(sec==-1)
					break;
			} catch (Exception e) {
				;
			}
			Long min = sec % 3600 / 60;
			Long second = sec % 60; 
			
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					m.setText(min.toString());
					s.setText(second.toString());
				}
			});
			
		}
		Platform.runLater(()->{
		s.setText("0");
		});
	}
}

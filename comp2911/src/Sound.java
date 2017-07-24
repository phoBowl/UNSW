import java.applet.Applet;
import java.applet.AudioClip;

public class Sound {
	public  AudioClip SLIDING ;//= Applet.newAudioClip(Sound.class.getResource("sliding.wav"));
	//public 	AudioClip GAMEOVER;// = Applet.newAudioClip(Sound.class.getResource("gameover.wav"));
	//public  AudioClip BACK ;//= Applet.newAudioClip(Sound.class.getResource("back.wav"));
	
	private String slidingSound = "sliding.wav";
	public Sound(){
		this.SLIDING = Applet.newAudioClip(Sound.class.getResource(slidingSound));
	}
	public void playSound (AudioClip soundf){
		this.SLIDING.play();
	}
}
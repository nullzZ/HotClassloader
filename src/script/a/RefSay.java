package script.a;

import com.game.script.ISayScript;

/**
 * @author nullzZ
 *
 */
public class RefSay implements ISayScript {
	@Override
	public int getId() {
		return 2;
	}

	@Override
	public void say() {
		System.out.println("RefSay!@@@好使了？");
	}

}

package script;

import com.game.script.ISayScript;

/**
 * @author nullzZ
 *
 */
public class RefSayTow implements ISayScript {

	@Override
	public int getId() {
		return 1;
	}

	@Override
	public void say() {
		System.out.println("RefSayTow!" + a());
	}

	public String a() {
		return "测试3";
	}

}

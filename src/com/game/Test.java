package com.game;

import com.game.script.ISayScript;

/**
 * @author nullzZ
 *
 */
public class Test {

	public Test() {
		try {
			ScriptManager.getInstance().load("E:\\mywork\\HotClassloader\\src\\script.xml",
					"E:\\mywork\\HotClassloader\\script");

			while (true) {
				ISayScript s1 = ScriptManager.getInstance().getScript(1);
				s1.say();/// !!!!!!!!!!!!!
				ISayScript s2 = ScriptManager.getInstance().getScript(2);
				s2.say();/// !!!!!!!!!!!!!
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) throws InterruptedException {
		new Test();
	}

}

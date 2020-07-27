package work.crystalnet.quartz;

import work.crystalnet.quartz.info.UserInfo;

public class CalculateClickTask implements Runnable {

	private final UserInfo info;

	public CalculateClickTask(UserInfo info) {
		this.info = info;
	}

	@Override
	public void run() {
		final int cps = info.getClick();
		info.setCps(cps);
		info.setClick(0);
	}
}

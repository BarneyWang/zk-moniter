package letv.zookeeper.monitor.task.runable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import letv.zookeeper.monitor.constant.Constant;
import letv.zookeeper.monitor.constant.GlobalInstance;
import letv.zookeeper.monitor.exception.SSHException;
import letv.zookeeper.monitor.model.HostPerformanceInfo;
import letv.zookeeper.monitor.model.ZkModelInfo;
import letv.zookeeper.monitor.util.DateUtil;
import letv.zookeeper.monitor.util.SSHUtil;

/**
 * 监控zookeeper服务器机器性能
 * @author wangdi
 *
 */
public class HostPerformanceCollectJob implements Runnable {

	@Override
	public void run() {
		while(true) {
			List<HostPerformanceInfo> list = new ArrayList<HostPerformanceInfo>();
			
			for(ZkModelInfo model : GlobalInstance.zkModelinfoList) {
				HostPerformanceInfo performanceInfo = new HostPerformanceInfo();
				performanceInfo.setIp(model.getIp());
				
				//执行单个服务器命令，获取服务状态信息
				try {
					SSHUtil.sshHostPerformanceInfo(model, performanceInfo);
					SSHUtil.sshHostPerformanceDiskInfo(model, performanceInfo);
				} catch (SSHException e) {
					e.printStackTrace();
				}
				
				list.add(performanceInfo);
			}
			
			GlobalInstance.resetHostPerformanceInfoList(list);
			
			GlobalInstance.dateStrHostPerformance = DateUtil.formatTime(new Date());
			
			try {
				Thread.sleep(Constant.THREAD_SLEEP_TIME);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

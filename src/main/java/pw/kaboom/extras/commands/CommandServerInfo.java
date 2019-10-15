package pw.kaboom.extras;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;

import java.net.InetAddress;

import org.bukkit.ChatColor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

class CommandServerInfo implements CommandExecutor {
	private void sendInfoMessage(CommandSender target, String description, String value) {
		target.sendMessage(
			ChatColor.GRAY + description + ": " +
			ChatColor.WHITE + value
		);
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		try {
			sendInfoMessage(sender, "Hostname",
				InetAddress.getLocalHost().getHostName()
			);
	        sendInfoMessage(sender, "IP address",
				InetAddress.getLocalHost().getHostAddress()
			);
		} catch (Exception exception) {
		}

		sendInfoMessage(sender, "OS name",
			ManagementFactory.getOperatingSystemMXBean().getName()
		);
		sendInfoMessage(sender, "OS architecture",
			ManagementFactory.getOperatingSystemMXBean().getArch()
		);
		sendInfoMessage(sender, "OS version",
			ManagementFactory.getOperatingSystemMXBean().getVersion()
		);
		sendInfoMessage(sender, "Java VM",
			ManagementFactory.getRuntimeMXBean().getVmName()
		);
		sendInfoMessage(sender, "Java version",
			ManagementFactory.getRuntimeMXBean().getSpecVersion() +
			" " +
			ManagementFactory.getRuntimeMXBean().getVmVersion()
		);

		try {
			String[] shCommand = {
				"/bin/sh",
				"-c",
				"cat /proc/cpuinfo | grep 'model name' | cut -f 2 -d ':' | awk '{$1=$1}1' | head -1"
			};

 			Process process = Runtime.getRuntime().exec(shCommand);
			InputStreamReader isr = new InputStreamReader(process.getInputStream());
			BufferedReader br = new BufferedReader(isr);
			String line;

			while ((line = br.readLine()) != null) {
				sendInfoMessage(sender, "CPU model",
					line
				);
			}

			br.close();
		} catch (Exception exception) {
		}

		sendInfoMessage(sender, "CPU cores",
			String.valueOf(Runtime.getRuntime().availableProcessors())
		);
		sendInfoMessage(sender, "CPU load",
			String.valueOf(ManagementFactory.getOperatingSystemMXBean().getSystemLoadAverage())
		);

		long heapUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
		long nonHeapUsage = ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getUsed();
		long memoryMax = (
			ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getMax() + 
			ManagementFactory.getMemoryMXBean().getNonHeapMemoryUsage().getMax()
		);
		long memoryUsage = (heapUsage + nonHeapUsage);

		sendInfoMessage(sender, "Available memory",
			(memoryMax / 1024 / 1024) + " MB"
		);
		sendInfoMessage(sender, "Heap memory usage",
			(heapUsage / 1024 / 1024) + " MB"
		);
		sendInfoMessage(sender, "Non-heap memory usage",
			(nonHeapUsage / 1024 / 1024) + " MB"
		);
		sendInfoMessage(sender, "Total memory usage",
			(memoryUsage / 1024 / 1024) + " MB"
		);
		
		long minutes = (ManagementFactory.getRuntimeMXBean().getUptime() / 1000) / 60;
		long seconds = (ManagementFactory.getRuntimeMXBean().getUptime() / 1000) % 60;

		sendInfoMessage(sender, "Server uptime",
			minutes + " minute(s) " +
			seconds + " second(s)"
		);
		return true;
	}
}

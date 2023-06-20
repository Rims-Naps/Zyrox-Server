package com.zenyte.game.world.entity.player;

import com.google.gson.annotations.Expose;
import com.zenyte.game.HardwareInfo;
import com.zenyte.game.packet.Session;
import com.zenyte.game.util.Utils;
import com.zenyte.network.login.packet.LoginPacketIn;
import lombok.Getter;
import lombok.Setter;
import lombok.val;

import java.net.InetSocketAddress;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class PlayerInformation {

	/**
	 * The channel which this connection communicates through.
	 */
	@Getter @Setter 
	private transient Session session;

	/**
	 * The player's user name
	 */
	@Getter
	private String username;


	/**
	 * The player's display name.
	 */
	@Getter
	private String displayname;

	private void setDisplayname(final String displayName) {
	    this.displayname = Utils.formatString(displayName == null || displayName.isEmpty() ? username : displayName);
    }

	@Getter private transient String plainPassword;

	/**
	 * The last known login address of a player
	 */
	@Expose
	@Getter
	@Setter
	private String ip;

	/**
	 * The register date of the player.
	 */
	@Expose
	@Getter
	@Setter
	private LocalDate registryDate;

	/**
	 * The display mode of the user.
	 */
	@Expose
	@Getter
	@Setter
	private int mode;

	@Getter
	private transient HardwareInfo hardware;

	@Getter
	private transient Device device;

	/**
	 * This id is binded to the player and will NEVER change. This is their branded id.
	 */
	@Expose
	@Getter
	@Setter
	private int userIdentifier;

	public PlayerInformation(final Session session, final LoginPacketIn query) {
		this.session = session;
		username = query.getUsername();
		setDisplayname(username);
		//password = query.getPassword();
		this.plainPassword = query.getPassword();
		mode = query.getMode();
		hardware = query.getHardwareInfo();
		userIdentifier = -1;
		registryDate = LocalDate.now();
		device = query.getDevice();
	}

	public void setPlayerInformation(final PlayerInformation details) {
		username = details.getUsername();
		//setPassword(details.getPassword());
		setDisplayname(details.getDisplayname());
		setUserIdentifier(details.getUserIdentifier());
		setIp(details.getIp());
		setRegistryDate(details.getRegistryDate());
	}

	public int getDaysSinceRegistry() {
		return (int) registryDate.until(LocalDate.now(), ChronoUnit.DAYS);
	}

	public String getIpFromChannel() {
        val remoteAddress = session.getChannel().remoteAddress();
        if (remoteAddress instanceof InetSocketAddress) {
            val socketAddress = (InetSocketAddress) remoteAddress;
            return socketAddress.getAddress().getHostAddress();
        }
	    return "null";
	}
	
	public boolean isOnMobile() {
		return device.equals(Device.MOBILE);
	}

    /**
     * TODO: figure out a solution for this.
     * @return
     */
    public String getMACFromChannel() {
        return this.session.getRequest().getMacAddress();
    }
}

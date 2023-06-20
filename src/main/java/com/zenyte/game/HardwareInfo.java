package com.zenyte.game;

import com.zenyte.network.io.ByteBufUtil;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.val;

public class HardwareInfo {
	
	@Getter private int osId, osVersion, javaVendorId,
            javaVersionMajor, javaVersionMinor, javaVersionUpdate, heap,
            logicalProcessors, physicalMemory, clockSpeed, graphicCardReleaseMonth,
            graphicCardReleaseYear, cpuCount, cpuBrandType, cpuModel;
	@Getter private String graphicCardManufacture, graphicCardName, dxVersion, cpuManufacture, cpuName;
	@Getter private final int[] cpuFeatures = new int[3];
	@Getter private boolean arch64Bit, isApplet;

	public HardwareInfo(final ByteBuf buffer) {
		decode(buffer);
	}

	@Override
	public int hashCode() {
		return clockSpeed + (osId << 12) + (osVersion << 16) + (logicalProcessors << 24);
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof HardwareInfo)) {
			return false;
		}
		final HardwareInfo o = (HardwareInfo) other;
		return osId == o.osId && arch64Bit == o.arch64Bit && osVersion == o.osVersion && javaVendorId == o.javaVendorId && javaVersionMajor == o.javaVersionMajor && javaVersionMinor == o.javaVersionMinor && javaVersionUpdate == o.javaVersionUpdate && isApplet == o.isApplet && heap == o.heap && logicalProcessors == o.logicalProcessors && physicalMemory == o.physicalMemory && clockSpeed == o.clockSpeed;
	}

	/**
	 * Decode's hardware information.
	 */
	@SuppressWarnings("unused")
	private void decode(final ByteBuf buffer) {
		val version = buffer.readUnsignedByte();
		/*if (version != 7) {
			throw new RuntimeException("Unsupported version: " + version);
		}*/
		
		osId = buffer.readUnsignedByte();
		arch64Bit = buffer.readUnsignedByte() == 1;
		osVersion = buffer.readUnsignedByte();
		javaVendorId = buffer.readUnsignedByte();
		javaVersionMajor = buffer.readUnsignedByte();
		javaVersionMinor = buffer.readUnsignedByte();
		javaVersionUpdate = buffer.readUnsignedByte();
		isApplet = buffer.readUnsignedByte() == 0;
		heap = buffer.readUnsignedShort();
		logicalProcessors = buffer.readUnsignedByte(); // only if > java1.3
		physicalMemory = ByteBufUtil.readMedium(buffer);
		clockSpeed = buffer.readUnsignedShort();

		graphicCardManufacture = ByteBufUtil.readJAGString(buffer);
		graphicCardName = ByteBufUtil.readJAGString(buffer);
		final String empty3 = ByteBufUtil.readJAGString(buffer);
		dxVersion = ByteBufUtil.readJAGString(buffer);
		graphicCardReleaseMonth = buffer.readUnsignedByte();
		graphicCardReleaseYear = buffer.readUnsignedShort();
		cpuManufacture = ByteBufUtil.readJAGString(buffer);
		cpuName = ByteBufUtil.readJAGString(buffer);

		cpuCount = buffer.readUnsignedByte();
		cpuBrandType = buffer.readUnsignedByte();

		for (int index = 0; index < cpuFeatures.length; index++) {
			cpuFeatures[index] = buffer.readInt();
		}
		final int cpuModel = buffer.readInt();
		ByteBufUtil.readJAGString(buffer);//unknown
	}

}

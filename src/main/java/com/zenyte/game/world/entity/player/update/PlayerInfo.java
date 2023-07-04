package com.zenyte.game.world.entity.player.update;

import com.zenyte.Constants;
import com.zenyte.game.packet.GamePacketEncoder;
import com.zenyte.game.packet.ServerProt;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.EntityList;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.player.LogLevel;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.update.mask.*;
import com.zenyte.game.world.region.Chunk;
import com.zenyte.game.world.region.LocationMap;
import com.zenyte.network.game.packet.GamePacketOut;
import com.zenyte.network.io.BitBuffer;
import com.zenyte.network.io.RSBuffer;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import lombok.val;

import org.jetbrains.annotations.NotNull;

/**
 * @author Kris | 1. veebr 2018 : 22:11.29
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server
 * profile</a>
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status
 * profile</a>
 */
public final class PlayerInfo implements GamePacketEncoder {

    private static final int MAX_PLAYER_ADD = 40;
    private static final int LIMITED_PLAYERS_COUNT = 0xFF;
    private static final int MAX_BYTES = 40000;
    private static final boolean LIMITED_MODE = true;
    private static final EntityList<Player> players = World.getPlayers();
    /**
     * An array of update masks in the respective order they're read in the client. PS: Order must be preserved!
     */
    private static final UpdateMask[] masks = new UpdateMask[]{
            new HitMask(), new GraphicsMask(), new TemporaryMovementMask(), new ForceMovementMask(),
            new ForceChatMask(), new FaceLocationMask(), new AppearanceMask(), new FaceEntityMask(),
            new MovementMask(), new ChatMask(), new NametagMask(), new AnimationMask()
    };
    /**
     * The length of the masks.
     */
    private static final int length = masks.length;
    private Player player;
    /**
     * <p>Activity flags are used to determine whether the player was updated this cycle, or skipped entirely due
     * to inactivity. The method is used to group together inactive & active players respectively, allowing the
     * server to skip larger amounts of players due to probability, effectively saving bandwidth in the long run,
     * as every skipping call writes a number of bits on its own.</p>
     */
    private byte[] activityFlags;
    /**
     * <p>The indexes of the players currently in our viewport.</p>
     */
    private int[] localIndexes;
    /**
     * <p>The indexes of the players currently outside our viewport.</p>
     */
    private int[] externalIndexes;
    /**
     * <p>The players currently in our viewport.</p>
     */
    private Player[] localPlayers;
    /**
     * <p>Position multipliers, used to transmit coordinates when they exceed 8191 in either direction.
     * RS only transmits 13 bits for x & y coordinates, meaning maximum value allowed is 2^13 - 1 = 8191.
     * For that reason, if it is needed to transmit coordinates higher than that, a multiplier must be transmitted
     * to the client which will then transform the coordinate to (multiplier * 8192) + remainderCoordinate.</p>
     * <p>Maximum allowed coordinates are 16383 in both directions; going past that will not render characters anymore.
     * </p>
     * <p>Coordinates were transmitted on a region level prior to deadman mode, which was why the method was
     * initially created.</p>
     */
    private int[] multipliers;
    /**
     * <p>The secondary per-player basis buffer for masks.</p>
     */
    private RSBuffer smallMaskBuffer;
    /**
     * <p>The primary buffer for masks.</p>
     */
    private RSBuffer largeMaskBuffer;
    /**
     * <p>The primary bitbuffer of GPI.</p>
     */
    private BitBuffer bitBuffer;
    /**
     * <p>The amount of players currently outside our viewport.</p>
     */
    private int externalIndexesCount;
    /**
     * <p>The amount of players currently inside our viewport.</p>
     */
    private int localIndexesCount;
    /**
     * <p>The amount of players added to the viewport during this cycle.</p>
     */
    private int addedPlayersCount;
    /**
     * <p>The primary buffer of GPI.</p>
     */
    private RSBuffer buffer;

    /**
     * <p>A linked hashset of players that's used when there are more than 255 players in the viewport of the player,
     * so we need to filter out only the 255 closest to us. Linked to preserve a specific order.</p>
     */
    private ObjectLinkedOpenHashSet<Player> limitedPlayers;

    /**
     * Whether or not the viewport is limited to 255 players.
     */
    private boolean limitedMode;

    @Override
    public boolean prioritized() {
        return true;
    }

    public void reset() {
        player = null;
        activityFlags = null;
        localIndexes = null;
        externalIndexes = null;
        localPlayers = null;
        multipliers = null;
        smallMaskBuffer.clear();
        smallMaskBuffer = null;
        largeMaskBuffer.clear();
        largeMaskBuffer = null;
        bitBuffer.reset();
        bitBuffer = null;
        buffer.clear();
        buffer = null;
        limitedPlayers.clear();
        limitedPlayers = null;
    }

    public PlayerInfo(final Player player) {
        this.player = player;
        activityFlags = new byte[2048];
        localPlayers = new Player[2048];
        localIndexes = new int[2048];
        multipliers = new int[2048];
        externalIndexes = new int[2048];
        smallMaskBuffer = new RSBuffer(0xFF);
        largeMaskBuffer = new RSBuffer(0xFF);
        buffer = new RSBuffer(750);
        bitBuffer = new BitBuffer(750, Constants.MAX_SERVER_BUFFER_SIZE);
        limitedPlayers = new ObjectLinkedOpenHashSet<>(0xFF);
    }

    /**
     * Initializes the GPI by transmitting coordinate multipliers for all of the players across the world.
     *
     * @param buffer the buffer to write the information to.
     */
    public synchronized void init(final RSBuffer buffer) {
        bitBuffer.write(30, player.getLocation().getPositionHash());
        localPlayers[player.getIndex()] = player;
        localIndexes[localIndexesCount++] = player.getIndex();
        for (var playerIndex = 1; playerIndex < 2048; playerIndex++) {
            if (playerIndex == player.getIndex()) {
                continue;
            }
            val player = players.getDirect(playerIndex);
            bitBuffer.write(18, multipliers[playerIndex] = (player == null ? 0 : player.getLocation().get18BitHash()));
            externalIndexes[externalIndexesCount++] = playerIndex;
        }
        buffer.writeBits(bitBuffer);
    }

    /**
     * Prepares and caches the GPI buffer for this player, processing all local and external players in the process.
     *
     * @return this for chaining.
     */
    public synchronized PlayerInfo cache() {
        addedPlayersCount = 0;
        buffer.clear();
        largeMaskBuffer.clear();
        limitedPlayers.clear();
        limitedMode = false;
        prefetch();
        processLocalPlayers(true);
        processLocalPlayers(false);
        processOutsidePlayers(true);
        processOutsidePlayers(false);
        buffer.writeBytes(largeMaskBuffer);
        localIndexesCount = 0;
        externalIndexesCount = 0;
        for (var playerIndex = 1; playerIndex < 2048; playerIndex++) {
            activityFlags[playerIndex] >>= 1;
            if (localPlayers[playerIndex] == null) {
                externalIndexes[externalIndexesCount++] = playerIndex;
            } else {
                localIndexes[localIndexesCount++] = playerIndex;
            }
        }
        return this;
    }

    /**
     * Prefetches a list of players closest to us when there are more than 255 players in the viewport.
     * TODO: Convert the LocationMap to function at the chunk level. No reason to separate them.
     */
    private void prefetch() {
        if (!LIMITED_MODE || player.isHeatmap() || World.getPlayers().size() < LIMITED_PLAYERS_COUNT)
            return;
        val px = player.getX();
        val py = player.getY();
        val z = player.getPlane();
        int count = 0;
        loop : for (int x = px - 15; x <= px + 15; x += 8) {
            for (int y = py - 15; y <= py + 15; y += 8) {
                val hash = Chunk.getChunkHash(x >> 3, y >> 3, z);
                count += World.getChunk(hash).getPlayers().size();
                if (count > LIMITED_PLAYERS_COUNT) {
                    break loop;
                }
            }
        }
        if (count <= LIMITED_PLAYERS_COUNT)
            return;
        limitedMode = true;
        LocationMap.iterate(player, c -> {
            if (limitedPlayers.size() + c.size() < LIMITED_PLAYERS_COUNT) {
                limitedPlayers.addAll(c);
                limitedPlayers.remove(player);
                return true;
            }
            for (val player : c) {
                if (player == this.player) {
                    continue;
                }
                limitedPlayers.add(player);
                if (limitedPlayers.size() >= LIMITED_PLAYERS_COUNT)
                    return false;
            }
            return true;
        });
    }

    /**
     * @param p           the player being removed.
     * @param playerIndex the index of the player being removed. If the player needs to be removed and the index
     *                    isn't -1, we also write the necessary information in the buffer.
     * @return whether or not the requested player needs to be removed from the local players list.
     */
    private boolean remove(@NotNull final Player p, final int playerIndex) {
        if (p == player) {
            return false;
        }
        if (p.isFinished() || p.isHidden() || !player.isHeatmap() && (!player.isVisibleInViewport(p) || limitedMode && !limitedPlayers.contains(p))) {
            if (playerIndex != -1) {
                bitBuffer.write(1, 1);
                bitBuffer.write(1, 0);
                bitBuffer.write(2, 0);
                val hash = p.getLocation().get18BitHash();
                if (hash == multipliers[playerIndex]) {
                    bitBuffer.write(1, 0);
                } else {
                    bitBuffer.write(1, 1);
                    updatePositionMultiplier(multipliers[playerIndex], multipliers[playerIndex] = hash);
                }
                localPlayers[playerIndex] = null;
            }
            return true;
        }
        return false;
    }

    /**
     * @param p           the player being added.
     * @param playerIndex the index of the player being added. If the player needs to be added and the index isn't
     *                    -1, we also write the necessary information in the buffer.
     * @return whether or not the requested player needs to be added to the local players list.
     */
    private boolean add(final Player p, final int playerIndex) {
        if (!(p == null || player == p || p.isFinished() || p.isHidden() || !player.isHeatmap() && !player.isVisibleInViewport(p) || addedPlayersCount >=
        MAX_PLAYER_ADD || buffer.readableBytes() + largeMaskBuffer.readableBytes() >= (MAX_BYTES - 0xFF))) {
            if (!player.isHeatmap() && limitedMode && !limitedPlayers.contains(p))
                return false;
            if (playerIndex != -1) {
                bitBuffer.write(1, 1);
                bitBuffer.write(2, 0);
                val multiplier = p.getLocation().get18BitHash();
                if (multiplier == multipliers[playerIndex]) {
                    bitBuffer.write(1, 0);
                } else {
                    bitBuffer.write(1, 1);
                    updatePositionMultiplier(multipliers[playerIndex], multipliers[playerIndex] = multiplier);
                }
                bitBuffer.write(13, p.getX());
                bitBuffer.write(13, p.getY());
                val updateMasks = !player.isHeatmap() || player.getLocation().getDistance(p.getLocation()) <= player.getHeatmapRenderDistance();
                bitBuffer.write(1, updateMasks ? 1 : 0);
                if (updateMasks) {
                    appendUpdateBlock(p, true);
                }
                addedPlayersCount++;
                localPlayers[p.getIndex()] = p;
                activityFlags[playerIndex] = (byte) (activityFlags[playerIndex] | 0x2);
            }
            return true;
        }
        return false;
    }

    /**
     * Appends the position changes of the requested player. As of right now, the last if-block is never reached as
     * the offsets can never exceed value 1.
     *
     * @param lastPosition    the last position multiplier transmitted to the client.
     * @param currentPosition the current position multiplier.
     */
    private void updatePositionMultiplier(final int lastPosition, final int currentPosition) {
        val lastY = lastPosition & 0xFF;
        val lastX = lastPosition >> 8 & 0xFF;
        val lastPlane = lastPosition >> 16;

        val currentY = currentPosition & 0xFF;
        val currentX = currentPosition >> 8 & 0xFF;
        val currentPlane = currentPosition >> 16;

        val yOffset = currentY - lastY;
        val xOffset = currentX - lastX;
        val planeOffset = (currentPlane - lastPlane) & 0x3;

        if (currentX == lastX && currentY == lastY) {
            bitBuffer.write(2, 1);
            bitBuffer.write(2, planeOffset);
        } else if (Math.abs(xOffset) <= 1 && Math.abs(yOffset) <= 1) {
            bitBuffer.write(2, 2);
            bitBuffer.write(2, planeOffset);
            bitBuffer.write(3, Utils.getMoveDirection(xOffset, yOffset));
        } else {
            bitBuffer.write(2, 3);
            bitBuffer.write(2, planeOffset);
            bitBuffer.write(8, xOffset & 0xFF);
            bitBuffer.write(8, yOffset & 0xFF);
        }
    }

    /**
     * Processes the players outside of our viewport; either adds, updates or skips them if necessary to do so.
     *
     * @param inactivePlayers whether or not we loop the inactive or active players.
     */
    private void processOutsidePlayers(final boolean inactivePlayers) {
        var skip = 0;
        for (var i = 0; i < externalIndexesCount; i++) {
            val playerIndex = externalIndexes[i];
            if (inactivePlayers == ((0x1 & activityFlags[playerIndex]) == 0)) {
                continue;
            }
            if (skip > 0) {
                skip--;
                activityFlags[playerIndex] = (byte) (activityFlags[playerIndex] | 0x2);
                continue;
            }
            val p = players.getDirect(playerIndex);
            if (!add(p, playerIndex)) {
                val hash = p == null ? multipliers[playerIndex] : p.getLocation().get18BitHash();
                if (hash != multipliers[playerIndex]) {
                    bitBuffer.write(1, 1);
                    updatePositionMultiplier(multipliers[playerIndex], hash);
                    multipliers[playerIndex] = hash;
                } else {
                    bitBuffer.write(1, 0);
                    skip(skip += getSkippedExternalPlayers(i, inactivePlayers));
                    activityFlags[playerIndex] = (byte) (activityFlags[playerIndex] | 0x2);
                }
            }
        }
        buffer.writeBits(bitBuffer);

        if (skip != 0) {
            throw new IllegalStateException(inactivePlayers ? "NSN2" : "NSN3");
        }
    }

    /**
     * @param index           the current index in the loop.
     * @param inactivePlayers whether we check inactive or active players.
     * @return the amount of external players we can skip.
     */
    private int getSkippedExternalPlayers(final int index, final boolean inactivePlayers) {
        int skip = 0;
        for (var i = index + 1; i < externalIndexesCount; i++) {
            val externalIndex = externalIndexes[i];
            if (inactivePlayers == ((0x1 & activityFlags[externalIndex]) == 0)) {
                continue;
            }
            val externalPlayer = players.getDirect(externalIndex);
            val externalHash = externalPlayer == null ? multipliers[externalIndex] : externalPlayer.getLocation().get18BitHash();
            if (add(externalPlayer, -1) || externalHash != multipliers[externalIndex]) {
                break;
            }
            skip++;
        }
        return skip;
    }

    /**
     * Processes the players inside of our viewport; either removes, updates or skips them if necessary to do so.
     *
     * @param inactivePlayers whether or not we loop the inactive or active players.
     */
    private void processLocalPlayers(final boolean inactivePlayers) {
        var skip = 0;
        for (var i = 0; i < localIndexesCount; i++) {
            val playerIndex = localIndexes[i];
            if (inactivePlayers == ((0x1 & activityFlags[playerIndex]) != 0)) {
                continue;
            }
            if (skip > 0) {
                skip--;
                activityFlags[playerIndex] = (byte) (activityFlags[playerIndex] | 0x2);
                continue;
            }
            val p = localPlayers[playerIndex];

            if (!remove(p, playerIndex)) {
                val walkDir = p.getWalkDirection();
                val runDir = p.getRunDirection();
                var update = p.getUpdateFlags().isUpdateRequired();
                if (player.isHeatmap()) {
                    if (player.getLocation().getDistance(p.getLocation()) > player.getHeatmapRenderDistance()) {
                        update = false;
                    }
                }
                if (update) {
                    appendUpdateBlock(p, false);
                }
                val teleported = p.isTeleported();

                if (teleported || walkDir != -1 || runDir != -1) {
                    bitBuffer.write(1, 1);
                    bitBuffer.write(1, update ? 1 : 0);
                    bitBuffer.write(2, 3);
                    val location = p.getLocation();
                    val lastLocation = p.getLastLocation();
                    val xOffset = location.getX() - lastLocation.getX();
                    val yOffset = location.getY() - lastLocation.getY();
                    val planeOffset = location.getPlane() - lastLocation.getPlane();
                    multipliers[playerIndex] = location.get18BitHash();
                    if (Math.abs(xOffset) < 16 && Math.abs(yOffset) < 16) {
                        bitBuffer.write(1, 0);
                        bitBuffer.write(2, planeOffset & 0x3);
                        bitBuffer.write(5, xOffset & 0x1F);
                        bitBuffer.write(5, yOffset & 0x1F);
                    } else {
                        bitBuffer.write(1, 1);
                        bitBuffer.write(2, planeOffset & 0x3);
                        bitBuffer.write(14, xOffset & 0x3FFF);
                        bitBuffer.write(14, yOffset & 0x3FFF);
                    }
                } else if (update) {
                    bitBuffer.write(1, 1);
                    bitBuffer.write(1, 1);
                    bitBuffer.write(2, 0);
                } else {
                    bitBuffer.write(1, 0);
                    skip(skip += getSkippedLocalPlayers(i, inactivePlayers));
                    activityFlags[playerIndex] = (byte) (activityFlags[playerIndex] | 0x2);
                }
            }
        }
        buffer.writeBits(bitBuffer);

        if (skip != 0) {
            throw new IllegalStateException(inactivePlayers ? "NSN0" : "NSN1");
        }
    }

    /**
     * @param index           the current index in the loop.
     * @param inactivePlayers whether we check inactive or active players.
     * @return the amount of local players we can skip.
     */
    private int getSkippedLocalPlayers(final int index, final boolean inactivePlayers) {
        int skip = 0;
        for (var i = index + 1; i < localIndexesCount; i++) {
            val localIndex = localIndexes[i];
            if (inactivePlayers == ((0x1 & activityFlags[localIndex]) != 0)) {
                continue;
            }
            val localPlayer = localPlayers[localIndex];
            if (remove(localPlayer, -1) || localPlayer.getWalkDirection() != -1
                    || localPlayer.getRunDirection() != -1 || localPlayer.getUpdateFlags().isUpdateRequired()) {
                break;
            }
            skip++;
        }
        return skip;
    }

    /**
     * Write the amount of players skipped into the buffer.
     *
     * @param count the amount of players skipped.
     */
    private void skip(final int count) {
        if (count == 0) {
            bitBuffer.write(2, 0);
        } else if (count < 32) {
            bitBuffer.write(2, 1);
            bitBuffer.write(5, count);
        } else if (count < 256) {
            bitBuffer.write(2, 2);
            bitBuffer.write(8, count);
        } else {
            bitBuffer.write(2, 3);
            bitBuffer.write(11, count);
        }
    }

    /**
     * Appends the update block into the small mask buffer, and then into the large mask buffer.
     *
     * @param p     the player whose block is being written.
     * @param added whether or not the player was added to the viewport during this cycle.
     */
    private void appendUpdateBlock(final Player p, final boolean added) {
        val flags = p.getUpdateFlags();
        var flag = 0;
        smallMaskBuffer.clear();
        for (var i = 0; i < length; i++) {
            val mask = masks[i];
            if (mask.apply(player, p, flags, added)) {
                flag |= mask.getFlag().getPlayerMask();
                mask.writePlayer(smallMaskBuffer, player, p);
            }
        }

        if (flag >= 0xFF) {
            flag |= 0x8;
        }

        largeMaskBuffer.writeByte(flag);

        if (flag >= 0xFF) {
            largeMaskBuffer.writeByte(flag >> 8);
        }
        largeMaskBuffer.writeBytes(smallMaskBuffer);
    }

    @Override
    public GamePacketOut encode() {
        return new GamePacketOut(ServerProt.PLAYER_INFO, buffer);
    }

    @Override
    public LogLevel level() {
        return LogLevel.LOW_PACKET;
    }

}
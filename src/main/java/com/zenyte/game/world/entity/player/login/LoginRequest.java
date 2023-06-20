package com.zenyte.game.world.entity.player.login;

import com.zenyte.Constants;
import com.zenyte.Game;
import com.zenyte.api.client.query.ApiIPCheck;
import com.zenyte.api.client.query.Valid2FACodeQuery;
import com.zenyte.cores.CoresManager;
import com.zenyte.game.tasks.WorldTasksManager;
import com.zenyte.game.util.BCrypt;
import com.zenyte.game.util.TextUtils;
import com.zenyte.game.util.TimeUnit;
import com.zenyte.game.util.Utils;
import com.zenyte.game.world.World;
import com.zenyte.game.world.entity.Location;
import com.zenyte.game.world.entity.player.Player;
import com.zenyte.game.world.entity.player.PlayerInformation;
import com.zenyte.game.world.entity.player.punishments.PunishmentManager;
import com.zenyte.game.world.entity.player.punishments.PunishmentType;
import com.zenyte.game.world.region.Area;
import com.zenyte.network.ClientResponse;
import com.zenyte.network.NetworkConstants;
import com.zenyte.network.game.GameHandler;
import com.zenyte.network.game.codec.GameDecoder;
import com.zenyte.network.game.codec.GameEncoder;
import com.zenyte.network.login.LoginHandler;
import com.zenyte.network.login.codec.LoginDecoder;
import com.zenyte.network.login.codec.LoginEncoder;
import com.zenyte.network.login.packet.LoginPacketOut;
import com.zenyte.network.login.packet.inc.LoginType;
import io.netty.channel.ChannelFutureListener;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.logging.log4j.util.Strings;

import java.util.Arrays;
import java.util.Objects;

/**
 * @author Kris | 3. aug 2018 : 23:56:59
 * @see <a href="https://www.rune-server.ee/members/kris/">Rune-Server
 * profile</a>
 * @see <a href="https://rune-status.net/members/kris.354/">Rune-Status
 * profile</a>
 */
@Slf4j
public class LoginRequest {

    @Getter private final PlayerInformation info;
    private ClientResponse response;
    @Getter private final long requestTime;

    public LoginRequest(final PlayerInformation info, final long requestTime) {
        this.info = info;
        this.requestTime = requestTime;
    }

    public boolean checkPreconditions() {
        if ((response = getResponseCode(null)) != ClientResponse.LOGIN_OK) {
            sendFailureResponse(response);
            return false;
        }
        return true;
    }


    public void log(final Player player, final boolean timeout) {
        if (timeout) {
            sendFailureResponse(ClientResponse.LOGIN_EXCEEDED);
            return;
        }
        if ((response = getResponseCode(player)) != ClientResponse.LOGIN_OK) {
            sendFailureResponse(response);
            return;
        }
        if (player == null) {
            sendFailureResponse(response = ClientResponse.ERROR_LOADING_PROFILE);
            return;
        }
        if (info.getSession().getRequest().getType() == LoginType.RECONNECT_LOGIN_CONNECTION) {
            World.getPlayer(info.getUsername()).ifPresent(p -> {
                p.finish();
                World.removePlayer(p);
                CoresManager.getLoginManager().save(p);
                CoresManager.getLoginManager().load(requestTime, info, this::postLogin);
            });
            return;
        }
        CoresManager.getLoginManager().save(player);
        postLogin(player);
    }

    private void postLogin(final Player player) {
        player.createLogger();
        WorldTasksManager.schedule(() -> {
            val reconnection = info.getSession().getRequest().getType() == LoginType.RECONNECT_LOGIN_CONNECTION;
            val alreadyOnline = World.containsPlayer(info.getUsername());
            if (!reconnection && alreadyOnline) {
                sendFailureResponse(ClientResponse.ALREADY_ONLINE);
                return;
            }
            info.getSession().setPlayer(player);
            if (alreadyOnline) {
                World.getPlayer(info.getUsername()).ifPresent(p -> {
                    p.finish();
                    World.removePlayer(p);
                });
            }
            World.addPlayer(player);
            sendSuccessfulResponse(player, () -> {
                val location = player.getNumericAttribute("ON_ENTER_LOCATION");
                if (location.intValue() != 0) {
                    player.forceLocation(new Location(location.intValue()));
                    player.getAttributes().remove("ON_ENTER_LOCATION");
                }
                val lastDynamicArea = player.getAttributes().remove("LAST_DYNAMIC_AREA");
                if (lastDynamicArea != null) {
                    val listener = Area.onLoginAttachments.get(lastDynamicArea.toString());
                    if (listener != null) {
                        listener.accept(player);
                    }
                }
                player.loadMapRegions();
                player.setRunning(true);
                player.onLogin();
            });
        });
    }

    private ClientResponse getResponseCode(final Player player) {
        val session = info.getSession();
        val request = session.getRequest();

        if (request.getVersion() != NetworkConstants.REVISION) {
            return ClientResponse.CLIENT_UPDATED;
        }

        if (request.getSubVersion() != NetworkConstants.SUB_VERSION_DESKTOP && request.getSubVersion() != NetworkConstants.SUB_VERSION_MOBILE) {
            return ClientResponse.CLIENT_UPDATED;
        }
        val crc = request.getCrc();
        for (int i = crc.length - 1; i >= 0; i--) {
            //Index 16 is never updated so ignore it entirely.
            if (i == 16) {
                continue;
            }
            if (crc[i] != Game.crc[i]) {
                return ClientResponse.SERVER_UPDATED;
            }
        }

        if (!request.getSessionToken().equals("ElZAIrq5NpKN6D3mDdihco3oPeYN2KFy2DCquj7JMmECPmLrDP3Bnw") && !request.getSessionToken().equals("p1cxIGuofoKN6D3mDdihco3oPeYN2KFy2DCquj7JMmEKzxp0igXplg")) {
            return ClientResponse.BAD_SESSION_ID;
        }

        if (World.getPlayers().size() >= NetworkConstants.PLAYER_CAP) {
            return ClientResponse.WORLD_FULL;
        }

        if (World.isUpdating()) {
            if (World.getUpdateTimer() < TimeUnit.MINUTES.toTicks(1)) {
                return ClientResponse.UPDATE_IN_PROGRESS;
            }
        }

        val username = info.getUsername();
        if (username.length() > 12 || !TextUtils.isValidName(username)) {
            return ClientResponse.INVALID_USERNAME_OR_PASSWORD;
        }

        if (!Constants.WORLD_PROFILE.isDevelopment() && Constants.WORLD_PROFILE.isBeta() && !Constants.OPEN_BETA) { //!InvitedPlayersList.invitedPlayers.contains(username.toLowerCase())
            return ClientResponse.CLOSED_BETA;
        }

        val ip = info.getIpFromChannel();
        val punishment = PunishmentManager.isPunishmentActive(username,
                ip, info.getMACFromChannel(), PunishmentType.BAN);
        if (punishment.isPresent()) {
            return ClientResponse.BANNED;
        }
    
        if (Constants.WORLD_PROFILE.isDevelopment() || !Constants.WORLD_PROFILE.isVerifyPasswords()) {
            return ClientResponse.LOGIN_OK;
        }
        if (player != null) {
            if (Constants.WHITELISTING) {
                if (!Constants.whitelistedUsernames.contains(info.getUsername())) {
                    return ClientResponse.UNEXPECTED_LOGINSERVER_RESPONSE;
                }
            }
            if (Constants.WORLD_PROFILE.getApi().isEnabled() || Constants.WORLD_PROFILE.isVerifyPasswords()) {
                if (Constants.ANTIKNOX) {
                    if (!ApiIPCheck.validIPs.contains(ip)) {
                        val ipCheck = new ApiIPCheck();
                        val result = ipCheck.execute(ip);
                        if (!result) {
                            log.info("Login blocked by antiknox: " + ip);
                            return ClientResponse.COMPUTER_ADDRESS_BLOCKED;
                        }
                        ApiIPCheck.validIPs.add(ip);
                    }
                }
            }
            val passwordResponse = checkPw(info, player.getTemporaryAttributes().get("hashed password"));
            if (passwordResponse.equals("false") || passwordResponse.equals("empty")) {
                return ClientResponse.INVALID_USERNAME_OR_PASSWORD;
            }
            if (passwordResponse.isEmpty()) {
                return ClientResponse.ACCOUNT_DOES_NOT_EXIST;
            }
            if (!Constants.WORLD_PROFILE.isBeta() && (Constants.WORLD_PROFILE.getApi().isEnabled() || Constants.WORLD_PROFILE.isVerifyPasswords())) {
                val twoFactorAuthentication = Objects.equals(player.getTemporaryAttributes().get("two factor authentication"), true);
                player.getAuthenticator().setEnabled(twoFactorAuthentication);
                if (twoFactorAuthentication) {
                    val type = info.getSession().getRequest().getAuthType();
                    switch (type) {
                        case NORMAL:
                            return ClientResponse.AUTHENTICATOR;
                        case TRUSTED_AUTHENTICATION:
                        case UNTRUSTED_AUTHENTICATION:
                            if (info.getSession().getRequest().getAuthenicatorCode() > 0) {
                                val valid = new Valid2FACodeQuery(info.getUserIdentifier(), Integer.toString(info.getSession().getRequest().getAuthenicatorCode())).execute();
                                if (valid) {
                                    if (type == LoginDecoder.AuthType.TRUSTED_AUTHENTICATION) {
                                        player.getAuthenticator().trust();
                                    }
                                    break;
                                }
                                return ClientResponse.WRONG_AUTHENICATOR_CODE;
                            }
                            return ClientResponse.AUTHENTICATOR;
                        case TRUSTED_COMPUTER:
                            val result = player.getAuthenticator().validate(info.getSession().getRequest().getPcIdentifier());
                            if (result == ClientResponse.LOGIN_OK) {
                                break;
                            }
                            return ClientResponse.AUTHENTICATOR;
                    }
                }
            }
        }
        if (World.containsPlayer(username)) {
            if (info.getSession().getRequest().getType() == LoginType.RECONNECT_LOGIN_CONNECTION) {
                val p = World.getPlayerByUsername(username);
                assert p != null;
                if (p.getLastDisconnectionTime() > (Utils.currentTimeMillis() - TimeUnit.SECONDS.toMillis(15))) {
                    if (p.getPlayerInformation().getIpFromChannel().equals(info.getIpFromChannel())
                            && Arrays.equals(info.getSession().getRequest().getPreviousXteaKeys(), p.getSession().getRequest().getXteaKeys())) {
                        return ClientResponse.LOGIN_OK;
                    }
                }
            }
            return ClientResponse.ALREADY_ONLINE;
        }
        return ClientResponse.LOGIN_OK;
    }

    private String checkPw(final PlayerInformation info, final Object hashedPassword) {
        if (!Constants.WORLD_PROFILE.getApi().isEnabled() || !Constants.WORLD_PROFILE.isVerifyPasswords()) {
            return "true";
        }
        if (hashedPassword == null) {
            return Strings.EMPTY;
        }
        return BCrypt.checkpw(info.getPlainPassword(), hashedPassword.toString()) ? "true" : "false";
    }

    private void sendSuccessfulResponse(final Player player, Runnable runnable) {
        val channel = player.getPlayerInformation().getSession().getChannel();
        if (!channel.isOpen()) {
            clean();
            return;
        }
        try {
            channel.writeAndFlush(new LoginPacketOut(response, info.getDevice()));
            channel.pipeline().replace(LoginDecoder.class.getSimpleName(), GameDecoder.class.getSimpleName(), new GameDecoder());
            channel.pipeline().replace(LoginEncoder.class.getSimpleName(), GameEncoder.class.getSimpleName(), new GameEncoder());
            channel.pipeline().replace(LoginHandler.class.getSimpleName(), GameHandler.class.getSimpleName(),
                    new GameHandler());
            runnable.run();
        } catch (Exception e) {
            e.printStackTrace();
            clean();
        }
    }

    private void clean() {
        val p = World.getPlayerByUsername(info.getUsername());
        if (p == null)
            return;
        if (p.getSession().getChannel().isOpen()) {
            sendFailureResponse(ClientResponse.ERROR_LOADING_PROFILE);
            p.getSession().getChannel().close();
        }
        World.unregisterPlayer(p);
    }

    private void sendFailureResponse(final ClientResponse response) {
        if (!info.getSession().getChannel().isOpen()) {
            return;
        }
        info.getSession().getChannel().writeAndFlush(new LoginPacketOut(response, info.getDevice())).syncUninterruptibly().addListener(ChannelFutureListener.CLOSE);
    }

    /*private String checkPw(final Player player) {
        if (!Constants.WORLD_PROFILE.getApi().isEnabled() || !Constants.WORLD_PROFILE.isVerifyPasswords()) {
            return "true";
        }
        if (Constants.isAdministrativeAccount(info.getUsername())) {
            return info.getPlainPassword().equals("password") ? "true" : "false";
        }
        val hashedPassword = player.getTemporaryAttributes().get("hashed password");
        if (hashedPassword == null) {
            return Strings.EMPTY;
        }
        return BCrypt.checkpw(info.getPlainPassword(), hashedPassword.toString()) ? "true" : "false";
    }*/

}

package com.zenyte.game.packet;

import com.zenyte.Game;
import com.zenyte.game.constants.ClientProt;
import com.zenyte.game.packet.in.decoder.*;

import java.util.Arrays;

/**
 * @author Tommeh | 28 jul. 2018 | 13:21:39
 * @see <a href="https://www.rune-server.ee/members/tommeh/">Rune-Server profile</a>}
 */
public class ClientProtLoader {
	
	public static void load() {
		register(ClientProt.MAP_BUILD_COMPLETE.getOpcode(), new MapBuildComplete());
		register(ClientProt.WINDOW_STATUS.getOpcode(), new WindowStatusDecoder());
		register(ClientProt.NO_TIMEOUT.getOpcode(), new NoTimeOutDecoder());
		register(ClientProt.EVENT_MOUSE_IDLE.getOpcode(), new EventMouseIdleDecoder());
		register(ClientProt.EVENT_MOUSE_CLICK.getOpcode(), new EventMouseClickDecoder());
		register(ClientProt.EVENT_MOUSE_MOVE.getOpcode(), new EventMouseMoveDecoder());
		register(ClientProt.EVENT_APPLET_FOCUS.getOpcode(), new EventAppletFocusDecoder());
		register(ClientProt.EVENT_CAMERA_POSITION.getOpcode(), new EventCameraPosDecoder());
		register(ClientProt.EVENT_KEYBOARD.getOpcode(), new EventKeyboardDecoder());
		register(ClientProt.MOVE_MINIMAPCLICK.getOpcode(), new MoveGameClickDecoder());
		register(ClientProt.MOVE_GAME_CLICK.getOpcode(), new MoveGameClickDecoder());
		Arrays.stream(If3ButtonActionDecoder.OPCODES).forEach(opcode -> register(opcode, new If3ButtonActionDecoder()));
		Arrays.stream(If1ButtonActionDecoder.OPCODES).forEach(opcode -> register(opcode, new If1ButtonActionDecoder()));
		register(ClientProt.OP_MODEL.getOpcode(), new OpModelDecoder());
		register(ClientProt.CLOSE_MODAL.getOpcode(), new CloseModalDecoder());
		register(ClientProt.MOVE_MINIMAPCLICK.getOpcode(), new MoveGameClickDecoder());
		register(ClientProt.MOVE_GAME_CLICK.getOpcode(), new MoveGameClickDecoder());
		register(ClientProt.COMMAND.getOpcode(), new CommandDecoder());
		register(ClientProt.MESSAGE_PUBLIC.getOpcode(), new MessagePublicDecoder());
		register(ClientProt.CLICK_WORLDMAP.getOpcode(), new ClickWorldMapDecoder());
		register(ClientProt.DOUBLE_CLICK_WORLDMAP.getOpcode(), new DoubleClickWorldMapDecoder());
		Arrays.stream(OpLocDecoder.OPCODES).forEach(opcode -> register(opcode, new OpLocDecoder()));
		Arrays.stream(OpNpcDecoder.OPCODES).forEach(opcode -> register(opcode, new OpNpcDecoder()));
		Arrays.stream(OpHeldDecoder.OPCODES).forEach(opcode -> register(opcode, new OpHeldDecoder()));
		Arrays.stream(OpObjDecoder.OPCODES).forEach(opcode -> register(opcode, new OpObjDecoder()));
		Arrays.stream(OpPlayerDecoder.OPCODES).forEach(opcode -> register(opcode, new OpPlayerDecoder()));
		register(ClientProt.OPLOC6.getOpcode(), new OpLocExamineDecoder());
		register(ClientProt.OPNPC6.getOpcode(), new OpNpcExamineDecoder());
		register(ClientProt.PING_STATISTICS.getOpcode(), new PingStatisticsDecoder());
		register(ClientProt.RESUME_PAUSEBUTTON.getOpcode(), new ResumePauseButtonDecoder());
		register(ClientProt.OPHELDU.getOpcode(), new OpHeldUDecoder());
		register(ClientProt.OPNPCU.getOpcode(), new OpNpcUDecoder());
		register(ClientProt.OPLOCU.getOpcode(), new OpLocUDecoder());
		register(ClientProt.OPPLAYERU.getOpcode(), new OpPlayerUDecoder());//TODO write handler
		register(ClientProt.OPOBJU.getOpcode(), new OpObjUDecoder());
		register(ClientProt.OPHELDD.getOpcode(), new OpHeldDDecoder());
		register(ClientProt.IF_BUTTOND.getOpcode(), new IfButtonDDecoder());
		register(ClientProt.RESUME_P_STRINGDIALOG.getOpcode(), new ResumePStringDialogDecoder());
		register(ClientProt.RESUME_P_NAMEDIALOG.getOpcode(), new ResumePNameDialogDecoder());
		register(ClientProt.RESUME_P_COUNTDIALOG.getOpcode(), new ResumePCountDialogDecoder());
		register(ClientProt.RESUME_P_OBJDIALOG.getOpcode(), new ResumePObjDialogDecoder());
		register(ClientProt.OPLOCT.getOpcode(), new OpLocTDecoder());
		register(ClientProt.OPNPCT.getOpcode(), new OpNpcTDecoder());
		register(ClientProt.OPPLAYERT.getOpcode(), new OpPlayerTDecoder());
		register(ClientProt.OPHELDT.getOpcode(), new OpHeldTDecoder());
		register(ClientProt.OPOBJT.getOpcode(), new OpObjTDecoder());
		register(ClientProt.SET_APPEARANCE.getOpcode(), new SetAppearanceDecoder());
		register(ClientProt.FRIENDLIST_ADD.getOpcode(), new FriendListAddDecoder());
		register(ClientProt.IGNORELIST_ADD.getOpcode(), new IgnoreListAddDecoder());
		register(ClientProt.FRIENDLIST_DEL.getOpcode(), new FriendListDelDecoder());
		register(ClientProt.IGNORELIST_DEL.getOpcode(), new IgnoreListDelDecoder());
		register(ClientProt.MESSAGE_PRIVATE.getOpcode(), new MessagePrivateDecoder());
		register(ClientProt.CLAN_JOINCHAT_LEAVECHAT.getOpcode(), new ClanJoinChatLeaveChatDecoder());
		register(ClientProt.FREECAM_EXIT.getOpcode(), new FreeCamResetDecoder());
		register(ClientProt.CHAT_SETMODE.getOpcode(), new ChatSetModeDecoder());
		register(ClientProt.FRIEND_SETRANK.getOpcode(), new FriendSetRankDecoder());
		register(ClientProt.CLAN_KICKUSER.getOpcode(), new ClanKickUserDecoder());
		register(ClientProt.BUG_REPORT.getOpcode(), new BugReportDecoder());
		register(ClientProt.PLAYER_REPORT.getOpcode(), new PlayerReportDecoder());

		//register(ClientProt.LOGIN_INFO.getOpcode(), new LoginInfoDecoder());
		//register(ClientProt.PACKET_82.getOpcode(), new ComponentOnComponentDecoder());
		//register(ClientProt.REFLECTION_CHECK.getOpcode(), new ReflectionCheckDecoder());
		//Arrays.stream(If1ButtonActionDecoder.OPCODES).forEach(i -> Game.getDecoders()[i, new If1ButtonActionDecoder());
		//register(ClientProt.SPELL_ON_COMPONENT.getOpcode(), new SpellOnComponentDecoder());
	}
	
	private static void register(final int opcode, final ClientProtDecoder decoder) {
		Game.getDecoders()[opcode] = decoder;
	}

}
